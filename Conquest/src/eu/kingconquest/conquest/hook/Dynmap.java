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
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class Dynmap{
	public static Plugin dynMap = null;
	public static DynmapAPI dynmapAPI = null;
	public static MarkerAPI markerAPI = null;
	public static MarkerSet markerSet = null;
	private static String path ="/eu/kingconquest/conquest/img/";
	private static String[] imgSet = { 
		"YELLOW_KINGDOM", "PINK_KINGDOM", "GREEN_KINGDOM", "BLUE_KINGDOM", "PURPLE_KINGDOM", 
		"RED_KINGDOM", "AQUA_KINGDOM", "DARK_GREEN_KINGDOM", "DARK_BLUE_KINGDOM",

		"NEUTRAL_TOWN", "YELLOW_TOWN", "PINK_TOWN", "GREEN_TOWN", "BLUE_TOWN", "PURPLE_TOWN", 
		"RED_TOWN", "AQUA_TOWN", "DARK_GREEN_TOWN", "DARK_BLUE_TOWN",

		"NEUTRAL_VILLAGE", "YELLOW_VILLAGE", "PINK_VILLAGE", "GREEN_VILLAGE", "BLUE_VILLAGE", "PURPLE_VILLAGE", 
		"RED_VILLAGE", "AQUA_VILLAGE", "DARK_GREEN_VILLAGE", "DARK_BLUE_VILLAGE",
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
			new Message(null, MessageType.CONSOLE, "&4Cannot load image!");
		}
		createKingdom();
		createTown();
		createVillage();
	}
	
	private void createKingdom(){
		try{
			for (Kingdom kingdom : Kingdom.getKingdoms()){
				if (Marker.create(kingdom)){
					if (!YmlStorage.getBoolean("DebugDynmapMarkers", kingdom.getLocation()))
						new Message(null, MessageType.CONSOLE, "&6Marker for " + kingdom.getName() + " &cWas not Added");
				}
			}
		}catch (Exception e){
			new Message(null, MessageType.CONSOLE, "&4Cannot load marker!");
		}
		
	}

	private void createTown(){
		try{
			for (Town town : Town.getTowns()){
				if (Marker.create(town)){
					if (!YmlStorage.getBoolean("DebugDynmapMarkers", town.getLocation()))
						new Message(null, MessageType.CONSOLE, "&6Marker for " + town.getName() + " &cWas not Added");
				}
			}
		}catch (Exception e){
			new Message(null, MessageType.CONSOLE, "&4Cannot load marker!");
		}
	}

	private void createVillage(){
		try{
			for (Village village : Village.getVillages()){
				if (Marker.create(village)){
					if (!YmlStorage.getBoolean("DebugDynmapMarkers", village.getLocation()))
						new Message(null, MessageType.CONSOLE, "&6Marker for " + village.getName() + " &cWas not Added");
				}
			}
		}catch (Exception e){
			new Message(null, MessageType.CONSOLE, "&4Cannot load marker!");
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
				case "YELLOW_KINGDOM": 			Marker.YELLOW_KINGDOM = icon; break;
				case "PINK_KINGDOM": 				Marker.PINK_KINGDOM = icon; break;
				case "GREEN_KINGDOM": 				Marker.GREEN_KINGDOM = icon; break;
				case "BLUE_KINGDOM": 				Marker.BLUE_KINGDOM = icon; break;
				case "PURPLE_KINGDOM": 			Marker.PURPLE_KINGDOM = icon; break;
				case "RED_KINGDOM": 					Marker.RED_KINGDOM = icon; break;
				case "AQUA_KINGDOM": 				Marker.AQUA_KINGDOM = icon; break;
				case "DARK_GREEN_KINGDOM": 	Marker.DARK_GREEN_KINGDOM = icon; break;
				case "DARK_BLUE_KINGDOM": 		Marker.DARK_BLUE_KINGDOM = icon; break;
				//Towns
				case "YELLOW_TOWN": 					Marker.YELLOW_TOWN = icon; break;
				case "PINK_TOWN": 						Marker.PINK_TOWN = icon; break;
				case "GREEN_TOWN": 					Marker.GREEN_TOWN = icon; break;
				case "BLUE_TOWN": 						Marker.BLUE_TOWN = icon; break;
				case "PURPLE_TOWN": 					Marker.PURPLE_TOWN = icon; break;
				case "RED_TOWN": 						Marker.RED_TOWN = icon; break;
				case "AQUA_TOWN": 					Marker.AQUA_TOWN = icon; break;
				case "DARK_GREEN_TOWN": 		Marker.DARK_GREEN_TOWN = icon; break;
				case "DARK_BLUE_TOWN": 			Marker.DARK_BLUE_TOWN = icon; break;
				case "NEUTRAL_TOWN": 				Marker.NEUTRAL_TOWN = icon; break;
				//Villages
				case "YELLOW_VILLAGE": 				Marker.YELLOW_VILLAGE = icon; break;
				case "PINK_VILLAGE": 					Marker.PINK_VILLAGE = icon; break;
				case "GREEN_VILLAGE": 				Marker.GREEN_VILLAGE = icon; break;
				case "BLUE_VILLAGE": 					Marker.BLUE_VILLAGE = icon; break;
				case "PURPLE_VILLAGE": 				Marker.PURPLE_VILLAGE = icon; break;
				case "RED_VILLAGE": 						Marker.RED_VILLAGE = icon; break;
				case "AQUA_VILLAGE": 					Marker.AQUA_VILLAGE = icon; break;
				case "DARK_GREEN_VILLAGE"	:	Marker.DARK_GREEN_VILLAGE = icon; break;
				case "DARK_BLUE_VILLAGE": 		Marker.DARK_BLUE_VILLAGE = icon; break;
				case "NEUTRAL_VILLAGE": 			Marker.NEUTRAL_VILLAGE = icon; break;
					
			}
		}
	}
}
