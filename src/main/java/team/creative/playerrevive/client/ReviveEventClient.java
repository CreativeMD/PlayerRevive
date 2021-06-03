package team.creative.playerrevive.client;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.gui.SubContainerRevive;
import com.creativemd.playerrevive.gui.SubGuiRevive;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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
                
                render time left
                
            }
            
            helping render
            label.setCaption(I18n.translateToLocalFormatted("playerrevive.gui.label.time_left", formatTime(revive.getTimeLeft())));
            
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
