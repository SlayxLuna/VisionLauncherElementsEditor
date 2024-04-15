package vision.controllers.model.homeSlider;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class NewHomeSliderDialog extends Dialog<HomeSlider>
{
	private TextField	titleField;
	private TextField	titleIconUrlField;
	private TextArea	descriptionField;
	private TextField	backgroundBannerUrlField;
	private TextField	clickUrlField;
	
	public NewHomeSliderDialog(int lastUsedId)
	{
		setTitle("Add New Home Slider");
		setHeaderText("Please enter the details for the new Home Slider:");
		titleField = new TextField();
		titleIconUrlField = new TextField();
		descriptionField = new TextArea();
		backgroundBannerUrlField = new TextField();
		clickUrlField = new TextField();
		GridPane grid = new GridPane();
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Title Icon URL:"), 0, 1);
		grid.add(titleIconUrlField, 1, 1);
		grid.add(new Label("Description:"), 0, 2);
		grid.add(descriptionField, 1, 2);
		grid.add(new Label("Background Banner URL:"), 0, 3);
		grid.add(backgroundBannerUrlField, 1, 3);
		grid.add(new Label("Click URL:"), 0, 4);
		grid.add(clickUrlField, 1, 4);
		getDialogPane().setContent(grid);
		ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
		setResultConverter(dialogButton ->
		{
			if (dialogButton == addButton)
			{
				int id = lastUsedId + 1;
				String title = titleField.getText();
				String titleIconUrl = titleIconUrlField.getText();
				String description = descriptionField.getText();
				String backgroundBannerUrl = backgroundBannerUrlField.getText();
				String clickUrl = clickUrlField.getText();
				return new HomeSlider(id, title, titleIconUrl, description, backgroundBannerUrl, clickUrl);
			}
			return null;
		});
	}
}
