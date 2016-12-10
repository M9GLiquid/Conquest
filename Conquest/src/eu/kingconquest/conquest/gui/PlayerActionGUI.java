package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class PlayerActionGUI extends ChestGui{
	private ArrayList<Kingdom> targets= new ArrayList<Kingdom>();
	private Objective objective;
	private ChestGui previous;
	private Player target;
	private Player p;

	public PlayerActionGUI(Player player, Player targetPlayer, Object objective, Object previousGui){
		super();
		this.p = player;
		this.target = targetPlayer;
		this.objective = (Kingdom) objective;
		this.previous = (ChestGui)  previousGui;

		create();
	}
	
	@Override
	public void create(){
		targets.clear();
		if (!(objective instanceof Kingdom)){
			for (Kingdom kingdom : Kingdom.getKingdoms())
				targets.add(kingdom);
		}
		createGui(p, "&6Player", 9);
		display();
	}
	
	private int slot;
	@Override
	public void display(){
		clearSlots();
		slot = 9;
		
		//Slot 0
		playerInfo(target);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 4
			infoIcon();
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);
		
		
		//Slot MAIN
		if (objective instanceof Kingdom){
			Kingdom kingdom = (Kingdom) objective;
			if (kingdom.hasMember(target.getUniqueId())){
				//Remove from current kingdom
				removeButton();
				if (Validate.isNull(kingdom.getKing())){
					//Promote within the kingdom
					promoteButton();
				}else{
					if (kingdom.getKing().getUniqueId().equals(target.getUniqueId())){
						//Demote within the kingdom
						demoteButton();
					}
				}
			}else{
				//Move to current kingdom
				moveToButton();
			}
		}else{
			kingdomButton();
		}
	}

	private void kingdomButton(){
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (targets.size() -1) || getItems() == 0)
				break;

			setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
				objective = targets.get(getCurrentItem());
				display();
			}, "§4Choose Kingdom", "§1-----------------"
					+ "\n§cGo back without Saving!\n");
		}

		slot++;
	}
	
	/**
	 * Rank up in the Kingdom Hierchy
	 */
	private void promoteButton(){
		//Promote to Count 		-> 	King
		//Promote to Baron 		-> 	Count
		//Promote to Knight		->	Baron
		//Promote to Squire		->	Knight

		//slot++;
	}

	/**
	 * Rank down in the Kingdom Hierchy
	 */
	private void demoteButton(){
		//Demote from King 			-> 	Count
		//Demote from Count 		-> 	Baron
		//Demote from Baron		->	Knight
		//Demote from Knight		->	Squire

		//slot++;
	}

	private void removeButton(){
		if (objective instanceof Kingdom){
			Kingdom kingdom = (Kingdom) objective;
			setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
				kingdom.leave(target);
				Cach.StaticKingdom = kingdom;
				Cach.StaticPlayer = target;
				ChatManager.Chat(p, Config.getChat("adminMoveSuccess"));
				ChatManager.Chat(target, Config.getChat("RemoveSuccess"));
				display();
			}, "§4Remove from " +kingdom.getColorSymbol() + kingdom.getName()
				, "§1-----------------"
					+ "\n§c\n");
			slot++;
		}
	}
	
	private void moveToButton(){
		if (objective instanceof Kingdom){
			Kingdom kingdom = (Kingdom) objective;
			setItem(slot, new ItemStack(Material.EMERALD_BLOCK), player -> {
				kingdom.join(target);
				Cach.StaticKingdom = kingdom;
				ChatManager.Chat(p, Config.getChat("adminMoveSuccess"));
				ChatManager.Chat(target, Config.getChat("MoveSuccess"));
				display();
			}, "§4Move to " + kingdom.getName()
				, "§1-----------------"
					+ "\n§c\n");
			slot++;
		}
	}

	private void infoIcon(){

		//slot++;
	}

}
