package CircusOfPlates;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;


public class Stack<E> {

	private ArrayList<E> data;

	public Stack() {
		data = new ArrayList<E>();
	}

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
