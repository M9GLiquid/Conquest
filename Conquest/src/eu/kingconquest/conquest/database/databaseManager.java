package eu.kingconquest.conquest.database;

import java.sql.Connection;
import java.sql.SQLException;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.util.Config;

public class databaseManager {
	public Config conf;
	protected static MySQL mysql;
	protected static SQLite sqlite;
	
	private static Main plugin;

	@SuppressWarnings("unused")
	private static String port;
	private static String host;
	private static String db;
	private static String user;
	private static String pass;
	
	private static Connection c;
	
	public databaseManager() {
		conf = Config.getConfig("Config");
	}

	@SuppressWarnings("unused")
	public void Main(Main pl) {
		plugin = pl;
		if (conf.getString("general.backend").equalsIgnoreCase("none") 
				|| conf.getString("general.backend").equalsIgnoreCase(null) 
				|| conf.getString("general.backend").equalsIgnoreCase("")){
			
			plugin.getLogger().info("-> Mysql, Succeeded.");return;}
		
		if ((conf.getString("general.backend").equalsIgnoreCase("mysql")) 
				&& (!(conf.getString("general.backend").equalsIgnoreCase("sqlite")))){
			
			port = (conf.getString("general.database.mysql.port") != null 
						? conf.getString("general.database.mysql.port") : "3306" );
			host = conf.getString("general.database.mysql.host");
			db = conf.getString("general.database.mysql.database");
			user = conf.getString("general.database.mysql.Usern");
			pass = conf.getString("general.database.mysql.Password");
			
			if (host == null || 
				db   == null || 
				user == null || 
				pass == null)
					return;
			try{
				c = mysql.openConnection();
				plugin.getLogger().info("-> Mysql, Succeeded.");
			}catch (ClassNotFoundException | SQLException e){
				try {
					c.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				plugin.getLogger().info("-> Mysql, Failed.");
			}
		}else{
			plugin.getLogger().info("-> Mysql, Failed.");
		}
		if (!(conf.getString("general.database.backend").equalsIgnoreCase("mysql")) && ((conf.getString("general.database.backend").equalsIgnoreCase("sqlite")))){
			SQLite sqlite = new SQLite(host);
			Connection c;
			try{
				c = sqlite.openConnection();
				plugin.getLogger().info("-> SQLite, Succeeded.");
			}catch (ClassNotFoundException | SQLException e){
				plugin.getLogger().info("-> SQLite, Failed.");
				e.printStackTrace();
			}
		}else{
			plugin.getLogger().info("-> SQLite, Failed.");
		}
	}
}
