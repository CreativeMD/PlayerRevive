package team.creative.playerrevive.cap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.CombatTrackerClone;
import team.creative.playerrevive.api.DamageBledToDeath;
import team.creative.playerrevive.api.IBleeding;
import team.creative.playerrevive.packet.HelperPacket;

public class Bleeding implements IBleeding {
    
    private boolean bleeding;
    private float progress;
    private int timeLeft;
    
    private DamageSource lastSource;
    private CombatTrackerClone trackerClone;
    
    public final List<PlayerEntity> revivingPlayers = new ArrayList<>();
    
    @Override
    public void tick(PlayerEntity player) {
        for (Iterator<PlayerEntity> iterator = revivingPlayers.iterator(); iterator.hasNext();) {
            PlayerEntity helper = iterator.next();
            if (helper.distanceTo(player) > PlayerRevive.CONFIG.maxDistance) {
                PlayerRevive.NETWORK.sendToClient(new HelperPacket(null, false), (ServerPlayerEntity) helper);
                iterator.remove();
            }
        }
        if (revivingPlayers.isEmpty() || !PlayerRevive.CONFIG.haltBleedTime)
            timeLeft--;
        progress += revivingPlayers.size() * PlayerRevive.CONFIG.progressPerPlayer;
        
        if (PlayerRevive.CONFIG.exhaustion > 0)
            for (int i = 0; i < revivingPlayers.size(); i++)
                revivingPlayers.get(i).causeFoodExhaustion(PlayerRevive.CONFIG.exhaustion);
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
        return timeLeft <= 0;
    }
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("timeLeft", timeLeft);
        nbt.putFloat("progress", progress);
        nbt.putBoolean("bleeding", bleeding);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        timeLeft = nbt.getInt("timeLeft");
        progress = nbt.getFloat("progress");
        bleeding = nbt.getBoolean("bleeding");
    }
    
    @Override
    public boolean isBleeding() {
        return bleeding;
    }
    
    @Override
    public void knockOut(PlayerEntity player, DamageSource source) {
        this.bleeding = true;
        this.progress = 0;
        this.timeLeft = PlayerRevive.CONFIG.bleedTime;
        this.lastSource = source;
        this.trackerClone = new CombatTrackerClone(player.getCombatTracker());
    }
    
    @Override
    public void revive() {
        this.bleeding = false;
        this.progress = 0;
        this.timeLeft = 0;
        this.lastSource = null;
        this.trackerClone = null;
    }
    
    @Override
    public int timeLeft() {
        return timeLeft;
    }
    
    @Override
    public List<PlayerEntity> revivingPlayers() {
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
