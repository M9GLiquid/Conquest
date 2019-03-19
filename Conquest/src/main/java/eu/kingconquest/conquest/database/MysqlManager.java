package eu.kingconquest.conquest.database;

import eu.kingconquest.conquest.core.*;
import eu.kingconquest.conquest.database.core.Database;
import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.util.DataType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MysqlManager {
    private static Database database;
    private static String tablePrefix;

    public MysqlManager(DataType option) {
        database = Database.getDatabase();
        tablePrefix = YmlStorage.getStr("TablePrefix");
        switch (option) {
            case LOAD:
                worldLoad(); // Need Testing
                kingdomLoad(); // Working
                userLoad();
                townLoad(); // Need Testing
                villageLoad(); // Need Testing
                Database.setOutputStream("&6| - &aSuccess:"); //TODO: Move into last executed method for Failed/Success state (Try/Catch)
                break;
            case REMOVE:
                kingdomRemove(); // Need Testing
                userRemove();
                worldRemove(); // Need Testing
                townRemove(); // Need Testing
                villageRemove(); // Need Testing
                break;
            case SAVE:
                worldSave(); // Need Testing
                kingdomSave(); // Working
                usersSave();
                townSave(); // Need Testing
                villageSave(); // Need Testing
                Database.setOutputStream("&6| - &aSuccess:"); //TODO: Move into last executed method for Failed/Success state (Try/Catch)
                break;
            case CREATE:
                kingdomTableCreate(); // Working
                userTableCreate(); // Working
                worldTableCreate(); // Working
                townTableCreate(); // Working
                villageTableCreate(); // Working
                break;
        }
    }


    /**
     * Kingdom Queries
     */
    private void kingdomSave() {
        Kingdom.getKingdoms().forEach(kingdom -> {
            if (!kingdom.getUpdate())
                return;
            try {

                ResultSet rs = database.querySQL(
                        "SELECT * " +
                                "FROM " + tablePrefix + "kingdoms " +
                                "WHERE EXISTS " +
                                "(SELECT * FROM " + tablePrefix + "kingdoms WHERE `kingdom_uuid`='" + kingdom.getUUID().toString() + "')");
                if (!rs.next()) {
                    database.updateSQL(
                            "INSERT INTO " + tablePrefix + "kingdoms " +
                                    "VALUES('" +
                                    kingdom.getUUID().toString() + "','" +
                                    kingdom.getLocation().getWorld().getUID().toString() + "','" +
                                    kingdom.getName() + "'," +
                                    kingdom.getLocation().getX() + "," +
                                    kingdom.getLocation().getY() + "," +
                                    kingdom.getLocation().getZ() + "," +
                                    kingdom.getSpawn().getX() + "," +
                                    kingdom.getSpawn().getY() + "," +
                                    kingdom.getSpawn().getZ() + "," +
                                    kingdom.getSpawn().getYaw() + "," +
                                    kingdom.getSpawn().getPitch() + "," +
                                    kingdom.getIntColor() + ");");

                } else {
                    PreparedStatement ps = database.getConnection().prepareStatement(
                            "UPDATE " + tablePrefix + "kingdoms " +
                                    "SET " +
                                    "`world_uuid` = ?," +
                                    "`name` = ?," +
                                    "`location.X` = ?," +
                                    "`location.Y` = ?," +
                                    "`location.Z` = ?," +
                                    "`spawn.X` = ?," +
                                    "`spawn.Y` = ?," +
                                    "`spawn.Z` = ?," +
                                    "`spawn.Yaw` = ?," +
                                    "`spawn.Pitch` = ?," +
                                    "`color` = ? " +
                                    "WHERE " +
                                    "kingdom_uuid = ?");

                    // set the preparedstatement parameters
                    ps.setString(1, kingdom.getLocation().getWorld().getUID().toString());
                    ps.setString(2, kingdom.getName());
                    ps.setDouble(3, kingdom.getLocation().getX());
                    ps.setDouble(4, kingdom.getLocation().getY());
                    ps.setDouble(5, kingdom.getLocation().getZ());
                    ps.setDouble(6, kingdom.getSpawn().getX());
                    ps.setDouble(7, kingdom.getSpawn().getY());
                    ps.setDouble(8, kingdom.getSpawn().getZ());
                    ps.setDouble(9, kingdom.getSpawn().getYaw());
                    ps.setDouble(10, kingdom.getSpawn().getPitch());
                    ps.setInt(11, kingdom.getIntColor());
                    ps.setString(12, kingdom.getUUID().toString());

                    ps.executeUpdate();
                    ps.close();

                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            kingdom.update(false);
        });
    }

    private void kingdomLoad() {
        try {
            ResultSet rs = database.querySQL("SELECT * FROM " + tablePrefix + "kingdoms");
            while (rs.next()) {
                Kingdom kingdom = new Kingdom(
                        rs.getString("name"),
                        null,
                        rs.getString("kingdom_uuid"),
                        new Location(
                                Bukkit.getWorld(UUID.fromString(rs.getString("world_uuid"))),
                                rs.getFloat("location.x"),
                                rs.getFloat("location.y"),
                                rs.getFloat("location.z"),
                                0.00f,
                                0.00f),
                        new Location(
                                Bukkit.getWorld(rs.getString("world_uuid")),
                                rs.getFloat("spawn.x"),
                                rs.getFloat("spawn.y"),
                                rs.getFloat("spawn.z"),
                                rs.getFloat("spawn.yaw"),
                                rs.getFloat("spawn.pitch")),
                        rs.getInt("color"));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void kingdomRemove() {
        Kingdom.getRemovedKingdoms().forEach(kingdom -> {
            try {
                database.updateSQL("DELETE FROM " + tablePrefix + "kingdoms WHERE `kingdom_uuid`='" + kingdom.getUUID().toString() + "')");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        Kingdom.getRemovedKingdoms().clear();
    }

    private void kingdomTableCreate() {
        try {
            database.updateSQL("CREATE TABLE IF NOT EXISTS " + tablePrefix + "kingdoms (" +
                    "    kingdom_uuid VARCHAR(45) NOT NULL," +
                    "    world_uuid VARCHAR(45) NOT NULL," +
                    "    name VARCHAR(25) NOT NULL," +
                    "    `location.X` FLOAT(6) NOT NULL," +
                    "    `location.Y` FLOAT(6) NOT NULL," +
                    "    `location.Z` FLOAT(6) NOT NULL," +
                    "    `spawn.X` FLOAT(6) NOT NULL," +
                    "    `spawn.Y` FLOAT(6) NOT NULL," +
                    "    `spawn.Z` FLOAT(6) NOT NULL," +
                    "    `spawn.Yaw` FLOAT(6) NOT NULL," +
                    "    `spawn.Pitch` FLOAT(6) NOT NULL," +
                    "    color INT(2) NOT NULL," +
                    "    PRIMARY KEY (kingdom_uuid)" +
                    ");");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * World Queries
     */
    private void worldSave() {
        ActiveWorld.getWorlds().forEach(world -> {

            if (!world.getUpdate())
                return;

            try {
                ResultSet rs = database.querySQL(
                        "SELECT * " +
                                "FROM " + tablePrefix + "worlds " +
                                "WHERE EXISTS " +
                                "(SELECT * FROM " + tablePrefix + "worlds WHERE `world_uuid`='" + world.getWorld().getUID().toString() + "')");
                if (!rs.next()) {
                    database.updateSQL(
                            "INSERT INTO " + tablePrefix + "worlds " +
                                    "VALUES('" +
                                    world.getWorld().getUID().toString() + "','" +
                                    world.getEnableDate() + "','" +
                                    world.getDisableDate() + "'," +
                                    world.getEnabled().toString() + ");");

                } else {
                    PreparedStatement ps = database.getConnection().prepareStatement(
                            "UPDATE " + tablePrefix + "worlds " +
                                    "SET " +
                                    "`enable_date` = ?," +
                                    "`disable_date` = ?," +
                                    "`enabled` = ?" +
                                    "WHERE world_uuid = ?");

                    // set the preparedstatement parameters
                    ps.setTimestamp(1, world.getEnableDate());
                    ps.setTimestamp(2, world.getDisableDate());
                    ps.setString(3, "false");
                    ps.setString(4, world.getWorld().getUID().toString());

                    ps.executeUpdate();
                    ps.close();

                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            world.update(false);
        });
    }

    private void worldLoad() {
        try {
            ResultSet rs = database.querySQL("SELECT * FROM " + tablePrefix + "worlds");
            while (rs.next()) {
                new ActiveWorld(
                        Bukkit.getWorld(UUID.fromString(rs.getString("world_uuid"))),
                        rs.getTimestamp("enable_date"),
                        rs.getTimestamp("disable_date"),
                        rs.getBoolean("enabled")
                );

            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void worldRemove() {
    }

    private void worldTableCreate() {
        try {
            database.updateSQL("CREATE TABLE IF NOT EXISTS " + tablePrefix + "worlds (" +
                    "    world_uuid VARCHAR(45) NOT NULL," +
                    "    enable_date TIMESTAMP NOT NULL," +
                    "    disable_date TIMESTAMP NOT NULL," +
                    "    enabled Varchar(5) NOT NULL," +
                    "    PRIMARY KEY (world_uuid));");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Town Queries
     */
    private void townSave() {
        Town.getTowns().forEach(town -> {
            if (!town.getUpdate())
                return;
            try {

                ResultSet rs = database.querySQL(
                        "SELECT * " +
                                "FROM " + tablePrefix + "towns " +
                                "WHERE EXISTS " +
                                "(SELECT * FROM " + tablePrefix + "towns WHERE `town_uuid`='" + town.getUUID().toString() + "')");
                if (!rs.next()) {
                    database.updateSQL(
                            "INSERT INTO " + tablePrefix + "towns " +
                                    "VALUES('" +
                                    town.getUUID().toString() + "','" +
                                    town.getLocation().getWorld().getUID().toString() + "','" +
                                    town.getName() + "'," +
                                    town.getLocation().getX() + "," +
                                    town.getLocation().getY() + "," +
                                    town.getLocation().getZ() + "," +
                                    town.getSpawn().getX() + "," +
                                    town.getSpawn().getY() + "," +
                                    town.getSpawn().getZ() + "," +
                                    town.getSpawn().getYaw() + "," +
                                    town.getSpawn().getPitch() + ",'" +
                                    town.getOwner().getUUID().toString() + "');");

                } else {
                    PreparedStatement ps = database.getConnection().prepareStatement(
                            "UPDATE " + tablePrefix + "towns " +
                                    "SET " +
                                    "`world_uuid` = ?," +
                                    "`name` = ?," +
                                    "`location.X` = ?," +
                                    "`location.Y` = ?," +
                                    "`location.Z` = ?," +
                                    "`spawn.X` = ?," +
                                    "`spawn.Y` = ?," +
                                    "`spawn.Z` = ?," +
                                    "`spawn.Yaw` = ?," +
                                    "`spawn.Pitch` = ?," +
                                    "`color` = ? " +
                                    "WHERE `town_uuid` = ?");

                    // set the preparedstatement parameters
                    ps.setString(1, town.getLocation().getWorld().getUID().toString());
                    ps.setString(2, town.getName());
                    ps.setDouble(3, town.getLocation().getX());
                    ps.setDouble(4, town.getLocation().getY());
                    ps.setDouble(5, town.getLocation().getZ());
                    ps.setDouble(6, town.getSpawn().getX());
                    ps.setDouble(7, town.getSpawn().getY());
                    ps.setDouble(8, town.getSpawn().getZ());
                    ps.setDouble(9, town.getSpawn().getYaw());
                    ps.setDouble(10, town.getSpawn().getPitch());
                    ps.setString(11, town.getOwner().getUUID().toString());
                    ps.setString(12, town.getUUID().toString());

                    ps.executeUpdate();
                    ps.close();

                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            town.update(false);
        });
    }

    private void townLoad() {
        try {
            ResultSet rs = database.querySQL("SELECT * FROM " + tablePrefix + "towns");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void townRemove() {
    }

    private void townTableCreate() {
        try {
            database.updateSQL("CREATE TABLE IF NOT EXISTS " + tablePrefix + "towns (" +
                    "    town_uuid VARCHAR(45) NOT NULL," +
                    "    world_uuid VARCHAR(45) NOT NULL," +
                    "    name VARCHAR(25) NOT NULL," +
                    "    `location.X` FLOAT(6) NOT NULL," +
                    "    `location.Y` FLOAT(6) NOT NULL," +
                    "    `location.Z` FLOAT(6) NOT NULL," +
                    "    `spawn.X` FLOAT(6) NOT NULL," +
                    "    `spawn.Y` FLOAT(6) NOT NULL," +
                    "    `spawn.Z` FLOAT(6) NOT NULL," +
                    "    `spawn.Yaw` FLOAT(6) NOT NULL," +
                    "    `spawn.Pitch` FLOAT(6) NOT NULL," +
                    "    owner VARCHAR(45) NOT NULL," +
                    "    PRIMARY KEY (town_uuid)," +
                    "    FOREIGN KEY (owner) REFERENCES " + tablePrefix + "kingdoms(kingdom_uuid) ON UPDATE CASCADE" +
                    ");");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Village Queries
     */
    private void villageSave() {
        Village.getVillages().forEach(village -> {
            if (!village.getUpdate())
                return;
            try {

                ResultSet rs = database.querySQL(
                        "SELECT * " +
                                "FROM " + tablePrefix + "villages " +
                                "WHERE EXISTS " +
                                "(SELECT * FROM " + tablePrefix + "villages WHERE `village_uuid`='" + village.getUUID().toString() + "')");
                if (!rs.next()) {
                    database.updateSQL(
                            "INSERT INTO " + tablePrefix + "villages " +
                                    "VALUES('" +
                                    village.getUUID().toString() + "','" +
                                    village.getLocation().getWorld().getUID().toString() + "','" +
                                    village.getName() + "'," +
                                    village.getLocation().getX() + "," +
                                    village.getLocation().getY() + "," +
                                    village.getLocation().getZ() + "," +
                                    village.getSpawn().getX() + "," +
                                    village.getSpawn().getY() + "," +
                                    village.getSpawn().getZ() + "," +
                                    village.getSpawn().getYaw() + "," +
                                    village.getSpawn().getPitch() + ",'" +
                                    village.getOwner().getUUID().toString() + "','" +
                                    village.getParent().getUUID().toString() + "');");

                } else {
                    PreparedStatement ps = database.getConnection().prepareStatement(
                            "UPDATE " + tablePrefix + "villages " +
                                    "SET " +
                                    "`world_uuid` = ?," +
                                    "`name` = ?," +
                                    "`location.X` = ?," +
                                    "`location.Y` = ?," +
                                    "`location.Z` = ?," +
                                    "`spawn.X` = ?," +
                                    "`spawn.Y` = ?," +
                                    "`spawn.Z` = ?," +
                                    "`spawn.Yaw` = ?," +
                                    "`spawn.Pitch` = ?," +
                                    "`owner` = ?, " +
                                    "`parent` = ? " +
                                    "WHERE `village_uuid` = ?");

                    // set the preparedstatement parameters
                    ps.setString(1, village.getLocation().getWorld().getUID().toString());
                    ps.setString(2, village.getName());
                    ps.setDouble(3, village.getLocation().getX());
                    ps.setDouble(4, village.getLocation().getY());
                    ps.setDouble(5, village.getLocation().getZ());
                    ps.setDouble(6, village.getSpawn().getX());
                    ps.setDouble(7, village.getSpawn().getY());
                    ps.setDouble(8, village.getSpawn().getZ());
                    ps.setDouble(9, village.getSpawn().getYaw());
                    ps.setDouble(10, village.getSpawn().getPitch());
                    ps.setString(11, village.getOwner().getUUID().toString());
                    ps.setString(12, village.getParent().getUUID().toString());
                    ps.setString(13, village.getUUID().toString());

                    ps.executeUpdate();
                    ps.close();

                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            village.update(false);
        });
    }

    private void villageLoad() {
        try {
            ResultSet rs = database.querySQL("SELECT * FROM " + tablePrefix + "villages");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void villageRemove() {
    }

    private void villageTableCreate() {
        try {
            database.updateSQL("CREATE TABLE IF NOT EXISTS " + tablePrefix + "villages (" +
                    "    `village_uuid` VARCHAR(45) NOT NULL," +
                    "    `world_uuid` VARCHAR(45) NOT NULL," +
                    "    `name` VARCHAR(25) NOT NULL," +
                    "    `location.X` FLOAT(6) NOT NULL," +
                    "    `location.Y` FLOAT(6) NOT NULL," +
                    "    `location.Z` FLOAT(6) NOT NULL," +
                    "    `spawn.X` FLOAT(6) NOT NULL," +
                    "    `spawn.Y` FLOAT(6) NOT NULL," +
                    "    `spawn.Z` FLOAT(6) NOT NULL," +
                    "    `spawn.Yaw` FLOAT(6) NOT NULL," +
                    "    `spawn.Pitch` FLOAT(6) NOT NULL," +
                    "    `owner` VARCHAR(45) NOT NULL," +
                    "    `parent` VARCHAR(45) NOT NULL," +
                    "    PRIMARY KEY (`village_uuid`)," +
                    "    FOREIGN KEY (`owner`) REFERENCES " + tablePrefix + "kingdoms(`kingdom_uuid`) ON UPDATE CASCADE," +
                    "    FOREIGN KEY (`parent`) REFERENCES " + tablePrefix + "towns(`town_uuid`) ON UPDATE CASCADE" +
                    ");");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void usersSave() {
        Bukkit.getOnlinePlayers().forEach(user -> {
            PlayerWrapper wrapper = new PlayerWrapper(user.getUniqueId());
            Kingdom kingdom = wrapper.getKingdom(ActiveWorld.getActiveWorld(user.getWorld()));
            try {

                ResultSet rs = database.querySQL(
                        "SELECT * " +
                                "FROM " + tablePrefix + "kingdoms " +
                                "WHERE EXISTS " +
                                "(SELECT * FROM " + tablePrefix + "users WHERE `user_uuid`='" + user.getUniqueId().toString() + "')");
                if (!rs.next()) {
                    database.updateSQL(
                            "INSERT INTO " + tablePrefix + "kingdoms " +
                                    "VALUES('" +
                                    user.getUniqueId().toString() + "','" +
                                    kingdom.getUUID().toString() + "','" +
                                    wrapper.getHierarchy().getName() + "');");

                } else {
                    PreparedStatement ps = database.getConnection().prepareStatement(
                            "UPDATE " + tablePrefix + "kingdoms " +
                                    "SET " +
                                    "`kingdom_uuid` = ?," +
                                    "`rank` = ?" +
                                    "WHERE `user_uuid`=?");

                    // set the preparedstatement parameters
                    ps.setString(1, kingdom.getUUID().toString());
                    ps.setString(2, wrapper.getHierarchy().getName());
                    ps.setString(3, user.getUniqueId().toString());

                    ps.executeUpdate();
                    ps.close();

                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private void userLoad() {
    }

    private void userRemove() {
    }

    private void userTableCreate() {
        try {
            database.updateSQL("CREATE TABLE IF NOT EXISTS " + tablePrefix + "users (" +
                    "    user_uuid VARCHAR(45) NOT NULL," +
                    "    kingdom_uuid VARCHAR(45) NOT NULL," +
                    "    hierarchy VARCHAR(10) NOT NULL," +
                    "    PRIMARY KEY (user_uuid)," +
                    "    FOREIGN KEY (kingdom_uuid) REFERENCES " + tablePrefix + "kingdoms(kingdom_uuid) ON UPDATE CASCADE " +
                    ");");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //TODO:
    public void saveUser(Player user) {
        PlayerWrapper wrapper = new PlayerWrapper(user.getUniqueId());
        Kingdom kingdom = wrapper.getKingdom(ActiveWorld.getActiveWorld(user.getWorld()));

        try {
            // TODO: Tiny Performance enhancement Don't "Select *" Instead select something specific
            ResultSet rs = database.querySQL(
                    "SELECT * " +
                            "FROM " + tablePrefix + "kingdoms " +
                            "WHERE EXISTS " +
                            "(SELECT * FROM " + tablePrefix + "users WHERE `user_uuid`='" + user.getUniqueId().toString() + "')");
            if (!rs.next()) {
                database.updateSQL(
                        "INSERT INTO " + tablePrefix + "kingdoms " +
                                "VALUES('" +
                                user.getUniqueId().toString() + "','" +
                                kingdom.getUUID().toString() + "','" +
                                wrapper.getHierarchy().getName() + "');");

            } else {
                PreparedStatement ps = database.getConnection().prepareStatement(
                        "UPDATE " + tablePrefix + "kingdoms " +
                                "SET " +
                                "`kingdom_uuid` = ?," +
                                "`rank` = ?" +
                                "WHERE `user_uuid`=?");

                // set the preparedstatement parameters
                ps.setString(1, kingdom.getUUID().toString());
                ps.setString(2, wrapper.getHierarchy().getName());
                ps.setString(3, user.getUniqueId().toString());

                ps.executeUpdate();
                ps.close();

            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
