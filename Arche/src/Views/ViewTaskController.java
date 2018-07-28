package Views;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TimeZone;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tornadofx.control.DateTimePicker;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;


public class ViewTaskController implements Initializable{
	@FXML private Button deleteButton;
	@FXML private Button saveCloseButton;
	@FXML private TextField name;
	@FXML private ChoiceBox<String> taskTypes;
	@FXML private ChoiceBox<String> classAbrs;
	@FXML private DateTimePicker dueDate;
	@FXML private TextArea description;
	@FXML private CheckBox completed;
	private final Task temp;
	private String oldClassAbr, newClassAbr;
	private String oldTypeName, newTypeName;
	
	public ViewTaskController(Task t) {
		temp = t;
		oldClassAbr = t.getClassAbr();
		oldTypeName = t.getType();
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(temp);
		initializeCloseEventProperty();
	}
	@FXML
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			closeButtonClicked();
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
		temp.setName(name.getText());
		temp.setType(taskTypes.getSelectionModel().getSelectedItem());
		temp.setClassAbr(classAbrs.getSelectionModel().getSelectedItem());
		temp.setDueDate(dueDate.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		temp.setDescription(description.getText());
		temp.setFinishFlag(completed.isSelected());
		
		newClassAbr = temp.getClassAbr();
		newTypeName = temp.getName();
		
		ModelControl.updateTaskAndClassDependency(temp, oldClassAbr, newClassAbr);
		ModelControl.updateTaskAndTypeDependency(temp, oldTypeName, newTypeName);
		closeWindow();
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
		
		//TODO: fix for no due date
		LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(t.getDueDate()), 
                TimeZone.getDefault().toZoneId()); 
		dueDate.setDateTimeValue(date);
		
		dueDate.setFormat("MM/dd/yyyy hh:mm");
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
			closeWindow();
		}
	}
	private void closeWindow() {
		Stage stage = (Stage) saveCloseButton.getScene().getWindow();
		stage.close();
	}
}
