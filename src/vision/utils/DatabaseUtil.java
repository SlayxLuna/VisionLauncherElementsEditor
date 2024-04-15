package vision.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import vision.VisionEditorLauncher;
import vision.servers.Database;
import vision.servers.Server;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class DatabaseUtil
{
	private static Connection connection;
	
	public static Connection getConnection()
	{
		try
		{
			if (connection.isClosed())
			{
				updateConnection(VisionEditorLauncher.currentDatabase);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return connection;
	}
	
	public static void updateConnection(Database database)
	{
		String url = database.getURL();
		String user = database.getUser();
		String password = database.getPassword();
		try
		{
			if (connection != null && !connection.isClosed())
			{
				connection.close();
			}
			connection = DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void initDatabaseConnection(Server server)
	{
		try
		{
			String url = server.getDatabase().getURL();
			String user = server.getDatabase().getUser();
			String password = server.getDatabase().getPassword();
			connection = DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
