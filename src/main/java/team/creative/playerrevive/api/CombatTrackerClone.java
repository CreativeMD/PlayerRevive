package team.creative.playerrevive.api;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CombatTrackerClone {
    
    private static final Field combatEntriesField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_94556_a");
    private static final Field lastDamageTimeField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_94555_c");
    private static final Field combatStartTimeField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_152775_d");
    private static final Field combatEndTimeField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_152776_e");
    private static final Field inCombatField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_94552_d");
    private static final Field takingDamageField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_94553_e");
    private static final Field nextLocationField = ObfuscationReflectionHelper.findField(CombatTracker.class, "field_94551_f");
    
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
