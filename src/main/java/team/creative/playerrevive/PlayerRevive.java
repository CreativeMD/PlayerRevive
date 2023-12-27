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
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.config.holder.CreativeConfigRegistry;
import team.creative.creativecore.common.network.CreativeNetwork;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.cap.Bleeding;
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
    
    public static final EntityCapability<IBleeding, Void> BLEEDING = EntityCapability.createVoid(BLEEDING_NAME, IBleeding.class);
    
    public void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, x -> {
            x.register(new ResourceLocation(MODID, "death"), DEATH_SOUND);
            x.register(new ResourceLocation(MODID, "revived"), REVIVED_SOUND);
        });
    }
    
    public PlayerRevive(IEventBus bus) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::client));
        bus.addListener(this::init);
        bus.addListener(this::register);
        NeoForge.EVENT_BUS.addListener(this::serverStarting);
        bus.addListener(this::registerCaps);
    }
    
    @OnlyIn(value = Dist.CLIENT)
    private void client(final FMLClientSetupEvent event) {
        CreativeCoreClient.registerClientConfig(MODID);
        NeoForge.EVENT_BUS.register(new ReviveEventClient());
    }
    
    private void init(final FMLCommonSetupEvent event) {
        NETWORK.registerType(ReviveUpdatePacket.class, ReviveUpdatePacket::new);
        NETWORK.registerType(HelperPacket.class, HelperPacket::new);
        NETWORK.registerType(GiveUpPacket.class, GiveUpPacket::new);
        
        CreativeConfigRegistry.ROOT.registerValue(MODID, CONFIG = new PlayerReviveConfig());
        NeoForge.EVENT_BUS.register(new ReviveEventServer());
    }
    
    private void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerEntity(BLEEDING, EntityType.PLAYER, (x, y) -> new Bleeding());
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
