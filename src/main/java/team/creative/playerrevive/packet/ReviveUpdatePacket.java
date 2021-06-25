package team.creative.playerrevive.packet;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class ReviveUpdatePacket extends CreativePacket {
    
    public UUID uuid;
    public CompoundNBT nbt;
    
    public ReviveUpdatePacket(PlayerEntity player) {
        this.nbt = PlayerReviveServer.getBleeding(player).serializeNBT();
        this.uuid = player.getUUID();
    }
    
    public ReviveUpdatePacket() {
        
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void executeClient(PlayerEntity player) {
        PlayerEntity member = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        if (member != null) {
            IBleeding bleeding = PlayerReviveServer.getBleeding(member);
            bleeding.deserializeNBT(nbt);
            if (!bleeding.isBleeding())
                member.setPose(Pose.STANDING);
        }
    }
    
    @Override
    public void executeServer(PlayerEntity player) {
        
    }
    
}
