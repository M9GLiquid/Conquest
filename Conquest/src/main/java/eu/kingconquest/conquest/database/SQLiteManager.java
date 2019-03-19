package eu.kingconquest.conquest.database;

import eu.kingconquest.conquest.database.core.Database;
import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.util.DataType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

import java.sql.SQLException;

public class SQLiteManager {
    private static Database database;
    private static String tablePrefix;
    private static String outputStream = "&6| - &cFailed:";//TODO: Move into last executed method for Failed/Success state (Try/Catch)
    private YmlStorage config;

    public SQLiteManager(DataType option) {
        database = Database.getDatabase();
        tablePrefix = YmlStorage.getStr("TablePrefix");
        switch (option) {
            case LOAD:
                //worldLoad();
                //kingdomLoad();
                //townLoad();
                //villageLoad();
                outputStream = "&6| - &aSuccess:";//TODO: Move into last executed method for Failed/Success state (Try/Catch)
                break;
            case REMOVE:
                //kingdomRemove();
                //kingRemove();
                //townRemove();
                //villageRemove();
                break;
            case SAVE:
                //worldSave();
                //kingSave();
                //townSave();
                //villageSave();
                outputStream = "&6| - &aSuccess:";//TODO: Move into last executed method for Failed/Success state (Try/Catch)
                break;
            case CREATE:
                kingdomTableCreate(); //Create first since this has no Foreign Keys
                //kingTableCreate();
                //worldTableCreate();
                //townTableCreate();
                //villageTableCreate();
                break;
        }
    }

    public static void kingdomTableCreate() {
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

    public static void output() {
        new Message(MessageType.CONSOLE, outputStream);
    }
}
