package eu.kingconquest.conquest.gui;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

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

        private ArrayList<Player> targets = new ArrayList<>();
	@Override
	public void create(){
		targets.clear();
			if (objective instanceof Kingdom){
                ((Kingdom) objective).getMembers().forEach(uuid ->
                        targets.add(Bukkit.getPlayer(uuid)));
			}else{
                targets.addAll(Bukkit.getOnlinePlayers());
			}
		createGui(player, "&6Player", targets.size());
		display();
	}

	@Override
	public void display(){
		setCurrentItem(0);
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
        setItem(6, new ItemStack(Material.REDSTONE_BLOCK), player ->
                        ((Kingdom) objective).clearMembers(), "�4Clear Members", "\n�cClick to clear a Kingdoms Members"
				+ "\n"
				);
	}

	private void playerButton(Player targetPlayer, int slot){

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
        skull.setOwningPlayer(targetPlayer);
		head.setItemMeta(skull);
        setSkullItem(slot, head, player ->
                        new PlayerActionGUI(player, targetPlayer, this), "�4" + targetPlayer.getName(),
                "\ncClick to get into Player specific GUI"
				+ "\n"
				);
        setCurrentItem(getCurrentItem() + 1);
	}
}
