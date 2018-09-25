package eu.kingconquest.conquest.hook;

import eu.kingconquest.conquest.MainClass;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.io.InputStream;
import java.util.HashSet;

public class Dynmap{
	public static Plugin	dynMap		= null;
	public static DynmapAPI	dynmapAPI	= null;
	public static MarkerAPI	markerAPI	= null;
	public static MarkerSet	markerSet	= null;
	private static String[]	imgSet		= {
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
		}
	}

	/**
	 * Setup Dynmap API Connection
	 * 
	 * @return boolean
	 */
	private boolean setupDynmapAPI(){
		dynmapAPI = (DynmapAPI) dynMap;
        return dynmapAPI != null;
	}

	/**
	 * Setup Dynmap Connection
	 * 
	 * @return boolean
	 */
	private boolean setupDynmap(){
        PluginManager pm = MainClass.getInstance().getServer().getPluginManager();
		dynMap = pm.getPlugin("dynmap");
        return dynMap != null;
	}

	/**
	 * Initiate Markers for the dynmap
	 * 
	 * @return void
	 */
	private void initMarkers(){
        HashSet<MarkerIcon> icons = new HashSet<>();
        InputStream stream;
		try{
			for (String image : imgSet){
				MarkerIcon icon = dynmapAPI.getMarkerAPI().getMarkerIcon(image);
				if (Validate.isNull(icon)){
                    String path = "/eu/kingconquest/conquest/img/";
                    stream = MainClass.class.getResourceAsStream(path + image + ".png");
					icon = dynmapAPI.getMarkerAPI().createMarkerIcon(image, image, stream);
				}
				icons.add(icon);
			}
			dynmapAPI.getMarkerAPI().createMarkerSet("Conquest", "Conquest", icons, false);
			setAllowedIcons(icons);
			setDynmapMarker(icons);
		}catch (Exception e){
			e.printStackTrace();
			new Message(MessageType.CONSOLE, "&4Cannot load image!");
		}
		createKingdom();
		createTown();
		createVillage();
	}

	private void createKingdom(){
		for (Kingdom kingdom : Kingdom.getKingdoms()){
			try{
				if (Marker.create(kingdom)){
					if (Validate.debug(kingdom.getLocation()))
						new Message(MessageType.CONSOLE, "&6Marker for Kingdom: " + kingdom.getName() + " &cWas Added");
				}
			}catch (Exception e){
				if (Validate.debug(kingdom.getLocation()))
					new Message(MessageType.CONSOLE, "&6Marker for Kingdom: " + kingdom.getName() + " &cWas not Added");
			}
		}
	}

	private void createTown(){
		for (Town town : Town.getTowns()){
			try{
				if (Marker.create(town)){
					if (Validate.debug(town.getLocation()))
						new Message(MessageType.CONSOLE, "&6Marker for Kingdom: " + town.getName() + " &cWas Added");
				}
			}catch (Exception e){
				if (Validate.debug(town.getLocation()))
					new Message(MessageType.CONSOLE, "&6Marker for Kingdom: " + town.getName() + " &cWas not Added");
			}
		}
	}

	private void createVillage(){
		for (Village village : Village.getVillages()){
			try{
				if (Marker.create(village)){
					if (Validate.debug(village.getLocation()))
						new Message(MessageType.CONSOLE, "&6Marker for Village: " + village.getName() + " &cWas Added");
				}
			}catch (Exception e){
				new Message(MessageType.CONSOLE, "&6Marker for Village: " + village.getName() + " &cWas not Added");
			}
		}
	}

	/**
	 * Tell Dynmap that my markers is allowed
	 * 
	 * @param icons
	 *            - HashSet<MarkerIcon>
	 * @return void
	 */
	private void setAllowedIcons(HashSet<MarkerIcon> icons){
        icons.forEach(icon ->
                dynmapAPI.getMarkerAPI().getMarkerSet("Conquest").addAllowedMarkerIcon(icon));
	}

	/**
	 * setDynmapMarkers for our color converter
	 * 
	 * @param icons
	 *            - HashSet<MarkerIcon>
	 * @return void
	 */
	private void setDynmapMarker(HashSet<MarkerIcon> icons){
		for (MarkerIcon icon : icons){
			switch (icon.getMarkerIconLabel()){
			//Kingdoms
			case "YELLOW_KINGDOM":
				Marker.YELLOW_KINGDOM = icon;
				break;
			case "PINK_KINGDOM":
				Marker.PINK_KINGDOM = icon;
				break;
			case "GREEN_KINGDOM":
				Marker.GREEN_KINGDOM = icon;
				break;
			case "BLUE_KINGDOM":
				Marker.BLUE_KINGDOM = icon;
				break;
			case "PURPLE_KINGDOM":
				Marker.PURPLE_KINGDOM = icon;
				break;
			case "RED_KINGDOM":
				Marker.RED_KINGDOM = icon;
				break;
			case "AQUA_KINGDOM":
				Marker.AQUA_KINGDOM = icon;
				break;
			case "DARK_GREEN_KINGDOM":
				Marker.DARK_GREEN_KINGDOM = icon;
				break;
			case "DARK_BLUE_KINGDOM":
				Marker.DARK_BLUE_KINGDOM = icon;
				break;
			//Towns
			case "YELLOW_TOWN":
				Marker.YELLOW_TOWN = icon;
				break;
			case "PINK_TOWN":
				Marker.PINK_TOWN = icon;
				break;
			case "GREEN_TOWN":
				Marker.GREEN_TOWN = icon;
				break;
			case "BLUE_TOWN":
				Marker.BLUE_TOWN = icon;
				break;
			case "PURPLE_TOWN":
				Marker.PURPLE_TOWN = icon;
				break;
			case "RED_TOWN":
				Marker.RED_TOWN = icon;
				break;
			case "AQUA_TOWN":
				Marker.AQUA_TOWN = icon;
				break;
			case "DARK_GREEN_TOWN":
				Marker.DARK_GREEN_TOWN = icon;
				break;
			case "DARK_BLUE_TOWN":
				Marker.DARK_BLUE_TOWN = icon;
				break;
			case "NEUTRAL_TOWN":
				Marker.NEUTRAL_TOWN = icon;
				break;
			//Villages
			case "YELLOW_VILLAGE":
				Marker.YELLOW_VILLAGE = icon;
				break;
			case "PINK_VILLAGE":
				Marker.PINK_VILLAGE = icon;
				break;
			case "GREEN_VILLAGE":
				Marker.GREEN_VILLAGE = icon;
				break;
			case "BLUE_VILLAGE":
				Marker.BLUE_VILLAGE = icon;
				break;
			case "PURPLE_VILLAGE":
				Marker.PURPLE_VILLAGE = icon;
				break;
			case "RED_VILLAGE":
				Marker.RED_VILLAGE = icon;
				break;
			case "AQUA_VILLAGE":
				Marker.AQUA_VILLAGE = icon;
				break;
			case "DARK_GREEN_VILLAGE":
				Marker.DARK_GREEN_VILLAGE = icon;
				break;
			case "DARK_BLUE_VILLAGE":
				Marker.DARK_BLUE_VILLAGE = icon;
				break;
			case "NEUTRAL_VILLAGE":
				Marker.NEUTRAL_VILLAGE = icon;
				break;

			}
		}
	}
}
