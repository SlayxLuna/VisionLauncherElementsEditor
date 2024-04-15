package vision.controllers.model.faq;

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
public class NewFAQDialog extends Dialog<FAQ>
{
	private TextField	titleField;
	private TextArea	descriptionField;
	
	public NewFAQDialog(int lastUsedId)
	{
		setTitle("Add New FAQ");
		setHeaderText("Please enter the details for the new FAQ:");
		titleField = new TextField();
		descriptionField = new TextArea();
		GridPane grid = new GridPane();
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Description:"), 0, 1);
		grid.add(descriptionField, 1, 1);
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
				return new FAQ(id, title, description);
			}
			return null;
		});
	}
}
