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

import java.util.Comparator;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class Util
{
	public static final int arrayIndexOf(final Object[] array, final Object value)
	{
		for (int i = array.length; i-- > 0;)
		{
			if (array[i] == value)
				return i;
		}
		return -1;
	}
	
	/**
	 * @see java.util.Arrays#mergeSort(Object[] src,Object[] dest,int low,int high,int off)
	 */
	public static final <T> void mergeSort(final T[] src, final T[] dest, final int length, final Comparator<T> comparator)
	{
		mergeSort(src, dest, 0, length, comparator);
	}
	
	/**
	 * @see java.util.Arrays#mergeSort(Object[] src,Object[] dest,int low,int high,int off)
	 */
	private static final <T> void mergeSort(final T[] src, final T[] dest, int low, int high, final Comparator<T> comparator)
	{
		int length = high - low;
		
		// Insertion sort on smallest arrays
		if (length < 7)
		{
			for (int i = low; i < high; i++)
				for (int j = i; j > low && comparator.compare(dest[j - 1], dest[j]) > 0; j--)
					swap(dest, j, j - 1);
			return;
		}
		
		// Recursively sort halves of dest into src
		int destLow = low;
		int destHigh = high;
		int mid = (low + high) >>> 1;
		mergeSort(dest, src, low, mid, comparator);
		mergeSort(dest, src, mid, high, comparator);
		
		// If list is already sorted, just copy from src to dest.  This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if (comparator.compare(src[mid - 1], src[mid]) <= 0)
		{
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}
		
		// Merge sorted halves (now in src) into dest
		for (int i = destLow, p = low, q = mid; i < destHigh; i++)
		{
			if (q >= high || p < mid && comparator.compare(src[p], src[q]) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}
	
	private static final <T> void swap(final T[] x, final int a, final int b)
	{
		final T t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
}