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

import g3deditor.entity.CellColor;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.GLRenderer;
import g3deditor.util.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class VBORenderer extends GLRenderer
{
	private static final int TEX_COORDS = 24;
	
	private static final short[] GEOMETRY_INDICES_DATA =
	{
		0, 1, 2, 2, 3, 0,
		1, 5, 6, 6, 2, 1,
		7, 6, 5, 5, 4, 7,
		4, 0, 3, 3, 7, 4,
		0, 5, 1, 5, 0, 4,
		8, 9, 10, 10, 11, 8 // top
	};
	
	private static final int GEOMETRY_INDICES_DATA_LENGTH = GEOMETRY_INDICES_DATA.length;
	private static final int GEOMETRY_INDICES_DATA_MAX = 12;
	
	private static final float[] GEOMETRY_VERTEX_DATA_SMALL =
	{
		0.1f, -0.2f, 0.9f,
		0.9f, -0.2f, 0.9f,
		0.9f,  0.0f, 0.9f,
		0.1f,  0.0f, 0.9f,
		0.1f, -0.2f, 0.1f,
		0.9f, -0.2f, 0.1f,
		0.9f,  0.0f, 0.1f,
		0.1f,  0.0f, 0.1f,
		0.1f,  0.0f, 0.9f, // top
		0.9f,  0.0f, 0.9f, // top
		0.9f,  0.0f, 0.1f, // top
		0.1f,  0.0f, 0.1f // top
	};
	
	private static final int GEOMETRY_VERTEX_DATA_SMALL_LENGTH = GEOMETRY_VERTEX_DATA_SMALL.length;
	
	private static final float[] GEOMETRY_VERTEX_DATA_BIG =
	{
		0.1f, -0.2f, 7.9f,
		7.9f, -0.2f, 7.9f,
		7.9f,  0.0f, 7.9f,
		0.1f,  0.0f, 7.9f,
		0.1f, -0.2f, 0.1f,
		7.9f, -0.2f, 0.1f,
		7.9f,  0.0f, 0.1f,
		0.1f,  0.0f, 0.1f,
		0.1f,  0.0f, 7.9f, // top
		7.9f,  0.0f, 7.9f, // top
		7.9f,  0.0f, 0.1f, // top
		0.1f,  0.0f, 0.1f // top
	};
	
	private static final float[] GEOMETRY_TEXTURE_DATA =
	{
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 0, 0, 1, 0, 1, 1 // top
	};
	
	private static final int GEOMETRY_TEXTURE_DATA_LENGTH = GEOMETRY_TEXTURE_DATA.length;
	
	private ShortBuffer _indexBuffer;
	private FloatBuffer _vertexBuffer;
	private FloatBuffer _textureBuffer;
	
	private int _vboIndex;
	private int _vboVertex;
	private int _vboTexture;
	
	/**
	 * @param display
	 */
	public VBORenderer(final GLDisplay display)
	{
		super(display);
	}
	
	private static final void fillTextureUV(final int nswe, final FloatBuffer textureBuffer)
	{
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
		textureBuffer.position(textureBuffer.position() + TEX_COORDS - 8);
		textureBuffer.put(u1);
		textureBuffer.put(v2);
		textureBuffer.put(u1);
		textureBuffer.put(v1);
		textureBuffer.put(u2);
		textureBuffer.put(v1);
		textureBuffer.put(u2);
		textureBuffer.put(v2);
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#init(javax.media.opengl.GL2)
	 */
	@Override
	public final void init(final GL2 gl)
	{
		_indexBuffer = BufferUtils.createShortBuffer(GEOMETRY_INDICES_DATA_LENGTH * NSWE_COMBINATIONS + GEOMETRY_INDICES_DATA_LENGTH);
		_vertexBuffer = BufferUtils.createFloatBuffer(GEOMETRY_VERTEX_DATA_SMALL_LENGTH * NSWE_COMBINATIONS + GEOMETRY_VERTEX_DATA_SMALL_LENGTH);
		_textureBuffer = BufferUtils.createFloatBuffer(GEOMETRY_TEXTURE_DATA_LENGTH * NSWE_COMBINATIONS + GEOMETRY_TEXTURE_DATA_LENGTH);
		
		_indexBuffer.put(GEOMETRY_INDICES_DATA);
		_vertexBuffer.put(GEOMETRY_VERTEX_DATA_BIG);
		fillTextureUV(GeoEngine.NSWE_ALL, _textureBuffer);
		
		for (int i = 0, j; i < NSWE_COMBINATIONS ; i++)
		{
			for (j = 0; j < GEOMETRY_INDICES_DATA_LENGTH; j++)
			{
				_indexBuffer.put((short) (GEOMETRY_INDICES_DATA[j] + GEOMETRY_INDICES_DATA_MAX * i + GEOMETRY_INDICES_DATA_MAX));
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
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, _indexBuffer.limit() * BufferUtils.SHORT_SIZE, _indexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _vertexBuffer.limit() * BufferUtils.FLOAT_SIZE, _vertexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _textureBuffer.limit() * BufferUtils.FLOAT_SIZE, _textureBuffer, GL2.GL_STATIC_DRAW);
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#enableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void enableRender(final GL2 gl)
	{
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#render(javax.media.opengl.GL2, g3deditor.geo.GeoCell)
	 */
	public final void render(final GL2 gl, final GeoCell cell)
	{
		final CellColor color = cell.getSelectionState().getColor(cell);
		gl.glPushMatrix();
		gl.glColor4f(color.getR(), color.getG(), color.getB(), COLOR_ALPHA);
		gl.glTranslatef(cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
		
		if (cell.isBig())
		{
			gl.glDrawElements(GL2.GL_TRIANGLES, GEOMETRY_INDICES_DATA_LENGTH, GL2.GL_UNSIGNED_SHORT, 0);
		}
		else
		{
			gl.glDrawElements(GL2.GL_TRIANGLES, GEOMETRY_INDICES_DATA_LENGTH, GL2.GL_UNSIGNED_SHORT, (cell.getNSWE() * GEOMETRY_INDICES_DATA_LENGTH + GEOMETRY_INDICES_DATA_LENGTH) * BufferUtils.SHORT_SIZE);
		}
		
		gl.glPopMatrix();
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#disableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void disableRender(final GL2 gl)
	{
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#dispose(javax.media.opengl.GL2)
	 */
	@Override
	public final void dispose(final GL2 gl)
	{
		gl.glDeleteBuffers(3, new int[]{_vboIndex, _vboVertex, _vboTexture}, 0);
	}
	
	@Override
	public final String toString()
	{
		return "VertexBufferObject Renderer";
	}
}