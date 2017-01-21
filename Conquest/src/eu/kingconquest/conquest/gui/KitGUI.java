package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

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
		setItem(slot, new ItemStack(Material.CHEST), player -> {
			if (getClickEvent().getClick().equals(ClickType.DOUBLE_CLICK)){
				Kit.removeKit(kit);
				previous.create();
				close(player);
			}else{
				new KitEditGUI(player, this, kit);
			}
		},"&6Kit Information" , 
				"&7Name: &3" + kit.getName()
				+ "\n&7Owner: &3" + (Validate.notNull(kit.getOwner()) ? kit.getOwner().getName(): "")
				+ "\n&7Cost: &3" + kit.getCost()
				+ "\n&7Cooldown: &3" + kit.getCooldown()
				+ "\n"
				+ "\n&4Warning! &3Double-Click to Remove"
				+"\n&3Click to Edit"
				);
	}

	private void createButton(){
		setItem(7, new ItemStack(Material.ENDER_CHEST), player -> {
			new KitCreateGUI(player, this);
		},"&3Create New Kit", 
				"");
	}

}
