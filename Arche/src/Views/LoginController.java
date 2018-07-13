package Views;


import java.util.concurrent.TimeUnit;

import Models.ModelControl;
import Runners.DBConn;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {
	@FXML private Button skipButton;
	@FXML private Button loginButton;
	@FXML private TextField username;
	@FXML private TextField password;
	@FXML private Label label;
	Stage window;
	
	public void SkipButtonClicked() {
		closeWindow();
	}
	public void LoginButtonClicked() throws InterruptedException {
		String u = username.getText();
		String p = password.getText();
		
		if(ModelControl.isAdmin(u, p)) {
			closeWindow();
		}
		else {
			label.setVisible(true);
			PauseTransition visiblePause = new PauseTransition(
			        Duration.seconds(5)
			);
			visiblePause.setOnFinished(
			        event -> label.setVisible(false)
			);
			visiblePause.play();
		}
	}
	private void closeWindow() {
		window = (Stage) skipButton.getScene().getWindow();
		window.close();
	}
}
