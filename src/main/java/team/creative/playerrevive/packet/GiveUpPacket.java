package team.creative.playerrevive.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class GiveUpPacket extends CreativePacket {
    
    @Override
    public void executeClient(Player player) {
        
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        IBleeding bleeding = PlayerReviveServer.getBleeding(player);
        if (bleeding.isBleeding())
            PlayerReviveServer.isBleeding(player);
    }
    
}
