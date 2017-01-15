package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

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
		setCurrentItem(0);
		for(int slot = 9; slot < 54; slot++) {
			if (getCurrentItem() > (kit.getItems().size()) || kit.getItems().size() < 1)
				break;
			editButton(slot, kit.getItem(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void addButton(){
		setItem(1, new ItemStack(Material.ARMOR_STAND), player -> {
			kit.addItem(kit.getItems().size(), player.getInventory().getItemInMainHand());
		}, "", "");
	}

	private void editButton(int slot, ItemStack item){
		if (Validate.isNull(item))
			return;
		String name = "&7" +  item.getType().toString();
		if (Validate.notNull(item))
			if (item.hasItemMeta())
				if (item.getItemMeta().hasDisplayName())
				name = item.getItemMeta().getDisplayName();
		setItem(slot, item, player -> {
			if (!item.getType().equals(Material.AIR))
				new KitItemEditGui(player, item, this);
		}, name,  
				"\n&3Click to Edit");
	}
}
