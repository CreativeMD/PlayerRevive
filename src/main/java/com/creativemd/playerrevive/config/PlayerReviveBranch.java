package com.creativemd.playerrevive.config;

import com.creativemd.igcm.api.ConfigBranch;
import com.creativemd.igcm.api.segments.BooleanSegment;
import com.creativemd.igcm.api.segments.IntegerSegment;
import com.creativemd.playerrevive.PlayerRevive;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerReviveBranch extends ConfigBranch {

	public PlayerReviveBranch() {
		super("Player Revive", new ItemStack(Items.TOTEM));
	}

	@Override
	public void createChildren() {
		registerElement("playerReviveTime", new IntegerSegment("Revive Time", 100, 1, Integer.MAX_VALUE));
		registerElement("playerReviveSurviveTime", new IntegerSegment("Survive Time", 1200, 1, Integer.MAX_VALUE));
		
		registerElement("playerHealthAfter", new IntegerSegment("Health After", 2, 1, 20));
		registerElement("playerFoodAfter", new IntegerSegment("Food After", 6, 1, 20));
		
		registerElement("banPlayerAfterDeath", new BooleanSegment("Ban After Death", false));
	}

	@Override
	public boolean requiresSynchronization() {
		return false;
	}

	@Override
	public void onRecieveFrom(Side side) {
		PlayerRevive.playerReviveTime = (Integer) getValue("playerReviveTime");
		PlayerRevive.playerReviveSurviveTime = (Integer) getValue("playerReviveSurviveTime");
		
		PlayerRevive.playerHealthAfter = (Integer) getValue("playerHealthAfter");
		PlayerRevive.playerFoodAfter = (Integer) getValue("playerFoodAfter");
		
		PlayerRevive.banPlayerAfterDeath = (Boolean) getValue("banPlayerAfterDeath");
	}

	@Override
	public void loadExtra(NBTTagCompound nbt) {
		
	}

	@Override
	public void saveExtra(NBTTagCompound nbt) {
		
	}
	
	
	
}
