package eu.kingconquest.conquest.core.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.util.ChestGui;
import eu.kingconquest.conquest.core.util.Validate;
import eu.kingconquest.conquest.hook.Vault;

public class TownGUI extends ChestGui{
	private ArrayList<Town> targets= new ArrayList<Town>();
	private Player p;
	private ChestGui previous;
	
	public TownGUI(Player p, Object previousGui) {
		super();
		this.p = p;
		this.previous = (ChestGui) previousGui;
		create();
	}

	@Override
	public void create(){
		targets.clear();
		for (Town town : Town.getTowns())
			targets.add(town);
		createGui( p, "&6Town Gui", targets.size());
		display();
	}
	
	@Override
	public void display() {
		clearSlots();
		
		//Slot 0
		playerInfo(p);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 7
		if (Vault.perms.has(p, Main.getInstance().getName() + ".admin.kingdoms.create"))
			createButton();
		//Slot 8
		backButton(previous);

		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() >= targets.size() || getItems() == 0)
				return;

			if (Validate.hasPerm(p, "admin.village.edit")) 
				towns(i, targets.get(getCurrentItem()));
			
			setCurrentItem(getCurrentItem()+1);
		}
	}
	
	private void towns(int i, Town town){
		setItem(i, new ItemStack(Material.BEACON), player -> {
			setCurrentItem(0);
			clearSlots();
			new EditGUI(player, town, this);
		},"&aEdit " + town.getOwner().getColorSymbol() + town.getName()
		, displayInfo(town));
	}

	private String displayInfo(Town town) {
		String str = "&1-----------------";
		str += "\n&aName: &f" + town.getName();
		if (!Validate.isNull(town.getChildren()))
			str += "\n&aChildren: &f" + town.getChildren().size();
		else
			str += "\n&aChildren: &fNone";
		str += "\n&aLocation:"
		+ "\n&cX: &f"+ Math.floor(town.getLocation().getX())
		+ "\n&cY: &f"+ Math.floor(town.getLocation().getY())
		+ "\n&cZ: &f"+ Math.floor(town.getLocation().getZ())
		+ "\n&aSpawn:"
		+ "\n&cX: &f"+ Math.floor(town.getSpawn().getX())
		+ "\n&cY: &f"+ Math.floor(town.getSpawn().getY())
		+ "\n&cZ: &f"+ Math.floor(town.getSpawn().getZ());
		return str;
	}
	
	private void createButton(){
		setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
			new CreateGUI(player, this);
			close(player);
		}, "§4Create new Town!", "§1-----------------"
				+ "\n§cClick to open Edit Manager!"
				);
	}
}
