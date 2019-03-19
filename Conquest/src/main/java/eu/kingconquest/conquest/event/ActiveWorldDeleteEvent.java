package eu.kingconquest.conquest.event;

import eu.kingconquest.conquest.core.ActiveWorld;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ActiveWorldDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private ActiveWorld aWorld;

    public ActiveWorldDeleteEvent(ActiveWorld aWorld) {
        this.aWorld = aWorld;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ActiveWorld getActiveWorld() {
        return aWorld;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
