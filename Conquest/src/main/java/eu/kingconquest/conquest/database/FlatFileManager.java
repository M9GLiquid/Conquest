package eu.kingconquest.conquest.database;

import eu.kingconquest.conquest.Conquest;
import eu.kingconquest.conquest.core.*;
import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.util.DataType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * MYSQL only active choice atm due to incompatibility (Move to SQLITE when possible)
 */
public class FlatFileManager extends YamlConfiguration {
    private static String tablePrefix;
    private static String outputStream = "&6| - &cFailed:";
    private static int saveTaskID = 0;
    private YmlStorage config;

    public FlatFileManager(DataType option) {
        tablePrefix = YmlStorage.getStr("TablePrefix");
        switch (option) {
            case LOAD:
                //load();
                outputStream = "&6| - &aSuccess:";
                break;
            case REMOVE:
                //remove();
                break;
            case SAVE:
                //save();
                outputStream = "&6| - &aSuccess:";
                break;
            case CREATE:
                break;
        }
    }

    public static void save() {
        if (saveTaskID > 0) {
            saveTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Conquest.getInstance(), () -> {
                //Save data
                YmlStorage.getWorlds().forEach(uniqueID -> {
                    World world = Bukkit.getWorld(uniqueID);
                    saveKingdoms(ActiveWorld.getActiveWorld(world));
                    saveTowns(ActiveWorld.getActiveWorld(world));
                    saveVillages(ActiveWorld.getActiveWorld(world));
                    saveUsers(ActiveWorld.getActiveWorld(world));
                    saveRewards(ActiveWorld.getActiveWorld(world));
                });
                YmlStorage.saveMsg.clear();
            }, 10, Long.valueOf(YmlStorage.getStr("AutoSaveInterval"))).getTaskId();
        } else {
            YmlStorage.headerMsg = "&6| - &aSaved:";
            YmlStorage.getWorlds().forEach(uniqueID -> {
                World world = Bukkit.getWorld(uniqueID);
                saveKingdoms(ActiveWorld.getActiveWorld(world));
                saveTowns(ActiveWorld.getActiveWorld(world));
                saveVillages(ActiveWorld.getActiveWorld(world));
                saveUsers(ActiveWorld.getActiveWorld(world));
                saveRewards(ActiveWorld.getActiveWorld(world));
                YmlStorage.output();
            });
        }
    }

    public static void load() {
        YmlStorage.getWorlds().forEach(aWorld -> {
            World world = Bukkit.getWorld(aWorld);
            YmlStorage.headerMsg = "&6| - &aLoaded:";
            loadKingdoms(ActiveWorld.getActiveWorld(world));
            loadUsers(ActiveWorld.getActiveWorld(world));
            loadTowns(ActiveWorld.getActiveWorld(world));
            loadVillages(ActiveWorld.getActiveWorld(world));
            loadRewards(ActiveWorld.getActiveWorld(world));
        });
        registerDynamicFiles();
    }

    public static void remove() {
        YmlStorage.headerMsg = "&6| - &cRemoved:";
        YmlStorage.getWorlds().forEach(uniqueID -> {
            World world = Bukkit.getWorld(uniqueID);
            removeRewards(ActiveWorld.getActiveWorld(world));
            removeKingdoms(ActiveWorld.getActiveWorld(world));
            removeUsers();
            removeTowns(ActiveWorld.getActiveWorld(world));
            removeVillages(ActiveWorld.getActiveWorld(world));
        });
        YmlStorage.output();
    }

    //Data Files
    private static void loadKingdoms(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Kingdoms");

        if (Validate.isNull(config)) {
            new Message(MessageType.DEBUG, "Creating new Kingdoms.yml file!");
            registerFileKingdoms();
            return;
        }
        if (!config.Exists(config)) {
            return;
        }
        for (String uniqueID : YmlStorage.getPathSection(config, world.getWorld().getUID().toString())) {
            if (Validate.notNull(Kingdom.getKingdom(UUID.fromString(uniqueID), world)))
                return; //Kingdom already loaded!
            Kingdom kingdom = new Kingdom(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".Name"),
                    config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".King"), uniqueID,
                    config.getLocation(world.getWorld(), world.getWorld().getUID().toString() + "." + uniqueID + ".Location"),
                    config.getLocation(world.getWorld(), world.getWorld().getUID().toString() + "." + uniqueID + ".Spawn"),
                    config.getInt(world.getWorld().getUID().toString() + "." + uniqueID + ".Color"));
            config.getStringList(world.getWorld().getUID().toString() + "." + uniqueID + ".Members").forEach(uUUID -> {
                if (Validate.notNull(Bukkit.getOfflinePlayer(UUID.fromString(uUUID)))) {
                    PlayerWrapper wrapper = new PlayerWrapper(UUID.fromString(uUUID));
                    wrapper.setKingdom(kingdom.getUUID());
                    kingdom.addMember(UUID.fromString(uUUID));
                }
            });
        }
        if (Validate.isNull(Kingdom.getKingdom("Neutral", ActiveWorld.getActiveWorld(world.getWorld())))) {
            Kingdom kingdom = new Kingdom("Neutral", null, world.getWorld().getSpawnLocation().clone(), -1);
            kingdom.create(null);
        }
        YmlStorage.loadMsg.put("&6| --&3 " + config.getName(), true);
    }

    private static void loadTowns(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Towns");

        if (Validate.isNull(config)) {
            new Message(MessageType.DEBUG, "Creating new Towns.yml file!");
            registerFileTowns();
            return;
        }
        YmlStorage.getPathSection(config, world.getWorld().getUID().toString()).forEach(uniqueID -> {
            if (Validate.notNull(Town.getTown(UUID.fromString(uniqueID), world)))
                return; //Town already loaded!
            new Town(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".Name"), uniqueID,
                    config.getLocation(world.getWorld(), world.getWorld().getUID().toString() + "." + uniqueID + ".Location"),
                    config.getLocation(world.getWorld(), world.getWorld().getUID().toString() + "." + uniqueID + ".Spawn"),
                    Kingdom.getKingdom(
                            UUID.fromString(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".Owner")),
                            world));
        });
        YmlStorage.loadMsg.put("&6| --&3 " + config.getName(), true);
    }

    private static void loadVillages(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Villages");

        if (Validate.isNull(config)) {
            new Message(MessageType.DEBUG, "Creating new Villages.yml file!");
            registerFileVillages();
            return;
        }
        YmlStorage.getPathSection(config, world.getWorld().getUID().toString()).forEach(uniqueID -> {
            Town parent = null;
            if (Validate.notNull(Village.getVillage(UUID.fromString(uniqueID), world)))
                return; //Village already loaded!
            if (config.isSet((world.getWorld().getUID().toString() + "." + uniqueID + ".Parent")))
                parent = Town.getTown(
                        UUID.fromString(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".Parent")),
                        world);

            Village village = new Village(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".Name"),
                    uniqueID, config.getLocation(world.getWorld(), world.getWorld().getUID().toString() + "." + uniqueID + ".Location"),
                    config.getLocation(world.getWorld(), world.getWorld().getUID().toString() + "." + uniqueID + ".Spawn"),
                    Kingdom.getKingdom(
                            UUID.fromString(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".Owner")),
                            world),
                    Kingdom.getKingdom(
                            UUID.fromString(config.getString(world.getWorld().getUID().toString() + "." + uniqueID + ".PreOwner")),
                            world),
                    parent);

            //Add Child to Parent
            if (village.hasParent())
                village.getParent().addChild(village);

            if (Validate.notNull(village.getOwner()))
                if (!village.getOwner().isNeutral())
                    village.setProgress(100.0d);
        });
        YmlStorage.loadMsg.put("&6| --&3 " + config.getName(), true);
    }

    private static void loadUsers(ActiveWorld world) {
        try (Stream<Path> paths = Files.walk(
                Paths.get(Conquest.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Users"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String userUUID = filePath.getFileName().toString().replaceAll(".yml", "");
                    YmlStorage config = YmlStorage.getConfig(userUUID);
                    if (Validate.notNull(config)) {
                        if (!config.isSet(world.getWorld().getUID().toString()))
                            return;
                        PlayerWrapper wrapper = new PlayerWrapper(UUID.fromString(userUUID));
                        wrapper.setBoardType(config.getString(world.getWorld().getUID() + "." + userUUID + ".Scoreboard"));
                        wrapper.setKingdom(
                                UUID.fromString(config.getString(world.getWorld().getUID() + "." + userUUID + ".Kingdom")));

                        YmlStorage.getPathSection(config, world.getWorld().getUID() + "." + userUUID + ".Rewards").forEach(rewardUUID ->
                                wrapper.setRewardCooldown(UUID.fromString(rewardUUID), config
                                        .getLong(world.getWorld().getUID() + "." + userUUID + ".Rewards." + rewardUUID + ".Cooldown")));
                    }
                }
            });
        } catch (IOException ignored) {
        }
    }

    private static void loadRewards(ActiveWorld world) {
        try (Stream<Path> paths = Files.walk(
                Paths.get(Conquest.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Rewards"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    String rewardUUID = filePath.getFileName().toString().replaceAll(".yml", "");
                    if (Validate.notNull(Reward.getReward(UUID.fromString(rewardUUID), world.getWorld())))
                        return; //Reward already loaded!

                    YmlStorage config = YmlStorage.getConfig(rewardUUID);
                    if (Validate.notNull(config)) {
                        if (!config.isSet(world.getWorld().getUID().toString()))
                            return;
                        Reward reward = new Reward(
                                config.getString(world.getWorld().getUID().toString() + "." + rewardUUID + ".Name"),
                                world,
                                config.getLong(world.getWorld().getUID().toString() + "." + rewardUUID + ".Cost"),
                                config.getLong(world.getWorld().getUID().toString() + "." + rewardUUID + ".Cooldown"),
                                UUID.fromString(
                                        config.getString(world.getWorld().getUID().toString() + "." + rewardUUID + ".Owner")),
                                UUID.fromString(rewardUUID));
                        //Get each type
                        if (config.isSet(world.getWorld().getUID().toString() + "." + rewardUUID + ".Slots")) {
                            YmlStorage.getPathSection(config, world.getWorld().getUID().toString() + "." + rewardUUID + ".Slots")
                                    .forEach(slot ->
                                            reward.addItem(reward.getItems().size() + 1, config.getItemStack(
                                                    world.getWorld().getUID().toString() + "." + rewardUUID + ".Slots." + slot)));
                        }
                    }
                }
            });
        } catch (IOException ignored) {
        }
    }

    //SAVE
    private static void saveKingdoms(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Kingdoms");
        try {
            Kingdom.getKingdoms().forEach(kingdom -> {
                if (!kingdom.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
                    return;
                config.set(world.getWorld().getUID().toString() + "." + kingdom.getUUID().toString() + ".Name", kingdom.getName());
                if (Validate.notNull(kingdom.getKing()))
                    config.set(world.getWorld().getUID().toString() + "." + kingdom.getUUID().toString() + ".King",
                            kingdom.getKing().getUniqueId().toString());
                config.set(world.getWorld().getUID().toString() + "." + kingdom.getUUID().toString() + ".Color",
                        kingdom.getIntColor());
                config.setLocation(world.getWorld().getUID().toString() + "." + kingdom.getUUID().toString() + ".Location",
                        kingdom.getLocation());
                config.setLocation(world.getWorld().getUID().toString() + "." + kingdom.getUUID().toString() + ".Spawn",
                        kingdom.getSpawn());
                List<String> members = new ArrayList<>();
                kingdom.getMembers().forEach(member ->
                        members.add(member.toString()));
                config.set(world.getWorld().getUID().toString() + "." + kingdom.getUUID().toString() + ".Members", members);
                config.saveConfig();
            });
            YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), true);
        } catch (Exception e) {
            e.printStackTrace();
            YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), false);
        }
    }

    private static void saveTowns(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Towns");
        try {
            Town.getTowns(world).forEach(town -> {
                if (!town.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
                    return;
                config.set(world.getWorld().getUID().toString() + "." + town.getUUID().toString() + ".Name", town.getName());
                config.set(world.getWorld().getUID().toString() + "." + town.getUUID().toString() + ".Owner",
                        town.getOwner().getUUID().toString());
                if (town.hasChildren()) {
                    town.getChildren().forEach(child ->
                            config.createSection(world.getWorld().getUID().toString() + "." + town.getUUID().toString() + ".Children."
                                    + child.getUUID().toString()));
                }
                config.setLocation(world.getWorld().getUID().toString() + "." + town.getUUID().toString() + ".Location",
                        town.getLocation());
                config.setLocation(world.getWorld().getUID().toString() + "." + town.getUUID().toString() + ".Spawn",
                        town.getSpawn());
                config.saveConfig();
            });
            YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), true);
        } catch (Exception e) {
            e.printStackTrace();
            YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), false);
        }
    }

    private static void saveVillages(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Villages");
        try {
            Village.getVillages(world).forEach(village -> {
                if (!village.getWorld().equals(world))// Proceed to save only if world is equal to objectives world
                    return;
                config.set(world.getWorld().getUID().toString() + "." + village.getUUID().toString() + ".Name", village.getName());
                config.set(world.getWorld().getUID().toString() + "." + village.getUUID().toString() + ".Owner",
                        village.getOwner().getUUID().toString());
                if (Validate.notNull(village.getPreOwner()))
                    config.set(world.getWorld().getUID().toString() + "." + village.getUUID().toString() + ".PreOwner",
                            village.getPreOwner().getUUID().toString());
                if (village.hasParent())
                    config.set(world.getWorld().getUID().toString() + "." + village.getUUID().toString() + ".Parent",
                            village.getParent().getUUID().toString());
                config.setLocation(world.getWorld().getUID().toString() + "." + village.getUUID().toString() + ".Location",
                        village.getLocation());
                config.setLocation(world.getWorld().getUID().toString() + "." + village.getUUID().toString() + ".Spawn",
                        village.getSpawn());
                config.saveConfig();
            });
            YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), true);
        } catch (Exception e) {
            e.printStackTrace();
            YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), false);
        }
    }

    private static void saveUsers(ActiveWorld world) { // Update so each Player has it's own file
        PlayerWrapper.Wrappers().forEach((uuid, wrapper) -> {
            YmlStorage config = new YmlStorage("Data" + File.separator + "Users", uuid + ".yml");
            try {
                config.set(world.getWorld().getUID().toString() + "." + uuid + ".Scoreboard", wrapper.getBoardType().getName());
                if (wrapper.isInKingdom(world))
                    config.set(world.getWorld().getUID().toString() + "." + uuid + ".Kingdom",
                            wrapper.getKingdom(world).getUUID().toString());
                wrapper.getRewardCooldowns().forEach((rUUID, cooldown) -> {
                    config.set(world.getWorld().getUID().toString() + "." + uuid + ".Rewards." + rUUID + ".Cooldown", cooldown);
                    config.saveConfig();
                });
            } catch (Exception e) {
                e.printStackTrace();
                YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), false);
            }
        });
    }

    private static void saveRewards(ActiveWorld world) {
        Reward.getRewards(world.getWorld()).forEach(reward -> {
            if (!reward.getWorld().equals(world.getWorld()))// Proceed to save only if world is equal to objectives world
                return;
            YmlStorage config = new YmlStorage("Data" + File.separator + "Rewards",
                    reward.getUUID().toString() + ".yml");
            try {
                config.set(world.getWorld().getUID().toString() + "." + reward.getUUID() + ".Name", reward.getName());
                config.set(world.getWorld().getUID().toString() + "." + reward.getUUID() + ".Owner",
                        reward.getParent().getUUID().toString());
                config.set(world.getWorld().getUID().toString() + "." + reward.getUUID() + ".Cost", reward.getCost());
                config.set(world.getWorld().getUID().toString() + "." + reward.getUUID() + ".Cooldown", reward.getCooldown());
                if (reward.getItems().size() < 1)
                    return;
                reward.getItems().forEach((i, item) ->
                        config.set(world.getWorld().getUID().toString() + "." + reward.getUUID() + ".Slots." + i, item));
                config.saveConfig();
            } catch (Exception e) {
                e.printStackTrace();
                YmlStorage.saveMsg.put("&6| --&3 " + config.getName(), false);
            }
        });
    }

    //REMOVE
    private static void removeKingdoms(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Kingdoms");
        if (!config.isSet(world.getWorld().getUID().toString()))
            return;
        YmlStorage.getPathSection(config, world.getWorld().getUID().toString()).forEach(kingdomUUID -> {
            //Remove Kingdom from config if removed from game
            if (Validate.isNull(Kingdom.getKingdom(UUID.fromString(kingdomUUID), world))) {
                config.set(world.getWorld().getUID().toString(), null);
                YmlStorage.removeMsg.put("&6| --&3 [&6Kingdom&3] " + kingdomUUID + " [&6" + world.getWorld().getName() + "&3]", true);
            }
        });
        config.saveConfig();
    }

    private static void removeTowns(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Towns");
        if (!config.isSet(world.getWorld().getUID().toString()))
            return;
        YmlStorage.getPathSection(config, world.getWorld().getUID().toString()).forEach(townUUID -> {
            //Remove Town from config if removed from game
            if (Validate.isNull(Town.getTown(UUID.fromString(townUUID), world))) {
                config.set(world.getWorld().getUID().toString(), null);
                YmlStorage.removeMsg.put("&6| --&3 [&6Town&3] " + townUUID + " [&6" + world.getWorld().getName() + "&3]", true);
            }
        });
        config.saveConfig();
    }

    private static void removeVillages(ActiveWorld world) {
        YmlStorage config = YmlStorage.getConfig("Villages");
        if (!config.isSet(world.getWorld().getUID().toString()))
            return;
        YmlStorage.getPathSection(config, world.getWorld().getUID().toString()).forEach(villageUUID -> {
            //Remove Village from config if removed from game
            if (Validate.isNull(Village.getVillage(UUID.fromString(villageUUID), world))) {
                config.set(world.getWorld().getUID().toString(), null);
                YmlStorage.removeMsg.put("&6| --&3 [&6Village&3] " + villageUUID + " [&6" + world.getWorld().getName() + "&3]", true);
            }
        });
        config.saveConfig();
    }

    private static void removeUsers() {
        try (Stream<Path> paths = Files.walk(
                Paths.get(Conquest.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Users"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        Files.delete(filePath);
                    } catch (NoSuchFileException x) {
                        System.err.format("%s: no such file or directory%n", filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException ignored) {
        }
    }

    private static void removeRewards(ActiveWorld world) {
        try (Stream<Path> paths = Files.walk(
                Paths.get(Conquest.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Rewards"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        Files.delete(filePath);
                    } catch (NoSuchFileException e) {
                        System.err.format("%s: no such file or directory%n", filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException ignored) {
        }
    }


    public static void registerFileKingdoms() {
        new YmlStorage("Data", "Kingdoms.yml");
    }

    public static void registerFileTowns() {
        new YmlStorage("Data", "Towns.yml");
    }

    public static void registerFileVillages() {
        new YmlStorage("Data", "Villages.yml");
    }

    public static void registerDynamicFiles() {

        try (Stream<Path> paths = Files.list(
                Paths.get(Conquest.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Users"))) {
            paths.filter(Files::isRegularFile).forEach(filePath ->
                    new YmlStorage("Data" + File.separator + "Users", filePath.getFileName().toString()));
        } catch (IOException ignored) {
        }

        try (Stream<Path> paths = Files.list(
                Paths.get(Conquest.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "Rewards"))) {
            paths.filter(Files::isRegularFile).forEach(filePath ->
                    new YmlStorage("Data" + File.separator + "Rewards", filePath.getFileName().toString()));
        } catch (IOException ignored) {
        }
    }


    public static void clear() {
        Bukkit.getScheduler().cancelTask(saveTaskID);
    }

    public static void output() {
        new Message(MessageType.CONSOLE, outputStream);
    }
}
