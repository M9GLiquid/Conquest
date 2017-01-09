package eu.kingconquest.conquest.core;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.BEACON;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.STAINED_GLASS;
import static org.bukkit.Material.STEP;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.event.ObjectiveDeleteEvent;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Validate;

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
		Marker.create(this);
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

	private ArrayList<Village> children = new ArrayList<Village>();
	/**
	 * If Town has Children
	 * @return boolean
	 */
	public boolean hasChildren(){
		if (children.size() != 0)
			return true;
		return false;
	}
	/**
	 * Add an Outpost bound to Town
	 * @param op - Outpost
	 * @return void
	 */
	public void addChild(Village village){
		children.add(village);
	}
	/**
	 * Add an ArrayList of Outposts to bind to Town
	 * @param op - ArrayList<Outpost>
	 * @return void
	 */
	public void addChildren(ArrayList<Village> villages){
		children = villages;
	}
	/**
	 * Remove Towns bound Outposts
	 * @param op - Outpost
	 * @return void
	 */
	public void removeChild(Village village){
		children.remove(village);
	}


	private static ArrayList<Town> towns = new ArrayList<Town>();
	
	public static ArrayList<Town> getTowns() {
		return towns;
	}
	public static ArrayList<Town> getTowns(World world) {
		ArrayList<Town> towns = new ArrayList<Town>();
		Town.getTowns().stream()
			.filter(town->town.getWorld().equals(world))
			.forEach(town->{
				towns.add(town);
			});
		return towns;
	}
	public static Town getTown(UUID ID, World world) {
		for (Town town : getTowns(world))
			if (town.getUUID().equals(ID)
					&& town.getWorld().equals(world))
				return town;
		return null;
	}
	public static Town getTown(String name, World world) {
		for (Town town : getTowns())
			if (town.getUUID().equals(name)
					&& town.getWorld().equals(world))
				return town;
		return null;
	}
	public static ArrayList<Town> getTowns(String name, World world) {
		ArrayList<Town> townect= new ArrayList<Town>();
		for (Town town : getTowns())
			if (town.getName().equals(name)
					&& town.getWorld().equals(world))
				townect.add(town);
		return townect;
	}
	public static void addTown(Town town) {
		towns.add(town);
	}
	public static void addTowns(ArrayList<Town> towns) {
		towns.addAll(towns);
	}
	public static void removeTowns(ArrayList<Town> towns) {
		towns.removeAll(towns);
	}
	public static void removeTown(Town town) {
		towns.remove(town);
	}
	public static boolean hasTowns() {
		if (towns.size() != 0)
			return true;
		return false;
	}
	public static void clear(){
		Town.getTowns().forEach(town->{
			town.children.clear();
		});
		towns.clear();
	}
	
	@Override
	public boolean create(Player player){
		try{
			for (Objective objective : Objective.getObjectives(getWorld())){
				if (objective.equals(this))
					continue;
				if (Validate.isWithinArea(player.getLocation(), objective.getLocation(), 20.0d, 20.0d, 20.0d)){
					ChatManager.Chat(player, Config.getChat("ToClose"));
					return false;
				}
			}
			if (getTowns(getName(), getWorld()).size() > 1) 
				ChatManager.Chat(player, Config.getChat("AlreadyExists"));
			
			setOwner(Kingdom.getKingdom("Neutral", getWorld()));

			Location loc = player.getLocation().clone();
			int rows = 5;

			loc.setY(loc.getY() - 3);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			//Set Iron Blocks! 5x5 area
			setBeaconBase(rows, loc.clone(), IRON_BLOCK, null);

			loc = player.getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Upper Blocks! 5x5 area
			setBeaconBase(rows, loc.clone(), STEP, 7);
			updateGlass();
			Bukkit.getPluginManager().callEvent(new ObjectiveCreateEvent(player, this));
			
			Cach.StaticTown = this;
			ChatManager.Chat(player, Config.getChat("TownCreated"));
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
			setBeaconBase(rows, loc, AIR, null);

			loc = getLocation().clone();
			
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Upper Blocks! 5x5 area
			setBeaconBase(rows, loc, AIR, null);

			Bukkit.getPluginManager().callEvent(new ObjectiveDeleteEvent(player, this));
			ChatManager.Chat(player, Config.getChat("TownDeleted"));
			removeTown(this);
			Marker.remove(this);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void updateGlass(){
		Location loc = getLocation().clone();
			loc.setY(loc.getY() - 1);
			setBlock(loc, STAINED_GLASS);
			loc.setX(loc.getX() - 1);
			setBlock(loc, STAINED_GLASS);
			loc.setX(loc.getX() + 1);
			loc.setZ(loc.getZ() - 1);
			setBlock(loc, STAINED_GLASS);
			loc.setZ(loc.getZ() + 1);
			loc.setZ(loc.getZ() + 1);
			setBlock(loc, STAINED_GLASS);
			loc.setZ(loc.getZ() - 1);
			loc.setX(loc.getX() + 1);
			setBlock(loc, STAINED_GLASS);
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
