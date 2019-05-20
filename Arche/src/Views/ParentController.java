package Views;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;

import Runners.DBConn;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tornadofx.control.DateTimePicker;

public abstract class ParentController implements Initializable{
	@FXML protected Label alertLabel;
	
	//loading overlay
	@FXML protected AnchorPane loadingOverlay;
	@FXML protected JFXButton cancelLoadingButton;
	@FXML protected Label loadingText;
	
	protected Stage thisWindow;
	protected MainNewController mainWindowController;
	
	protected String NORMAL_TEXT_REGEX = "^[a-zA-Z0-9\\-_\\s\\.]*$";
	protected String SPECIAL_TEXT_REGEX = "^[\\:\\.\\s\\w\\d\\?><;,\\{\\}\\[\\]\\-_\\+=!@\\#\\$%^&\\*\\|\\']*$";
	protected String NUMBER_REGEX = "^[0-9]*$";
	protected String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm a";
	protected int TIMEOUT_TIME = 25000;							//25 secs
	protected int NAME_MAX_LENGTH = 45;
	protected int ABRV_MAX_LENGTH = 15;							//abbreviation
	protected int TEXTAREA_MAX_LENGTH = 255;
	protected int NUMBER_MAX_LENGTH = 9;
	
	protected abstract boolean validate();						//check fields to validate input before operations
	protected abstract void enableVisualValidation();			//initialize validation that displays messages through alert label
	protected abstract void enableCloseEventProperty();			//confirm user wants to close window without saving changes
	
	public ParentController() {
		mainWindowController = null;
	}
	public ParentController(MainNewController mw) {
		mainWindowController = mw;
	}
	
	protected String getBinary(boolean b) {
		if(b) {
			return "1";
		}
		else {
			return "0";
		}
	}
	
	protected boolean getBooleanVal(String s) {
		if(s.equals("0")) {
			return false;
		}
		else {
			return true;
		}
	}
	
	protected Task<Boolean> createTimedThread(Task<Boolean> thread) {
		Task<Boolean> timingThread = new Task<Boolean>() {
			@Override
			public Boolean call() {
				long timeoutTime = TIMEOUT_TIME;
				return runUnderTimer(thread, timeoutTime);
			}
		};
		
		timingThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		    	boolean result = timingThread.getValue();
		    	computeTimedThreadResult(result);
		    }
		});
		
		return timingThread;
	}
	
	protected boolean runUnderTimer(Task<Boolean> thread, long timeoutTime) {
		TimeOut timer = new TimeOut(new Thread(thread), timeoutTime, true);
		try {                       
		  boolean success = timer.execute(); // Will return false if this times out, this freezes thread
		  return success;
		} catch (InterruptedException e) {
			e.getMessage();
		}
		return false;
	}
	
	protected void computeTimedThreadResult(boolean result) {
		if(!result) {
    		System.out.println("Connection timed out\nProcess killed\n Error Code: LC01");
    		disableLoadingOverlay();
        	displayAlertLabel("Connection timed out, Error Code: LC01");
    	}
	}
	
	protected void enableLoadingOverlay(String msg) {
		loadingText.setText(msg);
		alignLabel(loadingText);
		loadingOverlay.setVisible(true);
	}
	
	protected void disableLoadingOverlay() {
		loadingOverlay.setVisible(false);
	}
	
	protected void alignLabel(Label label) {
		label.setMaxWidth(Double.MAX_VALUE);
		AnchorPane.setLeftAnchor(label, 0.0);
		AnchorPane.setRightAnchor(label, 0.0);
		label.setAlignment(Pos.CENTER);
	}
	
	protected void displayAlertLabel(String msg) {
		displayAlert(msg, alertLabel);
	}
	
	protected void displayAlert(String msg, Label alert) {
		alert.setText(msg);
		alignLabel(alert);
		alert.setVisible(true);
		PauseTransition visiblePause = new PauseTransition(
		        Duration.seconds(3)
		);
		visiblePause.setOnFinished(
		        event -> alert.setVisible(false)
		);
		visiblePause.play();
	}
	
	protected void setLoadingText(String msg) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadingText.setText(msg);
                alignLabel(loadingText);
            }
        });
	}
	
	protected void enableTextCloseProperty(TextInputControl childNode) {
		childNode.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	setCloseEvent();
		    }
		});
	}
	
	protected void enableCheckboxCloseProperty(CheckBox cb) {
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	            setCloseEvent();
	        }
	    });
	}
	
	protected void enableTimepickerCloseProperty(JFXTimePicker tp) {
		tp.valueProperty().addListener((ov, oldValue, newValue) -> {
            setCloseEvent();
        });
	}
	
	protected void setThisWindow() {
		thisWindow = (Stage) alertLabel.getScene().getWindow();
	}
	
	protected void closeWindow(boolean answer) {
		if(answer == true) {
			if(loadingOverlay.isVisible()) {
				mainWindowController.displayConnectionTimeOut();
			}
			closeWindow();
		}
	}
	
	protected void closeWindow() {
		setThisWindow();
		thisWindow.close();
	}
	
	@FXML
	protected void setCloseEvent() {
		Stage window = (Stage) alertLabel.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without saving your changes?"));
		});
	}
}