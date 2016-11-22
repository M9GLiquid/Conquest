package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

import eu.kingconquest.conquest.core.event.CaptureZoneEnterEvent;
import eu.kingconquest.conquest.core.event.CaptureZoneExitEvent;
import eu.kingconquest.conquest.core.util.Validate;

/**
 * Capture Event for Outpost
 * @author Thomas Lundqvist
 *
 */
public class VillageProximity implements Listener{
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	private Player p;

	/**
	 * Player Move Event (X/Y/Z only Movement)
	 * @param e - Event
	 * @return void
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent e){
		if (e.getFrom().getPitch() == e.getTo().getPitch() && e.getFrom().getYaw() == e.getTo().getYaw()) {
			p = e.getPlayer();
			// If Player isn't in a Kingdom, break or if there is no outpost's to capture
			if (!PlayerWrapper.getWrapper(p).isInKingdom() 
					|| Village.getVillages().size() == 0)
				return;
			
			Village.getVillages().forEach(village->{
				if (!Validate.isWithinCaptureArea(p.getLocation(), village.getLocation())){
					// If the player is outside of the area
					if (village.isCapturing(p)){
						pm.callEvent(new CaptureZoneExitEvent(p, village));
						return;
					}
					return;
				}
				pm.callEvent(new CaptureZoneEnterEvent(p, village));
			});
		}
	}
}
