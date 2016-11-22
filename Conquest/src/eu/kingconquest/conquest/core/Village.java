package eu.kingconquest.conquest.core;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.STEP;

import java.util.ArrayList;
import java.util.HashMap;
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

public class Village extends Objective{
	public Village(String name, Location loc, Location spawn, Town parent) {
		this(name
				, null
				, spawn
				, spawn
				, null
				, parent);
	}
	public Village(String name, Location loc, Location spawn, Kingdom owner, Town parent) {
		this(name
				, null
				, loc
				, spawn
				, owner
				, parent);
	}
	public Village(String name, String uniqueID, Location loc, Location spawn, Kingdom owner, Town parent) {
		super(name, loc, spawn, uniqueID);
		if (!Validate.isNull(owner)){
			setOwner(owner);
		}else{
			setOwner(Kingdom.getKingdom("Neutral"));
			setPreOwner(Kingdom.getKingdom("Neutral"));
		}
		
		if (!Validate.isNull(parent))
			setParent(parent);
		
		addVillage(this);
		Marker.create(this);
		Marker.setDescription(this);
	}
	
//Getters
	/**
	 * Get Village Capture Progress
	 * 
	 * @return double
	 */
	private final static double SPEED = 1.5d;
	private double progress = 100.0d;
	public double getProgress(){
		return progress;
	}

	/**
	 * Get Village Parent
	 * 
	 * @return Objective
	 */
	private Town parent = null;
	public Town getParent(){
		return parent;
	}

	private Kingdom preOwner = null;
	public Kingdom getPreOwner(){
		return preOwner;
	}
	
//Setters
	/**
	 * Set Village Capture Progress
	 * 
	 * @param progress - double
	 * @return void
	 */
	public void setProgress(double progress){
		this.progress = progress;
	}

	/**
	 * Set Village Parent
	 * 
	 * @param parent - Objective
	 * @return void
	 */
	public void setParent(Town parent){
		this.parent = parent;
	}
	
	/**
	 * Set Village as Neutral
	 * @return void
	 */
	public void setNeutral(){
		setOwner(Kingdom.getKingdom("Neutral"));
		updateGlass();
		Marker.update(this);
	}
	
	public void setPreOwner(Kingdom kingdom){
		this.preOwner = kingdom;
	}
//boolean checks
	/**
	 * If Village has parent
	 * 
	 * @return boolean
	 */
	public boolean hasParent(){
		if (getParent() != null)
			return true;
		return false;

	}

	private ArrayList<UUID> isCapturing = new ArrayList<UUID>();
	public void setCapturing(Player player){
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
	
//Others
	public static double getCapSpeed(){
		return SPEED;
	}
	/**
	 * Outpost Agressors(Player)
	 */
	private HashMap<UUID, UUID> attackers = new HashMap<UUID, UUID>();
	public HashMap<UUID, UUID> getAttackers(){
		return attackers;
	}
	public void addAttacker(Player p){
		attackers.put(p.getUniqueId(), PlayerWrapper.getWrapper(p).getKingdom().getUUID());
	}
	public void removeAttacker(Player p){
		attackers.remove(p.getUniqueId());
	}

	/**
	 * Outpost Defenders(Player)
	 */
	private HashMap<UUID, UUID> defenders = new HashMap<UUID, UUID>();
	public HashMap<UUID, UUID> getDefenders(){
		return defenders;
	}
	public  void addDefender(Player p){
		defenders.put(p.getUniqueId(), PlayerWrapper.getWrapper(p).getKingdom().getUUID());
	}
	public  void removeDefender(Player p){
		defenders.remove(p.getUniqueId());
	}
	
	private static ArrayList<Village> villages = new ArrayList<Village>();
	public static ArrayList<Village> getVillages(){
		return villages;
	}
	public static ArrayList<Village> getVillages(World world){
		ArrayList<Village> villages = new ArrayList<Village>();
		Village.getVillages().stream()
			.filter(village->village.getLocation().getWorld().equals(world))
			.forEach(village->{
				villages.add(village);
			});
		return villages;
	}
	public static Village getVillage(UUID ID) {
		for (Village village : getVillages())
			if (village.getUUID().equals(ID))
				return village;
		return null;
	}
	public static ArrayList<Village> getVillages(String name){
		ArrayList<Village> villages= new ArrayList<Village>();
		for (Village village : getVillages())
			if (village.getName().equals(name))
				villages.add(village);
		return villages;
	}
	
	public static void addVillage(Village village) {
		villages.add(village);
	}
	public static void addVillages(ArrayList<Village> vs) {
		villages.addAll(vs);
	}
	public static void removeVillage(Village village) {
		villages.remove(village);
	}
	public static void removeVillages(ArrayList<Village> vs) {
		villages.removeAll(vs);
	}
	public static void clearVillages(){
		villages.clear();		
	}
	
	@Override
	public boolean create(Player p){
		try{
			System.out.println("Test 3");
			for (Town town : Town.getTowns(getLocation().getWorld())){
				if (Validate.isWithinArea(p.getLocation(), town.getLocation(), 20.0d, 20.0d, 20.0d)){
					ChatManager.Chat(p, Config.getChat("ToClose"));
					return false;
				}
			}
			System.out.println("Test 4");
			for (Village village : Village.getVillages(getLocation().getWorld())){
				if (Validate.isWithinArea(p.getLocation(), village.getLocation(), 20.0d, 20.0d, 20.0d)){
					ChatManager.Chat(p, Config.getChat("ToClose"));
					return false;
				}
			}
			System.out.println("Test 5");
			if (getVillages(getName()).size() == 0) {
					ChatManager.Chat(p, Config.getChat("AlreadyExists"));
					return false;
			}
			setOwner(Kingdom.getKingdom("Neutral"));

			Location loc = p.getLocation().clone();
			int rows = 3;

			loc.setY(loc.getY() -3);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Iron Blocks! 3x3 area
			setBeaconBase(rows, loc, IRON_BLOCK, null);

			loc = p.getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Upper Blocks! 3x3 area
			setBeaconBase(rows, loc.clone(), STEP, 7);
			updateGlass();

			Config.saveVillages(getLocation().getWorld());
			Cach.StaticVillage = this;
			ChatManager.Chat(p, Config.getChat("VillageCreated"));
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
			for (int y = 0; y <= 2; y++){
				loc.setY(loc.getY() - 1);
				setBlock(loc, AIR);
			}
			int rows = 3;
			loc = getLocation().clone();

			loc.setY(loc.getY() - 4);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Iron Blocks! 3x3 area
			setBeaconBase(rows, loc, AIR, null);

			loc = getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);

			//Set Upper Blocks! 3x3 area
			setBeaconBase(rows, loc, AIR, null);
			
			
			removeVillage(this);
			Config.removeTowns(loc.getWorld());
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
		for (int y = 0; y <= 1; y++){
			loc.setY(loc.getY() - 1);
			setBlock(loc, Material.STAINED_GLASS);
			if (y == 1)
				setBlock(loc, Material.BEACON);
		}
	}
	private int taskID = 0;
	public int getTaskID(){
		return taskID;
	}
	public void setTaskID(int taskID){
		this.taskID = taskID;
	}
	
}
