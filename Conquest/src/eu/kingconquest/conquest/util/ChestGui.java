package eu.kingconquest.conquest.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.gui.HomeGUI;

public abstract class ChestGui extends Pagination{
	private int invSize = 9;
	private String title = " ";
    private UUID uniqueID;
	private Inventory inventory;
    private ClickType clickType;
    private Map<Integer, onGuiAction> actions;
    public static Map<UUID, ChestGui> inventoriesByUUID = new HashMap<>();
    public static Map<UUID, UUID> openInventories = new HashMap<>();
	private static Map<UUID, Integer> taskID = new HashMap<>();
	private String ToolTip;
	
	/*
	 * ClickTypes
	 * Place in setItem->{ }
	 * if (getClickType().equals(ClickType.LEFT))
		player.sendMessage("LEFT");
	}*/
	
    /**
     * Greate a Chest GUI
     * @param invSize - int
     * @param invName - String
     */
	public ChestGui(){
		super();
	}

	public abstract void create();
	public void create(int invSize) {
		create(invSize, getTitle());
	}
	public void create(int invSize, String invName) {
		//Create the ChestGui
		setChestSlots(invSize);
        generateUUID();
        inventory = Bukkit.createInventory(null, this.invSize, this.title);

        actions = new HashMap<>();
        inventoriesByUUID.put(getUuid(), this);
	}
	
	public abstract void display();
	public Inventory getInventory(){
		return inventory;
	}
	public interface onGuiAction{
        void onClick(Player p);
    }

	private static int oldTaskID = 0;
	public void createGui(Player p, String str, Integer items ){
		if (Validate.notNull(items))
			setItems(items);
		if (Validate.notNull(str))
			setTitle(str);
		
		if (Validate.notNull(taskID.get(p.getUniqueId())))
				oldTaskID = taskID.get(p.getUniqueId()); // Close any old Tasks of the player (Previous ChestGui)
		
		taskID.put(p.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
            	open(p);
        		Bukkit.getScheduler().cancelTask(oldTaskID);
            }
        }.runTaskLater(Main.getInstance(), 1).getTaskId());
		create(getItems());
		clearSlots();
	}
	
	public void open(Player p){
        p.openInventory(inventory);
        openInventories.put(p.getUniqueId(), getUuid());
    }
	public void close(Player p){
		UUID u = openInventories.get(p.getUniqueId());
        if (u.equals(getUuid())){
        	p.closeInventory();
        }
        inventoriesByUUID.remove(getUuid());
    }
	
	public void playerInfo(Player p){
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(p.getName());
		head.setItemMeta(skull);
        setSkullItem(0, head, player ->{
        	p.sendMessage(p.getDisplayName());
        }, "§4" + p.getName() + " Information","§1-----------------"
        		+ "\n§4\n"
        		);
	}
	public void homeButton(){
		setItem(1, new ItemStack(Material.BARRIER), player -> {
			close(player);
			HomeGUI homeGui = new HomeGUI(player);
			homeGui.create();
		}, "§4Home","§1-----------------\n"
				+ "§cClick to goto Home Gui");
	}
	public void backButton(Object object){
		ChestGui chestGui = (ChestGui) object;
		setItem(8, new ItemStack(Material.ARROW), player -> {
			chestGui.create();
		}, "§4<< Back","§1-----------------\n"
				+ "§cClick to go Home");
	}
	public void closeButton(){
        setItem(8, new ItemStack(Material.BARRIER), player -> {
            close(player);
        }, "§4Close!","§1-----------------"
        		+ "\n§cClick to close!\n"
        		);
	}
	public void clearSlots(){
		getInventory().clear();
		//for(int i = 0; i < this.invSize; i++) 
			//setItem(i, new ItemStack(Material.AIR), player -> {},null, null);
	}
	
//Getters
	public UUID getUuid(){
		return uniqueID;
	}
	public String getTitle(){
		return this.title;
	}
	public static Map<UUID, ChestGui> getInventoriesByUUID() {
        return inventoriesByUUID;
    }
    public static Map<UUID, UUID> getOpenInventories() {
        return openInventories;
    }
    public Map<Integer, onGuiAction> getActions() {
        return actions;
    }
    public ClickType getClickType(){
		return clickType;
    }
    public int getSlotSize(){
		return invSize;
	}
	public int getCorrectSlots(int i) {
    	if (i <= 9) {
    		return 18;
    	}else if (i <= 18)
    		return 27;
    	else if (i <= 27)
    		return 36;
    	else if ( i <= 36)
    		return 45;
    	else if (i <= 45)
    		return 54;
    	else if (i > 45)
    		return 54;
		return 9;
	}
	
//Setters
    public void setClickType(ClickType type){
		clickType = type;
    }
    public void setChestSlots(int i){
		this.invSize = getCorrectSlots(i);
	}
	public void setItem(int slot, ItemStack stack, onGuiAction action, String itemName, String toolTip){
		ToolTip = toolTip;
        ItemMeta meta = stack.getItemMeta();
		
		if (Validate.notNull(itemName)){
			itemName = new StringBuilder(itemName).insert(2, "&l").toString(); // Make every title bold
			meta.setDisplayName(ChatManager.Format(itemName));
		}
		if (Validate.notNull(ToolTip)) {
			String[] temp = ChatManager.Format(ToolTip).split("\n");
			for (int i = 0; i < temp.length; i++) {
				temp[i] = temp[i].replace("\n", "");
			}
			meta.setLore(Arrays.asList(temp));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			stack.setItemMeta(meta);
		}
		inventory.setItem(slot, stack);
        if (Validate.notNull(action))
            actions.put(slot, action);
    }
	public void setSkullItem(int slot, ItemStack stack, onGuiAction action, String itemName, String toolTip){
		ToolTip = toolTip;
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		if (Validate.notNull(itemName)){
			itemName = new StringBuilder(itemName).insert(2, "&l").toString(); // Make every title bold
			meta.setDisplayName(ChatManager.Format(itemName));
		}
		if (Validate.notNull(meta.getDisplayName()))
		if (ToolTip != null)  {
			String[] temp = ChatManager.Format(ToolTip).split("\n");
			for (int i = 0; i < temp.length; i++) {
				temp[i] = temp[i].replace("\n", "");
			}
			meta.setLore(Arrays.asList(temp));
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			stack.setItemMeta(meta);
		}
		inventory.setItem(slot, stack);
        if (action != null)
            actions.put(slot, action);
    }
	public void setTitle(String title){
		this.title = ChatManager.Format(title);
	}

//Generator
	public void generateUUID(){
		uniqueID = UUID.randomUUID();
	}
}
