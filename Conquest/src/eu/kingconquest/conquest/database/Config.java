package eu.kingconquest.conquest.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Kit;
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
		loadDefault();
		loadLanguage();
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
		Config lang = getConfig("Language");

		try {
			getPathSection(lang, "Language").forEach(path->{
				getPathSection(lang, "Language." + path).forEach(section->{
					if (!section.toLowerCase().equals("admin")){
						language.put(section, (lang.getString("Language." + path + "." + section) != null ? lang.getString("Language." + path + "." + section) : "&4Report: &7" + section + " Error!"));
					}else{
						getPathSection(lang, "").forEach(adminSection->{
							language.put(section, (lang.getString("Language." + path + ".Admin." + section) != null ? lang.getString("Language." + path + ".Admin." + section) : "&4Report: &7" + section + " Error!"));
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
		if (Validate.isNull(Kingdom.getKingdom("Neutral", world))){
			Kingdom kingdom = new Kingdom("Neutral", null, world.getSpawnLocation().clone(), -1);
			kingdom.create(null);
		}
		loadMsg.put("&6| --&3 " +  config.getName() + " [&6" + world.getName() + "&3]", true);
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
		loadMsg.put("&6| --&3 " +  config.getName() + " [&6" + world.getName() + "&3]", true);
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
		loadMsg.put("&6| --&3 " +  config.getName() + " [&6" + world.getName() + "&3]", true);
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
		loadMsg.put("&6| --&3 " +  config.getName() + " [&6" + world.getName() + "&3]", true);
	}
	private static void loadKits(World world){
		Config config = getConfig("Kits");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(kitUUID->{
			if (Validate.notNull(Kit.getKit(UUID.fromString(kitUUID), world))) 
				return; //Kit already loaded!
			Kit kit = new Kit(
					config.getString(world.getName() + "." + kitUUID + ".Name")
					, world
					, config.getDouble(world.getName() + "." + kitUUID + ".Cost")
					, config.getLong(world.getName() + "." + kitUUID + ".Cooldown")
					, UUID.fromString(kitUUID));
			if (config.isSet(world.getName() + "." + kitUUID + ".Type") 
					&& config.isSet(world.getName() + "." + kitUUID + ".Amount")){
				//Get each type
				getPathSection(config, config.getString(world.getName() + "." + kitUUID))
				.forEach(type->{
					ItemStack item = new ItemStack(Material.matchMaterial(type.toUpperCase()), config.getInt(world.getName() + "." + kitUUID + type + ".Amount"));
					//Check if Config Item has Enchantment and if so add it to the item
					if (config.isSet(world.getName() + "." + kitUUID + type + ".Enchant")){
						getPathSection(config, config.getString(world.getName() + "." + kitUUID + type + ".Enchant"))
						.forEach(enchant->{
							item.addEnchantment(Enchantment.getByName(enchant.toUpperCase()), config.getInt(world.getName() + "." + kitUUID  + type + ".Enchant." + enchant));
						});
					}
					//Check if Config Item has a Meta and if so add it to the item
					if (config.isSet(world.getName() + "." + kitUUID + ".Meta")){
						ItemMeta meta = item.getItemMeta();
						if (config.isSet(world.getName() + "." + kitUUID  + type + ".Meta.Title"))
							meta.setDisplayName(ChatManager.Format(config.getString(world.getName() + "." + kitUUID + type + ".Meta.Title")));
						List<String> stringList  = new LinkedList<String>();
						if (config.isSet(world.getName() + "." + kitUUID + type + ".Meta.Lore"))
							config.getStringList(world.getName() + "." + kitUUID + type + ".Meta.Lore").forEach(string->{
								stringList.add(ChatManager.Format(string));
							});
						meta.setLore(stringList);
						item.setItemMeta(meta);
					}
					kit.addItem(kit.getItems().size() + 1, item);					
				});
			}
		});
		loadMsg.put("&6| --&3 " +  config.getName() + " [&6" + world.getName() + "&3]", true);		
	}

	//SAVE
	private static void saveKingdoms(World world){
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
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}
	private static void saveUsers(World world){
		Config config = getConfig("Users");

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
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}
	private static void saveVillages(World world){
		Config config = getConfig("Villages");
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
		Config config = getConfig("Kits");
		//Save Kits
		try {
			Kit.getKits(world).forEach(kit ->{
				if (!kit.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getName() + "." + kit.getUUID() + ".Name", kit.getName());
				config.set(world.getName() + "." + kit.getUUID() + ".Owner", kit.getOwner().toString());
				config.set(world.getName() + "." + kit.getUUID() + ".Cost", kit.getCost());
				config.set(world.getName() + "." + kit.getUUID() + ".Cooldown", kit.getCooldown());
				if (kit.getItems().size() < 1)
					return;
				kit.getItems().forEach((i, item)->{
					if (item.getType().equals(Material.AIR.name())) // If item is air, return
						return;
					config.set(world.getName() + "." + kit.getUUID() + "." + item.getType().toString() + ".Amount", item.getAmount()); //Material Amount
					if (item.hasItemMeta()){
						if (item.getEnchantments().size() > 0){
							item.getEnchantments().forEach((enchant, x)->{
								config.set(world.getName() + "." + kit.getUUID()  + "." + item.getType().toString() + ".Enchant." + enchant.toString(), x); //Material Enchantment Level
							});
						}
						config.createSection(world.getName() + "." + kit.getUUID()  + "." + item.getType().toString() + ".Meta");
						if (item.getItemMeta().hasDisplayName())
							config.set(world.getName() + "." + kit.getUUID()  + "." + item.getType().toString() + ".Meta.Title", item.getItemMeta().getDisplayName()); //Material Display Name
						if (item.getItemMeta().hasLore())
							config.set(world.getName() + "." + kit.getUUID()  + "." + item.getType().toString() + ".Meta.Lore", item.getItemMeta().getLore()); //Material Lore Name
					}
				});
				config.saveConfig();
			});
			config.saveConfig();
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch(Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}

	//REMOVE
	private static void removeKingdoms(World world){
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
		config.saveConfig();
	}
	private static void removeTowns(World world){
		Config config = getConfig("Towns");
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
		Config config = getConfig("Villages");
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
		Config config = getConfig("Users");
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
		Config config = getConfig("Kits");
		if (!config.isSet(world.getName()))
			return;
		getPathSection(config, world.getName()).forEach(uniqueID->{
			//Remove Village from config if removed from game
			if (Validate.isNull(Kit.getKit(UUID.fromString(uniqueID), world))){
				config.set(world.getName(), null);
				removeMsg.put("&6| --&3 [&6Kit&3] " + uniqueID  
						+ " [&6" + world.getName() + "&3]", true);
			}
		});
		config.saveConfig();
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
	private static HashMap<String, String> strings = new HashMap<String, String>();
	private static HashMap<String, String> language = new HashMap<String, String>();
	private static HashMap<UUID, HashMap<String, Boolean>> booleans = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Integer>> integers = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Double>> doubles = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Long>> longs = new HashMap<>();
	public static String getChat(String str) {
		return language.get(str);
	}
	public static String getStr(String str) {
		return language.get(str);
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
		new Config("Data", "Kits.yml");
	}
}