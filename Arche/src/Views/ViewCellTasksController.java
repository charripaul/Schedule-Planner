package Views;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXCheckBox;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tornadofx.control.DateTimePicker;

public class ViewCellTasksController implements Initializable{
	@FXML private JFXTabPane tabPane;
	@FXML private Tab tableTab, infoTab;
	
	//information tab
	@FXML private Label label;
	@FXML private TextField name;
	@FXML private ChoiceBox<String> className;
	@FXML private ChoiceBox<String> taskType;
	@FXML private TextArea description;
	@FXML private DateTimePicker dueDate, scheduledStartTime, scheduledEndTime;
	@FXML private JFXButton confirmInfo, cancelInfo;
	@FXML private TextField noticePeriod;
	@FXML private TextField hours, minutes;		//timeToComplete
	@FXML private JFXCheckBox completed;
	@FXML private Label warningLabel;
	
	//table tab
	@FXML private JFXButton confirmAll, cancelAll;
	@FXML private JFXButton add, edit, delete;
	@FXML private TableView<Task> newTasksTable;
	@FXML private TableColumn<Task, String> nameColumn, descriptionColumn;
	@FXML private TableColumn<Task, Boolean> scheduledColumn;
	
	private ObservableList<Task> viewableTaskList;
	private ArrayList<Task> taskList;		//for initialization
	private Task taskTemp;
	private Date taskLastClickTime;
	private boolean isOnAdd;			//test if got to confirmInfo from edit or add button
	private int sIndex;
	
	//ui loading
	@FXML public AnchorPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	//for registering changes made to tasks and confirm them once confirm button clicked
	private ArrayList<Task> addQueue = new ArrayList<Task>();
	private ArrayList<Task> updateQueue = new ArrayList<Task>();
	private ArrayList<Task> deleteQueue = new ArrayList<Task>();
	
	private MainNewController mainWindow;
	
