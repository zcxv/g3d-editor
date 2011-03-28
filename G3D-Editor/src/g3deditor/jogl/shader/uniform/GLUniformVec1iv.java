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

import g3deditor.jogl.shader.GLUniform;

import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLUniformVec1iv extends GLUniform
{
	private final IntBuffer _buffer;
	
	public GLUniformVec1iv(final String uniformName, final int numElements)
	{
		super(uniformName);
		_buffer = Buffers.newDirectIntBuffer(numElements);
	}
	
	public final void clear()
	{
		_buffer.clear();
	}
	
	public final void put(final int v1)
	{
		_buffer.put(v1);
	}
	
	public final boolean hasRemaining()
	{
		return _buffer.hasRemaining();
	}
	
	public final boolean isEmpty()
	{
		return _buffer.position() == 0;
	}
	
	public final int position()
	{
		return _buffer.position();
	}
	
	public final int getRemaining()
	{
		return _buffer.remaining();
	}
	
	@Override
	public final void update(final GL2 gl)
	{
		_buffer.flip();
		gl.glUniform1iv(getUniformLocation(), _buffer.remaining(), _buffer);
	}
}