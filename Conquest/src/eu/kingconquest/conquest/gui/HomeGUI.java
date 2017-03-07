package eu.kingconquest.conquest.gui;

import java.awt.Desktop;
import java.net.URI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.gui.objective.KingdomGUI;
import eu.kingconquest.conquest.gui.objective.TownGUI;
import eu.kingconquest.conquest.gui.objective.VillageGUI;
import eu.kingconquest.conquest.gui.reward.RewardGUI;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
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

		kingdomButton();

		if (Validate.hasPerm(player, ".admin")){
			townButton();
			villageButton();
			if (Validate.hasPerm(player, ".admin.reward"))
				rewardButton();
			if (Validate.hasPerm(player, ".admin.reset"))
				resetButton();
			reloadButton();
		}else{
			if (PlayerWrapper.getWrapper(player).isInKingdom(player.getWorld())){
				spawnButton();
				if (Validate.hasPerm(player, ".basic.teleport"))
					conflictButton();
				if (Validate.hasPerm(player, ".basic.reward"))
					rewardButton();
			}
		}
	}

	private void reloadButton(){
		setItem(2, new ItemStack(Material.REDSTONE_LAMP_OFF), player -> {
			if (YmlStorage.loadDefault() && YmlStorage.loadLanguage()){
				new Message(player, MessageType.CHAT, "&7Config.yml & Language.yml &aSuccessfully to reload!");
				return;
			}
			new Message(player, MessageType.CHAT, "&7Config.yml & Language.yml &cFailed to reload!");
		}, "&3Reload Config",
				"&7Affected Files: "
						+ "\n&3 - Language.yml"
						+ "\n&3 - Config.yml"
						+ "\n"
						+ "\n&bClick to reload!"
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
			if (getClickType().equals(ClickType.DOUBLE_CLICK)){
				if(Desktop.isDesktopSupported()){
					try{
						Desktop.getDesktop().browse(new URI(Main.getInstance().getDescription().getWebsite()));
					}catch (Exception e){	}
				}
			}
		}, "&6About"
				, aboutInfo());
	}

	String str = "";
	private String aboutInfo() {

		str = "&7Plugin Name: &3" + Main.getInstance().getDescription().getName()
				+ "\n&7Version: &3" + Main.getInstance().getDescription().getVersion()
				+ "\n&7Website: &3" + Main.getInstance().getDescription().getWebsite()
				+ "\n&7Author: ";
		Main.getInstance().getDescription().getAuthors().forEach(author->{
			str += "&3" + author + "\n";
		});
		str += "&7Plugin Description: "
				+"\n&8" + Main.getInstance().getDescription().getDescription()
				+ "\n"
				+ "\n&bDouble-Click to open website";
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
				"&7Teleport to Kingdom spawn"
						+ "\n");
		slot++;
	}

	private void kingdomButton(){	
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		String details;
		if (Validate.hasPerm(player, ".admin")){
			details = 
					"&4Admin only:"
							+"\n&aCreate &7a Kingdom!"
							+ "\n&3Edit &7a Kingdom!"
							+ "\n&cDelete &7a Kingdom!"
							+ "\n";
		}else{
			if (wrapper.isInKingdom(player.getWorld())){
				details = "\n&cLeave " + wrapper.getKingdom(player.getWorld()).getColorSymbol() + wrapper.getKingdom(player.getWorld()).getName()
						+ "\n";
			}else{
				details = "\n&aJoin &7a Kingdom!"
						+ "\n";
			}
		}
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			new KingdomGUI(player, this);
		}, "&6Kingdom Menu", details);
		slot++;
	}

	private void townButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			new TownGUI(player, this);
		}, "&6Town Menu", 
				"&4Admin only:" 
						+"\n&aCreate &7a Town!"
						+ "\n&3Edit &7a Town!"
						+ "\n&cDelete &7a Town!"
						+ "\n");
		slot++;
	}

	private void villageButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			new VillageGUI(player, this);
		}, "&6Village Menu", 
				"&4Admin only:" 
						+"\n&aCreate &7a Village!"
						+ "\n&3Edit &7a Village!"
						+ "\n&cDelete &7a Village!"
						+ "\n");
		slot++;
	}

	private void conflictButton(){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			new ConflictGUI(player, this);
		}, "&6Conflict Gui", 
				"&7Teleport to a Town/Village under"
						+ "\n&7your kingdoms controle"
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

	private void rewardButton(){
		String details;
		if (Validate.hasPerm(player, ".admin.rewards")){
			details = 
					"&4Admin only:" 
							+"\n&aCreate &7a Reward Box!"
							+ "\n&3Edit &7a Reward Box!"
							+ "\n&cDelete &7a Reward Box!"
							+ "\n";
		}else{
			details = "\n&aBuy&6 &7Rewards!"
					+ "\n&dView &7Rewards!"
					+ "\n";
		}
		setItem(slot, new ItemStack(Material.ENDER_CHEST), player -> {
			new RewardGUI(player, this);
		}, "&6Reward Box Menu", details);
		slot++;
	}

}
