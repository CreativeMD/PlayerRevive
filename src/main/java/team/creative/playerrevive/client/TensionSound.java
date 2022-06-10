package team.creative.playerrevive.client;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class TensionSound extends AbstractSoundInstance implements TickableSoundInstance {
    
    private boolean stopped;
    
    public TensionSound(ResourceLocation resource, float volume, float pitch, boolean loop) {
        super(resource, SoundSource.PLAYERS, RandomSource.create());
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
