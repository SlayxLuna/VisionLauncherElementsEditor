package vision.controllers.model.homeSlider;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class HomeSlider
{
	private int		id;
	private String	title;
	private String	titleIconUrl;
	private String	description;
	private String	backgroundBannerUrl;
	private String	clickUrl;
	
	public HomeSlider(int id, String title, String titleIconUrl, String description, String backgroundBannerUrl, String clickUrl)
	{
		this.id = id;
		this.title = title;
		this.titleIconUrl = titleIconUrl;
		this.description = description;
		this.backgroundBannerUrl = backgroundBannerUrl;
		this.clickUrl = clickUrl;
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
	
	public String getTitleIconUrl()
	{
		return titleIconUrl;
	}
	
	public void setTitleIconUrl(String titleIconUrl)
	{
		this.titleIconUrl = titleIconUrl;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getBackgroundBannerUrl()
	{
		return backgroundBannerUrl;
	}
	
	public void setBackgroundBannerUrl(String backgroundBannerUrl)
	{
		this.backgroundBannerUrl = backgroundBannerUrl;
	}
	
	public String getClickUrl()
	{
		return clickUrl;
	}
	
	public void setClickUrl(String clickUrl)
	{
		this.clickUrl = clickUrl;
	}
	
	@Override
	public String toString()
	{
		return "HomeSlider{" + "id=" + id + ", title='" + title + '\'' + ", titleIconUrl='" + titleIconUrl + '\'' + ", description='" + description + '\'' + ", backgroundBannerUrl='" + backgroundBannerUrl + '\'' + ", clickUrl='" + clickUrl + '\'' + '}';
	}
}
