package eu.kingconquest.conquest.listener;

import eu.kingconquest.conquest.core.Proximity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener{

	/**
	 * Player Move Event (X/Y/Z only Movement)
	 * @param e - Event
	 * @return void
	 */
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e){
		if (e.getFrom().getPitch() == e.getTo().getPitch() && e.getFrom().getYaw() == e.getTo().getYaw()) {
			Player player = e.getPlayer();
			
			//Village Zone Proximity
			Proximity.villageZoneProximity(player);

			//Village Zone Proximity
			//Proximity.arenaAreaProximity(player);

			//Village Area Proximity
			//proximity.villageAreaProximity(player);
			
			//Town Zone Proximity
			//proximity.townZoneProximity(player);

			//Town Area Proximity
			//proximity.townAreaProximity(player);
			
//To Replace all above on Kingdom Capture Implementation
			
			//Objective Zone Proximity
			//proximity.kingdomZoneProximity(player);
			
			//Objective Area Proximity
			//proximity.kingdomAreaProximity(player);
		}
	}
}
