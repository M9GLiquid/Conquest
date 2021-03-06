package eu.kingconquest.conquest.database;

import eu.kingconquest.conquest.MainClass;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLite extends Database {

    @Override
    public Connection connect() throws SQLException,
            ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }

		String pathway = MainClass.getInstance().getDataFolder() + File.separator + "Data" + File.separator + "database.ql";
		File file = new File(pathway);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}catch (IOException exception){
				MainClass.getInstance().getLogger().severe("Error while creating file " + file.getName());
			}
		}
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + pathway);
        return connection;
    }
}