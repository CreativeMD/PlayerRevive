package team.creative.playerrevive.server;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import team.creative.creativecore.common.config.premade.MobEffectConfig;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.PlayerReviveConfig.DamageTypeConfig;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.api.PlayerExtender;
import team.creative.playerrevive.packet.HelperPacket;

public class ReviveEventServer {
    
    public static boolean isReviveActive(Entity player) {
        if (player instanceof Player p && p.isCreative() && !PlayerRevive.CONFIG.bleeding.triggerForCreative)
            return false;
        return PlayerRevive.CONFIG.bleedInSingleplayer || player.getServer().isPublished();
    }
    
    @SubscribeEvent
    public void executeCommand(CommandEvent event) {
        var source = event.getParseResults().getContext().getSource();
        if (PlayerRevive.CONFIG.bleeding.disableServerCommands && source.isPlayer() && PlayerReviveServer.getBleeding(source.getPlayer()).isBleeding()) {
            source.getPlayer().sendSystemMessage(Component.translatable("playerrevive.chat.no_commands"));
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void playerTick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide && isReviveActive(event.getEntity())) {
            Player player = event.getEntity();
            if (!player.isAlive())
                return;
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            
            if (revive.isBleeding()) {
                revive.tick(player);
                
                if (revive.downedTime() % 5 == 0)
                    PlayerReviveServer.sendUpdatePacket(player);
                
                if (PlayerRevive.CONFIG.bleeding.affectHunger)
                    player.getFoodData().setFoodLevel(PlayerRevive.CONFIG.bleeding.remainingHunger);
                
                for (MobEffectConfig effect : PlayerRevive.CONFIG.bleeding.bleedingEffects)
                    player.addEffect(effect.create());
                
                if (PlayerRevive.CONFIG.bleeding.shouldGlow)
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10));
                
                if (revive.revived())
                    PlayerReviveServer.revive(player);
                else if (revive.bledOut())
                    PlayerReviveServer.kill(player);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLeave(PlayerLoggedOutEvent event) {
        IBleeding revive = PlayerReviveServer.getBleeding(event.getEntity());
        if (revive.isBleeding())
            PlayerReviveServer.kill(event.getEntity());
        if (!event.getEntity().level().isClientSide)
            PlayerReviveServer.removePlayerAsHelper(event.getEntity());
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Player && !event.getEntity().level().isClientSide) {
            Player target = (Player) event.getTarget();
            Player helper = event.getEntity();
            IBleeding revive = PlayerReviveServer.getBleeding(target);
            if (revive.isBleeding()) {
                event.setCanceled(true);
                if (PlayerRevive.CONFIG.revive.needReviveItem) {
                    if (PlayerRevive.CONFIG.revive.consumeReviveItem && !revive.isItemConsumed()) {
                        if (PlayerRevive.CONFIG.revive.reviveItem.is(helper.getMainHandItem())) {
                            if (!helper.isCreative()) {
                                helper.getMainHandItem().shrink(1);
                                helper.getInventory().setChanged();
                            }
                            revive.setItemConsumed();
                        } else {
                            if (!helper.level().isClientSide)
                                helper.sendSystemMessage(Component.translatable("playerrevive.revive.item", PlayerRevive.CONFIG.revive.reviveItem.description()));
                            return;
                        }
                    } else if (!PlayerRevive.CONFIG.revive.reviveItem.is(helper.getMainHandItem()))
                        return;
                }
                
                PlayerReviveServer.removePlayerAsHelper(helper);
                revive.revivingPlayers().add(helper);
                PlayerRevive.NETWORK.sendToClient(new HelperPacket(target.getUUID(), true), (ServerPlayer) helper);
            }
        }
    }
    
    private boolean doesByPass(Player player, DamageSource source) {
        if (source.typeHolder().is(PlayerRevive.BLED_TO_DEATH))
            return true;
        if (PlayerRevive.CONFIG.bypassDamageSources.contains(source.getMsgId()))
            return true;
        if (PlayerRevive.CONFIG.bypassDamageSources.contains(source.typeHolder().getRegisteredName()))
            return true;
        
        return false;
    }
    
