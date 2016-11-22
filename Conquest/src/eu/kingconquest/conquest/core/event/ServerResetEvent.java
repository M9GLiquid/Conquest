package eu.kingconquest.conquest.core.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerResetEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private boolean saveKingdoms;
    private boolean saveMembers;
    private Player player;

    public ServerResetEvent(Player player, boolean saveKingdoms, boolean saveMembers){
    	this.saveKingdoms = saveKingdoms;
    	this.saveMembers = saveMembers;
    	this.player = player;
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
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
