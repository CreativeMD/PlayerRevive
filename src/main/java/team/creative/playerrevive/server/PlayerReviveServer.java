package team.creative.playerrevive.server;

import java.io.IOException;
import java.util.Iterator;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.CombatTrackerClone;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.api.event.PlayerBleedOutEvent;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;
import team.creative.playerrevive.cap.Bleeding;
import team.creative.playerrevive.packet.HelperPacket;
import team.creative.playerrevive.packet.ReviveUpdatePacket;

public class PlayerReviveServer {
    
    public static IBleeding getBleeding(Player player) {
        return player.getCapability(PlayerRevive.BLEEDING).orElseGet(Bleeding::new);
    }
    
    public static void sendUpdatePacket(Player player) {
        ReviveUpdatePacket packet = new ReviveUpdatePacket(player);
        PlayerRevive.NETWORK.sendToClientTracking(packet, player);
        PlayerRevive.NETWORK.sendToClient(packet, (ServerPlayer) player);
    }
    
    public static void startBleeding(Player player, DamageSource source) {
        getBleeding(player).knockOut(player, source);
        sendUpdatePacket(player);
    }
    
    private static void resetPlayer(Player player, IBleeding revive) {
        player.getAbilities().invulnerable = player.isCreative();
        player.setInvulnerable(false);
        
        for (Player helper : revive.revivingPlayers())
            PlayerRevive.NETWORK.sendToClient(new HelperPacket(null, false), (ServerPlayer) helper);
        revive.revivingPlayers().clear();
        
        sendUpdatePacket(player);
    }
    
    public static void revive(Player player) {
        IBleeding revive = getBleeding(player);
        MinecraftForge.EVENT_BUS.post(new PlayerRevivedEvent(player, revive));
        revive.revive();
        
        if (PlayerRevive.CONFIG.revive.hasRevivedMobEffect)
            player.addEffect(PlayerRevive.CONFIG.revive.revivedMobEffect.create());
        resetPlayer(player, revive);
        
        PlayerRevive.CONFIG.sounds.revived.play(player, SoundSource.PLAYERS);
        
        sendUpdatePacket(player);
    }
    
    public static void kill(Player player) {
        IBleeding revive = getBleeding(player);
        MinecraftForge.EVENT_BUS.post(new PlayerBleedOutEvent(player, revive));
        DamageSource source = revive.getSource();
        CombatTrackerClone trackerClone = revive.getTrackerClone();
        if (trackerClone != null)
            trackerClone.overwriteTracker(player.getCombatTracker());
        player.setHealth(0.0F);
        revive.forceBledOut();
        player.die(source);
        resetPlayer(player, revive);
        
        PlayerRevive.CONFIG.sounds.death.play(player, SoundSource.PLAYERS);
        
        if (PlayerRevive.CONFIG.banPlayerAfterDeath) {
            try {
                player.getServer().getPlayerList().getBans().add(new UserBanListEntry(player.getGameProfile()));
                player.getServer().getPlayerList().getBans().save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        sendUpdatePacket(player);
    }
    
    public static void removePlayerAsHelper(Player player) {
        for (Iterator<ServerPlayer> iterator = player.getServer().getPlayerList().getPlayers().iterator(); iterator.hasNext();) {
            ServerPlayer member = iterator.next();
            IBleeding revive = getBleeding(member);
            revive.revivingPlayers().remove(player);
        }
        
    }
}
