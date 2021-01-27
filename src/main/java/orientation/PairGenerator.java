package orientation;

import altaite.geometry.primitives.AxisAngle;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
import altaite.geometry.primitives.Versor;
import altaite.geometry.superposition.Superposer;
import altaite.learn.Dataset;
import altaite.learn.MyInstance;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import structure.visual.PdbLine;

public class PairGenerator {

	private double sqrt3half = Math.sqrt(3) / 2;
	private Point[] triangle = {
		new Point(0, -1, 0),
		new Point(sqrt3half, 0.5, 0),
		new Point(-sqrt3half, 0.5, 0)};
	private Random random = new Random(1);
	private int objectCount = 100;
	private Superposer superposer = new Superposer();
	private Vectorizer vectorizer = new Vectorizer(triangle);

	public Point[] getBase() {
		return triangle;
	}

	private void run() {
		/*System.out.println(triangle[0].distance(triangle[1]));
		System.out.println(triangle[0].distance(triangle[2]));
		System.out.println(triangle[2].distance(triangle[1]));
		Point o = new Point(0, 0, 0);
		System.out.println(o.distance(triangle[0]));
		System.out.println(o.distance(triangle[1]));
		System.out.println(o.distance(triangle[2]));*/

		//Point[][] points = generatePairs();
		generateDataset();
	}

	private void generateDataset() {
		// align first to std, quat to second, stick after alignment - rot inv.
		// or inner distances
		Dataset dataset = new Dataset();
		int n = 5000;
		Point[][] points = new Point[2 * n][];
		for (int i = 0; i < n; i++) {
			Point[][] a = generateFixedDistancePair();
			Point[][] b = generateFixedDistancePair();
			points[2 * i] = merge(a);
			points[2 * i + 1] = merge(b);

			/*if (i == 0) {
				save(a, new File("d:/t/data/qsa/orientation/a.pdb"));
				save(b, new File("d:/t/data/qsa/orientation/b.pdb"));
			}*/
			MyInstance instance = new MyInstance(true);
			setFeatures(merge(a), merge(b), instance);
			//setApproximatingFeatures(a, b, instance);

			/*if (i == 30) {
				System.out.println(a[1][2]);
				System.out.println(merge(a)[3 + 2]);
			}*/
			instance.addNumeric(rmsd(merge(a), merge(b)));
			dataset.add(instance);
		}
		//save(points, new File("d:/t/data/qsa/orientation/points.pdb"));
		dataset.toArff(new File("d:/t/data/qsa/orientation/pairs.arff"));
	}

	// TODO try dual quaternions (fixed distance though)
	public double[] setApproximatingFeatures(Point[][] r1, Point[][] r2, MyInstance instance) {
		Quat a = vectorizer.align(r1[0], r2[0]);
		Quat b = vectorizer.align(r1[1], r2[1]);
		Quat c = vectorizer.align(r1[0], r1[1]);
		Quat d = vectorizer.align(r2[0], r2[1]);
		Quat e = vectorizer.align(r1[0], r2[1]);
		Quat f = vectorizer.align(r1[1], r2[0]);

		// try two aa options later
		/*AxisAngle aa = rotation.toAxisAngle();
		Point axis = aa.getAxis();
		double angle = aa.getAngle() / Math.PI;
		Point r = axis.multiply(angle);*/
		double[] coords = flatten(a, b, c, d, e, f);
		for (double coord : coords) {
			instance.addNumeric(coord);
		}
		return coords;
	}

	private double[] flatten(Quat... vs) {
		double[] a = new double[vs.length * 4];
		int i = 0;
		for (Quat v : vs) {
			a[i++] = v.x;
			a[i++] = v.y;
			a[i++] = v.z;
			a[i++] = v.w;
		}
		return a;
	}

	private double rmsd(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getRmsd();
	}

	private void generateDatasetFast(Point[][] points) {
		// align first to std, quat to second, stick after alignment - rot inv.
		// or inner distances
		Dataset dataset = new Dataset();
		for (int x = 0; x < points.length; x++) {
			for (int y = 0; y < x; y++) {
				double rmsd = rmsd(points[x], points[y]);
				MyInstance instance = new MyInstance(true);
				setFeatures(points[x], points[y], instance);
				instance.addNumeric(rmsd);
				dataset.add(instance);
			}
		}
		dataset.toArff(new File("d:/t/data/qsa/orientation/pairs.arff"));
	}

