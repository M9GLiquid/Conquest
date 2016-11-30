package eu.kingconquest.conquest.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.kingconquest.conquest.core.Objective;

public class CaptureNeutralEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
	private final Objective objective;
    private Player player;

    public CaptureNeutralEvent(Player player, Objective objective) {
    	this.objective = objective;
    	this.player = player;
    }
    
    public Player getPlayer(){
    	return player;
    }

    public Objective getObjective(){
		return objective;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
