package com.creativemd.playerrevive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.creativemd.playerrevive.api.DamageBledToDeath;
import com.creativemd.playerrevive.api.IRevival;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class Revival implements IRevival {
	
	private static HashMap<String, DamageSource> damageSources = new HashMap<>();
	
	private boolean healty = true;
	
	private float progress;
	private int timeLeft;
	
	public final ArrayList<EntityPlayer> revivingPlayers = new ArrayList<>();
	
	public Revival() {
		this.progress = 0;
		timeLeft = PlayerRevive.playerReviveSurviveTime;
	}

	@Override
	public void tick()
	{
		timeLeft--;
		progress += revivingPlayers.size();
		
		for (int i = 0; i < revivingPlayers.size(); i++) {
			revivingPlayers.get(i).addExhaustion(PlayerRevive.exhaustion);
		}
	}

	@Override
	public List<EntityPlayer> getRevivingPlayers()
	{
		return revivingPlayers;
	}

	@Override
	public boolean isHealty()
	{
		return healty;
	}

	@Override
	public void stopBleeding(EntityPlayer player)
	{
		this.healty = true;
		damageSources.remove(player.getCachedUniqueIdString());
	}

	@Override
	public void startBleeding(EntityPlayer player, DamageSource source)
	{
		this.healty = false;
		this.progress = 0;
		timeLeft = PlayerRevive.playerReviveSurviveTime;
		damageSources.put(player.getCachedUniqueIdString(), source);
	}

	@Override
	public float getProgress()
	{
		return progress;
	}

	@Override
	public boolean isRevived()
	{
		return progress >= PlayerRevive.playerReviveTime;
	}

	@Override
	public boolean isDead()
	{
		return timeLeft <= 0;
	}

	@Override
	public int getTimeLeft()
	{
		return timeLeft;
	}

	@Override
	public void kill(EntityPlayer player)
	{
		timeLeft = 0;
		progress = 0;
		damageSources.remove(player.getCachedUniqueIdString());
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("timeLeft", timeLeft);
		nbt.setFloat("progress", progress);
		nbt.setBoolean("healty", healty);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		timeLeft = nbt.getInteger("timeLeft");
		progress = nbt.getFloat("progress");
		healty = nbt.getBoolean("healty");
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}
	
	@Override
	public DamageSource getSource(EntityPlayer player) {
		return damageSources.getOrDefault(player.getCachedUniqueIdString(), DamageBledToDeath.bledToDeath);
	}
}
