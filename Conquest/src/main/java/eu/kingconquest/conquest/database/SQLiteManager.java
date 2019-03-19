package eu.kingconquest.conquest.database;

import eu.kingconquest.conquest.database.core.Database;
import eu.kingconquest.conquest.database.core.YmlStorage;
import eu.kingconquest.conquest.util.DataType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

public class SQLiteManager {
    private static Database database;
    private static String tablePrefix;
    private static String outputStream = "&6| - &cFailed:";
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
                outputStream = "&6| - &aSuccess:";
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
                outputStream = "&6| - &aSuccess:";
                break;
            case CREATE:
                //kingdomTableCreate(); //Create first since this has no Foreign Keys
                //kingTableCreate();
                //worldTableCreate();
                //townTableCreate();
                //villageTableCreate();
                break;
        }
    }

    public static void output() {
        new Message(MessageType.CONSOLE, outputStream);
    }
}
