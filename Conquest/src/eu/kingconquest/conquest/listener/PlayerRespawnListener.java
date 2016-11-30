package eu.kingconquest.conquest.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.Config;

public class PlayerRespawnListener implements Listener{
	private static Location deathLocation;

	/**
	 * Player Respawn Event
	 * @param e - event
	 * @return void
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		Player p = e.getPlayer();
		e.setRespawnLocation(deathLocation);
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(p);
		p.setGameMode(GameMode.SPECTATOR);
		p.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){
			@Override
			public void run(){
				p.setGameMode(GameMode.SURVIVAL);
				if (!wrapper.isInKingdom()){
					p.teleport(wrapper.getKingdom().getSpawn());
				}else{
					p.teleport(p.getWorld().getSpawnLocation());
				}
			}
		}, Config.getLongs("RespawnDelay", p.getLocation()));
	}
	
	public static Location getDeathLocation(){
		return deathLocation;
	}
	
	public static void setDeathLocation(Location deathLocation){
		PlayerRespawnListener.deathLocation = deathLocation;
	}
}
