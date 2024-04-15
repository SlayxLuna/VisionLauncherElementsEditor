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
import vision.controllers.model.homeSlider.HomeSlider;
import vision.controllers.model.homeSlider.NewHomeSliderDialog;
import vision.utils.DatabaseUtil;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionHomeSliderController
{
	@FXML
	private TableView<HomeSlider>				homeSliderTableView;
	@FXML
	private TableColumn<HomeSlider, Integer>	idColumn;
	@FXML
	private TableColumn<HomeSlider, String>		titleColumn;
	@FXML
	private TableColumn<HomeSlider, String>		titleIconUrlColumn;
	@FXML
	private TableColumn<HomeSlider, String>		descriptionColumn;
	@FXML
	private TableColumn<HomeSlider, String>		backgroundBannerUrlColumn;
	@FXML
	private TableColumn<HomeSlider, String>		clickUrlColumn;
	private boolean								deleteColumnAdded		= false;
	private static final String					SELECT_QUERY			= "SELECT * FROM home_slider";
	private static final String					DELETE_QUERY			= "DELETE FROM home_slider WHERE id = ?";
	private static final String					INSERT_QUERY			= "INSERT INTO home_slider (id, title, title_icon_url, description, background_banner_url, click_url) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String					UPDATE_HOMESLIDER_QUERY	= "UPDATE home_slider SET title = ?, title_icon_url = ?, description = ?, background_banner_url = ?, click_url = ? WHERE id = ?";
	
	public void initialize()
	{
		configureTableColumns();
		fetchHomeSliders();
		homeSliderTableView.setRowFactory(tv ->
		{
			TableRow<HomeSlider> row = new TableRow<>();
			row.setMinHeight(30);
			row.setMaxHeight(30);
			row.setOnMouseClicked(event ->
			{
				if (event.getClickCount() == 2 && !row.isEmpty())
				{
					HomeSlider rowData = row.getItem();
					TableColumn<HomeSlider, ?> clickedColumn = getColumnByPosition(homeSliderTableView, event.getX());
					if (clickedColumn != null)
					{
						openEditWindowForColumn(rowData, clickedColumn);
					}
				}
			});
			return row;
		});
	}
	
	private TableColumn<HomeSlider, ?> getColumnByPosition(TableView<HomeSlider> tableView, double x)
	{
		double totalWidth = 0.0;
		for (TableColumn<HomeSlider, ?> column : tableView.getColumns())
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
	
	private void openEditWindowForColumn(HomeSlider homeSlider, TableColumn<HomeSlider, ?> column)
	{
		String propertyName = column.getText();
		String currentValue = getValueByPropertyName(homeSlider, propertyName);
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
			setPropertyValue(homeSlider, propertyName, newValue);
			updateHomeSlider(homeSlider);
			refreshTableView();
		});
	}
	
	private String getValueByPropertyName(HomeSlider homeSlider, String propertyName)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				return homeSlider.getTitle();
			case "TitleIconURL":
				return homeSlider.getTitleIconUrl();
			case "Description":
				return homeSlider.getDescription();
			case "BackgroundBannerURL":
				return homeSlider.getBackgroundBannerUrl();
			case "ClickURL":
				return homeSlider.getClickUrl();
			default:
				return "";
		}
	}
	
	private void setPropertyValue(HomeSlider homeSlider, String propertyName, String newValue)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				homeSlider.setTitle(newValue);
				break;
			case "TitleIconURL":
				homeSlider.setTitleIconUrl(newValue);
				break;
			case "Description":
				homeSlider.setDescription(newValue);
				break;
			case "BackgroundBannerURL":
				homeSlider.setBackgroundBannerUrl(newValue);
				break;
			case "ClickURL":
				homeSlider.setClickUrl(newValue);
				break;
			default:
				break;
		}
	}
	
	private void configureTableColumns()
	{
		idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		titleIconUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitleIconUrl()));
		descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
		backgroundBannerUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBackgroundBannerUrl()));
		clickUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClickUrl()));
		addDeleteColumnIfNeeded();
	}
	
	private void addDeleteColumnIfNeeded()
	{
		if (!deleteColumnAdded)
		{
			TableColumn<HomeSlider, Void> deleteColumn = new TableColumn<>("Delete");
			deleteColumn.setCellFactory(param -> new TableCell<HomeSlider, Void>()
			{
				private final Button deleteButton = new Button("Delete");
				{
					deleteButton.setOnAction(event ->
					{
						HomeSlider ev = getTableView().getItems().get(getIndex());
						deleteHomeSlider(ev);
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
			homeSliderTableView.getColumns().add(deleteColumn);
			deleteColumnAdded = true;
		}
	}
	
	private void fetchHomeSliders()
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY); ResultSet resultSet = preparedStatement.executeQuery())
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				String titleIconUrl = resultSet.getString("title_icon_url");
				String description = resultSet.getString("description");
				String backgroundBannerUrl = resultSet.getString("background_banner_url");
				String clickUrl = resultSet.getString("click_url");
				HomeSlider homeSlider = new HomeSlider(id, title, titleIconUrl, description, backgroundBannerUrl, clickUrl);
				homeSliderTableView.getItems().add(homeSlider);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	private void addHomeSlider()
	{
		NewHomeSliderDialog dialog = new NewHomeSliderDialog(getLastUsedId());
		Optional<HomeSlider> result = dialog.showAndWait();
		result.ifPresent(newHomeSlider ->
		{
			addHomeSliderToDatabase(newHomeSlider);
			homeSliderTableView.getItems().clear();
			fetchHomeSliders();
		});
	}
	
	private void addHomeSliderToDatabase(HomeSlider homeSlider)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY))
		{
			preparedStatement.setInt(1, homeSlider.getId());
			preparedStatement.setString(2, homeSlider.getTitle());
			preparedStatement.setString(3, homeSlider.getTitleIconUrl());
			preparedStatement.setString(4, homeSlider.getDescription());
			preparedStatement.setString(5, homeSlider.getBackgroundBannerUrl());
			preparedStatement.setString(6, homeSlider.getClickUrl());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while adding the home slider to the database.");
		}
	}
	
	private int getLastUsedId()
	{
		int lastUsedId = 0;
		try (Connection conn = DatabaseUtil.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM home_slider"))
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
	
	private void deleteHomeSlider(HomeSlider homeSlider)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Deletion");
		alert.setHeaderText("Are you sure you want to delete this home slider?");
		alert.setContentText("Home Slider ID: " + homeSlider.getId() + "\nTitle: " + homeSlider.getTitle());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(DELETE_QUERY))
			{
				preparedStatement.setInt(1, homeSlider.getId());
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0)
				{
					homeSliderTableView.getItems().remove(homeSlider);
				}
				else
				{
					showAlert("Error", "Failed to delete home slider.");
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				showAlert("Error", "An error occurred while deleting the home slider.");
			}
		}
	}
	
	private void updateHomeSlider(HomeSlider homeSlider)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_HOMESLIDER_QUERY))
		{
			preparedStatement.setString(1, homeSlider.getTitle());
			preparedStatement.setString(2, homeSlider.getTitleIconUrl());
			preparedStatement.setString(3, homeSlider.getDescription());
			preparedStatement.setString(4, homeSlider.getBackgroundBannerUrl());
			preparedStatement.setString(5, homeSlider.getClickUrl());
			preparedStatement.setInt(6, homeSlider.getId());
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
		homeSliderTableView.refresh();
	}
}
