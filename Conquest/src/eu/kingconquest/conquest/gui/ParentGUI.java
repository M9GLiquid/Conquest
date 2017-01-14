package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class ParentGUI extends ChestGui{
	private ArrayList<Town> targets= new ArrayList<Town>();
	private ChestGui previous;
	private Village village;
	private Objective objective;
	private Player p;

	public ParentGUI(Player player, Objective objective, ChestGui previousGui){
		super();
		p = player;
		this.previous = (ChestGui) previousGui;
		this.objective = objective;

		create();
	}

	@Override
	public void create(){
		targets.clear();
		if (objective instanceof Village){
			Town.getTowns(village.getLocation().getWorld())
			.forEach(town->{
				
				if (village.hasParent() && village.getParent().equals(town))
					return;
				targets.add(town);
			});
		}else if(objective instanceof Town){
			
		}else if(Validate.isNull(objective)){
			targets = Town.getTowns(p.getWorld());
		}
		createGui(p, "", targets.size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();

		//Slot 0
		playerInfo(p);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		closeButton();

		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (targets.size() -1) || getItems() == 0)
				break;
			Town town = targets.get(getCurrentItem());
			ParentButton(i, town);
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void ParentButton(int slot, Town town){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			objective = town;
			previous.create();
		}, "&6Set &f" + town.getName() + " &6as Parent",
				"&cClick to Select!");
	}
	
	public Objective get(){
		return objective;
	}
}
