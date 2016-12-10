package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.util.Validate;

public class Kit{
	private HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	private UUID objective;
	private String name;
	private UUID uuid;
	private UUID world;
	private double cost;
	private long cooldown;
	
	public Kit(String name, World world, double cost, long cooldown, Objective objective){
		this.name = name;
		newUUID();
		this.world = world.getUID();
		this.cost = cost;
		this.cooldown = cooldown;
		this.objective = objective.getUUID();
		addKit(this);
	}

	public String getName(){
		return name;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	
	public World getWorld(){
		return Bukkit.getWorld(world);
	}
	
	public double getCost(){
		return cost;
	}
	
	public Objective getObjective(){
		if (Validate.notNull(Town.getTown(objective, getWorld())))
			return Town.getTown(objective, getWorld());
		if (Validate.notNull(Village.getVillage(objective, getWorld())))
			return Village.getVillage(objective, getWorld());
		return null;
	}
	
	public double getCooldown(){
		return cooldown;
	}
	
	public void setCooldown(long cooldown){
		this.cooldown = cooldown;
	}
	
	public void setCost(double cost){
		this.cost = cost;
	}
	
	public HashMap<Integer, ItemStack> getItems(){
		return items;
	}
	
	public ItemStack getItem(int i){
		return items.get(i);
	}
	
	public void addItem(int i, ItemStack item){
		items.put(i, item);
	}
	
	public void addItems(int i, ItemStack[] items){
		for (ItemStack item : items)
			this.items.put(i++, item);
	}
	
	private static ArrayList<Kit> kits = new ArrayList<Kit>();
	public static ArrayList<Kit> getKits(){
		return kits;
	}
	public static ArrayList<Kit> getKits(World world){
		ArrayList<Kit> kits = new ArrayList<Kit>();
		Kit.getKits().stream()
			.filter(kit->kit.getWorld().equals(world))
			.forEach(kit->{
				kits.add(kit);
			});
		return kits;
	}
	public static Kit getKit(UUID uuid, World world){
		for (Kit kit : getKits(world))
			if (kit.getUUID().equals(uuid)
					&& kit.getWorld().equals(world))
				return kit;
		return null;
	}
	private static void addKit(Kit kit){
		kits.add(kit);
	}
	
	private void newUUID(){
		this.uuid = UUID.randomUUID();
	}
}
