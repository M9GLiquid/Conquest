package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;

public class KitGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	
	public KitGUI(Player player, ChestGui previous){
		super();
		this.previous = previous;
		this.player = player;
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Kit Gui", Kit.getKits(player.getWorld()).size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();

		//Slot 0
		playerInfo(player);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 7
		createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int slot = 9; slot < 54; slot++) {
			if (getCurrentItem() > (getItems() -1) || getItems() == 0)
				break;
			editButton(slot, Kit.getKits(player.getWorld()).get(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
		
	}

	private void editButton(int slot, Kit kit){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			new KitEditGUI(player, this, kit);
		},"&1Kit: &f" + kit.getName(),
				"&aCost: " + kit.getCost()
				+ "\n&aCooldown: " + kit.getCooldown()
				);
	}

	private void createButton(){
		setItem(7, new ItemStack(Material.ENDER_PEARL), player -> {
			new KitCreateGUI(player, this);
		},"&3Create New Kit", 
				"");
	}

}
