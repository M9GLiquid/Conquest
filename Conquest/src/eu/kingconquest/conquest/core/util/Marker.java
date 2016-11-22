package eu.kingconquest.conquest.core.util;

import org.dynmap.markers.MarkerIcon;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.hook.Dynmap;

public class Marker{

	// Markers for Capitals
	public static MarkerIcon DARK_BLUE_KINGDOM;
	public static MarkerIcon DARK_GREEN_KINGDOM;
	public static MarkerIcon AQUA_KINGDOM;
	public static MarkerIcon RED_KINGDOM;
	public static MarkerIcon PURPLE_KINGDOM;
	public static MarkerIcon BLUE_KINGDOM;
	public static MarkerIcon GREEN_KINGDOM;
	public static MarkerIcon PINK_KINGDOM;
	public static MarkerIcon YELLOW_KINGDOM;
	public static MarkerIcon NEUTRAL_KINGDOM;

	// Markers for Objectives
	public static MarkerIcon DARK_BLUE_TOWN;
	public static MarkerIcon DARK_GREEN_TOWN;
	public static MarkerIcon AQUA_TOWN;
	public static MarkerIcon RED_TOWN;
	public static MarkerIcon PURPLE_TOWN;
	public static MarkerIcon BLUE_TOWN;
	public static MarkerIcon GREEN_TOWN;
	public static MarkerIcon PINK_TOWN;
	public static MarkerIcon YELLOW_TOWN;
	public static MarkerIcon NEUTRAL_TOWN;

	// Markers for Outposts
	public static MarkerIcon DARK_BLUE_VILLAGE;
	public static MarkerIcon DARK_GREEN_VILLAGE;
	public static MarkerIcon AQUA_VILLAGE;
	public static MarkerIcon RED_VILLAGE;
	public static MarkerIcon PURPLE_VILLAGE;
	public static MarkerIcon BLUE_VILLAGE;
	public static MarkerIcon GREEN_VILLAGE;
	public static MarkerIcon PINK_VILLAGE;
	public static MarkerIcon YELLOW_VILLAGE;
	public static MarkerIcon NEUTRAL_VILLAGE;

	public static MarkerIcon int2MarkerIcon(Objective objective){
		if (objective instanceof Kingdom){
			switch (((Kingdom) objective).getColor()){
				case 0: return DARK_BLUE_KINGDOM;
				case 1: return DARK_GREEN_KINGDOM; 
				case 2: return AQUA_KINGDOM;
				case 3: return RED_KINGDOM;
				case 4: return PURPLE_KINGDOM;
				case 5: return BLUE_KINGDOM;
				case 6: return GREEN_KINGDOM;
				case 7: return PINK_KINGDOM;
				case 8: return YELLOW_KINGDOM;
				case -1:return NEUTRAL_KINGDOM;
			}
		}else if (objective instanceof Town){
			switch (objective.getOwner().getColor()){
				case 0: return DARK_BLUE_TOWN;
				case 1: return DARK_GREEN_TOWN;
				case 2: return AQUA_TOWN;
				case 3: return RED_TOWN;
				case 4: return PURPLE_TOWN;
				case 5: return BLUE_TOWN;
				case 6: return GREEN_TOWN;
				case 7: return PINK_TOWN;
				case 8: return YELLOW_TOWN;
				case -1:return NEUTRAL_TOWN;
			}
		}else  if (objective instanceof Village){
			switch (objective.getOwner().getColor()){
				case 0: return DARK_BLUE_VILLAGE;
				case 1: return DARK_GREEN_VILLAGE;
				case 2: return AQUA_VILLAGE;
				case 3: return RED_VILLAGE;
				case 4: return PURPLE_VILLAGE;
				case 5: return BLUE_VILLAGE;
				case 6: return GREEN_VILLAGE;
				case 7: return PINK_VILLAGE;
				case 8: return YELLOW_VILLAGE;
				case -1:return NEUTRAL_VILLAGE;
			}
		}
		return NEUTRAL_VILLAGE;
	}

