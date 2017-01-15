package eu.kingconquest.conquest.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.gui.HomeGUI;
import eu.kingconquest.conquest.hook.TNEApi;

public abstract class ChestGui extends Pagination{
	public static Map<UUID, ChestGui> inventoriesByUUID = new HashMap<>();
	public static Map<UUID, UUID> openInventories = new HashMap<>();
	private static Map<UUID, Integer> taskID = new HashMap<>();
	private Map<Integer, onGuiAction> actions;
	private boolean itemFlag = false;
	private ClickType clickType;
	private Inventory inventory;
	private String title = " ";
	private UUID uniqueID;
	private String ToolTip;
	private int invSize = 9;

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
	public void createGui(Player player, String str, Integer items ){
		setItems(items);
		setTitle(str);

		if (Validate.notNull(taskID.get(player.getUniqueId())))
			oldTaskID = taskID.get(player.getUniqueId()); // Close any old Tasks of the player (Previous ChestGui)

		taskID.put(player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				open(player);
				Bukkit.getScheduler().cancelTask(oldTaskID);
			}
		}.runTaskLater(Main.getInstance(), 1).getTaskId());
		create(getItems());
		clearSlots();
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
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(player.getName());
		head.setItemMeta(skull);
		setSkullItem(0, head, p ->{
		}, "&4" + player.getName() + " Information",
				"\n&4Kingdom: " + (PlayerWrapper.getWrapper(player).isInKingdom(player.getWorld()) ? PlayerWrapper.getWrapper(player).getKingdom(player.getWorld()).getName() : "None")
				+ "\n&4Money: " + TNEApi.getBalance(player)
				);
	}
	public void homeButton(){
		setItem(1, new ItemStack(Material.BARRIER), player -> {
			close(player);
			HomeGUI homeGui = new HomeGUI(player);
			homeGui.create();
		}, "&4Home",
				"&cClick to goto Home Gui");
	}
	public void backButton(ChestGui chestGui){
		if (Validate.isNull(chestGui))
			return;
		setItem(8, new ItemStack(Material.ARROW), player -> {
			chestGui.create();
			openInventories.remove(player.getUniqueId());
		}, "&4<< Back",
				"\n&3Click to go &aHome");
	}
	public void closeButton(){
		setItem(8, new ItemStack(Material.BARRIER), player -> {
			close(player);
		}, "&4Close!",
				"\n&3Click to &cclose!"
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
	public void toggleItemFlag(){
		itemFlag = !itemFlag;
	}
	//Setters
	public void setClickType(ClickType type){
		clickType = type;
	}
	public void setChestSlots(int i){
		this.invSize = getCorrectSlots(i);
	}
	public void setItem(int slot, ItemStack item, onGuiAction action, String title, String lore){
		if (Validate.isNull(item))
			return;
		
		ItemStack stack = item.clone();
		ToolTip = lore;
		ItemMeta meta = stack.getItemMeta();
		if (Validate.notNull(meta)){ //Material that does not have a Item Meta (Example: Air)
			if (Validate.notNull(title) && title != ""){
				title = new StringBuilder(title).insert(2, "&l").toString(); // Make every title bold
				meta.setDisplayName(ChatManager.Format(title));
			}
	
			List<String> temp = new ArrayList<String>();
			if (Validate.notNull(lore) && lore != ""){
				temp.add(ChatManager.Format("&1-----------------"));
				if (meta.hasLore()) 
					meta.getLore().forEach(tempLore->{ temp.add(ChatManager.Format(tempLore)); });
				String[] a = lore.split("\n");
				for (int i= 0; i < a.length; i++)
					temp.add(ChatManager.Format(a[i]));
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
	public void setSkullItem(int slot, ItemStack item, onGuiAction action, String itemName, String toolTip){
		ItemStack stack = item.clone();
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
