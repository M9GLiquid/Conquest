package eu.kingconquest.conquest.gui.objective;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;

public class OwnerGUI extends ChestGui{
	private Player player;
	private ChestGui previous;
	private Kingdom owner;

	public OwnerGUI(Player player, ChestGui previousGui){
		super();
		this.player = player;
		previous = (ChestGui) previousGui;

		create();
	}

	@Override
	public void create(){
		owner = Kingdom.getKingdom("Neutral", player.getWorld());
		createGui(player, "OwnerShip GUI", Kingdom.getKingdoms().size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
		playerInfo(player);
		previous(this);
		next(this);
		backButton(previous);

		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (Kingdom.getKingdoms().size() -1) || getItems() == 0)
				break;
			
			Kingdom kingdom = Kingdom.getKingdoms().get(getCurrentItem());
			ownerButton(i, kingdom);
			setCurrentItem(getCurrentItem() + 1);
		}
	}

	private void ownerButton(int slot, Kingdom kingdom){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			this.owner = kingdom;
			previous.create();
		}, kingdom.getColorSymbol() + kingdom.getName(),
				"&bClick to select as &aOwner!");
	}

	public Kingdom get(){
		return this.owner;
	}
}
