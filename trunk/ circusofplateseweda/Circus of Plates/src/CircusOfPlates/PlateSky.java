package CircusOfPlates;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * The PlateSky class is designed for handling the falling state of plates and their motion
 * during falling from bars until caught by the players or vanish
 * The air factor is implemented here using static variables;
 * The game speed can also be modified
 * @author Mostafa Mahmod Mahmod Eweda
 * @version 1.0
 * @see Plate
 * @since JDK 1.6
 */
public class PlateSky {

	/**
	 * instance of the PlateSky class to represent the singleton pattern
	 */
	private static PlateSky instance;

	/**
	 * The update step of the plates falling towards earth
	 */
	private static int updateStep = 1;

	/**
	 * a list of the plates the sky has --> internally updated when updating the sky
	 */
	private ArrayList<Plate> flying;

	/**
	 * The air shift of the sky with which the plates oscillates eight and left
	 */
	private int airShift = 10;

	/**
	 * The repeat times the plates should be shifted right or left before innverting the direction
	 */
	private int repeatTimes = 20;

	/**
	 * counter to hold the number of times the plates shifted right or left till now
	 */
	private int times = 0;

	private Rectangle displayBounds;

	/**
	 * The nearest bar the sky has received plates from
	 */
	private int leastHeight;

	/**
	 * hiding the constructor and providing {@link #getInstance()} method is a way to represent the
	 * singleton of the sky class
	 * @return {@link #instance}
	 */
	public static synchronized PlateSky getInstance() {
		if (instance == null)
			return instance = new PlateSky();
		return instance;
	}

	/**
	 * initializes the starting elements for future management
	 */
	private PlateSky() {
		displayBounds = Display.getCurrent().getBounds();
		leastHeight = 0;
		flying = new ArrayList<Plate>();
	}

	/**
	 * adds a plate that has fallen from a bar to the skys
	 * @param p
	 */
	public void add(Plate p) {
		leastHeight = Math.max(p.getBounds().y, leastHeight);
		flying.add(p);
	}

	/**
	 * draws the list of plates contained in the {@link #flying} list
	 * @see Plate#draw(GC)
	 * @param gc the graphical context on which the plate should be drawn on
	 */
	public void draw(GC gc) {
		Iterator<Plate> iter = flying.iterator();
		while (iter.hasNext())
			iter.next().draw(gc);
	}

	/**
	 * updates the state of the sky and controls the execution of updates
	 * should be called by a display timer each while
	 * @see Plate#shift(int, int)
	 */
	public void update() {
		Iterator<Plate> iter = flying.iterator();
		ArrayList<Player> players = CircusUI.getInstance().getPlayers();
		Iterator<Player> playerIter;
		// iterate on the plates and shift them with the required attributes	
		while (iter.hasNext())
			iter.next().shift(airShift, updateStep);
		if (++times >= repeatTimes) {
			airShift = - airShift;
			times = 0;
		}
		/* iterate on the players and sees their intersection with the plates and updates the players with the 
				intersected plates */
		Plate current;
		for (int i = 0; i < flying.size(); i++) {
			current = flying.get(i);
			Rectangle rect = current.getBounds();
			playerIter = players.iterator();
			while (playerIter.hasNext()) {
				Player pl = playerIter.next();
				if (pl.intersects(current)) {
					flying.remove(current);
					// as an effect of removing the item the shift happened; decrement i to stand on the new element
					i--;
					pl.addPlate(current);
					break;
				}
			}
			if (rect.y >= displayBounds.height - Plate.PLATE_HEIGHT)
				flying.remove(current);
		}
	}

	/**
	 * sets the air shift of the sky to a given attribute factor
	 * @param airShift the value of the shift each time
	 * @see #airShift
	 */
	public void setAirShift(int airShift) {
		this.airShift = airShift;
	}

	/**
	 * Changes the repeat times done and consequently the horizontal motion of the plates in the sky
	 * @param repeatTimes the number of repeats done before inverting the
	 * @see #repeatTimes
	 */
	public void setRepeatTimes(int repeatTimes) {
		this.repeatTimes = repeatTimes;
	}

	/**
	 * @return the least height of the nearest bar to the ground
	 * @see #leastHeight
	 */
	public int getLeastHeight() {
		return leastHeight;
	}

	/**
	 * clears the plates managed by the sky
	 */
	public void clear() {
		flying.clear();
	}

	/**
	 * sets the speed of the sky updating by increasing the update step
	 * @param step the new step to be proessed in next update areas
	 * @see #updateStep 
	 */
	public static void setSpeed(int step) {
		updateStep = step;
	}
}
