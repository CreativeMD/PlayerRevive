package team.creative.playerrevive;

import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.premade.SoundConfig;
import team.creative.creativecore.common.config.sync.ConfigSynchronization;
import team.creative.creativecore.common.util.ingredient.CreativeIngredient;
import team.creative.creativecore.common.util.ingredient.CreativeIngredientItem;

public class PlayerReviveConfig {
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    public boolean disableMusic = false;
    
    @CreativeConfig(type = ConfigSynchronization.CLIENT)
    @CreativeConfig.DecimalRange(min = 0, max = 2)
    public float musicVolume = 1;
    
    @CreativeConfig
    public float requiredReviveProgress = 100;
    @CreativeConfig
    public float progressPerPlayer = 1;
    
    @CreativeConfig
    public double maxDistance = 3;
    
    @CreativeConfig
    public int bleedTime = 1200;
    
    @CreativeConfig
    public boolean haltBleedTime = true;
    
    @CreativeConfig
    public CreativeIngredient reviveItem = new CreativeIngredientItem(Items.PAPER);
    
    @CreativeConfig
    public boolean needReviveItem = false;
    
    @CreativeConfig
    public boolean consumeReviveItem = false;
    
    @CreativeConfig
    @CreativeConfig.IntRange(min = 1, max = 20)
    public int healthAfterRevive = 2;
    
    @CreativeConfig
    public boolean affectFood = true;
    @CreativeConfig
    @CreativeConfig.IntRange(min = 1, max = 20)
    public int foodAfterRevive = 6;
    
    @CreativeConfig
    public float exhaustion = 0.5F;
    
    @CreativeConfig
    public boolean banPlayerAfterDeath = false;
    
    @CreativeConfig
    public boolean disableBleedingMessage = false;
    
    @CreativeConfig
    public Sounds sounds = new Sounds();
    
    @CreativeConfig
    public List<String> bypassDamageSources = Arrays.asList("gorgon", "death.attack.sgcraft:transient", "death.attack.sgcraft:iris");
    
    public static class Sounds {
        
        @CreativeConfig
        public SoundConfig death = new SoundConfig(new ResourceLocation(PlayerRevive.MODID, "death"));
        @CreativeConfig
        public SoundConfig revived = new SoundConfig(new ResourceLocation(PlayerRevive.MODID, "revived"));
        
    }
    
}
