package vision.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import vision.controllers.model.events.Event;
import vision.controllers.model.events.NewEventDialog;
import vision.utils.DatabaseUtil;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionEventController implements Initializable
{
	@FXML
	private TableView<Event>			eventTableView;
	@FXML
	private TableColumn<Event, Integer>	idColumn;
	@FXML
	private TableColumn<Event, String>	titleColumn;
	@FXML
	private TableColumn<Event, String>	descriptionColumn;
	@FXML
	private TableColumn<Event, String>	cardImageUrlColumn;
	@FXML
	private TableColumn<Event, String>	redirectUrlColumn;
	@FXML
	private VBox						eventVBox;
	private boolean						deleteColumnAdded	= false;
	private static final String			SELECT_QUERY		= "SELECT * FROM events";
	private static final String			DELETE_QUERY		= "DELETE FROM events WHERE id = ?";
	private static final String			UPDATE_QUERY		= "UPDATE events SET title = ?, description = ?, card_image_url = ?, redirect_url = ? WHERE id = ?";
	private static final String			INSERT_QUERY		= "INSERT INTO events (id, title, description, card_image_url, redirect_url) VALUES (?, ?, ?, ?, ?)";
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		configureTableColumns();
		fetchEvents();
		setupTableEditHandlers();
		eventTableView.setRowFactory(tv ->
		{
			TableRow<Event> row = new TableRow<>();
			row.setMinHeight(30);
			row.setMaxHeight(30);
			row.setOnMouseClicked(event ->
			{
				if (event.getClickCount() == 2 && !row.isEmpty())
				{
					Event rowData = row.getItem();
					TableColumn<Event, ?> clickedColumn = getColumnByPosition(event.getX());
					if (clickedColumn != null)
					{
						openEditWindowForColumn(rowData, clickedColumn);
					}
				}
			});
			return row;
		});
	}
	
	private TableColumn<Event, ?> getColumnByPosition(double x)
	{
		double totalWidth = 0.0;
		for (TableColumn<Event, ?> column : eventTableView.getColumns())
		{
			double columnStartX = totalWidth;
			double columnEndX = totalWidth + column.getWidth();
			if (x >= columnStartX && x <= columnEndX)
			{
				return column;
			}
			totalWidth += column.getWidth();
		}
		return null;
	}
	
	private void configureTableColumns()
	{
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
		cardImageUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCardImageUrl()));
		redirectUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRedirectUrl()));
		addDeleteColumnIfNeeded();
	}
	
	private void addDeleteColumnIfNeeded()
	{
		if (!deleteColumnAdded)
		{
			TableColumn<Event, Void> deleteColumn = new TableColumn<>("Delete");
			deleteColumn.setCellFactory(param -> new TableCell<Event, Void>()
			{
				private final Button deleteButton = new Button("Delete");
				{
					deleteButton.setOnAction(event ->
					{
						Event ev = getTableView().getItems().get(getIndex());
						deleteEvent(ev);
					});
				}
				
				@Override
				protected void updateItem(Void item, boolean empty)
				{
					super.updateItem(item, empty);
					if (empty)
					{
						setGraphic(null);
					}
					else
					{
						setGraphic(deleteButton);
					}
				}
			});
			eventTableView.getColumns().add(deleteColumn);
			deleteColumnAdded = true;
		}
	}
	
	private void fetchEvents()
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY); ResultSet resultSet = preparedStatement.executeQuery())
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				String description = resultSet.getString("description");
				String cardImageUrl = resultSet.getString("card_image_url");
				String redirectUrl = resultSet.getString("redirect_url");
				Event event = new Event(id, title, description, cardImageUrl, redirectUrl);
				eventTableView.getItems().add(event);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void setupTableEditHandlers()
	{
		Map<TableColumn<Event, ?>, String> columnMap = new HashMap<>();
		columnMap.put(titleColumn, "Title");
		columnMap.put(descriptionColumn, "Description");
		columnMap.put(cardImageUrlColumn, "Card Image URL");
		columnMap.put(redirectUrlColumn, "Redirect URL");
		columnMap.forEach((column, columnName) ->
		{
			column.setOnEditCommit(event -> handleEditColumn(event, columnName));
		});
	}
	
	private void openEditWindowForColumn(Event event, TableColumn<Event, ?> column)
	{
		String propertyName = column.getText();
		String currentValue = getValueByPropertyName(event, propertyName);
		TextArea textArea = new TextArea(currentValue);
		textArea.setPrefSize(600, 400);
		textArea.setPrefRowCount(10);
		textArea.setPrefColumnCount(40);
		textArea.setWrapText(true);
		textArea.setEditable(true);
		VBox vBox = new VBox(10);
		vBox.getChildren().addAll(textArea);
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Edit " + propertyName);
		dialog.setHeaderText("Edit " + propertyName);
		dialog.getDialogPane().setContent(vBox);
		ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
		dialog.setResultConverter(dialogButton ->
		{
			if (dialogButton == okButton)
			{
				return textArea.getText();
			}
			return null;
		});
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(newValue ->
		{
			setPropertyValue(event, propertyName, newValue);
			updateEventInDatabase(event);
			refreshTableView();
		});
	}
	
	private void updateEventInDatabase(Event event)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_QUERY))
		{
			preparedStatement.setString(1, event.getTitle());
			preparedStatement.setString(2, event.getDescription());
			preparedStatement.setString(3, event.getCardImageUrl());
			preparedStatement.setString(4, event.getRedirectUrl());
			preparedStatement.setInt(5, event.getId());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0)
			{
				showAlert("Event Updated", "The event has been successfully updated.");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while updating the event.");
		}
	}
	
	private void deleteEvent(Event event)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Deletion");
		alert.setHeaderText("Are you sure you want to delete this event?");
		alert.setContentText("Event ID: " + event.getId() + "\nTitle: " + event.getTitle());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(DELETE_QUERY))
			{
				preparedStatement.setInt(1, event.getId());
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0)
				{
					eventTableView.getItems().remove(event);
				}
				else
				{
					showAlert("Error", "Failed to delete event.");
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				showAlert("Error", "An error occurred while deleting the event.");
			}
		}
	}
	
	private void refreshTableView()
	{
		eventTableView.refresh();
	}
	
	private void showAlert(String title, String message)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private String getValueByPropertyName(Event event, String propertyName)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				return event.getTitle();
			case "Description":
				return event.getDescription();
			case "CardImageURL":
				return event.getCardImageUrl();
			case "RedirectURL":
				return event.getRedirectUrl();
			default:
				return "";
		}
	}
	
	private void setPropertyValue(Event event, String propertyName, String newValue)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				event.setTitle(newValue);
				break;
			case "Description":
				event.setDescription(newValue);
				break;
			case "CardImageURL":
				event.setCardImageUrl(newValue);
				break;
			case "RedirectURL":
				event.setRedirectUrl(newValue);
				break;
			default:
				break;
		}
	}
	
	private void handleEditColumn(TableColumn.CellEditEvent<Event, ?> event, String columnName)
	{
		Event ev = event.getRowValue();
		Object oldValueObj = event.getOldValue();
		Object newValueObj = event.getNewValue();
		String oldValue = oldValueObj != null ? oldValueObj.toString() : "";
		String newValue = newValueObj != null ? newValueObj.toString() : "";
		if (!newValue.equals(oldValue))
		{
			Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationDialog.setTitle("Confirm Update");
			confirmationDialog.setHeaderText("Confirm Update for " + columnName);
			confirmationDialog.setContentText("Do you want to update the " + columnName + " from '" + oldValue + "' to '" + newValue + "'?");
			Optional<ButtonType> result = confirmationDialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK)
			{
				updateEventInDatabase(ev);
			}
			else
			{
				eventTableView.refresh();
			}
		}
	}
	
	@FXML
	private void addEvent()
	{
		NewEventDialog dialog = new NewEventDialog(getLastUsedId());
		Optional<Event> result = dialog.showAndWait();
		result.ifPresent(newEvent ->
		{
			addEventToDatabase(newEvent);
			eventTableView.getItems().clear();
			fetchEvents();
		});
	}
	
	private void addEventToDatabase(Event event)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY))
		{
			preparedStatement.setInt(1, event.getId());
			preparedStatement.setString(2, event.getTitle());
			preparedStatement.setString(3, event.getDescription());
			preparedStatement.setString(4, event.getCardImageUrl());
			preparedStatement.setString(5, event.getRedirectUrl());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while adding the event to the database.");
		}
	}
	
	private int getLastUsedId()
	{
		int lastUsedId = 0;
		try (Connection conn = DatabaseUtil.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM events"))
		{
			if (resultSet.next())
			{
				lastUsedId = resultSet.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return lastUsedId;
	}
}
