package Views;

import java.net.URL;
import java.util.ResourceBundle;

import Models.TaskType;
import javafx.fxml.Initializable;

public class ViewTypeController implements Initializable{
	private final TaskType temp;
	
	public ViewTypeController(TaskType tt) {
		temp = tt;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
}
