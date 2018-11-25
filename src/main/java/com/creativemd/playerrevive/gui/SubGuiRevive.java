package com.creativemd.playerrevive.gui;

import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiProgressBar;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.client.ReviveEventClient;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;

public class SubGuiRevive extends SubGui {
	
	public SubGuiRevive() {
		super(200, 140);
		setStyle(Style.emptyStyle);
	}
	
	public GuiProgressBar bar;
	public GuiLabel label;
	
	@Override
	public void createControls() {
		IRevival revive = ((SubContainerRevive) container).revive;
		bar = (GuiProgressBar) new GuiProgressBar("progress", 50, 0, 94, 13, PlayerRevive.playerReviveTime, revive.getProgress()).setStyle(defaultStyle);
		controls.add(bar);
		label = new GuiLabel(I18n.translateToLocalFormatted("playerrevive.gui.label.time_left", formatTime(revive.getTimeLeft())), 50, 20);
		controls.add(label);
		if (!((SubContainerRevive) container).isHelping) {
			controls.add(new GuiButton(I18n.translateToLocal("playerrevive.gui.button.give_up"), 80, 80) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					
					openYesNoDialog(I18n.translateToLocal("playerrevive.gui.popup.give_up"));
				}
			});
			
			controls.add(new GuiButton(I18n.translateToLocal("playerrevive.gui.button.disconnect"), 70, 100) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					
					openYesNoDialog(I18n.translateToLocal("playerrevive.gui.popup.disconnect"));
				}
			});
			
			GuiTextfield chat = new GuiTextfield("chat", "", 0, 120, 160, 10);
			chat.maxLength = 256;
			controls.add(chat.setStyle(Style.liteStyle));
			
			controls.add(new GuiButton(I18n.translateToLocal("playerrevive.gui.button.send"), 170, 120, 23, 10) {
				
				@Override
				public void onClicked(int x, int y, int button) {
					GuiTextfield chat = (GuiTextfield) SubGuiRevive.this.get("chat");
					
					if (!chat.text.equals("")) {
						gui.sendChatMessage(chat.text);
						
						chat.setSelectionPos(0);
						chat.selEnd = 0;
						chat.cursorPosition = 0;
						chat.text = "";
					}
				}
			}.setStyle(Style.liteStyle));
			
			controls.add(new GuiAnalogeSlider("volume", 160, 0, 40, 10, PlayerRevive.volumeModifier, 0, 1).setStyle(Style.liteStyle));
		}
	}
	
	public void onDialogClosed(String text, String[] buttons, String clicked) {
		if (clicked.equals("Yes")) {
			if (text.equals(I18n.translateToLocal("playerrevive.gui.popup.give_up"))) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("giveup", true);
				sendPacketToServer(nbt);
			} else {
				Minecraft mc = Minecraft.getMinecraft();
				mc.world.sendQuittingDisconnectingPacket();
				mc.loadWorld((WorldClient) null);
				mc.displayGuiScreen(new GuiMainMenu());
			}
		}
	}
	
	@Override
	public boolean onKeyPressed(char character, int key) {
		if (!((SubContainerRevive) container).isHelping && key == org.lwjgl.input.Keyboard.KEY_RETURN) {
			GuiTextfield chat = (GuiTextfield) SubGuiRevive.this.get("chat");
			
			if (!chat.text.equals("")) {
				gui.sendChatMessage(chat.text);
				
				chat.setSelectionPos(0);
				chat.selEnd = 0;
				chat.cursorPosition = 0;
				chat.text = "";
			}
			return true;
		}
		return super.onKeyPressed(character, key);
	}
	
	public String formatTime(int timeLeft) {
		int lengthOfMinute = 20 * 60;
		int lengthOfHour = lengthOfMinute * 60;
		
		int hours = timeLeft / lengthOfHour;
		timeLeft -= hours * lengthOfHour;
		
		int minutes = timeLeft / lengthOfMinute;
		timeLeft -= minutes * lengthOfMinute;
		
		int seconds = timeLeft / 20;
		
		// return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	@CustomEventSubscribe
	public void controlChanged(GuiControlChangedEvent event) {
		if (event.source.is("volume")) {
			PlayerRevive.volumeModifier = ((GuiAnalogeSlider) event.source).value;
			PlayerRevive.config.load();
			PlayerRevive.config.get("Sound", "volume", 1.0F).set(PlayerRevive.volumeModifier);
			PlayerRevive.config.save();
			if (ReviveEventClient.sound != null)
				ReviveEventClient.sound.volume = PlayerRevive.volumeModifier;
		}
	}
	
	@Override
	public void onTick() {
		double timer = 10000000D;
		
		IRevival revive = ((SubContainerRevive) container).revive;
		if (revive != null && revive.getTimeLeft() < 400) {
			timer = 1000000D;
			
		}
		double value = Math.cos(Math.toRadians(System.nanoTime() / timer)) * 0.5 + 0.5;
		label.color = ColorUtils.VecToInt(new Vec3d(1, value, value));
	}
	
	@Override
	public void receiveContainerPacket(NBTTagCompound nbt) {
		IRevival revive = ((SubContainerRevive) container).revive;
		if (revive != null) {
			revive.deserializeNBT(nbt);
			bar.pos = revive.getProgress();
			
			label.caption = I18n.translateToLocalFormatted("playerrevive.gui.label.time_left", formatTime(revive.getTimeLeft()));
		}
	}
	
	@Override
	public void closeGui() {
		if (((SubContainerRevive) container).isHelping)
			super.closeGui();
	}
	
	@Override
	public boolean hasGrayBackground() {
		return false;
	}
	
}
