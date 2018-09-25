package eu.kingconquest.conquest.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class NeutralCaptureTrapEvent extends Event{
	private UUID owner;
	private String name;
	private Location location;
	private boolean oneTimeUse;
	private double trapDistance;
	
	public NeutralCaptureTrapEvent(UUID owner, String name, Location location, boolean oneTimeUse, double trapDistance){
		this.owner = owner;
		this.name = name;
		this.location = location;
		this.oneTimeUse = false;
		this.oneTimeUse = oneTimeUse;
		this.trapDistance = trapDistance;
	}
	
	public boolean isPlayerOwner(){
		return Bukkit.getOfflinePlayer(owner) != null;
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public String getTrapName(){
		return name;
	}
	
	public boolean isOneTimeUse(){
		return oneTimeUse;
	}
	
	public double getTrapDistance(){
		return trapDistance;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public World getWorld(){
		return location.getWorld();
	}

    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
