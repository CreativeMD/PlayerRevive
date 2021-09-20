package team.creative.playerrevive.api;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CombatTrackerClone {
    
    private static final Field combatEntriesField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19276_");
    private static final Field lastDamageTimeField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19278_");
    private static final Field combatStartTimeField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19279_");
    private static final Field combatEndTimeField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19280_");
    private static final Field inCombatField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19281_");
    private static final Field takingDamageField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19282_");
    private static final Field nextLocationField = ObfuscationReflectionHelper.findField(CombatTracker.class, "f_19283_");
    
    private final List<CombatEntry> combatEntries = Lists.<CombatEntry>newArrayList();
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    private String nextLocation;
    
    public CombatTrackerClone(CombatTracker tracker) {
        try {
            combatEntries.addAll((Collection<? extends CombatEntry>) combatEntriesField.get(tracker));
            lastDamageTime = lastDamageTimeField.getInt(tracker);
            combatStartTime = combatStartTimeField.getInt(tracker);
            combatEndTime = combatEndTimeField.getInt(tracker);
            inCombat = inCombatField.getBoolean(tracker);
            takingDamage = takingDamageField.getBoolean(tracker);
            nextLocation = (String) nextLocationField.get(tracker);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void overwriteTracker(CombatTracker tracker) {
        try {
            List<CombatEntry> entries = (List<CombatEntry>) combatEntriesField.get(tracker);
            entries.clear();
            entries.addAll(combatEntries);
            lastDamageTimeField.setInt(tracker, lastDamageTime);
            combatStartTimeField.setInt(tracker, combatStartTime);
            combatEndTimeField.setInt(tracker, combatEndTime);
            inCombatField.setBoolean(tracker, inCombat);
            takingDamageField.setBoolean(tracker, takingDamage);
            nextLocationField.set(tracker, nextLocation);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
}
