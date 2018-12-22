package Views;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import Models.DataLock;
import Models.ModelControl;
import Models.User;
import Runners.DBConn;
import Runners.LocalDBConn;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import jfxtras.scene.control.ImageViewButton;

public class LoginController implements Initializable{
	@FXML private JFXButton loginButton;
	@FXML private JFXButton registerButton;
	@FXML private Button closeButton;
	@FXML private JFXTextField username;
	@FXML private JFXPasswordField password;
	@FXML private Label title;
	@FXML private Label description;
	@FXML private Label loginAlert;
	@FXML private JFXCheckBox remember;
	@FXML private ImageView bg;
	@FXML private AnchorPane entirePane;
	@FXML private AnchorPane loginPane;
	
	@FXML private AnchorPane registerPane;
	@FXML private JFXButton registerConfirmButton;
	@FXML private JFXTextField usernameRegister;
	@FXML private JFXPasswordField firstPassRegister;
	@FXML private JFXPasswordField secondPassRegister;
	@FXML private Button backButton;
	@FXML private Label registerAlert;
	
	@FXML private AnchorPane loadingPane;
	@FXML private JFXButton cancelLoadingButton;
	@FXML private Label loadingText;
	
	private double xOffset = 0;
	private double yOffset = 0;
	private Stage loginWindow;
	private Stage mainWindow;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		registerAlert.setVisible(false);
		registerPane.setVisible(false);
		loginPane.setVisible(true);
		loadingPane.setVisible(false);
		
		String usernameFill = LocalDBConn.getLoginUsername();
		if(!usernameFill.equals("")) {
			remember.setSelected(true);
		}
		username.setText(usernameFill);
		
