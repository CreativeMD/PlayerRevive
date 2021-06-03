package team.creative.playerrevive.api;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;

public interface IBleeding extends INBTSerializable<CompoundNBT> {
    
    public void tick(PlayerEntity player);
    
    public float getProgress();
    
    public boolean isBleeding();
    
    public boolean bledOut();
    
    public void knockOut(PlayerEntity player, DamageSource source);
    
    public boolean revived();
    
    public void revive();
    
    public int timeLeft();
    
    public List<PlayerEntity> revivingPlayers();
    
    public DamageSource getSource();
    
    public CombatTrackerClone getTrackerClone();
    
}
