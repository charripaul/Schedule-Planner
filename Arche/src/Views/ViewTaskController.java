package Views;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tornadofx.control.DateTimePicker;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;


public class ViewTaskController extends ParentNormalTaskController{
	@FXML private JFXButton deleteButton;
	@FXML private JFXButton saveCloseButton;
	
	private final Task dataHolder;
	
	public ViewTaskController(Task t, MainNewController mw) {
		super(mw);
		dataHolder = t;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		alertLabel.setVisible(false);
		initializeData(dataHolder);
		disableLoadingOverlay();
		enableCloseEventProperty();
		enableVisualValidation();
	}
	
	private void initializeData(Task selected) {
		initializeDateTimePickers();
		initializeChoiceBoxes();
		selectChoiceBox(selected);
		fillInfo(selected);
	}
	
	@FXML
	private void deleteButtonClicked() {
		if(ConfirmExitView.display("Are you sure you want to delete this task?")) {
			ModelControl.deleteTask(dataHolder);
			closeWindow();
		}
	}
	
	@FXML
	private void closeButtonClicked() {
		if(saveCloseButton.getText().equalsIgnoreCase("close")) {
			closeWindow();
		}
		else {
			enableLoadingOverlay("Updating");
			computeUpdateOperations();
		}
	}
	
	private void computeUpdateOperations() {
		javafx.concurrent.Task<Boolean> updateThread = createUpdateThread();
		javafx.concurrent.Task<Boolean> timedThread = createTimedThread(updateThread);
		new Thread(timedThread).start();
	}
	
	private javafx.concurrent.Task<Boolean> createUpdateThread(){
		javafx.concurrent.Task<Boolean> updateThread = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				return updateTask();
			}
		};
		
		updateThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				boolean result = updateThread.getValue();
				computeUpdateResult(result);
			}
		});
		
		return updateThread;
	}
	
	private boolean updateTask() {
		if(validate()) {
			dataHolder.setName(name.getText());
			dataHolder.setType(taskTypes.getSelectionModel().getSelectedItem());
			dataHolder.setClassAbr(classAbrs.getSelectionModel().getSelectedItem());
			dataHolder.setDueDate(dueDate.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			dataHolder.setScheduledStartTime((scheduledStartTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
			dataHolder.setScheduledEndTime(scheduledEndTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			dataHolder.setDescription(description.getText());
			dataHolder.setFinishFlag(completed.isSelected());
			dataHolder.setNoticePeriod(Integer.parseInt(noticePeriod.getText()));
			
			int amountOfTime = (Integer.parseInt(hours.getText())*60) + Integer.parseInt(minutes.getText());
			dataHolder.setTimeToComplete(amountOfTime);
			
			ModelControl.updateTask(dataHolder);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private void computeUpdateResult(boolean result) {
		if(result) {
			closeWindow();
		}
		else {
			loadingOverlay.setVisible(false);
			displayAlertLabel("Please fix input errors");
		}
	}
	
	private void setCloseButtonText(String text) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                saveCloseButton.setText(text);
            }
        });
	}
	
	//detect change, ask for save changes on close
	protected void enableCloseEventProperty() {
		name.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
		
	    taskTypes.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	        	setCloseEvent();
	        	ArrayList<TaskType> types = ModelControl.getTaskTypes();
	        	//make changes based on the selected type in combobox
	        	for(int count=0;count<types.size();count++) {
	        		if(types.get(count).getName().equals(taskTypes.getItems().get((Integer) number2))) {
	        			int amountOfTime = types.get(count).getTimeToComplete();
	        			hours.setText((amountOfTime/60)+"");
	        			minutes.setText((amountOfTime%60)+"");
	        			noticePeriod.setText(types.get(count).getWarningPeriod()+"");
	        			break;
	        		}
	        	}
	        }
	    });
	    
	    classAbrs.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	        	setCloseEvent();
	        }
	    });
	    
	    dueDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
	    
	    scheduledStartTime.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
	    
	    scheduledEndTime.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
	    
	    description.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	    
	    completed.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
	    
	    noticePeriod.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	    
	    hours.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	    
	    minutes.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	}
	//validate due date, choiceboxes
	protected void enableVisualValidation() {
		name.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!name.getText().matches(NORMAL_TEXT_REGEX)){
	                displayAlertLabel("Name text invalid");
	            }
	            else if(name.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a name");
	            }
	            else if(name.getText().length() > NAME_MAX_LENGTH) {
	            	displayAlertLabel("Name too long: Keep under 45 characters");
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
	            else if(noticePeriod.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a Notice Period");
	            }
	            else if(noticePeriod.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Notice Period value too large: Keep under 1,000,000,000");
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
	            else if(hours.getText().isEmpty()) {
	            	displayAlertLabel("Please enter an hour value");
	            }
	            else if(hours.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Hour value too large: Keep under 1,000,000,000");
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
	            else if(minutes.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a minute value");
	            }
	            else if(minutes.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Minute value too large: Keep under 60");
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
		else if(noticePeriod.getText().isEmpty()) {
        	return false;
        }
        else if(noticePeriod.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(Integer.parseInt(noticePeriod.getText()) <= 0) {
        	return false;
        }
		
		if(!hours.getText().matches(NUMBER_REGEX)){
			return false;
        }
		else if(hours.getText().isEmpty()) {
        	return false;
        }
        else if(hours.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(Integer.parseInt(hours.getText()) < 0) {
        	return false;
        }
		
		if(!minutes.getText().matches(NUMBER_REGEX)){
			return false;
        }
		else if(minutes.getText().isEmpty()) {
        	return false;
        }
        else if(minutes.getText().length() > NUMBER_MAX_LENGTH) {
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
	
	protected void setCloseEvent() {
		super.setCloseEvent();
		setCloseButtonText("Save");
	}
}
