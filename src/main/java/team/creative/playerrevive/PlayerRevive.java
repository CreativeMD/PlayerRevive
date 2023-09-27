package team.creative.playerrevive;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegisterEvent;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.client.ReviveEventClient;
import team.creative.playerrevive.packet.GiveUpPacket;
import team.creative.playerrevive.packet.HelperPacket;
import team.creative.playerrevive.packet.ReviveUpdatePacket;
import team.creative.playerrevive.server.PlayerReviveServer;
import team.creative.playerrevive.server.ReviveEventServer;

@Mod(PlayerRevive.MODID)
public class PlayerRevive {
    
    public static final Logger LOGGER = LogManager.getLogger(PlayerRevive.MODID);
    public static final String MODID = "playerrevive";
    public static PlayerReviveConfig CONFIG;
    public static final CreativeNetwork NETWORK = new CreativeNetwork(1, LOGGER, new ResourceLocation(PlayerRevive.MODID, "main"));
    
    public static final ResourceLocation BLEEDING_NAME = new ResourceLocation(MODID, "bleeding");
    public static final ResourceKey<DamageType> BLED_TO_DEATH = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "bled_to_death"));
    
    public static final SoundEvent DEATH_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "death"));
    public static final SoundEvent REVIVED_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "revived"));
    
    public static final Capability<IBleeding> BLEEDING = CapabilityManager.get(new CapabilityToken<>() {});
    
    public void register(RegisterEvent event) {
        event.register(Keys.SOUND_EVENTS, x -> {
            x.register(new ResourceLocation(MODID, "death"), DEATH_SOUND);
            x.register(new ResourceLocation(MODID, "revived"), REVIVED_SOUND);
        });
    }
    
    public PlayerRevive() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::register);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCaps);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        CreativeCoreClient.registerClientConfig(MODID);
        MinecraftForge.EVENT_BUS.register(new ReviveEventClient());
    }
    
    private void init(final FMLCommonSetupEvent event) {
        NETWORK.registerType(ReviveUpdatePacket.class, ReviveUpdatePacket::new);
        NETWORK.registerType(HelperPacket.class, HelperPacket::new);
        NETWORK.registerType(GiveUpPacket.class, GiveUpPacket::new);
        
        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new PlayerReviveConfig());
        MinecraftForge.EVENT_BUS.register(new ReviveEventServer());
    }
    
    private void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IBleeding.class);
    }
    
    private void serverStarting(final ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(Commands.literal("revive").requires(x -> x.hasPermission(2)).then(Commands.argument("players", EntityArgument
                .players()).executes(x -> {
                    Collection<ServerPlayer> players = EntityArgument.getPlayers(x, "players");
                    for (ServerPlayer player : players)
                        if (PlayerReviveServer.getBleeding(player).isBleeding())
                            PlayerReviveServer.revive(player);
                    return 0;
                })));
    }
    
}
