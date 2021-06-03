package team.creative.playerrevive;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.cap.Bleeding;
import team.creative.playerrevive.cap.BleedingStorage;
import team.creative.playerrevive.client.ReviveEventClient;
import team.creative.playerrevive.packet.ReviveUpdatePacket;
import team.creative.playerrevive.server.PlayerReviveServer;
import team.creative.playerrevive.server.ReviveEventServer;

@Mod(PlayerRevive.MODID)
public class PlayerRevive {
    
    public static final Logger LOGGER = LogManager.getLogger(PlayerRevive.MODID);
    public static final String MODID = "playerrevive";
    public static PlayerReviveConfig CONFIG;
    public static final CreativeNetwork NETWORK = new CreativeNetwork("1.0", LOGGER, new ResourceLocation(PlayerRevive.MODID, "main"));
    
    public static final ResourceLocation BLEEDING_NAME = new ResourceLocation(MODID, "bleeding");
    
    public static final SoundEvent DEATH_SOUND = new SoundEvent(new ResourceLocation(MODID, "death")).setRegistryName(new ResourceLocation(MODID, "death"));
    public static final SoundEvent REVIVED_SOUND = new SoundEvent(new ResourceLocation(MODID, "revived")).setRegistryName(new ResourceLocation(MODID, "revived"));
    
    @CapabilityInject(IBleeding.class)
    public static Capability<IBleeding> BLEEDING = null;
    
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(DEATH_SOUND, REVIVED_SOUND);
    }
    
    public PlayerRevive() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerSounds);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        CreativeCoreClient.registerClientConfig(MODID);
        MinecraftForge.EVENT_BUS.register(new ReviveEventClient());
    }
    
    private void init(final FMLCommonSetupEvent event) {
        NETWORK.registerType(ReviveUpdatePacket.class);
        
        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new PlayerReviveConfig());
        CapabilityManager.INSTANCE.register(IBleeding.class, new BleedingStorage(), Bleeding::new);
        
        MinecraftForge.EVENT_BUS.register(new ReviveEventServer());
    }
    
    private void serverStarting(final FMLServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher()
                .register(Commands.literal("revive").requires(x -> x.hasPermission(2)).then(Commands.argument("players", EntityArgument.players()).executes(x -> {
                    Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(x, "players");
                    for (ServerPlayerEntity player : players)
                        PlayerReviveServer.revive(player);
                    return 0;
                })));
    }
    
}
