/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package g3deditor.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class FastArrayList<E> implements List<E>
{
	private final class FastIterator implements Iterator<E>
	{
		private int _off;
		
		public FastIterator()
		{
			
		}
		
		@Override
		public final boolean hasNext()
		{
			return _off < _size;
		}
		
		@Override
		public final E next()
		{
			return _values[_off++];
		}
		
		@Override
		public final void remove()
		{
			removeUnsafeVoid(--_off);
		}
	}
	
	E[] _values;
	int _size;
	
	@SuppressWarnings("unchecked")
	public FastArrayList()
	{
		_values = (E[]) new Object[16];
	}
	
	@SuppressWarnings("unchecked")
	public FastArrayList(final E[] values, final boolean copy)
	{
		if (copy)
		{
			_values = (E[]) new Object[values.length];
			System.arraycopy(values, 0, _values, 0, values.length);
		}
		else
			_values = values;
		
		_size = values.length;
	}
	
	@SuppressWarnings("unchecked")
	public FastArrayList(final int initialCapacity)
	{
		_values = (E[]) new Object[initialCapacity];
	}
	
	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public final boolean add(final E value)
	{
		addLast(value);
		return true;
	}
	
	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public final void add(final int index, final E value)
	{
		if (index > _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		addUnsafe(index, value);
	}
	
	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public final boolean addAll(final Collection<? extends E> collection)
	{
		if (collection.isEmpty())
			return false;
		
		ensureCapacity(_size + collection.size());
		
		final Iterator<? extends E> iter = collection.iterator();
		while (iter.hasNext())
		{
			addLastUnsafe(iter.next());
		}
		
		return true;
	}
	
	public final boolean addAll(final E[] array)
	{
		final int lenght = array.length;
		if (lenght == 0)
			return false;
		
		final int newSize = _size + lenght;
		ensureCapacity(newSize);
		System.arraycopy(array, 0, _values, _size, lenght);
		_size = newSize;
		return true;
	}
	
	public final boolean addAll(final FastArrayList<? extends E> list)
	{
		if (list.isEmpty())
			return false;
		
		ensureCapacity(_size + list._size);
		System.arraycopy(list._values, 0, _values, _size, list._size);
		_size += list._size;
		
		return true;
	}
	
	public final void addAllIfAbsent(final FastArrayList<? extends E> list)
	{
		E value;
		for (int i = list.size(); i-- > 0;)
		{
			value = list.getUnsafe(i);
			if (!contains(value))
				addLast(value);
		}
	}
	
	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final boolean addAll(final int index, final Collection<? extends E> collection)
	{
		if (collection.isEmpty())
			return false;
		
		if (index > _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		final int size = collection.size();
		final int spaceNeeded = _size + size;
		final Iterator<? extends E> iter = collection.iterator();
		int i = 0;
		
		if (_values.length < spaceNeeded)
		{
			final E[] values = (E[]) new Object[spaceNeeded];
			System.arraycopy(_values, 0, values, 0, index);
			
			while (iter.hasNext())
			{
				values[i++] = iter.next();
			}
			
			System.arraycopy(_values, index, values, index + size, _size - index);
			_values = values;
		}
		else
		{
			System.arraycopy(_values, index, _values, index + size, _size - index);
			
			while (iter.hasNext())
			{
				_values[i++] = iter.next();
			}
			
			System.arraycopy(_values, 0, _values, 0, index);
		}
		
		return false;
	}
	
	public final void addFirst(final E value)
	{
		ensureCapacity(_size + 1);
		addFirstUnsafe(value);
	}
	
	public final void addFirstUnsafe(final E value)
	{
		System.arraycopy(_values, 0, _values, 1, _size);
		_values[0] = value;
	}
	
	public final void addLast(final E value)
	{
		ensureCapacity(_size + 1);
		addLastUnsafe(value);
	}
	
	public final void addLastUnsafe(final E value)
	{
		_values[_size++] = value;
	}
	
	@SuppressWarnings("unchecked")
	public final void addUnsafe(final int index, final E value)
	{
		final int spaceNeeded = _size + 1;
		
		if (_values.length < spaceNeeded)
		{
			final E[] values = (E[]) new Object[spaceNeeded];
			System.arraycopy(_values, 0, values, 0, index);
			values[index] = value;
			System.arraycopy(_values, index, values, index + 1, _size - index);
			_values = values;
		}
		else
		{
			System.arraycopy(_values, index, _values, index + 1, _size - index);
			_values[index] = value;
		}
		
		_size = spaceNeeded;
	}
	
	/**
	 * @see java.util.List#clear()
	 */
	@Override
	public final void clear()
	{
		for (int i = _size; i-- > 0;)
		{
			_values[i] = null;
		}
		reset();
	}
	
	public final void reset()
	{
		_size = 0;
	}
	
	@Override
	public final boolean contains(final Object obj)
	{
		if (obj != null)
		{
			for (int i = _size; i-- > 0;)
			{
				if (obj.equals(_values[i]))
					return true;
			}
		}
		else
		{
			for (int i = _size; i-- > 0;)
			{
				if (_values[i] == null)
					return true;
			}
		}
		
		return false;
	}
	
	public final Object[] directArray()
	{
		return _values;
	}
	
	@SuppressWarnings("unchecked")
	public final void setCapacity(final int capacity)
	{
		if (_values.length != capacity)
		{
			final E[] values = (E[]) new Object[capacity];
			System.arraycopy(_values, 0, values, 0, _size);
			_values = values;
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void ensureCapacity(final int capacity)
	{
		if (_values.length < capacity)
		{
			final int newCapacity = capacity << 1;
			final E[] values = (E[]) new Object[newCapacity > capacity ? newCapacity : capacity];
			System.arraycopy(_values, 0, values, 0, _size);
			_values = values;
		}
	}
	
	/**
	 * @see java.util.List#get(int)
	 */
	@Override
	public final E get(final int index)
	{
		if (index >= _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		return getUnsafe(index);
	}
	
	public final E getUnsafe(final int index)
	{
		return _values[index];
	}
	
	public final E getLastUnsafe()
	{
		return _values[_size - 1];
	}
	
	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public final int indexOf(final Object obj)
	{
		if (obj != null)
		{
			for (int i = 0; i < _size; i++)
			{
				if (obj.equals(_values[i]))
					return i;
			}
		}
		else
		{
			for (int i = 0; i < _size; i++)
			{
				if (_values[i] == null)
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * @see java.util.List#iterator()
	 */
	@Override
	public final Iterator<E> iterator()
	{
		return new FastIterator();
	}
	
	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public final int lastIndexOf(final Object obj)
	{
		if (obj != null)
		{
			for (int i = _size; i-- > 0;)
			{
				if (obj.equals(_values[i]))
					return i;
			}
		}
		else
		{
			for (int i = _size; i-- > 0;)
			{
				if (_values[i] == null)
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * NOT SUPPORTED!
	 * 
	 * @see java.util.List#listIterator()
	 */
	@Override
	public final ListIterator<E> listIterator()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * NOT SUPPORTED!
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public final ListIterator<E> listIterator(final int index)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @see java.util.List#remove(int)
	 */
	@Override
	public final E remove(final int index)
	{
		if (index >= _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		return removeUnsafe(index);
	}
	
	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public final boolean remove(final Object obj)
	{
		if (_size == 0)
			return false;
		
		return removeUnsafe(obj);
	}
	
	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public final boolean removeAll(final Collection<?> c)
	{
		if (_size == 0 || c.isEmpty())
			return false;
		
		boolean cng = false;
		final Iterator<?> iter = c.iterator();
		while (iter.hasNext())
		{
			if (removeUnsafe(iter.next()))
				cng = true;
		}
		return cng;
	}
	
	public final void removeAll(final FastArrayList<? extends E> list)
	{
		for (int i = list.size(); i-- > 0;)
		{
			removeUnsafeVoid(indexOf(list.getUnsafe(i)));
		}
	}
	
	public final E removeFirst()
	{
		if (_size == 0)
			throw new NoSuchElementException();
		
		return removeFirstUnsafe();
	}
	
	public final E removeFirstUnsafe()
	{
		final E value = _values[0];
		System.arraycopy(_values, 1, _values, 0, --_size);
		return value;
	}
	
	public final E removeLast()
	{
		if (_size == 0)
			throw new NoSuchElementException();
		
		return removeLastUnsafe();
	}
	
	public final E removeLastUnsafe()
	{
		final E value = _values[--_size];
		_values[_size] = null;
		return value;
	}
	
	public final E removeUnsafe(final int index)
	{
		final E value = _values[index];
		System.arraycopy(_values, index + 1, _values, index, --_size - index);
		_values[_size] = null;
		return value;
	}
	
	public final boolean removeUnsafe(final Object value)
	{
		final int index = indexOf(value);
		if (index >= 0)
		{
			removeUnsafe(index);
			return true;
		}
		
		return false;
	}
	
	public final void removeUnsafeVoid(final int index)
	{
		if (index < --_size)
			System.arraycopy(_values, index + 1, _values, index, _size - index);
		_values[_size] = null;
	}
	
	public final void removeVoid(final int index)
	{
		if (index >= _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		removeUnsafeVoid(index);
	}
	
	public final boolean retainAll(final Collection<?> collection)
	{
		throw new UnsupportedOperationException();
	}
	
	public final E set(final int index, final E value)
	{
		if (index >= _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		return setUnsafe(index, value);
	}
	
	public final E setUnsafe(final int index, final E value)
	{
		final E old = _values[index];
		_values[index] = value;
		return old;
	}
	
	public final void setUnsafeVoid(final int index, final E value)
	{
		_values[index] = value;
	}
	
	public final void setVoid(final int index, final E value)
	{
		if (index >= _size || index < 0)
			throw new IndexOutOfBoundsException();
		
		setUnsafeVoid(index, value);
	}
	
	public final void shuffle(final Random random)
	{
		for (int i = _size; i-- > 0;)
		{
			swapUnsafe(i, random.nextInt(_size));
		}
	}
	
	public final void sort(final Comparator<E> c)
	{
		Arrays.sort(_values, 0, _size, c);
	}
	
	public final void swap(final int index1, final int index2)
	{
		if (index1 < 0 || index1 >= _size)
			throw new IndexOutOfBoundsException("Index1 < 0 or >= _size");
		
		if (index2 < 0 || index2 >= _size)
			throw new IndexOutOfBoundsException("Index1 < 0 or >= _size");
		
		swapUnsafe(index1, index2);
	}
	
	public final void swapUnsafe(final int index1, final int index2)
	{
		final E value = _values[index1];
		_values[index1] = _values[index2];
		_values[index2] = value;
	}
	
	/**
	 * @see java.util.List#toArray()
	 */
	@Override
	public final Object[] toArray()
	{
		return toArray(new Object[_size]);
	}
	
	/**
	 * @see java.util.List#toArray(T[])
	 */
	@Override
	public final <T> T[] toArray(final T[] array)
	{
		System.arraycopy(_values, 0, array, 0, _size);
		return array;
	}
	
	public final <T> T[] toArray(final T[] array, final int offset)
	{
		System.arraycopy(_values, 0, array, offset, _size);
		return array;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString()
	{
		if (isEmpty())
			return "[]";
		
		final E[] values = _values;
		final int size = _size;
		final StringBuilder isb = new StringBuilder(128);
		isb.append('[');
		for (int i = 0; i < size; i++)
		{
			isb.append(values[i]);
			if (i != size - 1)
			{
				isb.append(' ');
				isb.append(',');
			}
		}
		isb.append(']');
		return isb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public final void trimToSize()
	{
		if (_values.length != _size)
		{
			final E[] values = (E[]) new Object[_size];
			System.arraycopy(_values, 0, values, 0, _size);
			_values = values;
		}
	}
	
	/**
	 * @see java.util.List#size()
	 */
	@Override
	public final int size()
	{
		return _size;
	}
	
	/**
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public final boolean isEmpty()
	{
		return _size == 0;
	}
	
	public final boolean containsAll(final FastArrayList<? extends E> list)
	{
		for (int i = list.size(); i-- > 0;)
		{
			if (!contains(list.getUnsafe(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public final boolean containsAll(final Collection<?> c)
	{
		final Iterator<?> iter = c.iterator();
		while (iter.hasNext())
		{
			if (!contains(iter.next()))
				return false;
		}
		return true;
	}
	
	/**
	 * NOT SUPPORTED!
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public final List<E> subList(final int fromIndex, final int toIndex)
	{
		throw new UnsupportedOperationException();
	}
}