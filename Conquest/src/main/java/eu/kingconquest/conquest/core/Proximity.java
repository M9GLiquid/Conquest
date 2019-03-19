package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.event.CaptureZoneEnterEvent;
import eu.kingconquest.conquest.event.CaptureZoneExitEvent;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

/**
 * Capture Event for Outpost
 * @author Thomas Lundqvist
 *
 */
public class Proximity implements Listener{
	private static PluginManager pm = Bukkit.getServer().getPluginManager();
	
	/**
	 * Village Proximity 
	 * @param player Player
	 */
	public static void villageZoneProximity(Player player){
		
		// If Player isn't in a Kingdom, break or if there is no villages to capture
        if (!PlayerWrapper.getWrapper(player).isInKingdom(ActiveWorld.getActiveWorld(player.getWorld()))
				|| Village.getVillages().size() < 1)
			return;

        Village.getVillages(ActiveWorld.getActiveWorld(player.getWorld())).forEach(village -> {
			if (Validate.isWithinCapture(player, village.getLocation())) {
				pm.callEvent(new CaptureZoneEnterEvent(player, village));
			}else{
				// If the player is outside of the area
				if (village.isCapturing(player)) {
					pm.callEvent(new CaptureZoneExitEvent(player, village));
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
	
	public static void objectiveZoneProximity(Village village, Player player){
		if (!Validate.isWithinCapture(player, village.getLocation())) {
			// If the player is outside of the area
			if (village.isCapturing(player)){
				pm.callEvent(new CaptureZoneExitEvent(player, village));
				return;
			}
			return;
		}
		pm.callEvent(new CaptureZoneEnterEvent(player, village));
	}

	public static void objectiveAreaProximity(Objective objective, Player player){

	}

	public static void isWithinSquareArea(Player player) {

		// If Player isn't in a Arena, break or if there is no arena's
		/*if (!PlayerWrapper.getWrapper(player).isInArena(player.getWorld())
				|| Arena.getArenas().size() < 1)
			return;

		Arena.getArenas(player.getWorld()).forEach(arena->{
			if (Validate.isWithinSquare(player.getLocation(), arena.getLocation(), 50.0d, 5.0d)){
				pm.callEvent(new ArenaZoneEnter(player, arena));
			}else{
                pm.callEvent(new ArenaZoneExit(player, arena));
			}
		});*/
	}
}
