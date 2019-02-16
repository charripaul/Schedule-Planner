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
import Models.DataLock.CannotPerformOperationException;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class LoginController extends ParentController{
	
	//login section
	@FXML private JFXButton loginButton;
	@FXML private JFXButton registerButton;
	@FXML private Button closeButton;
	@FXML private JFXTextField username;
	@FXML private JFXPasswordField password;
	@FXML private Label title;
	@FXML private Label description;
	@FXML private JFXCheckBox rememberMe;
	@FXML private ImageView backgroundImage;
	@FXML private AnchorPane loginLayout;
	@FXML private AnchorPane bluePane;
	
	//register section
	@FXML private AnchorPane registerLayout;
	@FXML private JFXButton registerConfirmButton;
	@FXML private JFXTextField registerUsername;
	@FXML private JFXPasswordField registerFirstPass;
	@FXML private JFXPasswordField registerSecondPass;
	@FXML private Button backButton;
	@FXML private Label registerAlert;
	
	private int USERNAME_MAX_LENGTH = 45;
	private int PASSWORD_MAX_LENGTH = 40;
	private int USERNAME_MIN_LENGTH = 1;
	private int PASSWORD_MIN_LENGTH = 6;
	
	//Login Controller remains hidden and running in background
	//Therefore, mainWindow instance variable needed
	private Stage mainWindow;
	
	private double xOffset = 0;
	private double yOffset = 0;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		registerAlert.setVisible(false);
		registerLayout.setVisible(false);
		alertLabel.setVisible(false);
		loginLayout.setVisible(true);
		
		disableLoadingOverlay();
		enableVisualValidation();
		enableMouseDragProperty();
		fillSavedUsername();
		focusNode(username);
	}
	
	protected void setThisWindow() {
		thisWindow = (Stage) alertLabel.getScene().getWindow();
	}
	
	@FXML
	private void loginButtonClicked() {
		enableLoadingOverlay("Authenticating");
		computeLoginOperations();
	}
	
	private void computeLoginOperations() {
		Task<Boolean> authThread = createAuthThread();
		Task<Boolean> timingThread = createTimedThread(authThread);
		
		new Thread(timingThread).start();
	}
	
	//computation thread
	private Task<Boolean> createAuthThread() {
		//perform  in separate thread
		Task<Boolean> authThread = new Task<Boolean>() {
		    @Override
		    public Boolean call() throws InterruptedException{
		    	return authenticate();
		    }
		};
		
		//reverts to javafx main app thread
		authThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        boolean result = authThread.getValue(); 						//result of computation
		        computeAuthResult(result);
		    }
		});
		
		return authThread;
	}
	
	//database authentication check and connection initialization, returns true if pass authentication
	private boolean authenticate() throws InterruptedException {
		String u = username.getText();
		String p = password.getText();
		
        boolean isUser = ModelControl.isUser(u,p);
    	boolean isAdmin = ModelControl.isAdmin(u,p);
    	
    	if(isUser || isAdmin) {
    		Thread.sleep(1000);
    		setLoadingText("Loading User Data");
    		ModelControl.initializeUserData();
    		
    		if(rememberMe.isSelected()) {
    			LocalDBConn.updateSavedUsername(u);
    		}
    		else {
    			LocalDBConn.updateSavedUsername("");
    		}
    		
			Thread.sleep(1000);
    		return true;
    	}
    	else {
    		Thread.sleep(1000);
    		return false;
    	}
	}
	
	private void computeAuthResult(boolean result) {
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
			thisWindow.hide();
			mainWindow.show();
        }
        else {
        	disableLoadingOverlay();
        	displayAlert("Incorrect username/password combination", alertLabel);
        }
	}
	
	@FXML
	private void enterPressedOnPW(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			loginButton.fire();
		}
	}
	
	@FXML
	private void loginKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.TAB) {
			focusNode(username);
		}
		else if(event.getCode() == KeyCode.ENTER) {
			loginButton.fire();
		}
	}
	
	@FXML
	private void registerKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.TAB) {
			focusNode(registerUsername);
		}
		else if(event.getCode() == KeyCode.ENTER) {
			registerConfirmButton.fire();
		}
	}
	
	@FXML
	private void closeButtonClicked() {
		closeWindow();
	}
	
	@FXML
	private void backButtonClicked() {
		registerUsername.clear();
		registerFirstPass.clear();
		registerSecondPass.clear();
		registerLayout.setVisible(false);
		loginLayout.setVisible(true);
		alertLabel.setVisible(false);
		focusNode(username);
	}
	
	@FXML
	private void registerButtonClicked() {
		loginLayout.setVisible(false);
		registerLayout.setVisible(true);
		password.setText("");
		focusNode(registerUsername);
	}
	
	@FXML
	private void confirmRegisterButtonClicked() {
		enableLoadingOverlay("Validating");
		computeRegisterOperations();
	}
	
	private void computeRegisterOperations() {
		Task<Boolean> registerThread = createRegisterThread();
		Task<Boolean> timedThread = createTimedThread(registerThread);
		
		new Thread(timedThread).start();
	}
	
	private Task<Boolean> createRegisterThread(){
		Task<Boolean> registerThread = new Task<Boolean>() {
		    @Override
		    public Boolean call() throws InterruptedException, CannotPerformOperationException{
		    	return register();
		    }
		};
		
		registerThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		    @Override
		    public void handle(WorkerStateEvent event) {
		        boolean result = registerThread.getValue();
		        computeRegisterResult(result);
		    }
		});
		return registerThread;
	}
	
	private boolean register() throws InterruptedException, CannotPerformOperationException {		
		if(!validate()) {
			return false;
		}
		else {
			setLoadingText("Registering");
			
			String hash = DataLock.createHash(registerFirstPass.getText());
			ModelControl.addUser(new User(registerUsername.getText(), hash));
			Thread.sleep(2000);
			
			return true;
		}
	}
	
	private void computeRegisterResult(boolean result){
		if(result) {
        	backButton.fire();
        	displayAlert("Registration Complete", alertLabel);
        	disableLoadingOverlay();
        }
        else {
        	displayAlert("Please fix input", registerAlert);
        	loadingOverlay.setVisible(false);
        	disableLoadingOverlay();
        }
	}
	
	protected boolean validate() {
		if(!registerUsername.getText().matches(NORMAL_TEXT_REGEX)){
            return false;
        }
        else if(registerUsername.getText().length() > USERNAME_MAX_LENGTH) {
        	return false;
        }
        else if(registerUsername.getText().isEmpty()) {
        	return false;
        }
        else if(ModelControl.usernameExists(registerUsername.getText())) {
			return false;
		}
		
		if(!registerFirstPass.getText().matches(SPECIAL_TEXT_REGEX)){
            return false;
        }
        else if(registerFirstPass.getText().length() > PASSWORD_MAX_LENGTH) {
        	return false;
        }
        else if(registerFirstPass.getText().length() < PASSWORD_MIN_LENGTH) {
        	return false;
        }
        else if(registerFirstPass.getText().isEmpty()) {
        	return false;
        }
        else if(!registerFirstPass.getText().equals(registerSecondPass.getText())) {
        	return false;
        }
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	protected void enableVisualValidation() {
		registerUsername.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!registerUsername.getText().matches(NORMAL_TEXT_REGEX)){
	                displayAlert("Username text invalid", registerAlert);
	            }
	            else if(registerUsername.getText().length() > USERNAME_MAX_LENGTH) {
	            	displayAlert("Username too long: Keep under 45 characters", registerAlert);
	            }
	            else if(registerUsername.getText().isEmpty()) {
	            	displayAlert("Please enter a username", registerAlert);
	            }
	            else if(ModelControl.usernameExists(registerUsername.getText())) {
	    			displayAlert("Username already exists", registerAlert);
	    		}
	            else {
	            	registerAlert.setVisible(false);
	            }
	        }
	    });
		
		registerFirstPass.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!registerFirstPass.getText().matches(SPECIAL_TEXT_REGEX)){
	                displayAlert("Password text invalid", registerAlert);
	            }
	            else if(registerFirstPass.getText().length() > PASSWORD_MAX_LENGTH) {
	            	displayAlert("Password too long: Keep under 40 characters", registerAlert);
	            }
	            else if(registerFirstPass.getText().isEmpty()) {
	            	displayAlert("Please enter a password", registerAlert);
	            }
	            else if(registerFirstPass.getText().length() < PASSWORD_MIN_LENGTH) {
	            	displayAlert("Password must be at least 6 characters long", registerAlert);
	            }
	            else {
	            	registerAlert.setVisible(false);
	            }
	        }
	    });
		
		registerSecondPass.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	        if (!newValue) {
	            if(!registerFirstPass.getText().equals(registerSecondPass.getText())){
	                displayAlert("Passwords do not match", registerAlert);
	            }
	            else {
	            	registerAlert.setVisible(false);
	            }
	        }
	    });
	}
	
	public void setStage(Stage stage) {
		thisWindow = stage;
	}
	
	private void enableMouseDragProperty() {
		backgroundImage.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
		
        backgroundImage.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                thisWindow.setX(event.getScreenX() - xOffset);
                thisWindow.setY(event.getScreenY() - yOffset);
            }
        });
        
        loadingOverlay.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        
        loadingOverlay.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                thisWindow.setX(event.getScreenX() - xOffset);
                thisWindow.setY(event.getScreenY() - yOffset);
            }
        });
	}
	
	private void focusNode(JFXTextField field) {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
                field.requestFocus();
            }
        });
	}
	
	private void fillSavedUsername() {
		String usernameFill = LocalDBConn.getSavedUsername();
		if(!usernameFill.equals("")) {
			rememberMe.setSelected(true);
		}
		username.setText(usernameFill);
	}
	
	private void closeProgram(boolean answer){
		if(answer == true) {
			DBConn.closeConnection();
			mainWindow.close();
		}
	}
	
	protected void enableCloseEventProperty() {
		//none
	}
}
