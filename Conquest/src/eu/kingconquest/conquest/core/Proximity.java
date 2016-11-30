package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import eu.kingconquest.conquest.event.CaptureZoneEnterEvent;
import eu.kingconquest.conquest.event.CaptureZoneExitEvent;
import eu.kingconquest.conquest.util.Validate;

/**
 * Capture Event for Outpost
 * @author Thomas Lundqvist
 *
 */
public class Proximity implements Listener{
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	
	/**
	 * Village Proximity 
	 * @param player
	 */
	public void villageZoneProximity(Player player){
		// If Player isn't in a Kingdom, break or if there is no outpost's to capture
		if (!PlayerWrapper.getWrapper(player).isInKingdom() 
				|| Village.getVillages().size() == 0)
			return;
		
		Village.getVillages().forEach(village->{
			if (!Validate.isWithinCaptureArea(player.getLocation(), village.getLocation())){
				// If the player is outside of the area
				if (village.isCapturing(player)){
					pm.callEvent(new CaptureZoneExitEvent(player, village));
					return;
				}
				return;
			}
			pm.callEvent(new CaptureZoneEnterEvent(player, village));
		});
	}
	
	public void villageAreaProximity(Player player){
	
	}
	
	public void townZoneProximity(Player player){
		
	}
	
	public void townAreaProximity(Player player){
		
	}
	
	public void objectiveZoneProximity(Objective objective, Player player){
		if (!Validate.isWithinCaptureArea(player.getLocation(), objective.getLocation())){
			// If the player is outside of the area
			if (objective.isCapturing(player)){
				pm.callEvent(new CaptureZoneExitEvent(player, objective));
				return;
			}
			return;
		}
		pm.callEvent(new CaptureZoneEnterEvent(player, objective));
	}

	public void objectiveAreaProximity(Objective objective,Player player){
		
	}
}
