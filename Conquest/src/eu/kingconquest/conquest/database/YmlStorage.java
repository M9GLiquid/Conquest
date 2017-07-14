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
import java.util.List;
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
	private String	name;
	private File	file;
	private String	defaults;

	//Instance Specific
	/**
	 * Creates new Config File, without defaults
	 * 
	 * @param path
	 *            - String
	 * @param fileName
	 *            - String
	 */
	public YmlStorage(String path, String fileName){
		this(path, fileName, null);
	}

	/**
	 * Creates new Config File, with defaults
	 * 
	 * @param path
	 *            - String
	 * @param fileName
	 *            - String
	 * @param defaultsName
	 *            - String
	 */
	public YmlStorage(String path, String fileName, String defaultsName){
		defaults = defaultsName;
		String pathway = (path == null) ? Main.getInstance().getDataFolder() + File.separator + fileName
				: Main.getInstance().getDataFolder() + File.separator + path + File.separator + fileName;

		try{
			file = new File(pathway);
			reload();
			setName(fileName);
			addConfig(this);
			UUID.fromString(fileName);
		}catch (IllegalArgumentException e){
			loadMsg.put("&6| --&3 " + fileName, true);
			return;
		}
		loadMsg.put("&6| --&3 " + fileName, false);
	}

	/**
	 * Reload configuration return void
	 */
	public boolean reload(){
		if (!file.exists()){
			try{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}catch (IOException exception){
				Main.getInstance().getLogger().severe("Error while creating file " + file.getName());
				return false;
			}
		}

		try{
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
		}catch (IOException e){
			Main.getInstance().getLogger().severe("Error while loading file " + file.getName());
			e.printStackTrace();
			return false;
		}catch (InvalidConfigurationException e){
			Main.getInstance().getLogger().severe("Error while loading file " + file.getName());
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * Save configuration
	 * 
	 * @return void
	 */
	public void saveConfig(){
		try{
			options().indent(2);
			save(file);
		}catch (IOException exception){
			exception.printStackTrace();
			Main.getInstance().getLogger().severe("Error while saving file " + file.getName());
		}
	}

	/**
	 * Set Location to config
	 * 
	 * @param config
	 *            - ConfigManager instance
	 * @param pathway
	 *            - String
	 * @param key
	 *            - Location
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
	 * 
	 * @param pathway
	 *            - String
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
	private static String					headerMsg	= "&6| - &aSuccess:";
	private static String					errorMsg	= "&6| - &cFailed:";
	private static HashMap<String, Boolean>	loadMsg		= new HashMap<>();
	private static HashMap<String, Boolean>	saveMsg		= new HashMap<>();
	private static HashMap<String, Boolean>	removeMsg	= new HashMap<>();
	private static int						saveTaskID	= 0;

	public static void save(){
		if (saveTaskID > 0){
			saveTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new Runnable(){
				@Override
				public void run(){
					//Save data
					worlds.forEach(uniqueID ->{
						World world = Bukkit.getWorld(uniqueID);
						saveKingdoms(world);
						saveTowns(world);
						saveVillages(world);
						saveUsers(world);
						saveRewards(world);
					});
					saveMsg.clear();
				}
			}, 10, Long.valueOf(getStr("AutoSaveInterval"))).getTaskId();
		}else{
			headerMsg = "&6| - &aSaved:";
			worlds.forEach(uniqueID ->{
				World world = Bukkit.getWorld(uniqueID);
				saveKingdoms(world);
				saveTowns(world);
				saveVillages(world);
				saveUsers(world);
				saveRewards(world);
				output();
			});
		}
	}

	public static void load(){
		registerFiles();
		loadLanguage();
		loadDefault();
		getWorlds().forEach(aWorld ->{
			World world = Bukkit.getWorld(aWorld);
			headerMsg = "&6| - &aLoaded:";
			loadKingdoms(world);
			loadUsers(world);
			loadTowns(world);
			loadVillages(world);
			loadRewards(world);
		});
	}

	public static void remove(){
		headerMsg = "&6| - &cRemoved:";
		worlds.forEach(uniqueID ->{
			World world = Bukkit.getWorld(uniqueID);
			removeRewards(world);
			removeKingdoms(world);
			removeUsers(world);
			removeTowns(world);
			removeVillages(world);
		});
		output();
	}

	public static Set<String> getPathSection(YmlStorage c, String path){
		Validate.notNull(c.getConfigurationSection(path).getKeys(false), "&cPath Section Failure: \n&3" + path);
		return c.getConfigurationSection(path).getKeys(false);
	}

	public static void output(){
		if (loadMsg.containsValue(true)){
			new Message(MessageType.CONSOLE, headerMsg);
			loadMsg.forEach((s, b) ->{
				if (b){
					new Message(MessageType.CONSOLE, s);
				}
			});
		}
		if (loadMsg.containsValue(false)){
			new Message(MessageType.CONSOLE, errorMsg);
			loadMsg.forEach((s, b) ->{
				if (!b)
					new Message(MessageType.CONSOLE, s);
			});
		}
		loadMsg.clear();

		if (saveMsg.containsValue(true)){
			new Message(MessageType.CONSOLE, headerMsg);
			saveMsg.forEach((s, b) ->{
				if (b){
					new Message(MessageType.CONSOLE, s);
				}
			});
		}
		if (saveMsg.containsValue(false)){
			new Message(MessageType.CONSOLE, errorMsg);
			saveMsg.forEach((s, b) ->{
				if (!b)
					new Message(MessageType.CONSOLE, s);
			});
		}
		saveMsg.clear();

		if (removeMsg.containsValue(true)){
			new Message(MessageType.CONSOLE, headerMsg);
			removeMsg.forEach((s, b) ->{
				if (b){
					new Message(MessageType.CONSOLE, s);
				}
			});
		}
		if (removeMsg.containsValue(false)){
			new Message(MessageType.CONSOLE, errorMsg);
			removeMsg.forEach((s, b) ->{
				if (!b)
					new Message(MessageType.CONSOLE, s);
			});
		}
		removeMsg.clear();
	}

	//LOAD
	//Config Loads
	public static boolean loadDefault(){
		YmlStorage config = getConfig("Config");

		strings.put("Port",
				(config.getString("Database.MySql.Port") != null ? config.getString("Database.MySql.Port") : "3306"));
		strings.put("Host", (config.getString("Database.MySql.Host") != null ? config.getString("Database.MySql.Host")
				: "localhost"));
		strings.put("Username", (config.getString("Database.MySql.Username") != null
				? config.getString("Database.MySql.Username") : "root"));
		strings.put("Password", (config.getString("Database.MySql.Password") != null
				? config.getString("Database.MySql.Password") : ""));
		strings.put("Database", (config.getString("Database.MySql.Database") != null
				? config.getString("Database.MySql.Database") : "Conquest"));
		strings.put("AutoSaveInterval", (config.getString("Database.AutoSaveInterval") != null
				? config.getString("Database.AutoSaveInterval") : "5"));
		if (!config.isSet("ActiveWorlds"))
			return false;
		getPathSection(config, "ActiveWorlds").forEach(aWorld ->{
			if (getWorlds().size() > 0 && !isActiveWorld(aWorld))
				return;
			Bukkit.getWorlds().stream().filter(world ->world.getName().equals(aWorld)).forEach(world ->{

				HashMap<String, Double> dmap = new HashMap<>();
				HashMap<String, Long> lmap = new HashMap<>();
				HashMap<String, Boolean> bmap = new HashMap<>();
				dmap.put("CapCash", config.getDouble("ActiveWorlds." + world.getName() + ".Income.CapCash"));
				dmap.put("CaptureDistance",
						config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureDistance"));
				dmap.put("CaptureMaxY", config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureMaxY"));
				dmap.put("CaptureMinY", config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureMinY"));
				dmap.put("CaptureRate", config.getDouble("ActiveWorlds." + world.getName() + ".Combat.CaptureRate"));

				lmap.put("RespawnDelay",
						(20 * config.getLong("ActiveWorlds." + world.getName() + ".General.RespawnDelay")));
				lmap.put("TeleportDelay",
						(20 * config.getLong("ActiveWorlds." + world.getName() + ".General.TeleportDelay")));

				bmap.put("DebugDynmapMarkers",
						config.getBoolean("ActiveWorlds." + world.getName() + ".Debug.DynmapMarkers"));

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
			getPathSection(lang, "Language").forEach(path ->{
				getPathSection(lang, "Language." + path).forEach(pathSection ->{
					if (!pathSection.toLowerCase().equals("admin")){
						strings.put(pathSection, (lang.getString("Language." + path + "." + pathSection) != null
								? lang.getString("Language." + path + "." + pathSection) : ""));
					}else{
						getPathSection(lang, "Language." + path + "." + pathSection).forEach(adminSection ->{
							strings.put("Admin" + adminSection,
									(lang.getString("Language." + path + ".Admin." + adminSection) != null
											? lang.getString("Language." + path + ".Admin." + adminSection) : ""));
						});
					}
				});
			});
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	//Data Files
	private static void loadKingdoms(World world){
		YmlStorage config = getConfig("Kingdoms");

		if (!config.isSet(world.getUID().toString()))
			return;
		for (String uniqueID : getPathSection(config, world.getUID().toString())){
			if (Validate.notNull(Kingdom.getKingdom(UUID.fromString(uniqueID), world)))
				return; //Kingdom already loaded!
			Kingdom kingdom = new Kingdom(config.getString(world.getUID().toString() + "." + uniqueID + ".Name"),
					config.getString(world.getUID().toString() + "." + uniqueID + ".King"), uniqueID,
					config.getLocation(world, world.getUID().toString() + "." + uniqueID + ".Location"),
					config.getLocation(world, world.getUID().toString() + "." + uniqueID + ".Spawn"),
					config.getInt(world.getUID().toString() + "." + uniqueID + ".Color"));
			config.getStringList(world.getUID().toString() + "." + uniqueID + ".Members").forEach(uUUID ->{
				if (Validate.notNull(Bukkit.getOfflinePlayer(UUID.fromString(uUUID)))){
					PlayerWrapper wrapper = new PlayerWrapper(UUID.fromString(uUUID));
					wrapper.setKingdom(kingdom.getUUID());
					kingdom.addMember(UUID.fromString(uUUID));
				}
			});
		}
		if (Validate.isNull(Kingdom.getKingdom("Neutral", world))){
			Kingdom kingdom = new Kingdom("Neutral", null, world.getSpawnLocation().clone(), -1);
			kingdom.create(null);
		}
	}

	private static void loadTowns(World world){
		YmlStorage config = getConfig("Towns");

		if (!config.isSet(world.getUID().toString()))
			return;
		getPathSection(config, world.getUID().toString()).forEach(uniqueID ->{
			if (Validate.notNull(Town.getTown(UUID.fromString(uniqueID), world)))
				return; //Town already loaded!
			new Town(config.getString(world.getUID().toString() + "." + uniqueID + ".Name"), uniqueID,
					config.getLocation(world, world.getUID().toString() + "." + uniqueID + ".Location"),
					config.getLocation(world, world.getUID().toString() + "." + uniqueID + ".Spawn"),
					Kingdom.getKingdom(
							UUID.fromString(config.getString(world.getUID().toString() + "." + uniqueID + ".Owner")),
							world));
		});
	}

	private static void loadVillages(World world){
		YmlStorage config = getConfig("Villages");
		if (!config.isSet(world.getUID().toString()))
			return;
		getPathSection(config, world.getUID().toString()).forEach(uniqueID ->{
			Town parent = null;
			if (Validate.notNull(Village.getVillage(UUID.fromString(uniqueID), world)))
				return; //Village already loaded!
			if (config.isSet((world.getUID().toString() + "." + uniqueID + ".Parent")))
				parent = Town.getTown(
						UUID.fromString(config.getString(world.getUID().toString() + "." + uniqueID + ".Parent")),
						world);

			Village village = new Village(config.getString(world.getUID().toString() + "." + uniqueID + ".Name"),
					uniqueID, config.getLocation(world, world.getUID().toString() + "." + uniqueID + ".Location"),
					config.getLocation(world, world.getUID().toString() + "." + uniqueID + ".Spawn"),
					Kingdom.getKingdom(
							UUID.fromString(config.getString(world.getUID().toString() + "." + uniqueID + ".Owner")),
							world),
					Kingdom.getKingdom(
							UUID.fromString(config.getString(world.getUID().toString() + "." + uniqueID + ".PreOwner")),
							world),
					parent);

			//Add Child to Parent
			if (village.hasParent())
				village.getParent().addChild(village);

			if (Validate.notNull(village.getOwner()))
				if (!village.getOwner().isNeutral())
					village.setProgress(100.0d);
		});
	}

	private static void loadUsers(World world){
		try (Stream<Path> paths = Files.walk(
				Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Users"))){
			paths.forEach(filePath ->{
				if (Files.isRegularFile(filePath)){
					String userUUID = filePath.getFileName().toString().replaceAll(".yml", "");
					YmlStorage config = getConfig(userUUID);
					if (Validate.notNull(config)){
						if (!config.isSet(world.getUID().toString()))
							return;
						PlayerWrapper wrapper = new PlayerWrapper(UUID.fromString(userUUID));
						wrapper.setBoardType(config.getString(world.getUID() + "." + userUUID + ".Scoreboard"));
						wrapper.setKingdom(
								UUID.fromString(config.getString(world.getUID() + "." + userUUID + ".Kingdom")));
						getPathSection(config, world.getUID() + "." + userUUID + ".Rewards").forEach(rewardUUID ->{
							wrapper.setRewardCooldown(UUID.fromString(rewardUUID), config
									.getLong(world.getUID() + "." + userUUID + ".Rewards." + rewardUUID + ".Cooldown"));
						});
						;
					}
				}
			});
		}catch (IOException e){}
	}

	private static void loadRewards(World world){
		try (Stream<Path> paths = Files.walk(
				Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Rewards"))){
			paths.forEach(filePath ->{
				if (Files.isRegularFile(filePath)){
					String rewardUUID = filePath.getFileName().toString().replaceAll(".yml", "");
					if (Validate.notNull(Reward.getReward(UUID.fromString(rewardUUID), world)))
						return; //Reward already loaded!

					YmlStorage config = getConfig(rewardUUID);
					if (Validate.notNull(config)){
						if (!config.isSet(world.getUID().toString()))
							return;
						Reward reward = new Reward(
								config.getString(world.getUID().toString() + "." + rewardUUID + ".Name"), world,
								config.getLong(world.getUID().toString() + "." + rewardUUID + ".Cost"),
								config.getLong(world.getUID().toString() + "." + rewardUUID + ".Cooldown"),
								UUID.fromString(
										config.getString(world.getUID().toString() + "." + rewardUUID + ".Owner")),
								UUID.fromString(rewardUUID));
						//Get each type
						if (config.isSet(world.getUID().toString() + "." + rewardUUID + ".Slots")){
							getPathSection(config, world.getUID().toString() + "." + rewardUUID + ".Slots")
									.forEach(slot ->{
										reward.addItem(reward.getItems().size() + 1, config.getItemStack(
												world.getUID().toString() + "." + rewardUUID + ".Slots." + slot));
									});
						}
					}
				}
			});
		}catch (IOException e){}
	}

	//SAVE
	private static void saveKingdoms(World world){
		YmlStorage config = getConfig("Kingdoms");
		try{
			Kingdom.getKingdoms().forEach(kingdom ->{
				if (!kingdom.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getUID().toString() + "." + kingdom.getUUID().toString() + ".Name", kingdom.getName());
				if (Validate.notNull(kingdom.getKing()))
					config.set(world.getUID().toString() + "." + kingdom.getUUID().toString() + ".King",
							kingdom.getKing().getUniqueId().toString());
				config.set(world.getUID().toString() + "." + kingdom.getUUID().toString() + ".Color",
						kingdom.getIntColor());
				config.setLocation(world.getUID().toString() + "." + kingdom.getUUID().toString() + ".Location",
						kingdom.getLocation());
				config.setLocation(world.getUID().toString() + "." + kingdom.getUUID().toString() + ".Spawn",
						kingdom.getSpawn());
				List<String> members = new ArrayList<String>();
				kingdom.getMembers().forEach(member ->{
					members.add(member.toString());
				});
				config.set(world.getUID().toString() + "." + kingdom.getUUID().toString() + ".Members", members);
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch (Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}

	private static void saveTowns(World world){
		YmlStorage config = getConfig("Towns");
		try{
			Town.getTowns(world).forEach(town ->{
				if (!town.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getUID().toString() + "." + town.getUUID().toString() + ".Name", town.getName());
				config.set(world.getUID().toString() + "." + town.getUUID().toString() + ".Owner",
						town.getOwner().getUUID().toString());
				if (town.hasChildren()){
					town.getChildren().forEach(child ->{
						config.createSection(world.getUID().toString() + "." + town.getUUID().toString() + ".Children."
								+ child.getUUID().toString());
					});
				}
				config.setLocation(world.getUID().toString() + "." + town.getUUID().toString() + ".Location",
						town.getLocation());
				config.setLocation(world.getUID().toString() + "." + town.getUUID().toString() + ".Spawn",
						town.getSpawn());
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch (Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}

	private static void saveVillages(World world){
		YmlStorage config = getConfig("Villages");
		try{
			Village.getVillages(world).forEach(village ->{
				if (!village.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
					return;
				config.set(world.getUID().toString() + "." + village.getUUID().toString() + ".Name", village.getName());
				config.set(world.getUID().toString() + "." + village.getUUID().toString() + ".Owner",
						village.getOwner().getUUID().toString());
				if (Validate.notNull(village.getPreOwner()))
					config.set(world.getUID().toString() + "." + village.getUUID().toString() + ".PreOwner",
							village.getPreOwner().getUUID().toString());
				if (village.hasParent())
					config.set(world.getUID().toString() + "." + village.getUUID().toString() + ".Parent",
							village.getParent().getUUID().toString());
				config.setLocation(world.getUID().toString() + "." + village.getUUID().toString() + ".Location",
						village.getLocation());
				config.setLocation(world.getUID().toString() + "." + village.getUUID().toString() + ".Spawn",
						village.getSpawn());
				config.saveConfig();
			});
			saveMsg.put("&6| --&3 " + config.getName(), true);
		}catch (Exception e){
			e.printStackTrace();
			saveMsg.put("&6| --&3 " + config.getName(), false);
		}
	}

	private static void saveUsers(World world){ // Update so each Player has it's own file
		PlayerWrapper.Wrappers().forEach((uuid, wrapper) ->{
			YmlStorage config = new YmlStorage("Data" + File.separator + "Users", uuid + ".yml");
			try{
				config.set(world.getUID().toString() + "." + uuid + ".Scoreboard", wrapper.getBoardType().getName());
				if (wrapper.isInKingdom(world))
					config.set(world.getUID().toString() + "." + uuid + ".Kingdom",
							wrapper.getKingdom(world).getUUID().toString());
				wrapper.getRewardCooldowns().forEach((rUUID, cooldown) ->{
					config.set(world.getUID().toString() + "." + uuid + ".Rewards." + rUUID + ".Cooldown", cooldown);
					config.saveConfig();
				});
			}catch (Exception e){
				e.printStackTrace();
				saveMsg.put("&6| --&3 " + config.getName(), false);
			}
		});
	}

	private static void saveRewards(World world){
		Reward.getRewards(world).forEach(reward ->{
			if (!reward.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
				return;
			YmlStorage config = new YmlStorage("Data" + File.separator + "Rewards",
					reward.getUUID().toString() + ".yml");
			try{
				config.set(world.getUID().toString() + "." + reward.getUUID() + ".Name", reward.getName());
				config.set(world.getUID().toString() + "." + reward.getUUID() + ".Owner",
						reward.getParent().getUUID().toString());
				config.set(world.getUID().toString() + "." + reward.getUUID() + ".Cost", reward.getCost());
				config.set(world.getUID().toString() + "." + reward.getUUID() + ".Cooldown", reward.getCooldown());
				if (reward.getItems().size() < 1)
					return;
				reward.getItems().forEach((i, item) ->{
					config.set(world.getUID().toString() + "." + reward.getUUID() + ".Slots." + i, item);
				});
				config.saveConfig();
			}catch (Exception e){
				e.printStackTrace();
				saveMsg.put("&6| --&3 " + config.getName(), false);
			}
		});
	}

	//REMOVE
	private static void removeKingdoms(World world){
		YmlStorage config = getConfig("Kingdoms");
		if (!config.isSet(world.getUID().toString()))
			return;
		getPathSection(config, world.getUID().toString()).forEach(kingdomUUID ->{
			//Remove Kingdom from config if removed from game
			if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUUID), world))){
				config.set(world.getUID().toString(), null);
				removeMsg.put("&6| --&3 [&6Kingdom&3] " + kingdomUUID + " [&6" + world.getName() + "&3]", true);
			}
		});
		config.saveConfig();
	}

	private static void removeTowns(World world){
		YmlStorage config = getConfig("Towns");
		if (!config.isSet(world.getUID().toString()))
			return;
		getPathSection(config, world.getUID().toString()).forEach(townUUID ->{
			//Remove Town from config if removed from game
			if (Validate.isNull(Town.getTown(UUID.fromString(townUUID), world))){
				config.set(world.getUID().toString(), null);
				removeMsg.put("&6| --&3 [&6Town&3] " + townUUID + " [&6" + world.getName() + "&3]", true);
			}
		});
		config.saveConfig();
	}

	private static void removeVillages(World world){
		YmlStorage config = getConfig("Villages");
		if (!config.isSet(world.getUID().toString()))
			return;
		getPathSection(config, world.getUID().toString()).forEach(villageUUID ->{
			//Remove Village from config if removed from game
			if (Validate.isNull(Village.getVillage(UUID.fromString(villageUUID), world))){
				config.set(world.getUID().toString(), null);
				removeMsg.put("&6| --&3 [&6Village&3] " + villageUUID + " [&6" + world.getName() + "&3]", true);
			}
		});
		config.saveConfig();
	}

	private static void removeUsers(World world){
		try (Stream<Path> paths = Files.walk(
				Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Users"))){
			paths.forEach(filePath ->{
				if (Files.isRegularFile(filePath)){
					try{
						Files.delete(filePath);
					}catch (NoSuchFileException x){
						System.err.format("%s: no such file or directory%n", filePath);
					}catch (IOException e){
						e.printStackTrace();
					}
				}
			});
		}catch (IOException e){}
	}

	private static void removeRewards(World world){
		try (Stream<Path> paths = Files.walk(
				Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Rewards"))){
			paths.forEach(filePath ->{
				if (Files.isRegularFile(filePath)){
					try{
						Files.delete(filePath);
					}catch (NoSuchFileException e){
						System.err.format("%s: no such file or directory%n", filePath);
					}catch (IOException e){
						e.printStackTrace();
					}
				}
			});
		}catch (IOException e){}
	}

	private static ArrayList<UUID> worlds = new ArrayList<UUID>();

	public static ArrayList<UUID> getWorlds(){
		return worlds;
	}

	public static void addWorld(World world){
		worlds.add(world.getUID());
	}

	public static World getWorld(UUID uuid){
		for (UUID uniqueID : worlds){
			if (Bukkit.getWorld(uniqueID).getUID().equals(uuid))
				return Bukkit.getWorld(uniqueID);
		}
		return null;
	}

	public static boolean isActiveWorld(String name){
		for (UUID uniqueID : worlds){
			Validate.notNull(Bukkit.getWorld(uniqueID), "Not a known world UUID" + uniqueID);
			if (Bukkit.getWorld(uniqueID).getName().equals(name))
				return true;
		}
		return false;
	}

	//UUID of World
	public static HashMap<String, String>					strings		= new HashMap<String, String>();
	private static HashMap<UUID, HashMap<String, Boolean>>	booleans	= new HashMap<>();
	private static HashMap<UUID, HashMap<String, Integer>>	integers	= new HashMap<>();
	private static HashMap<UUID, HashMap<String, Double>>	doubles		= new HashMap<>();
	private static HashMap<UUID, HashMap<String, Long>>		longs		= new HashMap<>();

	public static String getStr(String str){
		return strings.get(str);
	}

	public static boolean getBoolean(String str, Location loc){
		HashMap<String, Boolean> map2 = booleans.get(loc.getWorld().getUID());
		return map2.get(str);
	}

	public static int getInteger(String str, Location loc){
		HashMap<String, Integer> map2 = integers.get(loc.getWorld().getUID());
		return map2.get(str);
	}

	public static Double getDouble(String str, Location loc){
		HashMap<String, Double> map2 = doubles.get(loc.getWorld().getUID());
		return map2.get(str);
	}

	public static Long getLong(String str, Location loc){
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
		new Message(MessageType.ERROR, "Could not find config: " + name + " file");
		new Message(MessageType.CONSOLE, "&4: Wrong name?");
		return null;
	}

	public static void addConfig(YmlStorage config){
		configs.add(config);
	}

	public static void clear(){
		configs.clear();
		Bukkit.getScheduler().cancelTask(saveTaskID);
	}

	public static void clearData(){
		doubles.clear();
		longs.clear();
		booleans.clear();
		strings.clear();
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

		try (Stream<Path> paths = Files.list(
				Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Users"))){
			paths.filter(Files::isRegularFile).forEach(filePath ->{
				new YmlStorage("Data" + File.separator + "Users", filePath.getFileName().toString());
			});
			paths.close();
		}catch (IOException e){}

		try (Stream<Path> paths = Files.list(
				Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Rewards"))){
			paths.filter(Files::isRegularFile).forEach(filePath ->{
				new YmlStorage("Data" + File.separator + "Rewards", filePath.getFileName().toString());
			});
			paths.close();
		}catch (IOException e){}
	}
}