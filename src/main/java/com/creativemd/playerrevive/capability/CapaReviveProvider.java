package com.creativemd.playerrevive.capability;

import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.client.ReviveEventClient;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

public class CapaReviveProvider implements ICapabilitySerializable<NBTTagCompound> {
	
	//public static final CapaReviveProvider instance = new CapaReviveProvider();
	
	private Revival revive = new Revival();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == Revival.reviveCapa;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == Revival.reviveCapa)
			return (T) revive;
		return null; 
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return revive.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		revive.deserializeNBT(nbt);
	}
	
	

}
