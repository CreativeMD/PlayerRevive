package com.creativemd.playerrevive.gui;

import javax.swing.text.html.parser.Entity;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiProgressBar;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.client.PlayerReviveClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubGuiRevive extends SubGui {
	
	public static Style emptyStyle = new Style("empty", DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay, DisplayStyle.emptyDisplay);
	
	public SubGuiRevive() {
		super(100, 40);
		setStyle(emptyStyle);
	}
	
	public GuiProgressBar bar;
	
	@Override
	public void createControls() {
		bar = (GuiProgressBar) new GuiProgressBar("progress", 0, 0, 94, 13, PlayerRevive.playerReviveTime, PlayerReviveClient.playerRevive.getProgress()).setStyle(defaultStyle);
		controls.add(bar);
		controls.add(new GuiButton("disconnect", 30, 20) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.theWorld != null)
	            {
	                mc.theWorld.sendQuittingDisconnectingPacket();
	            }
				
	            mc.loadWorld((WorldClient)null);
	            mc.displayGuiScreen(new GuiMainMenu());
			}
		});
	}
	
	@Override
	public void receiveContainerPacket(NBTTagCompound nbt)
	{
		if(PlayerReviveClient.playerRevive != null)
		{
			PlayerReviveClient.playerRevive.readFromNBT(nbt);
			bar.pos = PlayerReviveClient.playerRevive.getProgress();
		}
	}
	
	@Override
	public void closeGui()
    {
    	
    }

}
