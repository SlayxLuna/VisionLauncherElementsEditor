package vision.controllers.model.changelog;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class NewChangeLogDialog extends Dialog<Changelog>
{
	private TextField			titleField;
	private TextField			descriptionField;
	private TextField			serverField;
	private DatePicker			datePicker;
	private Spinner<Integer>	hourSpinner;
	private Spinner<Integer>	minuteSpinner;
	
	public NewChangeLogDialog(int lastUsedId)
	{
		setTitle("Add New Change Log");
		setHeaderText("Please enter the details for the new change log:");
		titleField = new TextField();
		descriptionField = new TextField();
		serverField = new TextField();
		datePicker = new DatePicker();
		hourSpinner = new Spinner<>(0, 23, LocalDateTime.now().getHour());
		minuteSpinner = new Spinner<>(0, 59, LocalDateTime.now().getMinute());
		GridPane grid = new GridPane();
		grid.add(new Label("Title:"), 0, 0);
		grid.add(titleField, 1, 0);
		grid.add(new Label("Description:"), 0, 1);
		grid.add(descriptionField, 1, 1);
		grid.add(new Label("Server:"), 0, 2);
		grid.add(serverField, 1, 2);
		grid.add(new Label("Date:"), 0, 3);
		grid.add(datePicker, 1, 3);
		grid.add(new Label("Time:"), 0, 4);
		datePicker.setValue(LocalDate.now());
		grid.add(createTimePicker(), 1, 4);
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
				String server = serverField.getText();
				LocalDate selectedDate = datePicker.getValue();
				LocalTime selectedTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
				LocalDateTime dateTime = LocalDateTime.of(selectedDate, selectedTime);
				return new Changelog(id, title, description, Timestamp.valueOf(dateTime), server);
			}
			return null;
		});
	}
	
	private Node createTimePicker()
	{
		HBox hbox = new HBox();
		hourSpinner.setEditable(true);
		minuteSpinner.setEditable(true);
		hbox.getChildren().addAll(hourSpinner, new Label(":"), minuteSpinner);
		return hbox;
	}
}
