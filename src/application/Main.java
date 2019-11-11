package application;

import java.io.IOException;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	final static int NONE = 0, RED = 1, BLUE = 2, YELLOW = 3, GREEN = 4, ORANGE = 5, WHITE = 6, RANDOM = 7, MULTI = 8;

	private AnchorPane mainLayout;

	@Override
	public void start(Stage primaryStage) throws IOException {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("View.fxml"));
			mainLayout = loader.load();
			ViewController vc = loader.getController();
			primaryStage.setTitle("Julia & Mandelbrot fractals");
			primaryStage.setResizable(true);
			primaryStage.setScene(new Scene(mainLayout));
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
