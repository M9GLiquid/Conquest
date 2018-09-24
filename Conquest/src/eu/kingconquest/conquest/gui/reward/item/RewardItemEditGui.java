package eu.kingconquest.conquest.gui.reward.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.kingconquest.conquest.chatinteract.LorePrompt;
import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Validate;

public class RewardItemEditGui extends ChestGui{
	private ChestGui previous;
	private ItemStack tempItem;
	private ItemStack item;
	private Player player;
	private int itemSlot;
	private Reward kit;
	private int itemSize = 1;

	public RewardItemEditGui(Player player, Reward kit, ItemStack item, int slot, ChestGui previous){
		super();
		this.tempItem = item.clone();
		this.previous = previous;
		this.player = player;
		this.itemSlot = slot;
		this.item = item;
		this.kit = kit;
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Item Edit Gui", 45);
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();
		//Slot 0
		playerInfo(player);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);

		//Slot 8
		backButton(previous);

		nameButton(13);
		loreButton(22);
		enchantButton(31);
		flagButton(40);

		DecreaseItemButton(47, 10);
		DecreaseItemButton(48, 1);
		itemButton(49);
		IncreaseItemButton(50, 1);
		IncreaseItemButton(51, 10);
	}

	private NamePrompt namePrompt;
	private void nameButton(int slot){
		if (Validate.notNull(namePrompt)){
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(namePrompt.get());
			item.setItemMeta(meta);
			tempItem = item.clone();
			namePrompt = null;
			display();
		}
		
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			namePrompt = new NamePrompt(this);
			new ChatInteract(player, namePrompt, "Cancel");
			player.closeInventory();
		}, "",  
				"\n&bClick to Edit/Set Displayname");
	}

	private LorePrompt lorePrompt;
	private void loreButton(int slot){
		if (Validate.notNull(lorePrompt)){
			ItemMeta meta = item.getItemMeta();
			meta.setLore(lorePrompt.get());
			item.setItemMeta(meta);
			tempItem = item.clone();
			namePrompt = null;
			display();
		}
		
		setItem(slot, tempItem, player -> {
			lorePrompt = new LorePrompt(this);
			new ChatInteract(player, lorePrompt, "Cancel");
			player.closeInventory();
		}, "",  
				"\n&bClick to Edit/Set Lore");
	}
	
	private void enchantButton(int slot){
		setItem(slot, new ItemStack(Material.ENCHANTED_BOOK), player -> {
			new EnchantGUI(player, kit, item, itemSlot, this);
		}, "",  
				"\n&bClick to Edit Enchantments");
	}

	private void flagButton(int slot){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			new ItemFlagGUI(player, item, this);
		}, "",  
				"\n&bClick to Edit Flags");
	}

	private void DecreaseItemButton(int slot, int amount){
		tempItem.setAmount(amount);
		setItem(slot, new ItemStack(Material.WOOD_BUTTON, amount), player -> {
			if(itemSize - amount < 0)
				itemSize = item.getMaxStackSize();
			else 
				itemSize = itemSize - amount;
			display();
		},"&3Increase&6(&c- " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
	private void itemButton(int slot){
		tempItem.setAmount(itemSize);
		setItem(slot, tempItem, player -> {
			item.setAmount(itemSize);
		},"" , 
				"\n&3Edit the amount of blocks"
				+ "\n&aClick to Save"
				);
	}
	private void IncreaseItemButton(int slot, int amount){
		tempItem.setAmount(amount);
		setItem(slot, new ItemStack(Material.STONE_BUTTON, amount), player -> {
			if ((item.getAmount() + amount) > item.getMaxStackSize())
				itemSize = item.getMaxStackSize();
			else
				itemSize = itemSize + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
}
