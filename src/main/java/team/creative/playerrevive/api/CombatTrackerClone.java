package team.creative.playerrevive.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import team.creative.playerrevive.mixin.CombatTrackerAccessor;

public class CombatTrackerClone {
    
    private final List<CombatEntry> combatEntries = Lists.<CombatEntry>newArrayList();
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    
    public CombatTrackerClone(CombatTracker tracker) {
        CombatTrackerAccessor ac = (CombatTrackerAccessor) tracker;
        combatEntries.addAll(ac.getEntries());
        lastDamageTime = ac.getLastDamageTime();
        combatStartTime = ac.getCombatStartTime();
        combatEndTime = ac.getCombatEndTime();
        inCombat = ac.getInCombat();
        takingDamage = ac.getTakingDamage();
    }
    
    public void overwriteTracker(CombatTracker tracker) {
        CombatTrackerAccessor ac = (CombatTrackerAccessor) tracker;
        List<CombatEntry> entries = ac.getEntries();
        entries.clear();
        entries.addAll(combatEntries);
        ac.setLastDamageTime(lastDamageTime);
        ac.setCombatStartTime(combatStartTime);
        ac.setCombatEndTime(combatEndTime);
        ac.setInCombat(inCombat);
        ac.setTakingDamage(takingDamage);
    }
    
}
