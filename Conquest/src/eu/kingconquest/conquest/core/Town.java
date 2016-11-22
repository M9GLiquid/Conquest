package eu.kingconquest.conquest.core;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.BEACON;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.STAINED_GLASS;
import static org.bukkit.Material.STEP;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.core.util.Cach;
import eu.kingconquest.conquest.core.util.ChatManager;
import eu.kingconquest.conquest.core.util.Config;
import eu.kingconquest.conquest.core.util.Marker;
import eu.kingconquest.conquest.core.util.Validate;

public class Town extends Objective{
	public Town(String name, Location loc, Location spawn, ArrayList<Village> children){
		this(name, null, loc, spawn, null, children);
	}
	public Town(String name, Location loc, Location spawn, Kingdom owner, ArrayList<Village> children){
		this(name
				,null
				,loc
				,spawn
				,owner
				,children);
	}
	public Town(String name, String uniqueID, Location loc, Location spawn, Kingdom owner, ArrayList<Village> children){
		super(name, loc, spawn, uniqueID);
		
		if (!Validate.isNull(owner))
			setOwner(owner);
		else
			setOwner(Kingdom.getKingdom("Neutral"));
		
		if (!Validate.isNull(children))
			addChildren(children);

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
		return Children;
	}

//Setters
	/**
	 * Set Outpost as Neutral
	 * @return void
	 */
	public void setNeutral(){
			setOwner(Kingdom.getKingdom("Neutral"));
			updateGlass();
			Marker.update(this);
	}

	private ArrayList<Village> Children = new ArrayList<Village>();
	/**
	 * If Town has Children
	 * @return boolean
	 */
	public boolean hasChildren(){
		if (Children.size() != 0)
			return true;
		return false;
	}
	/**
	 * Add an Outpost bound to Town
	 * @param op - Outpost
	 * @return void
	 */
	public void addChild(Village village){
		Children.add(village);
	}
	/**
	 * Add an ArrayList of Outposts to bind to Town
	 * @param op - ArrayList<Outpost>
	 * @return void
	 */
	public void addChildren(ArrayList<Village> villages){
		Children = villages;
	}
	/**
	 * Remove Towns bound Outposts
	 * @param op - Outpost
	 * @return void
	 */
	public void removeChild(Village village){
		Children.remove(village);
	}


	private static ArrayList<Town> towns = new ArrayList<Town>();
	
	public static ArrayList<Town> getTowns() {
		return towns;
	}
	public static ArrayList<Town> getTowns(World world) {
		ArrayList<Town> towns = new ArrayList<Town>();
		Town.getTowns().stream()
			.filter(town->town.getLocation().getWorld().equals(world))
			.forEach(town->{
				towns.add(town);
			});
		return towns;
	}
	public static Town getTown(UUID ID) {
		for (Town town : getTowns())
			if (town.getUUID().equals(ID))
				return town;
		return null;
	}
	public static Town getTown(String name) {
		for (Town town : getTowns())
			if (town.getUUID().equals(name))
				return town;
		return null;
	}
	public static ArrayList<Town> getTowns(String name) {
		ArrayList<Town> townect= new ArrayList<Town>();
		for (Town town : getTowns())
			if (town.getName().equals(name))
				townect.add(town);
		return townect;
	}
	public static void addTown(Town town) {
		towns.add(town);
	}
	public static void addTowns(ArrayList<Town> ts) {
		towns.addAll(ts);
	}
	public static void removeTowns(ArrayList<Town> ts) {
		towns.removeAll(ts);
	}
	public static void removeTown(Town town) {
		towns.remove(town);
	}
	public static boolean hasTowns() {
		if (towns.size() != 0)
			return true;
		return false;
	}
	public static void clearTowns(){
		towns.clear();
	}
	
	@Override
	public boolean create(Player p){
		try{
			for (Town town : Town.getTowns(getLocation().getWorld())){
				if (Validate.isWithinArea(p.getLocation(), town.getLocation(), 20.0d, 20.0d, 20.0d)){
					ChatManager.Chat(p, Config.getChat("ToClose"));
					return false;
				}
			}
			for (Village village : Village.getVillages(getLocation().getWorld())){
				if (Validate.isWithinArea(p.getLocation(), village.getLocation(), 20.0d, 20.0d, 20.0d)){
					ChatManager.Chat(p, Config.getChat("ToClose"));
					return false;
				}
			}
			if (getTowns(getName()).size() == 0) {
				ChatManager.Chat(p, Config.getChat("AlreadyExists"));
				return false;
			}
			setOwner(Kingdom.getKingdom("Neutral"));

			Location loc = p.getLocation().clone();
			int rows = 5;

			loc.setY(loc.getY() - 3);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			//Set Iron Blocks! 5x5 area
			setBeaconBase(rows, loc.clone(), IRON_BLOCK, null);

			loc = p.getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Upper Blocks! 5x5 area
			setBeaconBase(rows, loc.clone(), STEP, 7);
			updateGlass();

			Cach.StaticTown = this;

			Config.saveTowns(loc.getWorld());
			ChatManager.Chat(p, Config.getChat("TownCreated"));
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean delete(){
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
			
			removeTown(this);
			Config.removeTowns(loc.getWorld());
			Marker.remove(this);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
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
}
