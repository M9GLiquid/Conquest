package eu.kingconquest.conquest.gui.reward;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class RewardGUI extends ChestGui{
	private ChestGui previous;
	private Player player;
	
	public RewardGUI(Player player, ChestGui previous){
		super();
		this.previous = previous;
		this.player = player;
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Reward Gui", Reward.getRewards(player.getWorld()).size());
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
		//Slot 7
		if (Validate.hasPerm(player, ".admin.kit"))
			createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int slot = 9; slot < 54; slot++) {
			if (getCurrentItem() > (getItems() -1) || getItems() == 0)
				break;
			if (Validate.hasPerm(player, ".admin.kit"))
				editButton(slot, Reward.getRewards(player.getWorld()).get(getCurrentItem()));
			else
				buyButton(slot, Reward.getRewards(player.getWorld()).get(getCurrentItem()));
			setCurrentItem(getCurrentItem() + 1);
		}
		
	}

	private void buyButton(int slot, Reward reward){
		setItem(slot, new ItemStack(Material.CHEST), player -> {
			if (getClickType().equals(ClickType.RIGHT)){ // Buy
				ItemStack[] items = player.getInventory().getContents();
				if (reward.getItems().size() > (player.getInventory().getSize() - items.length))
					new Message(player, MessageType.CHAT, "{NoInventoryRoom}");
				Vault.econ.withdrawPlayer(player, reward.getCost());
				reward.getItems().forEach((i, item)->{
					player.getInventory().addItem(item);
					player.getInventory().firstEmpty();
				});
				close(player);
			}else if (getClickType().equals(ClickType.LEFT)){ // View
				new RewardItemsGUI(player, this, reward);
			}
		},"&6Kit Information" , 
				"&7Name: &3" + reward.getName()
				+ "\n&7Owner: &3" + (Validate.notNull(reward.getOwner()) ? reward.getOwner().getName(): "")
				+ "\n&7Cost: &3" + reward.getCost()
				+ "\n&7Cooldown: &3" + reward.getCooldown()
				+ "\n"
				+ "\n&3Right-Click to Buy"
				+"\n&3Left-Click to View"
				);
	}

	private void editButton(int slot, Reward kit){
		setItem(slot, new ItemStack(Material.CHEST), player -> {
			if (getClickType().equals(ClickType.SHIFT_LEFT)){
				Reward.removeReward(kit);
				previous.create();
				close(player);
			}else{
				new RewardEditGUI(player, this, kit);
			}
		},"&6Kit Information" , 
				"&7Name: &3" + kit.getName()
				+ "\n&7Owner: &3" + (Validate.notNull(kit.getOwner()) ? kit.getOwner().getName(): "None")
				+ "\n&7Cost: &3" + kit.getCost()
				+ "\n&7Cooldown: &3" + kit.getCooldown()
				+ "\n"
				+"\n&bClick to Edit"
				+ "\n&bShift-Right Click to &4Remove (Cannot be undone)"
				);
	}

	private void createButton(){
		setItem(7, new ItemStack(Material.ENDER_CHEST), player -> {
			new RewardCreateGUI(player, this, null);
		},"&3Create New Kit", 
				"");
	}

}
