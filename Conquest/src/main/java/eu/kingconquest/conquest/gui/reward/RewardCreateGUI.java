package eu.kingconquest.conquest.gui.reward;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.gui.objective.ParentGUI;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class RewardCreateGUI extends ChestGui{
    private ArrayList<ItemStack> items = new ArrayList<>();
	private ItemStack item = new ItemStack(Material.ARMOR_STAND);
	private long cooldown = 0;
	private ChestGui previous;
	private String name = "";
	private Objective parent;
	private long cost = 0;
	private Player player;

	public RewardCreateGUI(Player player, ChestGui previous, ArrayList<ItemStack> items){
		super();
		if (Validate.notNull(items)){
			this.items = items;
		}
		this.previous = previous;
		this.player = player;
		
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Reward Create Gui", 54);
		display();
	}

	@Override
	public void display(){
		init();
		//Slot 0
		playerInfo(player);
		//Slot 3
		next(this);
		//Slot 4
		displayInfo();
		//Slot 5
		previous(this);

		if (cost > 0. 
				&& cooldown > 0
                && !Objects.equals(name, "")
				&& Validate.notNull(parent))
			saveButton();
		else
			backButton(previous);

		//Name
		nameButton(13);

		//Cost
		DecreaseCostButton(19, -100);
		DecreaseCostButton(20, -10);
		DecreaseCostButton(21, -1);
		costDisplayButton(22);
		IncreaseCostButton(23, 1);
		IncreaseCostButton(24, 10);
		IncreaseCostButton(25, 100);

		//Cooldown
		DecreaseCooldownButton(28, -100);
		DecreaseCooldownButton(29, -10);
		DecreaseCooldownButton(30, -1);
		cooldownDisplayButton(31);
		IncreaseCooldownButton(32, 1);
		IncreaseCooldownButton(33, 10);
		IncreaseCooldownButton(34, 100);

		//Owner
		parentButton(40);

		//Add ItemStacks
		DecreaseItemButton(47, -64);
		DecreaseItemButton(48, -1);
		itemButton(49);
		IncreaseItemButton(50, 1);
		IncreaseItemButton(51, 64);
	}

	private NamePrompt namePrompt = null;

    @SuppressWarnings("all")
    private void nameButton(int slot){
        if (Validate.notNull(namePrompt)){
            name = namePrompt.get();
            namePrompt = null;
            display();
        }

        setItem(slot, new ItemStack(Material.BOOK), player -> {
                    namePrompt = new NamePrompt(this);
                    new ChatInteract(player, namePrompt, "Cancel");
                    player.closeInventory();
                }, "&4Set Name!",
                "\n"
                        + "\n&bClick to set name");
    }

	private void DecreaseCooldownButton(int slot, int amount){
        setItem(slot, new ItemStack(Material.OAK_BUTTON), player -> {
			cooldown = cooldown + amount; // (+- = -)
			if (cooldown < 0)
				cooldown = 0;
			display();
		},"&3Decrease&6(&c " + amount +" &3minutes&6)" , 
				"&cClick to Decrease!"
				);
	}

    @SuppressWarnings("all")
    private void cooldownDisplayButton(int slot){
        setItem(slot, new ItemStack(Material.CLOCK), player -> {
                },"&3Cooldown: &6" + cooldown + " minutes" , ""
        );
    }
	private void IncreaseCooldownButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.STONE_BUTTON), player -> {
			cooldown = cooldown + amount;
			display();
		},"&3Increase&6(&c +" + amount +" &3minutes&6)" , 
				"&aClick to Increase!"
				);
	}

	private void DecreaseCostButton(int slot, int amount){
        setItem(slot, new ItemStack(Material.OAK_BUTTON), player -> {
			if ((cost + amount) >= 0)
				cost = cost + amount; // (+- = -)
			else
				cost = 0;
			display();
		},"&3Decrease&6(&c " + amount +"&6)" , 
				"&cClick to Decrease!"
				);
	}

    @SuppressWarnings("all")
    private void costDisplayButton(int slot){
        setItem(slot, new ItemStack(Material.GOLD_NUGGET), player -> {
                },"&3Cost: &6" + cost , ""
        );
    }
	private void IncreaseCostButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.STONE_BUTTON), player -> {
			cost = cost + amount;
			display();
		},"&3Increase&6(&c +" + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}

	private ParentGUI parentGui;

    @SuppressWarnings("all")
    private void parentButton(int slot){
        setItem(slot, new ItemStack(Material.BEACON), player ->
                        parentGui = new ParentGUI(player, null, this), "&3Select Parent",
                (Validate.notNull(parent) ? "&7Parent: &3" + parent.getName() + "\n" : "")
                        + "&aClick to Select!"
        );

        if (Validate.notNull(parentGui)){
            parent = parentGui.get();
            parentGui.close(player);
            parentGui = null;
            display();
        }
    }

	private void init(){
		clearSlots();
		setCurrentItem(0);
	}

	private void displayInfo(){
		setItem(4, new ItemStack(Material.PAPER), player -> {
		},"&6Kit Information" , 
				"&7Name: &3" + name
				+ "\n&7Owner: &3" + (Validate.notNull(parent) ? parent.getName(): "")
				+ "\n&7Cost: &3" + cost
				+ "\n&7Cooldown: &3" + cooldown
				+ "\n&7Items &3" + items.size()
				);
	}

	private void saveButton(){
		setItem(8, new ItemStack(Material.EMERALD_BLOCK), player -> {
			Reward reward = new Reward(name, player.getWorld(), cost, cooldown, parent.getUUID());
			reward.addItems(0, items);
			new RewardGUI(player, null);
			close(player);
		},"&aSave" , "");
	}

	private void DecreaseItemButton(int slot, int amount){
        setItem(slot, new ItemStack(Material.OAK_BUTTON), player -> {
			if ((cost - amount) >= 0)
				item.setAmount(item.getAmount() + amount);
			else
				item.setAmount(0);
			display();
		},"&3Decrease&6(&c" + amount +"&6)" , 
				"&cClick to Decrease!"
				);
	}

    @SuppressWarnings("all")
    private void itemButton(int slot){ //shift + click add item to Kit (Add to Slot and on press Save)
        if (getInventoryItems().containsKey(InventoryType.PLAYER))
            item = getInventoryItems().get(InventoryType.PLAYER);
        else
            item  = new ItemStack(Material.ARMOR_STAND);


        setItem(slot, item, player -> {
                    if (getClickType().equals(ClickType.LEFT)){
                        items.add(item.clone());
                    }else if (getClickType().equals(ClickType.RIGHT))
                        new RewardItemsGUI(player, this, items);
                    display();
                }, "",
                "\n&bClick item in your inventory, then..."
                        +"\n&bLeft-Click this to &aSave &3item to Reward Box or..."
                        +"\n&bRight-Click this to &dSee &3items selected");
    }
	private void IncreaseItemButton(int slot, int amount){
		setItem(slot,  new ItemStack(Material.STONE_BUTTON), player -> {
			item.setAmount(item.getAmount() + amount);
			display();
		},"&3Increase&6(&c+" + amount +"&6)" , 
				"&aClick to Increase!"
				);
	}
}
