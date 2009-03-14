package CircusOfPlates;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import Paint.Shapes.LineShape;

/**
 * @author Mostafa Mahmod Mahmod Eweda
 * A plate bar is designed to handle the motion of the plates on the bars before falling area in the sky
 * Should be managed by continuous update function calls from the GUI view
 * 
 * @version 1.0
 * @see QueueImpl
 * @see Plate
 * @since JDK 1.6
 */
public class PlateBar implements Externalizable {

	/**
	 * The updates step with which the plates hould be updated 
	 */
	private static int updateStep = 1;

	/**
	 * The default width of the plate bars
	 */
	private static final int DEFAULT_WIDTH = 5;

	/**
	 * The number of updates the bar should be still before changing its length randomly
	 */
	private static final int BAR_RATE = 20;

	/**
	 * The angle with which the on edge plate should rotate each update
	 */
	static final double ANGLE = 2.0;

	/**
	 * The maximum number of bars supported by the game
	 */
	private static final int MAX_BARS = 5;

	/**
	 * The colors of the plates that are generated randomly
	 */
	private static Color[] plateColors;

	/**
	 * The colors of the bar levels 
	 */
	private static Color[] barColors;

	/**
	 * The random that generates the motion of the bar and the creation of plates
	 */
	private Random rand;

	/**
	 * The shift of the last plate from the end of the display area
	 */
	private int shift;

	/**
	 * The line shape that represents the bar
	 */
	private LineShape line;

	/**
	 *  The start point of the bar
	 */
	private Point start;

	/**
	 * queue of plates to hold the plates currently on the bar
	 */
	private QueueImpl queue;

	/**
	 * true if the bar is left to right bar; false otherwise
	 */
	private boolean leftToRight;

	/**
	 * the angle with which the fallen plate has rotated
	 */
	double angle;

	/**
	 * number of updates done after last change
	 */
	private int updates;

	/**
	 *  The previous number of pixels shifted to the plate
	 */
	private int shifted;

	/**
	 * Empty constructor to be used in the de-serialization operation
	 */
	public PlateBar() {	
	}

	/**
	 * creates a plate bar with the given location and length
	 * @param startX the startX in which the bar should start from
	 * @param endX the end of the bar
	 * @param heightIdent the height indentation from the display bounds
	 * @param level the level of the plate bar
	 * @param leftToRight true of left to right bar motion; false otherwise
	 */
	public PlateBar(int startX, int endX, int heightIdent, int level,
			boolean leftToRight) {
		updates = 0;
		shifted = 0;
		if (barColors == null) {
			createFirst();
		}
		rand = new Random();
		this.leftToRight = leftToRight;
		start = new Point(startX, heightIdent);
		line = new LineShape();
		line.addPoint(startX, heightIdent);
		line.addPoint(endX, heightIdent);
		line.setColor(barColors[level-1]);
		line.setWidth(DEFAULT_WIDTH);
		queue = new QueueImpl();
	}

