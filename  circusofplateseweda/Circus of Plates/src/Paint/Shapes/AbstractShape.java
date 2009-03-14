package Paint.Shapes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import Paint.Constants;


/**
 * provides a default behavior for the shapes
 * @author  Mostafa Mahmoud Mahmoud Eweda
 * @version  1.0
 * @since  JDK 1.6
 */
public abstract class AbstractShape implements ShapeIF {

	protected Color color;
	private ShapeState currentState;
	protected boolean selected;
	protected boolean filled;
	protected ArrayList<Point> points;
	protected int width;

	public AbstractShape() {
		currentState = new ShapeNotSelected();
		filled = false;
	}

	/**
	 * @return the color of the shape
	 */
	@Override
	public final Color getColor() {
		return color;
	}

	@Override
	public final void setColor(Color color) {
		this.color = color;
	}

	@Override
	public final boolean isSelected() {
		return selected;
	}

	public final void select() {
		this.selected = true;
		currentState = new ShapeSelected();
	}

	public final void deselect() {
		this.selected = false;
		currentState = new ShapeNotSelected();
	}

	/**
	 * @return list of points forming the shape
	 */
	@Override
	public final ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 * @return the number of added points till now
	 */
	public int getAddedPoints() {
		return points.size();
	}

	public boolean contains(Point pt) {
		return GraphicsUtils.insidePolygon(pt, points);
	}

	public void addPoint(int ptX, int ptY) {
		if (addable())
			points.add(new Point(ptX, ptY));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		RGB rgb = new RGB(0, 0, 0);
		points = (ArrayList<Point>) in.readObject();
		rgb.red = in.readInt();
		rgb.green = in.readInt();
		rgb.blue = in.readInt();
		width = in.readInt();
		color = new Color(Display.getCurrent(), rgb);
		selected = false;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		RGB rgb = color.getRGB();
		out.writeObject(points);
		out.writeInt(rgb.red);
		out.writeInt(rgb.green);
		out.writeInt(rgb.blue);
		out.writeInt(width);
	}

	public void relocate(int shiftX, int shiftY) {
		Iterator<Point> iter = points.iterator();
		Point current;
		while (iter.hasNext()) {
			current = iter.next();
			current.x += shiftX;
			current.y += shiftY;
		}
	}

	public Rectangle getBounds() {
		Iterator<Point> iter = points.iterator();
		Point current = iter.next();
		int minX = current.x, minY = current.y, maxX = current.x, maxY = current.y;
		while (iter.hasNext()) {
			current = iter.next();
			if (current.x < minX)
				minX = current.x;
			else if (current.x > maxX)
				maxX = current.x;
			if (current.y < minY)
				minY = current.y;
			else if (current.y > maxY)
				maxY = current.y;
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	protected void fillPolygon(GC gc) {
		int[] polygon = new int[points.size() * 2];
		Iterator<Point> iter = points.iterator();
		Point current;
		int i = 0;
		while (iter.hasNext()) {
			current = iter.next();
			polygon[i++] = current.x;
			polygon[i++] = current.y;
		}
		gc.fillPolygon(polygon);
	}

	protected void connectPoints(GC gc) {
		Iterator<Point> iter = points.iterator();
		Point current = iter.next(), next;
		while (iter.hasNext()) {
			next = iter.next();
			gc.drawLine(current.x, current.y, next.x, next.y);
			current = next;
		}
	}

	protected void connectFirstLast(GC gc) {
		Point first = points.get(0);
		Point last = points.get(points.size() - 1);
		gc.drawLine(first.x, first.y, last.x, last.y);
	}

	public final void rotate(double theta) {
		Point center = calculateCenter();
		Iterator<Point> iter = points.iterator();
		while (iter.hasNext())
			GraphicsUtils.rotatePoint(iter.next(), center, theta);
	}

	public Point calculateCenter() {
		Rectangle rectangle = getBounds();
		return new Point(rectangle.x + rectangle.width/2, rectangle.y + rectangle.height/2);
	}

	public String getToolType() {
		return "click";
	}

	@Override
	public void draw(GC gc) {
		currentState.draw(gc);
		gc.setLineStyle(SWT.LINE_CUSTOM);
		gc.setLineWidth(width);
		gc.setForeground(color);
		gc.setBackground(color);
	}

	/**
	 * @param width
	 */
	public final void setWidth(int width) {
		this.width = width;
	}

	public void zoom(int zoom) {
		double factor = (double) zoom / 100;
		Iterator<Point> iter = points.iterator();
		Point current;
		while (iter.hasNext()) {
			current = iter.next();
			current.x += current.x * factor;
			current.y += current.y * factor;
		}
	}

	/**
	 * @return true if the shape is filled
	 */
	public boolean getFilled() {
		return filled;
	}

	public void flip(int flipMode) {
		Rectangle rect = getBounds();
		Iterator<Point> iter = points.iterator();
		Point current;
		if (flipMode == Constants.FLIP_HORIZONTAL) {
			while (iter.hasNext()) {
				current = iter.next();
				current.x = 2 * rect.x + (rect.width - current.x);
			}
		} else if (flipMode == Constants.FLIP_VERTICAL) {
			while (iter.hasNext()) {
				current = iter.next();
				current.y = 2 * rect.y + (rect.height - current.y);
			}
		}
		else
			throw new IllegalArgumentException();
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	private abstract class ShapeState {
		public abstract void draw(GC gc);
	}

	private class ShapeSelected extends ShapeState {
		@Override
		public void draw(GC gc) {
				Iterator<Point> iter = points.iterator();
				gc.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_GREEN));
				Point current;
				while (iter.hasNext()) {
					current = iter.next();
					gc.fillOval(current.x-5, current.y-5, 10, 10);
				}
		}
	}

	private class ShapeNotSelected extends ShapeState {
		@Override
		public void draw(GC gc) {
			// empty
		}

	}

	public final int getWidth(){
		return width;
	}
}