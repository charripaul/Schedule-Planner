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

public class ViewClassController implements Initializable{
	@FXML private TextField name;
	@FXML private TextField abbreviation;
	@FXML private TextArea details;
	@FXML private JFXCheckBox monday, tuesday, wednesday,
								thursday, friday, saturday, sunday;
	@FXML private JFXTimePicker startTime, endTime;
	@FXML private JFXButton deleteButton, saveButton;
	@FXML private Label totalAssignments, warningLabel;
	
	//ui loading
	@FXML public AnchorPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	private final Models.Class temp;
	private String oldClassAbr, newClassAbr;
	
	private MainNewController mainWindow;
	
	//TODO: fix all methods for dependency on these structures
	//if these are deleted,t ask have to be changed or deleted as well
	//probably should be done in modelcontrol for class and task type since
	//other structures are dependent on it
	public ViewClassController(Models.Class c, MainNewController mw) {
		temp = c;
		oldClassAbr = temp.getAbbreviation();
		mainWindow = mw;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(temp);
		loadingPane.setVisible(false);
		initializeCloseEventProperty();
		setValidators();
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
		
		warningLabel.setVisible(false);
	}
	@FXML
	private void saveButtonClicked() {
		loadingPane.setVisible(true);
		Task<Boolean> changeThread = new Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				//validate input data
				if(checkValidation()) {
					temp.setName(name.getText());
					temp.setAbbreviation(abbreviation.getText());
					temp.setDetails(details.getText());
					temp.setStartTime(startTime.getValue());
					temp.setEndTime(endTime.getValue());
					temp.setDaysOfWeek(getBinary(sunday.isSelected())+getBinary(monday.isSelected())+
							getBinary(tuesday.isSelected())+getBinary(wednesday.isSelected())+
							getBinary(thursday.isSelected())+getBinary(friday.isSelected())+
							getBinary(saturday.isSelected()));
					
					newClassAbr = temp.getAbbreviation();
					ModelControl.updateClassAndTaskDependency(temp, oldClassAbr, newClassAbr);
					
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
					closeWindow();
				}
				else {
					loadingPane.setVisible(false);
					displayWarningLabel("Please fix input errors");
				}
			}
		});
		
		Task<Boolean> midlayer = new Task<Boolean>() {
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
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			saveButton.fire();
		}
	}
	@FXML
	private void deleteButtonClicked() {
		if(ModelControl.isBeingUsed(temp)) {
			displayWarningLabel("Cannot delete Class: Already in use by Task");
		}else {
			if(ConfirmExitView.display("Are you sure you want to delete this class?")) {
				ModelControl.deleteClass(temp);
				closeWindow();
			}
		}
	}
	//detect change, ask for save changes on close
	private void initializeCloseEventProperty() {
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
		abbreviation.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!abbreviation.getText().matches("^[a-zA-Z0-9\\-_]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Abbreviation text invalid");
	            }
	            else if(abbreviation.getText().length() > 15) {
	            	displayWarningLabel("Abbreviation too long: Keep under 15 characters");
	            }
	            else if(abbreviation.getText().isEmpty()) {
	            	displayWarningLabel("Please enter an abbreviation");
	            }
	        }
	    });
		details.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!details.getText().matches("^[\\s\\w\\d\\?><;,\\{\\}\\[\\]\\-_\\+=!@\\#\\$%^&\\*\\|\\']*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Details text invalid");
	            }
	            else if(details.getText().length() > 255) {
	            	displayWarningLabel("Details text too long: Keep under 255 characters");
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
		
		if(!abbreviation.getText().matches("^[a-zA-Z0-9\\-_]*$")){
            //when it doesn't match the pattern
            //set the textField empty
            return false;
        }
        else if(abbreviation.getText().length() > 15) {
        	return false;
        }
        else if(abbreviation.getText().isEmpty()) {
        	return false;
        }
		
		if(!details.getText().matches("^[\\s\\w\\d\\?><;,\\{\\}\\[\\]\\-_\\+=!@\\#\\$%^&\\*\\|\\']*$")){
            //when it doesn't match the pattern
            //set the textField empty
            return false;
        }
        else if(details.getText().length() > 255) {
        	return false;
        }
		return true;
	}
	private void setCloseEvent() {
		Stage window = (Stage) name.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without saving?"));
		});
	}
	private boolean getBooleanVal(String s) {
		if(s.equals("0")) {
			return false;
		}
		else {
			return true;
		}
	}
	private String getBinary(boolean b) {
		if(b) {
			return "1";
		}
		else {
			return "0";
		}
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
		Stage stage = (Stage) saveButton.getScene().getWindow();
		stage.close();
	}
}
