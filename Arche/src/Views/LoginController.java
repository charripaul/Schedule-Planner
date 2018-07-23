package Views;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import Models.ModelControl;
import Runners.DBConn;
import javafx.animation.PauseTransition;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class LoginController implements Initializable{
	@FXML private JFXButton loginButton;
	@FXML private JFXButton registerButton;
	@FXML private Button closeButton;
	@FXML private JFXTextField username;
	@FXML private JFXPasswordField password;
	@FXML private Label title;
	@FXML private Label description;
	@FXML private Label alert;
	@FXML private CheckBox remember;
	@FXML private ImageView bg;
	@FXML private AnchorPane pane;
	
	private double xOffset = 0;
	private double yOffset = 0;
	private Stage loginWindow;
	private Stage mainWindow;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	@FXML
	private void closeButtonClicked() {
		closeWindow();
	}
	@FXML
	private void loginButtonClicked() throws InterruptedException, IOException {
		String u = username.getText();
		String p = password.getText();
		
		if(ModelControl.isAdmin(u, p)) {
			loginWindow.hide();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/main.fxml"));
			Parent root = loader.load();
			mainWindow = new Stage();
			mainWindow.setMinWidth(900);
			mainWindow.setMinHeight(600);
			mainWindow.setOnCloseRequest(e -> {
				e.consume();
				closeProgram(ConfirmExitView.display("Are you sure you want to exit?"));
			});
			//stage.initStyle(StageStyle.DECORATED);
			mainWindow.setScene(new Scene(root));
			mainWindow.show();
			//closeWindow();
		}
		else {
			alert.setVisible(true);
			PauseTransition visiblePause = new PauseTransition(
			        Duration.seconds(5)
			);
			visiblePause.setOnFinished(
			        event -> alert.setVisible(false)
			);
			visiblePause.play();
		}
	}
	@FXML
	private void tabPressed() {
		//TODO: if tab and enter pressed in either text field
	}
	@FXML
	private void registerButtonClicked() {
		//TODO: write
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
