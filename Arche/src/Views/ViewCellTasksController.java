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
import com.jfoenix.controls.JFXCheckBox;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tornadofx.control.DateTimePicker;

public class ViewCellTasksController extends ParentSpecialTaskController{
	//for registering changes made to tasks and confirm them once confirm button clicked
	private ArrayList<Task> addQueue = new ArrayList<Task>();
	private ArrayList<Task> updateQueue = new ArrayList<Task>();
	private ArrayList<Task> deleteQueue = new ArrayList<Task>();
	
	public ViewCellTasksController(ArrayList<Task> tasks, MainNewController mw) {
		super(mw);
		taskList = tasks;
		viewableTaskList = FXCollections.observableArrayList();
		updateTaskList();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		newTasksTable.setItems(viewableTaskList);
		initialize();
	}
	
	@FXML
	protected void deleteButtonClicked() {
		super.deleteButtonClicked();
		Task t = newTasksTable.getSelectionModel().getSelectedItem();
		deleteQueue.add(t);
	}
	
	protected boolean addTask() {
		for(int count=0;count<addQueue.size();count++) {
			ModelControl.addTask(addQueue.get(count));
		}
		for(int count=0;count<updateQueue.size();count++) {
			ModelControl.updateTask(updateQueue.get(count));
		}
		for(int count=0;count<deleteQueue.size();count++) {
			ModelControl.deleteTask(deleteQueue.get(count));
		}
		return true;
	}
	
	protected boolean changeTask() {
		if(validate()) {
			Task t = null;
			
			if(fromAdd) {				//got here from addButton
				fromAdd(t);
				addQueue.add(t);
			}
			else {						//got here from editButton
				fromEdit(t);
				updateQueue.add(t);
			}
			return true;
		}
		else {
			return false;
		}
	}
}