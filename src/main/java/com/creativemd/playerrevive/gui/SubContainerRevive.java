package com.creativemd.playerrevive.gui;

import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.premade.SubContainerTileEntity;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.server.PlayerReviveServer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerRevive extends SubContainer{
	
	public IRevival revive;
	
	public boolean isHelping;
	
	public SubContainerRevive(EntityPlayer player, IRevival revive, boolean isHelping) {
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
			NBTTagCompound nbt = revive.serializeNBT();
			sendNBTToGui(nbt);
			currentTick = 0;
		}
	}

	@Override
	public void onPacketReceive(NBTTagCompound nbt) {
		if(nbt.getBoolean("giveup") && !isHelping)
			PlayerReviveServer.kill(player);
	}
	
	@Override
	public void onClosed()
	{
		super.onClosed();
		if(isHelping && !player.getEntityWorld().isRemote)
			PlayerReviveServer.removePlayerAsHelper(player);
	}

}
