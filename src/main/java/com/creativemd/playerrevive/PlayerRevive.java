package com.creativemd.playerrevive;

import java.util.UUID;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.gui.container.SubContainer;
import com.creativemd.creativecore.gui.container.SubGui;
import com.creativemd.creativecore.gui.opener.CustomGuiHandler;
import com.creativemd.creativecore.gui.opener.GuiHandler;
import com.creativemd.playerrevive.capability.CapaReviveStorage;
import com.creativemd.playerrevive.gui.SubContainerRevive;
import com.creativemd.playerrevive.gui.SubGuiRevive;
import com.creativemd.playerrevive.packet.ReviveUpdatePacket;
import com.creativemd.playerrevive.server.PlayerReviveServer;
import com.creativemd.playerrevive.server.ReviveEventServer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = PlayerRevive.modid, version = PlayerRevive.version, name = "Player Revive", acceptedMinecraftVersions = "")
public class PlayerRevive {
	
	@SidedProxy(clientSide = "com.creativemd.playerrevive.client.PlayerReviveClient", serverSide = "com.creativemd.playerrevive.server.PlayerReviveServer")
	public static PlayerReviveServer proxy;
	
	public static final String modid = "playerrevive";
	public static final String version = "0.1";
	
	public static float playerReviveTime = 100;
	public static int playerReviveSurviveTime = 1200;
	
	public static int playerHealthAfter = 2;
	public static int playerFoodAfter = 6;
	
	public static boolean banPlayerAfterDeath = false;
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CreativeCorePacket.registerPacket(ReviveUpdatePacket.class, "PRUpdate");
		
		GuiHandler.registerGuiHandler("plrevive", new CustomGuiHandler() {
			
			@Override
			@SideOnly(Side.CLIENT)
			public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
				return new SubGuiRevive();
			}
			
			@Override
			public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
				return new SubContainerRevive(player, PlayerReviveServer.getRevival(player), false);
			}
		});
		
		GuiHandler.registerGuiHandler("plreviver", new CustomGuiHandler() {
			
			@Override
			@SideOnly(Side.CLIENT)
			public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
				return new SubGuiRevive();
			}
			
			@Override
			public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
				Revival revive = null;
				if(player.world.isRemote)
					revive = PlayerReviveServer.getRevival(player.world.getPlayerEntityByUUID(UUID.fromString(nbt.getString("uuid"))));
				else
					revive = PlayerReviveServer.getRevival(player.getServer().getPlayerList().getPlayerByUUID(UUID.fromString(nbt.getString("uuid"))));
				return new SubContainerRevive(player, revive, true);
			}
		});
		
		CapabilityManager.INSTANCE.register(Revival.class, new CapaReviveStorage(), new CapaReviveStorage.Factory());
		
		MinecraftForge.EVENT_BUS.register(new ReviveEventServer());
		
		proxy.loadSide();
	}
	
}
