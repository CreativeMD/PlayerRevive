package com.creativemd.playerrevive;

import com.creativemd.playerrevive.api.capability.CapaRevive;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapaReviveProvider implements ICapabilitySerializable<NBTTagCompound> {
	
	//public static final CapaReviveProvider instance = new CapaReviveProvider();
	
	private Revival revive = new Revival();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapaRevive.reviveCapa;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapaRevive.reviveCapa)
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
