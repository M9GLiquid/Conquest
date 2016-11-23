package eu.kingconquest.conquest.event;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldResetEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private boolean saveKingdoms;
    private boolean saveMembers;
    private Player player;
    private World world;

    public WorldResetEvent(Player player, World world, boolean saveKingdoms, boolean saveMembers){
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
    public World getWorld(){
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
