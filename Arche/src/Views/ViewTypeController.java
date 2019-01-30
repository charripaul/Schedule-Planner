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

public class ViewTypeController implements Initializable{
	@FXML private TextField name;
	@FXML private TextField noticePeriod;
	@FXML private TextField hour, minute;		//timeToComplete
	@FXML private TextArea description;
	@FXML private JFXButton deleteButton, saveButton;
	@FXML private Label warningLabel, totalAssignments;
	
	//ui loading
	@FXML public AnchorPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	private final TaskType temp;
	private String oldTypeName, newTypeName;
	
	private MainNewController mainWindow;
	
	public ViewTypeController(TaskType tt, MainNewController mw) {
		temp = tt;
		oldTypeName = temp.getName();
		mainWindow = mw;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(temp);
		loadingPane.setVisible(false);
		initializeCloseEventProperty();
		setValidators();
	}
	private void initializeData(TaskType tt) {
		name.setText(temp.getName());
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
		loadingPane.setVisible(true);
		Task<Boolean> changeThread = new Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				//validate input data
				if(checkValidation()) {
					temp.setName(name.getText());
					temp.setWarningPeriod(Integer.parseInt(noticePeriod.getText()));
					temp.setDescription(description.getText());
					int amountOfTime = (Integer.parseInt(hour.getText())*60) + Integer.parseInt(minute.getText());
					temp.setTimeToComplete(amountOfTime);
					
					newTypeName = temp.getName();
					ModelControl.updateTaskTypeAndTaskDependency(temp, oldTypeName, newTypeName);
					
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
	private void deleteButtonClicked() {
		if(ModelControl.isBeingUsed(temp)) {
			displayWarningLabel("Cannot delete Task Type: Already in use by Task");
		}else {
			if(ConfirmExitView.display("Are you sure you want to delete this task type?")) {
				ModelControl.deleteTaskType(temp);
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
	private void initializeCloseEventProperty() {
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
		hour.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!hour.getText().matches("^[0-9]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Hour characters invalid");
	            }
	            else if(hour.getText().length() > 9) {
	            	displayWarningLabel("Hour value too large: Keep under 1,000,000,000");
	            }
	            else if(Integer.parseInt(hour.getText()) < 0) {
	            	displayWarningLabel("Hour value must be positive");
	            }
	            else if(hour.getText().isEmpty()) {
	            	displayWarningLabel("Please enter an hour value");
	            }
	        }
	    });
		minute.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) { //when focus lost
	            if(!minute.getText().matches("^[0-9]*$")){
	                //when it doesn't match the pattern
	                //set the textField empty
	                displayWarningLabel("Minute characters invalid");
	            }
	            else if(minute.getText().length() > 9) {
	            	displayWarningLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minute.getText()) > 59) {
	            	displayWarningLabel("Minute value too large: Keep under 60");
	            }
	            else if(Integer.parseInt(minute.getText()) <= 0) {
	            	displayWarningLabel("Minute value must be positive");
	            }
	            else if(minute.getText().isEmpty()) {
	            	displayWarningLabel("Please enter a minute value");
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
		
		if(!hour.getText().matches("^[0-9]*$")){
            //when it doesn't match the pattern
            //set the textField empty
			return false;
        }
        else if(hour.getText().length() > 9) {
        	return false;
        }
        else if(Integer.parseInt(hour.getText()) < 0) {
        	return false;
        }
        else if(hour.getText().isEmpty()) {
        	return false;
        }
		
		if(!minute.getText().matches("^[0-9]*$")){
            //when it doesn't match the pattern
            //set the textField empty
			return false;
        }
        else if(minute.getText().length() > 9) {
        	return false;
        }
        else if(Integer.parseInt(minute.getText()) > 59) {
        	return false;
        }
        else if(Integer.parseInt(minute.getText()) <= 0) {
        	return false;
        }
        else if(minute.getText().isEmpty()) {
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
		return true;
	}
	private void setCloseEvent() {
		Stage window = (Stage) name.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without saving?"));
		});
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
