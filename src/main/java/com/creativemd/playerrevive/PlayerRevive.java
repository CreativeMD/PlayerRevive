package com.creativemd.playerrevive;

import java.util.UUID;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.opener.CustomGuiHandler;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.api.capability.CapaRevive;
import com.creativemd.playerrevive.config.PlayerReviveConfig;
import com.creativemd.playerrevive.gui.SubContainerRevive;
import com.creativemd.playerrevive.gui.SubGuiRevive;
import com.creativemd.playerrevive.packet.ReviveUpdatePacket;
import com.creativemd.playerrevive.server.PlayerReviveServer;
import com.creativemd.playerrevive.server.ReviveEventServer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = PlayerRevive.modid, version = PlayerRevive.version, name = "Player Revive", acceptedMinecraftVersions = "", dependencies = "required-before:creativecore")
public class PlayerRevive {
	
	@SidedProxy(clientSide = "com.creativemd.playerrevive.client.PlayerReviveClient", serverSide = "com.creativemd.playerrevive.server.PlayerReviveServer")
	public static PlayerReviveServer proxy;
	
	public static SoundEvent deathSound = new SoundEvent(new ResourceLocation(PlayerRevive.modid, "death")).setRegistryName(new ResourceLocation(PlayerRevive.modid, "death"));
	public static SoundEvent revivedSound = new SoundEvent(new ResourceLocation(PlayerRevive.modid, "revived")).setRegistryName(new ResourceLocation(PlayerRevive.modid, "revived"));
	
	public static final String modid = "playerrevive";
	public static final String version = "1.0";
	
	public static int playerReviveTime = 100;
	public static int playerReviveSurviveTime = 1200;
	
	public static int playerHealthAfter = 2;
	public static int playerFoodAfter = 6;
	
	public static boolean banPlayerAfterDeath = false;
	
	public static Configuration config;
	public static float volumeModifier = 1;
	
	public static float exhaustion = 0.5F;
	
	public static boolean disableMusic = false;
	public static boolean disableSounds = false;
	public static boolean disableBleedingMessage = false;
	public static boolean particleBeacon = false;
	
	public static boolean disableGiveUp;
	public static boolean disableDisconnect;
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(deathSound, revivedSound);
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBase() {
			
			@Override
			public String getUsage(ICommandSender sender) {
				return "revive a bleeding player";
			}
			
			@Override
			public String getName() {
				return "revive";
			}
			
			@Override
			public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
				EntityPlayer player = null;
				if (args.length > 1) {
					player = server.getPlayerList().getPlayerByUsername(args[0]);
				} else if (sender instanceof EntityPlayer)
					player = (EntityPlayer) sender;
				
				if (player != null)
					PlayerReviveServer.revive(player);
			}
			
			@Override
			public boolean isUsernameIndex(String[] args, int index) {
				return index == 1;
			}
		});
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		volumeModifier = config.getFloat("volume", "Sound", 1.0F, 0, 2, "Volume of the music played while bleeding");
		playerReviveTime = config.getInt("playerReviveTime", "General", playerReviveTime, 1, Integer.MAX_VALUE, "How long it takes to revive someone (in ticks). Will not be synchronized, therefore it's recommended to install IGCM.");
		playerReviveSurviveTime = config.getInt("playerReviveSurviveTime", "General", playerReviveSurviveTime, 1, Integer.MAX_VALUE, "How long a bleeding player will survive (in ticks). Will not be synchronized, therefore it's recommended to install IGCM.");
		
		playerHealthAfter = config.getInt("playerHealthAfter", "General", playerHealthAfter, 1, 20, "How much health a player will have after being revived.");
		playerFoodAfter = config.getInt("playerFoodAfter", "General", playerFoodAfter, 1, 20, "How much food a player will have after being revived.");
		
		banPlayerAfterDeath = config.getBoolean("banPlayerAfterDeath", "General", banPlayerAfterDeath, "If true someone who died will be banned from the server.");
		
		exhaustion = config.getFloat("exhaustion", "General", exhaustion, 0, 10000, "How exhausted helping players are (determines how much food will be drained).");
		
		disableMusic = config.getBoolean("disableMusic", "Sound", false, "Disable revive and dead sound");
		disableSounds = config.getBoolean("disableSounds", "Sound", false, "Disable revive and dead sound");
		disableBleedingMessage = config.getBoolean("disableBleedingMessage", "General", false, "");
		particleBeacon = config.getBoolean("particleBeacon", "General", false, "Will spawn particles above the player hinted at his location.");
		
		disableGiveUp = config.getBoolean("disableGiveUp", "General", false, "Disables give up button");
		disableDisconnect = config.getBoolean("disableDisconnect", "General", false, "Disables disconnect button");
		config.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//GameRegistry.register(deathSound);
		//GameRegistry.register(revivedSound);
		
		CreativeCorePacket.registerPacket(ReviveUpdatePacket.class);
		
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
				IRevival revive = null;
				if (player.world.isRemote)
					revive = PlayerReviveServer.getRevival(player.world.getPlayerEntityByUUID(UUID.fromString(nbt.getString("uuid"))));
				else
					revive = PlayerReviveServer.getRevival(player.getServer().getPlayerList().getPlayerByUUID(UUID.fromString(nbt.getString("uuid"))));
				return new SubContainerRevive(player, revive, true);
			}
		});
		CapaRevive.register();
		
		MinecraftForge.EVENT_BUS.register(new ReviveEventServer());
		
		proxy.loadSide();
		
		if (Loader.isModLoaded("igcm")) {
			PlayerReviveConfig.loadConfig();
		}
	}
	
}
