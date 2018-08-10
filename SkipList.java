/*
 * Author: Andres Santamaria
 */

import java.util.*;
import java.io.*;


class Node<Type extends Comparable<Type>>
{
	Type data;
	int height;
	ArrayList<Node<Type>> nextNodes = new ArrayList<Node<Type>>();

	Node(Type data, int height)
	{
		this.data = data;
		this.height = height;
		for(int i = 0; i < height; i++)
			nextNodes.add(null);
	}

	Node(int height)
	{
		Type dummyValue = null;
		this.data = dummyValue;
		this.height = height;
		for(int i = 0; i < height; i++)
			nextNodes.add(null);
	}

	public ArrayList<Node<Type>> getNextNodes()
	{
		return this.nextNodes;
	}

	public Type value()
	{
		return this.data;
	}

	public int height()
	{
		return this.height;
	}

	public void grow()
	{
		this.height++;
		this.nextNodes.add(null);
	}

	public void trim()
	{
		this.nextNodes.remove(this.height() - 1);
		this.height--;
	}

	// This returns a boolean to indicate to the program whether or not it needs to reconnect any nodes.
	public boolean maybeGrow()
	{
		Random r = new Random();
		int toss = r.nextInt(2);
		if(toss == 0)
			return false;;

		this.height++;
		this.nextNodes.add(null);
		return true;
	}

	public Node<Type> next(int level)
	{
		if((this.nextNodes != null) && (level < this.height()) && (this.nextNodes.get(level) != null))
		{
			Node<Type> retNode = nextNodes.get(level);
			return retNode;
		}
		return null;
	}

	public void setNext(int level, Node<Type> node)
	{
		this.nextNodes.remove(level);
		this.nextNodes.add(level, node);
	}

}

public class SkipList<Type extends Comparable<Type>>
{
	private int height;
	private Node<Type> head;
	private int size;

	SkipList()
	{
		Type dummyValue = null;
		head = new Node<>(dummyValue, 1);
		this.height = head.height();
		size = 0;
	}

	SkipList(int height)
	{
		Type dummyValue = null;
		if(height < 1)
		{
			head = new Node<>(dummyValue, 1);
			this.height = this.head.height();
			size = 0;
		}

		this.height = height;
		head = new Node<>(dummyValue, height);
		size = 0;
	}

	public Node<Type> head()
	{
		return this.head;
	}

	public int size()
	{
		return this.size;
	}

	public void increaseSize()
	{
		this.size++;
	}

	public void decreaseSize()
	{
		this.size--;
	}

	public int height()
	{
		return this.head.height;
	}

	public void growSkipList()
	{
		Node<Type> current = this.head();
		int listHeight = this.height() - 1;
		current.grow();		
		Node<Type> temp = current.next(listHeight);
		while(temp != null)
		{
			if(temp.maybeGrow())
			{
				current.setNext(listHeight + 1, temp);
			}
			temp = temp.next(listHeight);
		}
	}

	public void trimSkipList()
	{
		Node<Type> current = this.head();
		int listHeight = this.height() - 1;
		Node<Type> temp = current.next(listHeight);
		current.trim();
		while(temp != null)
		{
			current = temp;
			temp = current.next(listHeight);
			current.trim();
		}

	}


	public int generateHeight(int maxHeight)
	{
		int height = 1;
		Random r = new Random();
		int toss = r.nextInt(2);
		while(height < maxHeight)
		{
			if(toss == 0)
				return height;
			else
			{
				toss = r.nextInt(2);
				height++;
			}
		}

		return height;
	}

	// This inseert method works in conjunction with the other, similar to the public / private encapsulation 
	// structure shown to us before.
	// After increasing the size of the list, it checks to see if the height is acceptable given the updated parameters
	// The expected height of the list is calculated using the change of base forumla. 

	public void insert(Type data)
	{
		this.increaseSize();
		Node<Type> current = this.head();
		Node<Type> temp;
		int expectedHeight = (int) Math.ceil( (Math.log(this.size()) / Math.log(2)));
		if(expectedHeight > this.height())
			this.growSkipList();

		int height = generateHeight(this.height());
		insert(data, height);
	}



	public void insert(Type data, int height) 
	{
		if(height > this.height())
		{
			System.out.println("Invalid Height. Returning.");
			return;
		}

		Node<Type> check = this.head();
		int currentSize = 0, expectedSize = this.size();
		while(check.next(0) != null)
		{
			check = check.next(0);
			currentSize++;
		}

		if(currentSize == expectedSize)
			this.increaseSize();

		Node<Type> newNode = new Node<>(data, height);
		Node<Type> current = this.head();
		Node<Type> temp;
		int expectedHeight = (int) Math.ceil( (Math.log(this.size()) / Math.log(2)));
		while(expectedHeight > this.height())
			this.growSkipList();

		LinkedList<Node<Type>> reconnect = new LinkedList<Node<Type>>();

		int listHeight = this.height() - 1;
		for(int i = listHeight; i >= 0; i--)	
		{
			temp = current.next(i);

			while((temp != null) && (data.compareTo(temp.value()) > 0))
			{
				current = temp;
				temp = current.next(i);
			}
			reconnect.add(current);
		}

		for(int i = 0; i < height; i++)
		{
			Node<Type> n = reconnect.removeLast();
			newNode.setNext(i, n.next(i));
			n.setNext(i, newNode);
		}

	}

	public boolean contains(Type data)
	{
		Node<Type> current = this.head();
		Node<Type> temp;
		int listHeight = this.height() - 1;

		for(int i = listHeight; i >= 0; i--)
		{
			temp = current.next(i);
			while((temp != null))
			{
				if(data.compareTo(temp.value()) == 0)
				{
					return true;
				}

				else if(data.compareTo(temp.value()) > 0)
				{
					current = temp;
					temp = current.next(i);
				}
				else{
					break;
				}
			}
		}
		return false;
	}

	public Node<Type> get(Type data)
	{
		if(!contains(data))
			return null;
		Node<Type> current = this.head();
		Node<Type> temp;
		int listHeight = this.height() - 1;
		for(int i = listHeight; i >= 0; i--)
		{
			temp = current.next(i);
			while((temp != null))
			{
				if(data.compareTo(temp.value()) == 0)
					return temp;

				else if(data.compareTo(temp.value()) > 0)
				{
					current = temp;
					temp = current.next(i);
				}
				else{
					break;
				}
			}
		}
		return null;

	}

	public void delete(Type data)
	{
		if(!contains(data))
			return;

		this.decreaseSize();
		int expectedHeight = (int) Math.ceil( (Math.log(this.size()) / Math.log(2)));
		if(expectedHeight > 0)
			while(expectedHeight < this.height())
				this.trimSkipList();

		Node<Type> current = this.head();
		Node<Type> temp;
		Node<Type> deleted = this.get(data);
		int listHeight = this.height() - 1;
		LinkedList<Node<Type>> reconnect = new LinkedList<Node<Type>>();
		for(int i = listHeight; i >= 0; i--)
		{
			temp = current.next(i);
			while(temp != null)
			{
				if(data.compareTo(temp.value()) == 0)
				{	
					reconnect.add(current);
					break;
				}
					
				else if(data.compareTo(temp.value()) > 0)
				{
					current = temp;
					temp = current.next(i);
				}
				else{
					break;
				}
			}
		}
			
		for(int i = 0; i < deleted.height(); i++)
		{
			Node<Type> n = reconnect.removeLast();
			n.setNext(i, deleted.next(i));
		}
	
	}


}