	private void setFeatures(Point[] a, Point[] b, MyInstance instance) {
		double[] fa = vectorizer.getCoordinates(a);
		for (double d : fa) {
			instance.addNumeric(d);
		}
		double[] fb = vectorizer.getCoordinates(b);
		for (double d : fb) {
			instance.addNumeric(d);
		}
	}

	private void setDumbFeatures(Point[] a, Point[] b, MyInstance instance) {
		for (int i = 0; i < a.length; i++) {
			instance.addNumeric(a[i].x);
			instance.addNumeric(a[i].y);
			instance.addNumeric(a[i].z);
		}
		for (int i = 0; i < b.length; i++) {
			instance.addNumeric(b[i].x);
			instance.addNumeric(b[i].y);
			instance.addNumeric(b[i].z);
		}
	}

	/*private void save(Point[][][] points, File file) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			int atomSerialNumber = 0;
			for (int x = 0; x < points.length; x++) {
				for (int y = 0; y < points[x].length; y++) {
					for (int z = 0; z < points[x][y].length; y++) {
						PdbLine line = new PdbLine(atomSerialNumber++, "H", "H", "Hah",
							x + "", 'A', points[x][y][z].x, points[x][y][z].y, points[x][y][z].z);
						bw.write(line + "\n");
					}
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}*/
	private void save(Point[][] points, File file) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			int atomSerialNumber = 0;
			for (int x = 0; x < points.length; x++) {
				bw.write("MODEL        " + x + "\n");
				for (int y = 0; y < points[x].length; y++) {
					PdbLine line = new PdbLine(atomSerialNumber++, "H", "H", "Hah",
						x + "", 'A', points[x][y].x, points[x][y].y, points[x][y].z);
					bw.write(line + "\n");
				}
				bw.write("ENDMDL                                                              \n");
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private Point[][] generatePairs() {
		Point[][] points = new Point[objectCount][];
		for (int i = 0; i < points.length; i++) {
			points[i] = generatePair();
		}
		save(points, new File("d:/t/data/qsa/orientation/oo.pdb"));
		return points;
	}

	public Point[] generatePair() {
		Point[] triangle1 = rotate(triangle);
		Point[] triangle2 = rotate(triangle);

		Point u = Point.unit(random);
		double halfDistance = (random.nextDouble() * 3 + 2) / 2; // try this constant
		triangle1 = move(triangle1, u.multiply(halfDistance));
		triangle2 = move(triangle2, u.multiply(-halfDistance));

		Point[] all = merge(triangle1, triangle2);

		Point v = Point.unit(random);
		double distance = random.nextDouble() * 50;
		all = move(all, v.multiply(distance));
		return all;
	}

	public Point[][] generateFixedDistancePair() {
		Point[] triangle1 = rotate(triangle);
		Point[] triangle2 = rotate(triangle);

		Point u = Point.unit(random);
		//System.out.println(" " + u);
		//Point u = new Point(1,0,0);//Point.unit(random);
		//double halfDistance = 8 / 2 / 2; // total distance 4, cause triangle is 2x smaller than residue 
		double halfDistance = 10; // make it easire to start
		triangle1 = move(triangle1, u.multiply(halfDistance));
		triangle2 = move(triangle2, u.multiply(-halfDistance));

		Point[][] result = {triangle1, triangle2};
		return result;
	}

	private Point[] rotate(Point[] points) {
		Point u = Point.unit(random);
		//System.out.println(u);
		//double angle = (Math.random() * 2 - 1) * Math.PI / 10;
		double angle = (Math.random() * 2 - 1) * 2 * Math.PI;
		Versor v = Versor.create(new AxisAngle(u, angle));
		//AxisAngle aa = v.toAxisAngle();
		return rotate(points, v);
	}

	private Point[] move(Point[] points, Point v) {
		Point[] moved = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			moved[i] = points[i].plus(v);
		}
		return moved;
	}

	private Point[] rotate(Point[] points, Versor versor) {
		Point[] rotated = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			rotated[i] = versor.rotate(points[i]);
		}
		return rotated;
	}

	private Point[] merge(Point[][] a) {
		return merge(a[0], a[1]);
	}

	private Point[] merge(Point[] a, Point[] b) {
		Point[] c = new Point[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static void main(String[] args) {
		PairGenerator oo = new PairGenerator();
		oo.run();
	}
}
