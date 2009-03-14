package CircusOfPlates;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import Paint.Shapes.RectangleShape;

/**
 * The Plate class that represents the logic for drawing and shifting the plate with the required attributes
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see RectangleShape
 * @since JDK 1.6
 */
public class Plate implements Externalizable {

	/**
	 * The default plate width in pixels to be drawn on the screen
	 */
	public static final int PLATE_WIDTH = 50;

	/**
	 * The Default Plate Height in pixels to be drawn on the screen
	 */
	public static final int PLATE_HEIGHT = 10;

	/**
	 * The update step angle the plate should be rotated with when lying from the bar
	 */
	public static final int ANGLE_CONST = 5;

	/**
	 * The initial Plate Spacing between the start of the next plate to the start of the current plate
	 */
	public static int plateSpacing = 60;

	/**
	 * The shape to be drawn as delegating all method calls to the rectangle shape previously
	 * deigned in the paint project
	 */
	private RectangleShape plate;

	/**
	 * Create a plate with the given attributes
	 * @param color the color of the Plate
	 * @param start the start point the plate should be moving from initially
	 * @param LeftToRight determines if the plate will be moving left to right or from right to left
	 */
	public Plate(Color color, Point start, boolean LeftToRight) {
		plate = new RectangleShape();
		plate.setColor(color);
		plate.setFilled(true);
		ArrayList<Point> points = plate.getPoints();
		if (LeftToRight) {
			points.add(new Point(start.x - PLATE_WIDTH, start.y - PLATE_HEIGHT));
			points.add(new Point(start.x, start.y - PLATE_HEIGHT));
			points.add(new Point(start.x - ANGLE_CONST, start.y));
			points.add(new Point(start.x - PLATE_WIDTH + ANGLE_CONST, start.y));
		} else {
			points.add(new Point(start.x, start.y - PLATE_HEIGHT));
			points.add(new Point(start.x + PLATE_WIDTH, start.y - PLATE_HEIGHT));
			points.add(new Point(start.x + PLATE_WIDTH - ANGLE_CONST, start.y));
			points.add(new Point(start.x + ANGLE_CONST, start.y));
		}
	}

	/**
	 * Delegates the method call to the plate draw method
	 * @param gc the Graphical context on which the plate should be drawn on
	 */
	public void draw(GC gc) {
		plate.draw(gc);
	}

	/**
	 * shifts the plate horizontally with the required distance; mainly called by the bar moving the plate
	 * or the sky with air factor
	 * @see PlateBar
	 * @see PlateSky
	 * @param updateStep the step the plate should be shifted with
	 */
	public void shiftX(int updateStep) {
		plate.relocate(updateStep, 0);
	}

	/**
	 * shifts the plate vertically with the required distance; mainly called by the sky
	 * @param updateStep the step the plate should be shifted with
	 * @see PlateSky
	 */
	public void shiftY(int updateStep) {
		plate.relocate(0, updateStep);
	}

	/**
	 * shifts the plate horizontally and vertically with the required distance; mainly called by the sky
	 * @param updateStep the step the plate should be shifted with
	 * @see PlateSky
	 */
	public void shift(int x, int y) {
		plate.relocate(x, y);
	}

	/**
	 * @return the bounding rectangle of the plate shape
	 */
	public Rectangle getBounds() {
		return plate.getBounds();
	}

	/**
	 * @param pt the point to check containing
	 * @return true if the shape contains the given point; false otherwise
	 */
	public boolean contains(Point pt) {
		return plate.contains(pt);
	}

	/**
	 * @return the color of the plate
	 */
	public Color getColor() {
		return plate.getColor();
	}

	/**
	 * relocate the plate to new Coordinates in which lies above the top point
	 * @param top the point on which the plate should be lying on
	 */
	public void setBelow(Point top) {
		Point center = plate.calculateCenter();
		int shiftX = top.x - center.x;
		int shiftY = top.y - plate.getPoints().get(2).y;
//		plate.relocate(0, shiftY); 
		plate.relocate(shiftX, shiftY);
		top.y -= Plate.PLATE_HEIGHT;
//		top.x = center.x; // remove
	}

	/**
	 * rotates the plate with the givven angle
	 * @param d the theta with which the plate should be rotated
	 */
	public void rotate(double d) {
		plate.rotate(d);
	}

	/**
	 * sets the plate spacing between each start to a given value
	 * @param spacing the new spacing between plates
	 */
	public static void setPlateSpacing(int spacing) {
		plateSpacing = spacing;
	}

	/**
	 * reads the object from a stream
	 * @param in the input stream to read the object from
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		plateSpacing = in.readInt();
		plate = (RectangleShape) in.readObject();
	}

	/**
	 * writes the plate to a stream
	 * @param out the output stream to write the object in
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(plateSpacing);
		out.writeObject(plate);
	}
}
