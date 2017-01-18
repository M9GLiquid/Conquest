package eu.kingconquest.conquest.gui;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.kingconquest.conquest.core.Kit;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class KitItemEditGui extends ChestGui{
	private ChestGui previous;
	private ItemStack tempItem;
	private ItemStack item;
	private Player player;
	private int itemSlot;
	private Kit kit;
	private int itemSize;

	public KitItemEditGui(Player player, Kit kit, ItemStack item, int slot, ChestGui previous){
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
		init();
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

		RemoveButton(4);
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
	
	private void init(){
		setName();
		setLore();
	}

	private void RemoveButton(int slot){
		setItem(slot, new ItemStack(Material.BARRIER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				kit.removeItem(itemSlot);
				previous.create();
				close(player);
			}
		}, "&4Remove &fItem",  
				"\n&4WARNING!"
				+ "\n&3Double Click to &cRemove");
	}
	AlphabetGUI nameGUI;
	private void nameButton(int slot){
		setItem(slot, tempItem, player -> {
			nameGUI = new AlphabetGUI(player, this, "");
			System.out.println(nameGUI);
		}, "",  
				"\n&3Click to Edit Title");
	}
	private void setName(){
		if (Validate.notNull(nameGUI)){
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(nameGUI.get());
			item.setItemMeta(meta);
			tempItem = item.clone();
			nameGUI.close(player);
		}
	}

	AlphabetGUI loreGUI;
	private void loreButton(int slot){
		setItem(slot, tempItem, player -> {
			loreGUI = new AlphabetGUI(player, this, "");
		}, "",  
				"\n&3Click to Edit Lore");
	}
	private void setLore(){
		if (Validate.notNull(loreGUI)){
			ItemMeta meta = item.getItemMeta();
			/*List<String> lore = new ArrayList<String>();
			String temp = loreGUI.get();
			String[] a = temp.split("[NEW LINE]");
			for (int i= 0; i < a.length; i++){
				lore.add(ChatManager.Format(a[i]));
			}*/
			meta.setLore(Arrays.asList(loreGUI.get().split("[NEW LINE]")));
			
			item.setItemMeta(meta);
			tempItem = item.clone();
			loreGUI.close(player);
		}
	}
	
	private void enchantButton(int slot){
		setItem(slot, new ItemStack(Material.ENCHANTED_BOOK), player -> {
			new EnchantGUI(player, kit, item, itemSlot, this);
		}, "&3Edit &fEnchantments",  
				"\n&3Click to Edit");
	}

	private void flagButton(int slot){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			new ItemFlagGUI(player, item, this);
		}, "&3Edit &fItemFlags",  
				"\n&3Click to Edit");
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
			if ((item.getAmount() + amount) < item.getMaxStackSize())
				itemSize = item.getMaxStackSize();
			else
				itemSize = itemSize + amount;
			display();
		},"&3Increase&6(&c+ " + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
}
