package eu.kingconquest.conquest.event;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TrapEvent extends Event{
	private UUID owner;
	private String name;
	private Location location;
	
	public TrapEvent(UUID owner, String name, Location location){
		this.owner = owner;
		this.name = name;
		this.location = location;
	}
	
	public boolean isPlayerOwner(){
		if (Bukkit.getOfflinePlayer(owner) != null)
			return true;
		return false;
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public String getTrapName(){
		return name;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public World getWorld(){
		return location.getWorld();
	}
	
	@Override
	public HandlerList getHandlers(){
		// TODO Auto-generated method stub
		return null;
	}

}
