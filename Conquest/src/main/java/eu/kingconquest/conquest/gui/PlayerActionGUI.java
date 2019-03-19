package eu.kingconquest.conquest.gui;

import eu.kingconquest.conquest.Scoreboard.BoardType;
import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.core.ActiveWorld;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.gui.objective.KingdomGUI;
import eu.kingconquest.conquest.gui.objective.OwnerGUI;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerActionGUI extends ChestGui{
	private PlayerWrapper wrapper;
	private Kingdom kingdom;
	private ChestGui previous;
    private Player targetPlayer;
	private Player viewer;
    private ActiveWorld activeWorld;
	
	public PlayerActionGUI(Player viewingPlayer, Player targetPlayer, ChestGui previousGui){
		super();
		this.viewer = viewingPlayer;
        this.targetPlayer = (Validate.notNull(targetPlayer) ? targetPlayer : viewingPlayer);
		this.previous = previousGui;
        activeWorld = ActiveWorld.getActiveWorld(targetPlayer.getWorld());
        wrapper = PlayerWrapper.getWrapper(targetPlayer);
        if (wrapper.isInKingdom(activeWorld))
            kingdom = wrapper.getKingdom(activeWorld);
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
        playerInfo(targetPlayer);
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
            if (Validate.notNull(wrapper.isInKingdom(activeWorld))) {
				scoreboardButton(10);
                if (!viewer.equals(targetPlayer))
					kickButton(12);
				if (Validate.isNull(kingdom.getKing()))
					promoteButton(14);
				else if (Validate.notNull(kingdom.getKing()))
                    if (kingdom.getKing().getUniqueId().equals(targetPlayer.getUniqueId()))
						demoteButton(14);
			}
			moveToButton(16);
		}else{
            if (Validate.notNull(wrapper.isInKingdom(activeWorld))) {
                if (viewer.equals(targetPlayer) && Validate.hasPerm(targetPlayer, ".basic.leave"))
					leaveButton(13);
            } else if (viewer.equals(targetPlayer) && Validate.hasPerm(targetPlayer, ".basic.join"))
					joinButton(13);
		}
	}

    @SuppressWarnings("all")
	private void scoreboardButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			switch(wrapper.getBoardType()){ //Make it possible to switch between more scoreboards
				/*case TRAPBOARD: // if PlayerBoard witch to next in line
					new TrapBoard(p);
					wrapper.setBoardType(BoardType.PLAYERBOARD);
					break;*/
				case PLAYERBOARD: // if PlayerBoard witch to next in line
                    new KingdomBoard(targetPlayer);
					wrapper.setBoardType(BoardType.KINGDOMBOARD);
					break;
				case KINGDOMBOARD:// if KingdomBoard witch to next in line
				default:
                    new PlayerBoard(targetPlayer);
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

    @SuppressWarnings("all")
	private void joinButton(int slot){
        setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player ->
                        new KingdomGUI(targetPlayer, this), "&2Join a kingdom",
				"\n&c");
	}

    @SuppressWarnings("all")
	private void leaveButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			Cach.StaticKingdom = kingdom;
                    new Message(targetPlayer, MessageType.CHAT, "{LeaveSuccess}");
                    kingdom.leave(targetPlayer);
			display();
		}, "&4Leave " + kingdom.getColor() + kingdom.getName(), 
				"\n&c\n");
	}
	
	/**
	 * Rank up in the Kingdom Hierchy
	 */
    @SuppressWarnings("all")
    private void promoteButton(int slot){
		setItem(slot, new ItemStack(Material.GOLD_INGOT), player -> {

                }, "&2Promote &f" + targetPlayer.getDisplayName(),
				"&cComing Soon");
		//Promote Count/Countess 		-> 	King/Queen
		//Promote Baron/Baronesss 		-> 	Count/Countess
		//Promote Knight						->	Baron/Baronesss
		//Promote Squire						->	Knight
	}
	
	/**
	 * Rank down in the Kingdom Hierchy
	 */
    @SuppressWarnings("all")
    private void demoteButton(int slot){
		setItem(slot, new ItemStack(Material.IRON_INGOT), player -> {

                }, "&4Demote &f" + targetPlayer.getDisplayName(),
				"&cComing Soon");
		//Demote King/Queen				-> 	Count/Countess
		//Demote Count/Countess 		-> 	Baron/Baroness
		//Demote Baron/Baronesss		->	Knight
		//Demote Knight						->	Squire
	}

    @SuppressWarnings("all")
	private void kickButton(int slot){
        setItem(slot, new ItemStack(Material.GOLDEN_AXE), player -> {
			Cach.StaticKingdom = kingdom;
                    Cach.StaticPlayer = targetPlayer;
			new Message(viewer, MessageType.CHAT, "{AdminRemoveSuccess}");
			Cach.StaticPlayer = viewer;
                    new Message(targetPlayer, MessageType.CHAT, "{RemoveSuccess}");
                    kingdom.leave(targetPlayer);
			display();
                }, "&4Kick " + targetPlayer.getDisplayName() + " from " + kingdom.getColor() + kingdom.getName(),
				"\n&c\n");
	}
	
	private OwnerGUI ownerGui;

    @SuppressWarnings("all")
    private void moveToButton(int slot){
        setItem(slot, new ItemStack(Material.ENDER_EYE), player ->
                        ownerGui = new OwnerGUI(player, this), "&4Move to a diffrent kingdom",
                "&dOBS! &3This will change target\'s Kingdom"
                        + "\n&c &3This will also teleport target to new kingdom");

        if (Validate.notNull(ownerGui)){
            Cach.StaticKingdom = ownerGui.get();
            Cach.StaticPlayer = viewer;
            new Message(targetPlayer, MessageType.CHAT, "MoveSuccess");
            Cach.StaticPlayer = targetPlayer;
            new Message(targetPlayer, MessageType.CHAT, "AdminMoveSuccess");
            kingdom.leave(targetPlayer);
            ownerGui.get().join(targetPlayer);
            ownerGui = null;
        }

    }
	
	private void infoIcon(){
	}
}
