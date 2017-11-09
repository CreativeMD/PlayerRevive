package com.creativemd.playerrevive.config;

import com.creativemd.igcm.api.ConfigBranch;
import com.creativemd.igcm.api.segments.BooleanSegment;
import com.creativemd.igcm.api.segments.IntegerSegment;
import com.creativemd.playerrevive.PlayerRevive;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerReviveBranch extends ConfigBranch {

	public PlayerReviveBranch() {
		super(I18n.format("playerrevive.config.branch_name"), new ItemStack(Items.TOTEM_OF_UNDYING));
	}

	@Override
	public void createChildren() {
		registerElement("playerReviveTime", new IntegerSegment(I18n.format("playerrevive.config.player_revive_time"), 100, 1, Integer.MAX_VALUE));
		registerElement("playerReviveSurviveTime", new IntegerSegment(I18n.format("playerrevive.config.player_revive_survive_time"), 1200, 1, Integer.MAX_VALUE));
		
		registerElement("playerHealthAfter", new IntegerSegment(I18n.format("playerrevive.config.player_health_after"), 2, 1, 20));
		registerElement("playerFoodAfter", new IntegerSegment(I18n.format("playerrevive.config.player_food_after"), 6, 1, 20));
		
		registerElement("banPlayerAfterDeath", new BooleanSegment(I18n.format("playerrevive.config.ban_player_after_death"), false));
	}

	@Override
	public boolean requiresSynchronization() {
		return true;
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
