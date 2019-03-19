package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.event.ObjectiveDeleteEvent;
import eu.kingconquest.conquest.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.IRON_BLOCK;

public class Village extends Objective{
	private DecimalFormat  format = new DecimalFormat("###.##");  
	private double progress = 0.0d;
	
	public Village(String name, Location loc, Location spawn, Kingdom owner, Kingdom preOwner, Town parent) {
		this(name
				, null
				, loc
				, spawn
				, owner
				, preOwner
				, parent);
	}
	public Village(String name, String uniqueID, Location loc, Location spawn, Kingdom owner, Kingdom preOwner, Town parent) {
		super(name, loc, spawn, uniqueID);
		if (Validate.notNull(owner))
			setOwner(owner);
		else
			setOwner(Kingdom.getNeutral(getWorld()));
		if (Validate.notNull(preOwner))
			setPreOwner(preOwner);
		else
			setPreOwner(Kingdom.getNeutral(getWorld()));
		
		if (Validate.notNull(parent))
			setParent(parent);
		
		addVillage(this);
		Marker.update(this);
		Marker.setDescription(this);
        update(true);
	}
	
	//Getters
	/**
	 * Get Village Capture Progress
	 * 
	 * @return double
	 */
	public double getProgress(){
		return Double.valueOf(format.format(progress).replaceAll(",", "."));
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

    public static ArrayList<Village> getVillages(ActiveWorld world) {
        ArrayList<Village> villages = new ArrayList<>();
        Village.getVillages().stream()
                .filter(village -> village.getWorld().equals(world))
                .forEach(villages::add);
        return villages;
    }

    public static ArrayList<Village> getVillages(String name, ActiveWorld world) {
        ArrayList<Village> villages = new ArrayList<>();
        for (Village village : getVillages())
            if (village.getName().equals(name)
                    && village.getWorld().equals(world))
                villages.add(village);
        return villages;
	}
	
	private Kingdom preOwner = null;
	public Kingdom getPreOwner(){
		if (Validate.isNull(preOwner))
			preOwner = Kingdom.getKingdom("Neutral", getWorld());
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
		if (getProgress() > 100.0d)
			this.progress = 100.0d;
	}

    public static Village getVillage(UUID ID, ActiveWorld world) {
        for (Village village : getVillages())
            if (village.getUUID().equals(ID)
                    && village.getWorld().equals(world))
                return village;
        return null;
	}

	private static ArrayList<Village> villages = new ArrayList<>();

    public void setParent(Town parent) {
        this.parent = parent;
        //If parent owner not same as this owner update parent to neutral
        if (Validate.notNull(parent))
            if (!parent.getOwner().equals(getOwner()))
                parent.setNeutral();
        update(true);
	}
	//boolean checks
	/**
	 * Outpost Agressors(Player)
	 */
	private HashMap<UUID, UUID> attackers = new HashMap<>();
	/**
	 * Outpost Defenders(Player)
	 */
	private HashMap<UUID, UUID> defenders = new HashMap<>();
	public HashMap<UUID, UUID> getAttackers(){
		return attackers;
	}

    public void removeParent() {
        this.parent.removeChild(this);
        this.parent = null;
        update(true);
	}
	public void removeAttacker(Player player){
		attackers.remove(player.getUniqueId());
	}
	public void clearAttackers(){
		attackers.clear();
	}

    /**
     * Set Village as Neutral
     *
     * @return void
     */
    public void setNeutral() {
        setOwner(Kingdom.getKingdom("Neutral", getWorld()));
        setProgress(0.0d);
        updateGlass();
        Marker.update(this);
        update(true);
    }

    public void setPreOwner(Kingdom kingdom) {
        update(true);
        this.preOwner = kingdom;
	}
	public HashMap<UUID, UUID> getDefenders(){
		return defenders;
    }

    public void addAttacker(Player player) {
        attackers.put(player.getUniqueId(), PlayerWrapper.getWrapper(player).getKingdom(ActiveWorld.getActiveWorld(player.getWorld())).getUUID());
	}
	public  void removeDefender(Player player){
		defenders.remove(player.getUniqueId());
	}
	public void clearDefender(){
		defenders.clear();
	}

	public boolean isNeutral() {
		return getOwner().isNeutral();
	}
	public static ArrayList<Village> getVillages(){
		return villages;
	}

	/**
	 * If Village has parent
	 *
	 * @return boolean
	 */
	public boolean hasParent() {
		return Validate.notNull(parent);
    }

    public void addDefender(Player player) {
        defenders.put(player.getUniqueId(), PlayerWrapper.getWrapper(player).getKingdom(ActiveWorld.getActiveWorld(player.getWorld())).getUUID());
	}

	public boolean isCapturing(Player player) {
		return attackers.containsKey(player.getUniqueId()) || defenders.containsKey(player.getUniqueId());
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
	public static void clear(){
		Village.getVillages().forEach(village->{
			village.attackers.clear();
			village.defenders.clear();
			village.parent = null;
			village.preOwner = null;			
		});
		villages.clear();		
	}
	
	/**
	 * Stop capturing!
	 * 
	 * @return void
	 */
	public void stop(){
		Bukkit.getServer().getScheduler().cancelTask(getTaskID());
		setTaskID(0);
	}

	@Override
	public boolean create(Player player){
		try{
			for (Objective objective : Objective.getObjectives(getWorld())){
				if (objective.equals(this))
					continue;
				if (Validate.isWithinCircle(player, objective.getLocation(), 20.0d, 20.0d, 20.0d)) {
					new Message(player, MessageType.CHAT, "{ToClose}");
					removeVillage(this);
					return false;
				}
			}
			if (getVillages(getName(), getWorld()).size() > 1) 
				new Message(player, MessageType.CHAT, "{AlreadyExists}");
			
			setOwner(Kingdom.getKingdom("Neutral", getWorld()));
			setPreOwner(Kingdom.getKingdom("Neutral", getWorld()));
			
			Location loc = player.getLocation().clone();
			int rows = 3;
			
			loc.setY(loc.getY() -3);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			
			//Set Iron Blocks! 3x3 area
			setBeaconBase(rows, loc, IRON_BLOCK);
			
			loc = player.getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			
			//Set Upper Blocks! 3x3 area
			setBeaconBase(rows, loc.clone(), Material.QUARTZ_SLAB);
			updateGlass();
			Bukkit.getPluginManager().callEvent(new ObjectiveCreateEvent(player, this));
			
			Cach.StaticVillage = this;
			Cach.StaticKingdom = this.getOwner();
			new Message(player, MessageType.CHAT, "{VillageCreated}");
            update(true);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
        }
    }

    @Override
    public void delete(Player player) {
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
			setBeaconBase(rows, loc, AIR);
			
			loc = getLocation().clone();
			loc.setY(loc.getY() - 1);
			loc.setX(loc.getX() - Math.ceil((rows / 2)) - 1);
			loc.setZ(loc.getZ() - Math.ceil((rows / 2)) - 1);
			
			//Set Upper Blocks! 3x3 area
			setBeaconBase(rows, loc, AIR);
			
			
			Bukkit.getPluginManager().callEvent(new ObjectiveDeleteEvent(player, this));
			new Message(player, MessageType.CHAT, "{VillageDeleted}");
			removeVillage(this);
			Marker.remove(this);
            update(true);
        } catch (Exception e) {
			e.printStackTrace();
        }
	}
	@Override
	public void updateGlass(){
		Location loc = getLocation().clone();
		for (int y = 0; y <= 1; y++){
			loc.setY(loc.getY() - 1);
			setBlock(loc, Material.WHITE_STAINED_GLASS);
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
