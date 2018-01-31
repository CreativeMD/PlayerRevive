package com.creativemd.playerrevive.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.api.capability.CapaRevive;
import com.creativemd.playerrevive.packet.ReviveUpdatePacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PlayerReviveServer {
	
	//private static MinecraftServer server = FMLServerHandler.instance().getServer();
	
	public static boolean isPlayerBleeding(EntityPlayer player)
	{
		return !player.getCapability(CapaRevive.reviveCapa, null).isHealty();
	}
	
	public static void sendUpdatePacket(EntityPlayer player)
	{
		ReviveUpdatePacket packet = new ReviveUpdatePacket(player);
		PacketHandler.sendPacketToTrackingPlayers(packet, (EntityPlayerMP) player);
		PacketHandler.sendPacketToPlayer(packet, (EntityPlayerMP) player);
	}
	
	public static void startBleeding(EntityPlayer player)
	{
		getRevival(player).startBleeding();
		sendUpdatePacket(player);
	}
	
	public static void stopBleeding(EntityPlayer player)
	{
		getRevival(player).stopBleeding();
		if(!PlayerRevive.disableSounds)
			player.worldObj.playSound(null, player.getPosition(), PlayerRevive.revivedSound, SoundCategory.PLAYERS, 1, 1);	
		sendUpdatePacket(player);
	}
	
	public static IRevival getRevival(EntityPlayer player)
	{
		return player.getCapability(CapaRevive.reviveCapa, null);
	}
	
	public static void removePlayerAsHelper(EntityPlayer player)
	{
		for (Iterator<EntityPlayerMP> iterator = ReviveEventServer.getMinecraftServer().getPlayerList().getPlayerList().iterator(); iterator.hasNext();) {
			EntityPlayerMP member = iterator.next();
			IRevival revive = getRevival(member);
			revive.getRevivingPlayers().remove(player);
		}
		
	}
	
	public void loadSide()
	{
		
	}
}
