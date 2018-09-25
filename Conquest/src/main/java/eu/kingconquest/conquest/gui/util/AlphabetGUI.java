package eu.kingconquest.conquest.gui.util;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlphabetGUI extends ChestGui{
	private Player player;
	private ChestGui previous;
	private String[] alphabet = 
		{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q","R", "S", "T", "U", "V", "W", "X", "Y","Z", " ", "[NEW LINE]"};
	private String[] symbols = 
		{	"1", "2", "3", "4", "5" ,"6", "7", "8", "9", "0"
                , "!", "@", "#", "£", "¤", "$", "%", "&", "\\", "/", "{", "(", "[", ")", "]"
			, "=", "}", "?", "+", "^", "~", "*", "-", "_", ".", ":", ",", ";" ,"<", ">"
                , "í", "|"};
    private ArrayList<String> word = new ArrayList<>();
	private int slot = 9;
	private String tempWord = "";

	public AlphabetGUI(Player player, ChestGui previousGui, String word) {
		super();
		this.player = player;
		this.tempWord = word;
        previous = previousGui;
		create();
	}

	public AlphabetGUI(Player player, ChestGui previousGui, List<String> lore){
		super();
		this.player = player;
        previous = previousGui;
		create();
	}
	
	@Override
	 public void create(){
			createGui(player, "&6Alphabet Gui", symbols.length);
			if (Validate.isNull(tempWord))
				setWord(" ");
			else
				setWord(tempWord);
			display();
	}
	
	@Override
	public void display(){
		clearSlots();
		slot = 9;
		//Slot 0
		playerInfo(player);
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
		//Slot 8
		saveButton();

		if (!removeToggled){
			if (!symbolsToggled)
                for (String anAlphabet : alphabet) addLetters(anAlphabet);
			else if (symbolsToggled)
                for (String symbol : symbols) addSymbols(symbol);
		}else{
			for (int i = 0; i < word.size(); i++) 
				remove(i, word.get(i));
			if (word.size() == 0){
				removeToggled = false;
				display();
			}
		}
}

	private void displayWord(){
		setItem(1, new ItemStack(Material.PAPER), player -> {
		},"&6" + get() , " ");
	}

	private void clearButton(){
		setItem(5, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			word.clear();
			clearSlots();
			slot = 9;
			display();
		},"&cClear Word" , "");
	}

	private void saveButton(){
        setItem(8, new ItemStack(Material.EMERALD_BLOCK), player ->
                previous.create(), "&aSave", "");
	}

	private boolean removeToggled = false;
	private void removeButton(){
		setItem(3, new ItemStack(Material.BOOK), player -> {
			removeToggled = !removeToggled;
			display();
		},"&cRemove Letter" , "");
	}

	private void remove(int i, String str){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			Bukkit.getWorld(player.getWorld().getUID()).playSound(player.getLocation(), Sound.ITEM_HOE_TILL, 3.0F, 1.0F);
			word.remove(i);
			display();
		},"&6" + str , "");
		slot++;
	}

	private void CaseButton(){
		if (alphabet[0].equals(alphabet[0].toUpperCase())){
			setItem(2, new ItemStack(Material.BOOK), player -> {
				lowerCase();
				display();
			}, "&aLower Case" , "");
		}else{
			setItem(2, new ItemStack(Material.ENCHANTED_BOOK), player -> {
				upperCase();
				display();
			}, "&aUpper Case" , "");
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
			symbolsToggled = !symbolsToggled;
			display();
		},"Symbols" , "");
	}

	private void addSymbols(String str){
        String tempWord = str.replace(" ", "[SPACE]")
                .replace(" ", "[NEW LINE]");
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			word.add(str);
			displayWord();
			display();
		}, "&6" + tempWord, "");
		slot++;
	}

	private void addLetters(String str){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			word.add(str);
			display();
		},"&6" + str , "");
		slot++;
	}

	public String get() {
		if (word.size() < 1)
			return " ";
        StringBuilder text = new StringBuilder(" ");
		for (String str : word)
            text.append(str);
        return text.toString().trim();
	}

	public void setWord(String text) {
		word.clear();
		for (String str : text.replaceAll("([&][1-9a-f])", "").trim().split(" "))
            if (!Objects.equals(str, ""))
				word.add(str);
		if (word.size() < 1)
			word.add(" ");
		displayWord();
	}
}
