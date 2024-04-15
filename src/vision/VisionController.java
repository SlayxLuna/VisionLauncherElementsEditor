package vision;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import vision.controllers.VisionArticleController;
import vision.controllers.VisionChangelogController;
import vision.controllers.VisionEventController;
import vision.controllers.VisionFAQController;
import vision.controllers.VisionHomeSliderController;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionController implements Initializable
{
	private static VisionController	instance;
	@FXML
	private Tab						articlesTab;
	@FXML
	private Tab						changeLogsTab;
	@FXML
	private Tab						eventsTab;
	@FXML
	private Tab						faqTab;
	@FXML
	private Tab						homeSliderTab;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		loadTabs();
		setInstance(this);
	}
	
	public void reloadData()
	{
		loadTabs();
	}
	
	private void loadTabs()
	{
		try
		{
			FXMLLoader articlesLoader = new FXMLLoader(getClass().getResource("ArticlesTab.fxml"));
			articlesTab.setContent(articlesLoader.load());
			VisionArticleController articleController = articlesLoader.getController();
			articleController.initialize(null, null);
			FXMLLoader changeLogsLoader = new FXMLLoader(getClass().getResource("ChangelogsTab.fxml"));
			changeLogsTab.setContent(changeLogsLoader.load());
			VisionChangelogController changelogController = changeLogsLoader.getController();
			changelogController.initialize(null, null);
			FXMLLoader eventsLoader = new FXMLLoader(getClass().getResource("EventsTab.fxml"));
			eventsTab.setContent(eventsLoader.load());
			VisionEventController eventsController = eventsLoader.getController();
			eventsController.initialize(null, null);
			FXMLLoader faqLoader = new FXMLLoader(getClass().getResource("FAQTab.fxml"));
			faqTab.setContent(faqLoader.load());
			VisionFAQController faqController = faqLoader.getController();
			faqController.initialize();
			FXMLLoader homeSliderLoader = new FXMLLoader(getClass().getResource("HomeSliderTab.fxml"));
			homeSliderTab.setContent(homeSliderLoader.load());
			VisionHomeSliderController homeSliderController = homeSliderLoader.getController();
			homeSliderController.initialize();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void setInstance(VisionController controller)
	{
		instance = controller;
	}
	
	public static VisionController getInstance()
	{
		return instance;
	}
}
