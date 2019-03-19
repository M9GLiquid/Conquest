package eu.kingconquest.conquest.database.core;

import eu.kingconquest.conquest.util.DatabaseType;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 * 
 * @author -_Husky_-
 * @author tips48
 */
public abstract class Database {
    private static String outputStream = "&6| - &cFailed:";
	protected static Database database;
	private static DatabaseType databaseType;

	protected Connection connection;

	/**
	 * Creates a new Database
	 *
	 */
	protected Database() {
		this.connection = null;
	}

	/**
	 * Opens a connection with the database
	 * 
	 * @return Opened connection
	 * @throws SQLException
	 *             if the connection can not be opened
	 * @throws ClassNotFoundException
	 *             if the driver cannot be found
	 */
	public abstract Connection connect() throws SQLException,
			ClassNotFoundException;

	/**
	 * Checks if a connection is open with the database
	 * 
	 * @return true if the connection is open
	 * @throws SQLException
	 *             if the connection cannot be checked
	 */
	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	/**
	 * Gets the connection with the database
	 * 
	 * @return Connection with the database, null if none
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Closes the connection with the database
	 * 
	 * @return true if successful
	 * @throws SQLException
	 *             if the connection cannot be closed
	 */
	public boolean closeConnection() throws SQLException {
		if (connection == null) {
			return false;
		}
		connection.close();
		return true;
	}


	/**
	 * Executes a SQL Query<br>
	 * 
	 * If the connection is closed, it will be opened
	 * 
	 * @param query
	 *            Query to be run
	 * @return the results of the query
	 * @throws SQLException
	 *             If the query cannot be executed
	 * @throws ClassNotFoundException
	 *             If the driver cannot be found; see {@link #connect()}
	 */
	public ResultSet querySQL(String query) throws SQLException,
			ClassNotFoundException {
		if (!checkConnection()) {
			connect();
		}

		Statement statement = connection.createStatement();

		return statement.executeQuery(query);
	}

	public static DatabaseType getType() {
		return databaseType;
	}

	public static void setType(DatabaseType type) {
		databaseType = type;
	}

	public static Database getDatabase() {
		return database;
	}

	/**
	 * Executes an Update SQL Query<br>
	 * See {@link java.sql.Statement#executeUpdate(String)}<br>
	 * If the connection is closed, it will be opened
	 *
	 * @param query
	 *            Query to be run
	 * @return Result Code, see {@link java.sql.Statement#executeUpdate(String)}
	 * @throws SQLException
	 *             If the query cannot be executed
	 * @throws ClassNotFoundException
	 *             If the driver cannot be found; see {@link #connect()}
	 */
	public void updateSQL(String query) throws SQLException,
			ClassNotFoundException {
		if (!checkConnection()) {
			connect();
		}

		Statement statement = connection.createStatement();

		statement.executeUpdate(query);
	}


    public static void output() {
        new Message(MessageType.CONSOLE, outputStream);
    }

    public static void setOutputStream(String stream) {
        outputStream = stream;
    }
}