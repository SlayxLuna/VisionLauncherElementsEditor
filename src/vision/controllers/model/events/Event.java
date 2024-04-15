package vision.controllers.model.events;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class Event
{
	private int		id;
	private String	title;
	private String	description;
	private String	cardImageUrl;
	private String	redirectUrl;
	
	public Event(int id, String title, String description, String cardImageUrl, String redirectUrl)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.cardImageUrl = cardImageUrl;
		this.redirectUrl = redirectUrl;
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
	
	public String getCardImageUrl()
	{
		return cardImageUrl;
	}
	
	public void setCardImageUrl(String cardImageUrl)
	{
		this.cardImageUrl = cardImageUrl;
	}
	
	public String getRedirectUrl()
	{
		return redirectUrl;
	}
	
	public void setRedirectUrl(String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}
	
	@Override
	public String toString()
	{
		return "Event{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", cardImageUrl='" + cardImageUrl + '\'' + ", redirectUrl='" + redirectUrl + '\'' + '}';
	}
}
