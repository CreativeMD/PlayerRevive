package team.creative.playerrevive.api;

import net.minecraft.util.DamageSource;

public class DamageBledToDeath extends DamageSource {
    
    public static final DamageBledToDeath BLED_TO_DEATH = new DamageBledToDeath();
    
    public DamageBledToDeath() {
        super("bledToDeath");
        bypassArmor();
        bypassInvul();
    }
    
}
