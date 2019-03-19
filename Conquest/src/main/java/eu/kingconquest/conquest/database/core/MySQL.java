package eu.kingconquest.conquest.database.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Connects to and uses a MySQL database
 *
 * @author -_Husky_-
 * @author tips48
 */
public class MySQL extends Database {
    private String database;
    private String password;
    private String host;
    private String user;
    private int port;

    /**
     * Creates a new MySQL instance for a specific database
     *
     * @param hostname    Name of the host
     * @param port        Port number
     * @param database    Database name
     * @param username    Username
     * @param password    Password
     * @param tablePrefix SQL Table Prefix
     */
    public MySQL(String hostname, int port, String database, String tablePrefix, String username, String password) {
        Database.database = this;
        this.host = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    @Override
    public Connection connect() throws SQLException {
        if (checkConnection()) {
            return connection;
        }
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("useSSL", "true");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("verifyServerCertificate", "false");

        String connectionURL = "jdbc:mysql://"
                + this.host + ":" + this.port;
        if (database != null)
            connectionURL = connectionURL + "/" + this.database;


        connection = DriverManager.getConnection(connectionURL, properties);
        return connection;
    }

}