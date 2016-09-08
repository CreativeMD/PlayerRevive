package com.creativemd.playerrevive;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class Revival {
	
	private float progress;
	private int timeLeft;
	
	public ArrayList<EntityPlayer> revivingPlayers = new ArrayList<>();
	
	public Revival() {
		this.progress = 0;
		timeLeft = PlayerRevive.playerReviveSurviveTime;
	}
	
	public void tick()
	{
		timeLeft--;
		progress += revivingPlayers.size();
	}
	
	public float getProgress()
	{
		return progress;
	}
	
	public boolean isRevived()
	{
		return progress >= PlayerRevive.playerReviveTime;
	}
	
	public boolean isDead()
	{
		return timeLeft <= 0;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("timeLeft", timeLeft);
		nbt.setFloat("progress", progress);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		timeLeft = nbt.getInteger("timeLeft");
		progress = nbt.getFloat("progress");
	}
	
}
