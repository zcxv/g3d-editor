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

import java.io.IOException;
import java.io.InputStream;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoStreamReader implements GeoReader
{
	public static final GeoStreamReader wrap(final InputStream is)
	{
		return new GeoStreamReader(is);
	}
	
	private final InputStream _is;
	
	private GeoStreamReader(final InputStream is)
	{
		_is = is;
	}
	
	/**
	 * @see g3deditor.util.GeoReader#get()
	 */
	@Override
	public final byte get()
	{
		final int read;
		
		try
		{
			read = _is.read();
			if (read == -1)
				throw new RuntimeException("No more data available");
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return (byte) read;
	}
	
	/**
	 * @see g3deditor.util.GeoReader#getShort()
	 */
	@Override
	public final short getShort()
	{
		return (short) (get() & 0xFF | get() << 8 & 0xFF00); 
	}
}