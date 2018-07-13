package Views;

import java.net.URL;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tornadofx.control.DateTimePicker;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;

public class NewTaskController implements Initializable{
	@FXML private TextField name;
	@FXML private ChoiceBox<String> className;
	@FXML private ChoiceBox<String> taskType;
	@FXML private TextArea description;
	@FXML private DateTimePicker date;
	@FXML private Button confirm;
	@FXML private Button cancel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		date.setFormat("MM/dd/yyyy hh:mm");
		
		//initialize choiceboxes
		ArrayList<Models.Class> allClasses = ModelControl.getClasses();
		ArrayList<TaskType> allTaskTypes = ModelControl.getTaskTypes();
		
		ObservableList<String> tts = FXCollections.observableArrayList();
		ObservableList<String> cls = FXCollections.observableArrayList();
		for(int count = 0;count<allTaskTypes.size();count++) {
			tts.add(allTaskTypes.get(count).getName());
		}
		for(int count = 0;count<allClasses.size();count++) {
			cls.add(allClasses.get(count).getAbbreviation());
		}
		taskType.getItems().clear();
		taskType.setItems(tts);
		className.getItems().clear();
		className.setItems(cls);
		
		initializeCloseEventProperty();
	}
	@FXML
	private void setCloseEvent() {
		Stage window = (Stage) description.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without adding this task?"));
		});
	}
	@FXML
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			confirmButtonClicked();
		}
	}
	@FXML
	private void cancelButtonClicked() {
		closeWindow();
	}
	@FXML
	private void confirmButtonClicked() {
		Task t = new Task(name.getText(), description.getText(),
				date.getDateTimeValue().atZone(ZoneId.
				systemDefault()).toInstant().toEpochMilli(),
				false, taskType.getSelectionModel().getSelectedItem(),
				className.getSelectionModel().getSelectedItem());
		ModelControl.addTask(t);
		closeWindow();
	}
	private void initializeCloseEventProperty() {
		name.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	    taskType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	        	setCloseEvent();
	        }
	    });
	    className.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
	        	setCloseEvent();
	        }
	    });
	    date.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
	    description.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	}
	private void closeWindow(boolean answer) {
		if(answer == true) {
			closeWindow();
		}
	}
	private void closeWindow() {
		Stage stage = (Stage) confirm.getScene().getWindow();
		stage.close();
	}
}
