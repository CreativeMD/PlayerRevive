package com.creativemd.playerrevive.gui;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.premade.SubContainerTileEntity;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.server.PlayerReviveServer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerRevive extends SubContainer{
	
	public Revival revive;
	
	public boolean isHelping;
	
	public SubContainerRevive(EntityPlayer player, Revival revive, boolean isHelping) {
		super(player);
		this.revive = revive;
		this.isHelping = isHelping;
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
	
	@Override
	public void onClosed()
	{
		super.onClosed();
		if(isHelping && !player.getEntityWorld().isRemote)
			PlayerReviveServer.removePlayerAsHelper(player);
		//System.out.println("CLOSING!");
		
	}

}
