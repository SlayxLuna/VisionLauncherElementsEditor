package vision;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vision.servers.Database;
import vision.servers.Server;
import vision.utils.DatabaseUtil;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionEditorLauncher extends Application
{
	private List<Server>	servers;
	public static Database	currentDatabase;
	private StackPane		loadingPane;
	
	public void start(Stage primaryStage) throws Exception
	{
		try
		{
			Image logoImage = new Image(new File("data/logo.png").toURI().toString());
			primaryStage.getIcons().add(logoImage);
		}
		catch (Exception e)
		{
			System.err.println("Error loading logo image: " + e.getMessage());
		}
		servers = Server.parseServersFromXML("data/servers.xml");
		currentDatabase = servers.get(0).getDatabase();
		DatabaseUtil.initDatabaseConnection(servers.get(0));
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		BorderPane root = loader.load();
		MenuBar menuBar = createMenuBar();
		root.setTop(menuBar);
		VisionController mainController = loader.getController();
		Scene scene = new Scene(root, 1220, 840);
		scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
		primaryStage.setTitle("Vision Element Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private MenuBar createMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		Menu serversMenu = new Menu("Servers");
		ToggleGroup toggleGroup = new ToggleGroup();
		for (Server server : servers)
		{
			RadioMenuItem menuItem = new RadioMenuItem(server.getName());
			menuItem.setOnAction(event ->
			{
				updateDatabaseConnection(server.getDatabase());
				reloadData();
			});
			menuItem.setToggleGroup(toggleGroup);
			serversMenu.getItems().add(menuItem);
		}
		if (!serversMenu.getItems().isEmpty())
		{
			((RadioMenuItem) serversMenu.getItems().get(0)).setSelected(true);
			updateDatabaseConnection(servers.get(0).getDatabase());
			reloadData();
		}
		menuBar.getMenus().add(serversMenu);
		return menuBar;
	}
	
	private void reloadData()
	{
		VisionController.getInstance().reloadData();
		System.out.println("Data reloaded for server: " + currentDatabase.getURL());
	}
	
	private void updateDatabaseConnection(Database database)
	{
		currentDatabase = database;
		DatabaseUtil.updateConnection(currentDatabase);
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
