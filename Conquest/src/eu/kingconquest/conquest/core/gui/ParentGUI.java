package eu.kingconquest.conquest.core.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.core.util.Cach;
import eu.kingconquest.conquest.core.util.ChestGui;
import eu.kingconquest.conquest.core.util.Config;

public class ParentGUI extends ChestGui{
	private ArrayList<Town> targets= new ArrayList<Town>();
	private ChestGui previous;
	private Village village;
	private Player p;

	public ParentGUI(Player player, Object objective, ChestGui previousGui){
		super();
		p = player;
		this.previous = (ChestGui) previousGui;;
		this.village = (Village) objective;;
		
		create();
	}

	@Override
	public void create(){
		targets.clear();
		Town.getTowns(village.getLocation().getWorld())
			.forEach(town->{
				if (village.hasParent() && village.getParent().equals(town))
					return;
				targets.add(town);
			});
		createGui(p, "", targets.size());
		display();
	}

	@Override
	public void display(){
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
		if (targets.size() != 0) {
			for(int i = 9; i < 54; i++) {
				Town town = targets.get(getCurrentItem());
				ParentButton(i, town);
				setCurrentItem(getCurrentItem() + 1);
				if (getCurrentItem() >= targets.size())
					break;
			}
		}
	}

	private void ParentButton(int slot, Town town){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			village.setParent(town);
			Cach.StaticTown = town;
			player.sendMessage(Config.getChat("editVillageParent"));
			previous.create();
			close(player);
		}, "§6Set §f" + town.getName() + " §6as Parent","§f----------------- "
				+ "\n§cClick to Select!");
	}
}
