package vision.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
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
import vision.controllers.model.faq.FAQ;
import vision.controllers.model.faq.NewFAQDialog;
import vision.utils.DatabaseUtil;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionFAQController
{
	@FXML
	private TableView<FAQ>				faqTableView;
	@FXML
	private TableColumn<FAQ, Integer>	idColumn;
	@FXML
	private TableColumn<FAQ, String>	titleColumn;
	@FXML
	private TableColumn<FAQ, String>	descriptionColumn;
	private boolean						deleteColumnAdded	= false;
	private static final String			SELECT_QUERY		= "SELECT * FROM faq";
	private static final String			DELETE_QUERY		= "DELETE FROM faq WHERE id = ?";
	private static final String			INSERT_QUERY		= "INSERT INTO faq (id, title, description) VALUES (?, ?, ?)";
	private static final String			UPDATE_FAQ_QUERY	= "UPDATE faq SET title = ?, description = ? WHERE id = ?";
	
	public void initialize()
	{
		configureTableColumns();
		fetchFAQs();
		faqTableView.setRowFactory(tv ->
		{
			TableRow<FAQ> row = new TableRow<>();
			row.setMinHeight(30);
			row.setMaxHeight(30);
			row.setOnMouseClicked(event ->
			{
				if (event.getClickCount() == 2 && !row.isEmpty())
				{
					FAQ rowData = row.getItem();
					TableColumn<FAQ, ?> clickedColumn = getColumnByPosition(faqTableView, event.getX());
					if (clickedColumn != null)
					{
						openEditWindowForColumn(rowData, clickedColumn);
					}
				}
			});
			return row;
		});
	}
	
	private TableColumn<FAQ, ?> getColumnByPosition(TableView<FAQ> tableView, double x)
	{
		double totalWidth = 0.0;
		for (TableColumn<FAQ, ?> column : tableView.getColumns())
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
	
	private void openEditWindowForColumn(FAQ faq, TableColumn<FAQ, ?> column)
	{
		String propertyName = column.getText();
		String currentValue = getValueByPropertyName(faq, propertyName);
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
			setPropertyValue(faq, propertyName, newValue);
			updateFAQ(faq);
			refreshTableView();
		});
	}
	
	private String getValueByPropertyName(FAQ faq, String propertyName)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				return faq.getTitle();
			case "Description":
				return faq.getDescription();
			default:
				return "";
		}
	}
	
	private void setPropertyValue(FAQ faq, String propertyName, String newValue)
	{
		switch (propertyName)
		{
			case "Title":
				faq.setTitle(newValue);
				break;
			case "Description":
				faq.setDescription(newValue);
				break;
			default:
				break;
		}
	}
	
	private void configureTableColumns()
	{
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
		addDeleteColumnIfNeeded();
	}
	
	private void addDeleteColumnIfNeeded()
	{
		if (!deleteColumnAdded)
		{
			TableColumn<FAQ, Void> deleteColumn = new TableColumn<>("Delete");
			deleteColumn.setCellFactory(param -> new TableCell<FAQ, Void>()
			{
				private final Button deleteButton = new Button("Delete");
				{
					deleteButton.setOnAction(event ->
					{
						FAQ ev = getTableView().getItems().get(getIndex());
						deleteFAQ(ev);
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
			faqTableView.getColumns().add(deleteColumn);
			deleteColumnAdded = true;
		}
	}
	
	private void fetchFAQs()
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY); ResultSet resultSet = preparedStatement.executeQuery())
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				String description = resultSet.getString("description");
				FAQ faq = new FAQ(id, title, description);
				faqTableView.getItems().add(faq);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	private void addFAQ()
	{
		NewFAQDialog dialog = new NewFAQDialog(getLastUsedId());
		Optional<FAQ> result = dialog.showAndWait();
		result.ifPresent(newFAQ ->
		{
			addFAQToDatabase(newFAQ);
			faqTableView.getItems().clear();
			fetchFAQs();
		});
	}
	
	private void addFAQToDatabase(FAQ faq)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY))
		{
			preparedStatement.setInt(1, faq.getId());
			preparedStatement.setString(2, faq.getTitle());
			preparedStatement.setString(3, faq.getDescription());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while adding the FAQ to the database.");
		}
	}
	
	private int getLastUsedId()
	{
		int lastUsedId = 0;
		try (Connection conn = DatabaseUtil.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM faq"))
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
	
	private void deleteFAQ(FAQ faq)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Deletion");
		alert.setHeaderText("Are you sure you want to delete this FAQ?");
		alert.setContentText("FAQ ID: " + faq.getId() + "\nTitle: " + faq.getTitle());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(DELETE_QUERY))
			{
				preparedStatement.setInt(1, faq.getId());
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0)
				{
					faqTableView.getItems().remove(faq);
				}
				else
				{
					showAlert("Error", "Failed to delete FAQ.");
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				showAlert("Error", "An error occurred while deleting the FAQ.");
			}
		}
	}
	
	private void updateFAQ(FAQ faq)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_FAQ_QUERY))
		{
			preparedStatement.setString(1, faq.getTitle());
			preparedStatement.setString(2, faq.getDescription());
			preparedStatement.setInt(3, faq.getId());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void showAlert(String title, String message)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private void refreshTableView()
	{
		faqTableView.refresh();
	}
}
