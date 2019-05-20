package Views;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
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
import java.util.concurrent.TimeUnit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTreeTableView;

import javafx.concurrent.*;
import javafx.concurrent.WorkerStateEvent;
import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import Runners.DBConn;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
	
	//notifications (subset of home)
	@FXML private JFXTreeTableView homeTreeTable;
	@FXML private TreeTableColumn<Models.Task, String> nameTreeTableCol;
	@FXML private TreeTableColumn<Models.Task, String> descTreeTableCol;
	@FXML private TreeTableColumn<Models.Task, String> typeTreeTableCol;
	@FXML private TreeTableColumn<Models.Task, String> classTreeTableCol;
	@FXML private TreeTableColumn<Models.Task, String> dueDateTreeTableCol;
	@FXML private TreeTableColumn<Models.Task, Boolean> completedTreeTableCol;
	//schedule (subset of home)
	@FXML private JFXButton homeScheduleViewButton;
	@FXML private JFXButton homeScheduleAddButton;
	@FXML private Tab scheduleTab;
	
	@FXML private HBox scheduleHBox;
	@FXML private VBox scheduleTimes;
	@FXML private VBox scheduleSunday, scheduleMonday, scheduleTuesday, scheduleWednesday,
					   scheduleThursday, scheduleFriday, scheduleSaturday;
	
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
	
	//for cell date
	@FXML private Label label00, label10, label20, label30, label40, label50, label60,
						label01, label11, label21, label31, label41, label51, label61,
						label02, label12, label22, label32, label42, label52, label62,
						label03, label13, label23, label33, label43, label53, label63,
						label04, label14, label24, label34, label44, label54, label64,
						label05, label15, label25, label35, label45, label55, label65;
	
	//for disabling cell, set opacity
	@FXML private Pane 	pane00, pane10, pane20, pane30, pane40, pane50, pane60,
						pane01, pane11, pane21, pane31, pane41, pane51, pane61,
						pane02, pane12, pane22, pane32, pane42, pane52, pane62,
						pane03, pane13, pane23, pane33, pane43, pane53, pane63,
						pane04, pane14, pane24, pane34, pane44, pane54, pane64,
						pane05, pane15, pane25, pane35, pane45, pane55, pane65;
	//for cell data
	@FXML private JFXTextArea 	text00, text10, text20, text30, text40, text50, text60,
								text01, text11, text21, text31, text41, text51, text61,
								text02, text12, text22, text32, text42, text52, text62,
								text03, text13, text23, text33, text43, text53, text63,
								text04, text14, text24, text34, text44, text54, text64,
								text05, text15, text25, text35, text45, text55, text65;
	
	public Calendar guicalendar = Calendar.getInstance();		//for calendar the gui is displaying
	public Calendar calendarToday = Calendar.getInstance();		//for highlighted date on calendar
	
	ArrayList<Label> labelList = new ArrayList<Label>();
	ArrayList<Pane> paneList = new ArrayList<Pane>();
	ArrayList<TextArea> textList = new ArrayList<TextArea>();
	
	//task tab
	@FXML private BorderPane taskView;
	@FXML private TableView<Models.Task> taskTable;
	@FXML private TableColumn<Models.Task, String> taskNameColumn;
	@FXML private TableColumn<Models.Task, String> taskDescriptionColumn;
	@FXML private TableColumn<Models.Task, String> taskTypeColumn;
	@FXML private TableColumn<Models.Task, String> taskClassColumn;
	@FXML private TableColumn<Models.Task, LocalDateTime> taskDueDateColumn;
	@FXML private TableColumn<Models.Task, Boolean> taskCompletedColumn;
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
	
	//reconnecting panes
	@FXML private BorderPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	//for measuring time between clicks for double click feature
	Models.Task homeTemp;
	Models.Task taskTemp;
	Models.Class classTemp;
	TaskType typeTemp;
	Pane paneTemp;
	Date homeLastClickTime;
	Date taskLastClickTime;
	Date classLastClickTime;
	Date typeLastClickTime;
	Date cellLastClickTime;
	
	//button click properties
	final String buttonSelectedColor = "-fx-background-color:  #f2782f;";
	final String cellSelectedColor = "-fx-background-color: #97d4f8;";
	final String cellTodayColor = "-fx-background-color:  #2fa9f2";
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		calendarToday.setTimeZone(TimeZone.getDefault());
		loadingPane.setVisible(false);
		initializeLabels();
		homeButton.fire();
		createLists();
	}
	private void initializeLabels() {
		ArrayList<Models.Task> tableTasks = ModelControl.getUrgentTasks("get for todays labels");
		ArrayList<Models.Task> overDueTasks = ModelControl.getOverdueTasks("get for todays labels");
		ArrayList<Models.Task> dueSoonTasks = ModelControl.getApproachingTasks("get for todays labels");
		
		urgentLabel.setText(tableTasks.size() + "");
		overdueLabel.setText(overDueTasks.size() + "");
		approachingLabel.setText(dueSoonTasks.size() + "");
	}
	private void initializeHome() {
		//disable schedule
		scheduleTab.setDisable(true);
		scheduleTab.setText("");
		
		//date label formatting
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		//Date date = Date.from(ModelControl.dayOfReference.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date date = Date.from(Instant.ofEpochMilli(ModelControl.dayOfReference.getTimeInMillis()));
		dailyTaskDateLabel.setText(sdf.format(date));
		
		nameTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Models.Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getName())
				);
		descTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Models.Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getDescription())
	        );
		typeTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Models.Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getType())
	        );
		classTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Models.Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getClassAbr())
	        );
		dueDateTreeTableCol.setCellValueFactory(
	            (TreeTableColumn.CellDataFeatures<Models.Task, String> param) -> 
	            new ReadOnlyStringWrapper(param.getValue().getValue().getDueDate(0))
	        );
		/*final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
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
		});*/
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
		
		guicalendar.setTimeZone(TimeZone.getDefault());
		resetPanes();
		resetTextAreas();
		
		yearLabel.setText(getYear()+"");
		int month = getMonth();
		switch(month) {
		case 0: january.setStyle(buttonSelectedColor);	break;
		case 1: february.setStyle(buttonSelectedColor);	break;
		case 2: march.setStyle(buttonSelectedColor);		break;
		case 3: april.setStyle(buttonSelectedColor);		break;
		case 4: may.setStyle(buttonSelectedColor);		break;
		case 5: june.setStyle(buttonSelectedColor);		break;
		case 6: july.setStyle(buttonSelectedColor);		break;
		case 7: august.setStyle(buttonSelectedColor);		break;
		case 8: september.setStyle(buttonSelectedColor);	break;
		case 9: october.setStyle(buttonSelectedColor);	break;
		case 10: november.setStyle(buttonSelectedColor);	break;
		case 11: december.setStyle(buttonSelectedColor);	break;
		default: january.setStyle(buttonSelectedColor); 	break;
		}
		
		initializeCalendarData();
	}
	private void initializeTasks() {
		taskNameColumn.setCellValueFactory(new PropertyValueFactory<Models.Task, String>("name"));
		taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<Models.Task, String>("description"));
		taskTypeColumn.setCellValueFactory(new PropertyValueFactory<Models.Task, String>("type"));
		taskClassColumn.setCellValueFactory(new PropertyValueFactory<Models.Task, String>("classAbr"));
		
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
		taskDueDateColumn.setCellValueFactory(Task -> Task.getValue().getDueDate(""));
		taskDueDateColumn.setCellFactory(taskDueDateColumn -> new TableCell<Models.Task, LocalDateTime>() {
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
	            return new SimpleStringProperty(c.getValue().getStartTime("format")
	                    + " - " + c.getValue().getEndTime("format"));
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
		ArrayList<Models.Task> tableTasks = ModelControl.getUrgentTasks();
		ArrayList<Models.Task> overDueTasks = ModelControl.getOverdueTasks();
		ArrayList<Models.Task> dueSoonTasks = ModelControl.getApproachingTasks();
		
		homeTreeTable.setShowRoot(false);
		
		TreeItem<Models.Task> root = new TreeItem<>();
		
		boolean dueComplete = generateCompleteBoolean(tableTasks);
		boolean overdueComplete = generateCompleteBoolean(overDueTasks);
		boolean soonComplete = generateCompleteBoolean(dueSoonTasks);
			
		TreeItem<Models.Task> due = new TreeItem<>(new Models.Task("Urgent",dueComplete));	
		TreeItem<Models.Task> overdue = new TreeItem<>(new Models.Task("Overdue",overdueComplete));
		TreeItem<Models.Task> soon = new TreeItem<>(new Models.Task("Approaching",soonComplete));
		
		for(int count = 0;count<tableTasks.size();count++) {
			TreeItem<Models.Task> val = new TreeItem<>(tableTasks.get(count));
			due.getChildren().add(val);
		}
		for(int count = 0;count<overDueTasks.size();count++) {
			TreeItem<Models.Task> val = new TreeItem<>(overDueTasks.get(count));
			overdue.getChildren().add(val);
		}
		for(int count = 0;count<dueSoonTasks.size();count++) {
			TreeItem<Models.Task> val = new TreeItem<>(dueSoonTasks.get(count));
			soon.getChildren().add(val);
		}
		
		root.getChildren().add(due);
		root.getChildren().add(overdue);
		root.getChildren().add(soon);
		homeTreeTable.setRoot(root);				//suppressed warning
		
		//schedule
		
	}
	private void initializeCalendarData() {
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
			//set dates
			if(labelList.get(count).getId().equals(firstLabel)) {
				labelList.get(count).setText("1");
				firstIndex = count;
				
				int dateNum = Integer.parseInt(labelList.get(count).getText());
				if(testIfToday(dateNum)) {
					textList.get(count).setStyle(cellTodayColor);
				}
			}
			else if(count < firstIndex || firstIndex == -1){
				labelList.get(count).setText("");
				paneList.get(count).setOpacity(1.0);		//disable cell
			}
			else if(count >= firstIndex+days) {
				labelList.get(count).setText("");
				paneList.get(count).setOpacity(1.0);		//disable cell
			}
			else {
				labelList.get(count).setText((count-firstIndex+1)+"");
				
				int dateNum = Integer.parseInt(labelList.get(count).getText());
				if(testIfToday(dateNum)) {
					textList.get(count).setStyle(cellTodayColor);
					//textList.get(count).setText("\u2022\u00A0today");	//bullet then space, needed so text wraps properly
				}
			}
			//fill info
			if(paneList.get(count).getOpacity() == 0.0) {
				int dateNum = Integer.parseInt(labelList.get(count).getText());
				Calendar cal = (Calendar) guicalendar.clone();
				cal.set(Calendar.DATE, dateNum);
				ArrayList<Models.Task> tasks = ModelControl.getDayTasks(cal);
				String text = "";
				if(tasks.size() == 0) {
					//do nothing
				}
				else if(tasks.size() == 1 || tasks.size() == 2){
					for(int x=0;x<tasks.size();x++) {
						text += "\u2022\u00A0" + tasks.get(x).getName() + "\n"; 
						textList.get(count).setText(text);
					}
				}
				else {
					text += "\u2022\u00A0" + tasks.size() + " tasks due";
					textList.get(count).setText(text);
				}
			}
		}
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
			
			NewTaskController controller = new NewTaskController(this);
			loader.setController(controller);
			
			Scene addWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setResizable(false);
			
			stage.setScene(addWindow);
			stage.showAndWait();
			initializeTasks();
			//select correct row after closing add window
			if(size == taskTable.getItems().size()) {
				taskTable.getSelectionModel().select(sIndex);
			}
			else {
				ArrayList<Models.Task> tasks = ModelControl.getTasks();
				for(int count = 0;count<taskTable.getItems().size();count++) {
					if(tasks.get(tasks.size()-1).getId() == taskTable.getItems().get(count).getId()) {
						taskTable.getSelectionModel().select(count);
					}
				}
			}
		}catch(IOException e) {
			System.out.println("\nError code: MNC-Pouch\n" + e.getMessage());
			e.printStackTrace();
		}
		//update labels in case anything changed
		initializeLabels();
	}
	@FXML
	private void taskViewButtonClicked() {
		//highlighted row in table
		Models.Task selected = taskTable.getSelectionModel().selectedItemProperty().get();
		int sIndex = taskTable.getSelectionModel().getSelectedIndex();
		if(selected != null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("viewTask.fxml"));
				
				//pass in values from selected row
				ViewTaskController controller = new ViewTaskController(selected, this);
				loader.setController(controller);
				
				Scene viewWindow = new Scene(loader.load());
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UTILITY);
				stage.setResizable(false);
				
				stage.setScene(viewWindow);
				stage.showAndWait();
				initializeTasks();
				taskTable.getSelectionModel().select(sIndex);
			}catch(IOException e) {
				System.out.println("\nError code: MNC-Satchel\n" + e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			System.out.println("\nError Code: Row not selected");
		}
		//update labels in case anything changed
		initializeLabels();
	}
	private void taskViewCellClicked(Pane p) {
	    int dateNum = -1;
	    for(int count=0;count<paneList.size();count++) {
	    	if(p == paneList.get(count)) {
	    		dateNum = Integer.parseInt(labelList.get(count).getText());
	    		break;
	    	}
	    }
	    Calendar cal = (Calendar) guicalendar.clone();
		cal.set(Calendar.DATE, dateNum);
		ArrayList<Models.Task> tasks = ModelControl.getDayTasks(cal);
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("viewCellTasks.fxml"));
			
			//pass in values from selected row
			ViewCellTasksController controller = new ViewCellTasksController(tasks, this);
			loader.setController(controller);
			
			Scene viewWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setResizable(false);
			
			stage.setScene(viewWindow);
			stage.showAndWait();
			initializeCalendar();
			//select cell
			cellSelectedUpdate(p);
		}catch(IOException e) {
			System.out.println("\nError code: MNC-Shine\n" + e.getMessage());
			e.printStackTrace();
		}
		initializeLabels();
	}
	@FXML
	private void classAddButtonClicked() {
		int size = classTable.getItems().size();
		int sIndex = classTable.getSelectionModel().getSelectedIndex();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("newClass.fxml"));
			
			NewClassController controller = new NewClassController(this);
			loader.setController(controller);
			
			Scene addWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setResizable(false);
			
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
			System.out.println("\nError code: MNC-Pouch\n" + e.getMessage());
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
				ViewClassController controller = new ViewClassController(selected, this);
				loader.setController(controller);
				
				Scene viewWindow = new Scene(loader.load());
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UTILITY);
				stage.setResizable(false);
				
				stage.setScene(viewWindow);
				stage.showAndWait();
				initializeClass();
				classTable.getSelectionModel().select(sIndex);
			}catch(IOException e) {
				System.out.println("\nError code: MNC-Satchel\n" + e.getMessage());
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
			
			NewTypeController controller = new NewTypeController(this);	
			loader.setController(controller);
			
			Scene addWindow = new Scene(loader.load());
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setResizable(false);
			
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
			System.out.println("\nError code: MNC-Fume\n" + e.getMessage());
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
				ViewTypeController controller = new ViewTypeController(selected, this);
				loader.setController(controller);
				
				Scene viewWindow = new Scene(loader.load());
				Stage stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initStyle(StageStyle.UTILITY);
				stage.setResizable(false);
				
				stage.setScene(viewWindow);
				stage.showAndWait();
				initializeClass();
				typeTable.getSelectionModel().select(sIndex);
			}catch(IOException e) {
				System.out.println("\nError code: MNC-Amor\n" + e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			System.out.println("\nError Code: Row not selected");
		}
	}
	//open view window on double click
	@FXML
	private void handleHomeClick() {
		TreeItem<Models.Task> item = (TreeItem<Models.Task>) homeTreeTable.getSelectionModel().getSelectedItem();
		Models.Task row = null;
		Object test = null;
		try {
			test = item.getValue();
		}catch(NullPointerException e) {
			//null pointer
		}
		
		if(test != null) {
			row = (Models.Task) item.getValue();
		}
	    if(row == null) return;
	    if(row != homeTemp){
	        homeTemp = row;
	        homeLastClickTime = new Date();
	    } else if(row == homeTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - homeLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	        	//execute
	    		//highlighted row in table
	    		int sIndex = homeTreeTable.getSelectionModel().getSelectedIndex();
	    		if(row != null) {
	    			try {
	    				FXMLLoader loader = new FXMLLoader(getClass().getResource("viewTask.fxml"));
	    				
	    				//pass in values from selected row
	    				ViewTaskController controller = new ViewTaskController(row, this);
	    				loader.setController(controller);
	    				
	    				Scene viewWindow = new Scene(loader.load());
	    				Stage stage = new Stage();
	    				stage.initModality(Modality.APPLICATION_MODAL);
	    				stage.initStyle(StageStyle.UTILITY);
	    				stage.setResizable(false);
	    				
	    				stage.setScene(viewWindow);
	    				stage.showAndWait();
	    				initializeHome();
	    				homeTreeTable.getSelectionModel().select(sIndex);
	    			}catch(IOException e) {
	    				System.out.println("\nError code: MNC-Slate\n" + e.getMessage());
	    				e.printStackTrace();
	    			}
	    		}
	    		//update labels in case anything changed
	    		initializeLabels();
	        } else {
	            homeLastClickTime = new Date();
	        }
	    }
	}
	@FXML
	private void handleTaskClick() {
		Models.Task row = taskTable.getSelectionModel().getSelectedItem();
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
	private void handleCellClicked(MouseEvent event) {
		Pane pane = (Pane) event.getSource();
		cellSelectedUpdate(pane);
	    if(pane == null) return;
	    if(pane != paneTemp){
	        paneTemp = pane;
	        cellLastClickTime = new Date();
	    } else if(pane == paneTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - cellLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	             taskViewCellClicked(pane);
	        } else {
	            cellLastClickTime = new Date();
	        }
	    }
	}
	@FXML
	private void homeButtonClicked() {
		resetHomeButtons();
		homeButton.setStyle(buttonSelectedColor);
		resetViews();
		initializeHome();
		homeView.setVisible(true);
	}
	@FXML
	private void calendarButtonClicked() {
		resetHomeButtons();
		calendarButton.setStyle(buttonSelectedColor);
		resetViews();
		
		//make calendar reset after clicking on different tab
		guicalendar = Calendar.getInstance();
		resetMonthButtons();
		resetPanes();
		calendarToday = Calendar.getInstance();
		
		initializeCalendar();
		calendarView.setVisible(true);
	}
	@FXML
	private void taskButtonClicked() {
		resetHomeButtons();
		taskButton.setStyle(buttonSelectedColor);
		resetViews();
		initializeTasks();
		taskView.setVisible(true);
	}
	@FXML
	private void classButtonClicked() {
		resetHomeButtons();
		classButton.setStyle(buttonSelectedColor);
		resetViews();
		initializeClass();
		classView.setVisible(true);
	}
	@FXML
	private void leftCalendarClicked() {
		guicalendar.add(Calendar.YEAR, -1);
		if(guicalendar.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)) {
			guicalendar.set(Calendar.MONTH, calendarToday.get(Calendar.MONTH));
		}
		else {
			guicalendar.set(Calendar.MONTH, 0);
		}
		resetMonthButtons();
		initializeCalendar();
		
		Date year = guicalendar.getTime();
		DateFormat sdf = new SimpleDateFormat("yyyy");   
	    yearLabel.setText(sdf.format(year));
	}
	@FXML
	private void rightCalendarClicked() {
		guicalendar.add(Calendar.YEAR, 1);
		if(guicalendar.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)) {
			guicalendar.set(Calendar.MONTH, calendarToday.get(Calendar.MONTH));
		}
		else {
			guicalendar.set(Calendar.MONTH, 0);
		}
		resetMonthButtons();
		initializeCalendar();
		
		Date year = guicalendar.getTime();
		DateFormat sdf = new SimpleDateFormat("yyyy");   
	    yearLabel.setText(sdf.format(year));
	}
	@FXML
	private void monthButtonClicked(ActionEvent event) {
		resetMonthButtons();
		Button btn = (Button) event.getSource();
		btn.setStyle(buttonSelectedColor);
		
		String month = btn.getId();
		switch(month) {
		case "january": guicalendar.set(Calendar.MONTH, 0);		break;
		case "february": guicalendar.set(Calendar.MONTH, 1);	break;
		case "march": guicalendar.set(Calendar.MONTH, 2);		break;
		case "april": guicalendar.set(Calendar.MONTH, 3);		break;
		case "may": guicalendar.set(Calendar.MONTH, 4);			break;
		case "june": guicalendar.set(Calendar.MONTH, 5);		break;
		case "july": guicalendar.set(Calendar.MONTH, 6);		break;
		case "august": guicalendar.set(Calendar.MONTH, 7);		break;
		case "september": guicalendar.set(Calendar.MONTH, 8);	break;
		case "october": guicalendar.set(Calendar.MONTH, 9);		break;
		case "november": guicalendar.set(Calendar.MONTH, 10);	break;
		case "december": guicalendar.set(Calendar.MONTH, 11);	break;
		default: guicalendar.set(Calendar.MONTH, 0);			break;
		}
		
		initializeCalendar();
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
	private void cellSelectedUpdate(Pane pane) {
		resetTextAreasStyle();
		TextArea textArea = null;
		int dateNum = -1;
		for(int count=0;count<paneList.size();count++) {
			if(paneList.get(count) == pane && pane.getOpacity() == 0.0) {
				dateNum = Integer.parseInt(labelList.get(count).getText());
				textArea = textList.get(count);
				break;
			}
		}
		if(testIfToday(dateNum) == false && pane.getOpacity() == 0.0) {
			textArea.setStyle(cellSelectedColor);
		}
	}
	//for dates in gui calendar on initialization
	private boolean testIfToday(int dateNum) {
		Calendar cal = (Calendar) guicalendar.clone();
		cal.set(Calendar.DATE, dateNum);
		
		if((cal.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)) &&
				(cal.get(Calendar.MONTH) == calendarToday.get(Calendar.MONTH)) &&
				(cal.get(Calendar.DATE) == calendarToday.get(Calendar.DATE))) {
			return true;
		}
		else {
			return false;
		}
	}
	private boolean generateCompleteBoolean(ArrayList<Models.Task> vals) {
		if(vals.size()==0) {
			return true;
		}
		else{
			for(int count=0;count<vals.size();count++) {
				if(vals.get(count).getFinishFlag() == false) {
					return false;
				}
			}
			return true;
		}
	}
	private String getFirstDayOfMonth() {
	    //cal.set(Calendar.DATE, cal.getInstance().get(Calendar.DATE));
	    //cal.set(Calendar.MONTH, cal.getInstance().get(Calendar.MONTH));
	    //cal.set(Calendar.YEAR, cal.getInstance().get(Calendar.YEAR));
	    Calendar cal = (Calendar) guicalendar.clone();
		cal.set(Calendar.DAY_OF_MONTH, 1);
	    
	    Date firstDayOfMonth = cal.getTime();
	    DateFormat sdf = new SimpleDateFormat("EEEEEEEE");   
	    return sdf.format(firstDayOfMonth);
	}
	private int getNumberOfDaysInMonth() {
		return guicalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	private int getToday() {
		return calendarToday.get(Calendar.DAY_OF_MONTH);
	}
	private int getDay() {
		return guicalendar.get(Calendar.DAY_OF_MONTH);
	}
	private int getMonth() {
		return guicalendar.get(Calendar.MONTH);
	}
	private int getYear() {
		return guicalendar.get(Calendar.YEAR);
	}
	public void displayConnectionTimeOut() {
		loadingPane.setVisible(true);
		
		javafx.concurrent.Task<Boolean> connectThread = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				Connection con = DBConn.getConnection();
				if(con != null) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return true;
				}
				return false;
			}
		};
		connectThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				boolean result = connectThread.getValue();
				if(result) {
					ModelControl.initializeUserData();
					
					if(homeView.isVisible()) {
						homeButton.fire();
					}
					else if(calendarView.isVisible()){
						calendarButton.fire();
					}
					else if(taskView.isVisible()) {
						taskButton.fire();
					}
					else if(classView.isVisible()) {
						classButton.fire();
					}
					
					loadingPane.setVisible(false);
				}
			}
		});
		
		javafx.concurrent.Task<Boolean> midlayer = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() {
				long timeoutTime = 30000;		//30 secs
				boolean success = false;
				
				while(success == false) {
					TimeOut t = new TimeOut(new Thread(connectThread), timeoutTime, true);
					try {                       
					  success = t.execute(); 	// Will return false if this times out, this freezes thread
					} catch (InterruptedException e) {}
					try {
						Thread.sleep(60000);	//try connect again in 60 secs
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		};
		
		new Thread(midlayer).start();
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
	private void resetTextAreasStyle() {
		for(int count=0;count<textList.size();count++) {
			if((paneList.get(count).getOpacity() == 0.0) &&
					(calendarToday.get(Calendar.DATE) != Integer.parseInt(labelList.get(count).getText()) ||
					calendarToday.get(Calendar.MONTH) != guicalendar.get(Calendar.MONTH) ||
					calendarToday.get(Calendar.YEAR) != guicalendar.get(Calendar.YEAR))) {
				textList.get(count).setStyle(null);
			}
		}
	}
	private void resetTextAreas() {
		for(int count=0;count<textList.size();count++) {
			textList.get(count).setText("");
			textList.get(count).setStyle(null);
			
			//disable scrollbar
			ScrollBar scrollbar = (ScrollBar) textList.get(count).lookup(".scroll-bar:vertical");
			scrollbar.setVisible(false);
		}
	}
	private void resetPanes() {
		for(int count=0;count<paneList.size();count++) {
			paneList.get(count).setOpacity(0.0);
		}
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
	private void createLists() {
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
	    
	    //pane lists
	    paneList.add(pane00);
	    paneList.add(pane10);
	    paneList.add(pane20);
	    paneList.add(pane30);
	    paneList.add(pane40);
	    paneList.add(pane50);
	    paneList.add(pane60);
	    
	    paneList.add(pane01);
	    paneList.add(pane11);
	    paneList.add(pane21);
	    paneList.add(pane31);
	    paneList.add(pane41);
	    paneList.add(pane51);
	    paneList.add(pane61);

	    paneList.add(pane02);
	    paneList.add(pane12);
	    paneList.add(pane22);
	    paneList.add(pane32);
	    paneList.add(pane42);
	    paneList.add(pane52);
	    paneList.add(pane62);
	    
	    paneList.add(pane03);
	    paneList.add(pane13);
	    paneList.add(pane23);
	    paneList.add(pane33);
	    paneList.add(pane43);
	    paneList.add(pane53);
	    paneList.add(pane63);
	    
	    paneList.add(pane04);
	    paneList.add(pane14);
	    paneList.add(pane24);
	    paneList.add(pane34);
	    paneList.add(pane44);
	    paneList.add(pane54);
	    paneList.add(pane64);
	    
	    paneList.add(pane05);
	    paneList.add(pane15);
	    paneList.add(pane25);
	    paneList.add(pane35);
	    paneList.add(pane45);
	    paneList.add(pane55);
	    paneList.add(pane65);
	    
	    //text area list
	    textList.add(text00);
	    textList.add(text10);
	    textList.add(text20);
	    textList.add(text30);
	    textList.add(text40);
	    textList.add(text50);
	    textList.add(text60);
	    
	    textList.add(text01);
	    textList.add(text11);
	    textList.add(text21);
	    textList.add(text31);
	    textList.add(text41);
	    textList.add(text51);
	    textList.add(text61);

	    textList.add(text02);
	    textList.add(text12);
	    textList.add(text22);
	    textList.add(text32);
	    textList.add(text42);
	    textList.add(text52);
	    textList.add(text62);
	    
	    textList.add(text03);
	    textList.add(text13);
	    textList.add(text23);
	    textList.add(text33);
	    textList.add(text43);
	    textList.add(text53);
	    textList.add(text63);
	    
	    textList.add(text04);
	    textList.add(text14);
	    textList.add(text24);
	    textList.add(text34);
	    textList.add(text44);
	    textList.add(text54);
	    textList.add(text64);
	    
	    textList.add(text05);
	    textList.add(text15);
	    textList.add(text25);
	    textList.add(text35);
	    textList.add(text45);
	    textList.add(text55);
	    textList.add(text65);
	}
}
