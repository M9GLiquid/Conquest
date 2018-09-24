package eu.kingconquest.conquest.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ColorManager{

	/**
	 * Convert int to & Color Codes
	 * @param color - int
	 * @return String
	 */
	public static String intToSymbols(int color){
		switch(color){
			case 0:  return "&1"; // Dark Blue
			case 1:  return "&2"; // Dark Green
			case 2:  return "&3"; // Dark Aqua
			case 3:  return "&4"; // Dark Red
			case 4:  return "&5"; // Dark Purple
			case 5:  return "&9"; // Blue
			case 6:  return "&a"; // Green
			case 7:  return "&d"; // Pink
			case 8:  return "&e"; // Yellow
			default: return "&f"; // White
		}
	}

	/**
	 * Convert int to byte
	 * @param color - int
	 * @return byte
	 */
	public static byte intToByte(int color){
		switch(color){
			case 0:  return 11; // Dark Blue
			case 1:  return 13; // Dark Green
			case 2:  return 9;  // Dark Aqua
			case 3:  return 14; // Dark Red
			case 4:  return 10; // Dark Purple
			case 5:  return 3;  // Blue
			case 6:  return 5;  // Green
			case 7:  return 6;  // Pink
			case 8:  return 4;  // Yellow
			default: return 0;  // White
		}
	}

	/**
	 * Convert int to ChatColor
	 * @param color - int
	 * @return ChatColor
	 */
	public static ChatColor int2ChatColor(int color){
		switch(color){
			case 0:  return ChatColor.DARK_BLUE; 		// Dark Blue
			case 1:  return ChatColor.DARK_GREEN; 	// Dark Green
			case 2:  return ChatColor.DARK_AQUA; 		// Dark Aqua
			case 3:  return ChatColor.DARK_RED; 			// Dark Red
			case 4:  return ChatColor.DARK_PURPLE; 	// Dark Purple
			case 5:  return ChatColor.BLUE; 					// Blue
			case 6:  return ChatColor.GREEN; 				// Green
			case 7:  return ChatColor.LIGHT_PURPLE; 	// Pink
			case 8:  return ChatColor.YELLOW; 				// Yellow
			default: return ChatColor.WHITE; 				// White
		}
	}

	public static Color int2Color(int color){
		switch(color){
		case 0:  return Color.fromRGB(0, 0, 205); 			// Dark Blue
		case 1:  return Color.fromRGB(34,139,34);		// Dark Green
		case 2:  return Color.fromRGB(0,206,209); 		// Dark Aqua
		case 3:  return Color.fromRGB(139,0,0);			// Dark Red
		case 4:  return Color.fromRGB(139,0,139);		// Dark Purple
		case 5:  return Color.fromRGB(106,90,205);		// Blue
		case 6:  return Color.fromRGB(50,205,50);		// Green
		case 7:  return Color.fromRGB(255,20,147); 		// Pink
		case 8:  return Color.fromRGB(255,255,0); 		// Yellow
		default: return Color.fromRGB(255,255,255); 	// White
	}
		
	}
}
