package team.creative.playerrevive.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.mixin.LocalPlayerAccessor;
import team.creative.playerrevive.mixin.MinecraftAccessor;
import team.creative.playerrevive.packet.GiveUpPacket;
import team.creative.playerrevive.server.PlayerReviveServer;

@OnlyIn(value = Dist.CLIENT)
public class ReviveEventClient {
    
    private static final ResourceLocation BLUR_SHADER = ResourceLocation.tryBuild(PlayerRevive.MODID, "shaders/post/blobs2.json");
    public static Minecraft mc = Minecraft.getInstance();
    
    public static TensionSound sound;
    
    public static UUID helpTarget;
    public static boolean helpActive = false;
    
    public static void render(GuiGraphics graphics, List<Component> list) {
        int space = 15;
        int width = 0;
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            width = Math.max(width, mc.font.width(text) + 10);
        }
        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            graphics.drawString(mc.font, text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow().getGuiScaledHeight() / 2 + ((list
                    .size() / 2) * space - space * (i + 1)), 16579836);
        }
        RenderSystem.enableDepthTest();
    }
    
    public boolean lastShader = false;
    public boolean lastHighTension = false;
    
    private boolean addedEffect = false;
    private int giveUpTimer = 0;
    private boolean inPauseScreen = false;
    
    @SubscribeEvent
    public void playerTick(PlayerTickEvent.Post event) {
        IBleeding revive = PlayerReviveServer.getBleeding(event.getEntity());
        if (revive.isBleeding())
            event.getEntity().setPose(Pose.SWIMMING);
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void click(InteractionKeyMappingTriggered event) {
        Player player = mc.player;
        if (player != null) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            if (revive.isBleeding())
                event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void screenOpen(ScreenEvent.Opening event) {
        Player player = mc.player;
        if (player != null) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            if (!revive.isBleeding())
                return;
            if (event.getCurrentScreen() == null)
                inPauseScreen = false;
            if (PlayerRevive.CONFIG.bleeding.disableInventoryAccess && event.getNewScreen() instanceof InventoryScreen)
                event.setCanceled(true);
            else if (PlayerRevive.CONFIG.bleeding.disableChatAccess && event.getNewScreen() instanceof ChatScreen)
                event.setCanceled(true);
            else if (PlayerRevive.CONFIG.bleeding.disableAllGUIAccess && !(event.getNewScreen() instanceof DeathScreen)) {
                if (event.getNewScreen() instanceof PauseScreen)
                    inPauseScreen = true;
                if (!inPauseScreen)
                    event.setCanceled(true);
            } else
                inPauseScreen = true;
        }
    }
    
    @SubscribeEvent
    public void clientTick(ClientTickEvent.Post event) {
        
        Player player = mc.player;
        if (player != null) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            
            if (revive.isBleeding())
                if (mc.options.keyAttack.isDown())
                    if (giveUpTimer > PlayerRevive.CONFIG.bleeding.giveUpSeconds * 20) {
                        PlayerRevive.NETWORK.sendToServer(new GiveUpPacket());
                        giveUpTimer = 0;
                    } else
                        giveUpTimer++;
                else
                    giveUpTimer = 0;
            else
                giveUpTimer = 0;
        }
    }
    
    @SubscribeEvent
    public void frameEvent(RenderFrameEvent.Pre event) {
        Player player = mc.player;
        if (player != null && PlayerRevive.CONFIG.revive.forceLookAt) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            if (!revive.isBleeding() && helpActive) {
                Player other = player.level().getPlayerByUUID(helpTarget);
                if (other != null) {
                    float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
                    Vec3 vec3 = player.getEyePosition(partial);
                    Vec3 center = other.getPosition(partial);
                    double d0 = center.x - vec3.x;
                    double d1 = center.y - vec3.y;
                    double d2 = center.z - vec3.z;
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    player.setXRot(Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 180.0F / (float) Math.PI))));
                    player.setYRot(Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * 180.0F / (float) Math.PI) - 90.0F));
                    player.setYHeadRot(player.getYRot());
                    player.xRotO = player.getXRot();
                    player.yRotO = player.getYRot();
                    player.yHeadRotO = player.yHeadRot;
                    player.yBodyRot = player.yHeadRot;
                    player.yBodyRotO = player.yBodyRot;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void tick(RenderGuiLayerEvent.Post event) {
        Player player = mc.player;
        if (player != null) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            
            if (!revive.isBleeding()) {
                lastHighTension = false;
                if (lastShader) {
                    mc.gameRenderer.checkEntityPostEffect(mc.getCameraEntity());
                    lastShader = false;
                }
                
                if (addedEffect) {
                    player.removeEffect(MobEffects.JUMP);
                    ((LocalPlayerAccessor) player).setHandsBusy(false);
                    addedEffect = false;
                }
                
                if (sound != null) {
                    mc.getSoundManager().stop(sound);
                    sound = null;
                }
                
                if (helpActive && !mc.options.hideGui && mc.screen == null) {
                    Player other = player.level().getPlayerByUUID(helpTarget);
                    if (other != null) {
                        List<Component> list = new ArrayList<>();
                        IBleeding bleeding = PlayerReviveServer.getBleeding(other);
                        list.add(Component.translatable("playerrevive.gui.label.time_left", formatTime(bleeding.timeLeft())));
                        list.add(Component.literal("" + bleeding.getProgress() + "/" + PlayerRevive.CONFIG.revive.requiredReviveProgress));
                        render(event.getGuiGraphics(), list);
                    }
                }
            } else {
                player.setPose(Pose.SWIMMING);
                ((LocalPlayerAccessor) player).setHandsBusy(true);
                ((MinecraftAccessor) mc).setMissTime(2);
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 0, -10));
                
                player.hurtTime = 0;
                addedEffect = true;
                
                if (revive.timeLeft() < 400) {
                    if (!lastHighTension) {
                        if (!PlayerRevive.CONFIG.disableMusic) {
                            mc.getSoundManager().stop(sound);
                            sound = new TensionSound(ResourceLocation.tryBuild(PlayerRevive.MODID, "hightension"), PlayerRevive.CONFIG.countdownMusicVolume, 1.0F, false);
                            mc.getSoundManager().play(sound);
                        }
                        lastHighTension = true;
                        
                    }
                } else {
                    if (!lastShader) {
                        if (sound != null)
                            mc.getSoundManager().stop(sound);
                        if (!PlayerRevive.CONFIG.disableMusic) {
                            sound = new TensionSound(ResourceLocation.tryBuild(PlayerRevive.MODID, "tension"), PlayerRevive.CONFIG.bleedingMusicVolume, 1.0F, true);
                            mc.getSoundManager().play(sound);
                        }
                    }
                }
                
                if (!lastShader) {
                    if (PlayerRevive.CONFIG.bleeding.hasShaderEffect)
                        mc.gameRenderer.loadEffect(BLUR_SHADER);
                    lastShader = true;
                } else if (PlayerRevive.CONFIG.bleeding.hasShaderEffect && (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().equals(BLUR_SHADER
                        .toString()))) {
                    mc.gameRenderer.loadEffect(BLUR_SHADER);
                }
                
                if (!mc.options.hideGui && mc.screen == null) {
                    List<Component> list = new ArrayList<>();
                    IBleeding bleeding = PlayerReviveServer.getBleeding(player);
                    list.add(Component.translatable("playerrevive.gui.label.time_left", formatTime(bleeding.timeLeft())));
                    list.add(Component.literal("" + bleeding.getProgress() + "/" + PlayerRevive.CONFIG.revive.requiredReviveProgress));
                    list.add(Component.translatable("playerrevive.gui.hold", mc.options.keyAttack.getKey().getDisplayName(),
                        ((PlayerRevive.CONFIG.bleeding.giveUpSeconds * 20 - giveUpTimer) / 20) + 1));
                    render(event.getGuiGraphics(), list);
                }
            }
            
        }
    }
    
    public String formatTime(int timeLeft) {
        int lengthOfMinute = 20 * 60;
        int lengthOfHour = lengthOfMinute * 60;
        
        int hours = timeLeft / lengthOfHour;
        timeLeft -= hours * lengthOfHour;
        
        int minutes = timeLeft / lengthOfMinute;
        timeLeft -= minutes * lengthOfMinute;
        
        int seconds = timeLeft / 20;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
}
