package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.util.ColorManager;
import eu.kingconquest.conquest.util.Validate;

public abstract class Objective{
	private String name = "";
	private Kingdom owner;
	private Location location;
	private Location spawn;
	private UUID uniqueID;
	Objective(String name, Location location, Location spawn, String uniqueID){
		this.name = name;
		this.location = location;
		if (Validate.isNull(this.spawn))
			this.spawn = location;
		else
			this.spawn = spawn;
		if (Validate.notNull(uniqueID))
			setUUID(uniqueID);
		else
			newUUID();
	}
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public void setLocation(Location location){
		this.location = location;
	}
	public Location getLocation(){
		return this.location;
	}
	public World getWorld(){
		return this.location.getWorld();
	}
	public void setSpawn(Location spawn){
		this.spawn = spawn;
	}
	public Location getSpawn(){
		return this.spawn;
	}
	public void setOwner(Kingdom owner) {
		this.owner = owner;
	}
	public Kingdom getOwner() {
		return this.owner;
	}
	public static ArrayList<Objective> getObjectives(World world){
		ArrayList<Objective> objectives = new ArrayList<Objective>();
		objectives.addAll(Town.getTowns(world));
		objectives.addAll(Village.getVillages(world));
		return objectives;
	}
	
	public void newUUID() {
		this.uniqueID = UUID.randomUUID();
	}
	public UUID getUUID() {
		return this.uniqueID;
	}
	public boolean setUUID(String ID){
		if (ID.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
			uniqueID = UUID.fromString(ID);
			return true;
		}
		return false;
	}
	public void setUUID(UUID ID){
			uniqueID = ID;
	}
	@SuppressWarnings("deprecation")
	public void setBlock(Location loc, Material block){
		Bukkit.getServer().getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(block);
		if (owner != null){
			
			Bukkit.getServer().getWorld(loc.getWorld().getName()).getBlockAt(loc).setData(ColorManager.intToByte(owner.getColor()));
			return;
		}
		Bukkit.getServer().getWorld(loc.getWorld().getName()).getBlockAt(loc).setData(ColorManager.intToByte(999));
	}
	/**
	 * Algorithm for Beacon Base
	 * @param p - Player Instance
	 * @param rows - int
	 * @param loc - Location
	 * @param block - Material
	 * @param data - Material Data
	 * @return void
	 */
	@SuppressWarnings("deprecation")
	public static void setBeaconBase(int rows, Location loc, Material block, Integer data){
		for (int x = 0; x < rows; x++){ 
			loc.setX(loc.getX() + 1);
			for (int z = 0; z < rows; z++){
				loc.setZ(loc.getZ() + 1);
				loc.getWorld().getBlockAt(loc).setType(block);
				if (data != null)
					loc.getWorld().getBlockAt(loc).setData((byte) (int)data);
			}
			loc.setZ(loc.getZ() - rows);
		}
	}
	public abstract boolean create(Player player);
	public abstract boolean delete(Player player);
	public abstract void updateGlass();

	private ArrayList<UUID> isCapturing = new ArrayList<UUID>();
	public void addCapturing(Player player){
		isCapturing.add(player.getUniqueId());
	}
	public boolean isCapturing(Player player){
		if (isCapturing.contains(player.getUniqueId()))
			return true;
		return false;
	}
	public boolean removeCapturing(Player player){
		if (isCapturing.remove(player.getUniqueId()))
			return true;
		return false;
	}
	public void clearCapturing(){
		isCapturing.clear();
	}
}