	public static boolean update(Objective objective){

		if (remove(objective)){
			if (create(objective))
				return true;
		}else{
			create(objective);
			return true;
		}
		return false;
	}

	public static boolean remove(Objective objective){
		try{
			if (!Validate.isNull(Dynmap.dynmapAPI.getMarkerAPI().getMarkerSet("Conquest").findMarker(objective.getUUID().toString())))
				Dynmap.dynmapAPI.getMarkerAPI().getMarkerSet("Conquest").findMarker(objective.getUUID().toString()).deleteMarker();
			return true;
		}catch (Exception e){
			//e.printStackTrace();
			//ChatManager.Console("&4ERROR: &6Cannot remove Marker!");
			return false;
		}
	}

	public static boolean create(Objective objective){
		try{
			Dynmap.dynmapAPI
				.getMarkerAPI()
				.getMarkerSet("Conquest")
				.createMarker(objective.getUUID().toString(), objective.getName(),
						objective.getLocation().getWorld().getName(),
						objective.getLocation().getX(),
						objective.getLocation().getY(),
						objective.getLocation().getZ(),
						Marker.int2MarkerIcon(objective), false);
			setDescription(objective);
			return true;
		}catch (Exception e){
			//e.printStackTrace();
			//ChatManager.Console("&4ERROR: &6Cannot create Marker!");
			return false;
		}

	}

	public static boolean setDescription(Objective objective){
		String str = "<br>| Location:"
				+ "<br>| -> X: " + Math.ceil(objective.getLocation().getX()) 
				+ "<br>| -> Y: " + Math.ceil(objective.getLocation().getY()) 
				+ "<br>| -> Z: " + Math.ceil(objective.getLocation().getZ())
				+ "<br>| Spawn:"
				+ "<br>| -> X: " + Math.ceil(objective.getSpawn().getX()) 
				+ "<br>| -> Y: " + Math.ceil(objective.getSpawn().getY()) 
				+ "<br>| -> Z: " + Math.ceil(objective.getSpawn().getZ());
		String king = "No King";
		try{
			if (objective instanceof Kingdom){
				if (((Kingdom) objective).isNeutral())
					return false;
				if (!Validate.isNull(((Kingdom) objective).getKing()))
					 king  = ((Kingdom) objective).getKing().getName();
				Dynmap.dynmapAPI
				.getMarkerAPI()
				.getMarkerSet("Conquest")
				.findMarker(objective.getUUID().toString()).setDescription(
						"Name: " + objective.getName() 
						+ "<br>| Type: Capital"
						+ "<br>| King: " + king
						+ "<br>| Members: (" + ((Kingdom) objective).getMembers().size() + ")"
						+ str);
			}
			if (objective instanceof Town){
				Integer children = 0;
				if (!Validate.isNull( ((Town) objective).getChildren()))
					children =  ((Town) objective).getChildren().size();
				Dynmap.dynmapAPI
				.getMarkerAPI()
				.getMarkerSet("Conquest")
				.findMarker(objective.getUUID().toString()).setDescription(
						"Name: " + objective.getName() 
						+ "<br>| Type: Town" 
						+ "<br>| Owner: " + objective.getOwner().getName()
						+ "<br>| Children: (" + children + ")"
						+ str);
			}
			if (objective instanceof Village){
				String parent = "None";
				if (((Village)objective).getParent() != null)
					parent = ((Village)objective).getParent().getName();
				Dynmap.dynmapAPI
				.getMarkerAPI()
				.getMarkerSet("Conquest")
				.findMarker(objective.getUUID().toString()).setDescription(
						"Name: " + objective.getName() 
						+ "<br>| Type: Village" 
						+ "<br>| Owner: " + objective.getOwner().getName()
						+ "<br>| Parent: " + parent
						+ str);
			}
			return true;
		}catch (Exception e){
			//e.printStackTrace();
			//ChatManager.Console("&4ERROR: &6Cannot create Description! ");
			return false;
		}
	}
}
