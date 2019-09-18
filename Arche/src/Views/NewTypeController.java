package Views;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTimePicker;

import Models.ModelControl;
import Models.TaskType;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NewTypeController extends ParentController{
	@FXML private TextField name, noticePeriod;
	@FXML private TextField hour, minute;
	@FXML private TextArea description;
	@FXML private JFXButton cancelButton, confirmButton;
	
	public NewTypeController(MainNewController mw) {
		super(mw);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		alertLabel.setVisible(false);
		disableLoadingOverlay();
		enableCloseEventProperty();
		enableVisualValidation();
	}
	
	@FXML
	private void confirmButtonClicked() {
		enableLoadingOverlay("Updating");
		computeUpdateOperations();
	}
	
	@FXML
	private void cancelButtonClicked() {
		closeWindow();
	}
	
	private void computeUpdateOperations() {
		Task<Boolean> updateThread = createUpdateThread();
		Task<Boolean> timingThread = createTimedThread(updateThread);
		
		new Thread(timingThread).start();
	}
	
	private Task<Boolean> createUpdateThread() {
		Task<Boolean> changeThread = new Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException {
				return addType();
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
	
	private boolean addType() throws InterruptedException{
		//validate input data
		if(validate()) {
			int ttc = (Integer.parseInt(hour.getText())*60) + Integer.parseInt(minute.getText());
			TaskType tt = new TaskType(
					ModelControl.mainUID,
					name.getText(),
					description.getText(),
					Integer.parseInt(noticePeriod.getText()),
					ttc,
					0
					);
			
			Thread.sleep(1000);
			ModelControl.addTaskType(tt);
			
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
	            else if(hour.getText().isEmpty()) {
	            	displayAlertLabel("Please enter an hour value");
	            }
	            else if(Integer.parseInt(hour.getText()) < 0) {
	            	displayAlertLabel("Hour value must be positive");
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
	            else if(minute.getText().isEmpty()) {
	            	displayAlertLabel("Please enter a minute value");
	            }
	            else if(Integer.parseInt(minute.getText()) < 0) {
	            	displayAlertLabel("Minute value must be positive");
	            }
	        }
	    });
		
		description.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!description.getText().matches(SPECIAL_TEXT_REGEX)){
	                displayAlertLabel("Description text invalid");
	            }
	            else if(description.getText().length() > 255) {
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
