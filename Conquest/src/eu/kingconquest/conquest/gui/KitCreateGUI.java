package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class KitCreateGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	private long cost = 0;
	private long cooldown = 0;
	private String name = "";
	private Objective parent;
	
	public KitCreateGUI(Player player, ChestGui previous){
		super();
		this.player = player;
		this.previous = previous;
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Kit Create Gui", 36);
		display();
	}

	@Override
	public void display(){
		init();
		//Slot 0
		playerInfo(player);
		//Slot 3
		next(this);
		//Slot 4
		displayInfo();
		//Slot 5
		previous(this);
		
		//Slot 8
		backButton(previous);
		
		//Name
		nameButton();
		
		//Cost
		DecreaseCostButton(19, -100);
		DecreaseCostButton(20, -10);
		DecreaseCostButton(21, -1);
		costDisplayButton(22);
		IncreaseCostButton(23, 1);
		IncreaseCostButton(24, 10);
		IncreaseCostButton(25, 100);
		
		//Cooldown
		DecreaseCooldownButton(28, -100);
		DecreaseCooldownButton(29, -10);
		DecreaseCooldownButton(30, -1);
		cooldownDisplayButton(31);
		IncreaseCooldownButton(32, 1);
		IncreaseCooldownButton(33, 10);
		IncreaseCooldownButton(34, 100);
		
		//Owner
		parentButton();
		//Save
		saveButton();
		
		if (cost > 0. 
				&& cooldown > 0 
				&& name != "" 
				&& Validate.notNull(parent))
			saveButton();
		else
			backButton(previous);
	}

	AlphabetGUI alphabetGUI;
	private void nameButton(){
		setItem(13, new ItemStack(Material.BOOK), player -> {
			alphabetGUI = new AlphabetGUI(player, this, "");
		},"&3Set Name" , 

				(name != "" ? "&7Name: &3" + name + "\n" : name)
				 +  "&aClick to Select!"
				);
	}
	private void setName(){
		if (Validate.notNull(alphabetGUI)){
			name = alphabetGUI.get();
			alphabetGUI.close(player);
		}
	}

	private void DecreaseCooldownButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.WOOD_BUTTON), player -> {
			cooldown = cooldown - amount;
			if (cost < 0)
				cost = 0;
			display();
		},"&3Decrease&6(&c- " + amount +"&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void cooldownDisplayButton(int slot){
		setItem(slot, new ItemStack(Material.WATCH), player -> {
		},"&3Cooldown: &6" + cooldown , ""
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

	private void DecreaseCostButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.WOOD_BUTTON), player -> {
			if ((cost - amount) >= 0)
				cost = cost - amount;
			else
				cost = 0;
			display();
		},"&3Decrease&6(&c- " + amount +"&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void costDisplayButton(int slot){
		setItem(slot, new ItemStack(Material.GOLD_NUGGET), player -> {
		},"&3Cost: &6" + cost , ""
				);
	}
	private void IncreaseCostButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.STONE_BUTTON), player -> {
			cost = cost + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
	
	private ParentGUI parentGui;
	private void parentButton(){
		setItem(40, new ItemStack(Material.BEACON), player -> {
			parentGui = new ParentGUI(player, null, this);
		},"&3Select Parent" , 
				(Validate.notNull(parent) ? "&7Parent: &3" + parent.getName() + "\n" : "")
				+ "&aClick to Select!"
				);
	}
	private void setParent(){
		if (Validate.notNull(parentGui)){
			parent = parentGui.get();
			parentGui.close(player);
			parentGui = null;
		}
	}
	
	private void init(){
		clearSlots();
		setCurrentItem(0);
		setName();
		setParent();
	}

	private void displayInfo(){
		setItem(4, new ItemStack(Material.PAPER), player -> {
		},"&6Kit Information" , 
				"&7Name: &3" + name
				+ "\n&7Owner: &3" + (Validate.notNull(parent) ? parent.getName(): "")
				+ "\n&7Cost: &3" + cost
				+ "\n&7Cooldown: &3" + cooldown
				);
	}
	
	private void saveButton(){
		setItem(8, new ItemStack(Material.EMERALD_BLOCK), player -> {
			Kit kit = new Kit(name, player.getWorld(), cost, cooldown, parent.getUUID());
			kit.addItem(-1, player.getInventory().getItemInMainHand());
			kit.addItems(0, player.getInventory().getStorageContents());
			new KitGUI(player, null);
			close(player);
		},"&aSave" , "");
	}

}
