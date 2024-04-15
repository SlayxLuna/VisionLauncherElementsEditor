package vision.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import vision.controllers.model.changelog.Changelog;
import vision.controllers.model.changelog.NewChangeLogDialog;
import vision.utils.DatabaseUtil;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionChangelogController implements Initializable
{
	@FXML
	private TableView<Changelog>				changelogTableView;
	@FXML
	private TableColumn<Changelog, Integer>		idColumn;
	@FXML
	private TableColumn<Changelog, String>		titleColumn;
	@FXML
	private TableColumn<Changelog, String>		descriptionColumn;
	@FXML
	private TableColumn<Changelog, Timestamp>	dateColumn;
	@FXML
	private TableColumn<Changelog, String>		serverColumn;
	@FXML
	private VBox								changelogVBox;
	private boolean								deleteColumnAdded	= false;
	private static final String					SELECT_QUERY		= "SELECT * FROM changelogs";
	private static final String					DELETE_QUERY		= "DELETE FROM changelogs WHERE id = ?";
	private static final String					UPDATE_QUERY		= "UPDATE changelogs SET title = ?, description = ?, date = ?, server = ? WHERE id = ?";
	private static final String					INSERT_QUERY		= "INSERT INTO changelogs (id, title, description, date, server) VALUES (?, ?, ?, ?, ?)";
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		configureTableColumns();
		fetchChangelogs();
		setupTableEditHandlers();
		changelogTableView.setRowFactory(tv ->
		{
			TableRow<Changelog> row = new TableRow<>();
			row.setMinHeight(30);
			row.setMaxHeight(30);
			row.setOnMouseClicked(event ->
			{
				if (event.getClickCount() == 2 && !row.isEmpty())
				{
					Changelog rowData = row.getItem();
					TableColumn<Changelog, ?> clickedColumn = getColumnByPosition(changelogTableView, event.getX());
					if (clickedColumn != null)
					{
						openEditWindowForColumn(rowData, clickedColumn);
					}
				}
			});
			return row;
		});
	}
	
	private TableColumn<Changelog, ?> getColumnByPosition(TableView<Changelog> tableView, double x)
	{
		double totalWidth = 0.0;
		for (TableColumn<Changelog, ?> column : tableView.getColumns())
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
		dateColumn.setCellValueFactory(cellData ->
		{
			Changelog changelog = cellData.getValue();
			String dateStr = changelog.getDate().toString();
			ObservableValue<Timestamp> observableDate = Bindings.createObjectBinding(() -> Timestamp.valueOf(dateStr));
			return observableDate;
		});
		serverColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServer()));
		addDeleteColumnIfNeeded();
	}
	
	private void addDeleteColumnIfNeeded()
	{
		if (!deleteColumnAdded)
		{
			TableColumn<Changelog, Void> deleteColumn = new TableColumn<>("Delete");
			deleteColumn.setCellFactory(param -> new TableCell<Changelog, Void>()
			{
				private final Button deleteButton = new Button("Delete");
				{
					deleteButton.setOnAction(event ->
					{
						Changelog changelog = getTableView().getItems().get(getIndex());
						deleteChangelog(changelog);
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
			changelogTableView.getColumns().add(deleteColumn);
			deleteColumnAdded = true;
		}
	}
	
	private void fetchChangelogs()
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY); ResultSet resultSet = preparedStatement.executeQuery())
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				String description = resultSet.getString("description");
				Timestamp date = resultSet.getTimestamp("date");
				String server = resultSet.getString("server");
				Changelog changelog = new Changelog(id, title, description, date, server);
				changelogTableView.getItems().add(changelog);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void setupTableEditHandlers()
	{
		Map<TableColumn<Changelog, ?>, String> columnMap = new HashMap<>();
		columnMap.put(titleColumn, "Title");
		columnMap.put(descriptionColumn, "Description");
		columnMap.put(dateColumn, "Date");
		columnMap.put(serverColumn, "Server");
		TableColumn<Changelog, String> dateColumn = new TableColumn<>("Date");
		columnMap.forEach((column, columnName) ->
		{
			column.setOnEditCommit(event -> handleEditColumn(event, columnName));
		});
	}
	
	private void openEditWindowForColumn(Changelog changelog, TableColumn<Changelog, ?> column)
	{
		String propertyName = column.getText();
		String currentValue = getValueByPropertyName(changelog, propertyName);
		TextArea textArea = new TextArea(currentValue);
		textArea.setPrefSize(600, 400);
		textArea.setPrefRowCount(10);
		textArea.setPrefColumnCount(40);
		textArea.setWrapText(true);
		textArea.setEditable(true);
		Label descriptionLabel = new Label("Articles and Changelogs description column can contain only the following HTML tags:\n" + "!Always wrap your text inside a separate tag or it won't be read!!\n" + "<p>Your Text</p>\n" + "<b>Bolded text</b>\n" + "<h1>Heading Text or use h2, h3, h4, etc</h1>\n" + "<a href=\"http://example.com\">Your hyperlink</a>\n" + "<span style=\"color:gold;\">NCSOFT</span>");
		VBox vBox = new VBox(10);
		vBox.getChildren().addAll(textArea, descriptionLabel);
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
			setPropertyValue(changelog, propertyName, newValue);
			updateChangelogInDatabase(changelog);
			refreshTableView();
		});
	}
	
	private void updateChangelogInDatabase(Changelog changelog)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_QUERY))
		{
			preparedStatement.setString(1, changelog.getTitle());
			preparedStatement.setString(2, changelog.getDescription());
			preparedStatement.setString(3, changelog.getServer());
			preparedStatement.setInt(4, changelog.getId());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0)
			{
				showAlert("Changelog Updated", "The changelog has been successfully updated.");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while updating the changelog.");
		}
	}
	
	@FXML
	private void addChangelog()
	{
		NewChangeLogDialog dialog = new NewChangeLogDialog(getLastUsedId());
		Optional<Changelog> result = dialog.showAndWait();
		result.ifPresent(newArticle ->
		{
			addChangelogToDatabase(newArticle);
			changelogTableView.getItems().clear();
			fetchChangelogs();
		});
	}
	
	private void addChangelogToDatabase(Changelog changelog)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY))
		{
			preparedStatement.setString(1, changelog.getTitle());
			preparedStatement.setString(2, changelog.getDescription());
			preparedStatement.setTimestamp(3, changelog.getDate());
			preparedStatement.setString(4, changelog.getServer());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while adding the changelog to the database.");
		}
	}
	
	private int getLastUsedId()
	{
		int lastUsedId = 0;
		try (Connection conn = DatabaseUtil.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM changelogs"))
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
	
	private void refreshTableView()
	{
		changelogTableView.refresh();
	}
	
	private void showAlert(String title, String message)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private String getValueByPropertyName(Changelog changelog, String propertyName)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				return changelog.getTitle();
			case "Description":
				return changelog.getDescription();
			case "Date":
				Timestamp date = changelog.getDate();
				return date != null ? date.toString() : "";
			case "Server":
				return changelog.getServer();
			default:
				return "";
		}
	}
	
	private void setPropertyValue(Changelog changelog, String propertyName, String newValue)
	{
		switch (propertyName)
		{
			case "Title":
				changelog.setTitle(newValue);
				break;
			case "Description":
				changelog.setDescription(newValue);
				break;
			case "Date":
				try
				{
					Timestamp timestamp = Timestamp.valueOf(newValue);
					changelog.setDate(timestamp);
				}
				catch (IllegalArgumentException e)
				{
					System.err.println("Invalid date format: " + newValue);
				}
				break;
			case "Server":
				changelog.setServer(newValue);
				break;
			default:
				break;
		}
	}
	
	private void handleEditColumn(TableColumn.CellEditEvent<Changelog, ?> event, String columnName)
	{
		Changelog changelog = event.getRowValue();
		String oldValue = event.getOldValue().toString();
		String newValue = event.getNewValue().toString();
		if (!newValue.equals(oldValue))
		{
			Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationDialog.setTitle("Confirm Update");
			confirmationDialog.setHeaderText("Confirm Update for " + columnName);
			confirmationDialog.setContentText("Do you want to update the " + columnName + " from '" + oldValue + "' to '" + newValue + "'?");
			Optional<ButtonType> result = confirmationDialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK)
			{
				if ("Date".equals(columnName))
				{
					try
					{
						LocalDateTime newDateTime = LocalDateTime.parse(newValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
						changelog.setDate(Timestamp.valueOf(newDateTime));
					}
					catch (DateTimeParseException e)
					{
						showAlert("Error", "Invalid date format. Please enter date in the format 'yyyy-MM-dd HH:mm:ss'");
						event.getTableView().refresh();
					}
				}
				else
				{
					updateChangelogInDatabase(changelog);
				}
			}
			else
			{
				event.getTableView().refresh();
			}
		}
	}
	
	private void deleteChangelog(Changelog changelog)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Deletion");
		alert.setHeaderText("Are you sure you want to delete this changelog?");
		alert.setContentText("Changelog ID: " + changelog.getId() + "\nTitle: " + changelog.getTitle());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(DELETE_QUERY))
			{
				preparedStatement.setInt(1, changelog.getId());
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0)
				{
					changelogTableView.getItems().remove(changelog);
				}
				else
				{
					showAlert("Error", "Failed to delete article.");
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				showAlert("Error", "An error occurred while deleting the article.");
			}
		}
	}
}
