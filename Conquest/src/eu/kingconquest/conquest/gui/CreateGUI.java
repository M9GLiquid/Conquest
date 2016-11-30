package eu.kingconquest.conquest.gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class CreateGUI extends ChestGui{
	private Player p;
	private String name = "Not Set";
	private Location loc;
	private Location spawn;
	private Kingdom owner;

	private ChestGui previous;
	
	public CreateGUI(Player player, ChestGui previousGui){
		super();
		p = player;
		previous = previousGui;
		create();
	}

	@Override
	public void create(){
		owner  = Kingdom.getNeutral(p.getWorld());
		createGui(p, "&6Create Gui", 9);
		display();
	}

	private int slot = 9;
	@Override
	public void display(){
		clearSlots();
		init();
		slot = 9;
		
		//Slot 0
		playerInfo(p);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 4
		infoIcon();
		//Slot 5
		next(this);
		
		//Slot 6
		discardButton();
		//Slot 7
		saveButton();
		//----------------
		
		//Slot 9
		nameButton();
		//Slot 10
		locationButton();
		//Slot 11
		spawnButton();
		//Slot 12
		/*if (previous instanceof TownGUI) {
			//ownerButton();
			//childrenButton();
		}
		if (previous instanceof VillageGUI) {
			//ownerButton();
		 	//parentButton();
		 }*/
	}

	private void init(){
		setName();
	}
	
	private void infoIcon(){
		setItem(4, new ItemStack(Material.PAPER), player -> {
		},"&aInformation"
		, displayInfo());		
	}
	
	private void discardButton(){
		setItem(7, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			close(player);
			previous.create();
		}, "§4<< Home", "§1-----------------"
				+ "\n§cGo back without Saving!\n");
	}
	
	private void saveButton(){
			setItem(8, new ItemStack(Material.EMERALD_BLOCK), player -> {
				if (previous instanceof KingdomGUI) {
					Kingdom kingdom = new Kingdom(this.name //Name
							, null //UUID
							, loc //Fixed location
							, spawn //Spawn location
							);
					if (kingdom.create(player))
						Config.saveKingdoms(loc.getWorld());
					else
						Kingdom.removeKingdom(kingdom);
					
				} else if (previous instanceof TownGUI) {
					Town town = new Town(
							this.name //Name
							, loc //Fixed location
							, spawn //Spawn location
							, owner //Owner
							); 
					if (town.create(player))
						Config.saveTowns(loc.getWorld());
					else
						Town.removeTown(town);
				}else if (previous instanceof VillageGUI) {
					Village village = new Village(
							this.name //Name
							, loc //Fixed location
							, spawn //Spawn location
							, owner //Owner
							, Kingdom.getKingdom("Neutral", loc.getWorld()) //Pre Owner
							, null //Parent
							);
					if (village.create(player))
						Config.saveVillages(loc.getWorld());
					else
						Village.removeVillage(village);
				}
				close(player);
				previous.create();
			}, "§2Create new!", displayInfo());
	}

	private String displayInfo(){
		String str = "&1-----------------";

		str += "\n&aName: &r" + name;
		if (previous instanceof KingdomGUI)
		str += "\n&aKing: &r" + "None"
			+ "\n&aMembers: &r0";
		
		if (previous instanceof TownGUI)
			str += "\n&aOwner: &r" + owner.getName();
		
		if (previous instanceof VillageGUI)
			str += "\n&aOwner: &r" + owner.getName()
				+ "\n&aParent: &rNot Set";
			
		str += "\n&aLocation:";
		if (Validate.notNull(loc)) {
			str += "\n- &cX: &r"+ Math.floor(loc.getX())
					+"\n- &cY: &r"+ Math.floor(loc.getY())
					+"\n- &cZ: &r"+ Math.floor(loc.getZ());
		}else
			str += "\n- Not Set";
		str += "\n&aSpawn:";
		if (Validate.notNull(spawn)){
			str += "\n- &cX: &r"+ Math.floor(spawn.getX())
					+"\n- &cY: &r"+ Math.floor(spawn.getY())
					+"\n- &cZ: &r"+ Math.floor(spawn.getZ());
			
		}else
			str += "\n- Not Set";
		return str;
	}

	AlphabetGUI alphabetGUI;
	private void nameButton(){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			alphabetGUI = new AlphabetGUI(player, this, null);
		alphabetGUI.create();
		slot = 9;
	}, "§4Set Name!",  "&1-----------------"
			+ "\n§cSet name\n");
	slot++;
	}
	
	private void spawnButton(){
		setItem(slot, new ItemStack(Material.BED), player -> {
			spawn = player.getLocation().clone();
			clearSlots();
			slot = 9;
			display();
		}, "§4Set Spawn","§1-----------------"
				+ "\n§cSet Spawn\n");
		slot++;
	}
	
	private void locationButton(){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			slot = 9;
			loc = player.getLocation().clone();
			display();
		}, "§4Set Location","§1-----------------"
				+ "\n§cSet location\n");
		slot++;
	}

	protected void setName(){
		if (Validate.notNull(alphabetGUI)){
			name = alphabetGUI.getWord();
			alphabetGUI.close(p);
		}
	}
}
