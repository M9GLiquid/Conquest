package eu.kingconquest.conquest.listener;

import eu.kingconquest.conquest.Conquest;
import eu.kingconquest.conquest.core.ActiveWorld;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.core.YmlStorage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener{
	private static Location deathLocation;
	
	/**
	 * Player Respawn Event
	 * @param e - event
	 * @return void
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		Player player = e.getPlayer();
		
		YmlStorage.getWorlds().forEach(uniqueID->{
			if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
				e.setRespawnLocation(deathLocation);
				PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
				player.setGameMode(GameMode.SPECTATOR);
				player.setCanPickupItems(false);
                player.getServer().getScheduler().runTaskLater(Conquest.getInstance(), () -> {
					player.setCanPickupItems(true);
                    if (wrapper.isInKingdom(ActiveWorld.getActiveWorld(player.getWorld()))) {
                        player.teleport(wrapper.getKingdom(ActiveWorld.getActiveWorld(player.getWorld())).getSpawn());
					} else {
						player.teleport(player.getWorld().getSpawnLocation());
					}
					player.setGameMode(GameMode.SURVIVAL);
				}, YmlStorage.getLong("RespawnDelay", player.getLocation()));
			}
		});
	}
	
	public static Location getDeathLocation(){
		return deathLocation;
	}
	
	public static void setDeathLocation(Location deathLocation){
		PlayerRespawnListener.deathLocation = deathLocation;
	}
}
