package com.creativemd.playerrevive.config;

import com.creativemd.igcm.api.ConfigTab;

public class PlayerReviveConfig {
	
	public static PlayerReviveBranch branch;
	
	public static void loadConfig() {
		branch = new PlayerReviveBranch();
		ConfigTab.root.registerElement("PlayerRevive", branch);
	}
	
}
