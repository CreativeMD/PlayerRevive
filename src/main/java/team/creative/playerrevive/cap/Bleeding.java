package team.creative.playerrevive.cap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.CombatTrackerClone;
import team.creative.playerrevive.api.DamageBledToDeath;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.packet.HelperPacket;

public class Bleeding implements IBleeding {
    
    private boolean bleeding;
    private float progress;
    private int timeLeft;
    private int downedTime;
    
    private DamageSource lastSource;
    private CombatTrackerClone trackerClone;
    
    public final List<Player> revivingPlayers = new ArrayList<>();
    
    public Bleeding() {
        
    }
    
    @Override
    public void tick(Player player) {
        for (Iterator<Player> iterator = revivingPlayers.iterator(); iterator.hasNext();) {
            Player helper = iterator.next();
            if (helper.distanceTo(player) > PlayerRevive.CONFIG.maxDistance) {
                PlayerRevive.NETWORK.sendToClient(new HelperPacket(null, false), (ServerPlayer) helper);
                iterator.remove();
            }
        }
        //player.setPose(Pose.SWIMMING);
        if (revivingPlayers.isEmpty() || !PlayerRevive.CONFIG.haltBleedTime)
            timeLeft--;
        progress += revivingPlayers.size() * PlayerRevive.CONFIG.progressPerPlayer;
        downedTime++;
        
        if (PlayerRevive.CONFIG.exhaustion > 0)
            for (int i = 0; i < revivingPlayers.size(); i++)
                revivingPlayers.get(i).causeFoodExhaustion(PlayerRevive.CONFIG.exhaustion);
    }
    
    @Override
    public void forceBledOut() {
        bleeding = true;
        timeLeft = 0;
    }
    
    @Override
    public int downedTime() {
        return downedTime;
    }
    
    @Override
    public float getProgress() {
        return progress;
    }
    
    @Override
    public boolean revived() {
        return progress >= PlayerRevive.CONFIG.requiredReviveProgress;
    }
    
    @Override
    public boolean bledOut() {
        return bleeding && timeLeft <= 0;
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("timeLeft", timeLeft);
        nbt.putFloat("progress", progress);
        nbt.putBoolean("bleeding", bleeding);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        timeLeft = nbt.getInt("timeLeft");
        progress = nbt.getFloat("progress");
        bleeding = nbt.getBoolean("bleeding");
    }
    
    @Override
    public boolean isBleeding() {
        return bleeding;
    }
    
    @Override
    public void knockOut(Player player, DamageSource source) {
        this.bleeding = true;
        this.progress = 0;
        this.downedTime = 0;
        this.timeLeft = PlayerRevive.CONFIG.bleedTime;
        this.lastSource = source;
        this.trackerClone = new CombatTrackerClone(player.getCombatTracker());
    }
    
    @Override
    public void revive() {
        this.bleeding = false;
        this.progress = 0;
        this.timeLeft = 0;
        this.downedTime = 0;
        this.lastSource = null;
        this.trackerClone = null;
    }
    
    @Override
    public int timeLeft() {
        return timeLeft;
    }
    
    @Override
    public List<Player> revivingPlayers() {
        return revivingPlayers;
    }
    
    @Override
    public CombatTrackerClone getTrackerClone() {
        return trackerClone;
    }
    
    @Override
    public DamageSource getSource() {
        if (lastSource != null)
            return lastSource;
        return DamageBledToDeath.BLED_TO_DEATH;
    }
    
}
