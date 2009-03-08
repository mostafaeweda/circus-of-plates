package CircusOfPlates;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import Paint.Shapes.LineShape;

/**
 * The Player class handles all the operations supported by a player
 * it also contains the player internal data managed by creators of the player
 * 
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Stack
 * @since JDK 1.6
 */
public class Player extends Observable {

	/**
	 * The default width of the line of the stick the player holds the plates on
	 */
	private static final int STICK_WIDTH = 4;

	/**
	 * The default stick height --> should be managed with the image of the player
	 */
	private static final int STICK_HEIGHT = 150;

	/**
	 * the receiver board on which the player collects the plates
	 */
	private static final int RECIEVER_WIDTH = (int) (Plate.PLATE_WIDTH * 1.5);

	/**
	 * The players image
	 */
	private Image image;

	/**
	 * The name of the Player
	 */
	private String name;

	/**
	 * The right hand stack of the player
	 */
	private Stack<Plate> rightStack;

	/**
	 * The left hand stack of the player
	 */
	private Stack<Plate> leftStack;

	/**
	 * The top point of the left stack
	 */
	private Point topLeft;

	/**
	 * the top point of the right stack
	 */
	private Point topRight;

	/**
	 * the location of the player on the board
	 */
	private Point location;

	/**
	 * The array of lines that represent the sticks the player is holding
	 */
	private LineShape[] lines;

	/**
	 * the score of the player
	 */
	private int score;

	/**
	 * the number of plates the player should collect to gather a point score
	 */
	private static int levelPlateNum = 3;

	/**
	 * the observer the player should notify when its score is updated
	 */
	private Observer observ;

	/**
	 * creates a player with the given attributes
	 * @param observ
	 * @param name
	 * @param image
	 * @param lineColor
	 * @param location
	 */
	public Player(Observer observ, String name, Image image, Color lineColor, Point location) {
		this.observ = observ;
		this.name = name;
		score = 0;
		lines = new LineShape[4];
		Rectangle rect = image.getBounds();
		this.image = image;
		location.y -= rect.height;
		this.location = location;
		rightStack = new Stack<Plate>();
		leftStack = new Stack<Plate>();
		for (int i = 0; i < lines.length; i++) {
			lines[i] = new LineShape();
			lines[i].setColor(lineColor);
			lines[i].setWidth(STICK_WIDTH);
		}
		lines[0].addPoint(location.x+35, location.y+130);
		lines[0].addPoint(location.x+35, location.y - STICK_HEIGHT+130);
		lines[1].addPoint(location.x + rect.width-40, location.y + 130);
		lines[1].addPoint(location.x + rect.width-40, location.y - STICK_HEIGHT+130);
		lines[2].addPoint(location.x - RECIEVER_WIDTH / 2+35, location.y
				- STICK_HEIGHT+130);
		lines[2].addPoint(location.x + RECIEVER_WIDTH / 2+35, location.y
				- STICK_HEIGHT+130);
		lines[3].addPoint(location.x + rect.width - RECIEVER_WIDTH / 2-40,
				location.y - STICK_HEIGHT+130);
		lines[3].addPoint(location.x + rect.width + RECIEVER_WIDTH / 2-40,
				location.y - STICK_HEIGHT+130);
		topLeft = lines[2].calculateCenter();
		topLeft.y -= STICK_WIDTH / 2;
		topRight = lines[3].calculateCenter();
		topRight.y -= STICK_WIDTH / 2;
	}

	/**
	 * drawn the player with the current data including the left and right stack of plates ha has collected
	 * @param gc the graphical context the plates and the player are drawn on.
	 * @see Plate#draw(GC)
	 * @see LineShape#draw(GC)
	 */
	public void draw(GC gc) {
		for (int i = 0; i < lines.length; i++) {
			lines[i].draw(gc);
		}
		Iterator<Plate> iter = leftStack.iterator();
		while (iter.hasNext())
			iter.next().draw(gc);
		iter = rightStack.iterator();
		while (iter.hasNext())
			iter.next().draw(gc);
		gc.drawImage(image, location.x, location.y);
	}

	/**
	 * moves the player with the given shiftX attribute horizontally
	 * @param xShift the shift the user wants to move
	 */
	public void move(int xShift) {
		location.x += xShift;
		for (int i = 0; i < lines.length; i++)
			lines[i].relocate(xShift, 0);
		topLeft.x += xShift;
		topRight.x += xShift;
		Iterator<Plate> iter = leftStack.iterator();
		while (iter.hasNext())
			iter.next().shiftX(xShift);
		iter = rightStack.iterator();
		while (iter.hasNext())
			iter.next().shiftX(xShift);
	}

