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
import Models.TaskType;
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
	
	//home tab
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
	
	//calendar tab
	@FXML private BorderPane calendarView;
	
	//task tab
	@FXML private BorderPane taskView;
	@FXML private TableView<Task> taskTable;
	@FXML private TableColumn<Task, String> taskNameColumn;
	@FXML private TableColumn<Task, String> taskDescriptionColumn;
	@FXML private TableColumn<Task, String> taskTypeColumn;
	@FXML private TableColumn<Task, String> taskClassColumn;
	@FXML private TableColumn<Task, LocalDateTime> taskDueDateColumn;
	@FXML private TableColumn<Task, Boolean> taskCompletedColumn;
	@FXML private JFXButton taskAddButton;
	@FXML private JFXButton taskViewButton;
	
	//classes tab
	//class tab (subset of classes)
	@FXML private BorderPane classView;
	@FXML private JFXButton classAddButton;
	@FXML private JFXButton classViewButton;
	@FXML private TableView<Models.Class> classTable;
	@FXML private TableColumn<Models.Class, String> classNameColumn;
	@FXML private TableColumn<Models.Class, String> classAbrColumn;
	@FXML private TableColumn<Models.Class, String> classDetailsColumn;
	@FXML private TableColumn<Models.Class, String> classDOWColumn;
	@FXML private TableColumn<Models.Class, String> classTimeColumn;
	@FXML private TableColumn<Models.Class, Integer> classTAColumn;
	
	//type tab (subset of classes)
	@FXML private TableView<TaskType> typeTable;
	@FXML private JFXButton typeAddButton;
	@FXML private JFXButton typeViewButton;
	@FXML private TableColumn<TaskType, String> typeNameColumn;
	@FXML private TableColumn<TaskType, String> typeDescColumn;
	@FXML private TableColumn<TaskType, Integer> typeWarnColumn;
	@FXML private TableColumn<TaskType, Integer> typeTTCColumn;
	
	//for measuring time between clicks for double click feature
	Task taskTemp;
	Models.Class classTemp;
	Models.TaskType typeTemp;
	Date taskLastClickTime;
	Date classLastClickTime;
	Date typeLastClickTime;
	
	//button click properties
	String selectedColor = "-fx-background-color:  #f2782f;";
	
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
	}

	private void initializeCalendar() {
	}
	private void initializeTasks() {
		taskNameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
		taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		taskTypeColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("type"));
		taskClassColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("classAbr"));
		
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm");
		taskDueDateColumn.setCellValueFactory(Task -> Task.getValue().getDueDate(""));
		taskDueDateColumn.setCellFactory(taskDueDateColumn -> new TableCell<Task, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(formatter)));
		    }
		});
		taskCompletedColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getFinishFlag()));
		taskCompletedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
		
		//column autosizing
		taskNameColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(.107));
		taskDescriptionColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(.416));
		taskTypeColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(.111));
		taskClassColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(.111));
		taskDueDateColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(.13));
		taskCompletedColumn.prefWidthProperty().bind(taskTable.widthProperty().multiply(.107));
		
		//text alignment
		taskNameColumn.setStyle("-fx-alignment: CENTER;");
		taskTypeColumn.setStyle("-fx-alignment: CENTER;");
		taskClassColumn.setStyle("-fx-alignment: CENTER;");
		taskDueDateColumn.setStyle("-fx-alignment: CENTER;");
		
		initializeTasksData();
	}
	private void initializeClass() {
		//TODO: write
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
		taskTable.getItems().clear();
		taskTable.setItems(ModelControl.getTasks(""));
		taskTable.getSortOrder().add(taskDueDateColumn);
	}
	private void initializeClassData() {
		//TODO: write
	}
	@FXML
	private void taskAddButtonClicked() {
		int size = taskTable.getItems().size();
		int sIndex = taskTable.getSelectionModel().getSelectedIndex();
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
			if(size == taskTable.getItems().size()) {
				taskTable.getSelectionModel().select(sIndex);
			}
			else {
				ArrayList<Task> tasks = ModelControl.getTasks();
				for(int count = 0;count<taskTable.getItems().size();count++) {
					if(tasks.get(tasks.size()-1).getId() == taskTable.getItems().get(count).getId()) {
						taskTable.getSelectionModel().select(count);
					}
				}
			}
		}catch(IOException e) {
			System.out.println("\nError code: Pouch\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	@FXML
	private void taskViewButtonClicked() {
		//highlighted row in table
		Task selected = taskTable.getSelectionModel().selectedItemProperty().get();
		int sIndex = taskTable.getSelectionModel().getSelectedIndex();
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
				taskTable.getSelectionModel().select(sIndex);
			}catch(IOException e) {
				System.out.println("\nError code: Satchel\n" + e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			System.out.println("\nError Code: Row not selected");
		}
	}
	@FXML
	private void classAddButtonClicked() {
		int size = classTable.getItems().size();
		int sIndex = classTable.getSelectionModel().getSelectedIndex();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("newClass.fxml"));
			Scene addWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setScene(addWindow);
			stage.showAndWait();
			initializeClass();
			//select correct row after closing add window
			if(size == classTable.getItems().size()) {
				classTable.getSelectionModel().select(sIndex);
			}
			else {
				ArrayList<Models.Class> classes = ModelControl.getClasses();
				for(int count = 0;count<classTable.getItems().size();count++) {
					if(classes.get(classes.size()-1).getId() == classTable.getItems().get(count).getId()) {
						classTable.getSelectionModel().select(count);
					}
				}
			}
		}catch(IOException e) {
			System.out.println("\nError code: Pouch\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	@FXML
	private void classViewButtonClicked() {
		//highlighted row in table
		Models.Class selected = classTable.getSelectionModel().selectedItemProperty().get();
		int sIndex = classTable.getSelectionModel().getSelectedIndex();
		if(selected != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("viewClass.fxml"));
				
				//pass in values from selected row
				//TODO: create controller and layouts for different views
				ViewClassController controller = new ViewClassController(selected);
				
				loader.setController(controller);
				Scene viewWindow = new Scene(loader.load());
				
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UTILITY);
				stage.setScene(viewWindow);
				stage.showAndWait();
				initializeClass();
				classTable.getSelectionModel().select(sIndex);
			}catch(IOException e) {
				System.out.println("\nError code: Satchel\n" + e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			System.out.println("\nError Code: Row not selected");
		}
	}
	@FXML
	private void typeAddButtonClicked() {
		int size = typeTable.getItems().size();
		int sIndex = typeTable.getSelectionModel().getSelectedIndex();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("newType.fxml"));
			Scene addWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setScene(addWindow);
			stage.showAndWait();
			initializeClass();
			//select correct row after closing add window
			if(size == typeTable.getItems().size()) {
				typeTable.getSelectionModel().select(sIndex);
			}
			else {
				ArrayList<TaskType> types = ModelControl.getTaskTypes();
				for(int count = 0;count<typeTable.getItems().size();count++) {
					if(types.get(types.size()-1).getId() == typeTable.getItems().get(count).getId()) {
						typeTable.getSelectionModel().select(count);
					}
				}
			}
		}catch(IOException e) {
			System.out.println("\nError code: Pouch\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	@FXML
	private void typeViewButtonClicked() {
		//highlighted row in table
		TaskType selected = typeTable.getSelectionModel().selectedItemProperty().get();
		int sIndex = typeTable.getSelectionModel().getSelectedIndex();
		if(selected != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("viewType.fxml"));
				
				//pass in values from selected row
				ViewTypeController controller = new ViewTypeController(selected);
				
				loader.setController(controller);
				Scene viewWindow = new Scene(loader.load());
				
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UTILITY);
				stage.setScene(viewWindow);
				stage.showAndWait();
				initializeClass();
				typeTable.getSelectionModel().select(sIndex);
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
	private void handleTaskClick() {
	    Task row = taskTable.getSelectionModel().getSelectedItem();
	    if(row == null) return;
	    if(row != taskTemp){
	        taskTemp = row;
	        taskLastClickTime = new Date();
	    } else if(row == taskTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - taskLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	             taskViewButtonClicked();
	        } else {
	            taskLastClickTime = new Date();
	        }
	    }
	}
	@FXML
	private void handleClassClick() {
	    Models.Class row = classTable.getSelectionModel().getSelectedItem();
	    if(row == null) return;
	    if(row != classTemp){
	        classTemp = row;
	        classLastClickTime = new Date();
	    } else if(row == classTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - classLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	             classViewButtonClicked();
	        } else {
	            classLastClickTime = new Date();
	        }
	    }
	}
	@FXML
	private void handleTypeClick() {
	    TaskType row = typeTable.getSelectionModel().getSelectedItem();
	    if(row == null) return;
	    if(row != typeTemp){
	        typeTemp = row;
	        typeLastClickTime = new Date();
	    } else if(row == typeTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - typeLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	             typeViewButtonClicked();
	        } else {
	            typeLastClickTime = new Date();
	        }
	    }
	}
	@FXML
	private void homeButtonClicked() {
		resetButtons();
		homeButton.setStyle(selectedColor);
		resetViews();
		initializeHome();
		homeView.setVisible(true);
	}
	@FXML
	private void calendarButtonClicked() {
		resetButtons();
		calendarButton.setStyle(selectedColor);
		resetViews();
		initializeCalendar();
		calendarView.setVisible(true);
	}
	@FXML
	private void taskButtonClicked() {
		resetButtons();
		taskButton.setStyle(selectedColor);
		resetViews();
		initializeTasks();
		taskView.setVisible(true);
	}
	@FXML
	private void classButtonClicked() {
		resetButtons();
		classButton.setStyle(selectedColor);
		resetViews();
		initializeClass();
		classView.setVisible(true);
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
