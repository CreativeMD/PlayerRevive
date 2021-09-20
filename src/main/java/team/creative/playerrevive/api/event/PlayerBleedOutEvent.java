package team.creative.playerrevive.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import team.creative.playerrevive.api.IBleeding;

/** Fired before a player is killed */
public class PlayerBleedOutEvent extends PlayerEvent {
    
    private final IBleeding bleeding;
    
    public PlayerBleedOutEvent(Player player, IBleeding bleeding) {
        super(player);
        this.bleeding = bleeding;
    }
    
    public IBleeding getBleeding() {
        return this.bleeding;
    }
    
}