	/**
	 * sets the horizontal location of the player
	 * @param x the new x location
	 */
	public void setXLocation(int x) {
		int xShift = x - location.x;
		location.x = x;
		for (int i = 0; i < lines.length; i++)
			lines[i].relocate(xShift, 0);
		topLeft.x += xShift;
		topRight.x += xShift;
		Iterator<Plate> iter = leftStack.iterator();
		while (iter.hasNext())
			iter.next().shiftX(xShift);
		iter = rightStack.iterator();
		while (iter.hasNext())
			iter.next().shiftX(xShift);
	}

	/**
	 * @param plate
	 * @see Plate#contains(Point)
	 * @return true if the given plate intersects with the player
	 */
	public boolean intersects(Plate plate) {
		return plate.contains(topLeft) || plate.contains(topRight);
	}

	/**
	 * Adds the given plate to the right or the left stack depending on its location
	 * @param plate the plate to be added
	 * @see #checkColor(Stack)
	 */
	public void addPlate(Plate plate) {
		if (plate.contains(topLeft)) {
			if (leftStack.size() > 1) {
				leftStack.push(plate);
				Stack<Plate> temp = checkColor(leftStack);
				if (temp.isEmpty()) {
					score++;
					observ.update(this, "score"); // update the score at the GUI view
					topLeft.y += (levelPlateNum - 1) * Plate.PLATE_HEIGHT;
				} else {
					while (! temp.isEmpty())
						leftStack.push(temp.pop());
					leftStack.pop();
					add(leftStack, plate, topLeft);
					if (topLeft.y < PlateSky.getInstance().getLeastHeight() + Plate.PLATE_HEIGHT)
						observ.update(this, "lost Score:" + score);
				}
			} else // the stack is empty or size = 1
				add(leftStack, plate, topLeft);
		} else { // plate.contains(topRight)
			if (rightStack.size() > 1) {
				rightStack.push(plate);
				Stack<Plate> temp = checkColor(rightStack);
				if (temp.isEmpty()) {
					score++;
					observ.update(this, "score");
					topRight.y += (levelPlateNum - 1) * Plate.PLATE_HEIGHT;
				} else {
					while (!temp.isEmpty())
						rightStack.push(temp.pop());
					rightStack.pop();
					add(rightStack, plate, topRight);
					if (topRight.y < PlateSky.getInstance().getLeastHeight() + Plate.PLATE_HEIGHT)
						observ.update(this, "lost Score:" + score);
				}
			} else // the stack is empty or size = 1
				add(rightStack, plate, topRight);
		}
	}

	/**
	 * checks the color condition for the stack on which the add operation is intended to be done
	 * @param stack the stack on which the add operation is done
	 * @return the stack containing the popped elements or empty stack if none
	 */
	private Stack<Plate> checkColor(Stack<Plate> stack) {
		Stack<Plate> temp = new Stack<Plate>(levelPlateNum);
		Color common = stack.peek().getColor();
		temp.push(stack.pop());
		for (int i = 1; i < levelPlateNum; i++) {
			temp.push(stack.pop());
			if (! temp.peek().getColor().equals(common)) {
				return temp;
			}
		}
		temp.clear();
		return temp;
	}

	/**
	 * adds the plate to the given stack and modifies its top point consequently
	 * @param stack the stack on which the add is done
	 * @param plate the plate that will be added
	 * @param top the top point of the stack to be modified
	 */
	private void add(Stack<Plate> stack, Plate plate, Point top) {
		plate.setBelow(top);
		stack.push(plate);
	}


	/**
	 * sets the location of the player to the given point
	 * @param location the new location of the player
	 */
	public void setLocation(Point location) {
		this.location = location;
	}

	/** 
	 * @return the current location of the plyer
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * sets the name of the player with the given name --> happens mainly in network gaming
	 * @param name the name pf the player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return a string representation of the state of the player
	 */
	public String toString() {
		return "Player: "+name+"\nScore: " + score;
	}

	/**
	 * sets the image of the player to the given image
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @see Player#levelPlateNum
	 * @param plates the new number of plates for winning score
	 */
	public static void setLevelPlateNumber(int plates) {
		levelPlateNum = plates;
	}
}
