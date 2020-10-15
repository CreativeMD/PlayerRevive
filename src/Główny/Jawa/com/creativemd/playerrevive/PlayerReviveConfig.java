package com.creativemd.playerrevive;

import java.util.Arrays;
import java.util.List;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;

public class PlayerReviveConfig {
	
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	@CreativeConfig.DecimalRange(min = 0, max = 2)
	public float volumeModifier = 1;
	
	@CreativeConfig
	public int playerReviveTime = 600 ;
	@CreativeConfig
	public int playerReviveSurviveTime = 1200;
	
	@CreativeConfig
	@CreativeConfig.IntRange(min = 1, max = 20)
	public int playerHealthAfter = 2;
	@CreativeConfig
	@CreativeConfig.IntRange(min = 1, max = 20)
	public int playerFoodAfter = 6;
	
	@CreativeConfig
	public boolean banPlayerAfterDeath = false;
	
	@CreativeConfig
	public float exhaustion = 0.5F;
	
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	public boolean disableMusic = false;
	@CreativeConfig
	public boolean disableSounds = false;
	
	@CreativeConfig
	public boolean disableBleedingMessage = false;
	@CreativeConfig(type = ConfigSynchronization.CLIENT)
	public boolean particleBeacon = false;
	
	@CreativeConfig
	public boolean disableGiveUp;
	@CreativeConfig
	public boolean disableDisconnect;
	
	@CreativeConfig
	public boolean allowCommandsWhileBleeding = false;
	
	@CreativeConfig
	public List<String> bypassDamageSources = Arrays.asList("gorgon", "death.attack.sgcraft:transient", "death.attack.sgcraft:iris");
	
}
