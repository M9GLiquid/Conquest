package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.util.ColorManager;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Objective{
    private String name;
	private Kingdom owner;
	private Location location;
	private Location spawn;
	private UUID uniqueID;
    private boolean update;

    Objective(String name, Location location, Location spawn, String uniqueID) {
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
        update(true);
    }

    public static ArrayList<Objective> getObjectives(ActiveWorld world) {
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.addAll(Town.getTowns(world));
        objectives.addAll(Village.getVillages(world));
        return objectives;
    }

    public String getName() {
        return this.name;
    }

	public void setName(String name){
		this.name = name;
        update(true);
	}
	public Location getLocation(){
		return this.location;
	}

    public void setLocation(Location location) {
        this.location = location;
        update(true);
    }

    public ActiveWorld getWorld() {
        return ActiveWorld.getActiveWorld(location.getWorld());
	}
	public Location getSpawn(){
		return this.spawn;
	}

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        update(true);
	}
	public Kingdom getOwner() {
		return this.owner;
	}
	
	public void newUUID() {
		this.uniqueID = UUID.randomUUID();
	}
	public UUID getUUID() {
		return this.uniqueID;
	}

	/**
	 * Algorithm for Beacon Base
	 * @param rows - int
	 * @param loc - Location
	 * @param block - Material
	 * @return void
	 */
    public static void setBeaconBase(int rows, Location loc, Material block) {
        for (int x = 0; x < rows; x++) {
			loc.setX(loc.getX() + 1);
			for (int z = 0; z < rows; z++){
				loc.setZ(loc.getZ() + 1);
				loc.getWorld().getBlockAt(loc).setType(block);
			}
			loc.setZ(loc.getZ() - rows);
		}
	}

    public void setUUID(UUID ID) {
        uniqueID = ID;
    }

    public void setUUID(String ID) {
        if (ID.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
            uniqueID = UUID.fromString(ID);
        }
    }

    public void setBlock(Location loc, Material block) {
        Bukkit.getServer().getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(block);
        if (owner != null) {

            Bukkit.getServer().getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(ColorManager.intToMaterial(owner.getIntColor()));
            return;
        }
        Bukkit.getServer().getWorld(loc.getWorld().getName()).getBlockAt(loc).setType(Material.WHITE_STAINED_GLASS);
    }
	public abstract boolean create(Player player);

    public void setOwner(Kingdom owner) {
        this.owner = owner;
        update(true);
    }
	public abstract void updateGlass();

    public abstract void delete(Player player);

    public void update(boolean update) {
        this.update = update;
    }

    public boolean getUpdate() {
        return this.update;
	}
}
