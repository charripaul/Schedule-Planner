package Views;

import java.net.URL;
import java.time.LocalTime;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ViewClassController extends ParentController{
	@FXML private TextField name;
	@FXML private TextField abbreviation;
	@FXML private TextArea details;
	@FXML private JFXCheckBox monday, tuesday, wednesday,
								thursday, friday, saturday, sunday;
	@FXML private JFXTimePicker startTime, endTime;
	@FXML private JFXButton deleteButton, saveButton;
	@FXML private Label totalAssignments;
	
	private final Models.Class dataHolder;
	private String oldClassAbr, newClassAbr;
	
	//TODO: fix all methods for dependency on these structures
	//if these are deleted,t ask have to be changed or deleted as well
	//probably should be done in modelcontrol for class and task type since
	//other structures are dependent on it
	public ViewClassController(Models.Class c, MainNewController mw) {
		super(mw);
		dataHolder = c;
		oldClassAbr = dataHolder.getAbbreviation();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(dataHolder);
		disableLoadingOverlay();
		enableCloseEventProperty();
		enableVisualValidation();
	}
	
	private void initializeData(Models.Class c) {
		name.setText(c.getName());
		abbreviation.setText(c.getAbbreviation());
		details.setText(c.getDetails());
		startTime.setValue(c.getStartTime());
		endTime.setValue(c.getEndTime());
		totalAssignments.setText(c.getTotalAssignments()+"");
		
		//initialize checkboxes
		String binary = c.getDaysOfWeek("");
		sunday.setSelected(getBooleanVal(binary.substring(0,1)));
		monday.setSelected(getBooleanVal(binary.substring(1,2)));
		tuesday.setSelected(getBooleanVal(binary.substring(2,3)));
		wednesday.setSelected(getBooleanVal(binary.substring(3,4)));
		thursday.setSelected(getBooleanVal(binary.substring(4,5)));
		friday.setSelected(getBooleanVal(binary.substring(5,6)));
		saturday.setSelected(getBooleanVal(binary.substring(6,7)));
		
		alertLabel.setVisible(false);
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
				return updateClass();
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
	
	private boolean updateClass() {
		//validate input data
		if(validate()) {
			dataHolder.setName(name.getText());
			dataHolder.setAbbreviation(abbreviation.getText());
			dataHolder.setDetails(details.getText());
			dataHolder.setStartTime(startTime.getValue());
			dataHolder.setEndTime(endTime.getValue());
			dataHolder.setDaysOfWeek(
					getBinary(sunday.isSelected())+
					getBinary(monday.isSelected())+
					getBinary(tuesday.isSelected())+
					getBinary(wednesday.isSelected())+
					getBinary(thursday.isSelected())+
					getBinary(friday.isSelected())+
					getBinary(saturday.isSelected())
					);
			
			newClassAbr = dataHolder.getAbbreviation();
			ModelControl.updateClassAndTaskDependency(dataHolder, oldClassAbr, newClassAbr);
			
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
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			saveButton.fire();
		}
	}
	
	@FXML
	private void deleteButtonClicked() {
		if(ModelControl.isBeingUsed(dataHolder)) {
			displayAlertLabel("Cannot delete Class: Already in use by Task");
		}else {
			if(ConfirmExitView.display("Are you sure you want to delete this class?")) {
				ModelControl.deleteClass(dataHolder);
				closeWindow();
			}
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
		
		abbreviation.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
		
		details.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
		
		monday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		tuesday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		wednesday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		thursday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		friday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		saturday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		sunday.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
		
		startTime.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
		
		endTime.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
	}
	
	protected void enableVisualValidation() {
		name.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
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
		return true;
	}
	
}
