package Views;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.TimeZone;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import Models.ModelControl;
import Models.Task;
import Models.TaskType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tornadofx.control.DateTimePicker;

//parent controller for layouts that only display 1 task
public abstract class ParentNormalTaskController extends ParentController{
	//information tab
	@FXML protected Label label;
	@FXML protected TextField name;
	@FXML protected ChoiceBox<String> classAbrs;
	@FXML protected ChoiceBox<String> taskTypes;
	@FXML protected TextArea description;
	@FXML protected DateTimePicker dueDate, scheduledStartTime, scheduledEndTime;
	@FXML protected JFXButton confirmInfo, cancelInfo;
	@FXML protected TextField noticePeriod;
	@FXML protected TextField hours, minutes;		//timeToComplete
	@FXML protected JFXCheckBox completed;
	
	public ParentNormalTaskController(MainNewController mw) {
		super(mw);
	}
	
	protected void initializeChoiceBoxes() {
		ArrayList<Models.Class> allClasses = ModelControl.getClasses();
		ArrayList<TaskType> allTaskTypes = ModelControl.getTaskTypes();
		
		ObservableList<String> tts = FXCollections.observableArrayList();
		ObservableList<String> cls = FXCollections.observableArrayList();
		for(int count = 0;count<allTaskTypes.size();count++) {
			tts.add(allTaskTypes.get(count).getName());
		}
		for(int count = 0;count<allClasses.size();count++) {
			cls.add(allClasses.get(count).getAbbreviation());
		}
		
		taskTypes.getItems().clear();
		taskTypes.setItems(tts);
		classAbrs.getItems().clear();
		classAbrs.setItems(cls);
	}
	
	protected void initializeDateTimePickers() {
		dueDate.setFormat(DATE_TIME_FORMAT);
		scheduledStartTime.setFormat(DATE_TIME_FORMAT);
		scheduledEndTime.setFormat(DATE_TIME_FORMAT);
	}
	
	protected void fillInfo(Task selected) {
		name.setText(selected.getName());
		description.setText(selected.getDescription());
		completed.setSelected(selected.getFinishFlag());
		
		noticePeriod.setText(Integer.toString(selected.getNoticePeriod()));
		int amountOfTime = selected.getTimeToComplete();
		hours.setText((amountOfTime/60)+"");
		minutes.setText((amountOfTime%60)+"");
		
		//set datetime picker format
		LocalDateTime dateTime = LocalDateTime.ofInstant(
				Instant.ofEpochMilli(selected.getDueDate()), 
                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()
        );
		dueDate.setDateTimeValue(dateTime);
		
		if(selected.isScheduled()) {
			dateTime = LocalDateTime.ofInstant(
					Instant.ofEpochMilli(selected.getScheduledStartTime()), 
	                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()
	        ); 
			scheduledStartTime.setDateTimeValue(dateTime);
			
			dateTime = LocalDateTime.ofInstant(
					Instant.ofEpochMilli(selected.getScheduledEndTime()), 
	                TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId()
	        ); 
			scheduledEndTime.setDateTimeValue(dateTime);
		}		
	}
	
	protected void selectChoiceBox(Task selected) {
		ObservableList<String> tts = taskTypes.getItems();
		ObservableList<String> cls = classAbrs.getItems();
		
		for(int count=0;count<tts.size();count++) {
			if(tts.get(count).equals(selected.getType())) {
				taskTypes.getSelectionModel().select(count);
			}
		}
		for(int count=0;count<cls.size();count++) {
			if(cls.get(count).equals(selected.getClassAbr())) {
				classAbrs.getSelectionModel().select(count);
			}
		}
	}
}