	public ViewCellTasksController(ArrayList<Task> tasks, MainNewController mw) {
		taskList = tasks;
		viewableTaskList = FXCollections.observableArrayList();
		updateTaskList();
		mainWindow = mw;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		warningLabel.setVisible(false);
		
		dueDate.setFormat("MM/dd/yyyy hh:mm a");
		scheduledStartTime.setFormat("MM/dd/yyyy hh:mm a");
		scheduledEndTime.setFormat("MM/dd/yyyy hh:mm a");
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		scheduledColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isScheduled()));
		scheduledColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
		
		nameColumn.setStyle("-fx-alignment: CENTER;");
		descriptionColumn.setStyle("-fx-alignment: CENTER;");
		scheduledColumn.setStyle("-fx-alignment: CENTER;");
		
		//initialize choiceboxes
		ArrayList<Models.Class> allClasses = ModelControl.getClasses();
		ArrayList<TaskType> allTaskTypes = ModelControl.getTaskTypes();
		
		ObservableList<String> tts = FXCollections.observableArrayList();
		ObservableList<String> cls = FXCollections.observableArrayList();
		for(int count = 0;count<allTaskTypes.size();count++) {
			tts.add(allTaskTypes.get(count).getName());
		}
		for(int count = 0;count<allClasses.size();count++) {
			cls.add(allClasses.get(count).getAbbreviation());
		}
		taskType.getItems().clear();
		taskType.setItems(tts);
		className.getItems().clear();
		className.setItems(cls);
		
		//needs to be false in order for deleteButtonClicked method to work
		//as well as select indexing after completed operation
		nameColumn.setSortable(false);
		descriptionColumn.setSortable(false);
		scheduledColumn.setSortable(false);
		
		loadingPane.setVisible(false);
		initializeCloseEventProperty();
		setValidators();
		newTasksTable.setItems(viewableTaskList);
		resetTabPane();
	}
	@FXML
	private void addButtonClicked() {
		isOnAdd = true;
		tableTab.setDisable(true);
		infoTab.setDisable(false);
		infoTab.setText("Information");
		label.setText("New Task");
		tabPane.getSelectionModel().select(1);
	}
	@FXML
	private void editButtonClicked() {
		Task selected = newTasksTable.getSelectionModel().selectedItemProperty().get();
		sIndex = newTasksTable.getSelectionModel().getSelectedIndex();
		
		name.setText(selected.getName());
		description.setText(selected.getDescription());
		completed.setSelected(selected.getFinishFlag());
		
		noticePeriod.setText(Integer.toString(selected.getNoticePeriod()));
		int amountOfTime = selected.getTimeToComplete();
		hours.setText((amountOfTime/60)+"");
		minutes.setText((amountOfTime%60)+"");
		
		//set datetime picker format
		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(selected.getDueDate()), 
                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()); 
		dueDate.setDateTimeValue(dateTime);
		if(selected.isScheduled()) {
			dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(selected.getScheduledStartTime()), 
	                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()); 
			scheduledStartTime.setDateTimeValue(dateTime);
			dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(selected.getScheduledEndTime()), 
	                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()); 
			scheduledEndTime.setDateTimeValue(dateTime);
		}
		
		int classIndex = -1;
		int taskIndex = -1;
		ArrayList<Models.Class> allClasses = ModelControl.getClasses();
		ArrayList<TaskType> allTaskTypes = ModelControl.getTaskTypes();
		
		//find where in list our values are for default choicebox initialization
		for(int count = 0;count<allClasses.size();count++) {
			if(allClasses.get(count).getAbbreviation().equals(selected.getClassAbr())) {
				classIndex = count;
				break;
			}
		}
		for(int count = 0;count<allTaskTypes.size();count++) {
			if(allTaskTypes.get(count).getName().equals(selected.getType())) {
				taskIndex = count;
				break;
			}
		}
		//move names and abbreviations into observable lists
		ObservableList<String> tts = FXCollections.observableArrayList();
		ObservableList<String> cls = FXCollections.observableArrayList();
		for(int count = 0;count<allTaskTypes.size();count++) {
			tts.add(allTaskTypes.get(count).getName());
		}
		for(int count = 0;count<allClasses.size();count++) {
			cls.add(allClasses.get(count).getAbbreviation());
		}
		//fill choiceboxes
		taskType.getItems().clear();
		taskType.setItems(tts);
		className.getItems().clear();
		className.setItems(cls);
		//make default values the one we found earlier, which is one for passed in task
		taskType.getSelectionModel().select(taskIndex);
		className.getSelectionModel().select(classIndex);
		
		isOnAdd = false;
		tableTab.setDisable(true);
		infoTab.setDisable(false);
		infoTab.setText("Information");
		label.setText("Edit Task");
		tabPane.getSelectionModel().select(1);
	}
	@FXML
	private void deleteButtonClicked() {
		Task t = newTasksTable.getSelectionModel().getSelectedItem();
		taskList.remove(t);
		updateTaskList();
		newTasksTable.setItems(viewableTaskList);
		deleteQueue.add(t);
	}
	@FXML
	private void confirmAllButtonClicked() {
		loadingText.setText("Updating");
		loadingPane.setVisible(true);
		
		javafx.concurrent.Task<Boolean> changeThread = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				
				for(int count=0;count<addQueue.size();count++) {
					ModelControl.addTask(addQueue.get(count));
				}
				for(int count=0;count<updateQueue.size();count++) {
					ModelControl.updateTask(updateQueue.get(count));
				}
				for(int count=0;count<deleteQueue.size();count++) {
					ModelControl.deleteTask(deleteQueue.get(count));
				}
				
				return true;
			}
		};
		changeThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				boolean result = changeThread.getValue();
				if(result) {
					closeWindow();
				}
			}
		});
		
		javafx.concurrent.Task<Boolean> midlayer = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() {
				long timeoutTime = 15000;		//15 secs
				TimeOut t = new TimeOut(new Thread(changeThread), timeoutTime, true);
				try {                       
				  boolean success = t.execute(); // Will return false if this times out, this freezes thread
				  return success;
				} catch (InterruptedException e) {}
				return false;
			}
		};
		midlayer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				boolean result = midlayer.getValue();
				if(!result) {
					//display loading pane on main window
					mainWindow.displayConnectionTimeOut();
					closeWindow();
				}
			}
		});
		
		new Thread(midlayer).start();
	}
	@FXML
	private void cancelAllButtonClicked() {
		closeWindow();
	}
	@FXML
	private void confirmInfoButtonClicked() {
		loadingText.setText("Validating");
		loadingPane.setVisible(true);
		
		javafx.concurrent.Task<Boolean> changeThread = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				//validate input data
				if(checkValidation()) {
					Task t;
					
					if(isOnAdd) {				//got here from addButton
						int ttc = (Integer.parseInt(hours.getText())*60) + Integer.parseInt(minutes.getText());
						t = new Task(ModelControl.mainUID, name.getText(), description.getText(),
								dueDate.getDateTimeValue().atZone(ZoneId.
								systemDefault()).toInstant().toEpochMilli(),
								false, taskType.getSelectionModel().getSelectedItem(),
								className.getSelectionModel().getSelectedItem(),
								Integer.parseInt(noticePeriod.getText()), ttc);
						if(scheduledStartTime.getDateTimeValue() != null && scheduledStartTime.getDateTimeValue() != null) {
							t.setScheduledStartTime(scheduledStartTime.getDateTimeValue().atZone(ZoneId.
									systemDefault()).toInstant().toEpochMilli());
							t.setScheduledEndTime(scheduledEndTime.getDateTimeValue().atZone(ZoneId.
									systemDefault()).toInstant().toEpochMilli());
						}
						taskList.add(t);
						updateTaskList();
						newTasksTable.setItems(viewableTaskList);
						newTasksTable.getSelectionModel().select(newTasksTable.getItems().size());
						addQueue.add(t);
					}
					else {						//got here from editButton
						t = viewableTaskList.get(newTasksTable.getSelectionModel().getSelectedIndex());		//TODO: check
						
						t.setName(name.getText());
						t.setType(taskType.getSelectionModel().getSelectedItem());
						t.setClassAbr(className.getSelectionModel().getSelectedItem());
						t.setDueDate(dueDate.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
						t.setDescription(description.getText());
						t.setFinishFlag(completed.isSelected());
						int amountOfTime = (Integer.parseInt(hours.getText())*60) + Integer.parseInt(minutes.getText());
						t.setTimeToComplete(amountOfTime);
						t.setNoticePeriod(Integer.parseInt(noticePeriod.getText()));
						if(scheduledStartTime.getDateTimeValue() != null || scheduledStartTime.getDateTimeValue() != null) {
							t.setScheduledStartTime(scheduledStartTime.getDateTimeValue().atZone(ZoneId.
									systemDefault()).toInstant().toEpochMilli());
							t.setScheduledEndTime(scheduledEndTime.getDateTimeValue().atZone(ZoneId.
									systemDefault()).toInstant().toEpochMilli());
						}
						updateTaskList();
						newTasksTable.setItems(viewableTaskList);
						newTasksTable.getSelectionModel().select(sIndex);
						updateQueue.add(t);
					}
					return true;
				}
				else {
					return false;
				}
			}
		};
		changeThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				boolean result = changeThread.getValue();
				if(result) {
					resetTabPane();
					loadingPane.setVisible(false);
				}
				else {
					loadingPane.setVisible(false);
					displayWarningLabel("Please fix input errors");
				}
			}
		});
		
		new Thread(changeThread).start();
	}
	@FXML
	private void cancelInfoButtonClicked() {
		resetTabPane();
	}
	//open view window on double click
	@FXML
	private void handleTaskClick() {
	    Task row = newTasksTable.getSelectionModel().getSelectedItem();
	    if(row == null) return;
	    if(row != taskTemp){
	        taskTemp = row;
	        taskLastClickTime = new Date();
	    } else if(row == taskTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - taskLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	             editButtonClicked();
	        } else {
	            taskLastClickTime = new Date();
	        }
	    }
	}
	private void setValidators() {
		name.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!name.getText().matches("^[a-zA-Z0-9\\-_]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Name text invalid");
	            }
	            else if(name.getText().length() > 45) {
	            	displayWarningLabel("Name too long: Keep under 45 characters");
	            }
	            else if(name.getText().isEmpty()) {
	            	displayWarningLabel("Please enter a name");
	            }
	        }
	    });
		description.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!description.getText().matches("^[\\s\\w\\d\\?><;,\\{\\}\\[\\]\\-_\\+=!@\\#\\$%^&\\*\\|\\']*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Description text invalid");
	            }
	            else if(description.getText().length() > 255) {
	            	displayWarningLabel("Description text too long: Keep under 255 characters");
	            }
	        }
	    });
		noticePeriod.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!noticePeriod.getText().matches("^[0-9]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Notice Period characters invalid");
	            }
	            else if(noticePeriod.getText().length() > 9) {
	            	displayWarningLabel("Notice Period value too large: Keep under 1,000,000,000");
	            }
	            else if(Integer.parseInt(noticePeriod.getText()) <= 0) {
	            	displayWarningLabel("Notice Period value must be positive");
	            }
	            else if(noticePeriod.getText().isEmpty()) {
	            	displayWarningLabel("Please enter a Notice Period");
	            }
	        }
	    });
		hours.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!hours.getText().matches("^[0-9]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Hour characters invalid");
	            }
	            else if(hours.getText().length() > 9) {
	            	displayWarningLabel("Hour value too large: Keep under 1,000,000,000");
	            }
	            else if(Integer.parseInt(hours.getText()) < 0) {
	            	displayWarningLabel("Hour value must be positive");
	            }
	            else if(hours.getText().isEmpty()) {
	            	displayWarningLabel("Please enter an hour value");
	            }
	        }
	    });
		minutes.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!minutes.getText().matches("^[0-9]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Minute characters invalid");
	            }
	            else if(minutes.getText().length() > 9) {
	            	displayWarningLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minutes.getText()) > 59) {
	            	displayWarningLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minutes.getText()) <= 0) {
	            	displayWarningLabel("Minute value must be positive");
	            }
	            else if(minutes.getText().isEmpty()) {
	            	displayWarningLabel("Please enter a minute value");
	            }
	        }
	    });
	}
	private boolean checkValidation() {
		if(!name.getText().matches("^[a-zA-Z0-9\\-_]*$")){
            //when it doesn't match the pattern
            //set the textField empty
            return false;
        }
        else if(name.getText().length() > 45) {
        	return false;
        }
        else if(name.getText().isEmpty()) {
        	return false;
        }
		
		if(!description.getText().matches("^[\\s\\w\\d\\?><;,\\{\\}\\[\\]\\-_\\+=!@\\#\\$%^&\\*\\|\\']*$")){
            //when it doesn't match the pattern
            //set the textField empty
			return false;
        }
        else if(description.getText().length() > 255) {
        	return false;
        }
		
		if(!noticePeriod.getText().matches("^[0-9]*$")){
            //when it doesn't match the pattern
            //set the textField empty
			return false;
        }
        else if(noticePeriod.getText().length() > 9) {
        	return false;
        }
        else if(Integer.parseInt(noticePeriod.getText()) <= 0) {
        	return false;
        }
        else if(noticePeriod.getText().isEmpty()) {
        	return false;
        }
		
		if(!hours.getText().matches("^[0-9]*$")){
            //when it doesn't match the pattern
            //set the textField empty
			return false;
        }
        else if(hours.getText().length() > 9) {
        	return false;
        }
        else if(Integer.parseInt(hours.getText()) < 0) {
        	return false;
        }
        else if(hours.getText().isEmpty()) {
        	return false;
        }
		
		if(!minutes.getText().matches("^[0-9]*$")){
            //when it doesn't match the pattern
            //set the textField empty
			return false;
        }
        else if(minutes.getText().length() > 9) {
        	return false;
        }
        else if(Integer.parseInt(minutes.getText()) > 59) {
        	return false;
        }
        else if(Integer.parseInt(minutes.getText()) <= 0) {
        	return false;
        }
        else if(minutes.getText().isEmpty()) {
        	return false;
        }
		return true;
	}
	private void displayWarningLabel(String s) {
		warningLabel.setText(s);
		warningLabel.setVisible(true);
		PauseTransition visiblePause = new PauseTransition(
		        Duration.seconds(3)
		);
		visiblePause.setOnFinished(
		        event -> warningLabel.setVisible(false)
		);
		visiblePause.play();
	}
	@FXML
	private void setCloseEvent() {
		Stage window = (Stage) name.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without updating your tasks?"));
		});
	}
	private void initializeCloseEventProperty() {
		viewableTaskList.addListener(new ListChangeListener<Task>(){
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Task> pChange) {
                while(pChange.next()) {
                    setCloseEvent();
                }
            }
        });
		taskType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	        	ArrayList<TaskType> types = ModelControl.getTaskTypes();
	        	//update eta based on the selected task type in combobox
	        	for(int count=0;count<types.size();count++) {
	        		int selectIndex = (Integer) number2;
	        		if(selectIndex != -1 && types.get(count).getName().equals(taskType.getItems().get(selectIndex))) {
	        			int amountOfTime = types.get(count).getTimeToComplete();
	        			hours.setText((amountOfTime/60)+"");
	        			minutes.setText((amountOfTime%60)+"");
	        			noticePeriod.setText(types.get(count).getWarningPeriod()+"");
	        			break;
	        		}
	        	}
	        }
	    });
	}
	private void updateTaskList() {
		viewableTaskList.clear();
		for(int count=0;count<taskList.size();count++) {
			viewableTaskList.add(taskList.get(count));
		}
	}
	private void resetTabPane() {
		tabPane.getSelectionModel().select(0);
		tableTab.setDisable(false);
		infoTab.setDisable(true);
		infoTab.setText("");
		name.setText("");
		className.getSelectionModel().clearSelection();
		taskType.getSelectionModel().clearSelection();
		description.setText("");
		dueDate.setDateTimeValue(null);
		noticePeriod.setText("");
		hours.setText("");
		minutes.setText("");
		scheduledStartTime.setDateTimeValue(null);
		scheduledEndTime.setDateTimeValue(null);
	}
	private void closeWindow(boolean answer) {
		if(answer == true) {
			if(loadingPane.isVisible()) {
				mainWindow.displayConnectionTimeOut();
			}
			closeWindow();
		}
	}
	private void closeWindow() {
		Stage stage = (Stage) confirmAll.getScene().getWindow();
		stage.close();
	}
}
