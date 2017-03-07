package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.gui.HomeGUI;
import eu.kingconquest.conquest.gui.PlayerActionGUI;
import eu.kingconquest.conquest.gui.util.Pagination;
import eu.kingconquest.conquest.hook.TNEApi;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.Validate;

public abstract class ChestGui extends Pagination{
	public static Map<UUID, ChestGui> 			inventoriesByUUID = new HashMap<>();
	public static Map<UUID, UUID> 					openInventories = new HashMap<>();
	private Map<InventoryType, ItemStack> 	inventoryItems = new HashMap<>();
	private static Map<UUID, Integer> 				taskID = new HashMap<>();
	private Map<Integer, onGuiAction> 			actions = new HashMap<>();
	private boolean 											isPlayerInventory = false;
	private boolean 											itemFlag = false;
	private Inventory 											inventory;
	private String 												title = " ";
	private UUID 													uniqueID;
	private ClickType 											type;
	private int 														invSize = 9;

	/**
	 * Greate a Chest GUI
	 * @param invSize - int
	 * @param invName - String
	 */
	public ChestGui(){
	}

	//Create the Inventory
	public abstract void create();
	public void create(int invSize) {
		create(invSize, getTitle());
	}
	public void create(int invSize, String invName) {
		inventory = Bukkit.createInventory(null, getCorrectSlotSize(invSize), Message.getMessage(invName));
		inventoriesByUUID.put(generateUUID(), this);
	}

	public abstract void display();
	public Inventory getInventory(){
		return inventory;
	}
	public interface onGuiAction{
		void onClick(Player p);
	}

	private static int oldTaskID = 0;
	public void createGui(Player player, String str, Integer items ){
		setItems(items);

		if (Validate.notNull(taskID.get(player.getUniqueId())))
			oldTaskID = taskID.get(player.getUniqueId()); // Close any old Tasks of the player (Previous ChestGui)

		taskID.put(player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				open(player);
				Bukkit.getScheduler().cancelTask(oldTaskID);
			}
		}.runTaskLater(Main.getInstance(), 1).getTaskId());
		create(getItems(), str);
		clearSlots();
		
		display();
	}

	public void open(Player player){
		player.openInventory(inventory);
		openInventories.put(player.getUniqueId(), getUuid());
	}
	public void close(Player player){
		UUID u = openInventories.get(player.getUniqueId());
		if (u.equals(getUuid())){
			player.closeInventory();
		}
		inventoriesByUUID.remove(getUuid());
	}

	public void playerInfo(Player player){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(player.getName());
		head.setItemMeta(skull);
		setSkullItem(0, head, p ->{
			new PlayerActionGUI(p, p, this);
		}, "&6" + player.getName() + " Information",
				"&7ScoreBoard: &3" + (Validate.notNull(wrapper.getBoardType()) ? wrapper.getBoardType().getName() : "" )
				+ "\n&7Kingdom: &3" + (wrapper.isInKingdom(player.getWorld()) ? wrapper.getKingdom(player.getWorld()).getName() : "None")
				+ "\n&7Money: &6" + TNEApi.getBalance(player)
				+ "\n&7Friends : &a" + wrapper.getOnlineFriends() + "&6/&2" + wrapper.getNumberOfFriends() + " &aOnline"
				+"\n"
				+ "\n&bClick for Settings"
				);
	}
	public void homeButton(){
		setItem(1, new ItemStack(Material.BARRIER), player -> {
			close(player);
			HomeGUI homeGui = new HomeGUI(player);
			homeGui.create();
		}, "&4Home",
				"\n&bClick to go &cHome");
	}
	public void backButton(ChestGui chestGui){
		if (Validate.isNull(chestGui))
			return;
		setItem(8, new ItemStack(Material.ARROW), player -> {
			chestGui.create();
		}, "&4<< Back",
				"\n&bClick to go &cBack");
	}
	public void closeButton(){
		setItem(8, new ItemStack(Material.BARRIER), player -> {
			close(player);
		}, "&4Close!",
				"\n&bClick to &cclose!"
				);
	}
	public void clearSlots(){
		getInventory().clear();
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
	public int getSlotSize(){
		return invSize;
	}
	public int getCorrectSlotSize(int i) {
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
	public void toggleItemFlag(){
		itemFlag = !itemFlag;
	}
	public ClickType getClickType(){
		return type;
	}

	//Setters
	public void setClickType(ClickType type){
		this.type = type;
	}	
	public void setItem(int slot, ItemStack item, onGuiAction action, String title, String lore){
		if (Validate.isNull(item))
			return;
		
		ItemStack stack = item.clone();
		ItemMeta meta = stack.getItemMeta();
		if (Validate.notNull(meta)){ //Material that does not have a Item Meta (Example: Air)
			if (Validate.notNull(title) && title != ""){
				title = new StringBuilder(title).insert(2, "&l").toString(); // Make every title bold
				meta.setDisplayName(Message.getMessage(title));
			}
	
			List<String> temp = new ArrayList<String>();
			if (Validate.notNull(lore) && lore != ""){
				temp.add(Message.getMessage("&1-----------------"));
				if (meta.hasLore()) 
					meta.getLore().forEach(tempLore->{ temp.add(Message.getMessage(tempLore)); });
				String[] a = lore.split("\n");
				for (int i= 0; i < a.length; i++)
					temp.add(Message.getMessage(a[i]));
				meta.setLore(temp);
			}
			if (!itemFlag)
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			stack.setItemMeta(meta);
		}
		inventory.setItem(slot, stack);
		
		if (Validate.notNull(action))
			actions.put(slot, action);
	}
	public void setSkullItem(int slot, ItemStack item, onGuiAction action, String title, String lore){
		ItemStack stack = item.clone();
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		if (Validate.notNull(meta)){ //Material that does not have a Item Meta (Example: Air)
			if (Validate.notNull(title) && title != ""){
				title = new StringBuilder(title).insert(2, "&l").toString(); // Make every title bold
				meta.setDisplayName(Message.getMessage(title));
			}
	
			List<String> temp = new ArrayList<String>();
			if (Validate.notNull(lore) && lore != ""){
				temp.add(Message.getMessage("&1-----------------"));
				if (meta.hasLore()) 
					meta.getLore().forEach(tempLore->{ temp.add(Message.getMessage(tempLore)); });
				String[] a = lore.split("\n");
				for (int i= 0; i < a.length; i++)
					temp.add(Message.getMessage(a[i]));
				meta.setLore(temp);
			}
			if (!itemFlag)
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				stack.setItemMeta(meta);
			}
		inventory.setItem(slot, stack);
		if (action != null)
			actions.put(slot, action);
	}
	public void setTitle(String title){
		this.title = Message.getMessage(title);
	}
	public void setStackSize(Player player){
		player.updateInventory();
	}
	
	//Generator
	public UUID generateUUID(){
		uniqueID = UUID.randomUUID();
		return uniqueID;
	}

	public Map<InventoryType, ItemStack> getInventoryItems(){
		return inventoryItems;
	}

	public void setInventoryItem(InventoryType type, ItemStack item){
		if (inventoryItems.size() > 2)
			clearInventoryItems();
		this.inventoryItems.put(type, item.clone());
	}

	public void clearInventoryItems(){
		inventoryItems.clear();
	}

	
	public boolean isPlayerInventory(){
		return isPlayerInventory;
	}
	public void setPlayerInventory(boolean b){
		this.isPlayerInventory = b;
	}
}
