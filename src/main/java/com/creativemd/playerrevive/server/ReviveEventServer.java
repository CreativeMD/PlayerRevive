package com.creativemd.playerrevive.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.playerrevive.DamageBledToDeath;
import com.creativemd.playerrevive.PlayerRevive;
import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.capability.CapaReviveProvider;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ReviveEventServer {
	
	private static Boolean isClient = null;
	
	public static boolean isClient()
	{
		if(isClient == null){
			try {
				isClient = Class.forName("net.minecraft.client.Minecraft") != null;
			} catch (ClassNotFoundException e) {
				isClient = false;
			}
		}
		return isClient;
	}
	
	public static boolean isReviveActive()
	{
		if(isClient())
			return !isSinglePlayer();
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	private static boolean isSinglePlayer()
	{
		return Minecraft.getMinecraft().isSingleplayer();
	}
	
	//private static MinecraftServer server = FMLServerHandler.instance().getServer();
	
	@SubscribeEvent
	public void tick(ServerTickEvent event)
	{
		if(event.phase == Phase.END && isReviveActive())
		{
			ArrayList<UUID> removeFromList = new ArrayList<>();
			
			for (Iterator<EntityPlayerMP> iterator = FMLServerHandler.instance().getServer().getPlayerList().getPlayers().iterator(); iterator.hasNext();) {
				EntityPlayerMP player = iterator.next();
				Revival revive = PlayerReviveServer.getRevival(player);
				
				if(!revive.isHealty())
				{
					revive.tick();
					
					if(revive.getTimeLeft() % 20 == 0)
						PlayerReviveServer.sendUpdatePacket(player);
					
					player.getFoodStats().setFoodLevel(PlayerRevive.playerFoodAfter);
					player.setHealth(PlayerRevive.playerHealthAfter);
					player.capabilities.disableDamage = true;
					
					if(revive.isRevived() || revive.isDead())
					{
						PlayerReviveServer.stopBleeding(player);						
						
						if(player != null)
						{
							player.capabilities.disableDamage = player.capabilities.isCreativeMode;
							
							if(revive.isDead())
							{
								player.setHealth(0.0F);
								player.onDeath(DamageBledToDeath.bledToDeath);
								
							}
							for (int i = 0; i < revive.revivingPlayers.size(); i++) {
								revive.revivingPlayers.get(i).closeScreen();
							}
						}
						
						if(revive.isDead() && PlayerRevive.banPlayerAfterDeath)
						{
							GameProfile profile = null;
							profile = player.getGameProfile();
							FMLServerHandler.instance().getServer().getPlayerList().getBannedPlayers().addEntry(new UserListBansEntry(player.getGameProfile()));
							try {
								FMLServerHandler.instance().getServer().getPlayerList().getBannedPlayers().writeChanges();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}		
	}
	
	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public void playerLeave(PlayerLoggedOutEvent event)
	{
		Revival revive = PlayerReviveServer.getRevival(event.player);
		if(!revive.isHealty())
		{
			PlayerReviveServer.stopBleeding(event.player);
			event.player.setHealth(0.0F);
			event.player.onDeath(DamageBledToDeath.bledToDeath);
		}
		if(!event.player.world.isRemote)
			PlayerReviveServer.removePlayerAsHelper(event.player);
	}
	
	/*@SubscribeEvent
	public void playerJoin(PlayerLoggedInEvent event)
	{
		if(!isReviveActive())
			return ;
		Revival revive = PlayerReviveServer.playerRevivals.get(EntityPlayer.getUUID(event.player.getGameProfile()));
		if(revive != null)
			PacketHandler.sendPacketToPlayer(new PlayerRevivalPacket(revive), (EntityPlayerMP) event.player);
	}*/
	
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent.EntityInteract event)
	{
		if(!PlayerReviveServer.isPlayerBleeding(event.getEntityPlayer()) && event.getTarget() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote)
		{
			EntityPlayer player = (EntityPlayer) event.getTarget();
			Revival revive = PlayerReviveServer.getRevival(player);
			if(!revive.isHealty())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("uuid", EntityPlayer.getUUID(player.getGameProfile()).toString());
				revive.revivingPlayers.add(event.getEntityPlayer());
				GuiHandler.openGui("plreviver", nbt, event.getEntityPlayer());
				//System.out.println("OPEN GUI!");
			}
		}
	}
	
	@SubscribeEvent
	public void playerDamage(LivingHurtEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(PlayerReviveServer.isPlayerBleeding(player) && event.getSource() != DamageBledToDeath.bledToDeath)
				event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void playerDied(LivingDeathEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer && isReviveActive() && !event.getEntityLiving().world.isRemote && event.getSource() != DamageBledToDeath.bledToDeath)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			Revival revive = PlayerReviveServer.getRevival(player);
			if(revive.isHealty())
				PlayerReviveServer.startBleeding(player);
			
			if(!revive.isDead())
			{
				event.setCanceled(true);
				player.setHealth(0.5F);
				player.getFoodStats().setFoodLevel(1);
			}
		}
	}
	
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent.Entity event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			event.addCapability(new ResourceLocation(PlayerRevive.modid, "revive"), new CapaReviveProvider());
		}
	}
	
}
