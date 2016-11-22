package eu.kingconquest.conquest.core.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.kingconquest.conquest.core.Objective;

public class CaptureZoneEnterEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Objective objective;
    private Player player;

    public CaptureZoneEnterEvent(Player player, Objective objective){
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
