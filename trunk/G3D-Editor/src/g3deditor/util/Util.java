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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;

import javax.imageio.ImageIO;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class Util
{
	public static interface FastComparator<T>
	{
		public boolean compare(final T o1, final T o2);
	}
	
	public static final int arrayIndexOf(final Object[] array, final Object value)
	{
		for (int i = array.length; i-- > 0;)
		{
			if (array[i] == value)
				return i;
		}
		return -1;
	}
	
	public static final <T> void quickSort(final T[] values, final FastComparator<T> comparator)
	{
		quickSort(values, values.length, comparator);
	}
	
	public static final <T> void quickSort(final T[] values, final int length, final FastComparator<T> comparator)
	{
		final int high = length - 1;
		quickSortImpl(values, 0, high, comparator);
		insertionSort(values, 0, high, comparator);
	}
	
	private static final <T> void quickSortImpl(final T[] values, final int low, final int high, final FastComparator<T> comparator)
	{
		int i;
		int j;
		T v;
		
		if (high - low > 4)
		{
			i = (high + low) / 2;
			if (comparator.compare(values[low], values[i]))
				swap(values, low, i);
			if (comparator.compare(values[low], values[high]))
				swap(values, low, high);
			if (comparator.compare(values[i], values[high]))
				swap(values, i, high);
			
			j = high - 1;
			swap(values, i, j);
			i = low;
			v = values[j];
			while (true)
			{
				while (comparator.compare(v, values[++i]));
				while (comparator.compare(values[--j], v));
				if (j < i)
					break;
				swap(values, i, j);
			}
			swap(values, i, high - 1);
			quickSortImpl(values, low, j, comparator);
			quickSortImpl(values, i + 1, high, comparator);
		}
	}
	
	private static final <T> void insertionSort(final T[] values, final int low, final int high, final FastComparator<T> comparator)
	{
		T v;
		for (int i = low + 1, j; i <= high; i++)
		{
			v = values[i];
			j = i;
			
			while ((j > low) && comparator.compare(values[j - 1], v))
			{
				values[j] = values[j - 1];
				j--;
			}
			values[j] = v;
		}
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
	
	public static final BufferedImage loadImage(final String file)
	{
		return loadImage(new File(file));
	}
	
	public static final BufferedImage loadImage(final File file)
	{
		try
		{
			return ImageIO.read(file);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Scales the source image to the given width/height.<br>
	 * Faster then BufferedImage.getScaledInstance(width, height, hints) and returning an BufferedImage instead of an ToolkitImage.
	 * 
	 * @param img The image to be scaled
	 * @param width The resulting image width
	 * @param height The resulting image height
	 * @param quality The quality level (0 = lowest, 1 = medium, 2 = highest, anything else = default)
	 * @return The scaled image
	 */
	public static final BufferedImage scaleImage(final BufferedImage img, final int width, final int height, final int quality)
	{
		final BufferedImage scaled = new BufferedImage(width, height, img.getType() == 0 ? BufferedImage.TYPE_4BYTE_ABGR : img.getType());
		final Graphics2D g = scaled.createGraphics();
		
		switch (quality)
		{
			case 0:
			{
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				break;
			}
				
			case 1:
			{
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				break;
			}
				
			case 2:
			{
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				break;
			}
		}
		
		g.drawImage(img, 0, 0, width, height, null);
		g.dispose();
		
		return scaled;
	}
	
	public static final void writeByte(final int value, final OutputStream os) throws IOException
	{
		os.write((byte) (value & 0xFF));
	}
	
	public static final void writeBytes(final byte[] values, final OutputStream os) throws IOException
	{
		os.write(values);
	}
	
	public static final void writeInt(final int value, final OutputStream os) throws IOException
	{
		os.write((byte) (value & 0xFF));
		os.write((byte) (value >> 8 & 0xFF));
		os.write((byte) (value >> 16 & 0xFF));
		os.write((byte) (value >> 24 & 0xFF));
	}
	
	public static final void writeShort(final int value, final OutputStream os) throws IOException
	{
		os.write((byte) (value & 0xFF));
		os.write((byte) (value >> 8 & 0xFF));
	}
}