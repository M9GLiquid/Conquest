package eu.kingconquest.conquest.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.hook.Vault;

public class Validate{
	/**
	 * Check to see whether the player is within an outpost
	 * @param player
	 * @param loc
	 * @return
	 */
	public static boolean isWithinArea(Location loc1, Location loc2, double radius, double maxY, double minY){
		if (!loc1.getWorld().equals(loc2.getWorld()))
			return false;
		double dx = Math.abs(loc1.getX() - loc2.getX());
		double dz = Math.abs(loc1.getZ() - loc2.getZ());
		if (dx + dz <= radius 
				&& loc1.getY() <= loc2.getY() + maxY
				&& loc1.getY() >= loc2.getY() - minY){
			return true;
		}
		return false;
	}
	
	/**
	 * Check to see whether the player is within an outpost
	 * @param player
	 * @param loc
	 * @return
	 */
	public static boolean isWithinCaptureArea(Location loc1, Location loc2){
		double dx = Math.abs(loc1.getX() - loc2.getX());
		double dy = Math.abs(loc1.getY() - loc2.getY());
		double radius = Config.getDouble("CaptureDistance", loc1);
		double maxY = Config.getDouble("CaptureMaxY", loc1);
		double MinY = Config.getDouble("CaptureMinY", loc1);
		if (dx + dy <= radius 
				&& loc1.getY() <= loc2.getY() + maxY
				&& loc1.getY() >= loc2.getY() - MinY){
			return true;
		}
		return false;
	}

    public static boolean hasPerm(Player p, String path){
    	if (Vault.perms.has(p, Main.getInstance().getName() + path))
			return true;
		return false; 
    }
    
	public static boolean notNull(Object object) {
		if (object != null)
			return true;
		return false;
	}
	
    public static void notNull(Object object, String error) {
		if (object != null)
            throw new NullPointerException(ChatManager.Format(error));
    }

	 public static boolean isNull(Object object){
		if (object == null)
			return  true;
		return false;
	}
	 
	 public static void isNull(Object object, String error){
			if (object == null)
	            throw new NullPointerException(ChatManager.Format(error));
	}
}
