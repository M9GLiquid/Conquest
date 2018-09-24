package eu.kingconquest.conquest.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public enum TrapType{
		NeutralMobs("NeutralTrap", "world",  0, 0, 0, 20, false);

	private boolean oneTimeUse;
	private int rangeActive;
	private String name;
	private World world;
	private double x;
	private double y;
	private double z;
	

	TrapType(String name, Location location, int rangeActive, boolean oneTimeUse){
		this(name, 
				location.getWorld().getName(), 
				location.getX(), 
				location.getY(), 
				location.getZ(), 
				rangeActive,
				oneTimeUse);
	}
	private TrapType(String name, String world, double x, double y, double z, int rangeActive, boolean oneTimeUse){
		this.world = Bukkit.getWorld(world);
		this.rangeActive = rangeActive;
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Returns the Name of the Trap
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * Returns the Range of which the Trap get activated
	 * @return
	 */
	public int getRangeActive(){
		return rangeActive;
	}
	public World getWorld(){
		return world;
	}
	public void setWorld(World world){
		this.world = world;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getZ(){
		return z;
	}
	public boolean isOneTimeUse(){
		return oneTimeUse;
	}
	public void setOneTimeUse(boolean oneTimeUse){
		this.oneTimeUse = oneTimeUse;
	}
}
