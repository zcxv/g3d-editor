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

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoByteBuffer implements GeoReader, GeoWriter
{
	private final byte[] _data;
	private int _position;
	
	public static final GeoByteBuffer allocate(final int capacity)
	{
		return new GeoByteBuffer(capacity);
	}
	
	private GeoByteBuffer(final int capacity)
	{
		_data = new byte[capacity];
	}
	
	public final int position()
	{
		return _position;
	}
	
	public final int capacity()
	{
		return _data.length;
	}
	
	public final void clear()
	{
		_position = 0;
	}
	
	/**
	 * @see g3deditor.util.GeoReader#get()
	 */
	public final byte get()
	{
		return _data[_position++];
	}
	
	/**
	 * @see g3deditor.util.GeoReader#getShort()
	 */
	public final short getShort()
	{
		return (short) (get() & 0xFF | get() << 8 & 0xFF00); 
	}
	
	/**
	 * @see g3deditor.util.GeoWriter#put(byte)
	 */
	public final void put(final byte value)
	{
		_data[_position++] = value;
	}
	
	/**
	 * @see g3deditor.util.GeoWriter#putShort(short)
	 */
	public final void putShort(final short value)
	{
		_data[_position++] = (byte) (value & 0xFF);
		_data[_position++] = (byte) (value >> 8 & 0xFF);
	}
}