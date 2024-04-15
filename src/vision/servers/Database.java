package vision.servers;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class Database
{
	private String	url;
	private String	user;
	private String	password;
	
	public Database(String url, String user, String password)
	{
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setURL(String ip)
	{
		this.url = ip;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	@Override
	public String toString()
	{
		return "Database [url=" + url + ", user=" + user + ", password=" + password + "]";
	}
}
