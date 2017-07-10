package com.creativemd.playerrevive.client;

import java.awt.Container;
import java.lang.reflect.Field;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.mc.ContainerSub;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.gui.SubContainerRevive;
import com.creativemd.playerrevive.gui.SubGuiRevive;
import com.creativemd.playerrevive.server.PlayerReviveServer;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReviveEventClient {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static Field sleeping = ReflectionHelper.findField(EntityPlayer.class, "sleeping", "field_71083_bS");
	
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Pre event)
	{
		EntityPlayer player = event.getEntityPlayer();
		Revival revive = PlayerReviveServer.getRevival(player);
		if(!revive.isHealty())
		{
			double percentage = 1D-(revive.getTimeLeft()/(double)PlayerRevive.playerReviveSurviveTime);
			int amount = (int) Math.floor(percentage*1.3D);
			for(int i = 0; i < amount; i++)
			{
				player.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.posX-1, player.posY, player.posZ, 0, 0, 0);
			}
			
			player.renderOffsetX = 0;
			player.renderOffsetY = 0;
			player.renderOffsetZ = 0;
			
			try {
				sleeping.set(event.getEntityPlayer(), true);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event)
	{
		Revival revive = PlayerReviveServer.getRevival(event.getEntityPlayer());
		if(!revive.isHealty())
		{
			try {
				sleeping.set(event.getEntityPlayer(), false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	public void cameraSetup(CameraSetup event)
	{
		Revival revive = PlayerReviveServer.getRevival(mc.player);
		if(!revive.isHealty())
		{
			GlStateManager.translate(0, 0, -1.5);
			event.setYaw(0);
			event.setPitch(-90);
		}
	}
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
			return ;
		Revival revive = PlayerReviveServer.getRevival(event.player);
		if(!revive.isHealty() && event.player != mc.player)
		{
			AxisAlignedBB axisalignedbb = event.player.getEntityBoundingBox();
			double width = 0.6;
            double height = 1.8;
            
			event.player.setEntityBoundingBox(new AxisAlignedBB(event.player.posX - height, event.player.posY - width/2D, event.player.posZ - width/2D, event.player.posX, event.player.posY + width/2D, event.player.posZ + width/2D));
		}
	}
	
	public boolean lastShader = false;
	public boolean lastHighTension = false;
	
	public static TensionSound sound;

	@SubscribeEvent
	public void tick(RenderTickEvent event)
	{
		EntityPlayer player = mc.player;
		if(event.phase == Phase.END && player != null)
		{
			Revival revive = PlayerReviveServer.getRevival(player);
			
			SubGuiRevive gui = null;
			if(player.openContainer instanceof ContainerSub && ((ContainerSub) player.openContainer).gui.getTopLayer() instanceof SubGuiRevive)
				gui = (SubGuiRevive) ((ContainerSub) player.openContainer).gui.getTopLayer();
			
			if(revive.isHealty())
			{
				lastHighTension = false;
				if(lastShader)
				{
					mc.entityRenderer.loadEntityShader(mc.getRenderViewEntity());
					lastShader = false;
				}
				
				if(sound != null)
				{
					mc.getSoundHandler().stopSound(sound);
					sound = null;
				}
				if(gui != null && !((SubContainerRevive) gui.container).isHelping)
				{
					((SubContainerRevive) gui.container).isHelping = true;
					gui.closeGui();
				}
			}else{
				if(revive.getTimeLeft() < 400)
				{
					if(!lastHighTension)
					{
						mc.getSoundHandler().stopSound(sound);
						sound = new TensionSound(new ResourceLocation(PlayerRevive.modid, "hightension"), 1.0F, 1.0F, false);
						mc.getSoundHandler().playSound(sound);
						lastHighTension = true;
					}
				}else{
					if(!lastShader)
					{
						if(sound != null)
							mc.getSoundHandler().stopSound(sound);
						sound = new TensionSound(new ResourceLocation(PlayerRevive.modid, "tension"), 1.0F, 1.0F, true);
						mc.getSoundHandler().playSound(sound);
					}
				}
				
				if(!lastShader)
				{
					mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
					lastShader = true;
				}
				if(gui == null)
					GuiHandler.openGui("plrevive", new NBTTagCompound());
				//player.setHealth(0.5F);
			}
		}
	}
	
}
