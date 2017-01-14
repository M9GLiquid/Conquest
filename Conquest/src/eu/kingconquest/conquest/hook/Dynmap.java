package eu.kingconquest.conquest.hook;

import java.io.InputStream;
import java.util.HashSet;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Validate;

public class Dynmap{
	public static Plugin dynMap = null;
	public static DynmapAPI dynmapAPI = null;
	public static MarkerAPI markerAPI = null;
	public static MarkerSet markerSet = null;
	private static String path ="/eu/kingconquest/conquest/img/";
	private static String[] imgSet = { 
		"YELLOW_CAPITAL", "PINK_CAPITAL", "GREEN_CAPITAL", "BLUE_CAPITAL", "PURPLE_CAPITAL", 
		"RED_CAPITAL", "AQUA_CAPITAL", "DARK_GREEN_CAPITAL", "DARK_BLUE_KINGDOM",

		"NEUTRAL_OBJECTIVE", "YELLOW_OBJECTIVE", "PINK_OBJECTIVE", "GREEN_OBJECTIVE", "BLUE_OBJECTIVE", "PURPLE_OBJECTIVE", 
		"RED_OBJECTIVE", "AQUA_OBJECTIVE", "DARK_GREEN_OBJECTIVE", "DARK_BLUE_OBJECTIVE",

		"NEUTRAL_OUTPOST", "YELLOW_OUTPOST", "PINK_OUTPOST", "GREEN_OUTPOST", "BLUE_OUTPOST", "PURPLE_OUTPOST", 
		"RED_OUTPOST", "AQUA_OUTPOST", "DARK_GREEN_OUTPOST", "DARK_BLUE_OUTPOST",
	};

	public Dynmap(){
		if (setupDynmap()){
			Hooks.put("&6| --&3 Dynmap [&6Dynmap&3]", true);
		}else{
			Hooks.put("&6| --&3 Dynmap [&6Dynmap&3]", false);
			return;
		}
		if (setupDynmapAPI()){
			Hooks.put("&6| --&3 API [&6Dynmap API&3]", true);
			initMarkers();
		}else{
			Hooks.put("&6| --&3 API [&6Dynmap API&3]", false);
			return;
		}
		
	}

	/**
	 * Setup Dynmap API Connection
	 * 
	 * @return boolean
	 */
	private boolean setupDynmapAPI(){
		dynmapAPI = (DynmapAPI) dynMap;
		if (dynmapAPI != null)
			return true;
		return false;
	}

	/**
	 * Setup Dynmap Connection
	 * 
	 * @return boolean
	 */
	private boolean setupDynmap(){
		PluginManager pm = Main.getInstance().getServer().getPluginManager();
		dynMap = pm.getPlugin("dynmap");
		if (dynMap != null)
			return true;
		return false;
	}

	/**
	 * Initiate Markers for the dynmap
	 * @return void
	 */
	private void initMarkers(){
		HashSet<MarkerIcon> icons = new HashSet<MarkerIcon>();
		InputStream stream = null;
		try{
			for (String image : imgSet){
				MarkerIcon icon = dynmapAPI.getMarkerAPI().getMarkerIcon(image);
				if (Validate.isNull(icon)){
					stream = Main.class.getResourceAsStream(path + image + ".png");
					icon = dynmapAPI.getMarkerAPI().createMarkerIcon(image, image, stream);
				}
				icons.add(icon);
			}
			dynmapAPI.getMarkerAPI().createMarkerSet("Conquest", "Conquest", icons, false);
			setAllowedIcons(icons);
			setDynmapMarker(icons);
		}catch (Exception e){
			e.printStackTrace();
			ChatManager.Console("&4Cannot load image!");
		}
		createKingdom();
		createTown();
		createVillage();
	}
	
	private void createKingdom(){
		try{
			for (Kingdom kingdom : Kingdom.getKingdoms()){
				if (Marker.create(kingdom)){
					if (!Config.getBoolean("DebugDynmapMarkers", kingdom.getLocation()))
					ChatManager.Console("&6Marker for " + kingdom.getName()
							+ " &cWas not Added");
				}
			}
		}catch (Exception e){
			ChatManager.Console("&4Cannot load marker!");
		}
		
	}

