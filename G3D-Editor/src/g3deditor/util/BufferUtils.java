package g3deditor.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class BufferUtils
{
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