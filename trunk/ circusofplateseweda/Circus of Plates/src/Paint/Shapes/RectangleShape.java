package Paint.Shapes;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;


/**
 * Rectangle shape
 * @see AbstractShape extending from
 * @author  Mostafa Mahmoud Mahmoud Eweda
 * @version 1.0
 * @since JDK 1.6
 */
public class RectangleShape extends AbstractShape {

	private static final int POINTS_NUM = 4;
	private Point fixedPoint;

	public RectangleShape() {
		super();
		points = new ArrayList<Point>(POINTS_NUM);
	}

	public void setLocation(Point pt) {
		
	}

	@Override
	public ShapeIF copy() {
		return clone();
	}

	@Override
	public void draw(GC gc, Point pt) {
		super.draw(gc);
		int fixedX = fixedPoint.x;
		int fixedY = fixedPoint.y;
		int currentX = pt.x;
		int currentY = pt.y;
		int minX = 0;
		int minY = 0;
		if (currentX >= fixedX && currentY >= fixedY) {
			minX = fixedX;
			minY = fixedY;
		}

		else if (currentX >= fixedX && currentY <= fixedY) {
			minX = fixedX;
			minY = currentY;
		}

		else if (currentX <= fixedX && currentY >= fixedY) {
			minX = currentX;
			minY = fixedY;
		}

		else if (currentX <= fixedX && currentY <= fixedY) {
			minX = currentX;
			minY = currentY;
		}
		int width = Math.abs(fixedX - currentX);
		int hight = Math.abs(fixedY - currentY);
		points.get(0).x = minX;
		points.get(0).y = minY;
		points.get(1).x = minX + width;
		points.get(1).y = minY;
		points.get(2).x = minX + width;
		points.get(2).y = minY + hight;
		points.get(3).x = minX;
		points.get(3).y = minY + hight;
		gc.drawRectangle(minX, minY, width, hight);
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

	@Override
	public Point calculateCenter() {
		return GraphicsUtils.intersection(points.get(0), points.get(2), points.get(1), points.get(3));
	}

	@Override
	public String getToolType() {
		return "drag";
	}

	@Override
	public boolean addable() {
		return points.size() == 0;
	}

	public static String getShapeName() {
		return "rectangle";
	}

	public static String getIconPath() { // kan asmaha pen
		return "rectangle.png";
	}

	public static String getToolTip() {
		return "Rectangle : 4 vertices";
	}

	protected ShapeIF clone() {
		 Color newColor = new Color(color.getDevice(), color.getRed(),
		 color.getGreen(), color.getBlue());
		RectangleShape shape = new RectangleShape();
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

	public void addPoint(int ptX, int ptY) {
		if (addable()) {
			points.add(new Point(ptX, ptY));
			points.add(new Point(ptX, ptY));
			points.add(new Point(ptX, ptY));
			points.add(new Point(ptX, ptY));
			fixedPoint = new Point(points.get(0).x, points.get(0).y);
		}
	}
}
