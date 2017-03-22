package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.Scoreboard.BoardType;
import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.gui.objective.KingdomGUI;
import eu.kingconquest.conquest.gui.objective.OwnerGUI;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class PlayerActionGUI extends ChestGui{
	private PlayerWrapper wrapper;
	private Kingdom kingdom;
	private ChestGui previous;
	private Player target;
	private Player viewer;
	
	public PlayerActionGUI(Player viewingPlayer, Player targetPlayer, ChestGui previousGui){
		super();
		this.viewer = viewingPlayer;
		this.target = (Validate.notNull(targetPlayer) ? targetPlayer : viewingPlayer);
		this.previous = previousGui;
		wrapper = PlayerWrapper.getWrapper(target);
		if (wrapper.isInKingdom(target.getWorld()))
			kingdom = wrapper.getKingdom(target.getWorld());
		create();
	}
	
	@Override
	public void create(){
		createGui(viewer, "&6Player", 54);
		display();
	}
	
	@Override
	public void display(){
		clearSlots();
		
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
		if (Validate.hasPerm(viewer, ".admin.edit.player.*")){
			if (Validate.notNull(wrapper.isInKingdom(target.getWorld()))){
				scoreboardButton(10);
				if (!viewer.equals(target)) 
					kickButton(12);
				if (Validate.isNull(kingdom.getKing()))
					promoteButton(14);
				else if (Validate.notNull(kingdom.getKing()))
					if (kingdom.getKing().getUniqueId().equals(target.getUniqueId()))
						demoteButton(14);
			}
			moveToButton(16);
		}else{
			if (Validate.notNull(wrapper.isInKingdom(target.getWorld()))){
				if (viewer.equals(target)) 
					leaveButton(13);
			}else
				if (viewer.equals(target)) 
					joinButton(13);
		}
	}
	
	private void scoreboardButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			switch(wrapper.getBoardType()){ //Make it possible to switch between more scoreboards
				/*case TRAPBOARD: // if PlayerBoard witch to next in line
					new TrapBoard(p);
					wrapper.setBoardType(BoardType.PLAYERBOARD);
					break;*/
				case PLAYERBOARD: // if PlayerBoard witch to next in line
					new KingdomBoard(target);
					wrapper.setBoardType(BoardType.KINGDOMBOARD);
					break;
				case KINGDOMBOARD:// if KingdomBoard witch to next in line
				default:
					new PlayerBoard(target);
					wrapper.setBoardType(BoardType.PLAYERBOARD); //When TrapBoard Implemented Change to it
					break;
					
			}
		}, "&2Switch your Scoreboard", 
				"\n&6Scoreboards:"
						+ "\n &6- &3KingdomBoard &6(Default)"
						+ "\n &6- &3PlayerBoard"
						+ "\n &6&m- &3&mTrapBoard"
						+ "\n"
						+ "\n&bClick to Switch"
				);
	}
	
	private void joinButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			new KingdomGUI(target, this);
		}, "&2Join a kingdom", 
				"\n&c");
	}
	
	private void leaveButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			Cach.StaticKingdom = kingdom;
			new Message(target, MessageType.CHAT, "{LeaveSuccess}");
			kingdom.leave(target);
			display();
		}, "&4Leave " + kingdom.getColor() + kingdom.getName(), 
				"\n&c\n");
	}
	
	/**
	 * Rank up in the Kingdom Hierchy
	 */
	private void promoteButton(int slot){
		setItem(slot, new ItemStack(Material.GOLD_INGOT), player -> {
			
		}, "&2Promote &f" + target.getDisplayName(), 
				"&cComing Soon");
		//Promote Count/Countess 		-> 	King/Queen
		//Promote Baron/Baronesss 		-> 	Count/Countess
		//Promote Knight						->	Baron/Baronesss
		//Promote Squire						->	Knight
	}
	
	/**
	 * Rank down in the Kingdom Hierchy
	 */
	private void demoteButton(int slot){
		setItem(slot, new ItemStack(Material.IRON_INGOT), player -> {
			
		}, "&4Demote &f" + target.getDisplayName(), 
				"&cComing Soon");
		//Demote King/Queen				-> 	Count/Countess
		//Demote Count/Countess 		-> 	Baron/Baroness
		//Demote Baron/Baronesss		->	Knight
		//Demote Knight						->	Squire
	}
	
	private void kickButton(int slot){
		setItem(slot, new ItemStack(Material.GOLD_AXE), player -> {
			Cach.StaticKingdom = kingdom;
			Cach.StaticPlayer = target;
			new Message(viewer, MessageType.CHAT, "{AdminRemoveSuccess}");
			Cach.StaticPlayer = viewer;
			new Message(target, MessageType.CHAT, "{RemoveSuccess}");
			kingdom.leave(target);
			display();
		}, "&4Kick " +target.getDisplayName() + " from " + kingdom.getColor() + kingdom.getName(), 
				"\n&c\n");
	}
	
	private OwnerGUI ownerGui;
	private void moveToButton(int slot){
		setItem(slot, new ItemStack(Material.EYE_OF_ENDER), player -> {
			ownerGui = new OwnerGUI(player, this);
		}, "&4Move to a diffrent kingdom",
				"&dOBS! &3This will change target\'s Kingdom"
						+ "\n&c &3This will also teleport target to new kingdom");
		
		if (Validate.notNull(ownerGui)){
			Cach.StaticKingdom = ownerGui.get();
			Cach.StaticPlayer = viewer;
			new Message(target, MessageType.CHAT, "MoveSuccess");
			Cach.StaticPlayer = target;
			new Message(target, MessageType.CHAT, "AdminMoveSuccess");
			kingdom.leave(target);
			ownerGui.get().join(target);
			ownerGui = null;
		}
		
	}
	
	private void infoIcon(){
	}
}
