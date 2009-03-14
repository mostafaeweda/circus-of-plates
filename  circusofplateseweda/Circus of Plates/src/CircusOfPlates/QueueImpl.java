package CircusOfPlates;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Mostafa Mahmod Mahmod Eweda
 * Implementation of the queue is done by delegating the method calls to {@link LinkedList}
 * so that the queue can be dynamically expanded
 * @see LinkedList
 * @since JDK 1.6
 */
public class QueueImpl implements Externalizable {

	/**
	 * doubly linked list to be used as a queue
	 */
	private LinkedList<Plate> list;

	/**
	 * initializes the linked list creation
	 */
	public QueueImpl() {
		list = new LinkedList<Plate>();
	}

	/**
	 * enqueue an element e in the list
	 * @param e the element to be enqueued
	 * @see LinkedList#addFirst(Object)
	 */
	public void enqueue(Plate e) {
		list.addFirst(e);
	}

	/**
	 * watches the element at the end of the queue --> the next element to be served
	 * @return the first come
	 * @see LinkedList#getLast()
	 */
	public Plate peek() {
		return list.getLast();
	}

	/**
	 * @return the next serve element from the queue and removed it from the list
	 * @see LinkedList#removeLast()
	 */
	public Plate deque() {
		return list.removeLast();
	}

	/**
	 * clears the list
	 * @see LinkedList#clear()
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * @return true if the list is empty
	 * @see LinkedList#isEmpty()
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * @return an iterator over the list to iterate the queue sequentially for linear non related operations
	 * @see Iterator
	 * @see LinkedList#iterator()
	 */
	public Iterator<Plate> iterator() {
		return list.iterator();
	}

	/**
	 * @return the size of the queue
	 * @see LinkedList#size()
	 */
	public int size() {
		return list.size();
	}

	/**
	 * reads the queue from a stream
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		list = (LinkedList<Plate>) in.readObject();
	}

	/**
	 * writes the queue to s stream 
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(list);
	}
}
