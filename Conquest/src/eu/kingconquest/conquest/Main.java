package eu.kingconquest.conquest;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import eu.kingconquest.conquest.commands.HomeCommand;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.event.ServerRestartEvent;
import eu.kingconquest.conquest.hook.Dynmap;
import eu.kingconquest.conquest.hook.Hooks;
import eu.kingconquest.conquest.hook.TNEApi;
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
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.Validate;


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

		new TNEApi();
		new Vault();
		new Dynmap();

		Config.registerFiles();
		Config.loadFilesOnStartup();
		ChatManager.Console("&6|==============={plugin_prefix}==============|");
		ChatManager.Console("&6|&2 Version: " + getDescription().getVersion());
		ChatManager.Console("&6|&2 Hooks:");
		Hooks.output();
		ChatManager.Console("&6|&2 Configs:");
		Config.output();
		ChatManager.Console("&6|=======================================|");
		createNeutralKingdom();
		
		ServerRestartListener srl = new ServerRestartListener();
		if (Bukkit.getOnlinePlayers().size() > 0){
			//Bukkit.getServer().getPluginManager().callEvent(new ServerRestartEvent());
			srl.onServerRestart(new ServerRestartEvent());
		}
		setListeners();
	}

	private void setListeners(){
		this.getServer().getPluginManager().registerEvents(new ServerRestartListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChestGuiListener(), this);
		this.getServer().getPluginManager().registerEvents(new CaptureProgressListener(), this);
		this.getServer().getPluginManager().registerEvents(new ProximityZoneListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		this.getServer().getPluginManager().registerEvents(new CreateListener(), this);
		this.getServer().getPluginManager().registerEvents(new ResetListener(), this);
	}

	/**
	 * Set a neutral Kingdom on join
	 */
	private static void createNeutralKingdom(){
		Config.getWorlds().stream()
		.filter(uniqueID->Validate.isNull(Kingdom.getNeutral(Bukkit.getWorld(uniqueID))))
		.forEach(uniqueID->{
			World world = Config.getWorld(uniqueID);
			Kingdom kingdom = new Kingdom("Neutral", null, world.getSpawnLocation().clone(), -1);
			kingdom.create(null);
			Config.saveKingdoms(world);
		});
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
		ChatManager.Console("&6|==============={plugin_prefix}==============|");
		ChatManager.Console("&6| &2Configs:");
		Config.removeOnDisable();
		Config.saveOnDisable();
		ChatManager.Console("&6|========================================|");
		Config.clear();
		getServer().getServicesManager().unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}
