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
import java.io.OutputStream;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoStreamWriter implements GeoWriter
{
	public static final GeoStreamWriter wrap(final OutputStream os)
	{
		return new GeoStreamWriter(os);
	}
	
	private final OutputStream _os;
	
	private GeoStreamWriter(final OutputStream os)
	{
		_os = os;
	}
	
	/**
	 * @see g3deditor.util.GeoWriter#put(byte)
	 */
	@Override
	public final void put(final byte value)
	{
		try
		{
			_os.write(value);
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see g3deditor.util.GeoWriter#putShort(short)
	 */
	@Override
	public final void putShort(final short value)
	{
		try
		{
			_os.write((byte) (value & 0xFF));
			_os.write((byte) (value >> 8 & 0xFF));
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}