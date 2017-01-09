package eu.kingconquest.conquest.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.event.ServerResetEvent;
import eu.kingconquest.conquest.event.WorldResetEvent;
import eu.kingconquest.conquest.util.ChestGui;



public class ResetGUI extends ChestGui{
	private boolean saveKingdoms = false;
	private boolean saveMembers = false;
	private boolean serverReset = false;
	private ChestGui previous;
	private World world;
	private Player player;

	public ResetGUI(Player player, ChestGui previousGui){
		super();
		this.player = player;
		this.previous = previousGui;
		this.world = player.getWorld();
		
		create();
	}
	
	@Override
	public void create(){
		createGui(player, "&6ResetGUI", 9);
		display();
	}

	private int slot;
	@Override
	public void display(){
		clearSlots();
		slot = 9;
		//Slot 0
		playerInfo(player);
		//Slot 3
		next(this);
		//Slot 4
		infoButton();
		//Slot 5
		previous(this);
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		saveKingdomsButton();

		saveKingdomMembersButton();
		
		setWorldOrServerButton();		
		if (serverReset)
			serverResetButton();
		else
			worldResetButton();
	}

	private void infoButton(){
		String str =  "";
		if (!serverReset)
			str = "\n&6 -: World: &7" + world.getName();
		setItem(4, new ItemStack(Material.PAPER), player -> {
		}, "&6Current Settings:", 
				"\n&6Server Reset: &7" + serverReset
				+ "\n&6World Reset : &7" + !serverReset
				+ str
				+ "\n&6Save Kingdoms: &7" + saveKingdoms
				+ "\n&6Save Members: &7" + saveMembers
				+ "\n");
	}
	
	private void setWorldOrServerButton(){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			serverReset ^= true;
			display();
		}, "&2Toggle Reset Mode", 
				"\n&6Reset Server: &7" + serverReset
				+ "\n&6Reset World: &7" + !serverReset
				+ "\n");
		slot++;
	}
		
	private void saveKingdomsButton(){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			saveKingdoms ^= true;
			display();
		}, "&2Save the Kingdoms", 
				"\n&6Keep the Kingdoms: &7" + saveKingdoms
				+ "\n&6Does not include members see &7Save Members"
				+ "\n");
		slot++;
	}
	
private void saveKingdomMembersButton(){
	setItem(slot, new ItemStack(Material.BOOK), player -> {
		saveMembers ^= true;
		display();
	}, "&2Save the Kingdoms", 
			"\n&6Reset Kingdoms Members &7" + saveMembers
			+ "\n");
	slot++;
}
	
	/**
	 * Server Reset Button
	 */
	private void serverResetButton(){
		setItem(17, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			Bukkit.getPluginManager().callEvent(new ServerResetEvent(player, saveKingdoms, saveMembers));
			display();
		}, "&2Reset Server", 
				"\n&6Current Settings:"
				+ "\n&6Save Kingdoms: &7" + saveKingdoms
				+ "\n");
		slot++;
	}
	
	/**
	 * World Reset Button
	 */
	private void worldResetButton(){
		setItem(17, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			Bukkit.getPluginManager().callEvent(new WorldResetEvent(player, world, saveKingdoms, saveMembers));
			display();
		}, "&2Reset This World", 
				"\n&6Current Settings:"
				+ "\n&6World : &7" + player.getWorld().getName()
				+ "\n&6Save Kingdoms: &7" + saveKingdoms
				+ "\n");
		slot++;
	}
		
}
