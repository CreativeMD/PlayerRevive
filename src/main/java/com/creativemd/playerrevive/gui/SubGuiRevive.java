package com.creativemd.playerrevive.gui;

import javax.swing.text.html.parser.Entity;

import com.creativemd.creativecore.gui.GuiRenderHelper;
import com.creativemd.creativecore.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiProgressBar;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;
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
		super(100, 60);
		setStyle(emptyStyle);
	}
	
	public GuiProgressBar bar;
	public GuiLabel label;
	
	@Override
	public void createControls() {
		Revival revive = ((SubContainerRevive) container).revive;
		bar = (GuiProgressBar) new GuiProgressBar("progress", 0, 0, 94, 13, PlayerRevive.playerReviveTime, revive.getProgress()).setStyle(defaultStyle);
		controls.add(bar);
		label = new GuiLabel("Time left: " + revive.getTimeLeft(), 0, 20);
		controls.add(label);
		if(!((SubContainerRevive) container).isHelping)
		{
			controls.add(new GuiButton("disconnect", 30, 40) {
			
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
	}
	
	@Override
	public void receiveContainerPacket(NBTTagCompound nbt)
	{
		Revival revive = ((SubContainerRevive) container).revive;
		if(revive != null)
		{
			revive.readFromNBT(nbt);
			bar.pos = revive.getProgress();
			label.caption = "Time left: " + revive.getTimeLeft();
		}
	}
	
	@Override
	public void closeGui()
    {
    	if(((SubContainerRevive) container).isHelping)
    		super.closeGui();
    }

}
