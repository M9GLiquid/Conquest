package eu.kingconquest.conquest.gui.reward.item;

import eu.kingconquest.conquest.core.ChestGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class ItemFlagGUI extends ChestGui{
    private ArrayList<ItemFlag> targets = new ArrayList<>();
	private ChestGui previous;
	private Player player;
	private ItemStack item;

	
	public ItemFlagGUI(Player player, ItemStack item, ChestGui previousGui){
		super();
		this.player = player;
		previous = previousGui;
		this.item = item;
		create();
	}

	@Override
	public void create(){
        Collections.addAll(targets, ItemFlag.values());
		toggleItemFlag();
		
		createGui(player, "Item Flag GUI", targets.size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();
		
		playerInfo(player);
		previous(this);
		next(this);
		
		backButton();
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (getItems() -1) || getItems() == 0)
				break;
			FlagButton(i, targets.get(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void backButton(){
        setItem(8, new ItemStack(Material.ARROW), player ->
                        previous.create(), "&c<< Back",
				"&cClick to go Back!");
	}

	private void FlagButton(int slot, ItemFlag flag){
		ItemStack tempItem = item.clone();
		tempItem.getItemMeta().addItemFlags(flag);
		
		setItem(slot, tempItem, player -> {
			item.getItemMeta().addItemFlags(flag);
			previous.create();
		}, "&6Set Flag: &f" + flag.name(),
				"&bClick to Select!");
	}
}
