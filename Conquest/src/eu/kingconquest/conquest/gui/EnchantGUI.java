package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;

public class EnchantGUI extends ChestGui{
	private ArrayList<Enchantment> targets= new ArrayList<Enchantment>();
	private ChestGui previous;
	private ItemStack item;
	private Player player;
	private int itemSlot;
	private Kit kit;
	
	public EnchantGUI(Player player, Kit kit, ItemStack item, int slot, ChestGui previousGui){
		super();
		previous = previousGui;
		this.player = player;
		this.itemSlot = slot;
		this.item = item;
		this.kit = kit;
		create();
	}


	@Override
	public void create(){
		for (Enchantment enchant : Enchantment.values())
			targets.add(enchant);
		
		createGui(player, "Enchantment GUI", targets.size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();
		
		playerInfo(player);
		previous(this);
		next(this);
		
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (getItems() -1) || getItems() == 0)
				break;
			EnchantmentButton(i, targets.get(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void EnchantmentButton(int slot, Enchantment enchant){
		ItemStack tempItem = item.clone();
		tempItem.addUnsafeEnchantment(enchant, 1);
		
		setItem(slot, tempItem, player -> {
			new EnchantLevelGUI(player, kit, item, itemSlot, enchant, this);
			close(player);
		}, "&6Set Enchant: &f" + enchant.getName(),
				"&cClick to Select!");
	}
}
