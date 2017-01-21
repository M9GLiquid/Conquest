package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;

public class KitEditGUI extends ChestGui{
	private ChestGui previous;
	private int cooldown;
	private Player player;
	private int cost;
	private Kit kit;

	public KitEditGUI(Player player, ChestGui previous, Kit kit){
		super();
		this.previous = previous;
		this.player = player;
		this.kit = kit;
		
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Kit Gui", 54);
		display();
	}

	@Override
	public void display(){
		clearSlots();

		//Slot 0
		playerInfo(player);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);

		DecreaseCostButton(10, -100);
		DecreaseCostButton(11, -10);
		DecreaseCostButton(12, -1);
		costDisplayButton(13);
		IncreaseCostButton(14, 1);
		IncreaseCostButton(15, 10);
		IncreaseCostButton(16, 100);

		DecreaseCooldownButton(19, -100);
		DecreaseCooldownButton(20, -10);
		DecreaseCooldownButton(21, -1);
		cooldownDisplayButton(22);
		IncreaseCooldownButton(23, 1);
		IncreaseCooldownButton(24, 10);
		IncreaseCooldownButton(25, 100);
		
		itemsButton(40);
		addButton(49);
	}

	private void DecreaseCostButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.WOOD_BUTTON, amount), player -> {
			cost = cost - amount;
			display();
		},"&3Decrease&6(&c- " + amount +"&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void costDisplayButton(int slot){
		setItem(slot, new ItemStack(Material.GOLD_NUGGET), player -> {
			kit.setCost(cost);
		},"&3Cost: &6" + kit.getCost() , 
				"\n&aClick to Save"
				);
	}
	private void IncreaseCostButton(int slot, int amount){
		setItem(slot, new ItemStack(Material.STONE_BUTTON, amount), player -> {
			cost = cost + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}

	private void DecreaseCooldownButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.WOOD_BUTTON,  amount), player -> {
				cooldown = cooldown - amount;
			display();
		},"&3Decrease&6(&c- " + amount +"&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void cooldownDisplayButton(int slot){
		setItem(slot, new ItemStack(Material.WATCH), player -> {
			kit.setCooldown(cooldown);
		},"&3Cooldown: &6" + kit.getCooldown() , 
				"\n&aClick to Save"
				);
	}
	private void IncreaseCooldownButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.STONE_BUTTON,  amount), player -> {
			cooldown = cooldown + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
	
	private void addButton(int slot){ //shift + click add item to Kit (Add to Slot and on press Save)
		setItem(slot, player.getItemOnCursor(), player -> {
			if (getClickEvent().getClickedInventory().equals(player.getInventory())){
				kit.addItem(kit.getItems().size(), getClickEvent().getCurrentItem());
			}
			kit.addItem(kit.getItems().size(), player.getInventory().getItemInMainHand());
		}, "", 
				"\n&3Ctrl+Click to &aSelect Item &3in your inventory"
				+"\n&3Click to &aSave");
	}

	private void itemsButton(int slot){
		setItem(slot, new ItemStack(Material.ARMOR_STAND), player -> {
			new ItemEditGUI(player, kit, previous);
		}, "&3Edit Items",  
				"\n&3Click to Edit Items");
	}
}
