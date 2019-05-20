package Views;


import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import Models.ModelControl;
import Models.TaskType;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ViewTypeController extends ParentController{
	@FXML private TextField name;
	@FXML private TextField noticePeriod;
	@FXML private TextField hour, minute;		//timeToComplete
	@FXML private TextArea description;
	@FXML private JFXButton deleteButton, saveButton;
	@FXML private Label warningLabel, totalAssignments;
	
	private final TaskType dataHolder;
	private String oldTypeName, newTypeName;
	
	public ViewTypeController(TaskType tt, MainNewController mw) {
		super(mw);
		dataHolder = tt;
		oldTypeName = dataHolder.getName();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(dataHolder);
		disableLoadingOverlay();
		enableCloseEventProperty();
		enableVisualValidation();
	}
	
	private void initializeData(TaskType tt) {
		name.setText(dataHolder.getName());
		noticePeriod.setText(tt.getWarningPeriod()+"");
		description.setText(tt.getDescription());
		int amountOfTime = tt.getTimeToComplete();
		hour.setText((amountOfTime/60)+"");
		minute.setText((amountOfTime%60)+"");
		totalAssignments.setText(tt.getTotalAssignments()+"");
		
		warningLabel.setVisible(false);
	}
	
	@FXML
	private void saveButtonClicked() {
		enableLoadingOverlay("Updating");
		computeUpdateOperations();
	}
	
	private void computeUpdateOperations() {
		Task<Boolean> updateThread = createUpdateThread();
		Task<Boolean> timingThread = createTimedThread(updateThread);
		
		new Thread(timingThread).start();
	}
	
	private Task<Boolean> createUpdateThread(){
		Task<Boolean> changeThread = new Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				return updateType();
			}
		};
		
		changeThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				boolean result = changeThread.getValue();
				computeUpdateResult(result);
			}
		});
		
		return changeThread;
	}
	
	private boolean updateType() {
		//validate input data
		if(validate()) {
			dataHolder.setName(name.getText());
			dataHolder.setWarningPeriod(Integer.parseInt(noticePeriod.getText()));
			dataHolder.setDescription(description.getText());
			int amountOfTime = (Integer.parseInt(hour.getText())*60) + Integer.parseInt(minute.getText());
			dataHolder.setTimeToComplete(amountOfTime);
			
			newTypeName = dataHolder.getName();
			ModelControl.updateTaskTypeAndTaskDependency(dataHolder, oldTypeName, newTypeName);
			
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
			disableLoadingOverlay();
			displayAlertLabel("Please fix input errors");
		}
	}
	
	@FXML
	private void deleteButtonClicked() {
		if(ModelControl.isBeingUsed(dataHolder)) {
			displayAlertLabel("Cannot delete Task Type: Already in use by Task");
		}else {
			if(ConfirmExitView.display("Are you sure you want to delete this task type?")) {
				ModelControl.deleteTaskType(dataHolder);
				closeWindow();
			}
		}
	}
	
	@FXML
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			saveButton.fire();
		}
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
		
		noticePeriod.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
		
		hour.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
		
		minute.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
		
		description.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
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
		
		hour.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!hour.getText().matches(NUMBER_REGEX)){
	                displayAlertLabel("Hour characters invalid");
	            }
	            else if(hour.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Hour value too large: Keep under 1,000,000,000");
	            }
	            else if(Integer.parseInt(hour.getText()) < 0) {
	            	displayAlertLabel("Hour value must be positive");
	            }
	            else if(hour.getText().isEmpty()) {
	            	displayAlertLabel("Please enter an hour value");
	            }
	        }
	    });
		
		minute.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!minute.getText().matches(NUMBER_REGEX)){
	                displayAlertLabel("Minute characters invalid");
	            }
	            else if(minute.getText().length() > NUMBER_MAX_LENGTH) {
	            	displayAlertLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minute.getText()) > 59) {
	            	displayAlertLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minute.getText()) < 0) {
	            	displayAlertLabel("Minute value must be positive");
	            }
	            else if(minute.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a minute value");
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
		
		if(!noticePeriod.getText().matches(NUMBER_REGEX)){
			return false;
        }
        else if(noticePeriod.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(Integer.parseInt(noticePeriod.getText()) <= 0) {
        	return false;
        }
        else if(noticePeriod.getText().isEmpty()) {
        	return false;
        }
		
		if(!hour.getText().matches(NUMBER_REGEX)){
			return false;
        }
        else if(hour.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(Integer.parseInt(hour.getText()) < 0) {
        	return false;
        }
        else if(hour.getText().isEmpty()) {
        	return false;
        }
		
		if(!minute.getText().matches(NUMBER_REGEX)){
			return false;
        }
        else if(minute.getText().length() > NUMBER_MAX_LENGTH) {
        	return false;
        }
        else if(Integer.parseInt(minute.getText()) > 59) {
        	return false;
        }
        else if(Integer.parseInt(minute.getText()) < 0) {
        	return false;
        }
        else if(minute.getText().isEmpty()) {
        	return false;
        }
		
		if(!description.getText().matches(SPECIAL_TEXT_REGEX)){
			return false;
        }
        else if(description.getText().length() > TEXTAREA_MAX_LENGTH) {
        	return false;
        }
		return true;
	}
}
