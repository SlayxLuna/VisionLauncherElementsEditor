package vision.controllers.model.articles;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class Article
{
	private int		id;
	private String	title;
	private String	description;
	private String	tag;
	private String	tagHexColor;
	private String	cardImageUrl;
	
	public Article(int id, String title, String description, String tag, String tagHexColor, String cardImageUrl)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.tag = tag;
		this.tagHexColor = tagHexColor;
		this.cardImageUrl = cardImageUrl;
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
	
	public String getTag()
	{
		return tag;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public String getTagHexColor()
	{
		return tagHexColor;
	}
	
	public void setTagHexColor(String tagHexColor)
	{
		this.tagHexColor = tagHexColor;
	}
	
	public String getCardImageUrl()
	{
		return cardImageUrl;
	}
	
	public void setCardImageUrl(String cardImageUrl)
	{
		this.cardImageUrl = cardImageUrl;
	}
}
