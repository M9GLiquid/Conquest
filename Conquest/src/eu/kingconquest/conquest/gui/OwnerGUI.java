package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.ChestGui;

public class OwnerGUI extends ChestGui{
	private Player p;

	public OwnerGUI(Player player, ChestGui previousGui){
		super();
		this.p = player;
		previous = (ChestGui) previousGui;

		create();
	}

	@Override
	public void create(){
		owner = Kingdom.getKingdom("Neutral", p.getWorld());
		createGui(p, "OwnerShip GUI", Kingdom.getKingdoms().size());
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

		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (Village.getVillages().size() -1) || getItems() == 0)
				break;
			Kingdom kingdom = Kingdom.getKingdoms().get(getCurrentItem());
			if (!kingdom.getName().equals("Neutral"))
				ownerButton(i, kingdom);
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private ChestGui previous;
	private Kingdom owner;
	private void ownerButton(int slot, Kingdom kingdom){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			this.owner = kingdom;
			previous.create();
		}, "§6Set " + kingdom.getName() + " as Owner","§1-----------------"
				+ "\n§cClick to Select!!");
	}


	public Kingdom get(){
		return this.owner;
	}
}
