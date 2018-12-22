package Runners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import Views.ConfirmExitView;
import Views.LoginController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class main extends Application{
	Stage window;
	public static void main(String args[]) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws IOException {
		window = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/login.fxml"));
		Parent root = loader.load();
		
		LoginController controller = loader.getController();
		controller.setStage(window);
		
		window.getIcons().add(new Image("file:./src/resources/icons/icons8-idea-40.png"));
		window.initStyle(StageStyle.UNDECORATED);
		window.setResizable(false);
		window.setScene(new Scene(root));
		window.show();
	}
}
