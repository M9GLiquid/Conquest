package eu.kingconquest.conquest.gui.reward.item;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ItemEditGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	private Reward reward;

	public ItemEditGUI(Player player, Reward reward, ChestGui previousGui){
		this.previous= previousGui;
		this.player= player;
		this.reward = reward;
		
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Item Edit Gui", reward.getItems().size());
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
			if (getCurrentItem() > (getItems() -1) || getItems() < 1)
				break;
			editButton(slot, reward.getItem(getCurrentItem()), getCurrentItem());
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
					reward.removeItem(itemSlot);
					previous.create();
					close(player);
				}else{
					new RewardItemEditGui(player, reward, item, itemSlot, this);
				}
			}
		}, name,  
				"\n&bDouble-Click to &4Remove (Cannot be undone)"
				+"\n&bClick to Edit");
	}
}
