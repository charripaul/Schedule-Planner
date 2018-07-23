package Runners;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import Views.ConfirmExitView;
import Views.LoginController;
import Views.MainView;
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
		
		window.getIcons().add(new Image("file:resources/icons/icons8-idea-40.png"));
		window.initStyle(StageStyle.UNDECORATED);
		window.setResizable(false);
		window.setScene(new Scene(root));
		window.show();
		
		/*window.setTitle("Arche");
		window.setMinWidth(900);
		window.setMinHeight(600);
		window.setOnCloseRequest(e -> {
			e.consume();
			closeProgram(ConfirmExitView.display("Are you sure you want to exit?"));
		});
		
		window.setScene(new Scene(root));
		window.show();
		
		Stage loginWindow = new Stage();
		Parent root2 = FXMLLoader.load(getClass().getResource("/Views/login.fxml"));
		loginWindow.setTitle("Arche");
		loginWindow.getIcons().add(new Image("file:resources/icons/icons8-idea-40.png"));
		loginWindow.initModality(Modality.APPLICATION_MODAL);
		loginWindow.initStyle(StageStyle.UTILITY);
		loginWindow.setOnCloseRequest(e -> {
			//DBConn.closeConnection();
		});
		loginWindow.setScene(new Scene(root2));
		loginWindow.showAndWait();*/
	}
	public void showLoginWindow() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/login.fxml"));
		Parent root = loader.load();
		Stage stage = new Stage();
		
		LoginController controller = loader.getController();
		controller.setStage(stage);					//send stage to controller
		
		stage.getIcons().add(new Image("file:resources/icons/icons8-idea-40.png"));
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setResizable(false);
		stage.setScene(new Scene(root));
		stage.showAndWait();
	}
}
