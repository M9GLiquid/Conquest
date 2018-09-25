package eu.kingconquest.conquest.gui.reward;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RewardGUI extends ChestGui{
	private PlayerWrapper wrapper;
	private ChestGui previous;
	private Player player;
	private Integer[] cooldown;
	
	public RewardGUI(Player player, ChestGui previous){
		super();
		this.wrapper = PlayerWrapper.getWrapper(player);
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
            Reward reward = Reward.getRewards(player.getWorld()).get(getCurrentItem());
			if (Validate.hasPerm(player, ".admin.kit"))
				editButton(slot, reward);
			else if (reward.getParent().getOwner().equals(wrapper.getKingdom(player.getWorld())))
				buyButton(slot, reward);
			setCurrentItem(getCurrentItem() + 1);
		}
		
	}
	
	private void buyButton(int slot, Reward reward){
		if (Validate.notNull(wrapper.getRewardCooldown(reward)))
			cooldown =  Validate.getTime(wrapper.getRewardCooldown(reward));
		setItem(slot, new ItemStack(Material.CHEST), player -> {
			PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
			if (getClickType().equals(ClickType.RIGHT)){ // Buy
				if (wrapper.isRewardReady(reward)){
					int i = 0;
					for (ItemStack item : player.getInventory().getContents())
						if (Validate.isNull(item))
							i++;
					if (i < reward.getItems().size()){
						new Message(player, MessageType.CHAT, "{NoInventoryRoom}");
					}else{
						if (!Vault.econ.has(player, reward.getCost()))
							new Message(player, MessageType.CHAT, "{NotEnoughMoney}");
						
						Vault.econ.withdrawPlayer(player, reward.getCost());
                        reward.getItems().forEach((inventorySlot, item) ->
                                player.getInventory().addItem(item));
						wrapper.setRewardCooldown(reward);
						Cach.StaticReward = reward;
						new Message(player, MessageType.CHAT, "{RewardBought}");
					}
					display();
				}else{
					Cach.StaticCooldownLeft = cooldown;
					new Message(player, MessageType.CHAT, "{RewardNotReady}");
				}
			}else if (getClickType().equals(ClickType.LEFT)){ // View
				new RewardItemsGUI(player, this, reward);
			}
		},"&6Reward Information" , 
				"&7Name: &3" + reward.getName()
				+ "\n&7Parent: &3" + (Validate.notNull(reward.getParent()) ? reward.getParent().getOwner().getColor() + reward.getParent().getName(): "None")
				+ "\n&7Cost: &3" + reward.getCost()
				+ "\n&7Cooldown: &3" + reward.getCooldown()
				+ "\n&3" //+ wrapper.getRewardCooldown(reward)
				+ "\n&3" + (!wrapper.isRewardReady(reward) ? 
						"Buyable in: " 
						+ Double.valueOf(cooldown[0]).longValue() +"h, " 
						+ Double.valueOf(cooldown[1]).longValue() +"m, " 
						+ Double.valueOf(cooldown[2]).longValue() + "s" 
						: "Right-Click to Buy")
				+"\n&3Left-Click to View"
				);
		
	}
	
	private void editButton(int slot, Reward reward){
		setItem(slot, new ItemStack(Material.CHEST), player -> {
			if (getClickType().equals(ClickType.SHIFT_LEFT)){
				Reward.removeReward(reward);
				previous.create();
				close(player);
			}else{
				new RewardEditGUI(player, this, reward);
			}
		},"&6Reward Information" , 
				"&7Name: &3" + reward.getName()
				+ "\n&7Parent: &3" + (Validate.notNull(reward.getParent()) ? reward.getParent().getOwner().getColor() + reward.getParent().getName(): "None")
				+ "\n&7Cost: &3" + reward.getCost()
				+ "\n&7Cooldown: &3" + reward.getCooldown()
				+ "\n"
				+"\n&bClick to Edit"
				+ "\n&bShift-Right Click to &4Remove (Cannot be undone)"
				);
	}
	
	private void createButton(){
        setItem(7, new ItemStack(Material.ENDER_CHEST), player ->
                        new RewardCreateGUI(player, this, null), "&3Create New Reward",
				"");
	}
}
