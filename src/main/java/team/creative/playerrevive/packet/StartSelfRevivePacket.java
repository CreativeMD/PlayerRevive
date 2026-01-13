package team.creative.playerrevive.packet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import team.creative.creativecore.common.network.CreativePacket;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.server.PlayerReviveServer;

public class StartSelfRevivePacket extends CreativePacket {
    
    @Override
    public void executeClient(Player player) {}
    
    @Override
    public void executeServer(ServerPlayer player) {
        if (!PlayerRevive.CONFIG.revive.selfRevive.enabled)
            return;
        
        IBleeding bleeding = PlayerReviveServer.getBleeding(player);
        if (bleeding == null || bleeding.isSelfReviving())
            return;
        
        boolean consumed = false;
        if (PlayerRevive.CONFIG.revive.selfRevive.item.is(player.getMainHandItem())) {
            if (PlayerRevive.CONFIG.revive.selfRevive.consumeItem) {
                player.getInventory().items.get(player.getInventory().selected).shrink(1);
                player.getInventory().setChanged();
            }
            consumed = true;
        }
        
        if (!consumed && PlayerRevive.CONFIG.revive.selfRevive.item.is(player.getOffhandItem())) {
            if (PlayerRevive.CONFIG.revive.selfRevive.consumeItem) {
                player.getInventory().offhand.getFirst().shrink(1);
                player.getInventory().setChanged();
            }
            consumed = true;
        }
        
        if (!consumed)
            return;
        
        bleeding.startSelfRevive();
        PlayerRevive.CONFIG.revive.selfRevive.sound.play(player, SoundSource.PLAYERS);
    }
    
}
