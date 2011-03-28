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
package g3deditor.jogl.shader.uniform;

import g3deditor.jogl.GLColor;
import g3deditor.jogl.shader.GLUniform;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLUniformVec4fv extends GLUniform
{
	private final FloatBuffer _buffer;
	
	public GLUniformVec4fv(final String uniformName, final int numElements)
	{
		super(uniformName);
		_buffer = Buffers.newDirectFloatBuffer(numElements * 4);
	}
	
	public final void clear()
	{
		_buffer.clear();
	}
	
	public final void put(final float v1, final float v2, final float v3, final float v4)
	{
		_buffer.put(v1);
		_buffer.put(v2);
		_buffer.put(v3);
		_buffer.put(v4);
	}
	
	public final void put(final GLColor color)
	{
		put(color.getR(), color.getG(), color.getB(), color.getA());
	}
	
	@Override
	public final void update(final GL2 gl)
	{
		_buffer.flip();
		gl.glUniform4fv(getUniformLocation(), _buffer.remaining(), _buffer);
	}
}