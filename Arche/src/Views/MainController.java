package Views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.event.ChangeListener;

import Models.Admin;
import Models.ModelControl;
import Models.Project;
import Models.Task;
import Models.TaskType;
import Models.Class;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MainController implements Initializable{
	@FXML TabPane layout;
	@FXML Tab dailyTab;
	@FXML Tab calendarTab;
	@FXML Tab tasksTab;
	@FXML Tab classesTab;
	@FXML Tab projectsTab;

	//dailytab
	@FXML private Button leftArrowButton;
	@FXML private Button rightArrowButton;
	@FXML private Label dailyTaskDateLabel;
	@FXML private TabPane dailyTabPane;
	@FXML private Label incompleteTodayLabel;
	@FXML private Label incompleteSoonLabel;
	@FXML private Label overdueLabel;
	@FXML private TreeTableView<Task> dailyTreeTableView;
	@FXML private TreeTableColumn<Task, String> nameTreeTableCol;
	@FXML private TreeTableColumn<Task, String> descTreeTableCol;
	@FXML private TreeTableColumn<Task, String> typeTreeTableCol;
	@FXML private TreeTableColumn<Task, String> classTreeTableCol;
	@FXML private TreeTableColumn<Task, LocalDateTime> dueDateTreeTableCol;
	@FXML private TreeTableColumn<Task, Boolean> completedTreeTableCol;
	//tasktab
	@FXML private TableView<Task> tableView;
	@FXML private TableColumn<Task, String> nameColumn;
	@FXML private TableColumn<Task, String> descriptionColumn;
	@FXML private TableColumn<Task, String> typeColumn;
	@FXML private TableColumn<Task, String> classColumn;
	@FXML private TableColumn<Task, LocalDateTime> dueDateColumn;
	@FXML private TableColumn<Task, Boolean> completedColumn;
	@FXML private Button addButton;
	@FXML private Button viewButton;
	
	Task temp;
	Date lastClickTime;
	
	//main window initialization
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeWindowParams();
		initializeDailyTab();
		initializeCalendarTab();
		initializeTaskTab();
		initializeClassTab();
		initializeProjectTab();
	}
	private void initializeWindowParams() {
		layout.setTabMinWidth(75);
		layout.setTabMaxWidth(75);
		layout.setTabMinHeight(75);
		layout.setTabMaxHeight(75);
		try {
			dailyTab.setGraphic(buildIconImage("resources/icons/icons8-home-filled-50.png",50,50));
			calendarTab.setGraphic(buildIconImage("resources/icons/icons8-calendar-filled-50.png",50,50));
			tasksTab.setGraphic(buildIconImage("resources/icons/icons8-clipboard-filled-40.png",50,50));
			classesTab.setGraphic(buildIconImage("resources/icons/icons8-toolbox-filled-50.png",50,50));
			projectsTab.setGraphic(buildIconImage("resources/icons/icons8-open-50.png",50,50));
		}catch(FileNotFoundException e) {
			System.out.println("Error mcontrol1: " + e.getMessage());
		}		
	}
	private void initializeDailyTab(){
		try {
			leftArrowButton.setGraphic(buildIconImage("resources/icons/icons8-left-filled-50.png",25,25));
			rightArrowButton.setGraphic(buildIconImage("resources/icons/icons8-right-filled-50.png",25,25));
		}catch(FileNotFoundException e) {
			System.out.println("Error: mcontrol2: " + e.getMessage());
		}
		dailyTabPane.tabMinWidthProperty().bind(
				dailyTabPane.widthProperty().divide(dailyTabPane.getTabs().size()).subtract(25));
		//date label formatting
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		//Date date = Date.from(ModelControl.dayOfReference.atStartOfDay(ZoneId.systemDefault()).toInstant());
		dailyTaskDateLabel.setText(sdf.format(ModelControl.dayOfReference));
		
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
		nameTreeTableCol.prefWidthProperty().bind(dailyTreeTableView.widthProperty().multiply(.107));
		descTreeTableCol.prefWidthProperty().bind(tableView.widthProperty().multiply(.416));
		typeTreeTableCol.prefWidthProperty().bind(tableView.widthProperty().multiply(.111));
		classTreeTableCol.prefWidthProperty().bind(tableView.widthProperty().multiply(.111));
		dueDateTreeTableCol.prefWidthProperty().bind(tableView.widthProperty().multiply(.14));
		completedTreeTableCol.prefWidthProperty().bind(tableView.widthProperty().multiply(.107));
		
		//text alignment
		nameTreeTableCol.setStyle("-fx-alignment: CENTER;");
		descTreeTableCol.setStyle("-fx-alignment: CENTER;");
		typeTreeTableCol.setStyle("-fx-alignment: CENTER;");
		classTreeTableCol.setStyle("-fx-alignment: CENTER;");
		dueDateTreeTableCol.setStyle("-fx-alignment: CENTER;");
		completedTreeTableCol.setStyle("-fx-alignment: CENTER;");
		
		/*Stage stage = (Stage) leftArrowButton.getScene().getWindow();
		Scene scene = stage.getScene();
		
		DayView day = new DayView();
		Scene newScene = new Scene(day);
		
		
		scene.getRoot().getChildrenUnmodifiable().add(newScene.getRoot());
		stage.show();*/
		
		initializeDailyTabData();
	}
	private void initializeCalendarTab() {
		//TODO
	}
	private void initializeTaskTab() {
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
		dueDateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.14));
		completedColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(.107));
		
		//text alignment
		nameColumn.setStyle("-fx-alignment: CENTER;");
		typeColumn.setStyle("-fx-alignment: CENTER;");
		classColumn.setStyle("-fx-alignment: CENTER;");
		dueDateColumn.setStyle("-fx-alignment: CENTER;");
		
		initializeTaskTabData();
	}
	private void initializeClassTab() {
		//TODO
	}
	private void initializeProjectTab() {
		//TODO
	}
	@FXML
	private void initializeDailyTabData() {
		//dailyTreeTableView.getRoot().getChildren().clear();
		ArrayList<Task> tableTasks = ModelControl.getUrgentTasks();
		ArrayList<Task> overDueTasks = ModelControl.getOverdueTasks();
		ArrayList<Task> dueSoonTasks = ModelControl.getApproachingTasks();
		
		dailyTreeTableView.setShowRoot(false);
		incompleteTodayLabel.setText(tableTasks.size() + " Immaediate Incomplete Tasks");
		overdueLabel.setText(overDueTasks.size() + " Overdue Tasks");
		incompleteSoonLabel.setText(dueSoonTasks.size() + " Approaching Tasks");
		
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
		dailyTreeTableView.setRoot(root);
	}
	@FXML
	private void initializeTaskTabData() {
		tableView.getItems().clear();
		tableView.setItems(ModelControl.getTasks(""));
		tableView.getSortOrder().add(dueDateColumn);
	}
	@FXML
	private void leftArrowClicked() {
		ModelControl.removeDayFromDayOfReference();
		initializeDailyTab();
	}
	@FXML
	private void rightArrowClicked() {
		ModelControl.addDayToDayOfReference();
		initializeDailyTab();
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
			initializeTaskTabData();
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
				initializeTaskTabData();
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
	private ImageView buildIconImage(String url, int height, int width) throws FileNotFoundException{
		FileInputStream input = new FileInputStream(url);
		Image i = new Image(input);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setImage(i);
        return imageView;
	}
}
