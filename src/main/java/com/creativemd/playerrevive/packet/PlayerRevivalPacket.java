package com.creativemd.playerrevive.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.client.PlayerReviveClient;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerRevivalPacket extends CreativeCorePacket {
	
	public PlayerRevivalPacket() {
		
	}
	
	public Revival revive;
	
	public PlayerRevivalPacket(Revival revive) {
		this.revive = revive;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		NBTTagCompound nbt = new NBTTagCompound();
		if(revive != null)
			revive.writeToNBT(nbt);
		writeNBT(buf, nbt);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		NBTTagCompound nbt = readNBT(buf);
		if(nbt.getSize() == 0)
			revive = null;
		else{
			revive = new Revival();
			revive.readFromNBT(nbt);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		PlayerReviveClient.playerRevive = revive;
	}

	@Override
	public void executeServer(EntityPlayer player) {
		
	}
	
}