	private void createTown(){
		try{
			for (Town town : Town.getTowns()){
				if (Marker.create(town)){
					if (!Config.getBoolean("DebugDynmapMarkers", town.getLocation()))
					ChatManager.Console("&6Marker for " + town.getName()
							+ " &cWas not Added");
				}
			}
		}catch (Exception e){
			ChatManager.Console("&4Cannot load marker!");
		}
	}

	private void createVillage(){
		try{
			for (Village village : Village.getVillages()){
				if (Marker.create(village)){
					if (!Config.getBoolean("DebugDynmapMarkers", village.getLocation()))
					ChatManager.Console("&6Marker for " + village.getName()
							+ " &cWas not Added");
				}
			}
		}catch (Exception e){
			ChatManager.Console("&4Cannot load marker!");
		}
	}

	/**
	 * Tell Dynmap that my markers is allowed
	 * @param icons - HashSet<MarkerIcon>
	 * @return void
	 */
	private void setAllowedIcons(HashSet<MarkerIcon> icons){
		icons.forEach(icon->{
			dynmapAPI.getMarkerAPI().getMarkerSet("Conquest").addAllowedMarkerIcon(icon);
		});
	}

	/**
	 * setDynmapMarkers for our color converter
	 * @param icons - HashSet<MarkerIcon>
	 * @return void
	 */
	private void setDynmapMarker(HashSet<MarkerIcon> icons){
		for (MarkerIcon icon : icons){
			switch(icon.getMarkerIconLabel()){
				//Kingdoms
				case "YELLOW_CAPITAL": 				Marker.YELLOW_KINGDOM = icon; break;
				case "PINK_CAPITAL": 					Marker.PINK_KINGDOM = icon; break;
				case "GREEN_CAPITAL": 				Marker.GREEN_KINGDOM = icon; break;
				case "BLUE_CAPITAL": 					Marker.BLUE_KINGDOM = icon; break;
				case "PURPLE_CAPITAL": 				Marker.PURPLE_KINGDOM = icon; break;
				case "RED_CAPITAL": 						Marker.RED_KINGDOM = icon; break;
				case "AQUA_CAPITAL": 					Marker.AQUA_KINGDOM = icon; break;
				case "DARK_GREEN_CAPITAL": 		Marker.DARK_GREEN_KINGDOM = icon; break;
				case "DARK_BLUE_CAPITAL": 		Marker.DARK_BLUE_KINGDOM = icon; break;
				//Towns
				case "YELLOW_OBJECTIVE": 			Marker.YELLOW_TOWN = icon; break;
				case "PINK_OBJECTIVE": 				Marker.PINK_TOWN = icon; break;
				case "GREEN_OBJECTIVE": 			Marker.GREEN_TOWN = icon; break;
				case "BLUE_OBJECTIVE": 				Marker.BLUE_TOWN = icon; break;
				case "PURPLE_OBJECTIVE": 			Marker.PURPLE_TOWN = icon; break;
				case "RED_OBJECTIVE": 					Marker.RED_TOWN = icon; break;
				case "AQUA_OBJECTIVE": 				Marker.AQUA_TOWN = icon; break;
				case "DARK_GREEN_OBJECTIVE": 	Marker.DARK_GREEN_TOWN = icon; break;
				case "DARK_BLUE_OBJECTIVE": 	Marker.DARK_BLUE_TOWN = icon; break;
				case "NEUTRAL_OBJECTIVE": 		Marker.NEUTRAL_TOWN = icon; break;
				//Villages
				case "YELLOW_OUTPOST": 			Marker.YELLOW_VILLAGE = icon; break;
				case "PINK_OUTPOST": 					Marker.PINK_VILLAGE = icon; break;
				case "GREEN_OUTPOST": 				Marker.GREEN_VILLAGE = icon; break;
				case "BLUE_OUTPOST": 					Marker.BLUE_VILLAGE = icon; break;
				case "PURPLE_OUTPOST": 				Marker.PURPLE_VILLAGE = icon; break;
				case "RED_OUTPOST": 					Marker.RED_VILLAGE = icon; break;
				case "AQUA_OUTPOST": 				Marker.AQUA_VILLAGE = icon; break;
				case "DARK_GREEN_OUTPOST":	Marker.DARK_GREEN_VILLAGE = icon; break;
				case "DARK_BLUE_OUTPOST": 		Marker.DARK_BLUE_VILLAGE = icon; break;
				case "NEUTRAL_OUTPOST": 			Marker.NEUTRAL_VILLAGE = icon; break;
					
			}
		}
	}
}
