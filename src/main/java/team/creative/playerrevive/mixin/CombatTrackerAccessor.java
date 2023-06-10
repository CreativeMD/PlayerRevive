package team.creative.playerrevive.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;

@Mixin(CombatTracker.class)
public interface CombatTrackerAccessor {
    
    @Accessor
    public List<CombatEntry> getEntries();
    
    @Accessor
    public int getLastDamageTime();
    
    @Accessor
    public void setLastDamageTime(int value);
    
    @Accessor
    public int getCombatStartTime();
    
    @Accessor
    public void setCombatStartTime(int value);
    
    @Accessor
    public int getCombatEndTime();
    
    @Accessor
    public void setCombatEndTime(int value);
    
    @Accessor
    public boolean getInCombat();
    
    @Accessor
    public void setInCombat(boolean value);
    
    @Accessor
    public boolean getTakingDamage();
    
    @Accessor
    public void setTakingDamage(boolean value);
    
}
