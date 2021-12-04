package SortedList;

//import Tree.BTNode;

/**
 * Implementation of a SortedList using a SinglyLinkedList
 * @author Fernando J. Bermudez & Juan O. Lopez
 * @author Carlos A. Leyva Capote
 * @version 2.0
 * @since 10/16/2021
 */
public class SortedLinkedList<E extends Comparable<? super E>> extends AbstractSortedList<E> {

	@SuppressWarnings("unused")
	private static class Node<E> {

		private E value;
		private Node<E> next;

		public Node(E value, Node<E> next) {
			this.value = value;
			this.next = next;
		}

		public Node(E value) {
			this(value, null); // Delegate to other constructor
		}

		public Node() {
			this(null, null); // Delegate to other constructor
		}

		public E getValue() {
			return value;
		}

		public void setValue(E value) {
			this.value = value;
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		public void clear() {
			value = null;
			next = null;
		}				
	} // End of Node class

	
	private Node<E> head; // First DATA node (This is NOT a dummy header node)
	
	public SortedLinkedList() {
		head = null;
		currentSize = 0;
	}

	@Override
	public void add(E e) {
		/* Declaring and initializing node variables */
		Node<E> newNode = new Node<>(e);
		Node<E> curNode = head;
		Node<E> prevNode = null;
		/* If list is empty, add to header */
		if (head == null) {
			head = newNode;
			currentSize++;
			return;
		}
		/* Traverse through linked list */
		while (curNode != null) {
			/* If value to add is smaller than the current node... */
			if (e.compareTo(curNode.getValue()) < 0) {
				/* Add new node before current node */
				newNode.setNext(curNode);
				/* If current node is the header, change new node to head */
				if (curNode == head) head = newNode;
				/* If not, point the previous node to the new node */
				else prevNode.setNext(newNode);
				currentSize++;
				return;
			}
			/* Value has not been added, move to the next node */
			prevNode = curNode;
			curNode = curNode.getNext();
		}
		/* If value has not been added after traversal, append */
		newNode.setNext(curNode);
		prevNode.setNext(newNode);
		currentSize++;
	}

	@Override
	public boolean remove(E e) {
		/* Declaring node variables */
		Node<E> prevNode = null, curNode = head;
		/* Traverse through linked list */
		while (curNode != null) {
			/* If node to remove is found... */
			if (curNode.getValue() == e) {
				/* If node to remove is the first, change header */
				if (curNode == head) {
					head = curNode.getNext();
					return true;
				}
				/* If node to remove is not first, set previous's next to current's next */
				prevNode.setNext(curNode.getNext());
				return true;
			}
			/* Value has not been removed, move to the next node */
			prevNode = curNode;
			curNode = curNode.getNext();
		}
		/* Linked list was traversed, no element was removed */
		return false;
	}

	@Override
	public E removeIndex(int index) {
		/* Declaring node variables */
		Node<E> prevNode = null, curNode = head.getNext();
		/* If index is out of bounds return null */
		if (index >= currentSize) return null;
		/* If header is to be removed, change header to next */
		if (index == 0) {
			E rmNode = head.getValue();
			head = head.getNext();
			currentSize--;
			return rmNode;
		}
		/* Traverse linked list from second node to index */
		for (int i = 1; i < index; i++) {
			prevNode = curNode;
			curNode = curNode.getNext();
		}
		/* Remove node by setting previous's next to current's next */
		prevNode.setNext(curNode.getNext());
		currentSize--;
		return curNode.getValue();
	}

	@Override
	public int firstIndex(E e) {
		/* Declaring node variables */
		Node<E> prevNode = null, curNode = head;
		/* Traverse over linked list */
		for (int i = 0; i < currentSize; i++) {
			/* If found, return index */
			if (curNode.getValue() == e) return i;
			/* Value has not been found yet, move to the next node */
			prevNode = curNode;
			curNode = curNode.getNext();
		}
		/* If not found, return -1 */
		return -1;
	}

	@Override
	public E get(int index) {
		/* If index is out of bounds return null */
		if (index >= currentSize) return null;
		/* If index = 0, return head value */
		if (index == 0) return head.getValue();
		/* Declaring node variables */
		Node<E> prevNode = null, curNode = head;
		/* Traversing linked list up to index */
		for (int i = 0; i < index; i++) {
			/* Move to next node */
			prevNode = curNode;
			curNode = curNode.getNext();
		}
		return curNode.getValue();
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		int index = 0;
		E[] theArray = (E[]) new Comparable[size()]; // Cannot use Object here
		for(Node<E> curNode = this.head; index < size() && curNode != null; curNode = curNode.getNext(), index++) {
			theArray[index] = curNode.getValue();
		}
		return theArray;
	}

}
