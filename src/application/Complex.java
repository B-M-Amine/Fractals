package application;

public class Complex {

	private double x, y;

	public Complex(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public static int divergenceIndex(Complex x0, Complex c, int maxIteration) {
		int ite = 0;
		Complex xn = x0;
		while (ite++ < maxIteration && xn.mod() <= 2.0) {
			xn = Complex.sum(c, Complex.mul(xn, xn));
		}
		return ite;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Complexe [Partie réele = " + x + ", Partie imaginaire = " + y + "]";
	}

	public static Complex sum(Complex z, Complex k) {
		Complex t = new Complex(0, 0);
		t.setX(z.getX() + k.getX());
		t.setY(z.getY() + k.getY());

		return t;

	}

	public static Complex sub(Complex z, Complex k) {
		Complex t = new Complex(0, 0);
		t.setX(z.getX() - k.getX());
		t.setY(z.getY() - k.getY());

		return t;

	}

	public static Complex mul(Complex z, Complex k) {
		Complex t = new Complex(0, 0);
		t.setX(z.getX() * k.getX() - z.getY() * k.getY());
		t.setY(z.getX() * k.getY() + z.getY() * k.getX());
		return t;
	}

	public static Complex div(Complex z, Complex k) {
		Complex t = new Complex(0, 0);
		Complex temp1 = new Complex(0, 0);
		Complex temp2 = new Complex(0, 0);

		temp1 = mul(z, complement(k));
		temp2 = mul(k, complement(k));
		t.setX(temp1.x / temp2.x);
		t.setY(temp1.y / temp2.x);

		return t;
	}

	public static Complex complement(Complex z) {
		return new Complex(z.getX(), z.getY() * (-1));
	}

	public double mod() {
		double m = Math.pow(this.x * this.x + this.y * this.y, 0.5);
		return m;
	}

}
