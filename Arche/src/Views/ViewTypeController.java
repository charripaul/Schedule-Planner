package Views;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import Models.TaskType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ViewTypeController implements Initializable{
	@FXML private TextField name;
	@FXML private TextField noticePeriod;
	@FXML private TextField hour, minute;
	@FXML private TextArea description;
	@FXML private JFXButton deleteButton, saveButton;
	
	private final TaskType temp;
	
	public ViewTypeController(TaskType tt) {
		temp = tt;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
}
