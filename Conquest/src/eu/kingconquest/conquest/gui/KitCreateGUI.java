package eu.kingconquest.conquest.gui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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
	private Double cost = 0.00;
	private long cooldown = 0;
	private String name = "";
	private Objective parent;
	DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
	DecimalFormat df;
	
	public KitCreateGUI(Player player, ChestGui previous){
		super();
		symbols.setDecimalSeparator('.');
		df = new DecimalFormat("#.##", symbols);
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
		costDisplayButton();
		costAddReminderButton();
		costAddButton();
		costSubstractButton();
		costSubstractReminderButton();
		
		//Cooldown
		cooldownAddButton();
		cooldownDisplayButton();
		cooldownSubstractButton();
		
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

	private void costSubstractButton(){
		setItem(20, new ItemStack(Material.WOOD_BUTTON), player -> {
			cost = Double.valueOf(df.format(cost-1));
			display();
		},"&3Decrease&6(&c- 1.0&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void costSubstractReminderButton(){
		setItem(21, new ItemStack(Material.WOOD_BUTTON), player -> {
			cost = Double.valueOf(df.format(cost-0.1));
			display();
		},"&3Decrease&6(&c- 0.1&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void costDisplayButton(){
		setItem(22, new ItemStack(Material.GOLD_NUGGET), player -> {
		},"&3Cost: &6" + cost , ""
				);
	}
	private void costAddReminderButton(){
		setItem(23, new ItemStack(Material.STONE_BUTTON), player -> {
			cost = Double.valueOf(df.format(cost+0.1));
			display();
		},"&3Increase&6(&a+ 0.1&6)" , 
				"&aClick to Increase!"
				);
	}
	private void costAddButton(){
		setItem(24, new ItemStack(Material.STONE_BUTTON), player -> {
			cost = Double.valueOf(df.format(cost+1));
			display();
		},"&3Increase&6(&a+ 1.0&6)" , 
				"&aClick to Increase!"
				);
	}
	
	private void cooldownSubstractButton(){
		setItem(30, new ItemStack(Material.WOOD_BUTTON), player -> {
			cooldown--;
			display();
		},"&3Decrease&6(&c- 1&6)" , 
				"&cClick to Decrease!"
				);
	}
	private void cooldownDisplayButton(){
		setItem(31, new ItemStack(Material.WATCH), player -> {
		},"&3Cooldown: &6" + cooldown , ""
				);
	}
	private void cooldownAddButton(){
		setItem(32, new ItemStack(Material.STONE_BUTTON), player -> {
			cooldown++;
			display();
		},"&3Increase&6(&a+ 1&6)" , 
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
