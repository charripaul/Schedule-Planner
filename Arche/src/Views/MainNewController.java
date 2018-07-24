package Views;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class MainNewController implements Initializable{
	@FXML private JFXButton homeButton;
	@FXML private JFXButton calendarButton;
	@FXML private JFXButton taskButton;
	@FXML private JFXButton classButton;
	@FXML private Label title;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	@FXML
	private void homeButtonClicked() {
		resetButtons();
		homeButton.setStyle("-fx-background-color:  #f2782f;");
	}
	@FXML
	private void calendarButtonClicked() {
		resetButtons();
		calendarButton.setStyle("-fx-background-color:  #f2782f;");
	}
	@FXML
	private void taskButtonClicked() {
		resetButtons();
		taskButton.setStyle("-fx-background-color:  #f2782f;");
	}
	@FXML
	private void classButtonClicked() {
		resetButtons();
		classButton.setStyle("-fx-background-color:  #f2782f;");
	}
	private void resetButtons() {
		
	}
}
