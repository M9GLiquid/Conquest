package eu.kingconquest.conquest.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.database.YmlStorage;
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
		double radius = YmlStorage.getDouble("CaptureDistance", loc1);
		double maxY = YmlStorage.getDouble("CaptureMaxY", loc1);
		double MinY = YmlStorage.getDouble("CaptureMinY", loc1);
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

	public static boolean notNull(Object reference){
		if (reference == null)
			return false;
		return true;
	}

	public static void notNull(Object reference, String errorMsg){
		if (reference == null)
			new Message(null, MessageType.ERROR, errorMsg);
	}

	public static boolean isNull(Object reference){
		if (reference != null)
			return false;
		return true;
	}

	public static void isNull(Object reference, String errorMsg){
		if (reference != null)
			new Message(null, MessageType.ERROR, errorMsg);
	}
}
