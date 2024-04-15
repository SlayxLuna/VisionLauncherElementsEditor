package vision.servers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class Server
{
	private String		name;
	private Database	database;
	
	public Server(String name, Database database)
	{
		this.name = name;
		this.database = database;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Database getDatabase()
	{
		return database;
	}
	
	public void setDatabase(Database database)
	{
		this.database = database;
	}
	
	@Override
	public String toString()
	{
		return "Server [name=" + name + ", database=" + database + "]";
	}
	
	public static List<Server> parseServersFromXML(String filePath)
	{
		List<Server> servers = new ArrayList<>();
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filePath);
			NodeList serverNodes = doc.getElementsByTagName("server");
			for (int i = 0; i < serverNodes.getLength(); i++)
			{
				Element serverElement = (Element) serverNodes.item(i);
				String name = serverElement.getAttribute("name");
				Element databaseElement = (Element) serverElement.getElementsByTagName("database").item(0);
				String url = databaseElement.getAttribute("url");
				String user = databaseElement.getAttribute("user");
				String password = databaseElement.getAttribute("password");
				Database database = new Database(url, user, password);
				Server server = new Server(name, database);
				servers.add(server);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return servers;
	}
}
