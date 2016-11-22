package eu.kingconquest.conquest.core.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerRestartEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

	/**
	 * Set Boards on server Restart
	 * @return void
	 */
	public ServerRestartEvent(){
	}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
