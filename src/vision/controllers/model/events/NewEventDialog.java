package vision.controllers.model.events;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class NewEventDialog extends Dialog<Event>
{
	private TextField	titleField;
	private TextField	descriptionField;
	private TextField	cardImageUrlField;
	private TextField	redirectUrlField;
	
	public NewEventDialog(int lastUsedId)
	{
		setTitle("Add New Event");
		setHeaderText("Please enter the details for the new event:");
		titleField = new TextField();
		descriptionField = new TextField();
		cardImageUrlField = new TextField();
		redirectUrlField = new TextField();
		GridPane grid = new GridPane();
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Description:"), 0, 1);
		grid.add(descriptionField, 1, 1);
		grid.add(new Label("Card Image URL:"), 0, 2);
		grid.add(cardImageUrlField, 1, 2);
		grid.add(new Label("Redirect URL:"), 0, 3);
		grid.add(redirectUrlField, 1, 3);
		getDialogPane().setContent(grid);
		ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
		setResultConverter(dialogButton ->
		{
			if (dialogButton == addButton)
			{
				int id = lastUsedId + 1;
				String title = titleField.getText();
				String description = descriptionField.getText();
				String cardImageUrl = cardImageUrlField.getText();
				String redirectUrl = redirectUrlField.getText();
				return new Event(id, title, description, cardImageUrl, redirectUrl);
			}
			return null;
		});
	}
}
