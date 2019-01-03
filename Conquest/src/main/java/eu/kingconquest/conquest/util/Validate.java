package eu.kingconquest.conquest.util;

import eu.kingconquest.conquest.MainClass;
import eu.kingconquest.conquest.database.YmlStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Validate{
	/**
	 * Check to see whether the player is within an Circle
	 */
	public static boolean isWithinCircle(Player player, Location location2, double radius, double maxY, double minY) {
		Location location1 = player.getLocation();
		if (!location1.getWorld().equals(location2.getWorld()))
			return false;

		double dx = Math.abs(location1.getX() - location2.getX());
		double dz = Math.abs(location1.getZ() - location2.getZ());
		return dx <= radius
				&& dz <= radius
				&& location1.getY() <= (location2.getY() + maxY)
				&& location1.getY() >= (location2.getY() - minY);

	}

	/**
	 * Check to see whether the player is within an Square
	 */
	public static boolean isWithinSquare(Location loc1, Location loc2, double maxY, double minY) {
		return loc1.getWorld().equals(loc2.getWorld());

	}

	/**
	 * Check to see whether the player is within an outpost
	 */
	public static boolean isWithinCapture(Player player, Location targetLocation) {
		Location location1 = player.getLocation();
		if (!location1.getWorld().equals(targetLocation.getWorld()))
			return false;

		double dx = Math.abs(location1.getX() - targetLocation.getX());
		double dz = Math.abs(location1.getZ() - targetLocation.getZ());
		double radius = YmlStorage.getDouble("CaptureDistance", location1);
		double maxY = YmlStorage.getDouble("CaptureMaxY", location1);
		double MinY = YmlStorage.getDouble("CaptureMinY", location1);
		return dx <= radius
				&& dz <= radius
				&& location1.getY() <= (targetLocation.getY() + maxY)
				&& location1.getY() >= (targetLocation.getY() - MinY);
	}

	public static boolean hasPerm(Player p, String path){
		return p.hasPermission(MainClass.getInstance().getName() + path);
	}

	public static boolean notNull(Object reference){
		return reference != null;
	}

	public static void notNull(Object reference, String errorMsg){
		if (reference == null)
			new Message(null, MessageType.ERROR, errorMsg);
	}

	public static boolean isNull(Object reference){
		return reference == null;
	}

	public static void isNull(Object reference, String errorMsg){
		if (reference != null)
			new Message(null, MessageType.ERROR, errorMsg);
	}

	public static Integer[] getTime(long time){
		return new Integer[]{
				((int) time / 3600),
				((int) time % 3600) / 60,
				((int) time % 60)
		};
	}

	public static Double[] getTime(double seconds){
		return new Double[]{
				Math.floor((seconds / 60) / 60), //Hours
				Math.floor(seconds / 60), //Minutes
				(seconds / 60) - Math.floor(seconds / 60) //Seconds left
		};
	}

	public static boolean debug(Location location){
		return YmlStorage.getBoolean("Debug", location);
	}
}
