package team.creative.playerrevive.packet;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class ReviveUpdatePacket extends CreativePacket {
    
    public UUID uuid;
    public CompoundTag nbt;
    
    public ReviveUpdatePacket(Player player) {
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
        PlayerReviveServer.getBleeding(player).serialize(output);
        this.nbt = output.buildResult();
        this.uuid = player.getUUID();
    }
    
    public ReviveUpdatePacket() {
        
    }
    
    @Override
    public void executeClient(Player player) {
        Player member = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        if (member != null) {
            IBleeding bleeding = PlayerReviveServer.getBleeding(member);
            bleeding.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, player.registryAccess(), nbt));
            if (!bleeding.isBleeding())
                member.setPose(Pose.STANDING);
        }
    }
    
    @Override
    public void executeServer(ServerPlayer player) {}
    
}
