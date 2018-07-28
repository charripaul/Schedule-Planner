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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	
	private final Models.Class temp;
	private String oldClassAbr, newClassAbr;
	//TODO: fix all methods for dependency on these structures
	//if these are deleted,t ask have to be changed or deleted as well
	//probably should be done in modelcontrol for class and task type since
	//other structures are dependent on it
	public ViewClassController(Models.Class c) {
		temp = c;
		oldClassAbr = temp.getAbbreviation();
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(temp);
		initializeCloseEventProperty();
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
		monday.setSelected(getBooleanVal(binary.substring(0,1)));
		tuesday.setSelected(getBooleanVal(binary.substring(1,2)));
		wednesday.setSelected(getBooleanVal(binary.substring(2,3)));
		thursday.setSelected(getBooleanVal(binary.substring(3,4)));
		friday.setSelected(getBooleanVal(binary.substring(4,5)));
		saturday.setSelected(getBooleanVal(binary.substring(5,6)));
		sunday.setSelected(getBooleanVal(binary.substring(6,7)));
		
		warningLabel.setVisible(false);
	}
	@FXML
	private void saveButtonClicked() {
		temp.setName(name.getText());
		temp.setAbbreviation(abbreviation.getText());
		temp.setDetails(details.getText());
		temp.setStartTime(startTime.getValue());
		temp.setEndTime(endTime.getValue());
		temp.setDaysOfWeek(getBinary(monday.isSelected())+getBinary(tuesday.isSelected())+
				getBinary(wednesday.isSelected())+getBinary(thursday.isSelected())+
				getBinary(friday.isSelected())+getBinary(saturday.isSelected())+
				getBinary(sunday.isSelected()));
		
		newClassAbr = temp.getAbbreviation();
		ModelControl.updateClassAndTaskDependency(temp, oldClassAbr, newClassAbr);
		closeWindow();
	}
	@FXML
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			saveButtonClicked();
		}
	}
	@FXML
	private void deleteButtonClicked() {
		if(ModelControl.isBeingUsed(temp)) {
			displayWarningLabel("Cannot delete Class: Already in use by Task");
		}else {
			if(ConfirmExitView.display("Are you sure you want to delete this task?")) {
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
			closeWindow();
		}
	}
	private void closeWindow() {
		Stage stage = (Stage) saveButton.getScene().getWindow();
		stage.close();
	}
}
