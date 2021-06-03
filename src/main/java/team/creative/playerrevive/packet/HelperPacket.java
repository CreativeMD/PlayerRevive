package team.creative.playerrevive.packet;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.network.CanBeNull;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.client.ReviveEventClient;

public class HelperPacket extends CreativePacket {
    
    @CanBeNull
    public UUID helping;
    public boolean start;
    
    public HelperPacket(UUID helping, boolean start) {
        this.helping = helping;
        this.start = start;
    }
    
    public HelperPacket() {
        
    }
    
    @Override
    public void executeClient(PlayerEntity player) {
        ReviveEventClient.helpActive = start;
        ReviveEventClient.helpTarget = helping;
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        
    }
    
}
