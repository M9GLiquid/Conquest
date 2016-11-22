package eu.kingconquest.conquest.core.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.core.util.ChestGui;
import eu.kingconquest.conquest.core.util.Validate;
import eu.kingconquest.conquest.hook.Vault;

public class VillageGUI extends ChestGui{
	private ArrayList<Village> targets= new ArrayList<Village>();
	private Player p;
	private ChestGui previous;
	
	public VillageGUI(Player p, Object previousGui) {
		super();
		this.p = p;
		this.previous = (ChestGui) previousGui;
		create();
	}

	@Override
	public void create(){
		targets.clear();
		for (Village village : Village.getVillages())
			targets.add(village);
		createGui(p, "&6Village Gui", targets.size());
		display();
	}

	@Override
	public void display() {
		clearSlots();
		//Slot 0
		playerInfo(p);
		//SLot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 7
		if (Vault.perms.has(p, Main.getInstance().getName() + ".admin.village.create"))
			createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() >= targets.size() || getItems() == 0)
				return;
			
			if (Validate.hasPerm(p, "admin.village.edit")) 
				villages(i, targets.get(getCurrentItem()));
			
			setCurrentItem(getCurrentItem()+1);
		}
	}
	
	private void villages(int i, Village village){
		setItem(i, new ItemStack(Material.BEACON), player -> {
			setCurrentItem(0);
			new EditGUI(player, village, this);
		},"&aEdit " + village.getOwner().getColorSymbol() + village.getName()
		, displayInfo(village));
	}

	private String displayInfo(Village village) {
		String str = "&1-----------------";
		
		str += "\n&aName: &f" + village.getName();
		if (!Validate.isNull(village.getParent()))
			str += "\n&aParent: &f" + village.getParent().getName();
		else
			str += "\n&aParent: &fNone";
		str += "\n&aLocation:"
		+ "\n&cX: &f"+ Math.floor(village.getLocation().getX())
		+ "\n&cY: &f"+ Math.floor(village.getLocation().getY())
		+ "\n&cZ: &f"+ Math.floor(village.getLocation().getZ())
		+ "\n&aSpawn:"
		+ "\n&cX: &f"+ Math.floor(village.getSpawn().getX())
		+ "\n&cY: &f"+ Math.floor(village.getSpawn().getY())
		+ "\n&cZ: &f"+ Math.floor(village.getSpawn().getZ());
		return str;
	}
	
	private void createButton(){
		setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
			new CreateGUI(player, this);
		}, "§4Create new Village!", "§1-----------------"
				+ "\n§cClick to open Edit Manager!"
				);
	}
}
