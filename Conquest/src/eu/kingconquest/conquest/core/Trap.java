package eu.kingconquest.conquest.core;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;

@SuppressWarnings("unused")
public abstract class Trap{
	private boolean triggered = false;
	private boolean oneTimeTrigger = false;
	private final long cooldown = 10L;
	private String name;
	private Location location;
	private UUID owner;
	
	public Trap(UUID owner, String name, Location location, boolean oneTimeTrigger){
		this.owner = owner;
		this.name = name;
		this.location = location;
		this.oneTimeTrigger = oneTimeTrigger;
	}
	
	public abstract void create();
	
	public void trigger(Player... targets){
		if (triggered)
			return;
		for (Player target : targets){
			
		}
		triggered = true;
	}
	public boolean isTriggered(){
		return this.triggered;
	}
	
	public void cooldown(){
		if (oneTimeTrigger)
			return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){
			@Override
			public  void run(){
				triggered = false;
			}
		}, cooldown);
	}
}
