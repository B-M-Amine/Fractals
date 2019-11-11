package application;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

public class Julia extends Task<ArrayList<ImageView>> {

	int size, precision;
	Bornes bornes;
	Complex c;
	Pane pane;

	public Julia(int size, Bornes bornes, Complex c, int precision, Pane pane) {
		super();
		this.size = size;
		this.bornes = bornes;
		this.precision = precision;
		this.c = c;
		this.pane = pane;
	}

	/* Methode de création de l'ensembe de julia */
	 /* Utilise les caracteristiques de l'ensemble et enregistre celui ci dans une image PNG dans le chemin path */
	@Override
	protected ArrayList<ImageView> call() throws IOException {

		if (size % 2 == 1)
			size++;
	

		WritableImage image1 = new WritableImage(size, size);
		WritableImage image2 = new WritableImage(size, size);
		WritableImage image3 = new WritableImage(size, size);
		WritableImage image4 = new WritableImage(size, size);
		WritableImage image5 = new WritableImage(size, size);
		WritableImage image6 = new WritableImage(size, size);
		WritableImage image7 = new WritableImage(size, size);

		PixelWriter pw1 = image1.getPixelWriter();
		PixelWriter pw2 = image2.getPixelWriter();
		PixelWriter pw3 = image3.getPixelWriter();
		PixelWriter pw4 = image4.getPixelWriter();
		PixelWriter pw5 = image5.getPixelWriter();
		PixelWriter pw6 = image6.getPixelWriter();
		PixelWriter pw7 = image7.getPixelWriter();

		int i, j, k, k1, h, g = 0, index;

		Complex number = new Complex(bornes.getXmin(), bornes.getYmin());
		double step = (bornes.getXmax() - bornes.getXmin()) / size;

		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				index = Complex.divergenceIndex(number, c, precision);
				if (index == precision + 1) {
					pw1.setArgb(i, j, new Color(0, 0, 0).getRGB());
					pw2.setArgb(i, j, new Color(0, 0, 0).getRGB());
					pw3.setArgb(i, j, new Color(0, 0, 0).getRGB());
					pw4.setArgb(i, j, new Color(0, 0, 0).getRGB());
					pw5.setArgb(i, j, new Color(0, 0, 0).getRGB());
					pw6.setArgb(i, j, new Color(0, 0, 0).getRGB());
					pw7.setArgb(i, j, new Color(0, 0, 0).getRGB());

				} else {

					k = (int) (index * 10);
					g = (int) (index * 20);
					h = (int) (index * 30);
					k1 = (int) (index * 10);

					if (k > 255) {
						k = 255;
					}

					if (k1 > 255) {
						k1 = k1 % 255;
					}
					if (g > 255) {
						g = g % 255;
					}
					if (h > 255) {
						h = h % 255;
					}

					pw1.setArgb(i, j, new Color(k, 0, 0).getRGB());
					pw2.setArgb(i, j, new Color(0, 0, k).getRGB());
					pw3.setArgb(i, j, new Color(k, k, 0).getRGB());
					pw4.setArgb(i, j, new Color(0, k, 0).getRGB());
					pw5.setArgb(i, j, new Color(k, (int) (k * 0.30), 0).getRGB());
					pw6.setArgb(i, j, new Color(k1, g, h).getRGB());
					pw7.setArgb(i, j, new Color(255, 255, 255).getRGB());

				}

				number = Complex.sub(number, new Complex(0, step));

			}
			number.setY(bornes.getYmin());
			number = Complex.sum(number, new Complex(step, 0));
			updateProgress(i, size);
			if (this.isCancelled()) {
				updateProgress(0, size);

				return null;
			}
		}

		ArrayList<ImageView> list = new ArrayList<ImageView>();

		list.add(new ImageView(image1));
		list.add(new ImageView(image2));
		list.add(new ImageView(image3));
		list.add(new ImageView(image4));
		list.add(new ImageView(image5));
		list.add(new ImageView(image6));
		list.add(new ImageView(image7));

		for (int a = 0; a < list.size(); a++) {
			list.get(a).fitHeightProperty().bind(pane.heightProperty());
			list.get(a).fitWidthProperty().bind(pane.widthProperty());

			Zoom.setZoomable(list.get(a), pane, size, size);
		}

		return list;

	}

}
