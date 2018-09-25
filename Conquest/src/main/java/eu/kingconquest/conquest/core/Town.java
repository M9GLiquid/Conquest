package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.event.ObjectiveDeleteEvent;
import eu.kingconquest.conquest.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Material.*;

public class Town extends Objective{
	public Town(String name, Location loc, Location spawn, Kingdom owner){
		this(name
				,null
				,loc
				,spawn
				,owner);
	}
	public Town(String name, String uniqueID, Location loc, Location spawn, Kingdom owner){
		super(name, loc, spawn, uniqueID);
		if (Validate.notNull(owner))
			setOwner(owner);
		else
			setOwner(Kingdom.getNeutral(getWorld()));

		addTown(this);
		Marker.update(this);
		Marker.setDescription(this);
	}
	
	//Getters
	/**
	 * Get Arraylist of Child Outposts
	 * @return Arraylist<Outpost>
	 */
	public ArrayList<Village> getChildren(){
		return children;
	}
	
	//Setters
	/**
	 * Set Outpost as Neutral
	 * @return void
	 */
	public void setNeutral(){
		setOwner(Kingdom.getKingdom("Neutral", getWorld()));
		updateGlass();
		Marker.update(this);
	}

	private static ArrayList<Town> towns = new ArrayList<>();
	private ArrayList<Village> children = new ArrayList<>();

	public static ArrayList<Town> getTowns(World world) {
		ArrayList<Town> towns = new ArrayList<>();
		Town.getTowns().stream()
		.filter(town->town.getWorld().equals(world))
				.forEach(towns::add);
		return towns;
	}

	public static Town getTown(String name, World world) {
		for (Town town : getTowns())
			if (town.getUUID().equals(UUID.fromString(name))
					&& town.getWorld().equals(world))
				return town;
		return null;
	}

	public static ArrayList<Town> getTowns(String name, World world) {
		ArrayList<Town> townect = new ArrayList<>();
		for (Town town : getTowns())
			if (town.getName().equals(name)
					&& town.getWorld().equals(world))
				townect.add(town);
		return townect;
	}

	public static void addTowns(ArrayList<Town> tempTowns) {
		towns.addAll(tempTowns);
	}

	public static ArrayList<Town> getTowns() {
		return towns;
	}

	public static void removeTowns(ArrayList<Town> tempTowns) {
		towns.removeAll(tempTowns);
	}

	public static Town getTown(UUID ID, World world) {
		for (Town town : getTowns(world))
			if (town.getUUID().equals(ID)
					&& town.getWorld().equals(world))
				return town;
		return null;
	}

	public static boolean hasTowns() {
		return towns.size() != 0;
	}

	public static void clear() {
		Town.getTowns().forEach(town -> town.children.clear());
		towns.clear();
	}
	public static void addTown(Town town) {
		towns.add(town);
	}

	/**
	 * If Town has Children
	 *
	 * @return boolean
	 */
	public boolean hasChildren() {
		return children.size() != 0;
	}

	/**
	 * Add an Outpost bound to Town
	 *
	 * @param village - Village
	 * @return void
	 */
	public void addChild(Village village) {
		children.add(village);
	}
	public static void removeTown(Town town) {
		towns.remove(town);
	}

	/**
	 * Add an ArrayList of Outposts to bind to Town
	 *
	 * @param villages - ArrayList<Village>
	 * @return void
	 */
	public void addChildren(ArrayList<Village> villages) {
		children = villages;
	}

	/**
	 * Remove Towns bound Outposts
	 *
	 * @param village - Village
	 * @return void
	 */
	public void removeChild(Village village){
		children.remove(village);
	}
	
