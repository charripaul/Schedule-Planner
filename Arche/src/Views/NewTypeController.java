package Views;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTimePicker;

import Models.ModelControl;
import Models.TaskType;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NewTypeController implements Initializable{
	@FXML private TextField name, noticePeriod;
	@FXML private TextField hour, minute;
	@FXML private TextArea description;
	@FXML private Label warningLabel;
	@FXML private JFXButton cancelButton, confirmButton;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		warningLabel.setVisible(false);
		initializeCloseEventProperty();
	}
	@FXML
	private void confirmButtonClicked() {
		int ttc = (Integer.parseInt(hour.getText())*60) + Integer.parseInt(minute.getText());
		TaskType tt = new TaskType(name.getText(), description.getText(),
				Integer.parseInt(noticePeriod.getText()), ttc, 0);
		
		ModelControl.addTaskType(tt);
		closeWindow();
	}
	@FXML
	private void cancelButtonClicked() {
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
	@FXML
	private void setCloseEvent() {
		Stage window = (Stage) hour.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without adding this task type?"));
		});
	}
	private void closeWindow(boolean answer) {
		if(answer == true) {
			closeWindow();
		}
	}
	private void closeWindow() {
		Stage stage = (Stage) confirmButton.getScene().getWindow();
		stage.close();
	}
}
