package eu.kingconquest.conquest.core.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.util.ChestGui;

	//Make sure to setup a discard and a save button for this one
public class PlayerGUI extends ChestGui{
	private Player player;
	private ChestGui previous;
	private Objective objective;

	public PlayerGUI(Player p, Object previousGui, Object object){
		super();
		player = p;
		previous = (ChestGui) previousGui;
		objective = (Objective) object;
		create();
	}

	private ArrayList<Player> targets = new ArrayList<Player>();
	@Override
	public void create(){
		targets.clear();
			if (objective instanceof Kingdom){
				((Kingdom) objective).getMembers().forEach(uuid->{
					targets.add(Bukkit.getPlayer(uuid));
				});
			}else{
				Bukkit.getOnlinePlayers().forEach(player->{
					targets.add(player);
				});
			}
		createGui(player, "&6Player", targets.size());
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
		//Slot 6
		clearMembersButton();
		
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (targets.size() -1) || getItems() == 0)
				break;
			playerButton(targets.get(getCurrentItem()), i);
		}
	}

	private void clearMembersButton(){
		setItem(6, new ItemStack(Material.REDSTONE_BLOCK), player->{
			((Kingdom) objective).clearMembers();
		}, "§4Clear Members","§1-----------------"
				+ "\n§cClick to clear a Kingdoms Members"
				+ "\n"
				);
	}

	private void playerButton(Player targetPlayer, int slot){

		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(targetPlayer.getName());
		head.setItemMeta(skull);
        setSkullItem(slot, head, player ->{
			new PlayerActionGUI(player, targetPlayer, objective, this);
		}, "§4" + targetPlayer.getName(),"§1-----------------"
				+ "\n§cClick to get into Player specific GUI"
				+ "\n"
				);
        setCurrentItem(getCurrentItem() + 1);
	}
}
