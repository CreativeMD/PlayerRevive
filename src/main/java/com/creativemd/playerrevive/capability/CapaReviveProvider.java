package com.creativemd.playerrevive.capability;

import com.creativemd.playerrevive.Revival;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapaReviveProvider implements ICapabilityProvider {
	
	public static final CapaReviveProvider instance = new CapaReviveProvider();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return true;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null; 
	}

}
