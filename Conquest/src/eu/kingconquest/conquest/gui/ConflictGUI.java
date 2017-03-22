package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Teleport;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;


// Example 
public class ConflictGUI extends ChestGui{
	private ArrayList<Objective> targets= new ArrayList<Objective>();
	private Kingdom owner;
	private ChestGui previous;
	private Player player;
	
	public ConflictGUI(Player player, ChestGui previousGui){
		super();
		this.previous = previousGui;
		this.player = player;
		owner = PlayerWrapper.getWrapper(player).getKingdom(player.getWorld());
		create();
	}
	
	@Override
	public void create(){
		if (Validate.isNull(owner)) return;
		
		Village.getVillages(player.getWorld()).forEach(village->{
			if (!owner.equals(village.getOwner())) return;
			if (village.hasParent()){ // IF Village has a parent
				if (village.getParent().getOwner().equals(owner)) // If Village parent kingdom is same as players kingdom
					if (!targets.contains(village.getParent())) //If Town not already added
						targets.add(village.getParent());
			}else //Add if single Village
				targets.add(village);
		});
		
		if (targets.size() < 1){
			Cach.StaticKingdom = PlayerWrapper.getWrapper(player).getKingdom(player.getWorld());
			new Message(player, MessageType.CHAT, "{NoCapturedTowns}");
			new Message(player, MessageType.CHAT, "{ConflictGUIInfo}");
			return;
		}
		
		createGui(player, "&6Conflict Gui", targets.size());
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
						+ "\n -&1Z: &f" + Math.floor(target.getLocation().getZ()));
	}
}
