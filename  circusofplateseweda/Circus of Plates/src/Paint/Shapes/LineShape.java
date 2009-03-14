package Paint.Shapes;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Triangle shape
 * @see AbstractShape extending from
 * @author  Mostafa Mahmoud Mahmoud Eweda
 * @version 1.0
 * @since JDK 1.6
 */
public class LineShape extends AbstractShape {

	private static final int POINTS_NUM = 2;

	public LineShape() {
		super();
		points = new ArrayList<Point>(POINTS_NUM);
	}

	@Override
	public void draw(GC gc, Point currentPt) {
		super.draw(gc);
		int size = points.size();
		int[] drawn = new int[2 * size];
		Iterator<Point> iter = points.iterator();
		Point current;
		for (int i = 0; iter.hasNext(); i++) {
			current = iter.next();
			drawn[2 * i] = current.x;
			drawn[2 * i + 1] = current.y;
		}
		if (size == POINTS_NUM)
			gc.drawPolygon(drawn);
		else if (size > 0) {
			gc.drawPolyline(drawn);
			gc.drawLine(drawn[drawn.length - 2], drawn[drawn.length - 1],
					currentPt.x, currentPt.y);
		}
	}

	@Override
	public void draw(GC gc) {
		super.draw(gc);
		if (! filled) {
			connectPoints(gc);
			connectFirstLast(gc);
		}
		else
			fillPolygon(gc);
	}

	public ShapeIF copy() {
		return clone();
	}

	public boolean addable() {
		return points.size() < POINTS_NUM;
	}

	@Override
	public Point calculateCenter() {
		Point first = points.get(0);
		Point second = points.get(1);
		return new Point(Math.min(first.x, second.x) + Math.abs(first.x - second.x)/2,
				Math.min(first.y, second.y) + Math.abs(first.y - second.y)/2);
	}

	public static String getShapeName() {
		return "line";
	}

	public static String getIconPath() {
		return "line.png";
	}

	public static String getToolTip() {
		return "Draw line : 2 vertices";
	}

	protected ShapeIF clone() {
		Color newColor = new Color(color.getDevice(), color.getRed(), color
				.getGreen(), color.getBlue());
		LineShape shape = new LineShape();
		shape.setColor(newColor);
		shape.setWidth(width);
		ArrayList<Point> p = new ArrayList<Point>();
		Iterator<Point> iter = points.iterator();
		Point current;
		while (iter.hasNext()) {
			current = iter.next();
			p.add(new Point(current.x, current.y));
		}
		shape.points = p;
		return shape;
	}
}