	@Override
	public boolean create(Player player){
		try{
			for (Objective objective : Objective.getObjectives(getWorld())){
				if (objective.equals(this))
					continue;
				if (Validate.isWithinArea(player.getLocation(), objective.getLocation(), 20.0d, 20.0d, 20.0d)){
					new Message(player, MessageType.CHAT, "{ToClose}");
					removeTown(this);
					return false;
				}
			}
			if (getTowns(getName(), getWorld()).size() > 1) 
				new Message(player, MessageType.CHAT, "{AlreadyExists}");
			
			setOwner(Kingdom.getKingdom("Neutral", getWorld()));
			
			Location loc = player.getLocation().clone();
			int rows = 5;
			
			loc.setY(loc.getY() - 3);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			//Set Iron Blocks! 5x5 area
			setBeaconBase(rows, loc.clone(), IRON_BLOCK);
			
			loc = player.getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Upper Blocks! 5x5 area
			setBeaconBase(rows, loc.clone(), Material.QUARTZ_SLAB);
			updateGlass();
			Bukkit.getPluginManager().callEvent(new ObjectiveCreateEvent(player, this));
			
			Cach.StaticTown = this;
			new Message(player, MessageType.CHAT, "{TownCreated}");
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean delete(Player player){
		try{
			Location loc = getLocation().clone();
			for (int y = 0; y <= 1; y++){
				loc.setY(loc.getY() - 1);
				setBlock(loc, AIR);
				loc.setX(loc.getX() - 1);
				setBlock(loc, AIR);
				loc.setX(loc.getX() + 1);
				loc.setZ(loc.getZ() - 1);
				setBlock(loc, AIR);
				loc.setZ(loc.getZ() + 1);
				loc.setZ(loc.getZ() + 1);
				setBlock(loc, AIR);
				loc.setZ(loc.getZ() - 1);
				loc.setX(loc.getX() + 1);
				setBlock(loc, AIR);
				loc.setX(loc.getX() - 1);
			}
			setBase(loc, AIR);
			
			int rows = 5;
			loc = getLocation().clone();
			
			loc.setY(loc.getY() - 4);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			
			//Set Iron Blocks! 5x5 area
			setBeaconBase(rows, loc, AIR);
			
			loc = getLocation().clone();
			
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			
			//Set Upper Blocks! 5x5 area
			setBeaconBase(rows, loc, AIR);
			
			Bukkit.getPluginManager().callEvent(new ObjectiveDeleteEvent(player, this));
			new Message(player, MessageType.CHAT, "{TownDeleted}");
			removeTown(this);
			Marker.remove(this);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void updateGlass() {
		Location loc = getLocation().clone();
		loc.setY(loc.getY() - 1);
		setBlock(loc, Material.WHITE_STAINED_GLASS);
		loc.setX(loc.getX() - 1);
		setBlock(loc, Material.WHITE_STAINED_GLASS);
		loc.setX(loc.getX() + 1);
		loc.setZ(loc.getZ() - 1);
		setBlock(loc, Material.WHITE_STAINED_GLASS);
		loc.setZ(loc.getZ() + 1);
		loc.setZ(loc.getZ() + 1);
		setBlock(loc, Material.WHITE_STAINED_GLASS);
		loc.setZ(loc.getZ() - 1);
		loc.setX(loc.getX() + 1);
		setBlock(loc, Material.WHITE_STAINED_GLASS);
		loc.setX(loc.getX() - 1);
		setBase(loc, BEACON);
	}
	private void setBase(Location loc, Material block){
		loc.setY(loc.getY() - 1);
		setBlock(loc, block);
		loc.setX(loc.getX() - 1);
		setBlock(loc, block);
		loc.setX(loc.getX() + 1);
		loc.setZ(loc.getZ() - 1);
		setBlock(loc, block);
		loc.setZ(loc.getZ() + 1);
		loc.setZ(loc.getZ() + 1);
		setBlock(loc, block);
		loc.setZ(loc.getZ() - 1);
		loc.setX(loc.getX() + 1);
		setBlock(loc, block);
		loc.setX(loc.getX() - 1);
	}
}
