package application;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class ViewController implements Initializable {

	@FXML
	private Pane pane, Cpane, Coulpane;

	@FXML
	private TextField reel;

	@FXML
	private TextField imaginaire, xmin, ymin, xmax, ymax;

	@FXML
	Button b1, b2, b3, b4, b5, b6, b7, b8, defC, couleur, valider, reset, closeC, closeCoul, cancel, julia, mandelbrot,
			save, random;

	@FXML
	Slider s1, s3;

	@FXML
	ProgressBar prog;

	Task<?> task;
	int currentColor = 5;
	double currentR, currentIM;
	boolean randomClicked = false, juliaOrMandelbrot = true;

	private ArrayList<ImageView> list;

	/* Methode de rafraichissement de l'image courante */
	/* Utilise les données rentrées dans l'interface graphique pour generer un ensemble */
	@FXML
	public void refresh() throws IOException {
		prog.setVisible(true);
		Complex c = new Complex(Double.parseDouble(reel.getText()), Double.parseDouble(imaginaire.getText()));
		currentR = Double.parseDouble(reel.getText());
		currentIM = Double.parseDouble(imaginaire.getText());
		Bornes b = new Bornes(Double.parseDouble(xmin.getText()), Double.parseDouble(ymin.getText()),
				Double.parseDouble(xmax.getText()), Double.parseDouble(ymax.getText()));
		if (juliaOrMandelbrot == true) {
			task = new Julia((int) s3.getValue(), b, c, (int) s1.getValue(), pane);
		} else {
			task = new Mandelbrot((int) s3.getValue(), b, (int) s1.getValue(), pane);
		}

		prog.progressProperty().bind(task.progressProperty());
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				list = (ArrayList<ImageView>) task.getValue();
				Cpane.setVisible(false);
				Coulpane.setVisible(false);
				prog.setVisible(false);
				reset.setVisible(true);
				valider.toFront();
				if (randomClicked == true) {
					for (int i = 0; i < list.size(); i++) {
						if (pane.getChildren().contains(list.get(i))) {
							pane.getChildren().remove(list.get(i));
						}
					}
					pane.getChildren().add(list.get((int) (Math.random() * 6)));
					randomClicked = false;
				} else {
					pane.getChildren().add(list.get(5));

				}
				reset.toFront();

			}
		});
		new Thread(task).start();
		cancel.toFront();
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent a) {
				task.cancel();
				valider.toFront();
			}
		});
	}
    
	/* Methode de changement de couleur actuelle */
	@FXML
	public void refreshColor(ActionEvent event) throws IOException {
		Rectangle2D viewport = new Rectangle2D(0, 0, 600, 600);
		Button btn = (Button) event.getSource();
		for (int i = 0; i < list.size(); i++) {
			if (pane.getChildren().contains(list.get(i))) {
				viewport = list.get(i).getViewport();
				pane.getChildren().remove(list.get(i));
			}
		}
		if (Character.getNumericValue(btn.getId().charAt(1)) < 8) {
			currentColor = Character.getNumericValue(btn.getId().charAt(1)) - 1;
		} else {
			currentColor = (int) (Math.random() * 7);
		}
		pane.getChildren().add(list.get(currentColor));
		list.get(currentColor).setViewport(viewport);
		Cpane.setVisible(false);
		Coulpane.setVisible(false);
		reset.toFront();

	}
    /* Methode qui affiche/Cache le panneau des reglages */
	@FXML
	public void showC() {
		Cpane.toFront();
		Cpane.setVisible(!Cpane.isVisible());
	}
    /* Methode qui affiche/Cache le panneau des couleurs */

	@FXML
	public void showCouleur() {
		Coulpane.toFront();
		Coulpane.setVisible(!Coulpane.isVisible());
	}

	/* Methode qui reinitialise le zoom */
	@FXML
	public void reset() {

		for (int i = 0; i < list.size(); i++) {
			if (pane.getChildren().contains(list.get(i))) {
				list.get(i).setViewport(
						new Rectangle2D(0, 0, list.get(i).getImage().getHeight(), list.get(i).getImage().getHeight()));
			}
		}

	}
     /* Methode de choix de l'ensemble de Mandelbrot */
	@FXML
	public void mandleclick() {
		mandelbrot.setDisable(true);
		julia.setDisable(false);
		reel.setDisable(true);
		imaginaire.setDisable(true);
		juliaOrMandelbrot = false;
		closeC.toFront();
		
		

	}
    /* Methode de choix des ensembles de Julia */

	@FXML
	public void juliaclick() {
		julia.setDisable(true);
		mandelbrot.setDisable(false);
		reel.setDisable(false);
		imaginaire.setDisable(false);
		juliaOrMandelbrot = true;
		closeC.toFront();
	}
    
	/* Methode d'enregistrement de l'image sur le disque dur */
	@FXML
	public void save() {

		int num = 1;
		String sign;

		if (currentIM >= 0)
			sign = "+";
		else
			sign = "";

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
		fc.setInitialFileName("[" + currentR + sign + currentIM + " i ] " + num);
		File f = fc.showSaveDialog(null);
		BufferedImage bImage = null;
		bImage = SwingFXUtils.fromFXImage(list.get(currentColor).getImage(), null);

		try {
			if (f != null)
				ImageIO.write(bImage, "png", f);
			else
				System.out.println("nop");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    
	/* Methode pour afficher un ensemble aleatoire avec des nombres C dont les parties sont comprises entre -1 et 1 */
	@FXML
	public void random() throws IOException {
		reel.setText(Double.toString((Math.random() * 2) % 2 - 1));
		imaginaire.setText(Double.toString((Math.random() * 2) % 2 - 1));
		randomClicked = true;
		s3.setValue(600);
		s1.setValue(200);
		refresh();

	}
   
	/* Methode d'initialisation, on affiche un ensemble connu au lancement du programme */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
        
		reel.setText("0.285");
		imaginaire.setText("0.01");
		try {
			refresh();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		closeC.toFront();
		valider.setOnMouseEntered(e -> valider.setStyle("-fx-background-color: #707070;"));
		valider.setOnMouseExited(e -> valider.setStyle("-fx-background-color: #535353;"));
		cancel.setOnMouseEntered(e -> cancel.setStyle("-fx-background-color: #707070;"));
		cancel.setOnMouseExited(e -> cancel.setStyle("-fx-background-color: #535353;"));

	}

}
