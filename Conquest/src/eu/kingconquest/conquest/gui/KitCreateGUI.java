package eu.kingconquest.conquest.gui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class KitCreateGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	private boolean costToggle = false;
	private Double cost = 0.00;
	private long cooldown = 0;
	private String name = "";
	private Objective owner;
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
		createGui(player, "&6Kit Create Gui", (Town.getTowns(player.getWorld()).size() + 8));
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
		
		//Slot 9
		costToggleButton();
		if (costToggle)
			incrementReminderButton(); //Slot 10
		incrementButton(); // Slot 11
		decrementButton(); //Slot 12
		if (costToggle)
			decrementReminderButton(); //Slot 13

		nameButton(); //Slot 14
		if (cost > 0. 
				&& cooldown > 0 
				&& name != "" 
				&& Validate.notNull(owner))
			saveButton();
		else
			backButton(previous);
		
		//Slot MAIN
		for(int slot = 18; slot < 54; slot++) {
			if (getCurrentItem() >= (Town.getTowns(player.getWorld()).size() -1) || getItems() == 0)
				break;
			ownerButton(slot, Town.getTowns(player.getWorld()).get(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void init(){
		clearSlots();
		setCurrentItem(0);
		setName();
	}

	AlphabetGUI alphabetGUI;
	private void nameButton(){
		setItem(9, new ItemStack(Material.BOOK), player -> {
			alphabetGUI = new AlphabetGUI(player, this, null);
			alphabetGUI.create();
		},"&6Set Name of Kit" , "&1-----------------"
				);
	}

	private String ownerName = "";
	private void displayInfo(){
		if (Validate.notNull(owner))
			ownerName = owner.getName();
		setItem(4, new ItemStack(Material.PAPER), player -> {
		},"&6Kit Information" , "&1-----------------"
				+ "\n&7Name: &3" + name
				+ "\n&7Owner: &3" + ownerName
				+ "\n&7Cost: &3" + cost
				+ "\n&7Cooldown: &3" + cooldown
				);
	}

	private void ownerButton(int slot, Town town){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			owner = town;
			display();
		},"&6Select &7" + town.getName() +" &6as Owner of Kit" , "&1-----------------"
				+ "\n&7Name: " + town.getName()
				+ "\n&7Children: &7(" + town.getChildren().size() + ")"
				);
	}

	private void costToggleButton(){
		setItem(10, new ItemStack(Material.LEVER), player -> {
			costToggle = !costToggle;
			display();
		},"&3Toggle" , "&1-----------------"
				+ "\n&7Cost Modifier Active: &3" + costToggle
				+ "\n&7Cooldown Modifier Active: &3" + !costToggle
				);
	}

	private void incrementReminderButton(){
		setItem(12, new ItemStack(Material.ENCHANTED_BOOK), player -> {
			cost = Double.valueOf(df.format(cost + 0.1));
			display();
		},"&cReminder Incrementer" , "&1-----------------"
				+ "\n&7 Add one to the Reminder"
				);
	}

	private void incrementButton(){
		setItem(11, new ItemStack(Material.ENCHANTED_BOOK), player -> {
			if (costToggle)
				cost++;
			else
				cooldown++;
			display();
		},"&cMain Incrementer" , "&1-----------------"
				+ "\n&7 Add one to the Main"
				);
	}

	private void decrementButton(){
		int slot = 12;
		if (costToggle)
			slot = 13;
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			if (costToggle)
				cost--;
			else
				cooldown--;
			display();
		},"&cMain Decrementer" , "&1-----------------"
				);
	}

	private void decrementReminderButton(){
		setItem(14, new ItemStack(Material.BOOK), player -> {
			cost = Double.valueOf(df.format(cost - 0.1));
			display();
		},"&cReminder Decrementer" , "&1-----------------"
				);
	}

	protected void setName(){
		if (Validate.notNull(alphabetGUI)){
			name = alphabetGUI.getWord();
			alphabetGUI.close(player);
		}
	}

	private void saveButton(){
		ItemStack[] items = player.getInventory().getStorageContents();
		setItem(8, new ItemStack(Material.EMERALD_BLOCK), player -> {
			Kit kit = new Kit(name, player.getWorld(), cost, cooldown, owner);
			kit.addItem(-1, player.getInventory().getItemInMainHand());
			kit.addItems(0, items);
			new KitGUI(player, null);
		},"&aSave" , "&1-----------------"
				);
	}

}
