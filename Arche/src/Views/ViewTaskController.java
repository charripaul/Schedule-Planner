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

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.animation.PauseTransition;
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


public class ViewTaskController implements Initializable{
	@FXML private JFXButton deleteButton;
	@FXML private JFXButton saveCloseButton;
	@FXML private TextField name;
	@FXML private ChoiceBox<String> taskTypes;
	@FXML private ChoiceBox<String> classAbrs;
	@FXML private DateTimePicker dueDate, scheduledStartTime, scheduledEndTime;
	@FXML private TextArea description;
	@FXML private JFXCheckBox completed;
	@FXML private TextField noticePeriod;
	@FXML private TextField hours, minutes;
	@FXML private Label warningLabel;
	
	//ui loading
	@FXML public AnchorPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	private final Task temp;
	private String oldClassAbr, newClassAbr;
	private String oldTypeName, newTypeName;
	
	private MainNewController mainWindow;
	
	public ViewTaskController(Task t, MainNewController mw) {
		temp = t;
		oldClassAbr = t.getClassAbr();
		oldTypeName = t.getType();
		mainWindow = mw;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(temp);
		warningLabel.setVisible(false);
		loadingPane.setVisible(false);
		initializeCloseEventProperty();
		setValidators();
	}
	@FXML
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			saveCloseButton.fire();
		}
	}
	@FXML
	private void deleteButtonClicked() {
		if(ConfirmExitView.display("Are you sure you want to delete this task?")) {
			ModelControl.deleteTask(temp);
			closeWindow();
		}
	}
	@FXML
	private void closeButtonClicked() {
		loadingPane.setVisible(true);
		
		javafx.concurrent.Task<Boolean> changeThread = new javafx.concurrent.Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				//validate input data
				if(checkValidation()) {
					temp.setName(name.getText());
					temp.setType(taskTypes.getSelectionModel().getSelectedItem());
					temp.setClassAbr(classAbrs.getSelectionModel().getSelectedItem());
					temp.setDueDate(dueDate.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
					temp.setScheduledStartTime((scheduledStartTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
					temp.setScheduledEndTime(scheduledEndTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
					temp.setDescription(description.getText());
					temp.setFinishFlag(completed.isSelected());
					int amountOfTime = (Integer.parseInt(hours.getText())*60) + Integer.parseInt(minutes.getText());
					temp.setTimeToComplete(amountOfTime);
					temp.setNoticePeriod(Integer.parseInt(noticePeriod.getText()));
					
					newClassAbr = temp.getClassAbr();
					newTypeName = temp.getType();
					
					ModelControl.updateTaskAndClassDependency(temp, oldClassAbr, newClassAbr);
					ModelControl.updateTaskAndTypeDependency(temp, oldTypeName, newTypeName);
					
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
	private void initializeData(Task t) {
		//temp = t;
		int classIndex = -1;
		int taskIndex = -1;
		name.setText(t.getName());
		description.setText(t.getDescription());
		
		ArrayList<Models.Class> allClasses = ModelControl.getClasses();
		ArrayList<TaskType> allTaskTypes = ModelControl.getTaskTypes();
		
		//find where in list our values are for default choicebox initialization
		for(int count = 0;count<allClasses.size();count++) {
			if(allClasses.get(count).getAbbreviation().equals(t.getClassAbr())) {
				classIndex = count;
				break;
			}
		}
		for(int count = 0;count<allTaskTypes.size();count++) {
			if(allTaskTypes.get(count).getName().equals(t.getType())) {
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
		taskTypes.getItems().clear();
		taskTypes.setItems(tts);
		classAbrs.getItems().clear();
		classAbrs.setItems(cls);
		//make default values the one we found earlier, which is one for passed in task
		taskTypes.getSelectionModel().select(taskIndex);
		classAbrs.getSelectionModel().select(classIndex);
		
		completed.setSelected(t.getFinishFlag());
		
		noticePeriod.setText(Integer.toString(t.getNoticePeriod()));
		int amountOfTime = t.getTimeToComplete();
		hours.setText((amountOfTime/60)+"");
		minutes.setText((amountOfTime%60)+"");
		
		//TODO: fix for no due date
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(t.getDueDate()), 
                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()); 
		dueDate.setDateTimeValue(date);
		date = LocalDateTime.ofInstant(Instant.ofEpochMilli(t.getScheduledStartTime()), 
                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()); 
		scheduledStartTime.setDateTimeValue(date);
		date = LocalDateTime.ofInstant(Instant.ofEpochMilli(t.getScheduledEndTime()), 
                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()); 
		scheduledEndTime.setDateTimeValue(date);
		
		dueDate.setFormat("MM/dd/yyyy hh:mm a");
		scheduledStartTime.setFormat("MM/dd/yyyy hh:mm a");
		scheduledEndTime.setFormat("MM/dd/yyyy hh:mm a");
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
	private void setCloseEvent() {
		Stage window = (Stage) description.getScene().getWindow();
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
		Stage stage = (Stage) saveCloseButton.getScene().getWindow();
		stage.close();
	}
}
