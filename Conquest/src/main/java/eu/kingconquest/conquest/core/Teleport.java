package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.Conquest;
import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Teleport{
	private int taskID;
	
	public Teleport(Player player, Objective objective){
		Cach.tpDelay = YmlStorage.getLong("TeleportDelay", objective.getLocation());
		new Message(player, MessageType.CHAT, "{StartTP}");
		Location loc = objective.getSpawn().clone();
        Bukkit.getServer().getScheduler().runTaskLater(Conquest.getInstance(), () -> {
			if (objective instanceof Town) {
				player.setInvulnerable(true);
				loc.setY(256);
				player.teleport(loc);

				startFall(player);
			} else if (objective instanceof Village) {
				player.setInvulnerable(true);
				loc.setY(256);
				player.teleport(loc);

				startFall(player);
			} else if (objective instanceof Kingdom) {
				player.teleport(loc);
			}
		}, Cach.tpDelay);
	}
	
	/**
	 * Fall from Y: 256
	 * Involnerable while falling
	 * @return void
	 */
	private void startFall(Player player){
		player.setFlying(false);
        taskID = Bukkit.getServer().getScheduler().runTaskTimer(Conquest.getInstance(), () -> {
			if (player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
				stopFall(player);
		}, 0, 10).getTaskId();
	}
	
	/**
	 * Stop the Clock of capturing!
	 * @param player - Player instance
	 * @return void
	 */
	private void stopFall(Player player){
		player.setInvulnerable(false);
		player.getServer().getScheduler().cancelTask(taskID);
	}
}
