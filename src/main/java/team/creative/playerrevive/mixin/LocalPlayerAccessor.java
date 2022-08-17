package team.creative.playerrevive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {
    
    @Accessor
    public boolean getHandsBusy();
    
    @Accessor
    public void setHandsBusy(boolean value);
    
}
