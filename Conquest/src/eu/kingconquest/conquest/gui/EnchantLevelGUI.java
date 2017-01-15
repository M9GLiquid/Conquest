package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.util.ChestGui;

public class EnchantLevelGUI extends ChestGui{
	private Enchantment enchant;
	private ChestGui previous;
	private ItemStack item;
	private ItemStack tempItem;
	private Player player;
	private int level = 1;
	

	public EnchantLevelGUI(Player player, ItemStack item, Enchantment enchant, ChestGui previousGui){
		super();
		this.player = player;
		this.previous = previousGui;
		this.enchant = enchant;
		this.tempItem = item.clone();
		if (!tempItem.containsEnchantment(enchant)){
			level = tempItem.getEnchantmentLevel(enchant);
			tempItem.addEnchantment(enchant, level);
		}
		this.item = item;
		create();
	}

	@Override
	public void create(){
		toggleItemFlag();
		createGui(player, "Enchantment GUI", 9);
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();
		
		playerInfo(player);
		previous(this);
		next(this);

		if (level > 0)
			decreaseButton();
		saveButton();
		increaseButton();
		backButton(previous);
	}

	private void decreaseButton(){
		setItem(12, new ItemStack(Material.WOOD_BUTTON), player -> {
			tempItem.removeEnchantment(enchant);
			tempItem.addUnsafeEnchantment(enchant, level--);
			display();
		}, "&3<< Previous",
				"&cClick to Select!");
	}
	private void increaseButton(){
		setItem(14, new ItemStack(Material.STONE_BUTTON), player -> {
			tempItem.removeEnchantment(enchant);
			tempItem.addUnsafeEnchantment(enchant, level++);
			display();
		}, "&3 Next >>",
				"&cClick to Select!");
	}


	private void saveButton(){
		setItem(13, tempItem, player -> {
			item.removeEnchantment(enchant);
			item.addUnsafeEnchantment(enchant, level);
			new KitItemEditGui(player, item, new KitGUI(player, new HomeGUI(player)));
			close(player);
		}, "&aSave", "");
	}
}
