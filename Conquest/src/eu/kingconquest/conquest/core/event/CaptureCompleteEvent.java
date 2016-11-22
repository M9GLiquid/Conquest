package eu.kingconquest.conquest.core.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.kingconquest.conquest.core.Objective;

public class CaptureCompleteEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
	private final Objective objective;
	private boolean captured = false;
	private Player player;

    public CaptureCompleteEvent(Player player, Objective objective) {
    	this.objective = objective;
    	this.player = player;
    }
    
    public Player getPlayer(){
    	return player;
    }

    public Objective getObjective(){
		return objective;
    }
    
    public boolean isCaptured(){
    	return captured;
    }
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
