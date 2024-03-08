package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection
{
	private static String HOST = "localhost";
	private static String PASSWORD = "projects";
	private static int PORT = 3306;
	private static String SCHEMA = "projects";
	private static String USER = "projects";
	
	public static Connection getConnection()
	{
		final String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s", HOST, PORT, SCHEMA, USER, PASSWORD);
		
		try
		{
			Connection c = DriverManager.getConnection(uri); 
			System.out.println("Connection successful " + uri);
			return c;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			System.out.println("Connection failed: " + uri);
			throw new DbException("Connection failed: " + uri);
		}
	}
}