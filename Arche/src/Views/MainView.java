package Views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import Models.ModelControl;
import Models.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MainView {
	TabPane layout;
	Tab todayTab, calendarTab, tasksTab, goalsTab, projectsTab;
	ModelControl mc = new ModelControl();
	
	public MainView() {
		layout = new TabPane();
		layout.setSide(Side.LEFT);
		layout.setTabMinWidth(75);
		layout.setTabMaxWidth(75);
		layout.setTabMinHeight(75);
		layout.setTabMaxHeight(75);
		layout.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		todayTab = new Tab();
		calendarTab = new Tab();
		tasksTab = new Tab();
		goalsTab = new Tab();
		projectsTab = new Tab();
		try {
			todayTab.setGraphic(buildTabIconImage("resources/icons/icons8-home-filled-50.png"));
			calendarTab.setGraphic(buildTabIconImage("resources/icons/icons8-calendar-filled-50.png"));
			tasksTab.setGraphic(buildTabIconImage("resources/icons/icons8-clipboard-filled-40.png"));
			goalsTab.setGraphic(buildTabIconImage("resources/icons/icons8-toolbox-filled-50.png"));
			projectsTab.setGraphic(buildTabIconImage("resources/icons/icons8-open-50.png"));
		}catch(Exception e) {
			System.out.println("Error mv1: " + e.getMessage());
		}
		
		constructTodayTab();
		constructCalendarTab();
		constructTasksTab();
		//constructGoalsTab();
		constructProjectsTab();
		
		layout.getTabs().add(todayTab);
		layout.getTabs().add(calendarTab);
		layout.getTabs().add(tasksTab);
		layout.getTabs().add(goalsTab);
		layout.getTabs().add(projectsTab);
	}
	public TabPane getLayout() {
		return layout;
	}
	private ImageView buildTabIconImage(String url) throws FileNotFoundException{
		FileInputStream input = new FileInputStream(url);
		Image i = new Image(input);
        ImageView imageView = new ImageView();
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        imageView.setImage(i);
        return imageView;
	}
	private void constructTodayTab() {
		
	}
	private void constructCalendarTab() {
		
	}
	private void constructTasksTab() {
		/*ArrayList<Task> tasks = mc.getTasks();
		GridPane grid = new GridPane();
		
		Label name = new Label("Name");
		Label description = new Label("Description");
		Label dueDate = new Label("Due Date");
		Label finished = new Label("Completed");
		
		grid.add(name, 1, 0);
		grid.add(description, 2, 0);
		grid.add(dueDate, 3, 0);
		grid.add(finished, 4, 0);
		
		for(int count = 0;count<tasks.size();count++) {
			grid.add(new Label(Integer.toString(count)), 0, count+1);
			grid.add(new Label(tasks.get(count).getName()), 1, count+1);
			grid.add(new Label(tasks.get(count).getDescription()), 2, count+1);
			grid.add(new Label(tasks.get(count).getDueDate()), 3, count+1);
			grid.add(new CheckBox(), 4, count+1);
		}
		layout.setPadding(new Insets(10,10,10,10));
		layout.setStyle("-fx-grid-lines-visible: true");
		layout.setHgap(10);
		layout.setVgap(10);
		tasksTab.setContent(layout);*/
		ArrayList<Task> tasks = mc.getTasks();
		GridPane grid = new GridPane();
		Label name = new Label("Name");
		Label description = new Label("Description");
		Label dueDate = new Label("Due Date");
		Label finished = new Label("Completed");
		
		grid.add(name, 1, 0);
		grid.add(description, 2, 0);
		grid.add(dueDate, 3, 0);
		grid.add(finished, 4, 0);
		
		for(int count = 0;count<tasks.size();count++) {
			grid.add(new Label(Integer.toString(count)), 0, count+1);
			grid.add(new Label(tasks.get(count).getName()), 1, count+1);
			grid.add(new Label(tasks.get(count).getDescription()), 2, count+1);
			grid.add(new Label(new Date(tasks.get(count).getDueDate()).toString()), 3, count+1);
			grid.add(new CheckBox(), 4, count+1);
		}
	    // make all of the Controls and Panes inside the grid fill their grid cell, 
	    // align them in the center and give them a filled background.
	    // you could also place each of them in their own centered StackPane with 
	    // a styled background to achieve the same effect.
	    for (Node n: grid.getChildren()) {
	      if (n instanceof Control) {
	        Control control = (Control) n;
	        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	        control.setStyle("-fx-background-color: cornsilk; -fx-alignment: center;");
	      }
	      if (n instanceof Pane) {
	        Pane pane = (Pane) n;
	        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	        pane.setStyle("-fx-background-color: cornsilk; -fx-alignment: center;");
	      }
	    }
	    // style the grid so that it has a background and gaps around the grid and between the 
	    // grid cells so that the background will show through as grid lines.
	    grid.setStyle("-fx-padding: 2; -fx-hgap: 2; -fx-vgap: 2; -fx-grid-lines-visible: true;");
	    // turn layout pixel snapping off on the grid so that grid lines will be an even width.
	    grid.setSnapToPixel(false);

	    // set some constraints so that the grid will fill the available area.
	    ColumnConstraints oneThird = new ColumnConstraints();
	    oneThird.setPercentWidth(100/3.0);
	    oneThird.setHalignment(HPos.CENTER);
	    //grid.getColumnConstraints().addAll(oneThird, oneThird, oneThird);
	    RowConstraints oneHalf = new RowConstraints();
	    oneHalf.setPercentHeight(100/2.0);
	    oneHalf.setValignment(VPos.CENTER);
	    //grid.getRowConstraints().addAll(oneHalf, oneHalf);
	    
	    // layout the scene in a stackpane with some padding so that the grid is centered 
	    // and it is easy to see the outer grid lines.
	    StackPane main = new StackPane();
	    main.setStyle("-fx-background-color: white; -fx-padding: 10;");
	    main.getChildren().addAll(grid);
	    BorderPane layout = new BorderPane();
	    Label title = new Label("Tasks");
	    title.setPadding(new Insets(10,10,10,10));
	    title.setFont(new Font(title.getFont().toString(), 30));
	    layout.setTop(title);
	    layout.setCenter(main);
	    tasksTab.setContent(layout);
	}
	/*private void constructGoalsTab() {
		ArrayList<Goal> goals = mc.getGoals();
		
		TreeItem<String> root = new TreeItem<String>();
		for(int count = 0;count<goals.size();count++) {
			TreeItem<String> parent = new TreeItem<>(count + "\t" + goals.get(count).getName());
			TreeItem<String> child = new TreeItem<>("\t- " + goals.get(count).getDescription());
			root.getChildren().add(parent);
			parent.getChildren().add(child);
		}
		TreeView<String> tree = new TreeView<String>(root);
		tree.setShowRoot(false);
		//center.getChildren().add(tree);
		
		BorderPane layout = new BorderPane();
		Label title = new Label("Long-Term Goals");
		title.setPadding(new Insets(10,10,10,10));
		title.setFont(new Font(title.getFont().toString(), 30));
		layout.setTop(title);
		layout.setCenter(tree);
		goalsTab.setContent(layout);
	}*/
	private void constructProjectsTab() {
		
	}
}
