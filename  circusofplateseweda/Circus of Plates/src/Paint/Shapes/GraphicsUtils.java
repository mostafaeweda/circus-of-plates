package Paint.Shapes;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

/**
 * Contains the full math behavior of the drawing application
 * @author  Mostafa Mahmoud Mahmoud Eweda
 * @version 1.0
 * @since JDK 1.6
 */
public final class GraphicsUtils {

	/**
	 * @param points
	 * @param pt
	 * @return true if the point lies between the two given lines
	 */
	public static boolean liesBetween(int[] points, Point pt) {
		if (points.length != 6)
			throw new IllegalArgumentException();
		int t1 = (pt.x - points[0]) * (points[3] - points[1])
				- (pt.y - points[1]) * (points[2] - points[0]);
		if (t1 > 0)
			return false;
		int t2 = (points[4] - points[0]) * (pt.y - points[1])
				- (points[5] - points[1]) * (pt.x - points[0]);
		if (t2 > 0)
			return false;
		return true;
	}

	private static int helper(Point a, Point b, Point c) {
		return (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);
	}

	public static boolean rounded(Point a, Point b, Point c) {
		return helper(a, b, c) >= 0;
	}

	public static int fisaghors(int x, int y, boolean type) {
		int z;
		if (type)
			z = (int) Math.sqrt(x * x + y * y);
		else
			z = (int) Math.sqrt(x * x - y * y);
		return z;
	}

	/**
	 * @param p
	 * @param pol the polygon to check
	 * @return true if the given point is inside the polygon
	 */
	public static boolean insidePolygon(Point p, ArrayList<Point> pol) {
		int n = pol.size(), j = n - 1;
		boolean b = false;
		for (int i = 0; i < n; i++) {
			if (pol.get(j).y <= p.y && p.y < pol.get(i).y
					&& rounded(pol.get(j), pol.get(i), p)
					|| pol.get(i).y <= p.y && p.y < pol.get(j).y
					&& rounded(pol.get(i), pol.get(j), p))
				b = !b;
			j = i;
		}
		return b;
	}

	/**
	 * 
	 * @param pt
	 * @param center
	 * @param theta
	 */
	public static void rotatePoint(Point pt, Point center, double theta) {
		int xC = center.x, yC = center.y;
		int x = pt.x, y = pt.y;
		double radians = Math.toRadians(theta);
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		pt.x = (int) Math.round(((x * cos) + xC + (yC * sin))
				- ((y * sin) + (xC * cos)));
		pt.y = (int) Math.round(((x * sin) + yC + (y * cos))
				- ((xC * sin) + (yC * cos)));
	}

	/**
	 * morsi we yasser's method
	 * @param pt
	 * @param center
	 * @param rotationAngle
	 */
	public static void rotate(Point pt, Point center, double rotationAngle)
	{
		if (rotationAngle < 0)
			rotationAngle = 360 - rotationAngle;
		double radians = Math.toRadians(rotationAngle);
		Point firToPt = new Point(pt.x - center.x , pt.y - center.y);
		Point newFirToPt = new Point((int)(Math.round((firToPt.x * Math.cos(radians) 
				- firToPt.y * Math.sin(radians)))),
				(int)(Math.round((firToPt.x * Math.sin(radians) 
				+ firToPt.y * Math.cos(radians)))));
		pt.x = newFirToPt.x + center.x;
		pt.y = newFirToPt.y + center.y;
	}

	/**
s	 * @param pt1
	 * @param pt2
	 * @return the slope of the given line
	 */
	public static double getSlope(Point pt1, Point pt2) {
		return ((double) (-1 * pt1.y + pt2.y)) / (-1 * pt1.x + pt2.x);
	}

	/**
	 * @param pol an array of points to get the shape from
	 * @return the area of the shape
	 */
	public static float area(Point[] pol) {
		int n = pol.length, j = n - 1;
		float a = 0;
		for (int i = 0; i < n; i++) { // i and j denote neighbor vertices
			// with i one step ahead of j
			a += pol[j].x * pol[i].y - pol[j].y * pol[i].x;
			j = i;
		}
		return a;
	}

	/**
	 * @param a first point of the line
	 * @param b second point of the line
	 * @param p the point to check
	 * @return true if the point is on the line, otherwise returns false
	 */
	public static boolean onLine(Point a, Point b, Point p) {
		double dx = b.x - a.x, dy = b.y - a.y, error = 0.001 * (dx * dx + dy
				* dy);
		return (a.x != b.x
				&& (a.x <= p.x && p.x <= b.x || b.x <= p.x && p.x <= a.x)
				|| a.x == b.x
				&& (a.y <= p.y && p.y <= b.y || b.y <= p.y && p.y <= a.y))
				&& Math.abs(helper(a, b, p)) < error;
	}

	/**
	 * @param p1 first point of the first line
	 * @param p2 second point of the first line
	 * @param p3 first point of the second line
	 * @param p4 second point of the second line
	 * @return the intersection between the two lines given its points
	 */
	public static Point intersection(Point p1, Point p2, Point p3, Point p4) {
		int d = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x);
		if (d == 0)
			return null;
		int xi = ((p3.x - p4.x) * (p1.x * p2.y - p1.y * p2.x) - (p1.x - p2.x)
				* (p3.x * p4.y - p3.y * p4.x)) / d;
		int yi = ((p3.y - p4.y) * (p1.x * p2.y - p1.y * p2.x) - (p1.y - p2.y)
				* (p3.x * p4.y - p3.y * p4.x)) / d;
		return new Point(xi, yi);
	}
}
