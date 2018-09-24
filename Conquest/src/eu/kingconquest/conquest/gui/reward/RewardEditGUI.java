package eu.kingconquest.conquest.gui.reward;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.gui.objective.ParentGUI;
import eu.kingconquest.conquest.gui.reward.item.ItemEditGUI;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Validate;

public class RewardEditGUI extends ChestGui{
	private ItemStack item = new ItemStack(Material.ARMOR_STAND);
	private ChestGui previous;
	private String name = "";
	private int cooldown;
	private Player player;
	private int cost;
	private Reward reward;

	public RewardEditGUI(Player player, ChestGui previous, Reward reward){
		super();
		this.previous = previous;
		this.player = player;
		this.reward = reward;
		
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
		parentButton(40);

		addButton(48);
		itemsIcon(49);
		itemsButton(50);
	}

	private void displayInfo(int slot){
		setItem(4, new ItemStack(Material.PAPER), player -> {
		},"&6Kit Information" , 
				"&7Name: &3" + reward.getName()
				+ "\n&7Parent: &3" + reward.getParent().getOwner().getColor() + reward.getParent().getName()
				+ "\n&7Cost: &3" + reward.getCost()
				+ "\n&7Cooldown: &3" + reward.getCooldown()
				+ "\n&7Items &3" + reward.getItems().size()
				);
	}

	private NamePrompt namePrompt = new NamePrompt(this);
	private void nameButton(int slot){
		if (Validate.notNull(namePrompt)){
			name = namePrompt.get();
			namePrompt = null;
			display();
		}
		
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			new ChatInteract(player, namePrompt, "Cancel");
			player.closeInventory();
			display();
	}, "&4Set Name!", 
				(name != "" ? "&7Name: &3" + name + "\n" : name)
				+  "&aClick to Select!"
				);
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
			reward.setCost(cost);
		},"&3Cost: &6" + reward.getCost() , 
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
		},"&3Decrease&6(&c " + amount +" &3minutes&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void cooldownDisplayButton(int slot){
		setItem(slot, new ItemStack(Material.WATCH), player -> {
			reward.setCooldown(cooldown);
		},"&3Cooldown: &6" + reward.getCooldown() + " &3minutes" , 
				"\n&aClick to Save"
				);
	}
	private void IncreaseCooldownButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.STONE_BUTTON), player -> {
			cooldown = cooldown + amount;
			display();
		},"&3Increase&6(&c+ " + amount +" &3minutes&6)" , 
				"&aClick to Increase!"
				);
	}
	

	private ParentGUI parentGui;
	private void parentButton(int slot){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			parentGui = new ParentGUI(player, null, this);
		},"&3Select Parent" , 
				(Validate.notNull(reward.getParent()) ? "&7Parent: &3" + reward.getParent().getName() + "\n" : "")
				+ "&aClick to Select!"
				);

		if (Validate.notNull(parentGui)){
			reward.setParent(parentGui.get());
			parentGui.close(player);
			parentGui = null;
			display();
		}
	}
	
	private void addButton(int slot){ //shift + click add item to Kit (Add to Slot and on press Save)
		if (getInventoryItems().containsKey(InventoryType.PLAYER))
			item = getInventoryItems().get(InventoryType.PLAYER).clone();
		else
			item  = new ItemStack(Material.ARMOR_STAND);
		
		setItem(slot, item, player -> {
			if (getClickType().equals(ClickType.LEFT)){
				reward.addItem(reward.getItems().size() +1, item);
			}else if (getClickType().equals(ClickType.RIGHT))
				new RewardItemsGUI(player, this, reward);
				display();
		}, "", 
				"\n&bClick item in your inventory, then..."
						+"\n&bLeft-Click this to &aSave &3item to Reward Box or..."
						+"\n&bRight-Click this to &dSee &3items selected");
	}
	private void itemsIcon(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
		}, "&r&7<< &5&lAdd Items |  Edit Items &7>>", "");
	}
	private void itemsButton(int slot){
		setItem(slot, new ItemStack(Material.ARMOR_STAND), player -> {
			new ItemEditGUI(player, reward, previous);
		}, "&3Edit Items",  
				"\n&bClick to Edit Items");
	}
}
