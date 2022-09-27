package team.creative.playerrevive;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.premade.MobEffectConfig;
import team.creative.creativecore.common.config.premade.SoundConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItem;

public class PlayerReviveConfig {
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    public boolean disableMusic = true;
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    @CreativeConfig.DecimalRange(min = 0, max = 2)
    public float bleedingMusicVolume = 1;
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    @CreativeConfig.DecimalRange(min = 0, max = 2)
    public float countdownMusicVolume = 1;
    
    @CreativeConfig
    public boolean banPlayerAfterDeath = false;
    
    @CreativeConfig
    public Bleeding bleeding = new Bleeding();
    
    @CreativeConfig
    public Revive revive = new Revive();
    
    @CreativeConfig
    public Sounds sounds = new Sounds();
    
    @CreativeConfig
    public List<String> bypassDamageSources = Arrays.asList("gorgon", "death.attack.sgcraft:transient", "death.attack.sgcraft:iris", "vampirism_dbno");
    
    @CreativeConfig
    public boolean bleedInSingleplayer = false;
    
    public static class Bleeding {
        
        @CreativeConfig
        public int bleedTime = 1200;
        
        @CreativeConfig(type = ConfigSynchronization.CLIENT)
        public int giveUpSeconds = 5;
        
        @CreativeConfig
        public boolean triggerForCreative = true;
        
        @CreativeConfig
        public boolean bleedingMessage = true;
        @CreativeConfig
        public boolean bleedingMessageTrackingOnly = true;
        
        @CreativeConfig
        public boolean shouldGlow = false;
        
        @CreativeConfig
        public boolean hasBleedingMobEffect = true;
        @CreativeConfig
        public MobEffectConfig bleedingMobEffect = new MobEffectConfig(Registry.MOB_EFFECT, new ResourceLocation("minecraft", "slowness"), 2, 10);
        
        @CreativeConfig
        public boolean affectHunger = true;
        @CreativeConfig
        @CreativeConfig.IntRange(min = 1, max = 20)
        public int remainingHunger = 6;
        
        @CreativeConfig
        public int bleedingHealth = 10;
        
        @CreativeConfig
        public boolean disableMobDamage = true;
        
        @CreativeConfig
        public boolean disablePlayerDamage = false;
        
        @CreativeConfig
        public boolean disableOtherDamage = false;
        
        @CreativeConfig
        public boolean hasShaderEffect = true;
        
    }
    
    public static class Revive {
        
        @CreativeConfig
        public float requiredReviveProgress = 100;
        @CreativeConfig
        public float progressPerPlayer = 1;
        @CreativeConfig
        public float exhaustion = 0.5F;
        
        @CreativeConfig
        public boolean haltBleedTime = true;
        @CreativeConfig
        public boolean abortOnDamage = false;
        @CreativeConfig
        public boolean resetProgress = false;
        
        @CreativeConfig
        public boolean hasRevivedMobEffect = false;
        @CreativeConfig
        public MobEffectConfig revivedMobEffect = new MobEffectConfig(Registry.MOB_EFFECT, new ResourceLocation("minecraft", "slowness"), 2, 1);
        
        @CreativeConfig
        public CreativeIngredient reviveItem = new CreativeIngredientItem(Items.PAPER);
        @CreativeConfig
        public boolean needReviveItem = false;
        @CreativeConfig
        public boolean consumeReviveItem = false;
        
        @CreativeConfig
        public double maxDistance = 3;
        
        @CreativeConfig
        @CreativeConfig.IntRange(min = 1, max = 20)
        public int healthAfter = 2;
        
    }
    
    public static class Sounds {
        
        @CreativeConfig
        public SoundConfig death = new SoundConfig(new ResourceLocation(PlayerRevive.MODID, "death"));
        @CreativeConfig
        public SoundConfig revived = new SoundConfig(new ResourceLocation(PlayerRevive.MODID, "revived"));
        
    }
    
}
