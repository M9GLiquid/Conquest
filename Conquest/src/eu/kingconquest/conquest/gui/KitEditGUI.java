package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;

public class KitEditGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
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
		createGui(player, "&6Kit Gui", kit.getItems().size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();

		//Slot 0
		playerInfo(player);
		//Slot 1
		addButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int slot = 9; slot < 54; slot++) {
			if (getCurrentItem() > (getItems() -1) || getItems() == 0)
				break;
			editButton(slot, kit.getItem(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void addButton(){
		setItem(1, new ItemStack(Material.ARMOR_STAND), player -> {
			kit.addItem(kit.getItems().size(), player.getInventory().getItemInMainHand());
		});
	}

	private void editButton(int slot, ItemStack item){
		ItemMeta meta = item.getItemMeta();
		setItem(slot, item, player -> {
			new KitItemEditGui(player, item);
		},"&dEdit:  &r" + meta.getDisplayName() , "&1-----------------"
				+ "\n&dLore:"
				+ meta.getLore());
	}
}
