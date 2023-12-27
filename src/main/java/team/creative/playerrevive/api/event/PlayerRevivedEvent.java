package team.creative.playerrevive.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import team.creative.playerrevive.api.IBleeding;

/** Fired before a player is revived. */
public class PlayerRevivedEvent extends PlayerEvent {
    
    private final IBleeding bleeding;
    
    public PlayerRevivedEvent(Player player, IBleeding bleeding) {
        super(player);
        this.bleeding = bleeding;
    }
    
    public IBleeding getBleeding() {
        return this.bleeding;
    }
}
