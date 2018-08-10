package Views;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeTableView;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
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
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import jfxtras.icalendarfx.VCalendar;
import jfxtras.icalendarfx.components.VEvent;
import jfxtras.scene.control.agenda.icalendar.ICalendarAgenda;

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
	@FXML private JFXButton leftHomeButton;
	@FXML private JFXButton rightHomeButton;
	@FXML private Label dailyTaskDateLabel;
	
	//tasks (subset of home)
	@FXML private JFXTreeTableView homeTreeTable;
	@FXML private TreeTableColumn<Task, String> nameTreeTableCol;
	@FXML private TreeTableColumn<Task, String> descTreeTableCol;
	@FXML private TreeTableColumn<Task, String> typeTreeTableCol;
	@FXML private TreeTableColumn<Task, String> classTreeTableCol;
	@FXML private TreeTableColumn<Task, LocalDateTime> dueDateTreeTableCol;
	@FXML private TreeTableColumn<Task, Boolean> completedTreeTableCol;
	//schedule (subset of home)
	@FXML private JFXButton homeScheduleViewButton;
	@FXML private JFXButton homeScheduleAddButton;
	
	//calendar tab
	@FXML private BorderPane calendarView;
	@FXML private Label yearLabel;
	@FXML private JFXButton leftCalendarButton;
	@FXML private JFXButton rightCalendarButton;
	@FXML private JFXButton january, february, march, april, may, june, july,
	 						august, september, october, november, december;
	@FXML private GridPane calendarGrid;
	@FXML private Label sunday, monday, tuesday, wednesday, thursday,
						friday, saturday;
	@FXML private Label label00, label10, label20, label30, label40, label50, label60,
						label01, label11, label21, label31, label41, label51, label61,
						label02, label12, label22, label32, label42, label52, label62,
						label03, label13, label23, label33, label43, label53, label63,
						label04, label14, label24, label34, label44, label54, label64,
						label05, label15, label25, label35, label45, label55, label65;
	public Calendar calendarInstance = Calendar.getInstance();		//for position in time for reference
	ArrayList<Label> labelList = new ArrayList<Label>();
	
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
	//class (subset of classes)
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
	//type (subset of classes)
	@FXML private TableView<TaskType> typeTable;
	@FXML private JFXButton typeAddButton;
	@FXML private JFXButton typeViewButton;
	@FXML private TableColumn<TaskType, String> typeNameColumn;
	@FXML private TableColumn<TaskType, String> typeDescColumn;
	@FXML private TableColumn<TaskType, Integer> typeWarnColumn;
	@FXML private TableColumn<TaskType, String> typeTTCColumn;
	@FXML private TableColumn<TaskType, Integer> typeTAColumn;
	
	//for measuring time between clicks for double click feature
	Task taskTemp;
	Models.Class classTemp;
	TaskType typeTemp;
	Date taskLastClickTime;
	Date classLastClickTime;
	Date typeLastClickTime;
	
	//calendar
	Calendar calendar;
	
	//button click properties
	final String selectedColor = "-fx-background-color:  #f2782f;";
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeLabels();
		homeButton.fire();
		
		//consolidate date labels into arraylist
		//add in order of days according to gregorian calendar
	    labelList.add(label00);
	    labelList.add(label10);
	    labelList.add(label20);
	    labelList.add(label30);
	    labelList.add(label40);
	    labelList.add(label50);
	    labelList.add(label60);
	    
	    labelList.add(label01);
	    labelList.add(label11);
	    labelList.add(label21);
	    labelList.add(label31);
	    labelList.add(label41);
	    labelList.add(label51);
	    labelList.add(label61);

	    labelList.add(label02);
	    labelList.add(label12);
	    labelList.add(label22);
	    labelList.add(label32);
	    labelList.add(label42);
	    labelList.add(label52);
	    labelList.add(label62);
	    
	    labelList.add(label03);
	    labelList.add(label13);
	    labelList.add(label23);
	    labelList.add(label33);
	    labelList.add(label43);
	    labelList.add(label53);
	    labelList.add(label63);
	    
	    labelList.add(label04);
	    labelList.add(label14);
	    labelList.add(label24);
	    labelList.add(label34);
	    labelList.add(label44);
	    labelList.add(label54);
	    labelList.add(label64);
	    
	    labelList.add(label05);
	    labelList.add(label15);
	    labelList.add(label25);
	    labelList.add(label35);
	    labelList.add(label45);
	    labelList.add(label55);
	    labelList.add(label65);
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
		completedTreeTableCol.setCellFactory(c -> new CheckBoxTreeTableCell<>());
		
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
		//label grid alignment
		GridPane.setHalignment(sunday, HPos.CENTER);
		GridPane.setHalignment(monday, HPos.CENTER);
		GridPane.setHalignment(tuesday, HPos.CENTER);
		GridPane.setHalignment(wednesday, HPos.CENTER);
		GridPane.setHalignment(thursday, HPos.CENTER);
		GridPane.setHalignment(friday, HPos.CENTER);
		GridPane.setHalignment(saturday, HPos.CENTER);
		
		for(int count=0;count<labelList.size();count++) {
			GridPane.setValignment(labelList.get(count), VPos.TOP);
			GridPane.setMargin(labelList.get(count), new Insets(5, 5, 5, 5));
		}
		
		calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		
		String firstDay = getFirstDayOfMonth().toLowerCase();
		String firstLabel = "";
		if(firstDay.equals("sunday")) {
			firstLabel = "label00";
		}
		else if(firstDay.equals("monday")) {
			firstLabel = "label10";
		}
		else if(firstDay.equals("tuesday")) {
			firstLabel = "label20";
		}
		else if(firstDay.equals("wednesday")) {
			firstLabel = "label30";
		}
		else if(firstDay.equals("thursday")) {
			firstLabel = "label40";
		}
		else if(firstDay.equals("friday")) {
			firstLabel = "label50";
		}
		else if(firstDay.equals("saturday")){
			firstLabel = "label60";
		}
		
		int days = getNumberOfDaysInMonth();
		int firstIndex = -1;
		//list should be size 42
		for(int count=0;count<42;count++) {
			if(labelList.get(count).getId().equals(firstLabel)) {
				labelList.get(count).setText("1");
				firstIndex = count;
			}
			else if(count < firstIndex || firstIndex == -1){
				labelList.get(count).setText("");
				//TODO: disable cell
			}
			else if(count >= firstIndex+days) {
				labelList.get(count).setText("");
				//TODO: disable cell
			}
			else {
				labelList.get(count).setText((count-firstIndex+1)+"");
			}
		}
		
		initializeCalendarData();
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
		taskCompletedColumn.setCellValueFactory(tc -> new SimpleBooleanProperty(tc.getValue().getFinishFlag()));
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
		//class tab
		classNameColumn.setCellValueFactory(new PropertyValueFactory<Models.Class, String>("name"));
		classAbrColumn.setCellValueFactory(new PropertyValueFactory<Models.Class, String>("abbreviation"));
		classDetailsColumn.setCellValueFactory(new PropertyValueFactory<Models.Class, String>("details"));
		classDOWColumn.setCellValueFactory(new PropertyValueFactory<Models.Class, String>("daysOfWeek"));
		classTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Models.Class, String>,
				ObservableValue<String>>(){
	        @Override
	        public ObservableValue<String> call(
	                TableColumn.CellDataFeatures<Models.Class, String> c) {
	            return new SimpleStringProperty(c.getValue().getStartTime("")
	                    + " - " + c.getValue().getEndTime(""));
	        }
		});
		classTAColumn.setCellValueFactory(new PropertyValueFactory<Models.Class, Integer>("totalAssignments"));
		
		classNameColumn.setStyle("-fx-alignment: CENTER;");
		classAbrColumn.setStyle("-fx-alignment: CENTER;");
		classDetailsColumn.setStyle("-fx-alignment: CENTER;");
		classDOWColumn.setStyle("-fx-alignment: CENTER;");
		classTimeColumn.setStyle("-fx-alignment: CENTER;");
		classTAColumn.setStyle("-fx-alignment: CENTER;");
		
		classNameColumn.prefWidthProperty().bind(classTable.widthProperty().multiply(.163));
		classAbrColumn.prefWidthProperty().bind(classTable.widthProperty().multiply(.096));
		classDetailsColumn.prefWidthProperty().bind(classTable.widthProperty().multiply(.362));
		classDOWColumn.prefWidthProperty().bind(classTable.widthProperty().multiply(.118));
		classTimeColumn.prefWidthProperty().bind(classTable.widthProperty().multiply(.123));
		classTAColumn.prefWidthProperty().bind(classTable.widthProperty().multiply(.13));
		
		//type tab
		typeNameColumn.setCellValueFactory(new PropertyValueFactory<TaskType, String>("name"));
		typeDescColumn.setCellValueFactory(new PropertyValueFactory<TaskType, String>("description"));
		typeWarnColumn.setCellValueFactory(new PropertyValueFactory<TaskType, Integer>("warningPeriod"));
		typeTTCColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TaskType, String>,
				ObservableValue<String>>(){
	        @Override
	        public ObservableValue<String> call(
	                TableColumn.CellDataFeatures<TaskType, String> tt) {
	        	return new SimpleStringProperty(tt.getValue().getTimeToComplete(""));
	        }
		});
		typeTAColumn.setCellValueFactory(new PropertyValueFactory<TaskType, Integer>("totalAssignments"));
		
		typeNameColumn.setStyle("-fx-alignment: CENTER;");
		typeDescColumn.setStyle("-fx-alignment: CENTER;");
		typeWarnColumn.setStyle("-fx-alignment: CENTER;");
		typeTTCColumn.setStyle("-fx-alignment: CENTER;");
		typeTAColumn.setStyle("-fx-alignment: CENTER;");
		
		
		typeNameColumn.prefWidthProperty().bind(typeTable.widthProperty().multiply(.149));
		typeDescColumn.prefWidthProperty().bind(typeTable.widthProperty().multiply(.411));
		typeWarnColumn.prefWidthProperty().bind(typeTable.widthProperty().multiply(.12));
		typeTTCColumn.prefWidthProperty().bind(typeTable.widthProperty().multiply(.202));
		typeTAColumn.prefWidthProperty().bind(typeTable.widthProperty().multiply(.113));
		
		initializeClassData();
	}
	@SuppressWarnings("unchecked")
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
		homeTreeTable.setRoot(root);				//suppressed warning
		
		//schedule
		
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
		//class tab
		classTable.getItems().clear();
		classTable.setItems(ModelControl.getClasses(""));
		classTable.getSortOrder().add(classNameColumn);
		
		//type tab
		typeTable.getItems().clear();
		typeTable.setItems(ModelControl.getTaskTypes(""));
		typeTable.getSortOrder().add(typeNameColumn);
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
		//update labels in case anything changed
		initializeLabels();
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
		//update labels in case anything changed
		initializeLabels();
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
		resetHomeButtons();
		homeButton.setStyle(selectedColor);
		resetViews();
		initializeHome();
		homeView.setVisible(true);
	}
	@FXML
	private void calendarButtonClicked() {
		resetHomeButtons();
		calendarButton.setStyle(selectedColor);
		resetViews();
		initializeCalendar();
		calendarView.setVisible(true);
	}
	@FXML
	private void taskButtonClicked() {
		resetHomeButtons();
		taskButton.setStyle(selectedColor);
		resetViews();
		initializeTasks();
		taskView.setVisible(true);
	}
	@FXML
	private void classButtonClicked() {
		resetHomeButtons();
		classButton.setStyle(selectedColor);
		resetViews();
		initializeClass();
		classView.setVisible(true);
	}
	@FXML
	private void leftCalendarClicked() {
		
	}
	@FXML
	private void rightCalendarClicked() {
		
	}
	@FXML
	private void monthButtonClicked(ActionEvent event) {
		resetMonthButtons();
		Button btn = (Button) event.getSource();
		btn.setStyle(selectedColor);
	}
	@FXML
	private void leftHomeClicked() {
		ModelControl.removeDayFromDayOfReference();
		initializeHome();
	}
	@FXML
	private void rightHomeClicked() {
		ModelControl.addDayToDayOfReference();
		initializeHome();
	}
	private String getFirstDayOfMonth() {
	    //cal.set(Calendar.DATE, cal.getInstance().get(Calendar.DATE));
	    //cal.set(Calendar.MONTH, cal.getInstance().get(Calendar.MONTH));
	    //cal.set(Calendar.YEAR, cal.getInstance().get(Calendar.YEAR));
	    Calendar cal = (Calendar) calendarInstance.clone();
		cal.set(Calendar.DAY_OF_MONTH, 1);
	    
	    Date firstDayOfMonth = cal.getTime();
	    DateFormat sdf = new SimpleDateFormat("EEEEEEEE");   
	    return sdf.format(firstDayOfMonth);
	}
	private int getNumberOfDaysInMonth() {
		return calendarInstance.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	private void resetViews() {
		homeView.setVisible(false);
		calendarView.setVisible(false);
		taskView.setVisible(false);
		classView.setVisible(false);
	}
	private void resetHomeButtons() {
		homeButton.setStyle(null);
		calendarButton.setStyle(null);
		taskButton.setStyle(null);
		classButton.setStyle(null);
	}
	private void resetMonthButtons() {
		january.setStyle(null);
		february.setStyle(null);
		march.setStyle(null);
		april.setStyle(null);
		may.setStyle(null);
		june.setStyle(null);
		july.setStyle(null);
		august.setStyle(null);
		september.setStyle(null);
		october.setStyle(null);
		november.setStyle(null);
		december.setStyle(null);
	}
}
