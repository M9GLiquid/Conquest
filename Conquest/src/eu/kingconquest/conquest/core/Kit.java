package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.util.Validate;

public class Kit{
	private HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	private UUID owner;
	private String name;
	private UUID uuid;
	private UUID world;
	private double cost;
	private long cooldown;
	
	public Kit(String name, World world, double cost, long cooldown, UUID owner){
		this.name = name;
		newUUID();
		this.world = world.getUID();
		this.cost = cost;
		this.cooldown = cooldown;
		this.owner = owner;
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
	
	public Objective getOwner(){
		if (Validate.notNull(Town.getTown(owner, getWorld())))
			return Town.getTown(owner, getWorld());
		if (Validate.notNull(Village.getVillage(owner, getWorld())))
			return Village.getVillage(owner, getWorld());
		return null;
	}
	public UUID getOwnerUUID(){
		return owner;
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
	
	public void addItem(int i, ItemStack temp){
		if (Validate.notNull(temp))
			if (!temp.getType().equals(Material.AIR.name())) // if item is air, return
				items.put(i, temp.clone());
	}
	
	public void addItems(int i, ItemStack[] temp){
		for (ItemStack item : temp){
			if (Validate.notNull(item)){
				if (!item.getType().equals(Material.AIR.name())) // if item is air, return
					items.put(i++, item.clone());
			}
		}
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
	public static Kit getKit(UUID uniqueID, World world){
		for (Kit kit : getKits(world))
			if (kit.getUUID().equals(uniqueID)
					&& kit.getWorld().equals(world))
				return kit;
		return null;
	}
	private static void addKit(Kit kit){
		kits.add(kit);
	}
	
	public static void clear(){
		kits.forEach(kit ->{
			kit.items.clear();
		});
		kits.clear();
	}
	
	private void newUUID(){
		this.uuid = UUID.randomUUID();
	}
}
