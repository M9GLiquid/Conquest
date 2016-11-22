package eu.kingconquest.conquest.core.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.util.ChestGui;

public class OwnerGUI extends ChestGui{
	private ArrayList<Kingdom> targets= new ArrayList<Kingdom>();
	private Player p;
	
	public OwnerGUI(Player player, ChestGui previousGui){
		super();
		this.p = player;
		previous = (ChestGui) previousGui;
		
		create();
	}

	@Override
	public void create(){
		targets.clear();
		Kingdom.getKingdoms().stream()
		.filter(kingdom->!kingdom.isNeutral())
		.forEach(kingdom->{
			targets.add(kingdom);
		});
		createGui(p, "OwnerShip GUI", targets.size());
		display();
	}

	@Override
	public void display(){
		//Slot 0
		playerInfo(p);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		closeButton();

		if (targets.size() != 0) {
			for(int i = 9; i < 54; i++) {
				Kingdom kingdom = targets.get(getCurrentItem());
					ownerButton(i, kingdom);
					setCurrentItem(getCurrentItem() + 1);
				if (getCurrentItem() >= targets.size())
					break;
			}
		}
	}

	private void ownerButton(int slot, Kingdom kingdom){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			set(kingdom);
		}, "§6Set " + kingdom.getName() + " as Owner","§1-----------------"
				+ "\n§cClick to Select!!");
	}

	private ChestGui previous;
	private Kingdom owner = Kingdom.getKingdom("Neutral");
	private void set(Kingdom kingdom){
		this.owner = kingdom;
		previous.create();
	}

	public Kingdom get(){
		return this.owner;
	}
}
