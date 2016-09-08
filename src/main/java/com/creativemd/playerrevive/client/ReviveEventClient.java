package com.creativemd.playerrevive.client;

import java.awt.Container;

import com.creativemd.creativecore.gui.mc.ContainerSub;
import com.creativemd.creativecore.gui.mc.GuiContainerSub;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.gui.SubContainerRevive;
import com.creativemd.playerrevive.gui.SubGuiRevive;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReviveEventClient {
	
	public static Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void tick(RenderTickEvent event)
	{
		EntityPlayer player = mc.thePlayer;
		if(event.phase == Phase.END && player != null)
		{
			Revival revive = PlayerReviveClient.playerRevive;
			SubGuiRevive gui = null;
			if(player.openContainer instanceof ContainerSub && ((ContainerSub) player.openContainer).gui.getTopLayer() instanceof SubGuiRevive)
				gui = (SubGuiRevive) ((ContainerSub) player.openContainer).gui.getTopLayer();
			//else
				//System.out.println(player.openContainer);
			player.setHealth(0.5F);
			if(revive == null)
			{
				if(gui != null)
					gui.closeGui();
			}else{
				if(gui == null)
					GuiHandler.openGui("plrevive", new NBTTagCompound());
			}
		}
	}
	
}
