package Views;

import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTimePicker;

import Models.ModelControl;
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

public class ViewClassController implements Initializable{
	@FXML private TextField name;
	@FXML private TextField abbreviation;
	@FXML private TextArea details;
	@FXML private JFXCheckBox monday, tuesday, wednesday,
								thursday, friday, saturday, sunday;
	@FXML private JFXTimePicker startTime, endTime;
	@FXML private JFXButton deleteButton, saveButton;
	@FXML private Label totalAssignments;
	
	private final Models.Class temp;
	//TODO: fix all methods for dependency on these structures
	//if these are deleted,t ask have to be changed or deleted as well
	//probably should be done in modelcontrol for class and task type since
	//other structures are dependent on it
	public ViewClassController(Models.Class c) {
		temp = c;
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
		ModelControl.updateClass(temp);
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
		if(ConfirmExitView.display("Are you sure you want to delete this task?\nDeleting this"
				+ " Class will move any tasks assigned to it into other class")) {
			ModelControl.deleteClass(temp);
			closeWindow();
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
	private void setCloseEvent() {
		Stage window = (Stage) name.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without saving?"));
		});
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
