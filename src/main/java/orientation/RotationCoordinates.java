package orientation;

import altaite.geometry.primitives.AxisAngle;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Versor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import structure.visual.PdbLine;

public class RotationCoordinates {

	private double sqrt3half = Math.sqrt(3) / 2;
	private Point[] triangle = {
		new Point(0, -1, 0),
		new Point(sqrt3half, 0.5, 0),
		new Point(-sqrt3half, 0.5, 0)};
	private Random random = new Random(1);

	public Point[] getBase() {
		return triangle;
	}

	private void run() {
		generateTriangles();
		compare();

	}
	
	// angle between two triangles vs. quat/axis coord diff
	// improve by forces optimization?
	private void compare() {

	}

	private void generateTriangles() {
		Point[][] points = new Point[1][];
		for (int i = 0; i < points.length; i++) {
			points[i] = generateRotatedTriangle();
		}
		save(points, new File("d:/t/data/qsa/orientation/rotated_triangles.pdb"));
	}

	public Point[] generateRotatedTriangle() {
		return rotate(triangle);
	}

	private Point[] rotate(Point[] points) {
		Point u = Point.unit(random);
		double angle = (Math.random() * 2 - 1) * Math.PI;
		Versor v = Versor.create(new AxisAngle(u, angle));
		AxisAngle aa = v.toAxisAngle();
		return rotate(points, v);
	}

	private Point[] rotate(Point[] points, Versor versor) {
		Point[] rotated = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			rotated[i] = versor.rotate(points[i]);
		}
		return rotated;
	}

	private void save(Point[][] points, File file) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			int atomSerialNumber = 0;
			for (int x = 0; x < points.length; x++) {
				for (int y = 0; y < points[x].length; y++) {
					PdbLine line = new PdbLine(atomSerialNumber++, "H", "H", "Hah",
						x + "", 'A', points[x][y].x, points[x][y].y, points[x][y].z);
					bw.write(line + "\n");
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void main(String[] args) {
		RotationCoordinates oo = new RotationCoordinates();
		oo.run();
	}
}
