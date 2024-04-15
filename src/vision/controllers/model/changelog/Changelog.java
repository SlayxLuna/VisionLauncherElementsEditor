package vision.controllers.model.changelog;

import java.sql.Timestamp;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class Changelog
{
	private int			id;
	private String		title;
	private String		description;
	private Timestamp	date;
	private String		server;
	
	public Changelog(int id, String title, String description, Timestamp date, String server)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.date = date;
		this.server = server;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public Timestamp getDate()
	{
		return date;
	}
	
	public void setDate(Timestamp date)
	{
		this.date = date;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public void setServer(String server)
	{
		this.server = server;
	}
	
	@Override
	public String toString()
	{
		return "Changelog{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", date=" + date + ", server='" + server + '\'' + '}';
	}
}
