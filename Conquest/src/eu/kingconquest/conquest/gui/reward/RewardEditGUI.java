package eu.kingconquest.conquest.gui.reward;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.gui.reward.item.ItemEditGUI;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Validate;

public class RewardEditGUI extends ChestGui{
	private ChestGui previous;
	private String name = "";
	private int itemSize = 0;
	private int cooldown;
	private Player player;
	private int cost;
	private Reward kit;

	public RewardEditGUI(Player player, ChestGui previous, Reward kit){
		super();
		this.previous = previous;
		this.player = player;
		this.kit = kit;
		
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Reward Gui", 54);
		display();
	}

	@Override
	public void display(){
		clearSlots();
		setCurrentItem(0);
		//Slot 0
		playerInfo(player);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);
		
		displayInfo(4);

		nameButton(13);

		DecreaseCostButton(19, -100);
		DecreaseCostButton(20, -10);
		DecreaseCostButton(21, -1);
		costDisplayButton(22);
		IncreaseCostButton(23, 1);
		IncreaseCostButton(24, 10);
		IncreaseCostButton(25, 100);

		DecreaseCooldownButton(28, -100);
		DecreaseCooldownButton(29, -10);
		DecreaseCooldownButton(30, -1);
		cooldownDisplayButton(31);
		IncreaseCooldownButton(32, 1);
		IncreaseCooldownButton(33, 10);
		IncreaseCooldownButton(34, 100);
		
		itemsButton(40);
		addButton(49);
	}

	private void displayInfo(int slot){
		setItem(4, new ItemStack(Material.PAPER), player -> {
		},"&6Kit Information" , 
				"&7Name: &3" + kit.getName()
				+ "\n&7Owner: &3" + kit.getOwner().getName()
				+ "\n&7Cost: &3" + kit.getCost()
				+ "\n&7Cooldown: &3" + kit.getCooldown()
				+ "\n&7Items &3" + kit.getItems().size()
				);
	}

	private NamePrompt namePrompt;
	private void nameButton(int slot){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			new ChatInteract(player, namePrompt = new NamePrompt(this), "Cancel");
			player.closeInventory();
			display();
	}, "&4Set Name!", 
				(name != "" ? "&7Name: &3" + name + "\n" : name)
				+  "&aClick to Select!"
				);

		if (Validate.notNull(namePrompt)){
			name = namePrompt.get();
			namePrompt = null;
		}
	}
	
	private void DecreaseCostButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.WOOD_BUTTON), player -> {
			cost = cost + amount; // (+- = -)
			display();
		},"&3Decrease&6(&c " + amount +"&6)" , 
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
		setItem(slot, new ItemStack(Material.STONE_BUTTON), player -> {
			cost = cost + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}

	private void DecreaseCooldownButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.WOOD_BUTTON), player -> {
				cooldown = cooldown + amount; // (+- = -)
			display();
		},"&3Decrease&6(&c " + amount +"&6)" , 
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
		setItem(slot,  new ItemStack(Material.STONE_BUTTON), player -> {
			cooldown = cooldown + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
	
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	private ItemStack item = new ItemStack(Material.ARMOR_STAND);
	private void addButton(int slot){ //shift + click add item to Kit (Add to Slot and on press Save)
		if (getInventoryItems().containsKey(InventoryType.PLAYER))
			item = getInventoryItems().get(InventoryType.PLAYER);
		else
			item  = new ItemStack(Material.ARMOR_STAND);
		
		setItem(slot, item, player -> {
				item.setAmount(itemSize);
				items.add(item);
				display();
		}, "", 
				"\n&bClick item in your inventory to add, then..."
				+"\n&bClick to &aSave &3item to kit");
	}

	private void itemsButton(int slot){
		setItem(slot, new ItemStack(Material.ARMOR_STAND), player -> {
			new ItemEditGUI(player, kit, previous);
		}, "&3Edit Items",  
				"\n&bClick to Edit Items");
	}
}
