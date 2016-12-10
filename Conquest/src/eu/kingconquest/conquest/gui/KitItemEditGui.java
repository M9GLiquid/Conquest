package eu.kingconquest.conquest.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.util.ChestGui;

public class KitItemEditGui extends ChestGui{
	private Player player;
	@SuppressWarnings("unused")
	private ItemStack item;
	
	public KitItemEditGui(Player player, ItemStack item){
		super();
		this.player = player;
		this.item = item;
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Item Edit Gui", 8);
		display();
	}

	@Override
	public void display(){
		clearSlots();
		//Slot 0
		playerInfo(player);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		
		//Slot MAIN
	}
}
