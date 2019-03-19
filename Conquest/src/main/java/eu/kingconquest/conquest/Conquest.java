package eu.kingconquest.conquest;

import eu.kingconquest.conquest.commands.HomeCommand;
import eu.kingconquest.conquest.core.*;
import eu.kingconquest.conquest.database.FlatFileManager;
import eu.kingconquest.conquest.database.MysqlManager;
import eu.kingconquest.conquest.database.SQLiteManager;
import eu.kingconquest.conquest.database.core.Database;
import eu.kingconquest.conquest.database.core.DatabaseManager;
import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.hook.Dynmap;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.hook.Hooks;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.listener.*;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.DataType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


/**
 * Kingconquest Conquest Plugin
 * @author Thomas Lundqvist
 */
public class Conquest extends JavaPlugin implements Listener {
	private static Conquest instance;

	/**
	 * Get Instance of Plugin
	 *
	 * @return Plugin Instance
	 */
	public static Conquest getInstance() {
		return instance;
	}

	/**
	 * Plugin Startup
	 * @return void
	 */
	@Override
	public void onEnable(){
		instance = this;
		YmlStorage.load();
		new DatabaseManager();

		new EconAPI();
		new Vault();
		new Dynmap();

		switch (Database.getType()) {
			case FLATFILE:
				FlatFileManager.load();
				break;
			case MYSQL:
				new MysqlManager(DataType.CREATE);
				new MysqlManager(DataType.LOAD);
				break;
			case SQLITE:
                new SQLiteManager(DataType.CREATE);
                new SQLiteManager(DataType.LOAD);
				break;
		}

		new Message(MessageType.CONSOLE, "&6|==============={Prefix}==============|");
		new Message(MessageType.CONSOLE, "&6|&2 Version: " + getDescription().getVersion());
		new Message(MessageType.CONSOLE, "&6|&2 Hooks:");
		Hooks.output();
		new Message(MessageType.CONSOLE, "&6|&2 Configs:");
		YmlStorage.output();
		new Message(MessageType.CONSOLE, "&6|&2 Database:");
        Database.output();
		new Message(MessageType.CONSOLE, "&6| -- &6" + Database.getType());
		new Message(MessageType.CONSOLE, "&6|=======================================|");

		new ServerRestart(Bukkit.getOnlinePlayers());
		Kingdom.createNeutralKingdom();
		setListeners();

		Kingdom kingdom = new Kingdom(
				"Orion",
				null,
				null,
				new Location(Bukkit.getWorlds().get(0), 123.9D, 123.8D, 123.7D, 123.6F, 123.5F),
				new Location(Bukkit.getWorlds().get(0), 123.9D, 123.8D, 123.7D, 123.6F, 123.5F),
				3);
		new Message(MessageType.DEBUG, "Kingdom Created " + kingdom.getColor() + kingdom.getName());

		Town town = new Town(
                "Bagdad",
				new Location(Bukkit.getWorlds().get(0), 123.9D, 123.8D, 123.7D, 123.6F, 123.5F),
				new Location(Bukkit.getWorlds().get(0), 123.9D, 123.8D, 123.7D, 123.6F, 123.5F),
				kingdom);
		new Message(MessageType.DEBUG, "Town Created " + kingdom.getColor() + town.getName());
		Village village = new Village(
                "ElAmain",
				new Location(Bukkit.getWorlds().get(0), 123.9D, 123.8D, 123.7D, 123.6F, 123.5F),
				new Location(Bukkit.getWorlds().get(0), 123.9D, 123.8D, 123.7D, 123.6F, 123.5F),
				kingdom,
				kingdom,
				town);
		new Message(MessageType.DEBUG, "Village Created " + kingdom.getColor() + village.getName());
	}

	/**
	 * On Command /conquest
	 *
	 * @return boolean
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		this.getCommand("conquest").setExecutor(new HomeCommand());
		return true;
	}
	
	private void setListeners(){
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
		this.getServer().getPluginManager().registerEvents(new TrapListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChestGuiListener(), this);
		this.getServer().getPluginManager().registerEvents(new CaptureProgressListener(), this);
		this.getServer().getPluginManager().registerEvents(new ProximityZoneListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		this.getServer().getPluginManager().registerEvents(new ObjectiveListener(), this);
		this.getServer().getPluginManager().registerEvents(new ResetListener(), this);
	}

	/**
	 * Plugin Shutdown
	 * @return void
	 */
	@Override
	public void onDisable() {
		switch (Database.getType()) {
			case FLATFILE:
				FlatFileManager.remove();
				FlatFileManager.save();
				break;
			case MYSQL:
				new MysqlManager(DataType.REMOVE);
				new MysqlManager(DataType.SAVE);
				break;
			case SQLITE:
				new SQLiteManager(DataType.REMOVE);
				new SQLiteManager(DataType.SAVE);
				break;
		}

		new Message(MessageType.CONSOLE, "&6|==============={Prefix}==============|");
		new Message(MessageType.CONSOLE, "&6|&2 Version: " + getDescription().getVersion());
		new Message(MessageType.CONSOLE, "&6|&2 Hooks:");
		Hooks.output();
		new Message(MessageType.CONSOLE, "&6|&2 Configs:");
		YmlStorage.output();
		new Message(MessageType.CONSOLE, "&6|&2 Database:");
        Database.output();
		new Message(MessageType.CONSOLE, "&6| -- &6" + Database.getType());
		new Message(MessageType.CONSOLE, "&6|=======================================|");

		new Message(null, MessageType.CONSOLE, "&6|=======================================|");

		YmlStorage.clearData();
		Kingdom.clear();
		Town.clear();
		Village.clear();
		Reward.clear();
		PlayerWrapper.clear();
		Cach.nullify();
		
		getServer().getServicesManager().unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}

	public static void main(String[] args) {

	}
}
