package Views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTimePicker;

import Models.ModelControl;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NewClassController extends ParentController{
	@FXML private TextField name, abbreviation;
	@FXML private TextArea details;
	@FXML private JFXCheckBox monday, tuesday, wednesday, thursday,
								friday, saturday, sunday;
	@FXML private JFXTimePicker startTime, endTime;
	@FXML private JFXButton cancelButton, confirmButton;
	@FXML private Label alertLabel;
	
	public NewClassController(MainNewController mw) {
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
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			confirmButton.fire();
		}
	}
	@FXML
	private void confirmButtonClicked() {
		enableLoadingOverlay("Updating");
		computeUpdateOperations();
	}
	private void computeUpdateOperations() {
		Task<Boolean> updateThread = createUpdateThread();
		Task<Boolean> timingThread = createTimedThread(updateThread);
		
		new Thread(timingThread).start();
	}
	private Task<Boolean> createUpdateThread(){
		Task<Boolean> updateThread = new Task<Boolean>() {
		    @Override
		    public Boolean call() throws InterruptedException{
		    	return addClass();
		    }
		};
		//reverts to javafx main app thread
		updateThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        boolean result = updateThread.getValue(); 						//result of computation
		        computeUpdateResult(result);
		    }
		});
		return updateThread;
	}
	private boolean addClass() throws InterruptedException {
		if(validate()) {
			String daysOfWeek = 
					getBinary(sunday.isSelected())+
					getBinary(monday.isSelected())+
					getBinary(tuesday.isSelected())+
					getBinary(wednesday.isSelected())+
					getBinary(thursday.isSelected())+
					getBinary(friday.isSelected())+
					getBinary(saturday.isSelected()
			);
			Models.Class c = new Models.Class(
					ModelControl.mainUID,
					name.getText(),
					abbreviation.getText(),
					details.getText(),
					0,
					daysOfWeek,
					startTime.getValue(),
					endTime.getValue()
			);
			
			Thread.sleep(1000);
			ModelControl.addClass(c);
			
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
	private void cancelButtonClicked() {
		closeWindow();
	}
	protected void enableCloseEventProperty() {
		enableTextCloseProperty(name);
		enableTextCloseProperty(abbreviation);
		enableTextCloseProperty(details);
		
		enableCheckboxCloseProperty(monday);
		enableCheckboxCloseProperty(tuesday);
		enableCheckboxCloseProperty(wednesday);
		enableCheckboxCloseProperty(thursday);
		enableCheckboxCloseProperty(friday);
		enableCheckboxCloseProperty(saturday);
		enableCheckboxCloseProperty(sunday);
		
		enableTimepickerCloseProperty(startTime);
		enableTimepickerCloseProperty(endTime);
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
		abbreviation.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!abbreviation.getText().matches(NORMAL_TEXT_REGEX)){
	                displayAlertLabel("Abbreviation text invalid");
	            }
	            else if(abbreviation.getText().length() > ABRV_MAX_LENGTH) {
	            	displayAlertLabel("Abbreviation too long: Keep under 15 characters");
	            }
	            else if(abbreviation.getText().isEmpty()) {
	            	displayAlertLabel("Please enter an abbreviation");
	            }
	        }
	    });
		details.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!details.getText().matches(SPECIAL_TEXT_REGEX)){
	                displayAlertLabel("Details text invalid");
	            }
	            else if(details.getText().length() > TEXTAREA_MAX_LENGTH) {
	            	displayAlertLabel("Details text too long: Keep under 255 characters");
	            }
	        }
	    });
		startTime.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(startTime.getValue() == null) {
	            	displayAlertLabel("Please set a start time");
	            }
	        }
	    });
		endTime.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	        	if(startTime.getValue() == null) {
	            	displayAlertLabel("Please set an end time");
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
		
		if(!abbreviation.getText().matches(NORMAL_TEXT_REGEX)){
            return false;
        }
        else if(abbreviation.getText().length() > ABRV_MAX_LENGTH) {
        	return false;
        }
        else if(abbreviation.getText().isEmpty()) {
        	return false;
        }
		
		if(!details.getText().matches(SPECIAL_TEXT_REGEX)){
            return false;
        }
        else if(details.getText().length() > TEXTAREA_MAX_LENGTH) {
        	return false;
        }
		
		if(startTime.getValue() == null) {
        	return false;
        }
		if(endTime.getValue() == null) {
        	return false;
        }
		return true;
	}
}
