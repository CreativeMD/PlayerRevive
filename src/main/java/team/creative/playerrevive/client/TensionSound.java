package team.creative.playerrevive.client;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class TensionSound extends LocatableSound implements ITickableSound {
    
    private boolean stopped;
    
    public TensionSound(ResourceLocation resource, float volume, float pitch, boolean loop) {
        super(resource, SoundCategory.PLAYERS);
        this.looping = loop;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    @Override
    public boolean isStopped() {
        return this.stopped;
    }
    
    protected final void stop() {
        this.stopped = true;
        this.looping = false;
    }
    
    @Override
    public void tick() {}
    
}
