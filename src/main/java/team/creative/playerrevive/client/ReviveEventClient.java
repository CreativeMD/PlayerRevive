package team.creative.playerrevive.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.mixin.LocalPlayerAccessor;
import team.creative.playerrevive.mixin.MinecraftAccessor;
import team.creative.playerrevive.packet.GiveUpPacket;
import team.creative.playerrevive.server.PlayerReviveServer;

@OnlyIn(value = Dist.CLIENT)
public class ReviveEventClient {
    
    public static Minecraft mc = Minecraft.getInstance();
    
    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.phase == Phase.START)
            return;
        IBleeding revive = PlayerReviveServer.getBleeding(event.player);
        if (revive.isBleeding() && event.player != mc.player)
            event.player.setPose(Pose.SWIMMING);
    }
    
    public boolean lastShader = false;
    public boolean lastHighTension = false;
    
    public static TensionSound sound;
    
    public static UUID helpTarget;
    public static boolean helpActive = false;
    
    private boolean addedEffect = false;
    private int giveUpTimer = 0;
    
    @SubscribeEvent
    public void click(ClickInputEvent event) {
        Player player = mc.player;
        if (player != null) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            if (revive.isBleeding())
                event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.END) {
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
    }
    
    @SubscribeEvent
    public void tick(RenderTickEvent event) {
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
                    Player other = player.level.getPlayerByUUID(helpTarget);
                    if (other != null) {
                        List<Component> list = new ArrayList<>();
                        IBleeding bleeding = PlayerReviveServer.getBleeding(other);
                        list.add(new TranslatableComponent("playerrevive.gui.label.time_left", formatTime(bleeding.timeLeft())));
                        list.add(new TextComponent("" + bleeding.getProgress() + "/" + PlayerRevive.CONFIG.revive.requiredReviveProgress));
                        render(list);
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
                            sound = new TensionSound(new ResourceLocation(PlayerRevive.MODID, "hightension"), PlayerRevive.CONFIG.countdownMusicVolume, 1.0F, false);
                            mc.getSoundManager().play(sound);
                        }
                        lastHighTension = true;
                        
                    }
                } else {
                    if (!lastShader) {
                        if (sound != null)
                            mc.getSoundManager().stop(sound);
                        if (!PlayerRevive.CONFIG.disableMusic) {
                            sound = new TensionSound(new ResourceLocation(PlayerRevive.MODID, "tension"), PlayerRevive.CONFIG.bleedingMusicVolume, 1.0F, true);
                            mc.getSoundManager().play(sound);
                        }
                    }
                }
                
                if (!lastShader) {
                    if (PlayerRevive.CONFIG.bleeding.hasShaderEffect)
                        mc.gameRenderer.loadEffect(new ResourceLocation("shaders/post/blobs2.json"));
                    lastShader = true;
                }
                
                if (!mc.options.hideGui && mc.screen == null) {
                    List<Component> list = new ArrayList<>();
                    IBleeding bleeding = PlayerReviveServer.getBleeding(player);
                    list.add(new TranslatableComponent("playerrevive.gui.label.time_left", formatTime(bleeding.timeLeft())));
                    list.add(new TextComponent("" + bleeding.getProgress() + "/" + PlayerRevive.CONFIG.revive.requiredReviveProgress));
                    list.add(new TranslatableComponent("playerrevive.gui.hold", mc.options.keyAttack.getKey().getDisplayName(), ((PlayerRevive.CONFIG.bleeding.giveUpSeconds * 20 - giveUpTimer) / 20) + 1));
                    render(list);
                }
            }
            
        }
    }
    
    public static void render(List<Component> list) {
        int space = 15;
        int width = 0;
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            width = Math.max(width, mc.font.width(text) + 10);
        }
        
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i).getString();
            mc.font.drawShadow(new PoseStack(), text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow()
                    .getGuiScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)), 16579836);
        }
        RenderSystem.enableDepthTest();
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
