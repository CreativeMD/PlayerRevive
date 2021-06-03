package team.creative.playerrevive.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

@OnlyIn(value = Dist.CLIENT)
public class ReviveEventClient {
    
    public static Minecraft mc = Minecraft.getInstance();
    
    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.phase == Phase.START)
            return;
        IBleeding revive = PlayerReviveServer.getBleeding(event.player);
        if (revive.isBleeding() && event.player != mc.player) {
            event.player.setPose(Pose.SWIMMING);
            //event.player.setForcedPose(Pose.SWIMMING);
        }
    }
    
    public boolean lastShader = false;
    public boolean lastHighTension = false;
    
    public static TensionSound sound;
    
    public static UUID helpTarget;
    public static boolean helpActive = false;
    
    @SubscribeEvent
    public void tick(RenderTickEvent event) {
        PlayerEntity player = mc.player;
        if (event.phase == Phase.END && player != null) {
            IBleeding revive = PlayerReviveServer.getBleeding(player);
            
            if (!revive.isBleeding()) {
                lastHighTension = false;
                if (lastShader) {
                    mc.gameRenderer.checkEntityPostEffect(mc.getCameraEntity());
                    lastShader = false;
                }
                
                if (sound != null) {
                    mc.getSoundManager().stop(sound);
                    sound = null;
                }
            } else if (helpActive) {
                List<ITextComponent> list = new ArrayList<>();
                int space = 15;
                
                PlayerEntity other = player.level.getPlayerByUUID(helpTarget);
                if (other != null) {
                    IBleeding bleeding = PlayerReviveServer.getBleeding(other);
                    list.add(new TranslationTextComponent("playerrevive.gui.label.time_left", formatTime(bleeding.timeLeft())));
                    list.add(new StringTextComponent("" + bleeding.getProgress() + "/" + PlayerRevive.CONFIG.requiredReviveProgress));
                    int width = 0;
                    for (int i = 0; i < list.size(); i++) {
                        String text = list.get(i).getString();
                        width = Math.max(width, mc.font.width(text) + 10);
                    }
                    
                    RenderSystem.disableBlend();
                    RenderSystem.enableAlphaTest();
                    RenderSystem.enableTexture();
                    for (int i = 0; i < list.size(); i++) {
                        String text = list.get(i).getString();
                        mc.font.drawShadow(new MatrixStack(), text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow()
                                .getGuiScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)), 16579836);
                    }
                }
            } else {
                if (revive.timeLeft() < 400) {
                    if (!lastHighTension) {
                        if (!PlayerRevive.CONFIG.disableMusic) {
                            mc.getSoundManager().stop(sound);
                            sound = new TensionSound(new ResourceLocation(PlayerRevive.MODID, "hightension"), PlayerRevive.CONFIG.musicVolume, 1.0F, false);
                            mc.getSoundManager().play(sound);
                        }
                        lastHighTension = true;
                        
                    }
                } else {
                    if (!lastShader) {
                        if (sound != null)
                            mc.getSoundManager().stop(sound);
                        if (!PlayerRevive.CONFIG.disableMusic) {
                            sound = new TensionSound(new ResourceLocation(PlayerRevive.MODID, "tension"), PlayerRevive.CONFIG.musicVolume, 1.0F, true);
                            mc.getSoundManager().play(sound);
                        }
                    }
                }
                
                if (!lastShader) {
                    mc.gameRenderer.loadEffect(new ResourceLocation("shaders/post/blur.json"));
                    lastShader = true;
                }
                
                List<ITextComponent> list = new ArrayList<>();
                int space = 15;
                
                IBleeding bleeding = PlayerReviveServer.getBleeding(player);
                list.add(new TranslationTextComponent("playerrevive.gui.label.time_left", formatTime(bleeding.timeLeft())));
                list.add(new StringTextComponent("" + bleeding.getProgress() + "/" + PlayerRevive.CONFIG.requiredReviveProgress));
                int width = 0;
                for (int i = 0; i < list.size(); i++) {
                    String text = list.get(i).getString();
                    width = Math.max(width, mc.font.width(text) + 10);
                }
                
                RenderSystem.disableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                for (int i = 0; i < list.size(); i++) {
                    String text = list.get(i).getString();
                    mc.font.drawShadow(new MatrixStack(), text, mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(text) / 2, mc.getWindow()
                            .getGuiScaledHeight() / 2 + ((list.size() / 2) * space - space * (i + 1)), 16579836);
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
