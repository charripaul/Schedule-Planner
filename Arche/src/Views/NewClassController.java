package Views;

import java.io.IOException;
import java.net.URL;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NewClassController implements Initializable{
	@FXML private TextField name, abbreviation;
	@FXML private TextArea details;
	@FXML private JFXCheckBox monday, tuesday, wednesday, thursday,
								friday, saturday, sunday;
	@FXML private JFXTimePicker startTime, endTime;
	@FXML private JFXButton cancelButton, confirmButton;
	@FXML private Label warningLabel;
	
	//ui loading
	@FXML public AnchorPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	private MainNewController mainWindow;
	
	public NewClassController(MainNewController mw) {
		mainWindow = mw;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		warningLabel.setVisible(false);
		loadingPane.setVisible(false);
		initializeCloseEventProperty();
		setValidators();
	}
	@FXML
	private void handleKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			confirmButtonClicked();
		}
	}
	@FXML
	private void confirmButtonClicked() {
		loadingPane.setVisible(true);
		
		Task<Boolean> changeThread = new Task<Boolean>() {
			@Override
			public Boolean call() throws InterruptedException{
				//validate input data
				if(checkValidation()) {
					String dow = getBinary(sunday.isSelected()) + getBinary(monday.isSelected())+
							getBinary(tuesday.isSelected())+getBinary(wednesday.isSelected())+
							getBinary(thursday.isSelected())+getBinary(friday.isSelected())+
							getBinary(saturday.isSelected());
					Models.Class c = new Models.Class(ModelControl.mainUID, name.getText(), abbreviation.getText(), details.getText(),
							0, dow, startTime.getValue(), endTime.getValue());
					Thread.sleep(10000);
					ModelControl.addClass(c);
					
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
	private String getBinary(boolean b) {
		if(b) {
			return "1";
		}
		else {
			return "0";
		}
	}
	private void setValidators() {
		
	}
	private boolean checkValidation() {
		return true;
	}
	@FXML
	private void setCloseEvent() {
		Stage window = (Stage) details.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeWindow(ConfirmExitView.display("Are you sure you want to exit without adding this class?"));
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
		Stage stage = (Stage) confirmButton.getScene().getWindow();
		stage.close();
	}
}
