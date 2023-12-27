package team.creative.playerrevive.api;

import java.util.List;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IBleeding extends INBTSerializable<CompoundTag> {
    
    public void tick(Player player);
    
    public float getProgress();
    
    public boolean isBleeding();
    
    public boolean bledOut();
    
    public void forceBledOut();
    
    public void knockOut(Player player, DamageSource source);
    
    public boolean revived();
    
    public void revive();
    
    public int timeLeft();
    
    public int downedTime();
    
    public List<Player> revivingPlayers();
    
    public DamageSource getSource(RegistryAccess access);
    
    public CombatTrackerClone getTrackerClone();
    
    public boolean isItemConsumed();
    
    public void setItemConsumed();
    
}