    private boolean doesByPassDamageAmount(Player player, DamageSource source) {
        if (!PlayerRevive.CONFIG.enableBypassDamage)
            return false;
        var amount = ((PlayerExtender) player).getOverkill();
        if (PlayerRevive.CONFIG.bypassDamage <= amount)
            return true;
        for (DamageTypeConfig d : PlayerRevive.CONFIG.bypassSourceByDamage) {
            if (d.damageAmount > amount)
                continue;
            if (d.damageType.equals(source.getMsgId()) || d.damageType.equals(source.typeHolder().getRegisteredName()))
                return true;
        }
        return false;
    }
    
    @SubscribeEvent
    public void playerDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            if (revive.isBleeding()) {
                if (doesByPass(player, event.getSource()))
                    return;
                
                if (revive.bledOut())
                    event.setCanceled(true);
                
                if (revive.downedTime() <= PlayerRevive.CONFIG.bleeding.initialDamageCooldown)
                    event.setCanceled(true);
                
                if (event.getSource().getEntity() instanceof Player) {
                    if (PlayerRevive.CONFIG.bleeding.disablePlayerDamage)
                        event.setCanceled(true);
                } else if (event.getSource().getEntity() instanceof LivingEntity) {
                    if (PlayerRevive.CONFIG.bleeding.disableMobDamage)
                        event.setCanceled(true);
                } else if (PlayerRevive.CONFIG.bleeding.disableOtherDamage)
                    event.setCanceled(true);
                
            } else if (PlayerRevive.CONFIG.revive.abortOnDamage)
                PlayerReviveServer.removePlayerAsHelper(player);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerOverkillTracker(LivingDamageEvent.Pre event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof PlayerExtender player && isReviveActive(event.getEntity()))
            player.setOverkill(Math.max(0, event.getContainer().getNewDamage() - event.getEntity().getHealth()));
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDied(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player player && isReviveActive(event.getEntity())) {
            if (!doesByPass(player, event.getSource()) && !doesByPassDamageAmount(player, event.getSource())) {
                IBleeding revive = PlayerReviveServer.getBleeding(player);
                
                if (revive.bledOut() || revive.isBleeding()) {
                    if (revive.isBleeding())
                        PlayerRevive.CONFIG.sounds.death.play(player, SoundSource.PLAYERS);
                    for (Player helper : revive.revivingPlayers())
                        PlayerRevive.NETWORK.sendToClient(new HelperPacket(null, false), (ServerPlayer) helper);
                    revive.revivingPlayers().clear();
                    return;
                }
                
                PlayerReviveServer.removePlayerAsHelper(player);
                PlayerRevive.NETWORK.sendToClient(new HelperPacket(null, false), (ServerPlayer) player);
                
                PlayerReviveServer.startBleeding(player, event.getSource());
                
                if (player.isPassenger())
                    player.stopRiding();
                
                event.setCanceled(true);
                
                if (PlayerRevive.CONFIG.bleeding.affectHunger)
                    player.getFoodData().setFoodLevel(PlayerRevive.CONFIG.bleeding.remainingHunger);
                
                player.setHealth(PlayerRevive.CONFIG.bleeding.bleedingHealth);
                
                if (PlayerRevive.CONFIG.bleeding.bleedingMessage)
                    if (PlayerRevive.CONFIG.bleeding.bleedingMessageTrackingOnly) {
                        if (player.level().getChunkSource() instanceof ServerChunkCache chunkCache)
                            chunkCache.broadcastAndSend(player, new ClientboundSystemChatPacket(Component.translatable("playerrevive.chat.bleeding", player
                                    .getDisplayName()), false));
                    } else
                        player.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("playerrevive.chat.bleeding", player.getDisplayName()), false);
            }
        }
    }
}
