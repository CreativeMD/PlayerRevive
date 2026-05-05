package team.creative.playerrevive.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ReviveStartEvent extends PlayerEvent {
    
    private final Player target;
    
    public ReviveStartEvent(Player player, Player target) {
        super(player);
        this.target = target;
    }
    
    public Player getTarget() {
        return target;
    }
}
