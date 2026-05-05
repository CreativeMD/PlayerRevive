package team.creative.playerrevive.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ReviveCancelEvent extends PlayerEvent {
    
    private final Player target;
    
    public ReviveCancelEvent(Player player, Player target) {
        super(player);
        this.target = target;
    }
    
    public Player getTarget() {
        return target;
    }
}
