package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class ItemEditGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	private Kit kit;

	public ItemEditGUI(Player player, Kit kit, ChestGui previousGui){
		this.previous= previousGui;
		this.player= player;
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
		clearSlots();

		//Slot 0
		playerInfo(player);
		//Slot 1
		//addButton();
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
			editButton(slot, kit.getItem(getCurrentItem()), getCurrentItem());
			setCurrentItem(getCurrentItem() + 1);
		}
	}
	
	private void editButton(int slot, ItemStack item, int itemSlot){
		if (Validate.isNull(item))
			return;
		String name = "&7" +  item.getType().toString();
		if (Validate.notNull(item))
			if (item.hasItemMeta())
				if (item.getItemMeta().hasDisplayName())
				name = item.getItemMeta().getDisplayName();
		setItem(slot, item, player -> {
			if (!item.getType().equals(Material.AIR)){
				if (getClickType().equals(ClickType.DOUBLE_CLICK)){
					kit.removeItem(itemSlot);
					previous.create();
					close(player);
				}else{
					new KitItemEditGui(player, kit, item, itemSlot, this);
				}
			}
		}, name,  
				"\n&4Warning! &3Double-Click to Remove"
				+"\n&3Click to Edit");
	}

}
