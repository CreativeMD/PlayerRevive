package com.creativemd.playerrevive.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.api.capability.CapaRevive;
import com.creativemd.playerrevive.packet.ReviveUpdatePacket;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.DamageSource;
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
	
	public static void startBleeding(EntityPlayer player, DamageSource source)
	{
		getRevival(player).startBleeding(player, source);
		sendUpdatePacket(player);
	}
	
	private static void resetPlayer(EntityPlayer player, IRevival revive)
	{
		player.capabilities.disableDamage = player.capabilities.isCreativeMode;
		
		for (int i = 0; i < revive.getRevivingPlayers().size(); i++) {
			revive.getRevivingPlayers().get(i).closeScreen();
		}
	}
	
	public static void revive(EntityPlayer player)
	{
		IRevival revive = getRevival(player);
		revive.stopBleeding(player);
		resetPlayer(player, revive);
		
		if(!PlayerRevive.disableSounds)
			player.world.playSound(null, player.getPosition(), PlayerRevive.revivedSound, SoundCategory.PLAYERS, 1, 1);	
		
		sendUpdatePacket(player);
	}
	
	public static void kill(EntityPlayer player)
	{
		IRevival revive = getRevival(player);
		DamageSource source = revive.getSource(player);
		PlayerReviveServer.kill(player);
		player.setHealth(0.0F);
		player.onDeath(source);
		resetPlayer(player, revive);
		
		if(!PlayerRevive.disableSounds)
			player.world.playSound(null, player.getPosition(), PlayerRevive.deathSound, SoundCategory.PLAYERS, 1, 1);
		
		if(PlayerRevive.banPlayerAfterDeath)
		{
			GameProfile profile = null;
			profile = player.getGameProfile();
			player.getServer().getPlayerList().getBannedPlayers().addEntry(new UserListBansEntry(player.getGameProfile()));
			try {
				player.getServer().getPlayerList().getBannedPlayers().writeChanges();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		sendUpdatePacket(player);
	}
	
	public static IRevival getRevival(EntityPlayer player)
	{
		return player.getCapability(CapaRevive.reviveCapa, null);
	}
	
	public static void removePlayerAsHelper(EntityPlayer player)
	{
		for (Iterator<EntityPlayerMP> iterator = ReviveEventServer.getMinecraftServer().getPlayerList().getPlayers().iterator(); iterator.hasNext();) {
			EntityPlayerMP member = iterator.next();
			IRevival revive = getRevival(member);
			revive.getRevivingPlayers().remove(player);
		}
		
	}
	
	public void loadSide()
	{
		
	}
}
