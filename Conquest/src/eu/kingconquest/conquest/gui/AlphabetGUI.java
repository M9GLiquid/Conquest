package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class AlphabetGUI extends ChestGui{
	private Player p;
	private ChestGui previous;
	private String[] alphabet = 
		{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q","R", "S", "T", "U", "V", "W", "X", "Y","Z"};
	private String[] symbols = 
		{" ", "!", "@", "#", "£", "¤", "$", "%", "&", "/", "{", "(", "[", ")", "]"
			, "=", "}", "?", "+", "^", "~", "*", "-", "_", ".", ":", ",", ";" ,"<", ">"
			, "|", "1", "2", "3", "4", "5" ,"6", "7", "8", "9", "0"};
	private ArrayList<String> word = new ArrayList<String>();
	private int slot = 9;

	public AlphabetGUI(Player p, Object previousGui, String word) {
		super();
		this.p = p;
		previous = (ChestGui) previousGui;
		create();
		
		if (Validate.isNull(word))
			setWord(" ");
		else
			setWord(word);
	}

	@Override
	 public void create(){
			createGui(p, "&6Alphabet Gui", symbols.length);
			displayItems();
			display();
	}

	private void displayItems(){
		//Slot 0
		playerInfo(p);
		//Slot 1
		displayWord();
		//Slot 2
		CaseButton();
		//Slot 3
		if (word.size() != 0)
			removeButton();
		//Slot 4
		symbolsButton();
		//Slot 5
		clearButton();
		//Slot 6
		//Slot 7
		saveButton();
		//Slot 8
		backButton(previous);
	}
	
		@Override
	public void display(){
		slot = 9;
		for (int i = 0; i < alphabet.length; i++) 
			add(alphabet[i]);
	}

	private void displayWord(){
		setItem(1, new ItemStack(Material.PAPER), player -> {
		},"&6" + getWord() , "&1-----------------");
	}

	private void clearButton(){
		setItem(5, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			word.clear();
			Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3.0F, 1.0F);
			clearSlots();
			slot = 9;
			display();
			displayItems();
		},"&cClear Word" , "&1-----------------");
	}

	private void saveButton(){
		setItem(7, new ItemStack(Material.EMERALD_BLOCK), player -> {
			Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3.0F, 1.0F);
			previous.create();
		},"&aSave Word" , "&1-----------------");
	}


	private void removeButton(){
		setItem(3, new ItemStack(Material.BOOK), player -> {
			Bukkit.getWorld(player.getWorld().getUID())
				.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3.0F, 1.0F);
			clearSlots();
			slot = 9;
			displayRemove();
			displayItems();
		},"&cRemove Letter" , "&1-----------------");
	}
	
	private void displayRemove(){
		for (int i = 0; i < word.size(); i++) 
			remove(i, word.get(i));
		if (word.size() == 0)
			display();
	}

	private void remove(int i, String str){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			Bukkit.getWorld(player.getWorld().getUID())
				.playSound(player.getLocation(), Sound.ITEM_HOE_TILL, 3.0F, 1.0F);
			word.remove(i);
			clearSlots();
			slot = 9;
			displayRemove();
			displayItems();
		},"&6" + str , "&1-----------------");
		slot++;
	}

	private void CaseButton(){
		if (alphabet[0].equals(alphabet[0].toUpperCase())){
			setItem(2, new ItemStack(Material.BOOK), player -> {
				clearSlots();
				slot = 9;
				lowerCase();
				Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3.0F, 1.0F);
				display();
				displayItems();
			}, "&aLower Case" , "&1-----------------\n");
		}else{
			setItem(2, new ItemStack(Material.ENCHANTED_BOOK), player -> {
				clearSlots();
				slot = 9;
				upperCase();
				Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3.0F, 1.0F);
				display();
				displayItems();
			}, "&aUpper Case" , "&1-----------------\n");
		}
	}

	private void upperCase(){
		for (int i = 0; i < alphabet.length; i++)
			alphabet[i] = alphabet[i].toUpperCase();
	}

	private void lowerCase() {
		for (int i = 0; i < alphabet.length; i++)
			alphabet[i] = alphabet[i].toLowerCase();
	}

	private boolean symbolsToggled = false;
	private void symbolsButton(){
		setItem(4, new ItemStack(Material.ENCHANTED_BOOK), player -> {
			Bukkit.getWorld(player.getWorld().getUID())
				.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3.0F, 1.0F);
			clearSlots();
			if (symbolsToggled){
				symbolsToggled = false;
				slot = 9;
				display();
				displayItems();
			}else{
				symbolsToggled = true;
				slot = 9;
				for (int i = 0; i < symbols.length; i++) 
					displaySymbols(symbols[i]);
				displayItems();
			}
		},"Symbols" , "&1-----------------\n");
	}

	private void displaySymbols(String str){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			word.add(str);
			slot = 9;
			Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ITEM_HOE_TILL, 3.0F, 1.0F);
			displayWord();
		}, str.replace(" ", "[SPACE]") , "&1-----------------");
		slot++;
	}

	private void add(String str){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			clearSlots();
			slot = 9;
			word.add(str);
			Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ITEM_HOE_TILL, 3.0F, 1.0F);
			displayWord();
			displayItems();
			display();
		},"&6" + str , "&1-----------------");
		slot++;
	}

	public String getWord() {
		if (word.size() <= 0)
			return " ";
		String text = " ";
		for (String str : word) 
			text = text + str;
		return text.trim();
	}

	public void setWord(String text) {
		word.clear();
		for (String str : text.replaceAll("([§][1-9a-f])", "").trim().split(""))
			if (str != "")
				word.add(str);
		displayWord();
	}
}
