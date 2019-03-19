package eu.kingconquest.conquest.database.core;

import eu.kingconquest.conquest.database.FlatFileManager;
import eu.kingconquest.conquest.database.MysqlManager;
import eu.kingconquest.conquest.util.DataType;
import eu.kingconquest.conquest.util.DatabaseType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

	private static Connection connection;

	public DatabaseManager() {
		YmlStorage config = YmlStorage.getConfig("Config");
        switch (DatabaseType.valueOf(config.getString("Database.Backend").toUpperCase())) {
            case FLATFILE:
                FlatFileManager.load();
                break;
            case MYSQL:
                int port = Integer.valueOf(YmlStorage.getStr("Port"));
                String host = YmlStorage.getStr("Host");
                String database = YmlStorage.getStr("Database");
                String user = YmlStorage.getStr("Username");
                String pass = YmlStorage.getStr("Password");
                String tablePrefix = YmlStorage.getStr("TablePrefix");

                MySQL mysql = new MySQL(
                        host,
                        port,
                        database,
                        tablePrefix,
                        user,
                        pass);
                try {
                    connection = mysql.connect();
                    new MysqlManager(DataType.CREATE);
                } catch (SQLException e) {
                    try {
                        connection.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                }
                break;
            case SQLITE:
                SQLite sqlite = new SQLite();
                try {
                    connection = sqlite.connect();
                    new Message(MessageType.CONSOLE, "-> SQLite, Connected.");
                } catch (ClassNotFoundException | SQLException e) {
                    try {
                        connection.close();
                        new Message(MessageType.CONSOLE, "-> SQLite, Closed.");
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                        new Message(MessageType.CONSOLE, "-> SQLite, Failed.");
                    }
                    new Message(MessageType.CONSOLE, "-> SQLite, Failed.");
                    e.printStackTrace();
                }
                break;
		}
	}
}
