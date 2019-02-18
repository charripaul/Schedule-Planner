package Views;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTabPane;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import tornadofx.control.DateTimePicker;

//parent for any controllers for layouts that can display multiple tasks
public abstract class ParentSpecialTaskController extends ParentNormalTaskController{
	@FXML protected JFXTabPane tabPane;
	@FXML protected Tab tableTab, infoTab;
	
	//table tab
	@FXML protected JFXButton saveButton, cancelAll;
	@FXML protected JFXButton addButton, editButton, deleteButton;
	@FXML protected TableView<Task> newTasksTable;
	@FXML protected TableColumn<Task, String> nameColumn, descriptionColumn;
	@FXML protected TableColumn<Task, Boolean> scheduledColumn;
	
	protected ObservableList<Task> viewableTaskList;
	protected ArrayList<Task> taskList;		//for initialization
	protected Task taskTemp;
	protected Date taskLastClickTime;
	protected boolean fromAdd;			//test if got to confirmInfo from edit or add button
	protected int sIndex;
	
	protected abstract boolean addTask();
	
	public ParentSpecialTaskController(MainNewController mw) {
		super(mw);
	}
	
	protected void initialize() {
		alertLabel.setVisible(false);
		initializeDateTimePickers();
		initializeColumns();
		initializeChoiceBoxes();
		disableLoadingOverlay();
		enableCloseEventProperty();
		enableVisualValidation();
		resetTabPane();
	}
	
