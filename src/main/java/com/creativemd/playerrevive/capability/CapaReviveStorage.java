package com.creativemd.playerrevive.capability;

import java.util.concurrent.Callable;

import com.creativemd.playerrevive.Revival;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapaReviveStorage implements IStorage<Revival> {

	@Override
	public NBTBase writeNBT(Capability<Revival> capability, Revival instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		instance.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readNBT(Capability<Revival> capability, Revival instance, EnumFacing side, NBTBase nbt) {
		instance.readFromNBT((NBTTagCompound) nbt);
	}
	
	
	public static class Factory implements Callable<Revival> {
		
		@Override
		public Revival call() throws Exception {
			return new Revival();
		}
	}
}
