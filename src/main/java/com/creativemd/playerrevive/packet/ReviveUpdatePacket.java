package com.creativemd.playerrevive.packet;

import java.util.UUID;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.playerrevive.server.PlayerReviveServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReviveUpdatePacket extends CreativeCorePacket {
	
	public EntityPlayer player;
	public UUID uuid;
	public NBTTagCompound nbt;
	
	public ReviveUpdatePacket(EntityPlayer player) {
		this.player = player;
	}
	
	public ReviveUpdatePacket() {
		
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		NBTTagCompound nbt = new NBTTagCompound();
		PlayerReviveServer.getRevival(player).writeToNBT(nbt);
		writeNBT(buf, nbt);
		writeString(buf, player.getUniqueID().toString());
	}

	@Override
	public void readBytes(ByteBuf buf) {
		nbt = readNBT(buf);
		uuid = UUID.fromString(readString(buf));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer player) {
		EntityPlayer member = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
		if(member != null)
			PlayerReviveServer.getRevival(member).readFromNBT(nbt);
	}

	@Override
	public void executeServer(EntityPlayer player) {
		
	}
	
}
