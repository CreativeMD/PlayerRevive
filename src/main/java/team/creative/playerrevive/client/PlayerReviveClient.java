package team.creative.playerrevive.client;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.playerrevive.PlayerRevive;

public class PlayerReviveClient {
    
    public static void init(FMLClientSetupEvent event) {
        CreativeCoreClient.registerClientConfig(PlayerRevive.MODID);
        NeoForge.EVENT_BUS.register(new ReviveEventClient());
    }
    
}
