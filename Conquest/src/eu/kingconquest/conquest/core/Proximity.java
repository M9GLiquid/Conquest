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
	private static PluginManager pm = Bukkit.getServer().getPluginManager();
	
	/**
	 * Village Proximity 
	 * @param player
	 */
	public static void villageZoneProximity(Player player){
		// If Player isn't in a Kingdom, break or if there is no villages to capture
		if (!PlayerWrapper.getWrapper(player).isInKingdom(player.getWorld()) 
				|| Village.getVillages().size() < 1)
			return;
		
		Village.getVillages(player.getWorld()).forEach(village->{
			if (Validate.isWithinCaptureArea(player.getLocation(), village.getLocation())){
				pm.callEvent(new CaptureZoneEnterEvent(player, village));
			}else{
				// If the player is outside of the area
				if (village.isCapturing(player)) {
					pm.callEvent(new CaptureZoneExitEvent(player, village));
					return;
				}
			}
		});
	}
	
	public static void villageAreaProximity(Player player){
		
	}
	
	public static void townZoneProximity(Player player){
		
	}
	
	public static void townAreaProximity(Player player){
		
	}
	
	public static void objectiveZoneProximity(Objective objective, Player player){
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
	
	public static void objectiveAreaProximity(Objective objective,Player player){
		
	}
}
