package team.creative.playerrevive.packet;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class ReviveUpdatePacket extends CreativePacket {
    
    public UUID uuid;
    public CompoundTag nbt;
    
    public ReviveUpdatePacket(Player player) {
        this.nbt = PlayerReviveServer.getBleeding(player).serializeNBT();
        this.uuid = player.getUUID();
    }
    
    public ReviveUpdatePacket() {
        
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void executeClient(Player player) {
        Player member = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        if (member != null) {
            IBleeding bleeding = PlayerReviveServer.getBleeding(member);
            bleeding.deserializeNBT(nbt);
            if (!bleeding.isBleeding())
                member.setPose(Pose.STANDING);
        }
    }
    
    @Override
    public void executeServer(ServerPlayer player) {
        
    }
    
}
