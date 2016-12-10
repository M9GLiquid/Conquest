package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.ChestGui;

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
			village.setParent(town);
			town.addChild(village);
			Cach.StaticVillage = village;
			Cach.StaticTown = town;
			ChatManager.Chat(player, Config.getChat("editVillageParent"));
			previous.create();
			close(player);
		}, "§6Set §f" + town.getName() + " §6as Parent","§f----------------- "
				+ "\n§cClick to Select!");
	}
}
