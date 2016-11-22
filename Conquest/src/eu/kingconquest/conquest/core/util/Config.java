package eu.kingconquest.conquest.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;


public class Config extends YamlConfiguration{
	private String name;
	private File file;
	private String defaults;

	//Instance Specific
	/**
	 * Creates new Config File, without defaults
	 * @param path - String
	 * @param fileName - String
	 */
	public Config(String path, String fileName) {
		this(path, fileName, null);
	}

	/**
	 * Creates new Config File, with defaults
	 * @param path - String
	 * @param fileName - String
	 * @param defaultsName - String
	 */
	public Config(String path, String fileName, String defaultsName) {
		defaults = defaultsName;

		String pathway = (path == null) ? Main.getInstance().getDataFolder() + File.separator + fileName : Main.getInstance().getDataFolder() + File.separator + path + File.separator + fileName;
		file = new File(pathway);
		reload();
		setName(fileName);
		addConfig(this);
	}

	/**
	 * Reload configuration
	 * return void
	 */
	public boolean reload() {
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}catch (IOException exception){
				Main.getInstance().getLogger().severe("Error while creating file " + file.getName());
				return false;
			}
		}

		try {
			load(file);
			if (defaults != null){
				InputStreamReader reader = new InputStreamReader(Main.getInstance().getResource(defaults));
				FileConfiguration defaultsConfig = YamlConfiguration.loadConfiguration(reader);       

				setDefaults(defaultsConfig);
				options().copyDefaults(true);

				reader.close();
				save();
				return true;
			}
		}catch (IOException exception){
			Main.getInstance().getLogger().severe("Error while loading file " + file.getName());
			return false;

		}catch (InvalidConfigurationException exception){
			Main.getInstance().getLogger().severe("Error while loading file " + file.getName());
			return false;
		}
		return false;
	}

	/**
	 * Save configuration
	 * @return void
	 */
	public void save() {
		try {
			options().indent(2);
			save(file);
		}catch (IOException exception){
			exception.printStackTrace();
			Main.getInstance().getLogger().severe("Error while saving file " + file.getName());
		}
	}

	/**
	 * Set Location to config
	 * @param config - ConfigManager instance
	 * @param pathway - String
	 * @param key - Location
	 * @return void
	 */
	public void setLocation(String pathway, Location key){
		if (!Validate.isNull(pathway) && !Validate.isNull(key.getWorld())){
			this.set(pathway + ".X", key.getX());
			this.set(pathway + ".Y", key.getY());
			this.set(pathway + ".Z", key.getZ());
			this.set(pathway + ".World", key.getWorld().getName());
			return;
		}
	}

	/**
	 * Get location from config
	 * @param pathway - String
	 * @return Location
	 */
	public Location getLocation(String pathway){
		Double X = this.getDouble(pathway + ".X");
		Double Y = this.getDouble(pathway + ".Y");
		Double Z = this.getDouble(pathway + ".Z");
		Location loc = new Location(getWorld(this.getString(pathway + ".World")), X, Y, Z);
		return loc;
	}
	
	//Statics
	private static String headerMsg = "";
	private static String errorMsg = "&6| - &cFailed:";
	private static HashMap<String, Boolean> loadMsg = new HashMap<>();
	private static HashMap<String, Boolean> saveMsg = new HashMap<>();
	private static HashMap<String, Boolean> removeMsg = new HashMap<>();

	public static void loadFilesOnStartup(){
		if (loadDefault()) 
			loadMsg.put("&6| --&3 Default.yml [&6Global&3]", true);
		else
			loadMsg.put("&6| --&3 Default.yml [&6Global&3]", false);
		if (loadLanguages())
			loadMsg.put("&6| --&3 Language.yml [&6Global&3]", true);
		else
			loadMsg.put("&6| --&3 Language.yml [&6Global&3]", false);
		loadFiles();
	}
	public static void loadFiles() {
		getWorlds().forEach(aWorld->{
			headerMsg = "&6| - &aLoaded:";
			Bukkit.getWorlds().stream()
			.filter(world->aWorld.equals(world))
			.forEach(world->{
				if (loadKingdoms(world))
					loadMsg.put("&6| --&3 Kingdoms.yml [&6" + world.getName() + "&3]", true);
				else 
					loadMsg.put("&6| --&3 Kingdoms.yml [&6" + world.getName() + "&3]", false);

				if (loadUsers(world)) 
					loadMsg.put("&6| --&3 Users.yml [&6" + world.getName() + "&3]", true);
				else 
					loadMsg.put("&6| --&3 Users.yml [&6" + world.getName() + "&3]", true);

				if (loadVillages(world)) 
					loadMsg.put("&6| --&3 Villages.yml [&6" + world.getName() + "&3]", true);
				else 
					loadMsg.put("&6| --&3 Villages.yml [&6" + world.getName() + "&3]", true);

				if (loadTowns(world)) 
					loadMsg.put("&6| --&3 Towns.yml [&6" + world.getName() + "&3]", true);
				else 
					loadMsg.put("&6| --&3 Towns.yml [&6" + world.getName() + "&3]", true);
			});
		});
	}
	public static void removeOnDisable(){
		removeFromFiles();
		output();
	}
	public static void saveOnDisable(){
		saveFiles();
		output();
	}
	public static void removeFromFiles(){
		headerMsg = "&6| - &cRemoved:";
		Worlds.forEach(world->{
			removeKingdoms(world);
			removeTowns(world);
			removeVillages(world);
			removeUsers(world);
		});
	}
	public static void saveFiles() {
		headerMsg = "&6| - &aSaved:";
		Worlds.forEach(world->{
			if (saveKingdoms(world))
				saveMsg.put("&6| --&3 Kingdoms.yml [&6" + world.getName() + "&3]", true);
			else
				saveMsg.put("&6| --&3 Kingdoms.yml [&6" + world.getName() + "&3]", false);
			if (saveUsers(world))
				saveMsg.put("&6| --&3 Users.yml [&6" + world.getName() + "&3]", true);
			else
				saveMsg.put("&6| --&3 Users.yml [&6" + world.getName() + "&3]", false);
			if (saveTowns(world))
				saveMsg.put("&6| --&3 Towns.yml [&6" + world.getName() + "&3]", true);
			else
				saveMsg.put("&6| --&3 Towns.yml [&6" + world.getName() + "&3]", false);
			if (saveVillages(world))
				saveMsg.put("&6| --&3 Villages.yml [&6" + world.getName() + "&3]", true);
			else
				saveMsg.put("&6| --&3 Villages.yml [&6" + world.getName() + "&3]", false);
		});
	}

	public static Set<String> getPathSection(Config c, String path){
		if(c.isConfigurationSection(path))
			return c.getConfigurationSection(path).getKeys(false);
		Set<String> s = new HashSet<>();
		return s;
	}

	public static void output(){
		if (loadMsg.containsValue(true)) {
			ChatManager.Console(headerMsg);
			loadMsg.forEach((s,b)->{
				if (b) {
					ChatManager.Console(s);
				}
			});
		}
		if (loadMsg.containsValue(false)) {
			ChatManager.Console(errorMsg);
			loadMsg.forEach((s,b)->{
				if (!b)
					ChatManager.Console(s);
			});
		}
		loadMsg.clear();

		if (saveMsg.containsValue(true)) {
			ChatManager.Console(headerMsg);
			saveMsg.forEach((s,b)->{
				if (b) {
					ChatManager.Console(s);
				}
			});
		}
		if (saveMsg.containsValue(false)) {
			ChatManager.Console(errorMsg);
			saveMsg.forEach((s,b)->{
				if (!b)
					ChatManager.Console(s);
			});
		}
		saveMsg.clear();

		if (removeMsg.containsValue(true)) {
			ChatManager.Console(headerMsg);
			removeMsg.forEach((s,b)->{
				if (b) {
					ChatManager.Console(s);
				}
			});
		}
		if (removeMsg.containsValue(false)) {
			ChatManager.Console(errorMsg);
			removeMsg.forEach((s,b)->{
				if (!b)
					ChatManager.Console(s);
			});
		}
		removeMsg.clear();
	}

