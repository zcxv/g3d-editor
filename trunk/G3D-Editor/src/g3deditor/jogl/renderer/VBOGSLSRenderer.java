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

import g3deditor.entity.SelectionState;
import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLCellRenderer;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.shader.GLShader;
import g3deditor.jogl.shader.uniform.GLUniformVec1i;
import g3deditor.jogl.shader.uniform.GLUniformVec1iv;
import g3deditor.jogl.shader.uniform.GLUniformVec2f;
import g3deditor.jogl.shader.uniform.GLUniformVec4fv;
import g3deditor.util.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class VBOGSLSRenderer extends GLCellRenderer
{
	public static final String NAME = "VertexBufferObject GSLS";
	public static final String NAME_SHORT = "VBO GSLS";
	
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
	private static final int GEOMETRY_INDICES_DATA_MAX_INDEX = 11;
	private static final int GEOMETRY_INDICES_DATA_MAX = GEOMETRY_INDICES_DATA_MAX_INDEX + 1;
	
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
		0.1f, 0.4f, 0.1f, 0.3f, 0.2f, 0.3f, 0.2f, 0.4f // top
	};
	
	private static final int GEOMETRY_TEXTURE_DATA_LENGTH = GEOMETRY_TEXTURE_DATA.length;
	
	private static final void fillTextureUV(final int nswe, final FloatBuffer textureBuffer)
	{
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
		textureBuffer.position(textureBuffer.position() + GEOMETRY_TEXTURE_DATA_LENGTH - 8);
		textureBuffer.put(u1);
		textureBuffer.put(v2);
		
		textureBuffer.put(u1);
		textureBuffer.put(v1);
		
		textureBuffer.put(u2);
		textureBuffer.put(v1);
		
		textureBuffer.put(u2);
		textureBuffer.put(v2);
	}
	
	private int _vboIndex;
	private int _vboVertex;
	private int _vboTexture;
	
	private GLShader _shader;
	private GLUniformVec4fv _cellColors;
	private GLUniformVec2f _blockPosition;
	private GLUniformVec1iv _blockData;
	private GLUniformVec1iv _cellPositions;
	private GLUniformVec1i _blockType;
	private GLUniformVec1i _cellData;
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#init(javax.media.opengl.GL2)
	 */
	@Override
	public final boolean init(final GL2 gl)
	{
		if (!super.init(gl))
			return false;
		
		final int[] temp = new int[3];
		gl.glGenBuffers(3, temp, 0);
		_vboIndex = temp[0];
		_vboVertex = temp[1];
		_vboTexture = temp[2];
		
		final ShortBuffer indexBuffer = BufferUtils.createShortBuffer(GEOMETRY_INDICES_DATA_LENGTH * 65);
		final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(GEOMETRY_VERTEX_DATA_SMALL_LENGTH * 65);
		final FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(GEOMETRY_TEXTURE_DATA_LENGTH * 65);
		
		indexBuffer.put(GEOMETRY_INDICES_DATA);
		vertexBuffer.put(GEOMETRY_VERTEX_DATA_BIG);
		fillTextureUV(GeoEngine.NSWE_ALL, textureBuffer);
		
		for (int x = 0, y, i; x < 8; x++)
		{
			for (y = 0; y < 8; y++)
			{
				for (i = 0; i < GEOMETRY_INDICES_DATA_LENGTH; i++)
				{
					indexBuffer.put((short) (GEOMETRY_INDICES_DATA[i] + GEOMETRY_INDICES_DATA_MAX * (x * 8 + y) + GEOMETRY_INDICES_DATA_MAX));
				}
				
				for (i = 0; i < GEOMETRY_VERTEX_DATA_SMALL_LENGTH;)
				{
					vertexBuffer.put(GEOMETRY_VERTEX_DATA_SMALL[i++] + x);
					vertexBuffer.put(GEOMETRY_VERTEX_DATA_SMALL[i++]);
					vertexBuffer.put(GEOMETRY_VERTEX_DATA_SMALL[i++] + y);
				}
				
				textureBuffer.put(GEOMETRY_TEXTURE_DATA);
			}
		}
		
		indexBuffer.flip();
		vertexBuffer.flip();
		textureBuffer.flip();
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.remaining() * BufferUtils.SHORT_SIZE, indexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexBuffer.remaining() * BufferUtils.FLOAT_SIZE, vertexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, textureBuffer.remaining() * BufferUtils.FLOAT_SIZE, textureBuffer, GL2.GL_STATIC_DRAW);
		
		_shader = new GLShader("./data/shader/VertexShader.txt", "./data/shader/FragmentShader.txt");
		_shader.init(gl);
		_shader.setTexture(gl, getNsweTexture(), "nswe_texture");
		_cellColors = _shader.getUniformVec4fv(gl, "cell_colors", 12);
		_blockPosition = _shader.getUniformVec2f(gl, "block_position");
		_blockData = _shader.getUniformVec1iv(gl, "block_data", 64);
		_blockType = _shader.getUniformVec1i(gl, "block_type");
		_cellData = _shader.getUniformVec1i(gl, "cell_data");
		_cellPositions = _shader.getUniformVec1iv(gl, "cell_positions", 64);
		return true;
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#enableRender(javax.media.opengl.GL2)
	 */
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
		
		gl.glUseProgram(_shader.getProgramId());
		
		_cellColors.clear();
		_cellColors.put(SelectionState.NORMAL.getColorFlat());
		_cellColors.put(SelectionState.HIGHLIGHTED.getColorFlat());
		_cellColors.put(SelectionState.SELECTED.getColorFlat());
		_cellColors.put(SelectionState.NORMAL.getColorComplex());
		_cellColors.put(SelectionState.HIGHLIGHTED.getColorComplex());
		_cellColors.put(SelectionState.SELECTED.getColorComplex());
		_cellColors.put(SelectionState.NORMAL.getColorMultiLayer());
		_cellColors.put(SelectionState.HIGHLIGHTED.getColorMultiLayer());
		_cellColors.put(SelectionState.SELECTED.getColorMultiLayer());
		_cellColors.put(SelectionState.NORMAL.getColorMultiLayerSpecial());
		_cellColors.put(SelectionState.HIGHLIGHTED.getColorMultiLayerSpecial());
		_cellColors.put(SelectionState.SELECTED.getColorMultiLayerSpecial());
		_cellColors.update(gl);
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#render(javax.media.opengl.GL2, g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector)
	 */
	public final void render(final GL2 gl, final GLSubRenderSelector selector)
	{
		if (selector.getElementsToRender() == 0)
			return;
		
		final GeoBlock block = selector.getGeoBlock();
		_blockType.set(block.getType());
		_blockType.update(gl);
		_blockPosition.set(block.getGeoX(), block.getGeoY());
		_blockPosition.update(gl);
		
		GeoCell cell;
		
		switch (block.getType())
		{
			case GeoEngine.GEO_BLOCK_TYPE_FLAT:
			{
				cell = block.nGetCellByLayer(0, 0, 0);
				_cellData.set(cell.getSelectionState().ordinal() << 1 | cell.getHeight() << 3);
				_cellData.update(gl);
				gl.glDrawElements(GL2.GL_TRIANGLES, GEOMETRY_INDICES_DATA_LENGTH, GL2.GL_UNSIGNED_SHORT, 0);
				break;
			}
			
			case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
			{
				_blockData.clear();
				for (int x = 0, y; x < 8; x++)
				{
					for (y = 0; y < 8; y++)
					{
						cell = block.nGetCellByLayer(x, y, 0);
						_blockData.put(cell.getSelectionState().ordinal() << 1 | cell.getHeightAndNSWE() << 3);
					}
				}
				_blockData.update(gl);
				gl.glDrawElements(GL2.GL_TRIANGLES, GEOMETRY_INDICES_DATA_LENGTH * 64, GL2.GL_UNSIGNED_SHORT, GEOMETRY_INDICES_DATA_LENGTH * Buffers.SIZEOF_SHORT);
				break;
			}
				
			case GeoEngine.GEO_BLOCK_TYPE_MULTILAYER:
			{
				_blockData.clear();
				_cellPositions.clear();
				for (int i = selector.getElementsToRender(); i-- > 0;)
				{
					if (!_blockData.hasRemaining())
					{
						_blockData.update(gl);
						_cellPositions.update(gl);
						gl.glDrawElements(GL2.GL_TRIANGLES, GEOMETRY_INDICES_DATA_LENGTH * 64, GL2.GL_UNSIGNED_SHORT, GEOMETRY_INDICES_DATA_LENGTH * Buffers.SIZEOF_SHORT);
					}
					
					cell = selector.getElementToRender(i);
					if (GLDisplay.getInstance().getSelectionBox().isInside(cell))
					{
						_blockData.put(1 | cell.getSelectionState().ordinal() << 1 | cell.getHeightAndNSWE() << 3);
					}
					else
					{
						_blockData.put(cell.getSelectionState().ordinal() << 1 | cell.getHeightAndNSWE() << 3);
					}
					
					_cellPositions.put(cell.getCellX() | cell.getCellY() << 16);
				}
				
				if (!_blockData.isEmpty())
				{
					_blockData.update(gl);
					_cellPositions.update(gl);
					gl.glDrawElements(GL2.GL_TRIANGLES, GEOMETRY_INDICES_DATA_LENGTH * _blockData.getRemaining(), GL2.GL_UNSIGNED_SHORT, GEOMETRY_INDICES_DATA_LENGTH * Buffers.SIZEOF_SHORT);
				}
				break;
			}
		}
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#disableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void disableRender(final GL2 gl)
	{
		super.disableRender(gl);
		
		gl.glUseProgram(0);
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#dispose(javax.media.opengl.GL2)
	 */
	@Override
	public final void dispose(final GL2 gl)
	{
		super.dispose(gl);
		
		_shader.dispose(gl);
		gl.glDeleteBuffers(3, new int[]{_vboIndex, _vboVertex, _vboTexture}, 0);
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#getName()
	 */
	@Override
	public final String getName()
	{
		return NAME;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString()
	{
		return NAME_SHORT;
	}
}