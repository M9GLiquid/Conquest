package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Teleport;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.util.Validate;


// Example 
public class ConflictGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	private ArrayList<Objective> targets= new ArrayList<Objective>();

	public ConflictGUI(Player player, ChestGui previousGui){
		super();
		this.previous = previousGui;
		this.player = player;

		create();
	}

	@Override
	public void create(){
		targets.clear();
		if (Validate.isNull(PlayerWrapper.getWrapper(player).getKingdom(player.getWorld())))
			return;
		YmlStorage.getWorlds().forEach(uniqueID->{
			Village.getVillages(Bukkit.getWorld(uniqueID)).forEach(village->{
				if (PlayerWrapper.getWrapper(player).getKingdom(player.getWorld()).equals(village.getOwner()))
					return;
				if (village.hasParent() 
						&& !targets.contains(village.getOwner()))
					targets.add(village.getParent());
				else
					targets.add(village);
			});
		});
		if (targets.size() < 1)
			return;
		createGui(player, "&6Teleport Gui", targets.size());
		setCurrentItem(0);
		display();
	}
	
	@Override
	public void display(){
		clearSlots();

		//Slot 0
		playerInfo(player);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);

		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > getItems() -1 || getItems() < 1)
				break;
			tpButton(i, targets.get(getCurrentItem()));
			setCurrentItem(getCurrentItem()+1);
		}
	}

	private void tpButton(int slot, Objective target){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			new Teleport(player, target.getLocation().clone());
			targets.clear();
		},"&1Teleport to: &f" + target.getName(),
				"&aSpawn Location: "
				+ "\n -&1X: &f" + Math.floor(target.getLocation().getX())
				+ "\n -&1Y: &f" + Math.floor(target.getLocation().getY())
				+ "\n -&1Z: &f" + Math.floor(target.getLocation().getZ())
				);
	}
}
