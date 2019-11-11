package application;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public class Zoom {

	/*
	 * Cette methode prend une imageView, le pane dans laquelle elle est,et sa
	 * taille
	 */
	/* Permet de changer le viewport de cette image */
	/* Utiliser la mollette de la souris pour zoomer/dezoomer */
	/* Maintenir le clic de souris et bouger pour se deplacer dans l'image zoomée */
	/* Double cliquer n'importe ou dans l'image pour reinitialiser le zoom */

	public static void setZoomable(ImageView imageView, Pane pane, int height, int width) {
		imageView.setViewport(new Rectangle2D(0, 0, height, width));

		ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();
		imageView.setPreserveRatio(true);
		imageView.setOnMousePressed(e -> {

			/* On recupere les coordonnées du clic*/
			Point2D mousePress = imageViewToImage(imageView, new Point2D(e.getX(), e.getY()));
			mouseDown.set(mousePress);
		});

		/* Le deplacement */
		imageView.setOnMouseDragged(e -> {
			Point2D dragPoint = imageViewToImage(imageView, new Point2D(e.getX(), e.getY()));
			move(imageView, dragPoint.subtract(mouseDown.get()));
			mouseDown.set(imageViewToImage(imageView, new Point2D(e.getX(), e.getY())));
		});
        
		/* Le zoom */
		imageView.setOnScroll(e -> {
			double delta = -e.getDeltaY();
			Rectangle2D viewport = imageView.getViewport();

			double scale = adjust(Math.pow(1.01, delta),
					// On empeche de zoomer a plus de 10 pixels
					Math.min(10 / viewport.getWidth(), 10 / viewport.getHeight()),

					// On empeche de dezoomer de façon a depasser les dimensions de l'image
					Math.max(width / viewport.getWidth(), height / viewport.getHeight())

			);

			Point2D mouse = imageViewToImage(imageView, new Point2D(e.getX(), e.getY()));

			double newWidth = viewport.getWidth() * scale;
			double newHeight = viewport.getHeight() * scale;

			double newMinX = adjust(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale, 0, width - newWidth);
			double newMinY = adjust(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale, 0, height - newHeight);
  
			/* On definit le nouveau viewport calculé */
			imageView.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
		});

		/* Double cliquer pour reinitialiser le zoom */
		imageView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				reset(imageView, width, height);
			}
		});

	}

	private static void reset(ImageView imageView, double width, double height) {
		imageView.setViewport(new Rectangle2D(0, 0, width, height));
	}

	/*
	 * Methode qui deplace le point de vue actuel tout en empechant de sortir de
	 * l'image en utilisant adjust
	 */
	private static void move(ImageView imageView, Point2D delta) {
		Rectangle2D viewport = imageView.getViewport();

		double width = imageView.getImage().getWidth();
		double height = imageView.getImage().getHeight();

		double maxX = width - viewport.getWidth();
		double maxY = height - viewport.getHeight();
       
		/* On ajuste pour ne pas deborder */
		double minX = adjust(viewport.getMinX() - delta.getX(), 0, maxX);
		double minY = adjust(viewport.getMinY() - delta.getY(), 0, maxY);

		imageView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
	}

	private static double adjust(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	/*
	 * Methode qui convertit les coordonnees dans la vue actuelle en coordonnee de
	 * la vraie image
	 */
	private static Point2D imageViewToImage(ImageView imageView, Point2D imageViewCoordinates) {
		double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
		double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

		Rectangle2D viewport = imageView.getViewport();
		return new Point2D(viewport.getMinX() + xProportion * viewport.getWidth(),
				viewport.getMinY() + yProportion * viewport.getHeight());
	}

}
