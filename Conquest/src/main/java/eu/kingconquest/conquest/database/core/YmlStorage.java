package eu.kingconquest.conquest.database.core;

import eu.kingconquest.conquest.Conquest;
import eu.kingconquest.conquest.core.ActiveWorld;
import eu.kingconquest.conquest.util.DatabaseType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class YmlStorage extends YamlConfiguration {
    //Config Specific
    //Statics
    public static String headerMsg = "&6| - &aSuccess:";
    public static HashMap<String, Boolean> loadMsg = new HashMap<>();
    public static HashMap<String, Boolean> saveMsg = new HashMap<>();
    public static HashMap<String, Boolean> removeMsg = new HashMap<>();

    //Instance Specific
    public static HashMap<String, String> strings = new HashMap<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Stockholm"));
    private static ArrayList<UUID> worlds = new ArrayList<>();
    private static HashMap<UUID, HashMap<String, Long>> longs = new HashMap<>();
    private static HashMap<UUID, HashMap<String, Boolean>> booleans = new HashMap<>();
    private static HashMap<UUID, HashMap<String, Integer>> integers = new HashMap<>();
    private static HashMap<UUID, HashMap<String, Double>> doubles = new HashMap<>();
    private static ArrayList<YmlStorage> configs = new ArrayList<>();
    private String name;
    private File file;
    private String defaults;

    /**
     * Creates new Config File, without defaults
     *
     * @param path     - String
     * @param fileName - String
     */
    public YmlStorage(String path, String fileName) {
        this(path, fileName, null);
    }

    /**
     * Creates new Config File, with defaults
     *
     * @param path         - String
     * @param fileName     - String
     * @param defaultsName - String
     */
    public YmlStorage(String path, String fileName, String defaultsName) {
        defaults = defaultsName;
        String pathway = (path == null) ? Conquest.getInstance().getDataFolder() + File.separator + fileName
                : Conquest.getInstance().getDataFolder() + File.separator + path + File.separator + fileName;

        try {
            file = new File(pathway);
            reload();
            setName(fileName);
            addConfig(this);
            if (getBoolean("Debug"))
                if (fileName.replace(".yml", "").matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
                    loadMsg.put("&6| --&3 " + fileName, true);


        } catch (IllegalArgumentException e) {
            if (!fileName.replace(".yml", "").matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"))
                loadMsg.put("&6| --&3 " + fileName, false);

        }
    }

    public static Set<String> getPathSection(YmlStorage c, String path) {
        Validate.notNull(c.getConfigurationSection(path).getKeys(false), "&cPath Section Failure: &3" + path);
        return c.getConfigurationSection(path).getKeys(false);
    }

    public static void output() {
        if (loadMsg.containsValue(true)) {
            new Message(MessageType.CONSOLE, headerMsg);
            loadMsg.forEach((s, b) -> {
                if (b) {
                    new Message(MessageType.CONSOLE, s);
                }
            });
        }
        String errorMsg = "&6| - &cFailed:";
        if (loadMsg.containsValue(false)) {
            new Message(MessageType.CONSOLE, errorMsg);
            loadMsg.forEach((s, b) -> {
                if (!b)
                    new Message(MessageType.CONSOLE, s);
            });
        }
        loadMsg.clear();

        if (saveMsg.containsValue(true)) {
            new Message(MessageType.CONSOLE, headerMsg);
            saveMsg.forEach((s, b) -> {
                if (b) {
                    new Message(MessageType.CONSOLE, s);
                }
            });
        }
        if (saveMsg.containsValue(false)) {
            new Message(MessageType.CONSOLE, errorMsg);
            saveMsg.forEach((s, b) -> {
                if (!b)
                    new Message(MessageType.CONSOLE, s);
            });
        }
        saveMsg.clear();

        if (removeMsg.containsValue(true)) {
            new Message(MessageType.CONSOLE, headerMsg);
            removeMsg.forEach((s, b) -> {
                if (b) {
                    new Message(MessageType.CONSOLE, s);
                }
            });
        }
        if (removeMsg.containsValue(false)) {
            new Message(MessageType.CONSOLE, errorMsg);
            removeMsg.forEach((s, b) -> {
                if (!b)
                    new Message(MessageType.CONSOLE, s);
            });
        }
        removeMsg.clear();
    }

    //LOAD
    //Config Loads
    public static boolean loadDefault() {
        YmlStorage config = getConfig("Config");
        loadMsg.put("&6| --&3 " + config.getName(), true);

        loadDatabaseConfigurations(config);

        strings.put("AutoSaveInterval", (config.getString("Database.AutoSaveInterval") != null
                ? config.getString("Database.AutoSaveInterval") : "5"));
        if (!config.isSet("ActiveWorlds")) {
            new Message(MessageType.ERROR, "No Active worlds Set, Please setup an active world");
            return false;
        }

        getPathSection(config, "ActiveWorlds").forEach(aWorld ->
                Bukkit.getWorlds().stream().filter(world ->
                        world.getName().equals(aWorld)).forEach(world -> {
                    addWorld(world);


                    Instant date = Instant.now();
                    Timestamp dateNow = Timestamp.valueOf(formatter.format(date));
                    Timestamp dateFuture = Timestamp.valueOf(formatter.format(date.plusSeconds(3600)));
                    new ActiveWorld(world, dateNow, dateFuture, true);


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

                    bmap.put("Debug",
                            config.getBoolean("ActiveWorlds." + world.getName() + ".General.Debug"));

                    doubles.put(world.getUID(), dmap);
                    longs.put(world.getUID(), lmap);
                    booleans.put(world.getUID(), bmap);
                }));
        loadMsg.put("&6| --&3 " + config.getName(), true);
        return true;
    }

    public static boolean loadLanguage() {
        YmlStorage config = getConfig("Language");

        try {
            getPathSection(config, "Language").forEach(path ->
                    getPathSection(config, "Language." + path).forEach(pathSection -> {
                        if (!pathSection.toLowerCase().equals("admin")) {
                            strings.put(pathSection, (config.getString("Language." + path + "." + pathSection) != null
                                    ? config.getString("Language." + path + "." + pathSection) : ""));
                        } else {
                            getPathSection(config, "Language." + path + "." + pathSection).forEach(adminSection ->
                                    strings.put("Admin" + adminSection,
                                            (config.getString("Language." + path + ".Admin." + adminSection) != null
                                                    ? config.getString("Language." + path + ".Admin." + adminSection) : "")));
                        }
                    }));
            loadMsg.put("&6| --&3 " + config.getName(), true);
            return true;
        } catch (Exception e) {
            loadMsg.put("&6| --&3 " + config.getName(), false);
            return false;
        }
    }

    public static void loadDatabaseConfigurations(YmlStorage config) {
        Database.setType(config.getString("Database.Backend") != null
                ? DatabaseType.valueOf(config.getString("Database.Backend").toUpperCase())
                : DatabaseType.FLATFILE);

        if (Database.getType().equals(DatabaseType.MYSQL)) {
            strings.put("Port", (config.getString("Database.MySql.Port") != null
                    ? config.getString("Database.MySql.Port") : "3306"));
            strings.put("Host", (config.getString("Database.MySql.Host") != null
                    ? config.getString("Database.MySql.Host") : "localhost"));
            strings.put("Database", (config.getString("Database.MySql.Database") != null
                    ? config.getString("Database.MySql.Database") : ""));
            strings.put("Username", (config.getString("Database.MySql.Username") != null
                    ? config.getString("Database.MySql.Username") : "root"));
            strings.put("Password", (config.getString("Database.MySql.Password") != null
                    ? config.getString("Database.MySql.Password") : "root"));
        }
        strings.put("TablePrefix", (config.getString("Database.TablePrefix") != null
                ? config.getString("Database.TablePrefix") : "conquest_"));
    }

    public static boolean hasConfigs() {
        return configs.size() != 0;
    }

    public static ArrayList<UUID> getWorlds() {
        return worlds;
    }

    public static void addWorld(World world) {
        worlds.add(world.getUID());
    }

    public static World getWorld(UUID uuid) {
        for (UUID uniqueID : worlds) {
            if (Bukkit.getWorld(uniqueID).getUID().equals(uuid))
                return Bukkit.getWorld(uniqueID);
        }
        return null;
    }

    public static boolean isActiveWorld(String name) {
        for (UUID uniqueID : worlds) {
            Validate.notNull(Bukkit.getWorld(uniqueID), "Not a known world UUID" + uniqueID);
            if (Bukkit.getWorld(uniqueID).getName().equals(name))
                return true;
        }
        return false;
    }

    public static YmlStorage getConfig(String name) {
        for (YmlStorage config : getConfigs()) {
            if (config.getName().replace(".yml", "").equals(name)) {
                config.reload();
                return config;
            }
        }
        return null;
    }

    public static String getStr(String str) {
        return strings.get(str);
    }

    public static boolean getBoolean(String str, Location loc) {
        HashMap<String, Boolean> map2 = booleans.get(loc.getWorld().getUID());
        return map2.get(str);
    }

    public static int getInteger(String str, Location loc) {
        HashMap<String, Integer> map2 = integers.get(loc.getWorld().getUID());
        return map2.get(str);
    }

    public static Double getDouble(String str, Location loc) {
        HashMap<String, Double> map2 = doubles.get(loc.getWorld().getUID());
        return map2.get(str);
    }

    public static Long getLong(String str, Location loc) {
        HashMap<String, Long> map2 = longs.get(loc.getWorld().getUID());
        return map2.get(str);
    }

    public static ArrayList<YmlStorage> getConfigs() {
        return configs;
    }

    public static void load() {
        registerDefaultFiles();
        YmlStorage.loadLanguage();
        YmlStorage.loadDefault();
    }

    public static void registerDefaultFiles() {
        new YmlStorage(null, "Config.yml", "Config.yml");
        new YmlStorage(null, "Language.yml", "Language.yml");
    }

    public static void addConfig(YmlStorage config) {
        configs.add(config);
    }

    public static void clearData() {
        configs.clear();
        doubles.clear();
        longs.clear();
        booleans.clear();
        strings.clear();
    }

    /**
     * Set Location to config
     *
     * @param pathway - String
     * @param key     - Location
     * @return void
     */
    public void setLocation(String pathway, Location key) {
        if (Validate.notNull(pathway) && Validate.notNull(key.getWorld())) {
            this.set(pathway + ".X", key.getX());
            this.set(pathway + ".Y", key.getY());
            this.set(pathway + ".Z", key.getZ());
        }
    }

    /**
     * Save configuration
     *
     * @return void
     */
    public void saveConfig() {
        try {
            options().indent(2);
            save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
            Conquest.getInstance().getLogger().severe("Error while saving file " + file.getName());
        }
    }

    /**
     * Reload configuration return void
     */
    public void reload() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException exception) {
                new Message(MessageType.ERROR, "Error while creating file: " + file.getName());
                return;
            }
        } else
            loadMsg.put("&6| --&3 " + file.getName(), true);

        try {
            load(file);
            if (defaults != null) {
                InputStreamReader reader = new InputStreamReader(Conquest.getInstance().getResource(defaults));
                FileConfiguration defaultsConfig = YamlConfiguration.loadConfiguration(reader);

                setDefaults(defaultsConfig);
                options().copyDefaults(true);

                reader.close();
                saveConfig();
            }
        } catch (IOException | InvalidConfigurationException e) {
            new Message(MessageType.ERROR, "Error while loading file: " + file.getName());
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean Exists(YmlStorage config) {
        if (Validate.notNull(config))
            return config.file.exists();
        return false;
    }

    /**
     * Get location from config
     *
     * @param pathway - String
     * @return Location
     */
    public Location getLocation(World world, String pathway) {
        double X = this.getDouble(pathway + ".X");
        double Y = this.getDouble(pathway + ".Y");
        double Z = this.getDouble(pathway + ".Z");
        return new Location(world, X, Y, Z);
    }
}