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
package g3deditor.jogl;

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.util.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLTerrain
{
	private TerrainBuilder _builder;
	private int _vboIndex;
	private int _vboVertex;
	private int _vboTexture;
	
	private GeoRegion _region;
	private Texture _texture;
	
	private boolean _needUpdateTexture;
	private boolean _needUpdateVBO;
	
	private boolean _wireframe;
	private boolean _enabled;
	
	public GLTerrain()
	{
		_vboIndex = -1;
		_vboVertex = -1;
		_vboTexture = -1;
	}
	
	public final void setRegion(final GeoRegion region)
	{
		if (_region != region)
		{
			_region = region;
			_needUpdateTexture = true;
			_needUpdateVBO = true;
		}
	}
	
	public final void setEnabled(final boolean enabled)
	{
		_enabled = enabled;
	}
	
	public final GeoRegion getRegion()
	{
		return _region;
	}
	
	public final boolean getWireframe()
	{
		return _wireframe;
	}
	
	public final void setWireframe(final boolean wireframe)
	{
		_wireframe = wireframe;
	}
	
	public final void checkNeedUpdateVBO(final boolean blockMinHeightChanged, final boolean blockMaxHeightChanged)
	{
		if (blockMinHeightChanged || blockMaxHeightChanged)
			_needUpdateVBO = true;
	}
	
	public final void init(final GL2 gl)
	{
		if (_vboIndex != -1)
			return;
		
		final int[] temp = new int[3];
		gl.glGenBuffers(3, temp, 0);
		_vboIndex = temp[0];
		_vboVertex = temp[1];
		_vboTexture = temp[2];
		
		_builder = TerrainDetailLevel.LOW.newBuilder();
		_builder.init(gl, _vboIndex, _vboTexture);
	}
	
	public final void render(final GL2 gl)
	{
		if (_vboIndex == -1 || !_enabled || _region == null)
			return;
		
		if (_needUpdateTexture)
		{
			_needUpdateTexture = false;
			
			if (_texture != null)
				_texture.destroy(gl);
			
			try
			{
				File file = new File("./data/textures/region/" + (_region.getRegionX() + 10) + "_" + (_region.getRegionY() + 10) + ".jpg");
				if (!file.isFile())
					file = new File("./data/textures/region/water.jpg");
				
				if (file.isFile())
				{
					final BufferedImage img = Util.loadImage(file);
					ImageUtil.flipImageVertically(img);
					_texture = AWTTextureIO.newTexture(gl.getGLProfile(), img, false);
					_texture.enable();
					_texture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
					_texture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				}
			}
			catch (final Exception e)
			{
				_texture = null;
				e.printStackTrace();
			}
		}
		
		if (_needUpdateVBO)
		{
			_needUpdateVBO = false;
			_builder.update(gl, _vboVertex, _region.getGeoBlocks(), false);
		}
		
		if (_texture != null)
			_texture.bind();
		
		if (_wireframe)
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
		
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glVertexPointer(3, GL2.GL_SHORT, 0, 0);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		
		gl.glPushMatrix();
		GLState.glColor4f(gl, GLColor.WHITE);
		gl.glTranslatef(GeoEngine.getGeoXY(_region.getRegionX(), 0), -0.3f, GeoEngine.getGeoXY(_region.getRegionY(), 0));
		final int scale = _builder.getDetailLevel().getScaleXZ();
		gl.glScalef(scale, 1f, scale);
		gl.glDrawElements(GL2.GL_TRIANGLES, _builder.getIndexBufferLen(), GL2.GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}
	
	public final void dispose(final GL2 gl)
	{
		if (_vboIndex == -1)
			return;
		
		if (_texture != null)
			_texture.destroy(gl);
		
		gl.glDeleteBuffers(3, new int[]{_vboIndex, _vboVertex, _vboTexture}, 0);
		_vboIndex = -1;
		_vboVertex = -1;
		_vboTexture = -1;
	}
	
	public static enum TerrainDetailLevel
	{
		LOW(TerrainBuilderLowDetail.class),
		MEDIUM(TerrainBuilderXDetail.class),
		HIGH(TerrainBuilderXDetail.class),
		ULTRA(TerrainBuilderXDetail.class);
		
		private final Class<?> _builder;
		
		private TerrainDetailLevel(final Class<?> builder)
		{
			_builder = builder;
		}
		
		public final int getScaleXZ()
		{
			return ULTRA.getFactor() / getFactor();
		}
		
		public final int getFactor()
		{
			return 1 << ordinal();
		}
		
		public final TerrainBuilder newBuilder()
		{
			try
			{
				return (TerrainBuilder) _builder.getConstructor(TerrainDetailLevel.class).newInstance(this);
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	public static abstract class TerrainBuilder
	{
		private final TerrainDetailLevel _detailLevel;
		private ShortBuffer _vertexBuffer;
		private boolean _initialized;
		private int _indexBufferLen;
		
		public TerrainBuilder(final TerrainDetailLevel detailLevel)
		{
			_detailLevel = detailLevel;
		}
		
		public final TerrainDetailLevel getDetailLevel()
		{
			return _detailLevel;
		}
		
		public final int getIndexBufferLen()
		{
			return _indexBufferLen;
		}
		
		public final void init(final GL2 gl, final int vboIndex, final int vboTexture)
		{
			if (_initialized)
				return;
			
			_initialized = true;
			
			final int factor = getDetailLevel().getFactor();
			final int xUp = GeoEngine.GEO_REGION_SIZE * factor;
			final int zUp = GeoEngine.GEO_REGION_SIZE * factor;
			
			final int xUnit = xUp - 1;
			final int zUnit = zUp - 1;
			final IntBuffer indexBuffer = Buffers.newDirectIntBuffer(xUnit * zUnit * 6);
			for (int i = 0, j; i < zUnit; i++)
			{
				for (j = 0; j < xUnit; j++)
				{
					indexBuffer.put(i * xUp + j);
					indexBuffer.put(i * xUp + xUp + j);
					indexBuffer.put(i * xUp + xUp + j + 1);
					indexBuffer.put(i * xUp + j);
					indexBuffer.put(i * xUp + xUp + j + 1);
					indexBuffer.put(i * xUp + j + 1);
				}
			}
			
			indexBuffer.flip();
			
			final FloatBuffer textureBuffer = Buffers.newDirectFloatBuffer(xUp * zUp * 2);
			final float xStep = 1F / (xUp);
			final float zStep = 1F / (zUp);
			for (int i = 0, j; i < zUp; i++)
			{
				for (j = 0; j < xUp; j++)
				{
					textureBuffer.put(j * xStep);
					textureBuffer.put(1F - i * zStep);
				}
			}
			textureBuffer.flip();
			
			_vertexBuffer = Buffers.newDirectShortBuffer(xUp * zUp * 3);
			_indexBufferLen = indexBuffer.remaining();
			
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vboIndex);
			gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.remaining() * Buffers.SIZEOF_INT, indexBuffer, GL2.GL_STATIC_DRAW);
			
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboTexture);
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, textureBuffer.remaining() * Buffers.SIZEOF_FLOAT, textureBuffer, GL2.GL_STATIC_DRAW);
		}
		
		public final void update(final GL2 gl, final int vboVertex, final GeoBlock[][] blocks, final boolean flipped)
		{
			_vertexBuffer.clear();
			updateImpl(_vertexBuffer, blocks, flipped);
			_vertexBuffer.flip();
			
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboVertex);
			// call with null buffer first to tell VGA-driver that we give a shit about the old content
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, 0, null, GL2.GL_DYNAMIC_DRAW);
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, _vertexBuffer.remaining() * Buffers.SIZEOF_SHORT, _vertexBuffer, GL2.GL_DYNAMIC_DRAW);
		}
		
		public abstract void updateImpl(final ShortBuffer vertexBuffer, final GeoBlock[][] blocks, final boolean flipped);
	}
	
	public static final class TerrainBuilderLowDetail extends TerrainBuilder
	{
		public TerrainBuilderLowDetail(final TerrainDetailLevel detailLevel)
		{
			super(detailLevel);
		}
		
		/**
		 * @see g3deditor.jogl.GLTerrain.TerrainBuilder#updateImpl(java.nio.FloatBuffer, g3deditor.geo.GeoBlock[][], boolean)
		 */
		@Override
		public final void updateImpl(final ShortBuffer vertexBuffer, final GeoBlock[][] blocks, final boolean flipped)
		{
			for (int blockY = 0, blockX; blockY < GeoEngine.GEO_REGION_SIZE; blockY++)
			{
				for (blockX = 0; blockX < GeoEngine.GEO_REGION_SIZE; blockX++)
				{
					vertexBuffer.put((short) blockX);
					vertexBuffer.put((short) (getNeighboursHeight(blocks, blockX, blockY, flipped) / 16f -0.5f));
					vertexBuffer.put((short) blockY);
				}
			}
		}
		
		private final int getNeighboursHeight(final GeoBlock[][] blocks, final int blockX, final int blockY, final boolean flipped)
		{
			if (flipped)
			{
				if (blockX > 0)
				{
					if (blockY > 0)
					{
						return Math.min(Math.min(Math.min(blocks[blockX][blockY].getMaxHeight(), blocks[blockX - 1][blockY].getMaxHeight()), blocks[blockX][blockY - 1].getMaxHeight()), blocks[blockX - 1][blockY - 1].getMaxHeight());
					}
					else
					{
						return Math.min(blocks[blockX][blockY].getMaxHeight(), blocks[blockX - 1][blockY].getMaxHeight());
					}
				}
				else
				{
					if (blockY > 0)
					{
						return Math.min(blocks[blockX][blockY].getMaxHeight(), blocks[blockX][blockY - 1].getMaxHeight());
					}
					else
					{
						return blocks[blockX][blockY].getMaxHeight();
					}
				}
			}
			else
			{
				if (blockX > 0)
				{
					if (blockY > 0)
					{
						return Math.min(Math.min(Math.min(blocks[blockX][blockY].getMinHeight(), blocks[blockX - 1][blockY].getMinHeight()), blocks[blockX][blockY - 1].getMinHeight()), blocks[blockX - 1][blockY - 1].getMinHeight());
					}
					else
					{
						return Math.min(blocks[blockX][blockY].getMinHeight(), blocks[blockX - 1][blockY].getMinHeight());
					}
				}
				else
				{
					if (blockY > 0)
					{
						return Math.min(blocks[blockX][blockY].getMinHeight(), blocks[blockX][blockY - 1].getMinHeight());
					}
					else
					{
						return blocks[blockX][blockY].getMinHeight();
					}
				}
			}
		}
	}
	
	public static final class TerrainBuilderXDetail extends TerrainBuilder
	{
		public TerrainBuilderXDetail(final TerrainDetailLevel detailLevel)
		{
			super(detailLevel);
		}
		
		/**
		 * @see g3deditor.jogl.GLTerrain.TerrainBuilder#updateImpl(java.nio.FloatBuffer, g3deditor.geo.GeoBlock[][], boolean)
		 */
		@Override
		public final void updateImpl(final ShortBuffer vertexBuffer, final GeoBlock[][] blocks, final boolean flipped)
		{
			final int factor = getDetailLevel().getFactor();
			final int cells = GeoEngine.GEO_BLOCK_SHIFT / factor;
			final int xzUp = GeoEngine.GEO_REGION_SIZE * factor;
			
			for (int blockX = GeoEngine.GEO_REGION_SIZE, blockY, vertexX, vertexY, factorX, factorY; blockX-- > 0;)
			{
				for (blockY = GeoEngine.GEO_REGION_SIZE; blockY-- > 0;)
				{
					for (factorX = factor; factorX-- > 0;)
					{
						vertexX = blockX * factor + factorX;
						for (factorY = factor; factorY-- > 0;)
						{
							vertexY = blockY * factor + factorY;
							vertexBuffer.position((vertexY * xzUp + vertexX) * 3);
							vertexBuffer.put((short) vertexX);
							vertexBuffer.put((short) (getHeight(blocks, blockX, blockY, factorX * cells, factorY * cells, cells, flipped) / 16f - 0.5f));
							vertexBuffer.put((short) vertexY);
						}
					}
				}
			}
			
			vertexBuffer.position(vertexBuffer.capacity());
		}
		
		private final int getHeight(final GeoBlock[][] blocks, final int blockX, final int blockY, final int cellX, final int cellY, final int cells, final boolean flipped)
		{
			int height = Integer.MAX_VALUE;
			height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX - cells, cellY - cells, cells, flipped));
			height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX - cells, cellY, cells, flipped));
			height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX, cellY - cells, cells, flipped));
			height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX, cellY, cells, flipped));
			return height;
		}
		
		private final int getBlockHeightOfGroup(final GeoBlock[][] blocks, int blockX, int blockY, int cellX, int cellY, final int cells, final boolean flipped)
		{
			if (cellX < 0)
			{
				blockX -= 1;
				cellX = GeoEngine.GEO_BLOCK_SHIFT - cells;
			}
			else if (cellX >= GeoEngine.GEO_BLOCK_SHIFT)
			{
				blockX += 1;
				cellX = 0;
			}
			
			if (cellY < 0)
			{
				blockY -= 1;
				cellY = GeoEngine.GEO_BLOCK_SHIFT - cells;
			}
			else if (cellY >= GeoEngine.GEO_BLOCK_SHIFT)
			{
				blockY += 1;
				cellY = 0;
			}
			
			if (blockX < 0 || blockY < 0 || blockX >= GeoEngine.GEO_REGION_SIZE || blockY >= GeoEngine.GEO_REGION_SIZE)
				return Short.MAX_VALUE;
			
			return getBlockHeightOfGroup(blocks[blockX][blockY], cellX, cellY, cells, flipped);
		}
		
		private final int getBlockHeightOfGroup(final GeoBlock block, final int cellX, final int cellY, final int cells, final boolean flipped)
		{
			int height = Integer.MAX_VALUE, x, y;
			for (x = cells; x-- > 0;)
			{
				for (y = cells; y-- > 0;)
				{
					height = Math.min(height, (flipped ? block.nGetCellByLayer(cellX + x, cellY + y, block.nGetLayerCount(cellX + x, cellY + y) - 1) : block.nGetCellByLayer(cellX + x, cellY + y, 0)).getHeight());
				}
			}
			return height;
		}
	}
}