package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class HomeGUI extends ChestGui{
	private Player player;

	public HomeGUI(Player p){
		super();
		this.player = p;
		
		create();
	}

	@Override
	public void create(){
		createGui(player, "&6Home", 9);
		display();
	}

	private int slot;
	@Override
	public void display(){
		clearSlots();
		slot = 9;
		str = "";
		
		//Slot 0
		playerInfo(player);

		//Slot 7
		aboutButton();
		
		//Slot 8
		closeButton();
		
		//Slot MAIN
		//Help Gui
		helpButton();
		

		if (Validate.hasPerm(player, ".admin.edit.player"))
			playerButton();
		
		//Friends Gui
		//friendsButton();
		
		if (PlayerWrapper.getWrapper(player).isInKingdom(player.getWorld())){
			spawnButton();
			if (Validate.hasPerm(player, ".basic.teleport"))
			conflictButton();
		}
		
		kingdomButton();
		
		if (Validate.hasPerm(player, ".admin")){
			townButton();
			villageButton();
			kitButton();
			if (Validate.hasPerm(player, ".admin.reset"))
			resetButton();
			reloadButton();
		}
	}

	private void reloadButton(){
		setItem(2, new ItemStack(Material.REDSTONE_LAMP_OFF), player -> {
			if (Config.loadDefault() && Config.loadLanguage()){
				ChatManager.Chat(player, "{plugin_prefix} &7Config.yml & Language.yml &aSuccessfully reloaded!");
				return;
			}
			ChatManager.Chat(player, "{plugin_prefix} &7Config.yml & Language.yml &cFailed to reload!");
		}, "&3Reload Config",
				"&6Affected Files: "
				+ "\n&7 - Language.yml"
				+ "\n&7 - Config.yml"
        		+ "\n"
				);
	}

	public String displayInfo(){
		String str = "";
		if (Validate.hasPerm(player, "admin.*")){
			str = "&7Manage Player Attributes:"
        		+ "\n&6- &cExperience"
        		+ "\n&6- &cDemote"
        		+ "\n&6- &cPromote"
        		+ "\n&6- &cMoney"
        		+ "\n&6- &cRank"
        		+ "\n&6- &cMute"
        		+ "\n&6- &cKick"
        		+ "\n&6- &cBan"
        		+ "\n";
		}else{
			str = 
	        		"&7Manage Player Attributes:"
	        		+ "\n&6- &aScoreBoard"
	        		+ "\n&6- &cFriends"
	        		+ "\n&6- &cMoney"
	        		+ "\n";
		}
		
		return str;
	}
	
	private void playerButton(){
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(player.getName());
		head.setItemMeta(skull);
        setSkullItem(slot, head, player ->{
        	new PlayerGUI(player ,this, null);
        }, "&3Player Management Menu:", displayInfo()
        		);
		slot++;
	}

	private void aboutButton(){
		setItem(1, new ItemStack(Material.PAPER), player -> {
		}, "&6About"
			, aboutInfo());
	}
	
	String str = "";
	private String aboutInfo() {
		
		str = "&6Plugin Name: &7" + Main.getInstance().getDescription().getName()
				+ "\n&6Version: &7" + Main.getInstance().getDescription().getVersion()
				+ "\n&6Website: &7" + Main.getInstance().getDescription().getWebsite()
				+ "\n&6Author: ";
		Main.getInstance().getDescription().getAuthors().forEach(author->{
			str += "&7" + author + "\n";
		});
		str += "&6Plugin Description: "
				+"\n&8" + Main.getInstance().getDescription().getDescription();
		return str;
	}

	private void helpButton(){	
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			new HelpGUI(player, this);
		}, "&2Help Information", 
				"");
		slot++;
	}

	private void spawnButton(){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			player.teleport(PlayerWrapper.getWrapper(player).getKingdom(player.getWorld()).getSpawn());
		}, "&2Kingdom Spawn", 
				"&6Teleport to Kingdom spawn"
				+ "\n&6Alias: &7/kc home"
				+ "\n");
		slot++;
	}
	
	private void kingdomButton(){	
		String detail;
		if (Validate.hasPerm(player, ".admin")){
		 detail = 
				 "&6Admin only:" 
				+ "\n&2Create&6/&3Edit &6Kingdoms!"
				+ "\n";
		}else{
			 detail = "\n&2Join&6/&4Leave &6Kingdoms!"
						+ "\n";
		}
	setItem(slot, new ItemStack(Material.BEACON), player -> {
		new KingdomGUI(player, this);
	}, "&6Kingdom Menu", detail);
	slot++;
	}

	private void townButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			new TownGUI(player, this);
		}, "&6Town Menu", 
				"&6Admin only:" 
				+ "\n&2Create&6/&3Edit &6Towns!"
				+ "\n");
		slot++;
	}
	
	private void villageButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			new VillageGUI(player, this);
		}, "&6Village Menu", 
				"&6Admin only:" 
				+ "\n&2Create&6/&3Edit &6Villages!"
				+ "\n");
		slot++;
	}

	private void conflictButton(){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			new ConflictGUI(player, this);
		}, "&6Conflict Gui", 
				"&6Teleport to a Town/Village under"
				+ "\n&6your kingdoms controle"
				+ "Alias: /kc tp" 
				+ "\n");
		slot++;
	}
	
	private void resetButton(){
		setItem(slot, new ItemStack(Material.BARRIER), player -> {
			new ResetGUI(player, this);
		}, "&6Reset Menu", 
				"&6"
				+ "\n");
		slot++;
	}

	private void kitButton(){
		setItem(slot, new ItemStack(Material.ENDER_CHEST), player -> {
			new KitGUI(player, this);
		}, "&6Kit Menu", 
				"&3Create/Edit Kits &7(Reward Boxes)"
				+ "\n");
		slot++;
	}
	
}
