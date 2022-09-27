package team.creative.playerrevive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    
    @Accessor
    public void setMissTime(int time);
    
}
