/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a node of a tree. The node also contains data of type T.
 */
public class Node<T>
{

    public T data;
    public List<Node<T>> children;

    /**
     * Default ctor.
     */
    public Node()
    {

    }

    /**
     * Convenience ctor to create a Node<T> with an instance of T.
     * @param data an instance of T.
     */
    public Node(T data)
    {
        setData(data);
    }

    /**
     * Looks for a node with the given data.
     * @param data
     * @return
     */
    public Node<T> find(Filter<T> filter, T data)
    {
        // Otherwise, look at all the children.
        if (this.children == null) return null;

        for ( Node<T> node : this.children )
        {
            if (filter.apply(node.data, data)) return node;
        }

        for ( Node<T> node : this.children )
        {
            Node<T> targetNode = node.find(filter, data);
            if (targetNode != null) return targetNode;
        }

        return null;
    }

    public Node<T> find(Filter<T> filter)
    {
        return find(filter, null);
    }

    public Node<T> find(T data)
    {
        return find(this.equalityFilter, data);
    }

    /**
     * Searches for a piece of data in a tree and returns
     * all node with occurances of it.
     * @param data
     * @return
     */
    public List<Node<T>> findAll(Filter<T> filter, T data)
    {
        // Create a list to return.
        List<Node<T>> foundList = new ArrayList<Node<T>>();

        for ( Node<T> node : getChildren() )
        {
            if (filter.apply(node.data, data)) foundList.add(node);
            foundList.addAll(node.findAll(filter, data));
        }

        return foundList;
    }

    public List<Node<T>> findAll(Filter<T> filter)
    {
        return findAll(filter, null);
    }

    /**
     * Return the children of Node<T>. The Tree<T> is represented by a single
     * root Node<T> whose children are represented by a List<Node<T>>. Each of
     * these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     * @return the children of Node<T>
     */
    public List<Node<T>> getChildren()
    {
        if (this.children == null)
        {
            return new ArrayList<Node<T>>();
        }

        return this.children;
    }

    /**
     * Sets the children of a Node<T> object. See docs for getChildren() for
     * more information.
     * @param children the List<Node<T>> to set.
     */
    public void setChildren(List<Node<T>> children)
    {
        this.children = children;
    }

    /**
     * Returns the number of immediate children of this Node<T>.
     * @return the number of immediate children.
     */
    public int getNumberOfChildren()
    {
        if (children == null)
        {
            return 0;
        }
        return children.size();
    }

    /**
     * Adds a child to the list of children for this Node<T>. The addition of
     * the first child will create a new List<Node<T>>.
     * @param child a Node<T> object to set.
     */
    public void addChild(Node<T> child)
    {
        if (children == null)
        {
            children = new ArrayList<Node<T>>();
        }
        children.add(child);
    }

    /**
     * Adds a child to the list of children.
     * @param data
     */
    public Node<T> addChild(T data)
    {
        Node<T> node = new Node<T>(data);
        this.addChild(node);
        return node;
    }

    /**
     * Add a collection to the list of children.  Will add each
     * element in the collection as a child.
     * @param collection
     */
    public Collection<Node<T>> addChildren(Collection<T> collection)
    {
        Collection<Node<T>> list = new ArrayList<Node<T>>();
        for ( T item : collection )
        {
            list.add(this.addChild(item));
        }
        return list;
    }

    /**
     * Inserts a Node<T> at the specified position in the child list. Will
     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
     * @param index the position to insert at.
     * @param child the Node<T> object to insert.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void insertChildAt(int index, Node<T> child) throws IndexOutOfBoundsException
    {
        if (index == getNumberOfChildren())
        {
            // this is really an append
            addChild(child);
            return;
        }
        else
        {
            children.get(index); //just to throw the exception, and stop here
            children.add(index, child);
        }
    }

    /**
     * Remove the Node<T> element at index index of the List<Node<T>>.
     * @param index the index of the element to delete.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void removeChildAt(int index) throws IndexOutOfBoundsException
    {
        children.remove(index);
    }

    public T getData()
    {
        return this.data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("{");
        buffer.append(this.data == null ? "*empty*" : this.data.toString());
        buffer.append(",[");

        int i = 0;
        for (Node<T> e : getChildren())
        {
            if (i > 0)
            {
                buffer.append(",");
            }
            buffer.append(e.getData() == null ? "*empty*" : e.toString());
            i++;
        }
        
        buffer.append("]").append("}");
        return buffer.toString();
    }

    /**
     * A class for creating find filters.
     * @param <T> The data type that the filter is applied to.
     */
    public static class Filter<T>
    {
        /**
         * Apply the filter to the given data.
         * @param nodeData
         * @param otherData
         * @return
         */
        public boolean apply(T nodeData, T otherData)
        {
            return nodeData == otherData;
        }       
    }

    private Filter<T> equalityFilter = new Filter<T>();

    public Filter<T> getEqualityFilter()
    {
        return equalityFilter;
    }

}