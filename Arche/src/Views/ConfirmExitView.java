package Views;
import Runners.DBConn;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ConfirmExitView {
	static Stage window;
	static Label label;
	static Button yesButton, noButton;
	static boolean answer;
	public static boolean display(String text) {
		window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.UTILITY);
		window.setTitle("Confirm");
		window.setMinWidth(180);
		window.setMaxWidth(220);
		window.setMinHeight(80);
		window.setMaxHeight(120);
		window.setWidth(200);
		window.setHeight(100);
		
		label = new Label();
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setText(text);
		yesButton = new Button("Yes");
		noButton = new Button("No");
		
		yesButton.setOnAction(e -> {
			answer = true;
			window.close();
		});
		noButton.setOnAction(e -> {
			answer = false;
			window.close();
		});
		
		VBox topText = new VBox();
		topText.getChildren().add(label);
		topText.setAlignment(Pos.CENTER);
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.setSpacing(10);
		buttons.getChildren().addAll(yesButton, noButton);
		
		BorderPane layout = new BorderPane();
		layout.setTop(topText);
		layout.setCenter(buttons);
		
		Scene scene = new Scene(layout);
		//scene.getStylesheets().add("Views/dark.css");
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}
}
