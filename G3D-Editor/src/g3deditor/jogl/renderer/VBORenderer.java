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
package g3deditor.jogl.renderer;

import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLCellRenderer;
import g3deditor.jogl.GLState;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class VBORenderer extends GLCellRenderer
{
	public static final boolean isAvailable(final GL2 gl)
	{
		return gl.isExtensionAvailable("GL_ARB_vertex_buffer_object")
			&& gl.isFunctionAvailable("glGenBuffersARB")
			&& gl.isFunctionAvailable("glBindBufferARB")
			&& gl.isFunctionAvailable("glBufferDataARB")
			&& gl.isFunctionAvailable("glDeleteBuffersARB");
	}
	
	public static final String NAME = "VertexBufferObject";
	public static final String NAME_SHORT = "VBO";
	
	private ByteBuffer _indexBuffer;
	private FloatBuffer _vertexBuffer;
	private FloatBuffer _textureBuffer;
	
	private int _vboIndex;
	private int _vboVertex;
	private int _vboTexture;
	
	@Override
	public final boolean init(final GL2 gl)
	{
		if (!super.init(gl))
			return false;
		
		isAvailable(gl);
		_indexBuffer = Buffers.newDirectByteBuffer(GEOMETRY_INDICES_DATA_LENGTH * NSWE_COMBINATIONS + GEOMETRY_INDICES_DATA_LENGTH);
		_vertexBuffer = Buffers.newDirectFloatBuffer(GEOMETRY_VERTEX_DATA_SMALL_LENGTH * NSWE_COMBINATIONS + GEOMETRY_VERTEX_DATA_SMALL_LENGTH);
		_textureBuffer = Buffers.newDirectFloatBuffer(GEOMETRY_TEXTURE_DATA_LENGTH * NSWE_COMBINATIONS + GEOMETRY_TEXTURE_DATA_LENGTH);
		
		_indexBuffer.put(GEOMETRY_INDICES_DATA_BYTE);
		_vertexBuffer.put(GEOMETRY_VERTEX_DATA_BIG);
		fillTextureUV(GeoEngine.NSWE_ALL, _textureBuffer);
		
		for (int i = 0, j; i < NSWE_COMBINATIONS ; i++)
		{
			for (j = 0; j < GEOMETRY_INDICES_DATA_LENGTH; j++)
			{
				_indexBuffer.put((byte) (GEOMETRY_INDICES_DATA_BYTE[j] + GEOMETRY_INDICES_DATA_MAX * i + GEOMETRY_INDICES_DATA_MAX));
			}
			
			_vertexBuffer.put(GEOMETRY_VERTEX_DATA_SMALL);
			fillTextureUV(i, _textureBuffer);
		}
		_indexBuffer.flip();
		_vertexBuffer.flip();
		_textureBuffer.flip();
		
		final int[] temp = new int[3];
		gl.glGenBuffers(3, temp, 0);
		_vboIndex = temp[0];
		_vboVertex = temp[1];
		_vboTexture = temp[2];
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, _indexBuffer.limit() * Buffers.SIZEOF_BYTE, _indexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _vertexBuffer.limit() * Buffers.SIZEOF_FLOAT, _vertexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _textureBuffer.limit() * Buffers.SIZEOF_FLOAT, _textureBuffer, GL2.GL_STATIC_DRAW);
		
		return true;
	}
	
	@Override
	public final void enableRender(final GL2 gl)
	{
		super.enableRender(gl);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
	}
	
	@Override
	public final void render(final GL2 gl, final GLSubRenderSelector selector)
	{
		GeoCell cell;
		for (int i = selector.getElementsToRender(); i-- > 0;)
		{
			cell = selector.getElementToRender(i);
			GLState.glColor4f(gl, cell.getSelectionState().getColor(cell));
			GLState.translatef(gl, cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
			gl.glDrawElements(
				GL2.GL_TRIANGLES,
				GEOMETRY_INDICES_DATA_LENGTH,
				GL2.GL_UNSIGNED_BYTE,
				(cell.isBig()
					? 0
					: (cell.getNSWE() * GEOMETRY_INDICES_DATA_LENGTH) + GEOMETRY_INDICES_DATA_LENGTH)
			);
		}
	}
	
	@Override
	public final void disableRender(final GL2 gl)
	{
		super.disableRender(gl);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}
	
	@Override
	public final void dispose(final GL2 gl)
	{
		super.dispose(gl);
		gl.glDeleteBuffers(3, new int[]{_vboIndex, _vboVertex, _vboTexture}, 0);
	}
	
	@Override
	public final String getName()
	{
		return NAME;
	}
	
	@Override
	public final String toString()
	{
		return NAME_SHORT;
	}
}