	protected void initializeColumns() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		scheduledColumn.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isScheduled()));
		scheduledColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
		
		nameColumn.setStyle("-fx-alignment: CENTER;");
		descriptionColumn.setStyle("-fx-alignment: CENTER;");
		scheduledColumn.setStyle("-fx-alignment: CENTER;");
		
		//needs to be false in order for deleteButtonClicked method to work
		//as well as select indexing after completed operation
		nameColumn.setSortable(false);
		descriptionColumn.setSortable(false);
		scheduledColumn.setSortable(false);
	}
	
	@FXML
	protected void confirmAllButtonClicked() {
		enableLoadingOverlay("Updating");
		computeUpdateOperations();
	}
	
	protected void computeUpdateOperations() {
		javafx.concurrent.Task<Boolean> updateThread = createUpdateThread();
		javafx.concurrent.Task<Boolean> timingThread = createTimedThread(updateThread);
		
		new Thread(timingThread).start();
	}
	
	protected javafx.concurrent.Task<Boolean> createUpdateThread() {
		javafx.concurrent.Task<Boolean> updateThread = new javafx.concurrent.Task<Boolean>() {
		    @Override
		    public Boolean call() throws InterruptedException{
		    	return addTask();
		    }
		};
		
		//reverts to javafx main app thread
		updateThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        boolean result = updateThread.getValue(); 						//result of computation
		        if(result) {
					closeWindow();
				}
		    }
		});
		
		return updateThread;
	}
	
	@FXML
	protected void confirmInfoButtonClicked() {
		enableLoadingOverlay("Validating");
		computeChangeOperations();
	}
	
	protected void computeChangeOperations() {
		javafx.concurrent.Task<Boolean> changeThread = createChangeThread();
		javafx.concurrent.Task<Boolean> timingThread = createTimedThread(changeThread);
		new Thread(timingThread).start();
	}
	
	protected javafx.concurrent.Task<Boolean> createChangeThread(){
		javafx.concurrent.Task<Boolean> changeThread = new javafx.concurrent.Task<Boolean>() {
		    @Override
		    public Boolean call() throws InterruptedException{
		    	return changeTask();
		    }
		};
		
		changeThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        boolean result = changeThread.getValue(); 						//result of computation
		        computeChangeResult(result);
		    }
		});
		
		return changeThread;
	}
	
	protected void computeChangeResult(boolean result) {
		if(result) {
			resetTabPane();
			disableLoadingOverlay();
		}
		else {
			disableLoadingOverlay();
			displayAlertLabel("Please fix input errors");
		}
	}
	
	@FXML
	protected void addButtonClicked() {
		selectInfoTab("New Task", true);
	}
	
	@FXML
	protected void editButtonClicked() {
		Task selected = newTasksTable.getSelectionModel().selectedItemProperty().get();
		sIndex = newTasksTable.getSelectionModel().getSelectedIndex();
		
		fillInfo(selected);
		selectChoiceBox(selected);
		
		selectInfoTab("Edit Task", false);
	}
	
	private void selectInfoTab(String title, boolean fa) {
		fromAdd = fa;
		tableTab.setDisable(true);
		infoTab.setDisable(false);
		infoTab.setText("Information");
		label.setText(title);
		tabPane.getSelectionModel().select(1);
	}
	
	@FXML
	protected void cancelAllButtonClicked() {
		closeWindow();
	}
	
	//open view window on double click
	@FXML
	protected void handleTaskClick() {
	    Task row = newTasksTable.getSelectionModel().getSelectedItem();
	    if(row == null) return;
	    if(row != taskTemp){
	        taskTemp = row;
	        taskLastClickTime = new Date();
	    } else if(row == taskTemp) {
	        Date now = new Date();
	        long diff = now.getTime() - taskLastClickTime.getTime();
	        if (diff < 300){ //another click registered in 300 millis
	        	editButton.fire();
	        } else {
	            taskLastClickTime = new Date();
	        }
	    }
	}
	
	@FXML
	protected void deleteButtonClicked() {
		Task t = newTasksTable.getSelectionModel().getSelectedItem();
		taskList.remove(t);
		updateTaskList();
		newTasksTable.setItems(viewableTaskList);
	}
	
	protected boolean changeTask() {
		if(validate()) {
			Task t = null;
			
			if(fromAdd) {				//got here from addButton
				fromAdd(t);
			}
			else {						//got here from editButton
				fromEdit(t);
			}
			return true;
		}
		return false;
	}
	
	protected void fromAdd(Task t) {
		int ttc = (Integer.parseInt(hours.getText())*60) + Integer.parseInt(minutes.getText());
		
		t = new Task(
				ModelControl.mainUID,
				name.getText(),
				description.getText(),
				dueDate.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
				false,
				taskTypes.getSelectionModel().getSelectedItem(),
				classAbrs.getSelectionModel().getSelectedItem(),
				Integer.parseInt(noticePeriod.getText()),
				ttc
		);
		
		if(scheduledStartTime.getDateTimeValue() != null && scheduledStartTime.getDateTimeValue() != null) {
			t.setScheduledStartTime(
					scheduledStartTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
			);
			t.setScheduledEndTime(
					scheduledEndTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
			);
		}
		
		taskList.add(t);
		updateTaskList();
		newTasksTable.setItems(viewableTaskList);
		newTasksTable.getSelectionModel().select(newTasksTable.getItems().size());
	}
	
	protected void fromEdit(Task t) {
		t = viewableTaskList.get(newTasksTable.getSelectionModel().getSelectedIndex());		//TODO: check
		
		t.setName(name.getText());
		t.setType(taskTypes.getSelectionModel().getSelectedItem());
		t.setClassAbr(classAbrs.getSelectionModel().getSelectedItem());
		t.setDueDate(dueDate.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		t.setDescription(description.getText());
		t.setFinishFlag(completed.isSelected());
		t.setNoticePeriod(Integer.parseInt(noticePeriod.getText()));
		
		int amountOfTime = (Integer.parseInt(hours.getText())*60) + Integer.parseInt(minutes.getText());
		t.setTimeToComplete(amountOfTime);
		
		if(scheduledStartTime.getDateTimeValue() != null || scheduledStartTime.getDateTimeValue() != null) {
			t.setScheduledStartTime(
					scheduledStartTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
			);
			t.setScheduledEndTime(
					scheduledEndTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
			);
		}
		
		updateTaskList();
		newTasksTable.setItems(viewableTaskList);
		newTasksTable.getSelectionModel().select(sIndex);
	}
	
	protected void enableCloseEventProperty() {
		viewableTaskList.addListener(new ListChangeListener<Task>(){
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Task> pChange) {
                while(pChange.next()) {
                    setCloseEvent();
                }
            }
        });
		
		taskTypes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	        	ArrayList<TaskType> types = ModelControl.getTaskTypes();
	        	
	        	//update eta based on the selected task type in combobox
	        	for(int count=0;count<types.size();count++) {
	        		int selectIndex = (Integer) number2;
	        		if(selectIndex != -1 && types.get(count).getName().equals(taskTypes.getItems().get(selectIndex))) {
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
	
	protected boolean validate() {
		if(!name.getText().matches(NORMAL_TEXT_REGEX)){
            return false;
        }
        else if(name.getText().length() > NAME_MAX_LENGTH) {
        	return false;
        }
        else if(name.getText().isEmpty()) {
        	return false;
        }
		
		if(!description.getText().matches(SPECIAL_TEXT_REGEX)){
			return false;
        }
        else if(description.getText().length() > TEXTAREA_MAX_LENGTH) {
        	return false;
        }
		
		if(!noticePeriod.getText().matches(NUMBER_REGEX)){
			return false;
        }
        else if(noticePeriod.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(noticePeriod.getText().isEmpty()) {
        	return false;
        }
        else if(Integer.parseInt(noticePeriod.getText()) <= 0) {
        	return false;
        }
        
		
		if(!hours.getText().matches(NUMBER_REGEX)){
			return false;
        }
        else if(hours.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(hours.getText().isEmpty()) {
        	return false;
        }
        else if(Integer.parseInt(hours.getText()) < 0) {
        	return false;
        }
		
		if(!minutes.getText().matches(NUMBER_REGEX)){
			return false;
        }
        else if(minutes.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(minutes.getText().isEmpty()) {
        	return false;
        }
        else if(Integer.parseInt(minutes.getText()) > 59) {
        	return false;
        }
        else if(Integer.parseInt(minutes.getText()) < 0) {
        	return false;
        }
		
        if(classAbrs.getSelectionModel().isEmpty()) {
        	return false;
        }
        if(taskTypes.getSelectionModel().isEmpty()) {
        	return false;
        }
        if(dueDate.getValue() == null){
            return false;
        }
		return true;
	}
	
	protected void enableVisualValidation() {
		name.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!name.getText().matches(NORMAL_TEXT_REGEX)){
	                displayAlertLabel("Name text invalid");
	            }
	            else if(name.getText().length() > NAME_MAX_LENGTH) {
	            	displayAlertLabel("Name too long: Keep under 45 characters");
	            }
	            else if(name.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a name");
	            }
	        }
	    });
		
		description.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!description.getText().matches(SPECIAL_TEXT_REGEX)){
	                displayAlertLabel("Description text invalid");
	            }
	            else if(description.getText().length() > TEXTAREA_MAX_LENGTH) {
	            	displayAlertLabel("Description text too long: Keep under 255 characters");
	            }
	        }
	    });
		
		noticePeriod.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!noticePeriod.getText().matches(NUMBER_REGEX)){
	                displayAlertLabel("Notice Period characters invalid");
	            }
	            else if(noticePeriod.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Notice Period value too large: Keep under 1,000,000,000");
	            }
	            else if(noticePeriod.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a Notice Period");
	            }
	            else if(Integer.parseInt(noticePeriod.getText()) <= 0) {
	            	displayAlertLabel("Notice Period value must be positive");
	            }
	        }
	    });
		
		hours.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!hours.getText().matches(NUMBER_REGEX)){
	                displayAlertLabel("Hour characters invalid");
	            }
	            else if(hours.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Hour value too large: Keep under 1,000,000,000");
	            }
	            else if(hours.getText().isEmpty()) {
	            	displayAlertLabel("Please enter an hour value");
	            }
	            else if(Integer.parseInt(hours.getText()) < 0) {
	            	displayAlertLabel("Hour value must be positive");
	            }
	        }
	    });
		
		minutes.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!minutes.getText().matches(NUMBER_REGEX)){
	                displayAlertLabel("Minute characters invalid");
	            }
	            else if(minutes.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Minute value too large: Keep under 60");
	            }
	            else if(minutes.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a minute value");
	            }
	            else if(Integer.parseInt(minutes.getText()) > 59) {
	            	displayAlertLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minutes.getText()) < 0) {
	            	displayAlertLabel("Minute value must be positive");
	            }
	        }
	    });
		
		classAbrs.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(classAbrs.getSelectionModel().isEmpty()){
	                displayAlertLabel("Please choose a class");
	            }
	        }
	    });
		
		taskTypes.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	        	if(taskTypes.getSelectionModel().isEmpty()){
	                displayAlertLabel("Please choose a task type");
	            }
	        }
	    });
		
		dueDate.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	        	if(dueDate.getValue() == null){
	                displayAlertLabel("Please set a due date");
	            }
	        }
	    });
	}
	
	@FXML
	private void cancelInfoButtonClicked() {
		resetTabPane();
	}
	
	protected void updateTaskList() {
		viewableTaskList.clear();
		for(int count=0;count<taskList.size();count++) {
			viewableTaskList.add(taskList.get(count));
		}
	}
	
	//move back to table tab, and clear info tab
	protected void resetTabPane() {
		tabPane.getSelectionModel().select(0);
		tableTab.setDisable(false);
		infoTab.setDisable(true);
		infoTab.setText("");
		
		name.setText("");
		classAbrs.getSelectionModel().clearSelection();
		taskTypes.getSelectionModel().clearSelection();
		description.setText("");
		dueDate.setDateTimeValue(null);
		noticePeriod.setText("");
		hours.setText("");
		minutes.setText("");
		
		scheduledStartTime.setDateTimeValue(null);
		scheduledEndTime.setDateTimeValue(null);
	}
}
