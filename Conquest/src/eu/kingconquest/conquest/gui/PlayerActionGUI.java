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
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class PlayerActionGUI extends ChestGui{
	private PlayerWrapper wrapper;
	private Kingdom kingdom;
	private ChestGui previous;
	private Player target;
	private Player player;

	public PlayerActionGUI(Player player, Player targetPlayer, ChestGui previousGui){
		super();
		this.player = player;
		this.target = targetPlayer;
		this.previous = previousGui;
		wrapper = PlayerWrapper.getWrapper(target);
		if (wrapper.isInKingdom(target.getWorld()))
			kingdom = wrapper.getKingdom(target.getWorld());
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Player", 54);
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
		if (Validate.hasPerm(player, ".admin.edit.player.*")){
			if (Validate.notNull(wrapper.isInKingdom(target.getWorld()))){
				if (!player.equals(target)) 
					kickButton(10);
				if (Validate.isNull(kingdom.getKing()))
					promoteButton(12);
				else if (kingdom.getKing().getUniqueId().equals(target.getUniqueId()))
					demoteButton(12);
			}
			moveToButton(14);
		}else{
			scoreboardButton(10);
			if (Validate.notNull(wrapper.isInKingdom(target.getWorld()))){
				if (player.equals(target)) 
					leaveButton(13);
			}else
				if (player.equals(target)) 
					joinButton(13);
		}
	}

	private void scoreboardButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
			switch(wrapper.getBoardType()){ //Make it possible to switch between more scoreboards
				/*case TRAPBOARD: // if PlayerBoard witch to next in line
					new TrapBoard(player);
					wrapper.setBoardType(BoardType.PLAYERBOARD);
					break;*/
				case PLAYERBOARD: // if PlayerBoard witch to next in line
					new KingdomBoard(player);
					wrapper.setBoardType(BoardType.KINGDOMBOARD);
					break;
				case KINGDOMBOARD:// if KingdomBoard witch to next in line
				default:
					new PlayerBoard(player);
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
			new KingdomGUI(player, this);
		}, "&2Join a kingdom", 
				"\n&c");
	}

	private void leaveButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			Cach.StaticKingdom = kingdom;
			new Message(target, MessageType.CHAT, "{LeaveSuccess}");
			kingdom.leave(target);
			display();
		}, "&4Leave " + kingdom.getColorSymbol() + kingdom.getName(), 
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
			Cach.StaticPlayer = target;
			Cach.StaticKingdom = kingdom;
			new Message(player, MessageType.CHAT, "{AdminMoveSuccess}");
			Cach.StaticPlayer = player;
			new Message(player, MessageType.CHAT, "{RemoveSuccess}");
			kingdom.leave(target);
			display();
		}, "&4Kick " +target.getDisplayName() + " from " + kingdom.getColorSymbol() + kingdom.getName(), 
				"\n&c\n");
	}


	private void moveToButton(int slot){
		setItem(slot, new ItemStack(Material.EYE_OF_ENDER), player -> {
		}, "&4Move to diffrent kingdom",
				"&dOBS! &3This will teleport player to new kingdom"
				+ "\n&cComing Soon");
	}

	private void infoIcon(){
	}
}
