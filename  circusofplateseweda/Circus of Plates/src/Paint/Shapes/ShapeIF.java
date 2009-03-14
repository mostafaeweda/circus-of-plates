package Paint.Shapes;
import java.io.Externalizable;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @see AbstractShape   provides the default implementations for the shapes
 * @see Externalizable
 * @see Cloneable
 * @author  Mostafa Mahmoud Mahmoud Eweda
 * @version   1.0
 * @since   JDK 1.6
 */
public interface ShapeIF extends Externalizable, Cloneable {

	/**
	 * draw the current shape updated by the given point
	 * @param gc the graphics context 
	 * @param currentPoint the current location of the cursor as a point
	 */
	public void draw(GC gc, Point currentPoint);

	/**
	 * draw the current shape
	 * @param gc the graphics context
	 */
	public void draw(GC gc);

	/**
	 * checks the shape if it contains a given point
	 * @param pt the given point
	 */
	public boolean contains(Point pt);

	/**
	 * selects the shape
	 */
	public void select();

	/**
	 * returns the color of the shape
	 * @return   the color of the shape
	 */
	public Color getColor();

	/**
	 * sets the current color of the shape
	 * @param color   the color to be changed
	 */
	public void setColor(Color color);

	/**
	 * gets the bounding rectangle of the shape
	 * @return a rectangle containing of x, y the location of the current drawing,
	 * width of the shape, height of the shape
	 */
	public Rectangle getBounds();

	/**
	 * deselects the shape
	 */
	public void deselect();

	/**
	 * shift the shape location by the given shift
	 * @param shiftX the x shift
	 * @param shiftY the x shift
	 */
	public void relocate(int shiftX, int shiftY);

	/**
	 * @return true if the shape is selected
	 */
	public boolean isSelected();

	/**
	 * @return a copy of the shape
	 */
	public ShapeIF copy();

	/**
	 * rotates the shape by the given angle
	 * @param theta
	 */
	public void rotate(double theta);

	/**
	 * adds a point to the shape
	 * @param ptX
	 * @param ptY
	 */
	public void addPoint(int ptX, int ptY);

	/**
	 * @return a list of the points of the shape
	 */
	public ArrayList<Point> getPoints();

	/**
	 * @return true if we can add other points to the shape
	 */
	public boolean addable();

	/**
	 * @return an integer representing number of points in the shape
	 */
	public int getAddedPoints();

	/**
	 * @return a string re[resenting the type of drawing the shape can be drawn with
	 */
	public String getToolType();
	
	/**
	 * @param width   the width of the line the shape should be drawn with
	 */
	public void setWidth(int width);

	/**
	 * scales the shape with the given zoom value
	 * @param zoom the shape should be zoomed with
	 */
	public void zoom(int zoom);

	/**
	 * @return   true if the shape if filled
	 */
	public boolean getFilled();

	/**
	 * sets the shape to be filled or not
	 * @param  filled
	 */
	public void setFilled(boolean filled);

	/**
	 * @param flipMode the mode of the flipping as integer
	 * it would be either Constants.FLIP_HORIZONTAL or Constants.FLIP_VERTICAL
	 */
	public void flip(int flipMode);

	/**
	 * Gets the center the shape
	 * @return a point representing x and y concerned with the location of the shape
	 */
	public Point calculateCenter();

	/**
	 * @return   the width of the shape
	 */
	public int getWidth();
}
