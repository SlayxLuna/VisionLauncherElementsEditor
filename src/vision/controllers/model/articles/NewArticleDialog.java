package vision.controllers.model.articles;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class NewArticleDialog extends Dialog<Article>
{
	private TextField	titleField;
	private TextField	descriptionField;
	private TextField	tagField;
	private ColorPicker	tagHexColorPicker;
	private TextField	cardImageUrlField;
	
	public NewArticleDialog(int lastUsedId)
	{
		setTitle("Add New Article");
		setHeaderText("Please enter the details for the new article:");
		titleField = new TextField();
		descriptionField = new TextField();
		tagField = new TextField();
		tagHexColorPicker = new ColorPicker();
		cardImageUrlField = new TextField();
		GridPane grid = new GridPane();
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Description:"), 0, 1);
		grid.add(descriptionField, 1, 1);
		grid.add(new Label("Tag:"), 0, 2);
		grid.add(tagField, 1, 2);
		grid.add(new Label("Tag Hex Color:"), 0, 3);
		grid.add(tagHexColorPicker, 1, 3);
		grid.add(new Label("Card Image URL:"), 0, 4);
		grid.add(cardImageUrlField, 1, 4);
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
				String tag = tagField.getText();
				String tagHexColor = Integer.toHexString(tagHexColorPicker.getValue().hashCode()).substring(0, 6);
				tagHexColor = tagHexColor.toUpperCase();
				String cardImageUrl = cardImageUrlField.getText();
				return new Article(id, title, description, tag, tagHexColor, cardImageUrl);
			}
			return null;
		});
	}
}
