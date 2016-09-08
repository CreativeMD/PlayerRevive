package com.creativemd.playerrevive.packet;

import java.util.UUID;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.server.PlayerReviveServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerRevivalProgress extends CreativeCorePacket {
	
	public PlayerRevivalProgress() {
		
	}
	
	public boolean isReviving = false;
	public UUID playerUUID;
	
	public PlayerRevivalProgress(UUID uuid, boolean isReviving) {
		this.isReviving = isReviving;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		buf.writeBoolean(isReviving);
		writeString(buf, playerUUID.toString());
	}

	@Override
	public void readBytes(ByteBuf buf) {
		isReviving = buf.readBoolean();
		playerUUID = UUID.fromString(readString(buf));
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		PlayerReviveServer.removePlayerAsHelper(player);
		
		if(isReviving)
		{
			Revival revive = PlayerReviveServer.playerRevivals.get(playerUUID);
			if(revive != null)
				revive.revivingPlayers.add(player);
		}
		
	}

}
