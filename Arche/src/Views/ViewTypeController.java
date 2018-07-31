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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ViewTypeController implements Initializable{
	@FXML private TextField name;
	@FXML private TextField noticePeriod;
	@FXML private TextField hour, minute;		//timeToComplete
	@FXML private TextArea description;
	@FXML private JFXButton deleteButton, saveButton;
	@FXML private Label warningLabel, totalAssignments;
	
	private final TaskType temp;
	private String oldTypeName, newTypeName;
	
	public ViewTypeController(TaskType tt) {
		temp = tt;
		oldTypeName = temp.getName();
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeData(temp);
		initializeCloseEventProperty();
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
		temp.setName(name.getText());
		temp.setWarningPeriod(Integer.parseInt(noticePeriod.getText()));
		temp.setDescription(description.getText());
		int amountOfTime = (Integer.parseInt(hour.getText())*60) + Integer.parseInt(minute.getText());
		temp.setTimeToComplete(amountOfTime);
		
		newTypeName = temp.getName();
		ModelControl.updateTaskTypeAndTaskDependency(temp, oldTypeName, newTypeName);
		closeWindow();
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
			saveButtonClicked();
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
	private void setCloseEvent() {
		Stage window = (Stage) name.getScene().getWindow();
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
		Stage stage = (Stage) saveButton.getScene().getWindow();
		stage.close();
	}
}