		selectNode(username);
	}
	@FXML
	private void closeButtonClicked() {
		closeWindow();
	}
	@FXML
	private void loginButtonClicked() {
		loadingText.setText("Authenticating");
		loadingPane.setVisible(true);
		
		//perform database authentication check and connection in separate thread
		Task<Boolean> initializeConnection = new Task<Boolean>() {
		    @Override
		    public Boolean call() throws InterruptedException{
		    	String u = username.getText();
				String p = password.getText();
		        boolean isUser = ModelControl.isUser(u,p);
		    	boolean isAdmin = ModelControl.isAdmin(u,p);
		    	
		    	if(isUser || isAdmin) {
		    		ModelControl.initialize();
		    		
		    		if(remember.isSelected()) {
		    			LocalDBConn.updateLoginUsername(u);
		    		}
		    		else {
		    			LocalDBConn.updateLoginUsername("");
		    		}
		    		
					Thread.sleep(1000);
		    		return true;
		    	}
		    	else {
		    		Thread.sleep(1000);
		    		return false;
		    	}
		    }
		};
		//reverts to javafx main app thread
		initializeConnection.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        boolean result = initializeConnection.getValue(); // result of computation
		        
		        if(result) {
		        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/mainNew.fxml"));
		        	Parent root = null;
					try {
						root = loader.load();
					} catch (IOException e) {
						e.printStackTrace();
					}
		        	
		        	mainWindow = new Stage();
					mainWindow.setMinWidth(900);
					mainWindow.setMinHeight(600);
					
					mainWindow.setOnCloseRequest(e -> {
						e.consume();
						closeProgram(ConfirmExitView.display("Are you sure you want to exit?"));
					});
					//stage.initStyle(StageStyle.DECORATED);
					mainWindow.setScene(new Scene(root));
					loginWindow.hide();
					mainWindow.show();
		        }
		        else {
		        	loadingPane.setVisible(false);
		        	displayLoginAlert("Incorrect username/password combination");
		        }
		    }
		});
		
		//new Thread(initializeConnection).start();
		
		//mid layer thread to handle the use of the initializeconnection thread
		//needed because this thread waits on the connection thread for information to determine
		//timeout information
		Task<Boolean> midlayer = new Task<Boolean>() {
			@Override
			public Boolean call() {
				long timeoutTime = 25000;		//25 secs
				TimeOut t = new TimeOut(new Thread(initializeConnection), timeoutTime, true);
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
		    		System.out.println("Connection timed out\nProcess killed\n Error Code: LC01");
		    		loadingPane.setVisible(false);
		        	displayLoginAlert("Connection timed out, Error Code: LC01");
		    	}
		    }
		});
		
		new Thread(midlayer).start();
	}
	@FXML
	private void loginKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.TAB) {
			selectNode(username);
		}
		else if(event.getCode() == KeyCode.ENTER) {
			loginButton.fire();
		}
	}
	@FXML
	private void registerKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.TAB) {
			selectNode(usernameRegister);
		}
		else if(event.getCode() == KeyCode.ENTER) {
			registerConfirmButton.fire();
		}
	}
	@FXML
	private void backButtonClicked() {
		usernameRegister.clear();
		firstPassRegister.clear();
		secondPassRegister.clear();
		registerPane.setVisible(false);
		loginPane.setVisible(true);
		selectNode(username);
	}
	@FXML
	private void registerButtonClicked() {
		loginPane.setVisible(false);
		registerPane.setVisible(true);
		selectNode(usernameRegister);
	}
	@FXML
	private void confirmRegisterButtonClicked() {
		loadingText.setText("Registering");
		loadingPane.setVisible(true);
		
		Task<String> initializeConnection = new Task<String>() {
		    @Override
		    public String call() throws InterruptedException{
		    	if(usernameRegister.getText().equals("")) {
					return "Please enter a username";
				}
				else if(ModelControl.usernameExists(usernameRegister.getText()) == false) {
					if(firstPassRegister.getText().equals(secondPassRegister.getText())) {
						if(!firstPassRegister.getText().equals("")) {
							ModelControl.addUser(new User(usernameRegister.getText(),DataLock.encrypt(firstPassRegister.getText())));
							Thread.sleep(2000);
							return "";
						}
						else {
							return "Please enter a password";
						}
					}
					else {
						return "Passwords do not match";
					}
				}
				else {
					return "Username already exists";
				}
		    }
		};
		
		initializeConnection.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        String result = initializeConnection.getValue(); // result of computation
		        
		        if(result.equals("")) {
		        	backButton.fire();
		        	displayLoginAlert("Registration Complete");
		        	loadingPane.setVisible(false);
		        }
		        else {
		        	displayRegisterAlert(result);
		        	loadingPane.setVisible(false);
		        }
		    }
		});
		
		Task<Boolean> midlayer = new Task<Boolean>() {
			@Override
			public Boolean call() {
				long timeoutTime = 25000;		//25 secs
				TimeOut t = new TimeOut(new Thread(initializeConnection), timeoutTime, true);
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
		    		System.out.println("Connection timed out\nProcess killed\nError Code: LC02");
		    		loadingPane.setVisible(false);
		        	displayRegisterAlert("Connection timed out, Error Code: LC02");
		    	}
		    }
		});
		
		new Thread(midlayer).start();
	}
	public void setStage(Stage stage) {
		loginWindow = stage;
		bg.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        bg.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loginWindow.setX(event.getScreenX() - xOffset);
                loginWindow.setY(event.getScreenY() - yOffset);
            }
        });
        
        loadingPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        loadingPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loginWindow.setX(event.getScreenX() - xOffset);
                loginWindow.setY(event.getScreenY() - yOffset);
            }
        });
	}
	private void selectNode(JFXTextField field) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                field.requestFocus();			//select username on start
            }
        });
	}
	private void selectNode(JFXPasswordField field) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                field.requestFocus();			//select username on start
            }
        });
	}
	private void selectNode(JFXButton button) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                button.requestFocus();			//select username on start
            }
        });
	}
	private void displayRegisterAlert(String msg) {
		registerAlert.setText(msg);
		registerAlert.setVisible(true);
		PauseTransition visiblePause = new PauseTransition(
		        Duration.seconds(5)
		);
		visiblePause.setOnFinished(
		        event -> registerAlert.setVisible(false)
		);
		visiblePause.play();
	}
	private void displayLoginAlert(String msg) {
		loginAlert.setText(msg);
		loginAlert.setVisible(true);
		
		PauseTransition visiblePause = new PauseTransition(
		        Duration.seconds(5)
		);
		visiblePause.setOnFinished(
		        event -> loginAlert.setVisible(false)
		);
		visiblePause.play();
	}
	private void closeWindow() {
		Stage window = (Stage) loginButton.getScene().getWindow();
		DBConn.closeConnection();
		window.close();
	}
	private void closeProgram(boolean answer){
		if(answer == true) {
			DBConn.closeConnection();
			mainWindow.close();
		}
	}
}
