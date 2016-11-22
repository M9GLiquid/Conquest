package eu.kingconquest.conquest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import eu.kingconquest.conquest.command.Commands;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.VillageProximity;
import eu.kingconquest.conquest.core.event.ServerRestartEvent;
import eu.kingconquest.conquest.core.listener.CaptureProgressListener;
import eu.kingconquest.conquest.core.listener.CaptureZoneListener;
import eu.kingconquest.conquest.core.listener.ChestGuiListener;
import eu.kingconquest.conquest.core.listener.PlayerDeathListener;
import eu.kingconquest.conquest.core.listener.PlayerJoinListener;
import eu.kingconquest.conquest.core.listener.PlayerRespawnListener;
import eu.kingconquest.conquest.core.listener.ResetListener;
import eu.kingconquest.conquest.core.listener.ServerRestartListener;
import eu.kingconquest.conquest.core.util.ChatManager;
import eu.kingconquest.conquest.core.util.Config;
import eu.kingconquest.conquest.core.util.Validate;
import eu.kingconquest.conquest.hook.Dynmap;
import eu.kingconquest.conquest.hook.Hooks;
import eu.kingconquest.conquest.hook.Vault;


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
		this.getServer().getPluginManager().registerEvents(new VillageProximity(), this);
		this.getServer().getPluginManager().registerEvents(new ChestGuiListener(), this);
		this.getServer().getPluginManager().registerEvents(new CaptureProgressListener(), this);
		this.getServer().getPluginManager().registerEvents(new CaptureZoneListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		this.getServer().getPluginManager().registerEvents(new ResetListener(), this);
	}

	/**
	 * Set a neutral Kingdom on join
	 */
	private static void createNeutralKingdom(){
		Config.getWorlds().stream()
		.filter(world->Validate.isNull(Kingdom.getKingdom("Neutral", world)))
		.forEach(world->{
			Kingdom kingdom = new Kingdom("Neutral", null, -1, Bukkit.getWorld(world.getName()).getSpawnLocation().clone());
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
		if (command.getName().equalsIgnoreCase("c") 
				|| command.getName().equalsIgnoreCase("kc") 
				|| command.getName().equalsIgnoreCase("kingc") 
				|| command.getName().equalsIgnoreCase("conquest")
				|| command.getName().equalsIgnoreCase("kingconquest")){
			Commands.Main(sender, args);
			return true;
		}
		return false;
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
		Config.clearAll();
		getServer().getServicesManager().unregisterAll(this);
		Bukkit.getScheduler().cancelAllTasks();
		Bukkit.getScheduler().cancelTasks(this);


	}
}
