package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class VillageGUI extends ChestGui{
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
		createGui(p, "&6Village Gui", Village.getVillages(p.getWorld()).size());
		display();
	}

	@Override
	public void display() {
		setCurrentItem(0);
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
		if (Validate.hasPerm(p, ".admin.create.village"))
			createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > getItems() -1 || getItems() < 1)
				break;
			
			if (Validate.hasPerm(p, "admin.edit.village")) 
				villages(i, Village.getVillages(p.getWorld()).get(getCurrentItem()));
			
			setCurrentItem(getCurrentItem()+1);
		}
	}
	
	private void villages(int i, Village village){
		setItem(i, new ItemStack(Material.BEACON), player -> {
			new EditGUI(player, village, this);
		},"&aEdit " + village.getOwner().getColorSymbol() + village.getName()
		, displayInfo(village));
	}

	private String displayInfo(Village village) {
		String str = "\n&aName: &f" + village.getName();
		if (Validate.notNull(village.getParent()))
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
			setCurrentItem(0);
			new CreateGUI(player, this);
		}, "&4Create new Village!", 
				"\n&cClick to open the Create manager!"
				);
	}
}
