package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Reward{
	private static ArrayList<Reward> rewards = new ArrayList<>();
	private long cooldown;
	private UUID parent;
	private String name;
	private UUID world;
	private UUID uuid;
	private long cost;
	
	public Reward(String name, World world, long cost, long cooldown, UUID parent){
		this(name,world,cost, cooldown,parent,null);
	}
	public Reward(String name, World world, long cost, long cooldown, UUID parent, UUID uuid){
		this.name = name;
		if(Validate.notNull(uuid))
			this.uuid = uuid;
		else
			newUUID();
		this.world = world.getUID();
		this.cost = cost;
		this.cooldown = cooldown;
		this.parent = parent;
		addReward(this);
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
	
	public Long getCost(){
		return cost;
	}
	
	public Objective getParent(){
		if (Validate.notNull(Town.getTown(parent, getWorld())))
			return Town.getTown(parent, getWorld());
		if (Validate.notNull(Village.getVillage(parent, getWorld())))
			return Village.getVillage(parent, getWorld());
		return null;
	}
	
	public void setParent(Objective parent){
		this.parent = parent.getUUID();
	}
	
	public Long getCooldown(){
		return cooldown;
	}
	
	public void setCooldown(long cooldown){
		this.cooldown = cooldown;
	}
	
	public void setCost(long cost){
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
			items.put(i, temp.clone());
	}
	
	public void addItems(int i, ArrayList<ItemStack> temp){
		for (ItemStack item : temp){
			if (Validate.notNull(item)){
				items.put(i++, item.clone());
			}
		}
	}
	
	public void removeItem(int slot){
		items.replace(slot, new ItemStack(Material.AIR));
	}
	public void removeItems(int... slots){
		for (int slot : slots)
			items.replace(slot, new ItemStack(Material.AIR));
	}

	private HashMap<Integer, ItemStack> items = new HashMap<>();
	public static ArrayList<Reward> getRewards(){
		return rewards;
	}

	public static ArrayList<Reward> getRewards(World world){
		ArrayList<Reward> rewards = new ArrayList<>();
		Reward.getRewards().stream()
		.filter(reward->reward.getWorld().equals(world))
				.forEach(rewards::add);
		return rewards;
	}
	public static Reward getReward(UUID uniqueID, World world){
		for (Reward reward : getRewards(world))
			if (reward.getUUID().equals(uniqueID)
					&& reward.getWorld().equals(world))
				return reward;
		return null;
	}
	private static void addReward(Reward reward){
		rewards.add(reward);
	}
	public static void removeReward(Reward reward){
		rewards.remove(reward);
	}
	
	public static void clear(){
		rewards.forEach(reward -> reward.items.clear());
		rewards.clear();
	}
	
	private void newUUID(){
		this.uuid = UUID.randomUUID();
	}
}
