package eu.kingconquest.conquest.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.Validate;


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

		String pathway = (path == null) 
				? Main.getInstance().getDataFolder() + File.separator + fileName 
						: Main.getInstance().getDataFolder() + File.separator + path + File.separator + fileName;
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
		if (Validate.notNull(pathway) && Validate.notNull(key.getWorld())){
			this.set(pathway + ".X", key.getX());
			this.set(pathway + ".Y", key.getY());
			this.set(pathway + ".Z", key.getZ());
			return;
		}
	}

	/**
	 * Get location from config
	 * @param pathway - String
	 * @return Location
	 */
	public Location getLocation(World world, String pathway){
		Double X = this.getDouble(pathway + ".X");
		Double Y = this.getDouble(pathway + ".Y");
		Double Z = this.getDouble(pathway + ".Z");
		Location loc = new Location(world, X, Y, Z);
		return loc;
	}

//Config Specific
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
		if (loadLanguage())
			loadMsg.put("&6| --&3 Language.yml [&6Global&3]", true);
		else
			loadMsg.put("&6| --&3 Language.yml [&6Global&3]", false);
		loadFiles();
	}
	public static void loadFiles() {
		getWorlds().forEach(aWorld->{
			headerMsg = "&6| - &aLoaded:";
			Bukkit.getWorlds().stream()
			.filter(world->Bukkit.getWorld(aWorld).equals(world))
			.forEach(world->{
				loadKingdoms(world);
				loadUsers(world);
				loadTowns(world);
				loadVillages(world);
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

		Kingdom.clear();
		Town.clear();
		Village.clear();
	}
	public static void removeFromFiles(){
		headerMsg = "&6| - &cRemoved:";
		worlds.forEach(uniqueID->{
			World world = Bukkit.getWorld(uniqueID);
			removeKingdoms(world);
			removeTowns(world);
			removeVillages(world);
			removeUsers(world);
		});
	}
	public static void saveFiles() {
		headerMsg = "&6| - &aSaved:";
		worlds.forEach(uniqueID->{
			World world = Bukkit.getWorld(uniqueID);
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
		return c.getConfigurationSection(path).getKeys(false);
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

	//Config Loads
	public static boolean loadDefault(){
		Config config = getConfig("Config");

		strings.put("Port", 	(config.getString("Database.MySql.Port") 		!= null ? 	config.getString("Database.MySql.Port") : "3306" ));
		strings.put("Host", 	(config.getString("Database.MySql.Host") 		!= null ? 	config.getString("Database.MySql.Host") : "localhost" ));
		strings.put("User", 	(config.getString("Database.MySql.Username") 	!= null ? 	config.getString("Database.MySql.Username") : "root" ));
		strings.put("Pass", 	(config.getString("Database.MySql.Password") 	!= null ? 	config.getString("Database.MySql.Password") : "" ));
		strings.put("Database", (config.getString("Database.MYSQL.Database") 	!= null ? 	config.getString("Database.MYSQL.Database") : "Conquest" ));
		if (!config.isSet("ActiveWorlds"))
			return false;
		getPathSection(config, "ActiveWorlds").forEach(aWorld->{
			if (getWorlds().size() != 0 && isActiveWorld(aWorld))
				return;
			Bukkit.getWorlds().stream()
			.filter(world->world.getName().equals(aWorld))
			.forEach(world ->{

				HashMap<String ,Double> dmap=new HashMap<>(); 
				HashMap<String ,Long> lmap=new HashMap<>(); 
				HashMap<String ,Boolean> bmap=new HashMap<>(); 
				dmap.put("CapCash", config.getDouble("ActiveWorlds." + world.getName() + ".Income.CapCash"));
				dmap.put("CaptureDistance", config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureDistance"));
				dmap.put("CaptureMaxY", config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureMaxY"));
				dmap.put("CaptureMinY", config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureMinY"));

				lmap.put("CaptureRate", (20 * config.getLong("ActiveWorlds." + world.getName() + ".Combat.CaptureRate")));
				lmap.put("RespawnDelay", (20 * config.getLong("ActiveWorlds." + world.getName() + ".General.RespawnDelay")));
				lmap.put("TeleportDelay", (20 * config.getLong("ActiveWorlds." + world.getName() + ".General.TeleportDelay")));

				bmap.put("DebugDynmapMarkers", config.getBoolean("ActiveWorlds." + world.getName() + ".Debug.DynmapMarkers"));
				
				doubles.put(world.getUID(), dmap);
				longs.put(world.getUID(), lmap);
				booleans.put(world.getUID(), bmap);
				addWorld(world);
			});
		});
		return true;
	}
	public static boolean loadLanguage(){
		Config lang = getConfig("Language");

		try {
			//General
			language.put("Prefix", 							lang.getString("Language.General.PluginPrefix"));
			language.put("JoinSuccess", 					lang.getString("Language.General.JoinSuccess"));
			language.put("LeaveSuccess", 				lang.getString("Language.General.LeaveSuccess"));
			language.put("Teleported", 					lang.getString("Language.General.Teleported"));
			language.put("StartTP", 							lang.getString("Language.General.startTP"));
			language.put("UnBindSuccess", 				lang.getString("Language.General.unBindSuccess"));
			language.put("MoveSuccess", 				lang.getString("Language.General.MoveSuccess"));
			language.put("RemoveSuccess", 			lang.getString("Language.General.RemoveSuccess"));
			/*languages.put("ConfigReloaded", 		lang.getString("Language.General.ConfigReloaded"));*/
			//Admin
			language.put("AdminMoveSuccess", 		lang.getString("Language.General.Admin.MoveSuccess"));
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
			language.put("CaptureTownSuccess", 	lang.getString("Language.Towns.CaptureSuccess"));
			language.put("CaptureTownFail",	 		lang.getString("Language.Towns.CaptureFail"));
			language.put("TownCreated", 				lang.getString("Language.Towns.TownCreated"));
			language.put("TownDeleted", 				lang.getString("Language.Towns.TownDeleted"));
			//Admin
			language.put("editTownName", 			lang.getString("Language.Towns.Admin.EditName"));
			language.put("editTownOwner", 			lang.getString("Language.Towns.Admin.EditOwner"));
			language.put("editTownChildren", 		lang.getString("Language.Towns.Admin.EditChildren"));
			language.put("editTownLocation", 		lang.getString("Language.Towns.Admin.EditLocation"));
			language.put("editTownSpawn", 			lang.getString("Language.Towns.Admin.EditSpawn"));
			//Villages (Outposts)
			language.put("CaptureVillageSuccess", lang.getString("Language.Villages.CaptureSuccess"));
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
			language.put("AlreadyExists", 				lang.getString("Language.Errors.AlreadyExists"));
			language.put("ToClose", 						lang.getString("Language.Errors.ToClose"));
			//Broadcasts
			language.put("Captured", 						lang.getString("Language.Broadcast.Captured"));
			language.put("TownCaptured", 						lang.getString("Language.Broadcast.TownCaptured"));
			language.put("WarnDistress", 				lang.getString("Language.Broadcast.WarnDistress"));
			language.put("WarnNeutral", 				lang.getString("Language.Broadcast.WarnNeutral"));
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	//Data Files
	//LOAD
	private static void loadKingdoms(World world){
		Config config = getConfig("Kingdoms");

		if (!config.isSet(world.getName()))
			return;
			for (String uniqueID : getPathSection(config, world.getName())){
				if (Validate.notNull(Kingdom.getKingdom(UUID.fromString(uniqueID), world))) 
					return; //Kingdom already loaded!

			new Kingdom(config.getString(world.getName() + "." + uniqueID + ".Name"),
					config.getString(world.getName() + "." + uniqueID + ".King"),
					uniqueID,
					config.getLocation(world, world.getName() + "." + uniqueID + ".Location"),
					config.getLocation(world, world.getName() + "." + uniqueID + ".Spawn"),
					config.getInt(world.getName() + "." + uniqueID + ".Color"));
		}
			loadMsg.put("&6| --&3 Kingdoms.yml [&6" + world.getName() + "&3]", true);
	}
	private static void loadUsers(World world){
		Config config = getConfig("Users");

		if (!config.isSet(world.getName()))
			return;
		Kingdom.getKingdoms(world).forEach(kingdom->{
			getPathSection(config, world.getName()).forEach(kUUID->{
				if (kUUID.equals(kingdom.getUUID().toString())){
					if (!config.isSet(world.getName() + "." + kUUID))
						return;
					getPathSection(config, world.getName() + "." + kUUID).forEach(uUUID->{
						if (Validate.notNull(Bukkit.getOfflinePlayer(UUID.fromString(uUUID)))){
							PlayerWrapper wrapper = new PlayerWrapper(UUID.fromString(uUUID));
							wrapper.setKingdom(kingdom.getUUID());
							kingdom.addMember(UUID.fromString(uUUID));
						}
					});
				}
			});
		});
		loadMsg.put("&6| --&3 Users.yml [&6" + world.getName() + "&3]", true);
	}
	private static void loadTowns(World world){
		Config config = getConfig("Towns");

		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(uniqueID ->{
			if (Validate.notNull(Town.getTown(UUID.fromString(uniqueID), world)))
				return; //Town already loaded!
			
			new Town(config.getString(world.getName() + "." + uniqueID + ".Name")
					,uniqueID
					,config.getLocation(world, world.getName() + "." + uniqueID + ".Location")
					,config.getLocation(world, world.getName() + "." + uniqueID + ".Spawn")
					,Kingdom.getKingdom(UUID.fromString(config.getString(world.getName() + "." + uniqueID + ".Owner")), world));
		});
		loadMsg.put("&6| --&3 Towns.yml [&6" + world.getName() + "&3]", true);
	}
	private static void loadVillages(World world){
		Config config = getConfig("Villages");


		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(uniqueID->{
			Town parent = null;
			if (Validate.notNull(Village.getVillage(UUID.fromString(uniqueID), world))) 
				return; //Village already loaded!
			if (config.isSet((world.getName() + "." + uniqueID + ".Parent")))
				parent = Town.getTown(UUID.fromString(config.getString(world.getName() + "." + uniqueID + ".Parent")), world);
			
			Village village = new Village(
					config.getString(world.getName() + "." + uniqueID + ".Name"),
					uniqueID, 
					config.getLocation(world, world.getName() + "." + uniqueID + ".Location"),
					config.getLocation(world, world.getName() + "." + uniqueID + ".Spawn"),
					Kingdom.getKingdom(UUID.fromString(config.getString(world.getName() + "." + uniqueID + ".Owner")), world),
					Kingdom.getKingdom(UUID.fromString(config.getString(world.getName() + "." + uniqueID + ".PreOwner")), world),
					parent);

			//Add Child to Parent
			if (village.hasParent())
				village.getParent().addChild(village);

			if (Validate.notNull(village.getOwner()))
				if (!village.getOwner().isNeutral())
					village.setProgress(100.0d);
		});
		loadMsg.put("&6| --&3 Villages.yml [&6" + world.getName() + "&3]", true);
	}

	//SAVE
	public static boolean saveKingdoms(World world){
		Config config = getConfig("Kingdoms");
		try {
			Kingdom.getKingdoms().forEach(kingdom->{
				if (!kingdom.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getName() + "." + kingdom.getUUID().toString() + ".Name", kingdom.getName());
				if (Validate.notNull(kingdom.getKing()))
					config.set(world.getName() + "." + kingdom.getUUID().toString() + ".King", kingdom.getKing().getUniqueId().toString());
				config.set(world.getName() + "." + kingdom.getUUID().toString() + ".Color", kingdom.getColor());
				config.setLocation(world.getName() + "." + kingdom.getUUID().toString() + ".Location", kingdom.getLocation());
				config.setLocation(world.getName() + "." + kingdom.getUUID().toString() + ".Spawn", kingdom.getSpawn());
				config.save();
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean saveUsers(World world){
		Config config = getConfig("Users");

		try {
			Kingdom.getKingdoms(world).forEach(kingdom->{
				kingdom.getMembers().forEach(user->{
					config.createSection(world.getName() + "." + kingdom.getUUID().toString() + "." + user.toString());
					config.save();
				});
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean saveTowns(World world){
		Config config = getConfig("Towns");
		try {
			Town.getTowns(world).forEach(town->{
				if (!town.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getName() + "." + town.getUUID().toString() + ".Name", town.getName());
				config.set(world.getName() + "." + town.getUUID().toString() + ".Owner", town.getOwner().getUUID().toString());
				if (town.hasChildren()){
					town.getChildren().forEach(child->{
						config.createSection(world.getName() + "." + town.getUUID().toString() + ".Children." + child.getUUID().toString());
					});
				}
				config.setLocation(world.getName() + "." + town.getUUID().toString() + ".Location", town.getLocation());
				config.setLocation(world.getName() + "." + town.getUUID().toString() + ".Spawn", town.getSpawn());
				config.save();
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean saveVillages(World world){
		Config config = getConfig("Villages");
		try {
			Village.getVillages(world).forEach(village->{
				if (!village.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getName() 
						+ "." + village.getUUID().toString() 
						+ ".Name", village.getName());
				config.set(world.getName() + "." + village.getUUID().toString() + ".Owner", village.getOwner().getUUID().toString());
				if (Validate.notNull(village.getPreOwner()))
					config.set(world.getName() + "." + village.getUUID().toString() + ".PreOwner", village.getPreOwner().getUUID().toString());
				if (village.hasParent())
					config.set(world.getName() + "." + village.getUUID().toString() + ".Parent", village.getParent().getUUID().toString());
				config.setLocation(world.getName() + "." + village.getUUID().toString() + ".Location", village.getLocation());
				config.setLocation(world.getName() + "." + village.getUUID().toString() + ".Spawn", village.getSpawn());
				config.save();
			});
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	//REMOVE
	public static void removeKingdoms(World world){
		Config config = getConfig("Kingdoms");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(kingdomUUID->{
			//Remove Kingdom from config if removed from game
			if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUUID), world))){
				config.set(world.getName(), null);
				removeMsg.put("&6| --&3 [&6Kingdom&3] " + kingdomUUID
						+ " [&6" + world.getName() + "&3]", true);
			}
		});
		config.save();
	}
	public static void removeTowns(World world){
		Config config = getConfig("Towns");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(townUniqueID->{
			//Remove Town from config if removed from game
			if (Validate.isNull(Town.getTown(UUID.fromString(townUniqueID), world))){
				config.set(world.getName(), null);
				removeMsg.put("&6| --&3 [&6Town&3] " +  townUniqueID 
						+ " [&6" + world.getName() + "&3]", true);
			}
		});
		config.save();
	}
	public static void removeVillages(World world){
		Config config = getConfig("Villages");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(villageUniqueID->{
			//Remove Village from config if removed from game
			if (Validate.isNull(Village.getVillage(UUID.fromString(villageUniqueID), world))){
				config.set(world.getName(), null);
				removeMsg.put("&6| --&3 [&6Village&3] " + villageUniqueID  
						+ " [&6" + world.getName() + "&3]", true);
			}
		});
		config.save();
	} 
	public static void removeUsers(World world){
		Config config = getConfig("Users");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(kingdomUniqueID ->{
			if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUniqueID), world))) {
				config.set(world.getName() + "." + kingdomUniqueID, null);
				config.save();
				removeMsg.put("&6| --&3 [&6User|Kingdom&3] " + kingdomUniqueID 
						+ " [&6" + world.getName() + "&3]", true);
				return;
			}
			getPathSection(config, world.getName() + "." + kingdomUniqueID).forEach(uniqueID ->{
				if (!Kingdom.getKingdom(UUID.fromString(kingdomUniqueID), world).hasMember(UUID.fromString(uniqueID)) 
						|| Kingdom.getKingdom(UUID.fromString(kingdomUniqueID), world).getMembers().size() == 0){
					config.set(world.getName() + "." + kingdomUniqueID + "." + uniqueID, null);
					removeMsg.put("&6| --&3 [&6User&3] " + uniqueID 
							+ " [&6" + world.getName() + "&3]", true);
				}
			});
		});
		config.save();
	}

	private static ArrayList<UUID> worlds = new ArrayList<UUID>();
	public static ArrayList<UUID> getWorlds(){
		return worlds;
	}
	public static void addWorld(World world) {
		worlds.add(world.getUID());
	}
	public static World getWorld(UUID uuid) {
		for (UUID uniqueID : worlds){
			Validate.isNull(Bukkit.getWorld(uniqueID), "Test 1");
			if (Bukkit.getWorld(uniqueID).getUID().equals(uuid))
				return Bukkit.getWorld(uniqueID);
		}
		return null;
	}
	public static boolean isActiveWorld(String name) {
		for (UUID uniqueID : worlds){
			Validate.isNull(Bukkit.getWorld(uniqueID), "Not a known world UUID");
			if (Bukkit.getWorld(uniqueID).getName().equals(name))
				return true;
		}
		return false;
	}

	//UUID of World
	public static HashMap<String, String> strings = new HashMap<String, String>();
	public static HashMap<String, String> language = new HashMap<String, String>();
	public static HashMap<UUID, HashMap<String, Boolean>> booleans = new HashMap<>();
	public static HashMap<UUID, HashMap<String, Integer>> integers = new HashMap<>();
	public static HashMap<UUID, HashMap<String, Double>> doubles = new HashMap<>();
	public static HashMap<UUID, HashMap<String, Long>> longs = new HashMap<>();
	public static String getChat(String str) {
		return language.get(str);
	}
	public static boolean getBooleans(String str, Location loc) {
		HashMap<String, Boolean> map2 = booleans.get(loc.getWorld().getUID()) ;
		return map2.get(str);
	}
	public static int getIntegers(String str, Location loc) {
		HashMap<String, Integer> map2 = integers.get(loc.getWorld().getUID());
		return map2.get(str);
	}
	public static Double getDoubles(String str, Location loc) {
		HashMap<String, Double> map2 = doubles.get(loc.getWorld().getUID()) ;
		return map2.get(str);
	}
	public static Long getLongs(String str, Location loc) {
		HashMap<String, Long> map2 = longs.get(loc.getWorld().getUID());
		return map2.get(str);
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
			if (c.getName().replace(".yml", "").equals(name)){
				c.reload();
				return c;
			}
		}
		ChatManager.Console("&4ERROR: Could not find config: " + name + " file");
		ChatManager.Console("&4: Wrong name?");
		return null;
	}
	public static void addConfig(Config config){
		configs.add(config);
	}

	public static void clear() {
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