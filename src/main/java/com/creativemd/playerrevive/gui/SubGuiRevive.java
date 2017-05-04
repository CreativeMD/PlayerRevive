package com.creativemd.playerrevive.gui;

import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiProgressBar;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;

public class SubGuiRevive extends SubGui {
	
	public SubGuiRevive() {
		super(100, 80);
		setStyle(Style.emptyStyle);
	}
	
	public GuiProgressBar bar;
	public GuiLabel label;
	
	@Override
	public void createControls() {
		Revival revive = ((SubContainerRevive) container).revive;
		bar = (GuiProgressBar) new GuiProgressBar("progress", 0, 0, 94, 13, PlayerRevive.playerReviveTime, revive.getProgress()).setStyle(defaultStyle);
		controls.add(bar);
		label = new GuiLabel("Time left " + formatTime(revive.getTimeLeft()), 0, 20);
		controls.add(label);
		if(!((SubContainerRevive) container).isHelping)
		{
			controls.add(new GuiButton("give up", 30, 40) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					NBTTagCompound nbt = new NBTTagCompound();
		    		nbt.setBoolean("giveup", true);
		    		sendPacketToServer(nbt);
				}
			});
			
			controls.add(new GuiButton("disconnect", 20, 60) {
			
				@Override
				public void onClicked(int x, int y, int button) {
					Minecraft mc = Minecraft.getMinecraft();
					if (mc.world != null)
		            {
		                mc.world.sendQuittingDisconnectingPacket();
		            }
					
		            mc.loadWorld((WorldClient)null);
		            mc.displayGuiScreen(new GuiMainMenu());
				}
			});
		}
	}
	
	public String formatTime(int timeLeft)
	{
		int lengthOfMinute = 20*60;
		int lengthOfHour = lengthOfMinute*60;
		
		int hours = timeLeft/lengthOfHour;
		timeLeft -= hours*lengthOfHour;
		
		int minutes = timeLeft/lengthOfMinute;
		timeLeft -= minutes*lengthOfMinute;
		
		int seconds = timeLeft/20;
		
		return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
	}
	
	@Override
	public void receiveContainerPacket(NBTTagCompound nbt)
	{
		Revival revive = ((SubContainerRevive) container).revive;
		if(revive != null)
		{
			revive.readFromNBT(nbt);
			bar.pos = revive.getProgress();
			
			label.caption = "Time left " + formatTime(revive.getTimeLeft());
		}
	}
	
	@Override
	public void closeGui()
    {
    	if(((SubContainerRevive) container).isHelping)
    		super.closeGui();
    }

}
