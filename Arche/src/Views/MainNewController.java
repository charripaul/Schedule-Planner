package Views;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeTableView;

import Models.ModelControl;
import Models.Task;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainNewController implements Initializable{
	@FXML private JFXButton homeButton;
	@FXML private JFXButton calendarButton;
	@FXML private JFXButton taskButton;
	@FXML private JFXButton classButton;
	@FXML private Label title;
	@FXML private Label urgentLabel;
	@FXML private Label approachingLabel;
	@FXML private Label overdueLabel;
	
	//home
	@FXML private BorderPane homeView;
	@FXML private JFXButton leftArrowButton;
	@FXML private JFXButton rightArrowButton;
	@FXML private Label dailyTaskDateLabel;
	
	@FXML private JFXTreeTableView homeTreeTable;
	@FXML private TreeTableColumn<Task, String> nameTreeTableCol;
	@FXML private TreeTableColumn<Task, String> descTreeTableCol;
	@FXML private TreeTableColumn<Task, String> typeTreeTableCol;
	@FXML private TreeTableColumn<Task, String> classTreeTableCol;
	@FXML private TreeTableColumn<Task, LocalDateTime> dueDateTreeTableCol;
	@FXML private TreeTableColumn<Task, Boolean> completedTreeTableCol;
	
	//calendar
	@FXML private BorderPane calendarView;
	
	//task
	@FXML private BorderPane taskView;
	@FXML private TableView<Task> tableView;
	@FXML private TableColumn<Task, String> nameColumn;
	@FXML private TableColumn<Task, String> descriptionColumn;
	@FXML private TableColumn<Task, String> typeColumn;
	@FXML private TableColumn<Task, String> classColumn;
	@FXML private TableColumn<Task, LocalDateTime> dueDateColumn;
	@FXML private TableColumn<Task, Boolean> completedColumn;
	@FXML private Button addButton;
	@FXML private Button viewButton;
	
	//classes
	@FXML private BorderPane classView;
	
	Task temp;
	Date lastClickTime;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeLabels();
		homeButton.fire();
	}
	private void initializeLabels() {
		ArrayList<Task> tableTasks = ModelControl.getDayTasks("get for todays labels");
		ArrayList<Task> overDueTasks = ModelControl.getOverdueTasks("get for todays labels");
		ArrayList<Task> dueSoonTasks = ModelControl.getSoonDueTasks("get for todays labels");
		
		urgentLabel.setText(tableTasks.size() + "");
		overdueLabel.setText(overDueTasks.size() + "");
		approachingLabel.setText(dueSoonTasks.size() + "");
	}
	private void initializeHome() {
		//date label formatting
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		Date date = Date.from(ModelControl.dayOfReference.atStartOfDay(ZoneId.systemDefault()).toInstant());
		dailyTaskDateLabel.setText(sdf.format(date));
		
		nameTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getName())
	        );
		descTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getDescription())
	        );
		typeTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getType())
	        );
		classTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getClassAbr())
	        );
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm");
		dueDateTreeTableCol.setCellValueFactory(T -> T.getValue().getValue().getDueDate(""));
		dueDateTreeTableCol.setCellFactory(dueDateColumn -> new TreeTableCell<Task, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});
		completedTreeTableCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getValue().getFinishFlag()));
		//completedTreeTableCol.setCellFactory(c -> new CheckBoxTableCell<>());
		
		//column autosizing
		nameTreeTableCol.prefWidthProperty().bind(homeTreeTable.widthProperty().multiply(.107));
		descTreeTableCol.prefWidthProperty().bind(homeTreeTable.widthProperty().multiply(.416));
		typeTreeTableCol.prefWidthProperty().bind(homeTreeTable.widthProperty().multiply(.111));
		classTreeTableCol.prefWidthProperty().bind(homeTreeTable.widthProperty().multiply(.111));
		dueDateTreeTableCol.prefWidthProperty().bind(homeTreeTable.widthProperty().multiply(.14));
		completedTreeTableCol.prefWidthProperty().bind(homeTreeTable.widthProperty().multiply(.107));
		
		//text alignment
		nameTreeTableCol.setStyle("-fx-alignment: CENTER;");
		descTreeTableCol.setStyle("-fx-alignment: CENTER;");
		typeTreeTableCol.setStyle("-fx-alignment: CENTER;");
		classTreeTableCol.setStyle("-fx-alignment: CENTER;");
		dueDateTreeTableCol.setStyle("-fx-alignment: CENTER;");
		completedTreeTableCol.setStyle("-fx-alignment: CENTER;");
		
		initializeHomeData();
		
		homeView.setVisible(true);
	}

	private void initializeCalendar() {
		calendarView.setVisible(true);
	}
	private void initializeTasks() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
		classColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("classAbr"));
		
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm");
		dueDateColumn.setCellValueFactory(Task -> Task.getValue().getDueDate(""));
		dueDateColumn.setCellFactory(dueDateColumn -> new TableCell<Task, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});
		completedColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getFinishFlag()));
		completedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
		
		//column autosizing
		nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.107));
		descriptionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.416));
		typeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.111));
		classColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.111));
		dueDateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.13));
		completedColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.107));
		
		//text alignment
		nameColumn.setStyle("-fx-alignment: CENTER;");
		typeColumn.setStyle("-fx-alignment: CENTER;");
		classColumn.setStyle("-fx-alignment: CENTER;");
		dueDateColumn.setStyle("-fx-alignment: CENTER;");
		
		initializeTasksData();
		
		taskView.setVisible(true);
	}
	private void initializeClass() {
		classView.setVisible(true);
	}
	private void initializeHomeData() {
		//dailyTreeTableView.getRoot().getChildren().clear();
		ArrayList<Task> tableTasks = ModelControl.getDayTasks();
		ArrayList<Task> overDueTasks = ModelControl.getOverdueTasks();
		ArrayList<Task> dueSoonTasks = ModelControl.getSoonDueTasks();
		
		homeTreeTable.setShowRoot(false);
		
		TreeItem<Task> root = new TreeItem<>();
		TreeItem<Task> overdue = new TreeItem<>(new Task("Overdue"));
		TreeItem<Task> due = new TreeItem<>(new Task("Immediate"));
		TreeItem<Task> soon = new TreeItem<>(new Task("Approaching"));
		
		for(int count = 0;count<tableTasks.size();count++) {
			TreeItem<Task> val = new TreeItem<>(tableTasks.get(count));
			due.getChildren().add(val);
		}
		for(int count = 0;count<overDueTasks.size();count++) {
			TreeItem<Task> val = new TreeItem<>(overDueTasks.get(count));
			overdue.getChildren().add(val);
		}
		for(int count = 0;count<dueSoonTasks.size();count++) {
			TreeItem<Task> val = new TreeItem<>(dueSoonTasks.get(count));
			soon.getChildren().add(val);
		}
		
		root.getChildren().add(due);
		root.getChildren().add(overdue);
		root.getChildren().add(soon);
		homeTreeTable.setRoot(root);		
	}
	private void initializeCalendarData() {
		//TODO: write
	}
	private void initializeTasksData() {
		tableView.getItems().clear();
		tableView.setItems(ModelControl.getTasks(""));
		tableView.getSortOrder().add(dueDateColumn);
	}
	//task list tab
	@FXML
	private void addButtonClicked() {
		int size = tableView.getItems().size();
		int sIndex = tableView.getSelectionModel().getSelectedIndex();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("newTask.fxml"));
			Scene addWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setScene(addWindow);
			stage.showAndWait();
			initializeTasks();
			//select correct row after closing add window
			if(size == tableView.getItems().size()) {
				tableView.getSelectionModel().select(sIndex);
			}
			else {
				ArrayList<Task> tasks = ModelControl.getTasks();
				for(int count = 0;count<tableView.getItems().size();count++) {
					if(tasks.get(tasks.size()-1).getId() == tableView.getItems().get(count).getId()) {
						tableView.getSelectionModel().select(count);
					}
				}
			}
		}catch(IOException e) {
			System.out.println("\nError code: Pouch\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	private void initializeClassData() {
		//TODO: write
	}
	//task list tab
	@FXML
	private void viewButtonClicked() {
		//highlighted row in table
		Task selected = tableView.getSelectionModel().selectedItemProperty().get();
		int sIndex = tableView.getSelectionModel().getSelectedIndex();
		if(selected != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("viewTask.fxml"));
				
				//pass in values from selected row
				ViewTaskController controller = new ViewTaskController(selected);
				
				loader.setController(controller);
				Scene viewWindow = new Scene(loader.load());
				
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UTILITY);
				stage.setScene(viewWindow);
				stage.showAndWait();
				initializeTasks();
				tableView.getSelectionModel().select(sIndex);
			}catch(IOException e) {
				System.out.println("\nError code: Satchel\n" + e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			System.out.println("\nError Code: Row not selected");
		}
	}
	//open view window on double click
	@FXML
	private void handleClick() {
	    Task row = tableView.getSelectionModel().getSelectedItem();
	    if(row == null) return;
	    if(row != temp){
	        temp = row;
	        lastClickTime = new Date();
	    } else if(row == temp) {
	        Date now = new Date();
	        long diff = now.getTime() - lastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	             viewButtonClicked();
	        } else {
	            lastClickTime = new Date();
	        }
	    }
	}

	@FXML
	private void homeButtonClicked() {
		resetButtons();
		homeButton.setStyle("-fx-background-color:  #f2782f;");
		resetViews();
		initializeHome();
	}
	@FXML
	private void calendarButtonClicked() {
		resetButtons();
		calendarButton.setStyle("-fx-background-color:  #f2782f;");
		resetViews();
		initializeCalendar();
	}
	@FXML
	private void taskButtonClicked() {
		resetButtons();
		taskButton.setStyle("-fx-background-color:  #f2782f;");
		resetViews();
		initializeTasks();
	}
	@FXML
	private void classButtonClicked() {
		resetButtons();
		classButton.setStyle("-fx-background-color:  #f2782f;");
		resetViews();
		initializeClass();
	}
	@FXML
	private void leftArrowClicked() {
		ModelControl.removeDayFromDayOfReference();
		initializeHome();
	}
	@FXML
	private void rightArrowClicked() {
		ModelControl.addDayToDayOfReference();
		initializeHome();
	}
	private void resetViews() {
		homeView.setVisible(false);
		calendarView.setVisible(false);
		taskView.setVisible(false);
		classView.setVisible(false);
	}
	private void resetButtons() {
		homeButton.setStyle(null);
		calendarButton.setStyle(null);
		taskButton.setStyle(null);
		classButton.setStyle(null);
	}
}
