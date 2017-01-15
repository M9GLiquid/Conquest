package eu.kingconquest.conquest.gui;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class KitItemEditGui extends ChestGui{
	private ChestGui previous;
	private ItemStack item;
	private Player player;

	public KitItemEditGui(Player player, ItemStack item, ChestGui previous){
		super();
		this.previous = previous;
		this.player = player;
		this.item = item;
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

		nameButton(13);
		loreButton(22);
		enchantButton(31);
		flagButton(40);
	}
	
	private void init(){
		setName();
		setLore();
	}
	
	AlphabetGUI nameGUI;
	private void nameButton(int slot){
		setItem(slot, item, player -> {
			nameGUI = new AlphabetGUI(player, this, "");
		}, "&3Edit &f" + ( name != "" ? name : "Name"),  
				"\n&3Click to Edit");
	}
	private String name = "";
	private void setName(){
		if (Validate.notNull(nameGUI)){
			name = nameGUI.get();
			nameGUI.close(player);
		}
	}

	AlphabetGUI loreGUI;
	private void loreButton(int slot){
		setItem(slot, item, player -> {
			loreGUI = new AlphabetGUI(player, this, "");
		}, "&3Edit &fLore",  
				"\n&3Click to Edit");
	}
	private Set<String> lore;
	private void setLore(){
		if (Validate.notNull(loreGUI)){
			for (String tempLore : loreGUI.get().split("\n")){
				lore.add(tempLore);
			}
			loreGUI.close(player);
		}
	}
	
	private void enchantButton(int slot){
		setItem(slot, new ItemStack(Material.ENCHANTED_BOOK), player -> {
			new EnchantGUI(player, item, this);
		}, "&3Edit &fEnchantments",  
				"\n&3Click to Edit");
	}

	private void flagButton(int slot){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			new ItemFlagGUI(player, item, this);
		}, "&3Edit &fItemFlags",  
				"\n&3Click to Edit");
	}
}
