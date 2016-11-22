package eu.kingconquest.conquest.core.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.util.ChestGui;

public class HelpGUI extends ChestGui{
	private Player p;
	private ChestGui previous;

	public HelpGUI(Player p, ChestGui previousGui) {
		this.p = p;
		this.previous = previousGui;

		create();
	}
	
	@Override
	public void create(){
		createGui(p, "Help Information", 18);
		display();
	}
	
	@SuppressWarnings("unused")
	private int slot;
	@Override
	public void display(){
		clearSlots();
		slot = 9;
		
		//Slot 0
		playerInfo(p);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		//First row
		desc1Button();
		slot1Button();
		slot2Button();
		slot3Button();

		//Second row
		desc2Button();
		sslot9Button();
		slot10Button();
		slot11Button();
	}

	//First row

	private void desc1Button(){
		setItem(9, new ItemStack(Material.BOOK), player -> {
		}, "&6Conquest Plugin Help:", "&1-----------------"
				+ "\n");
		slot++;
	}

	private void slot1Button(){
		setItem(10, new ItemStack(Material.PAPER), player -> {
		}, "&6Step 1:", "&1-----------------"
				+ "\n&6# &2Join &6the movement,"
				+ "\n&6# &2Join &6a Kingdom"
				+ "\n");
		slot++;
	}

	private void slot2Button(){
		setItem(11, new ItemStack(Material.PAPER), player -> {
		}, "&6Step 2:", "&1-----------------"
				+ "\n&6# &eCapture &3'Beacons' &6spread across the map"
				+ "\n&6# &2Get rewards"
				+ "\n");
		slot++;
	}

	private void slot3Button(){
		setItem(12, new ItemStack(Material.PAPER), player -> {
		}, "&6Step 3:", "&1-----------------"
				+ "\n&6# &2Collect your rewards"
				+ "\n&6# &3Rent/&2Buy &6an apartment/house"
				+ "\n&6#  &4*Coming Soon* &2Buy &6a 'Building Zone'"
				+ "\n");
		slot++;
	}

	//Second row

	private void desc2Button(){
		setItem(18, new ItemStack(Material.BOOK), player -> {
		}, "&6Conquest Plugin Description:", "&1-----------------"
				+ "\n&6"
				+ "\n");
		slot++;
	}

	private void sslot9Button(){
		setItem(19, new ItemStack(Material.PAPER), player -> {
		}, "&bConquest Plugin Description:", "&1-----------------"
				+ "\n&6The &eConquest &6Plugin is a &eunique &6plugin,"
				+ "\n&6Developed for the KingConquest Network,"
				+ "\n&6By the Network Developer!"
				+ "\n");
		slot++;
	}

	private void slot10Button(){
		setItem(20, new ItemStack(Material.PAPER), player -> {
		}, "&bConquest Plugin Description:", "&1-----------------"
				+ "\n&6The &eMini Game &6is based on the popular,"
				+ "\n&6Game mode &eCapture the Flag (ctf),"
				+ "\n&6But on a &emuch &6larget scale."
				+ "\n");
		slot++;
	}

	private void slot11Button(){
		setItem(21, new ItemStack(Material.PAPER), player -> {
		}, "&bConquest Plugin Description:", "&1-----------------"
				+ "\n&6The Basic of this plugin is:"
				+ "\n&cWorld Domination&6, &aDone Right!"
				+ "\n");
		slot++;
	}
}
