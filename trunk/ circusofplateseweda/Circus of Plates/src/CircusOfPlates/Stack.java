package CircusOfPlates;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;

/**
 * The class is designed to be generically used for multiple stack usages
 * @author Mostafa Mahmoud Mahmoud Eweda
 * @since JDK 1.6
 * @see ArrayList
 * @param <E> the type of the data to be represented using the class
 */
public class Stack<E> {

	/**
	 * The place holder of the data
	 * All the stack operations are delegated to it
	 */
	private ArrayList<E> data;

	/**
	 * creates a stack with 10 elements initially
	 */
	public Stack() {
		data = new ArrayList<E>();
	}

	/**
	 * create a stack with the given size
	 * @param size the initial size of the stack
	 */
	public Stack(int size) {
		data = new ArrayList<E>(size);
	}

	/**
     * Pushes an item onto the top of this stack.
     * @param e the item to be pushed onto this stack.
     * @see ArrayList#add(Object)t
     */
	public void push(E e) {
		data.add(e);
	}

	/**
	 * @return an iterator to iterate the stack sequentially for unrelated operations
	 */
	public Iterator<E> iterator() {
		return data.iterator();
	}

	/**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     * @return     The object at the top of this stack
     * @exception  EmptyStackException  if this stack is empty.
     */
	public E pop() {
		if (data.isEmpty())
			throw new EmptyStackException();
		return data.remove(data.size() - 1);
	}

	/**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     * @return     the object at the top of this stack
     * @exception  EmptyStackException  if this stack is empty.
     */
	public E peek() {
		if (data.isEmpty())
			throw new EmptyStackException();
		return data.get(data.size() - 1);
	}

	/**
	 * @return true if the stack is empty
	 * @see ArrayList#isEmpty()
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * clears the data stored at the stack
	 * @see ArrayList#clear()
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * @return the size of the stack
	 * @see ArrayList#size()
	 */
	public int size() {
		return data.size();
	}
}
