package com.creativemd.playerrevive.gui;

import com.creativemd.creativecore.common.utils.ColorUtils;
import com.creativemd.creativecore.gui.client.style.Style;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.gui.controls.gui.GuiProgressBar;
import com.creativemd.creativecore.gui.controls.gui.GuiTextfield;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;

import net.java.games.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SubGuiRevive extends SubGui {
	
	public SubGuiRevive() {
		super(200, 120);
		setStyle(Style.emptyStyle);
	}
	
	public GuiProgressBar bar;
	public GuiLabel label;
	
	@Override
	public void createControls() {
		Revival revive = ((SubContainerRevive) container).revive;
		bar = (GuiProgressBar) new GuiProgressBar("progress", 50, 0, 94, 13, PlayerRevive.playerReviveTime, revive.getProgress()).setStyle(defaultStyle);
		controls.add(bar);
		label = new GuiLabel("Time left " + formatTime(revive.getTimeLeft()), 50, 20);
		controls.add(label);
		if(!((SubContainerRevive) container).isHelping)
		{
			controls.add(new GuiButton("give up", 80, 60) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					NBTTagCompound nbt = new NBTTagCompound();
		    		nbt.setBoolean("giveup", true);
		    		sendPacketToServer(nbt);
				}
			});
			
			controls.add(new GuiButton("disconnect", 70, 80) {
			
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
			
			controls.add(new GuiTextfield("chat", "", 0, 100, 160, 10).setStyle(Style.liteStyle));
			
			controls.add(new GuiButton("send", 170, 100, 23, 10) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					GuiTextfield chat = (GuiTextfield) SubGuiRevive.this.get("chat");
					
					if(!chat.text.equals(""))
					{
						gui.sendChatMessage(chat.text);
						
						chat.cursorPosition = 0;
						chat.selEnd = 0;
						chat.text = "";
					}
				}
			}.setStyle(Style.liteStyle));
		}
	}
	
	@Override
	public boolean onKeyPressed(char character, int key) {
		if(key == org.lwjgl.input.Keyboard.KEY_RETURN)
		{
			GuiTextfield chat = (GuiTextfield) SubGuiRevive.this.get("chat");
			
			if(!chat.text.equals(""))
			{
				gui.sendChatMessage(chat.text);
				
				chat.cursorPosition = 0;
				chat.selEnd = 0;
				chat.text = "";
			}
			return true;
		}
		return super.onKeyPressed(character, key);
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
	public void onTick() {
		double timer = 10000000D;
		
		Revival revive = ((SubContainerRevive) container).revive;
		if(revive != null && revive.getTimeLeft() < 400)
		{
			timer = 1000000D;
			
		}
		double value = Math.cos(Math.toRadians(System.nanoTime()/timer))*0.5+0.5;
		label.color = ColorUtils.VecToInt(new Vec3d(1, value, value));
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
	
	@Override
	public boolean hasGrayBackground()
	{
		return false;
	}
	
}
