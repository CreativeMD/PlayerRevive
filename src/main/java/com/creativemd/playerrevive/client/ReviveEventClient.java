package com.creativemd.playerrevive.client;

import java.awt.Container;
import java.lang.reflect.Field;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.mc.ContainerSub;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;
import com.creativemd.creativecore.gui.opener.GuiHandler;
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
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
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
		Revival revive = PlayerReviveServer.getRevival(event.getEntityPlayer());
		if(!revive.isHealty())
		{
			try {
				sleeping.set(event.getEntityPlayer(), true);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			event.getEntityPlayer().rotationYaw = -90;
			event.getEntityPlayer().rotationPitch = 90;
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
	public void tick(RenderTickEvent event)
	{
		EntityPlayer player = mc.player;
		if(event.phase == Phase.END && player != null)
		{
			Revival revive = PlayerReviveServer.getRevival(player);
			
			SubGuiRevive gui = null;
			if(player.openContainer instanceof ContainerSub && ((ContainerSub) player.openContainer).gui.getTopLayer() instanceof SubGuiRevive)
				gui = (SubGuiRevive) ((ContainerSub) player.openContainer).gui.getTopLayer();
			//else
				//System.out.println(player.openContainer);
			
			if(revive.isHealty())
			{
				if(gui != null && !((SubContainerRevive) gui.container).isHelping)
				{
					((SubContainerRevive) gui.container).isHelping = true;
					gui.closeGui();
				}
			}else{
				if(gui == null)
					GuiHandler.openGui("plrevive", new NBTTagCompound());
				//player.setHealth(0.5F);
			}
		}
	}
	
}