//Config Specific
	public static HashMap<World, Long> CaptureRate = new HashMap<>();
	public static HashMap<World, Double> CapDistance = new HashMap<>();
	public static HashMap<World, Double> CaptureMaxY = new HashMap<>();
	public static HashMap<World, Double> CaptureMinY = new HashMap<>();
	public static HashMap<World, Long> RespawnDelay = new HashMap<>();
	public static HashMap<World, Long> TeleportDelay = new HashMap<>();
	public static Double DistanceBetween = null;

//Config Loads
	public static boolean loadDefault(){
		Config config = getConfig("Config");


		try {
			strings.put("Port", 	(config.getString("Database.MYSQL.Port") 		!= null ? 	config.getString("Database.MYSQL.Port") : "3306" ));
			strings.put("Host", 	(config.getString("Database.MYSQL.Host") 		!= null ? 	config.getString("Database.MYSQL.Host") : "localhost" ));
			strings.put("User", 	(config.getString("Database.MYSQL.Username") 	!= null ? 	config.getString("Database.MYSQL.Username") : "root" ));
			strings.put("Pass", 	(config.getString("Database.MYSQL.Password") 	!= null ? 	config.getString("Database.MYSQL.Password") : "" ));
			strings.put("Database", (config.getString("Database.MYSQL.Database") 	!= null ? 	config.getString("Database.MYSQL.Database") : "Conquest" ));

			getPathSection(config, "ActiveWorlds").forEach(aWorld->{
				if (getWorlds().size() != 0)
					if (isWorld(aWorld))
						return;
				Bukkit.getWorlds().stream()
				.filter(world->world.getName().equals(aWorld))
				.forEach(world ->{
					CaptureRate.put(world, (20 * config.getLong("ActiveWorlds." + world.getName() + ".Combat.CaptureRate")));
					CapDistance.put(world, config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureDistance"));

					CaptureMaxY.put(world, config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureMaxY"));
					CaptureMinY.put(world, config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureMinY"));

					RespawnDelay.put(world, (20 * config.getLong("ActiveWorlds." + world.getName() + ".General.RespawnDelay")));
					TeleportDelay.put(world, (20 * config.getLong("ActiveWorlds." + world.getName() + ".General.TeleportDelay")));

					doubles.put("DistanceBetween", config.getDouble("ActiveWorlds." + world.getName() + ".Build.DistanceBetween"));
					booleans.put("DebugDynmapMarkers", config.getBoolean("ActiveWorlds." + world.getName() + ".Debug.DynmapMarkers"));
					addWorld(world);
				});
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean loadLanguages(){
		Config lang = getConfig("Language");

		try {
//General
			language.put("Prefix", 							lang.getString("Language.General.PluginPrefix"));
			language.put("JoinSuccess", 					lang.getString("Language.General.JoinSuccess"));
			language.put("LeaveSuccess", 				lang.getString("Language.General.LeaveSuccess"));
			language.put("Teleported", 					lang.getString("Language.General.Teleported"));
			language.put("startTP", 							lang.getString("Language.General.startTP"));
			language.put("Reloaded", 						lang.getString("Language.General.Reloaded"));
			language.put("unBindSuccess", 				lang.getString("Language.General.unBindSuccess"));
			language.put("MoveSuccess", 				lang.getString("Language.General.MoveSuccess"));
			language.put("RemoveSuccess", 			lang.getString("Language.General.RemoveSuccess"));
			/*languages.put("ConfigReloaded", 		lang.getString("Language.General.ConfigReloaded"));*/
			//Admin
			language.put("adminMoveSuccess", 		lang.getString("Language.General.Admin.MoveSuccess"));
//Kingdoms
			language.put("BelongTo", 						lang.getString("Language.Kingdoms.Belonging"));
			language.put("KingdomCreated", 			lang.getString("Language.Kingdoms.KingdomCreated"));
			language.put("KingdomDeleted", 			lang.getString("Language.Kingdoms.KingdomDeleted"));
			language.put("KingdomExist", 				lang.getString("Language.Kingdoms.KingdomExist"));
			//Admin
			language.put("editKingdomName", 		lang.getString("Language.Kingdoms.Admin.EditName"));
			language.put("editKingdomKing", 		lang.getString("Language.Kingdoms.Admin.EditKing"));
			language.put("editKingdomSpawn", 		lang.getString("Language.Kingdoms.Admin.EditSpawn"));
			language.put("editKingdomLocation", 	lang.getString("Language.Kingdoms.Admin.EditLocation"));
//Towns (Objectives)
			language.put("CapureTownSuccess", 	lang.getString("Language.Towns.CaptureSuccess"));
			language.put("CapureTownFail",	 		lang.getString("Language.Towns.CaptureFail"));
			language.put("TownCreated", 				lang.getString("Language.Towns.TownCreated"));
			language.put("TownDeleted", 				lang.getString("Language.Towns.TownDeleted"));
			//Admin
			language.put("editTownName", 			lang.getString("Language.Towns.Admin.EditName"));
			language.put("editTownOwner", 			lang.getString("Language.Towns.Admin.EditOwner"));
			language.put("editTownChildren", 		lang.getString("Language.Towns.Admin.EditChildren"));
			language.put("editTownLocation", 		lang.getString("Language.Towns.Admin.EditLocation"));
			language.put("editTownSpawn", 			lang.getString("Language.Towns.Admin.EditSpawn"));
//Villages (Outposts)
			language.put("CapureVillageSuccess", 	lang.getString("Language.Villages.CaptureSuccess"));
			language.put("CaptureVillageFail", 		lang.getString("Language.Villages.CaptureFail"));
			language.put("VillageCreated", 				lang.getString("Language.Villages.VillageCreated"));
			language.put("VillageDeleted", 				lang.getString("Language.Villages.VillageDeleted"));
			//Admin
			language.put("editVillageName", 			lang.getString("Language.Villages.Admin.EditName"));
			language.put("editVillageOwner", 			lang.getString("Language.Villages.Admin.EditOwner"));
			language.put("editVillageParent", 			lang.getString("Language.Villages.Admin.EditParent"));
			language.put("editVillageLocation", 		lang.getString("Language.Villages.Admin.EditLocation"));
			language.put("editVillageSpawn", 		lang.getString("Language.Villages.Admin.EditSpawn"));
//Errors
			language.put("AlreadyExists", 				lang.getString("Errors.AlreadyExists"));
			language.put("ToClose", 						lang.getString("Errors.ToClose"));
//Broadcasts
			language.put("CapComplete", 				lang.getString("Language.Broadcast.CapComplete"));
			language.put("Distress", 							lang.getString("Language.Broadcast.distress"));
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

//Data Files
	//LOAD
	private static boolean loadKingdoms(World world){
		Config kingdoms = getConfig("Kingdoms");
		try{
			getPathSection(kingdoms, world.getName()).forEach(uniqueID ->{
				boolean exists = false;
				for (Kingdom kingdom : Kingdom.getKingdoms()) {
					if (kingdom.getUUID().equals(UUID.fromString(uniqueID)))
						exists = true;
				}
				if (exists)
					return;

				Kingdom kingdom = new Kingdom(kingdoms.getString(world.getName() + "." + uniqueID + ".Name"),
						kingdoms.getString(world.getName() + "." + uniqueID + ".King"),
						uniqueID,
						kingdoms.getLocation(world.getName() + "." + uniqueID + ".Location"),
						kingdoms.getLocation(world.getName() + "." + uniqueID + ".Spawn"),
						kingdoms.getInt(world.getName() + "." + uniqueID + ".Color"));
				kingdom.create(null);
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@SuppressWarnings("deprecation")
	private static boolean loadUsers(World world){
		Config users = getConfig("Users");

		try{
			Kingdom.getKingdoms(world).forEach(kingdom->{
				getPathSection(users, world.getName()).forEach(kUUID->{
					if (kUUID.equals(kingdom.getUUID().toString())){
						getPathSection(users, world.getName() + "." + kUUID).forEach(uUUID->{
							 PlayerWrapper wrapper = new PlayerWrapper(((Player) Bukkit.getOfflinePlayer(uUUID)));
							 wrapper.setKingdom(Kingdom.getKingdom(kUUID));
						});
					}
				});
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			ChatManager.Console("Most Likly PathSection");
			return false;
		}
	}
	private static boolean loadTowns(World world){
		Config towns = getConfig("Towns");

		try {

			getPathSection(towns, world.getName()).forEach(uniqueID ->{
				boolean exists = false;
				for (Town town : Town.getTowns()) {
					if (town.getUUID().equals(UUID.fromString(uniqueID)))
						exists = true;
				}
				if (exists)
					return;
				ArrayList<Village> villages = new ArrayList<>();
				if (!Validate.isNull(Village.getVillages())) {
					Village.getVillages().stream()
					.filter(village->!Validate.isNull(village.getParent()))
					.forEach(child->{
						if (child.getUUID().toString().equals(uniqueID))
							villages.add(child);
					});
				}
				new Town(towns.getString(world.getName() + "." + uniqueID + ".Name")
						,uniqueID
						,towns.getLocation(world.getName() + "." + uniqueID + ".Location")
						,towns.getLocation(world.getName() + "." + uniqueID + ".Spawn")
						,Kingdom.getKingdom(towns.getString(world.getName() + "." + uniqueID + ".Owner"))
						,villages);
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	private static boolean loadVillages(World world){
		Config villages = getConfig("Villages");

		try {
			getPathSection(villages, world.getName()).forEach(uniqueID->{
				boolean exists = false;
				for (Village village : Village.getVillages())
					if (village.getUUID().toString().equals(uniqueID))
						exists = true;
				if (exists)
					return;


				Village op = new Village(
						villages.getString(world.getName() + "." + uniqueID + ".Name"),
						uniqueID, 
						villages.getLocation(world.getName() + "." + uniqueID + ".Location"),
						villages.getLocation(world.getName() + "." + uniqueID + ".Spawn"),
						Kingdom.getKingdom(villages.getString(world.getName() + "." + uniqueID + ".Owner")),
						Town.getTown(villages.getString(world.getName() + "." + uniqueID + ".Parent")));
				op.setLocation(villages.getLocation(world.getName() + "." + uniqueID + ".Location"));
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	//SAVE
	public static boolean saveKingdoms(World world){
		Config kingdoms = getConfig("Kingdoms");

		try {
			Kingdom.getKingdoms().forEach(kingdom->{
				kingdoms.set(world.getName() + "." + kingdom.getUUID().toString() + ".Name", kingdom.getName());
				if (!Validate.isNull(kingdom.getKing()))
					kingdoms.set(world.getName() + "." + kingdom.getUUID().toString() + ".King", kingdom.getKing().getUniqueId().toString());
				kingdoms.set(world.getName() + "." + kingdom.getUUID().toString() + ".Color", kingdom.getColor());
				kingdoms.setLocation(world.getName() + "." + kingdom.getUUID().toString() + ".Location", kingdom.getLocation());
				kingdoms.setLocation(world.getName() + "." + kingdom.getUUID().toString() + ".Spawn", kingdom.getSpawn());
				kingdoms.save();
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean saveUsers(World world){
		Config users = getConfig("Users");

		try {
			Kingdom.getKingdoms(world).forEach(kingdom->{
				kingdom.getMembers().forEach(user->{
					users.createSection(world.getName() + "." + kingdom.getUUID().toString() + "." + user.toString());
					users.save();
				});
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean saveTowns(World world){
		Config towns = getConfig("Towns");
		try {
			Town.getTowns(world).forEach(town->{
				towns.set(world.getName() + "." + town.getUUID().toString() + ".Name", town.getName());
				towns.set(world.getName() + "." + town.getUUID().toString() + ".Owner", town.getOwner().getUUID().toString());
				if (!Validate.isNull(town.getChildren()))
					town.getChildren().forEach(child->{
						towns.createSection(world.getName() + "." + town.getUUID().toString() + ".Children." + child.getUUID().toString());
					});
				towns.setLocation(world.getName() + "." + town.getUUID().toString() + ".Location", town.getLocation());
				towns.setLocation(world.getName() + "." + town.getUUID().toString() + ".Spawn", town.getSpawn());
				towns.save();
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean saveVillages(World world){
		Config villages = getConfig("Villages");
		try {
			Village.getVillages(world).forEach(village->{
				villages.set(world.getName() 
						+ "." + village.getUUID().toString() 
						+ ".Name", village.getName());
				villages.set(world.getName() + "." + village.getUUID().toString() + ".Owner", village.getOwner().getUUID().toString());
				if (!Validate.isNull(village.getParent()))
					villages.set(world.getName() + "." + village.getUUID().toString() + ".Parent", village.getParent().getUUID().toString());
				villages.setLocation(world.getName() + "." + village.getUUID().toString() + ".Location", village.getLocation());
				villages.setLocation(world.getName() + "." + village.getUUID().toString() + ".Spawn", village.getSpawn());
				villages.save();
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	//REMOVE
	public static void removeKingdoms(World world){
		Config kingdoms = getConfig("Kingdoms");
		try {
			getPathSection(kingdoms, world.getName()).forEach(kingdomUUID->{
				//Remove if object is in config but removed in game.
				if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUUID)))){
					kingdoms.set(world.getName(), null);
					removeMsg.put("&6| --&3 [&6Kingdom&3] " + kingdomUUID 
							+ " [&6" + world.getName() + "&3]", true);
				}
			});
			kingdoms.save();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void removeTowns(World world){
		Config towns = getConfig("Towns");
		try {

			getPathSection(towns, world.getName()).forEach(townUniqueID->{
				//Remove if object is in config but removed in game.
				if (Validate.isNull(Town.getTown(UUID.fromString(townUniqueID)))){
					towns.set(world.getName(), null);
					removeMsg.put("&6| --&3 [&6Town&3] " + townUniqueID 
							+ " [&6" + world.getName() + "&3]", true);
				}
			});
			towns.save();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void removeVillages(World world){
		Config villages = getConfig("Villages");
		try {

			getPathSection(villages, world.getName()).forEach(villageUniqueID->{
				//Remove if object is in config but removed in game.
				if (Validate.isNull(Village.getVillage(UUID.fromString(villageUniqueID)))){
					villages.set(world.getName(), null);
					removeMsg.put("&6| --&3 [&6Village&3] " + villageUniqueID 
							+ " [&6" + world.getName() + "&3]", true);
				}
			});
			villages.save();
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	public static void removeUsers(World world){
		Config users = getConfig("Users");

		try {
			getPathSection(users, world.getName()).forEach(kingdomUniqueID ->{
				if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUniqueID)))) {
					users.set(world.getName() + "." + kingdomUniqueID, null);;
					users.save();
					removeMsg.put("&6| --&3 [&6User|Kingdom&3] " + Kingdom.getKingdom(kingdomUniqueID).getName() 
							+ " [&6" + world.getName() + "&3]", true);
					return;
				}
				getPathSection(users, world.getName() + "." + kingdomUniqueID).forEach(uniqueID ->{
					if (!Kingdom.getKingdom(UUID.fromString(kingdomUniqueID)).hasMember(UUID.fromString(uniqueID)) 
							|| Kingdom.getKingdom(UUID.fromString(kingdomUniqueID)).getMembers().size() == 0){
						users.set(world.getName() + "." + kingdomUniqueID + "." + uniqueID, null);
						removeMsg.put("&6| --&3 [&6User&3] " + uniqueID 
								+ " [&6" + Kingdom.getKingdom(UUID.fromString(kingdomUniqueID)).getLocation().getWorld().getName() + "&3]", true);
					}
				});
			});
			users.save();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<World> Worlds = new ArrayList<>();
	public static ArrayList<World> getWorlds(){
		return Worlds;
	}
	public static void addWorld(World world) {
		Worlds.add(world);
	}
	public static World getWorld(String name) {
		for (World world : getWorlds())
			if (world.getName().equals(name))
				return world;
		return null;
	}
	public static boolean isWorld(String name) {
		for (World world : getWorlds())
			if (world.getName().equals(name))
				return true;
		return false;
	}

	public static HashMap<String, String> strings = new HashMap<>();
	public static HashMap<String, String> language = new HashMap<>();
	public static HashMap<String, Boolean> booleans = new HashMap<>();
	public static HashMap<String, Integer> integers = new HashMap<>();
	public static HashMap<String, Double> doubles = new HashMap<>();
	public static String getChat(String str) {
		return language.get(str);
	}
	public static boolean getBooleans(String str) {
		return booleans.get(str);
	}
	public static int getIntegers(String str) {
		return integers.get(str);
	}

	private static ArrayList<Config> configs = new ArrayList<>();
	public static boolean hasConfigs(){
		if (configs.size() != 0)
			return true;
		return false;
	}
	public static ArrayList<Config> getConfigs(){
		return configs;
	}
	public static Config getConfig(String name){
		for (Config c : getConfigs()){
			if (c.getName().replace(".yml", "").equals(name))
				return c;
		}
		ChatManager.Console("§4ERROR: Could not find config: " + name + " file");
		ChatManager.Console("§4: Wrong name?");
		return null;
	}
	public static void addConfig(Config config){
		configs.add(config);
	}

	public static void clearAll() {
		configs.clear();
	}

	@Override
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public static void registerFiles(){
		new Config(null, "Config.yml", "Config.yml");
		new Config(null, "Language.yml", "Language.yml");
		new Config("Data", "Kingdoms.yml");
		new Config("Data", "Towns.yml");
		new Config("Data", "Villages.yml");
		new Config("Data", "Users.yml");
	}
}