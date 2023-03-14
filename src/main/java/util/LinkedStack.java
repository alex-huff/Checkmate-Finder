package util;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public
class LinkedStack<E>
{
    private Node front;   // null when list empty
    private int  size;

    // Constructs a new empty stack.
    public
    LinkedStack()
    {
        front = null;
        size  = 0;
    }

    // Returns true if the stack does not contain any elements.
    public
    boolean isEmpty()
    {
        return size == 0;
    }

    // Returns an Iterator to traverse the elements of this stack.
    public
    Iterator<E> iterator()
    {
        return new LinkedStackIterator();
    }

    // Returns the top element of this stack without removing it.
    // Throws an EmptyStackException if the stack is empty.
    public
    E peek()
    {
        if (size == 0)
        {
            throw new EmptyStackException();
        }
        return front.data;
    }

    // Removes and returns the top element of this stack.
    // Throws an EmptyStackException if the stack is empty.
    public
    E pop()
    {
        if (size == 0)
        {
            throw new EmptyStackException();
        }
        E top = front.data;
        front = front.next;
        size--;
        return top;
    }

    // Adds the given value to the top of this stack.
    public
    void push(E value)
    {
        Node newNode = new Node(value);
        newNode.next = front;
        front        = newNode;
        size++;
    }

    // Returns the number of elements contained in this stack.
    public
    int size()
    {
        return size;
    }

    // Returns a string representation of the stack, such as "bottom a b c top".
    public
    String toString()
    {
        StringBuilder sb      = new StringBuilder();
        Node          current = front;
        while (current != null)
        {
            sb.insert(0, current.data);
            sb.insert(0, ' ');
            current = current.next;
        }
        sb.insert(0, "bottom");
        sb.append(" top");
        return sb.toString();
    }

    // Each Node object stores a single element of data in the linked list and
    // a link to another (possibly null) Node for the next piece of data.
    private
    class Node
    {
        private E    data;
        private Node next;

        // Constructs a new node to store the given data value.
        public
        Node(E data)
        {
            this.data = data;
        }
    }

    // An iterator class to traverse the elements of this stack
    // from top to bottom.
    private
    class LinkedStackIterator implements Iterator<E>
    {
        private Node position;   // current position in list

        // Constructs an iterator at the beginning (top) of this stack.
        public
        LinkedStackIterator()
        {
            position = front;
        }

        // Returns true if there are any more elements for this iterator to return.
        public
        boolean hasNext()
        {
            return position != null;
        }

        // Returns the next element from the stack and advances iterator by one slot.
        public
        E next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            E result = position.data;
            position = position.next;
            return result;
        }

        // Not implemented.
        public
        void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}