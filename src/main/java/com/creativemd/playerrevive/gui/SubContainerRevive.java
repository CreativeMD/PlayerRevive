package com.creativemd.playerrevive.gui;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.premade.SubContainerTileEntity;
import com.creativemd.playerrevive.Revival;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerRevive extends SubContainer{
	
	public Revival revive;
	
	public SubContainerRevive(EntityPlayer player, Revival revive) {
		super(player);
		this.revive = revive;
	}

	@Override
	public void createControls() {
		
	}
	
	public int currentTick = 0;
	
	@Override
	public void onTick()
	{
		currentTick++;
		if(currentTick > 10)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			revive.writeToNBT(nbt);
			sendNBTToGui(nbt);
			currentTick = 0;
		}
	}

	@Override
	public void onPacketReceive(NBTTagCompound nbt) {
		
	}

}
