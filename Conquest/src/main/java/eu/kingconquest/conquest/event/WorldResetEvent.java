package eu.kingconquest.conquest.event;

import eu.kingconquest.conquest.core.ActiveWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldResetEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private boolean saveKingdoms;
    private boolean saveMembers;
    private Player player;
    private ActiveWorld world;

    public WorldResetEvent(Player player, ActiveWorld world, boolean saveKingdoms, boolean saveMembers) {
    	this.saveKingdoms = saveKingdoms;
    	this.saveMembers = saveMembers;
    	this.player = player;
    	this.world = world;
    }
    public boolean getKingdomSave(){
    	return saveKingdoms;
    }
    public boolean getMemberSave(){
    	return saveMembers;
    }
    
    public Player getPlayer(){
    	return player;
    }

    public ActiveWorld getWorld() {
    	return world;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
