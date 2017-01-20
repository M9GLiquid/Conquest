package eu.kingconquest.conquest.database;

import java.sql.Connection;
import java.sql.SQLException;

import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

public class DatabaseManager {

	private static Connection connection;

	public DatabaseManager() {
		YmlStorage config = YmlStorage.getConfig("Config");
		if (config.getString("Database.Backend").equalsIgnoreCase("none")){
			new Message(null, MessageType.CONSOLE, "-> Mysql, Succeeded.");
			return;
		}

		if (config.getString("Database.Backend").equalsIgnoreCase("mysql")){

			int port = Integer.valueOf(YmlStorage.getStr("Port"));
			String host = YmlStorage.getStr("Host");
			String database = YmlStorage.getStr("Database");
			String user = YmlStorage.getStr("Username");
			String pass =  YmlStorage.getStr("Password");
			
			MySQL mysql = new MySQL(host, port, database, user, pass);
			try{
				connection = mysql.connect();
				new Message(null, MessageType.CONSOLE, "-> Mysql, Connected.");
			}catch (ClassNotFoundException | SQLException e){
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					new Message(null, MessageType.CONSOLE, "-> Mysql, Failed.");
				}
				new Message(null, MessageType.CONSOLE, "-> Mysql, Failed.");
			}
		}else if (config.getString("Database.Backend").equalsIgnoreCase("sqlite")){
			SQLite sqlite = new SQLite();
			try{
				connection = sqlite.connect();
				new Message(null, MessageType.CONSOLE, "-> SQLite, Connected.");
			}catch (ClassNotFoundException | SQLException e){
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					new Message(null, MessageType.CONSOLE, "-> SQLite, Failed.");
				}
				new Message(null, MessageType.CONSOLE, "-> SQLite, Failed.");
				e.printStackTrace();
			}
		}else if (config.getString("Database.Backend").equalsIgnoreCase("flatfile")){
			//YAML database
			//new Save();
			//new Load();
			//new Remove();
		}
	}
}
