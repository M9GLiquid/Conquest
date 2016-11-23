package eu.kingconquest.conquest.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener{
	
	/**
	 * Player Death Event
	 * @param e - event
	 * @return void
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		PlayerRespawnListener.setDeathLocation(p.getLocation());
		p.spigot().respawn();
	}
}
