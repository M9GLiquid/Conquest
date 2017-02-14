package eu.kingconquest.conquest.gui;

import java.awt.Desktop;
import java.net.URI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.ChestGui;


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
	
	@Override
	public void display(){
		clearSlots();
		
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
		desc1Button(9);
		slot1Button(10);
		slot2Button(11);
		slot3Button(12);
		slot4Button(13);

		//Second row
		desc2Button(18);
		slot9Button(19);
		slot10Button(20);
		slot11Button(21);
	}

	//First row
	private void desc1Button(int slot){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&6Conquest Plugin Help", 
				"");
	}

	private void slot1Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&3Join the movement!", 
				"&6# &aJoin &7a Kingdom"
						+ "\n"
				+ "\n&6# &cNote: &7Any kingdom is a &agood &7kingdom"
				+ "\n");
	}

	private void slot2Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&3Conquer the world", 
				"&6# &eCapture &7by Standing next to a"
				+ "\n&7  - &3Small beacon&7 can be found all over the world"
				+ "\n&6# &7You can only capture the larger &3beacon by"
				+ "\n&7 - &7capturing all small beacons around the larger beacon"
				+ "\n&6# Once all the smaller beacons are captured "
				+ "\n&7 - automatically capture the big one "
				+ "\n"
				+ "\n&6# &cNote: &3Smaller Beacons &7are referred to as &eVillages"
				+ "\n&6# &cNote: &3Larger Beacons &7are referred to as &eTowns"
				+ "\n");
	}

	private void slot3Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&3Collect Rewards!", 
				"&6# &8Collect your &5rewards"
				+ "\n&7  - &6Money"
				+ "\n&7  - &5Reward Boxes &7(\"Kits\")"
				+ "\n"
				+ "\n&6# &cNote: &7Reward boxes belong to Towns"
				+ "\n&6# &cNote: &7Holding a Town grants you it's Reward Box"
				);
	}

	private void slot4Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&3Establish your home!", 
				"\n&6#  &8Properties &3Renting&7/&aBuying&7/&cSelling"
				+ "\n&7  - &7Rooms\'s"
				+ "\n&7  - &7Apartment\'s"
				+ "\n&7  - &7Houses"
				+ "\n&7  - &7&mInn\'s"
				+ "\n&7  - &7&mProperty Agencies"
				+ "\n&7  - &7&mStables"
				+ "\n&7  - &7&mBuilding Zones"
				+ "\n"
				+ "\n&6# &cNote: &7Some Property can only be rented"
				+ "\n&6# &cNote: Some can only be Baught/Sold"
				);
	}

	//Second row
	private void desc2Button(int slot){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&6Conquest Plugin Description", 
				"");
	}

	private void slot9Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&bPart 1", 
				"&7The &eConquest &7Plugin is a &eunique &7plugin,"
				+ "\n&7Developed for this &8Network &7only,"
				+ "\n&7By the Network Developers!"
				+ "\n");
	}

	private void slot10Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&bPart 2", 
				"&7It\'s based on the popular,"
				+ "\n&7Game mode &eCapture the Flag&7,"
				+ "\n&7But on a &aWorld &7scale."
				+ "\n");
	}

	private void slot11Button(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite() + "/Wiki"));
					}catch (Exception e){	}
				}
			}
		}, "&bPart 3", 
				"&7The description of this plugin would be:"
				+ "\n"
				+ "\n&8World Domination, &aDone Right!"
				+ "\n");
	}
}
