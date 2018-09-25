package eu.kingconquest.conquest.event;

import eu.kingconquest.conquest.core.Objective;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ObjectiveDeleteEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Objective objective;

    public ObjectiveDeleteEvent(Player player, Objective objective){
    	this.player = player;
    	this.objective = objective;
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
