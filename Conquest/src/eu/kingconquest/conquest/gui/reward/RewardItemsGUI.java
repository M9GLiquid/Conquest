package eu.kingconquest.conquest.gui.reward;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.util.Validate;

public class RewardItemsGUI extends ChestGui{
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	private Player player;
	private ChestGui previous;
	private Reward reward;
	
	public RewardItemsGUI(Player player, ChestGui previous, Reward reward){
		this.previous = previous;
		this.player = player;
		this.reward = reward;
		
		create();
	}
	public RewardItemsGUI(Player player, ChestGui previous, ArrayList<ItemStack> items){
		this.previous = previous;
		this.player = player;
		this.items = items;
		
		create();
	}

	@Override
	public void create(){
		createGui(
				player
				, "Item View GUI"
				, (Validate.notNull(reward) ? reward.getItems().size() : items.size())
				);
	}

	@Override
	public void display(){
		setCurrentItem(0);
		clearSlots();
		
		playerInfo(player);
		previous(previous);
		backButton(previous);
		next(previous);

		for(int slot = 9; slot < 54; slot++) {
			if (getCurrentItem() > (Validate.notNull(reward) ? getItems() - 1 : items.size() - 1) || getItems() < 1)
				break;
			if (Validate.notNull(reward))
				itemButton(slot, reward.getItem(getCurrentItem()));
			else
				itemsEditButton(slot, items.get(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
	}
	private void itemButton(int slot, ItemStack item){
		setItem(slot, item, player->{
		}, "", "");
	}
	private void itemsEditButton(int slot, ItemStack item){
		setItem(slot, item, player->{
			items.remove(item);
			display();
		}, "", ""
				+ "\n&4Warning! &7Cannot be undone!"
				+ "\n&3Click to &cRemove &3from list");
	}
}
