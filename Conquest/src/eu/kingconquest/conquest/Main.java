package eu.kingconquest.conquest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import eu.kingconquest.conquest.commands.HomeCommand;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.hook.Dynmap;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.hook.Hooks;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.listener.CaptureProgressListener;
import eu.kingconquest.conquest.listener.ChestGuiListener;
import eu.kingconquest.conquest.listener.CreateListener;
import eu.kingconquest.conquest.listener.PlayerDeathListener;
import eu.kingconquest.conquest.listener.PlayerJoinListener;
import eu.kingconquest.conquest.listener.PlayerMoveListener;
import eu.kingconquest.conquest.listener.PlayerRespawnListener;
import eu.kingconquest.conquest.listener.ProximityZoneListener;
import eu.kingconquest.conquest.listener.ResetListener;
import eu.kingconquest.conquest.listener.ServerRestartListener;
import eu.kingconquest.conquest.listener.TrapListener;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;


/**
 * Kingconquest Conquest Plugin
 * @author Thomas Lundqvist
 */
public class Main extends JavaPlugin implements Listener{
	private static Main instance;

	/**
	 * Plugin Startup
	 * @return void
	 */
	@Override
	public void onEnable(){
		instance = this;

		new EconAPI();
		new Vault();
		new Dynmap();
		
		YmlStorage.load();
		new Message(null, MessageType.CONSOLE, "&6|==============={Prefix}==============|");
		new Message(null, MessageType.CONSOLE, "&6|&2 Version: " + getDescription().getVersion());
		new Message(null, MessageType.CONSOLE, "&6|&2 Hooks:");
		Hooks.output();
		new Message(null, MessageType.CONSOLE, "&6|&2 Configs:");
		YmlStorage.output();
		new Message(null, MessageType.CONSOLE, "&6|=======================================|");

		ServerRestartListener.onServerRestart(Bukkit.getOnlinePlayers());
		setListeners();
		
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
		this.getServer().getPluginManager().registerEvents(new CreateListener(), this);
		this.getServer().getPluginManager().registerEvents(new ResetListener(), this);
	}
	
	/**
	 * On Command /c /kc, kingc, conquest, kingconquest
	 * @return boolean
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		this.getCommand("kingconquest").setExecutor(new HomeCommand());
		return true;
	}
	
	/**
	 * Get Instance of Plugin
	 * @return Plugin Instance
	 */
	public static final Main getInstance(){
		return instance;
	}

	/**
	 * Plugin Shutdown
	 * @return void
	 */
	@Override
	public void onDisable() {
		new Message(null, MessageType.CONSOLE, "&6|==============={Prefix}==============|");
		new Message(null, MessageType.CONSOLE, "&6|&2 Configs:");
		YmlStorage.remove();
		YmlStorage.save();
		new Message(null, MessageType.CONSOLE, "&6|=======================================|");
		
		YmlStorage.clear();
		Kingdom.clear();
		Town.clear();
		Village.clear();
		Reward.clear();
		PlayerWrapper.clear();
		Cach.nullify();
		
		getServer().getServicesManager().unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}
