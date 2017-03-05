package eu.kingconquest.conquest.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class YmlStorage extends YamlConfiguration{
	private String name;
	private File file;
	private String defaults;

	//Instance Specific
	/**
	 * Creates new Config File, without defaults
	 * @param path - String
	 * @param fileName - String
	 */
	public YmlStorage(String path, String fileName) {
		this(path, fileName, null);
	}

	/**
	 * Creates new Config File, with defaults
	 * @param path - String
	 * @param fileName - String
	 * @param defaultsName - String
	 */
	public YmlStorage(String path, String fileName, String defaultsName) {
		defaults = defaultsName;
		String pathway = (path == null) 
				? Main.getInstance().getDataFolder() + File.separator + fileName 
						: Main.getInstance().getDataFolder() + File.separator + path + File.separator + fileName;
		
		try{
			file = new File(pathway);
			reload();
			setName(fileName);
			addConfig(this);
			loadMsg.put("&6| --&3 " +  fileName, true);	
		}catch(Exception e){
			e.printStackTrace();
			loadMsg.put("&6| --&3 " +  fileName, false);	
		}
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
				saveConfig();
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
	public void saveConfig() {
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
	private static int saveTaskID = 0;

	public static void save(){
		if (saveTaskID > 0){
			saveTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new Runnable(){
				@Override
				public void run(){
					//Save data
					worlds.forEach(uniqueID->{
						World world = Bukkit.getWorld(uniqueID);
						saveKingdoms(world);
						saveTowns(world);
						saveVillages(world);
						saveUsers(world);
						saveKits(world);
					});
					saveMsg.clear();
				}
			}, 10, Long.valueOf(getStr("AutoSaveInterval"))).getTaskId();
		}else{
			headerMsg = "&6| - &aSaved:";
			worlds.forEach(uniqueID->{
				World world = Bukkit.getWorld(uniqueID);
				saveKingdoms(world);
				saveTowns(world);
				saveVillages(world);
				saveUsers(world);
				saveKits(world);
				output();
			});
		}
	}
	public static void load(){
		registerFiles();
		loadLanguage();
		loadDefault();
		getWorlds().forEach(aWorld->{
			World world = Bukkit.getWorld(aWorld);
			headerMsg = "&6| - &aLoaded:";
			loadKingdoms(world);
			loadUsers(world);
			loadTowns(world);
			loadVillages(world);
			loadKits(world);
		});
	}
	public static void remove(){
		headerMsg = "&6| - &cRemoved:";
		worlds.forEach(uniqueID->{
			World world = Bukkit.getWorld(uniqueID);
			removeKingdoms(world);
			removeUsers(world);
			removeTowns(world);
			removeVillages(world);
			removeKits(world);
		});
		output();
	}

	public static Set<String> getPathSection(YmlStorage c, String path){
		Validate.notNull(c.getConfigurationSection(path).getKeys(false), "&cPath Section Failure: \n&3" + path);
		return c.getConfigurationSection(path).getKeys(false);
	}

	public static void output(){
		if (loadMsg.containsValue(true)) {
			new Message(null, MessageType.CONSOLE, headerMsg);
			loadMsg.forEach((s,b)->{
				if (b) {
					new Message(null, MessageType.CONSOLE, s);
				}
			});
		}
		if (loadMsg.containsValue(false)) {
			new Message(null, MessageType.CONSOLE, errorMsg);
			loadMsg.forEach((s,b)->{
				if (!b)
					new Message(null, MessageType.CONSOLE, s);
			});
		}
		loadMsg.clear();

		if (saveMsg.containsValue(true)) {
			new Message(null, MessageType.CONSOLE, headerMsg);
			saveMsg.forEach((s,b)->{
				if (b) {
					new Message(null, MessageType.CONSOLE, s);
				}
			});
		}
		if (saveMsg.containsValue(false)) {
			new Message(null, MessageType.CONSOLE, errorMsg);
			saveMsg.forEach((s,b)->{
				if (!b)
					new Message(null, MessageType.CONSOLE, s);
			});
		}
		saveMsg.clear();

		if (removeMsg.containsValue(true)) {
			new Message(null, MessageType.CONSOLE, headerMsg);
			removeMsg.forEach((s,b)->{
				if (b) {
					new Message(null, MessageType.CONSOLE, s);
				}
			});
		}
		if (removeMsg.containsValue(false)) {
			new Message(null, MessageType.CONSOLE, errorMsg);
			removeMsg.forEach((s,b)->{
				if (!b)
					new Message(null, MessageType.CONSOLE, s);
			});
		}
		removeMsg.clear();
	}

	
	//LOAD
	//Config Loads
	public static boolean loadDefault(){
		YmlStorage config = getConfig("Config");

		strings.put("Port", 	(config.getString("Database.MySql.Port") 								!= null ? 	config.getString("Database.MySql.Port") 				: "3306" ));
		strings.put("Host", 	(config.getString("Database.MySql.Host") 								!= null ? 	config.getString("Database.MySql.Host") 			: "localhost" ));
		strings.put("Username", 	(config.getString("Database.MySql.Username") 				!= null ? 	config.getString("Database.MySql.Username") 	: "root" ));
		strings.put("Password", 	(config.getString("Database.MySql.Password") 				!= null ? 	config.getString("Database.MySql.Password") 	: "" ));
		strings.put("Database", (config.getString("Database.MySql.Database") 				!= null ? 	config.getString("Database.MySql.Database") 	: "Conquest" ));
		strings.put("AutoSaveInterval", (config.getString("Database.AutoSaveInterval") 	!= null ? 	config.getString("Database.AutoSaveInterval") 	: "5" ));
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
		YmlStorage lang = getConfig("Language");

		try{
			getPathSection(lang, "Language").forEach(path->{
				getPathSection(lang, "Language." + path).forEach(pathSection->{
					if (!pathSection.toLowerCase().equals("admin")){
						strings.put(pathSection, (lang.getString("Language." + path + "." + pathSection) != null ? lang.getString("Language." + path + "." + pathSection) : ""));
					}else{
						getPathSection(lang, "Language." + path + "." + pathSection).forEach(adminSection->{
							strings.put("Admin" +adminSection, (lang.getString("Language." + path + ".Admin." + adminSection) != null ? lang.getString("Language." + path + ".Admin." + adminSection) : ""));
						});
					}
				});
			});
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	//Data Files
	private static void loadKingdoms(World world){
		YmlStorage config = getConfig("Kingdoms");

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
		if (Validate.isNull(Kingdom.getKingdom("Neutral", world))){
			Kingdom kingdom = new Kingdom("Neutral", null, world.getSpawnLocation().clone(), -1);
			kingdom.create(null);
		}
	}
	private static void loadUsers(World world){
		YmlStorage config = getConfig("Users");

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
	}
	private static void loadTowns(World world){
		YmlStorage config = getConfig("Towns");

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
	}
	private static void loadVillages(World world){
		YmlStorage config = getConfig("Villages");


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
	}
	private static void loadKits(World world){
		try(Stream<Path> paths = Files.walk(Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Kits"))){
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		    		String kitUUID = filePath.getFileName().toString().replaceAll(".yml", "");
	    			if (Validate.notNull(Reward.getReward(UUID.fromString(kitUUID), world))) 
	    				return; //Kit already loaded!
	    			
		    		YmlStorage config = getConfig(kitUUID);
		    		if (!config.isSet(world.getName()))
		    			return;
		    			Reward kit = new Reward(
		    					config.getString(world.getName() + "." + kitUUID + ".Name")
		    					, world
		    					, config.getLong(world.getName() + "." + kitUUID + ".Cost")
		    					, config.getLong(world.getName() + "." + kitUUID + ".Cooldown")
		    					, UUID.fromString(config.getString(world.getName() + "." + kitUUID + ".Owner"))
		    					, UUID.fromString(kitUUID));
		    			//Get each type
		    			if (config.isSet(world.getName() + "." + kitUUID + ".Slots")){
		    				getPathSection(config, world.getName() + "." + kitUUID + ".Slots")
		    				.forEach(slot->{
		    					kit.addItem(kit.getItems().size() + 1, config.getItemStack(world.getName() + "." + kitUUID + ".Slots." + slot));	
		    				});
		    			}
		    		loadMsg.put("&6| --&3 " +  config.getName() + " [&6" + world.getName() + "&3]", true);	
		        }
		    });
		}
		catch (IOException e){
			e.printStackTrace();
		} 
	}


	//SAVE
	private static void saveKingdoms(World world){
		YmlStorage config = getConfig("Kingdoms");
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
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}
	private static void saveUsers(World world){
		YmlStorage config = getConfig("Users");

		try {
			Kingdom.getKingdoms(world).forEach(kingdom->{
				kingdom.getMembers().forEach(user->{
					config.createSection(world.getName() + "." + kingdom.getUUID().toString() + "." + user.toString());
					config.saveConfig();
				});
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}
	private static void saveTowns(World world){
		YmlStorage config = getConfig("Towns");
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
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}
	private static void saveVillages(World world){
		YmlStorage config = getConfig("Villages");
		try {
			Village.getVillages(world).forEach(village->{
				if (!village.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getName() + "." + village.getUUID().toString() + ".Name", village.getName());
				config.set(world.getName() + "." + village.getUUID().toString() + ".Owner", village.getOwner().getUUID().toString());
				if (Validate.notNull(village.getPreOwner()))
					config.set(world.getName() + "." + village.getUUID().toString() + ".PreOwner", village.getPreOwner().getUUID().toString());
				if (village.hasParent())
					config.set(world.getName() + "." + village.getUUID().toString() + ".Parent", village.getParent().getUUID().toString());
				config.setLocation(world.getName() + "." + village.getUUID().toString() + ".Location", village.getLocation());
				config.setLocation(world.getName() + "." + village.getUUID().toString() + ".Spawn", village.getSpawn());
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}
	private static void saveKits(World world){
		Reward.getRewards(world).forEach(kit ->{
			if (!kit.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
				return;
			YmlStorage config = new YmlStorage("Data" + File.separator + "Kits", kit.getUUID().toString() + ".yml");
			try {
				config.set(world.getName() + "." + kit.getUUID() + ".Name", kit.getName());
				config.set(world.getName() + "." + kit.getUUID() + ".Owner", kit.getOwnerUUID().toString());
				config.set(world.getName() + "." + kit.getUUID() + ".Cost", kit.getCost());
				config.set(world.getName() + "." + kit.getUUID() + ".Cooldown", kit.getCooldown());
				if (kit.getItems().size() < 1)
					return;
				kit.getItems().forEach((i, item)->{
					config.set(world.getName() + "." + kit.getUUID() + ".Slots." + i, item);
				});
				config.saveConfig();
				saveMsg.put("&6| --&3 " + config.getName(), true);
			}catch(Exception e){
				e.printStackTrace();
				saveMsg.put("&6| --&3 " + config.getName(), false);
			}
		});
	}


	//REMOVE
	private static void removeKingdoms(World world){
		YmlStorage config = getConfig("Kingdoms");
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
		config.saveConfig();
	}
	private static void removeTowns(World world){
		YmlStorage config = getConfig("Towns");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(townUUID->{
			//Remove Town from config if removed from game
			if (Validate.isNull(Town.getTown(UUID.fromString(townUUID), world))){
				config.set(world.getName(), null);
				removeMsg.put("&6| --&3 [&6Town&3] " +  townUUID 
						+ " [&6" + world.getName() + "&3]", true);
			}
		});
		config.saveConfig();
	}
	private static void removeVillages(World world){
		YmlStorage config = getConfig("Villages");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(villageUUID->{
			//Remove Village from config if removed from game
			if (Validate.isNull(Village.getVillage(UUID.fromString(villageUUID), world))){
				config.set(world.getName(), null);
				removeMsg.put("&6| --&3 [&6Village&3] " + villageUUID  
						+ " [&6" + world.getName() + "&3]", true);
			}
		});
		config.saveConfig();
	} 
	private static void removeUsers(World world){
		YmlStorage config = getConfig("Users");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(kingdomUUID ->{
			if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUUID), world))) {
				config.set(world.getName() + "." + kingdomUUID, null);
				config.saveConfig();
				removeMsg.put("&6| --&3 [&6User|Kingdom&3] " + kingdomUUID 
						+ " [&6" + world.getName() + "&3]", true);
				return;
			}
			getPathSection(config, world.getName() + "." + kingdomUUID).forEach(playerrUUID ->{
				if (!Kingdom.getKingdom(UUID.fromString(kingdomUUID), world).hasMember(UUID.fromString(playerrUUID)) 
						|| Kingdom.getKingdom(UUID.fromString(kingdomUUID), world).getMembers().size() == 0){
					config.set(world.getName() + "." + kingdomUUID + "." + playerrUUID, null);
					removeMsg.put("&6| --&3 [&6User&3] " + playerrUUID 
							+ " [&6" + world.getName() + "&3]", true);
				}
			});
		});
		config.saveConfig();
	}
	private static void removeKits(World world){
		try(Stream<Path> paths = Files.walk(Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Kits"))){
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		    		String kitUUID = filePath.getFileName().toString().replaceAll(".yml", "");
	    			if (Validate.isNull(Reward.getReward(UUID.fromString(kitUUID), world))){
						try{
							Files.delete(filePath);
						} catch (NoSuchFileException x) {
						    System.err.format("%s: no such" + " file or directory%n", filePath);
						}catch (IOException e){
							e.printStackTrace();
						}
	    			}
		    		removeMsg.put("&6| --&3 " +  kitUUID + " [&6" + world.getName() + "&3]", true);	
		        }
		    });
		}
		catch (IOException e){
			e.printStackTrace();
		} 
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
			if (Bukkit.getWorld(uniqueID).getUID().equals(uuid))
				return Bukkit.getWorld(uniqueID);
		}
		return null;
	}
	public static boolean isActiveWorld(String name) {
		for (UUID uniqueID : worlds){
			Validate.notNull(Bukkit.getWorld(uniqueID), "Not a known world UUID" + uniqueID);
			if (Bukkit.getWorld(uniqueID).getName().equals(name))
				return true;
		}
		return false;
	}

	//UUID of World
	public static HashMap<String, String> strings = new HashMap<String, String>();
	private static HashMap<UUID, HashMap<String, Boolean>> booleans = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Integer>> integers = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Double>> doubles = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Long>> longs = new HashMap<>();
	public static String getStr(String str) {
		return strings.get(str);
	}
	public static boolean getBoolean(String str, Location loc) {
		HashMap<String, Boolean> map2 = booleans.get(loc.getWorld().getUID()) ;
		return map2.get(str);
	}
	public static int getInteger(String str, Location loc) {
		HashMap<String, Integer> map2 = integers.get(loc.getWorld().getUID());
		return map2.get(str);
	}
	public static Double getDouble(String str, Location loc) {
		HashMap<String, Double> map2 = doubles.get(loc.getWorld().getUID()) ;
		return map2.get(str);
	}
	public static Long getLong(String str, Location loc) {
		HashMap<String, Long> map2 = longs.get(loc.getWorld().getUID());
		return map2.get(str);
	}

	private static ArrayList<YmlStorage> configs = new ArrayList<>();
	public static boolean hasConfigs(){
		if (configs.size() != 0)
			return true;
		return false;
	}
	public static ArrayList<YmlStorage> getConfigs(){
		return configs;
	}
	public static YmlStorage getConfig(String name){
		for (YmlStorage c : getConfigs()){
			if (c.getName().replace(".yml", "").equals(name)){
				c.reload();
				return c;
			}
		}
		new Message(null, MessageType.CONSOLE, "&4ERROR: Could not find config: " + name + " file");
		new Message(null, MessageType.CONSOLE, "&4: Wrong name?");
		return null;
	}
	public static void addConfig(YmlStorage config){
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
		new YmlStorage(null, "Config.yml", "Config.yml");
		new YmlStorage(null, "Language.yml", "Language.yml");
		new YmlStorage("Data", "Kingdoms.yml");
		new YmlStorage("Data", "Towns.yml");
		new YmlStorage("Data", "Villages.yml");
		new YmlStorage("Data", "Users.yml");

		try(Stream<Path> paths = Files.walk(Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Kits"))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		    		new YmlStorage("Data" + File.separator + "Kits", filePath.getFileName().toString());
					loadMsg.put("&6| --&3 " +  filePath.getFileName().toString(), true);		
		        }
		    });
		}
		catch (IOException e){
			new Message(null, MessageType.CONSOLE, "&6| --&3 No Kits");
		}
	}
}