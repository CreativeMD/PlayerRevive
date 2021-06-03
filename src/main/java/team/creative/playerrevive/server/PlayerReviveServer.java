package team.creative.playerrevive.server;

import java.io.IOException;
import java.util.Iterator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.CombatTrackerClone;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.api.event.PlayerBleedOutEvent;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;
import team.creative.playerrevive.packet.ReviveUpdatePacket;

public class PlayerReviveServer {
    
    public static IBleeding getBleeding(PlayerEntity player) {
        return player.getCapability(PlayerRevive.BLEEDING, null).orElseThrow(RuntimeException::new);
    }
    
    public static void sendUpdatePacket(PlayerEntity player) {
        ReviveUpdatePacket packet = new ReviveUpdatePacket(player);
        PlayerRevive.NETWORK.sendToClientTracking(packet, player);
        PlayerRevive.NETWORK.sendToClient(packet, (ServerPlayerEntity) player);
    }
    
    public static void startBleeding(PlayerEntity player, DamageSource source) {
        getBleeding(player).knockOut(player, source);
        sendUpdatePacket(player);
    }
    
    private static void resetPlayer(PlayerEntity player, IBleeding revive) {
        player.abilities.invulnerable = player.isCreative();
        player.setInvulnerable(false);
        
        for (int i = 0; i < revive.revivingPlayers().size(); i++)
            sendPacketToNotify
    }
    
    public static void revive(PlayerEntity player) {
        IBleeding revive = getBleeding(player);
        MinecraftForge.EVENT_BUS.post(new PlayerRevivedEvent(player, revive));
        revive.revive();
        resetPlayer(player, revive);
        
        PlayerRevive.CONFIG.sounds.revived.play(player, SoundCategory.PLAYERS);
        
        sendUpdatePacket(player);
    }
    
    public static void kill(PlayerEntity player) {
        IBleeding revive = getBleeding(player);
        MinecraftForge.EVENT_BUS.post(new PlayerBleedOutEvent(player, revive));
        DamageSource source = revive.getSource();
        CombatTrackerClone trackerClone = revive.getTrackerClone();
        if (trackerClone != null)
            trackerClone.overwriteTracker(player.getCombatTracker());
        revive.bledOut();
        player.setHealth(0.0F);
        player.die(source);
        resetPlayer(player, revive);
        
        PlayerRevive.CONFIG.sounds.death.play(player, SoundCategory.PLAYERS);
        
        if (PlayerRevive.CONFIG.banPlayerAfterDeath) {
            try {
                player.getServer().getPlayerList().getBans().add(new ProfileBanEntry(player.getGameProfile()));
                player.getServer().getPlayerList().getBans().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        sendUpdatePacket(player);
    }
    
    public static void removePlayerAsHelper(PlayerEntity player) {
        for (Iterator<ServerPlayerEntity> iterator = player.getServer().getPlayerList().getPlayers().iterator(); iterator.hasNext();) {
            ServerPlayerEntity member = iterator.next();
            IBleeding revive = getBleeding(member);
            revive.revivingPlayers().remove(player);
        }
        
    }
}
