package Views;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tornadofx.control.DateTimePicker;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;

public class NewTaskController extends ParentSpecialTaskController{
	public NewTaskController(MainNewController mw) {
		super(mw);
		taskList = new ArrayList<Task>();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		viewableTaskList = FXCollections.observableArrayList();
		initialize();
	}
	
	protected boolean addTask() {
		for(int count=0;count<taskList.size();count++) {
			ModelControl.addTask(taskList.get(count));
		}
		return true;
	}
	
	@FXML
	protected void setCloseEvent() {
		Stage window = (Stage) description.getScene().getWindow();
		window.setOnCloseRequest(e -> {
			e.consume();
			
			if(newTasksTable.getItems().size() > 0) {
				closeWindow(ConfirmExitView.display("Are you sure you want to exit without adding your tasks?"));
			}
			else {
				closeWindow();
			}
		});
	}
}