	/**
	 * Creates the needed colors by the bars and plates for the first time
	 */
	public static void createFirst() {
		Display display = Display.getCurrent();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					CircusUI.class.getResourceAsStream("PlatesProperties.properties")));
			String s;
			while (! (s = in.readLine()).equals("Plates")) {
					// Wait for plates attrubute;
			}
			int plateNum = Integer.parseInt(in.readLine());
			plateColors = new Color[plateNum];
			barColors = new Color[MAX_BARS];
			String[] temp;
			int i = 0;
			while (! (s = in.readLine()).equals("Bars")) {
				temp = s.split(" ");
				plateColors[i++] = new Color(display, Integer.parseInt(temp[0]),
						Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
			}
			i = 0;
			while ((s = in.readLine()) != null) {
				temp = s.split(" ");
				barColors[i++] = new Color(display, Integer.parseInt(temp[0]),
						Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
			}
		}catch (IOException e) {
			barColors = new Color[MAX_BARS];
			barColors[0] = display.getSystemColor(SWT.COLOR_BLACK);
			barColors[1] = display.getSystemColor(SWT.COLOR_DARK_GRAY);
			barColors[2] = display.getSystemColor(SWT.COLOR_GRAY);
			barColors[3] = display.getSystemColor(SWT.COLOR_WHITE);
			barColors[4] = display.getSystemColor(SWT.COLOR_WHITE);

			plateColors = new Color[] { display.getSystemColor(SWT.COLOR_BLUE),
					display.getSystemColor(SWT.COLOR_RED),
					display.getSystemColor(SWT.COLOR_YELLOW),
					display.getSystemColor(SWT.COLOR_GREEN),
					display.getSystemColor(SWT.COLOR_MAGENTA),
					display.getSystemColor(SWT.COLOR_CYAN),
					display.getSystemColor(SWT.COLOR_GRAY) };
		}
	}

	/**
	 *  updated each while using a display timer
	 * 	updates the movement of the bar and plates going over it
	 */
	public void update() {
		if (++updates == BAR_RATE) {
			Point changed = line.getPoints().get(1);
			changed.x -= shifted;
			shifted = rand.nextInt() % 100;
			changed.x += shifted;
			updates = 0;
		}
		Iterator<Plate> iter = queue.iterator();
		int direct = leftToRight ? updateStep : -updateStep;
		while (iter.hasNext())
			iter.next().shiftX(direct);
		shift += direct;
		if (shift >= Plate.plateSpacing || shift <= -Plate.plateSpacing) {
			Color color = plateColors[Math.abs(rand.nextInt()) % plateColors.length];
			Plate plate = new Plate(color, start, leftToRight);
			queue.enqueue(plate);
			shift = 0;
		}
		if (queue.isEmpty())
			return;
		Plate onEdge = queue.peek();
		int x = onEdge.getBounds().x;
		int y = line.getPoints().get(1).x;
		if (leftToRight) {
			if (x > y) {
				// onEdge.rotate(360-angle);
				PlateSky.getInstance().add(queue.deque());
			}
			// else if (x > y - Plate.PLATE_WIDTH) {
			// onEdge.rotate(ANGLE);
			// angle += ANGLE;
			// }
		} else { // rightToLeft
			if (x < y - Plate.PLATE_WIDTH) {
				// onEdge.rotate(360+angle);
				PlateSky.getInstance().add(queue.deque());
			}
			// else if (x < y) {
			// onEdge.rotate(360-ANGLE);
			// angle -= ANGLE;
			// }
		}
	}

	/**
	 * draw the plate bar to the UI
	 * @param gc the graphical context to draw
	 */
	public void draw(GC gc) {
		line.draw(gc);
		Iterator<Plate> iter = queue.iterator();
		while (iter.hasNext())
			iter.next().draw(gc);
	}

	/**
	 * dispose the plate bars resources
	 */
	public static void dispose() {
		if (plateColors == null)
			return;
		for (int i = 0; i < plateColors.length; i++)
			plateColors[i].dispose();
		for (int i = 0; i < barColors.length; i++)
			barColors[i].dispose();
	}

	public static void setSpeed(int step) {
		updateStep = step;
	}

	/**
	 * reads the plate bar from a stream
	 * @param in the input stream to read the object from
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		updates = 0;
		updateStep = in.readInt();
		rand = (Random) in.readObject();
		shift = in.readInt();
		line = (LineShape) in.readObject();
		start = (Point) in.readObject();
		queue = (QueueImpl) in.readObject();
		leftToRight = in.readBoolean();
	}

	/**
	 * writes the plate bar to a stream
	 * @param out the output stream to write the object in
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(updateStep);
		out.writeObject(rand);
		out.writeInt(shift);
		out.writeObject(line);
		out.writeObject(start);
		out.writeObject(queue);
		out.writeBoolean(leftToRight);
	}
}
