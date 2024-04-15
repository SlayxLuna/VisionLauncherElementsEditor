package vision.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import vision.controllers.model.articles.Article;
import vision.controllers.model.articles.NewArticleDialog;
import vision.utils.DatabaseUtil;

/**
 * @author Brado
 *         Made for Cybermist's Launcher
 */
public class VisionArticleController implements Initializable
{
	@FXML
	private TableView<Article>				articleTableView;
	@FXML
	private TableColumn<Article, Integer>	idColumn;
	@FXML
	private TableColumn<Article, String>	titleColumn;
	@FXML
	private TableColumn<Article, String>	descriptionColumn;
	@FXML
	private TableColumn<Article, String>	tagColumn;
	@FXML
	private TableColumn<Article, String>	tagHexColorColumn;
	@FXML
	private TableColumn<Article, String>	cardImageUrlColumn;
	@FXML
	private VBox							articleVBox;
	private static final String				SELECT_QUERY	= "SELECT id, title, description, tag, tag_hex_color, card_image_url FROM articles";
	private static final String				DELETE_QUERY	= "DELETE FROM articles WHERE id = ?";
	private static final String				UPDATE_QUERY	= "UPDATE articles SET title = ?, description = ?, tag = ?, tag_hex_color = ?, card_image_url = ? WHERE id = ?";
	private static final String				INSERT_QUERY	= "INSERT INTO articles (id, title, description, tag, tag_hex_color, card_image_url) VALUES (?, ?, ?, ?, ?, ?)";
	private boolean							INITIALIZED		= false;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		if (!INITIALIZED)
		{
			configureTableColumns();
			fetchArticles();
			setupTableEditHandlers();
			articleTableView.setRowFactory(tv ->
			{
				TableRow<Article> row = new TableRow<>();
				row.setMinHeight(30);
				row.setMaxHeight(30);
				row.setOnMouseClicked(event ->
				{
					if (event.getClickCount() == 2 && !row.isEmpty())
					{
						Article rowData = row.getItem();
						TableColumn<Article, ?> clickedColumn = getColumnByPosition(articleTableView, event.getX());
						if (clickedColumn != null)
						{
							openEditWindowForColumn(rowData, clickedColumn);
						}
					}
				});
				return row;
			});
			INITIALIZED = true;
		}
	}
	
	private TableColumn<Article, ?> getColumnByPosition(TableView<Article> tableView, double x)
	{
		double totalWidth = 0.0;
		for (TableColumn<Article, ?> column : tableView.getColumns())
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
		tagColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));
		tagHexColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTagHexColor()));
		cardImageUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCardImageUrl()));
		TableColumn<Article, Void> deleteColumn = new TableColumn<>("Delete");
		deleteColumn.setCellFactory(param -> new TableCell<Article, Void>()
		{
			private final Button deleteButton = new Button("Delete");
			{
				deleteButton.setOnAction(event ->
				{
					Article article = getTableView().getItems().get(getIndex());
					deleteArticle(article);
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
		articleTableView.getColumns().add(deleteColumn);
	}
	
	private void fetchArticles()
	{
		articleTableView.getItems().clear();
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY); ResultSet resultSet = preparedStatement.executeQuery())
		{
			while (resultSet.next())
			{
				int id = resultSet.getInt("id");
				String title = resultSet.getString("title");
				String description = resultSet.getString("description");
				String tag = resultSet.getString("tag");
				String tagHexColor = resultSet.getString("tag_hex_color");
				String cardImageUrl = resultSet.getString("card_image_url");
				Article article = new Article(id, title, description, tag, tagHexColor, cardImageUrl);
				articleTableView.getItems().add(article);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void setupTableEditHandlers()
	{
		titleColumn.setOnEditCommit(event -> handleEditColumn(event, "Title"));
		descriptionColumn.setOnEditCommit(event -> handleEditColumn(event, "Description"));
		tagColumn.setOnEditCommit(event -> handleEditColumn(event, "Tag"));
		tagHexColorColumn.setOnEditCommit(event -> handleEditColumn(event, "Tag Hex Color"));
		cardImageUrlColumn.setOnEditCommit(event -> handleEditColumn(event, "Card Image URL"));
	}
	
	private void openEditWindowForColumn(Article article, TableColumn<Article, ?> column)
	{
		String propertyName = column.getText();
		String currentValue = getValueByPropertyName(article, propertyName);
		final ColorPicker[] colorPicker = new ColorPicker[1];
		Control control;
		if (propertyName.equals("Tag Hex Color"))
		{
			colorPicker[0] = new ColorPicker(Color.web(currentValue));
			control = colorPicker[0];
		}
		else
		{
			TextArea textArea = new TextArea(currentValue);
			textArea.setPrefSize(600, 400);
			textArea.setPrefRowCount(10);
			textArea.setPrefColumnCount(40);
			textArea.setWrapText(true);
			textArea.setEditable(true);
			control = textArea;
		}
		Label descriptionLabel = new Label("Articles and Changelogs description column can contain only the following HTML tags:\n" + "!Always wrap your text inside a separate tag or it won't be read!!\n" + "<p>Your Text</p>\n" + "<b>Bolded text</b>\n" + "<h1>Heading Text or use h2, h3, h4, etc</h1>\n" + "<a href=\"http://example.com\">Your hyperlink</a>\n" + "<span style=\"color:gold;\">NCSOFT</span>");
		VBox vBox = new VBox(10);
		vBox.getChildren().addAll(control, descriptionLabel);
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
				if (propertyName.equals("Tag Hex Color") && colorPicker[0] != null)
				{
					String color = colorPicker[0].getValue().toString().substring(2, 8).toUpperCase();
					return color;
				}
				else if (control instanceof TextArea)
				{
					return ((TextArea) control).getText();
				}
			}
			return null;
		});
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(newValue ->
		{
			setPropertyValue(article, propertyName, newValue);
			updateArticleInDatabase(article);
			refreshTableView();
		});
	}
	
	private void updateArticleInDatabase(Article article)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_QUERY))
		{
			preparedStatement.setString(1, article.getTitle());
			preparedStatement.setString(2, article.getDescription());
			preparedStatement.setString(3, article.getTag());
			preparedStatement.setString(4, article.getTagHexColor());
			preparedStatement.setString(5, article.getCardImageUrl());
			preparedStatement.setInt(6, article.getId());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0)
			{
				showAlert("Article Updated", "The article has been successfully updated.");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while updating the article.");
		}
	}
	
	@FXML
	private void addArticle()
	{
		NewArticleDialog dialog = new NewArticleDialog(getLastUsedId());
		Optional<Article> result = dialog.showAndWait();
		result.ifPresent(newArticle ->
		{
			addArticleToDatabase(newArticle);
			articleTableView.getItems().clear();
			fetchArticles();
		});
	}
	
	private void addArticleToDatabase(Article article)
	{
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY))
		{
			preparedStatement.setInt(1, article.getId());
			preparedStatement.setString(2, article.getTitle());
			preparedStatement.setString(3, article.getDescription());
			preparedStatement.setString(4, article.getTag());
			preparedStatement.setString(5, article.getTagHexColor());
			preparedStatement.setString(6, article.getCardImageUrl());
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			showAlert("Error", "An error occurred while adding the article to the database.");
		}
	}
	
	private int getLastUsedId()
	{
		int lastUsedId = 0;
		try (Connection conn = DatabaseUtil.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM articles"))
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
		articleTableView.refresh();
	}
	
	private void showAlert(String title, String message)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private String getValueByPropertyName(Article article, String propertyName)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				return article.getTitle();
			case "Description":
				return article.getDescription();
			case "Tag":
				return article.getTag();
			case "TagHexColor":
				return article.getTagHexColor();
			case "CardImageURL":
				return article.getCardImageUrl();
			default:
				return "";
		}
	}
	
	private void setPropertyValue(Article article, String propertyName, String newValue)
	{
		propertyName = propertyName.replace(" ", "");
		switch (propertyName)
		{
			case "Title":
				article.setTitle(newValue);
				break;
			case "Description":
				article.setDescription(newValue);
				break;
			case "Tag":
				article.setTag(newValue);
				break;
			case "TagHexColor":
				article.setTagHexColor(newValue);
				break;
			case "CardImageURL":
				article.setCardImageUrl(newValue);
				break;
			default:
				break;
		}
	}
	
	private void handleEditColumn(TableColumn.CellEditEvent<Article, String> event, String columnName)
	{
		Article article = event.getRowValue();
		String oldValue = event.getOldValue();
		String newValue = event.getNewValue();
		if (!newValue.equals(oldValue))
		{
			Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationDialog.setTitle("Confirm Update");
			confirmationDialog.setHeaderText("Confirm Update for " + columnName);
			confirmationDialog.setContentText("Do you want to update the " + columnName + " from '" + oldValue + "' to '" + newValue + "'?");
			Optional<ButtonType> result = confirmationDialog.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK)
			{
				updateArticleInDatabase(article);
			}
			else
			{
				event.getTableView().refresh();
			}
		}
	}
	
	private void deleteArticle(Article article)
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm Deletion");
		alert.setHeaderText("Are you sure you want to delete this article?");
		alert.setContentText("Article ID: " + article.getId() + "\nTitle: " + article.getTitle());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(DELETE_QUERY))
			{
				preparedStatement.setInt(1, article.getId());
				int rowsAffected = preparedStatement.executeUpdate();
				if (rowsAffected > 0)
				{
					articleTableView.getItems().remove(article);
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
