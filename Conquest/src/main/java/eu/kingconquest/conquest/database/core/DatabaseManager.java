package eu.kingconquest.conquest.database.core;

import eu.kingconquest.conquest.database.FlatFileManager;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

	private static Connection connection;

	public DatabaseManager() {
        switch (Database.getType()) {
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
                } catch (SQLException e) {
                    try {
                        connection.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    e.printStackTrace();
                }
                break;
            case SQLITE:
                SQLite sqlite = new SQLite();
                try {
                    connection = sqlite.connect();
                } catch (ClassNotFoundException | SQLException e) {
                    try {
                        connection.close();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    e.printStackTrace();
                }
                break;
		}
	}
}
