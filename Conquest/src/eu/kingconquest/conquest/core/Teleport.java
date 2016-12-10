package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;

public class Teleport{
	private static int taskID;

	public Teleport(Player player, Location loc){
		Cach.tpDelay = Config.getLongs("TeleportDelay", loc);
		ChatManager.Chat(player, Config.getChat("startTP"));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){
			@Override
			public void run(){
				player.setInvulnerable(true);
				loc.setY(256);
				player.teleport(loc);
				startFall(player);
				ChatManager.Chat(player, Config.getChat("Teleported"));
			}
		}, Config.getLongs("TeleportDelay", loc));
	}

	/**
	 * Fall from Y: 256
	 * Involnerable while falling
	 * @return void
	 */
	private void startFall(Player player){
		taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){
			@Override
			public void run(){;
			player.setFlying(false);
			if (player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
				stopFall(player);
			}
		}, 0, 10);
	}

	/**
	 * Stop the Clock of capturing!
	 * @param p - Player instance
	 * @return void
	 */
	private void stopFall(Player player){
		player.setInvulnerable(false);
		player.getServer().getScheduler().cancelTask(taskID);
	}
}
