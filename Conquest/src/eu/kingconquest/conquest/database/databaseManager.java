package eu.kingconquest.conquest.database;

import java.sql.Connection;
import java.sql.SQLException;

import eu.kingconquest.conquest.util.ChatManager;

public class DatabaseManager {

	private static Connection connection;

	public DatabaseManager() {
		Config config = Config.getConfig("Config");
		if (config.getString("Database.Backend").equalsIgnoreCase("none")){
			ChatManager.Console("-> Mysql, Succeeded.");
			return;
		}

		if (config.getString("Database.Backend").equalsIgnoreCase("mysql")){

			int port = Integer.valueOf(Config.getStr("Port"));
			String host = Config.getStr("Host");
			String database = Config.getStr("Database");
			String user = Config.getStr("Username");
			String pass =  Config.getStr("Password");
			
			MySQL mysql = new MySQL(host, port, database, user, pass);
			try{
				connection = mysql.connect();
				ChatManager.Console("-> Mysql, Connected.");
			}catch (ClassNotFoundException | SQLException e){
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					ChatManager.Console("-> Mysql, Failed.");
				}
				ChatManager.Console("-> Mysql, Failed.");
			}
		}else if (config.getString("Database.Backend").equalsIgnoreCase("sqlite")){
			SQLite sqlite = new SQLite();
			try{
				connection = sqlite.connect();
				ChatManager.Console("-> SQLite, Connected.");
			}catch (ClassNotFoundException | SQLException e){
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					ChatManager.Console("-> SQLite, Failed.");
				}
				ChatManager.Console("-> SQLite, Failed.");
				e.printStackTrace();
			}
		}else if (config.getString("Database.Backend").equalsIgnoreCase("flatfile")){
			//YAML database
		}
	}
}
