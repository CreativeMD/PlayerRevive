package com.creativemd.playerrevive;

import net.minecraft.util.DamageSource;

public class DamageBledToDeath extends DamageSource {

	public static DamageBledToDeath bledToDeath = new DamageBledToDeath();
	
	public DamageBledToDeath() {
		super("bledToDeath");
		setDamageBypassesArmor();
	}

}
