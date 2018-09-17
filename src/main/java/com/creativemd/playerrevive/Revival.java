package com.creativemd.playerrevive;

import java.util.ArrayList;
import java.util.List;

import com.creativemd.playerrevive.api.DamageBledToDeath;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.server.CombatTrackerClone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class Revival implements IRevival {
	
	private boolean healty = true;
	
	private float progress;
	private int timeLeft;
	
	private DamageSource lastSource;
	private CombatTrackerClone trackerClone;
	
	public final ArrayList<EntityPlayer> revivingPlayers = new ArrayList<>();
	
	public Revival() {
		this.progress = 0;
		timeLeft = PlayerRevive.playerReviveSurviveTime;
	}
	
	@Override
	public void tick() {
		timeLeft--;
		progress += revivingPlayers.size();
		
		for (int i = 0; i < revivingPlayers.size(); i++) {
			revivingPlayers.get(i).addExhaustion(PlayerRevive.exhaustion);
		}
	}
	
	@Override
	public List<EntityPlayer> getRevivingPlayers() {
		return revivingPlayers;
	}
	
	@Override
	public boolean isHealty() {
		return healty;
	}
	
	@Override
	public void stopBleeding() {
		this.timeLeft = PlayerRevive.playerReviveSurviveTime;
		this.progress = 0;
		this.healty = true;
		this.lastSource = null;
		this.trackerClone = null;
	}
	
	@Override
	public void startBleeding(EntityPlayer player, DamageSource source) {
		this.healty = false;
		this.progress = 0;
		this.timeLeft = PlayerRevive.playerReviveSurviveTime;
		this.lastSource = source;
		this.trackerClone = new CombatTrackerClone(player.getCombatTracker());
	}
	
	@Override
	public float getProgress() {
		return progress;
	}
	
	@Override
	public boolean isRevived() {
		return progress >= PlayerRevive.playerReviveTime;
	}
	
	@Override
	public boolean isDead() {
		return timeLeft <= 0;
	}
	
	@Override
	public int getTimeLeft() {
		return timeLeft;
	}
	
	@Override
	public void kill() {
		timeLeft = 0;
		progress = 0;
		healty = true;
		lastSource = null;
		trackerClone = null;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("timeLeft", timeLeft);
		nbt.setFloat("progress", progress);
		nbt.setBoolean("healty", healty);
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
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
	public DamageSource getSource() {
		if (lastSource != null)
			return lastSource;
		return DamageBledToDeath.bledToDeath;
	}
	
	@Override
	public CombatTrackerClone getTrackerClone() {
		return trackerClone;
	}
}
