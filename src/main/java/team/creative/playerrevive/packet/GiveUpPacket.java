package team.creative.playerrevive.packet;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class GiveUpPacket extends CreativePacket {
    
    @Override
    public void executeClient(PlayerEntity player) {
        
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        IBleeding bleeding = PlayerReviveServer.getBleeding(player);
        if (bleeding.isBleeding())
            PlayerReviveServer.kill(player);
    }
    
}
