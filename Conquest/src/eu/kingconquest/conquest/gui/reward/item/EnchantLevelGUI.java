package eu.kingconquest.conquest.gui.reward.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.gui.HomeGUI;
import eu.kingconquest.conquest.gui.reward.RewardGUI;

public class EnchantLevelGUI extends ChestGui{
	private Enchantment enchant;
	private ItemStack tempItem;
	private ChestGui previous;
	private ItemStack item;
	private Player player;
	private int level = 1;
	private int itemSlot;
	private Reward kit;
	

	public EnchantLevelGUI(Player player, Reward kit, ItemStack item, int slot, Enchantment enchant, ChestGui previousGui){
		super();
		this.tempItem = item.clone();
		this.previous = previousGui;
		this.enchant = enchant;
		this.player = player;
		this.itemSlot = slot;
		this.kit = kit;
		
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
				"&bClick to Select!");
	}
	private void increaseButton(){
		setItem(14, new ItemStack(Material.STONE_BUTTON), player -> {
			tempItem.removeEnchantment(enchant);
			tempItem.addUnsafeEnchantment(enchant, level++);
			display();
		}, "&3 Next >>",
				"&bClick to Select!");
	}


	private void saveButton(){
		setItem(13, tempItem, player -> {
			item.removeEnchantment(enchant);
			item.addUnsafeEnchantment(enchant, level);
			new RewardItemEditGui(player, kit, item, itemSlot, new RewardGUI(player, new HomeGUI(player)));
			close(player);
		}, "&aSave", "");
	}
}
