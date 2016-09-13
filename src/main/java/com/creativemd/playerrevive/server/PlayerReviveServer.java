package com.creativemd.playerrevive.server;

import java.util.HashMap;
import java.util.UUID;

import com.creativemd.playerrevive.Revival;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerReviveServer {
	
	/**Information needs to be saved inside the player file!**/
	public static HashMap<UUID, Revival> playerRevivals = new HashMap<>();
	
	public static boolean isPlayerBleeding(EntityPlayer player)
	{
		return playerRevivals.containsKey(EntityPlayer.getUUID(player.getGameProfile()));
	}
	
	public static Revival getRevival(EntityPlayer player)
	{
		return playerRevivals.get(EntityPlayer.getUUID(player.getGameProfile()));
	}
	
	public static void removePlayerAsHelper(EntityPlayer player)
	{
		for (UUID uuid : playerRevivals.keySet()) {
			Revival revive = playerRevivals.get(uuid);
			revive.revivingPlayers.remove(player);
		}
	}
	
	public void initSide()
	{
		
	}
	
}
