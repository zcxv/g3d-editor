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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class BufferUtils
{
	public static final int BYTE_SIZE = Byte.SIZE / Byte.SIZE;
	public static final int SHORT_SIZE = Short.SIZE / Byte.SIZE;
	public static final int CHAR_SIZE = Character.SIZE / Byte.SIZE;
	public static final int INTEGER_SIZE = Integer.SIZE / Byte.SIZE;
	public static final int LONG_SIZE = Long.SIZE / Byte.SIZE;
	public static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;
	public static final int DOUBLE_SIZE = Double.SIZE / Byte.SIZE;
	
	public static final ShortBuffer createShortBuffer(final short[] data)
	{
		final ShortBuffer sb = createShortBuffer(data.length);
		sb.put(data);
		sb.flip();
		return sb;
	}
	
	public static final IntBuffer createIntBuffer(final int[] data)
	{
		final IntBuffer ib = createIntBuffer(data.length);
		ib.put(data);
		ib.flip();
		return ib;
	}
	
	public static final FloatBuffer createFloatBuffer(final float[] data)
	{
		final FloatBuffer fb = createFloatBuffer(data.length);
		fb.put(data);
		fb.flip();
		return fb;
	}
	
	public static final ByteBuffer createByteBuffer(final byte[] data)
	{
		final ByteBuffer bb = createByteBuffer(data.length);
		bb.put(data);
		bb.flip();
		return bb;
	}
	
	public static final ByteBuffer createByteBuffer(final int size)
	{
		final ByteBuffer bb = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		bb.clear();
		return bb;
	}
	
	public static final ShortBuffer createShortBuffer(final int size)
	{
		final ShortBuffer sb = ByteBuffer.allocateDirect((Short.SIZE / Byte.SIZE) * size).order(ByteOrder.nativeOrder()).asShortBuffer();
		sb.clear();
		return sb;
	}
	
	public static final IntBuffer createIntBuffer(final int size)
	{
		final IntBuffer ib = ByteBuffer.allocateDirect((Integer.SIZE / Byte.SIZE) * size).order(ByteOrder.nativeOrder()).asIntBuffer();
		ib.clear();
		return ib;
	}
	
	public static final FloatBuffer createFloatBuffer(final int size)
	{
		final FloatBuffer fb = ByteBuffer.allocateDirect((Float.SIZE / Byte.SIZE) * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
		fb.clear();
		return fb;
	}
}