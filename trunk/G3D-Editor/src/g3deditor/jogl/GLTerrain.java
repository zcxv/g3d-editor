package g3deditor.jogl;

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.util.BufferUtils;
import g3deditor.util.Util;
import g3deditor.util.Vector3f;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public final class GLTerrain
{
	private final GeoRegion _region;
	private final TerrainDetailLevel _detailLevel;
	private final IntBuffer _indexBuffer;
	private final FloatBuffer _vertexBuffer;
	private final FloatBuffer _textureBuffer;
	
	private int _vboIndex;
	private int _vboVertex;
	private int _vboTexture;
	private Texture _texture;
	
	private boolean _initialized;
	private boolean _wireframe;
	
	public GLTerrain(final GeoRegion region, final TerrainDetailLevel detailLevel)
	{
		_region = region;
		_detailLevel = detailLevel;
		
		final Vector3f[][] vertex = getVertex(region.getGeoBlocks(), detailLevel);
		
		final int xUp = vertex[0].length;
		final int zUp = vertex.length;
		final int xUnit = xUp - 1;
		final int zUnit = zUp - 1;
		
		_indexBuffer = BufferUtils.createIntBuffer(xUnit * zUnit * 6);
		for (int i = 0, j; i < zUnit; i++)
		{
			for (j = 0; j < xUnit; j++)
			{
				_indexBuffer.put(i * xUp + j);
				_indexBuffer.put(i * xUp + xUp + j);
				_indexBuffer.put(i * xUp + xUp + j + 1);
				_indexBuffer.put(i * xUp + j);
				_indexBuffer.put(i * xUp + xUp + j + 1);
				_indexBuffer.put(i * xUp + j + 1);
			}
		}
		_indexBuffer.flip();
		
		_vertexBuffer = BufferUtils.createFloatBuffer(xUp * zUp * 3);
		for (int i = 0, j; i < zUp; i++)
		{
			for (j = 0; j < xUp; j++)
			{
				_vertexBuffer.put(vertex[i][j].getX());
				_vertexBuffer.put(vertex[i][j].getY());
				_vertexBuffer.put(vertex[i][j].getZ());
			}
		}
		_vertexBuffer.flip();
		
		_textureBuffer = BufferUtils.createFloatBuffer(xUp * zUp * 2);
		final float xStep = 1F / (xUp - 1);
		final float zStep = 1F / (zUp - 1);
		for (int i = 0, j; i < zUp; i++)
		{
			for (j = 0; j < xUp; j++)
			{
				_textureBuffer.put(j * xStep);
				_textureBuffer.put(1F - i * zStep);
			}
		}
		_textureBuffer.flip();
	}
	
	public final GeoRegion getRegion()
	{
		return _region;
	}
	
	public final TerrainDetailLevel getDetailLevel()
	{
		return _detailLevel;
	}
	
	public final boolean getWireframe()
	{
		return _wireframe;
	}
	
	public final void setWireframe(final boolean wireframe)
	{
		_wireframe = wireframe;
	}
	
	public final void init(final GL2 gl)
	{
		if (_initialized)
			return;
		
		_initialized = true;
		
		try
		{
			final File file = new File("./data/textures/region/" + (_region.getRegionX() + 10) + "_" + (_region.getRegionY() + 10) + ".jpg");
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
			e.printStackTrace();
		}
		
		final int[] temp = new int[3];
		gl.glGenBuffers(3, temp, 0);
		_vboIndex = temp[0];
		_vboVertex = temp[1];
		_vboTexture = temp[2];
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, _indexBuffer.limit() * BufferUtils.INTEGER_SIZE, _indexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _vertexBuffer.limit() * BufferUtils.FLOAT_SIZE, _vertexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _textureBuffer.limit() * BufferUtils.FLOAT_SIZE, _textureBuffer, GL2.GL_STATIC_DRAW);
	}
	
	public final void render(final GL2 gl)
	{
		// TODO Make multiple small terrain blocks and check for frustum culling + LOD
		if (!_initialized)
			return;
		
		if (_texture != null)
			_texture.bind();
		
		if (_wireframe)
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
		
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		
		gl.glPushMatrix();
		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glTranslatef(GeoEngine.getGeoXY(_region.getRegionX(), 0), -0.3f, GeoEngine.getGeoXY(_region.getRegionY(), 0));
		gl.glScalef(_detailLevel.getScale(), 1f, _detailLevel.getScale());
		gl.glDrawElements(GL2.GL_TRIANGLES, _indexBuffer.limit(), GL2.GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
		
		// clear depth buffer to not corrupt the cell picking
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
	}
	
	public final void dispose(final GL2 gl)
	{
		if (!_initialized)
			return;
		
		_initialized = false;
		
		if (_texture != null)
			_texture.destroy(gl);
		
		gl.glDeleteBuffers(3, new int[]{_vboIndex, _vboVertex, _vboTexture}, 0);
	}
	
	private static final Vector3f[][] getVertex(final GeoBlock[][] blocks, final TerrainDetailLevel detailLevel)
	{
		final Vector3f[][] vertex;
		
		if (detailLevel == TerrainDetailLevel.LOW)
		{
			int blockX, blockY;
			
			vertex = new Vector3f[GeoEngine.GEO_REGION_SIZE][GeoEngine.GEO_REGION_SIZE];
			for (blockX = GeoEngine.GEO_REGION_SIZE; blockX-- > 0;)
			{
				for (blockY = GeoEngine.GEO_REGION_SIZE; blockY-- > 0;)
				{
					vertex[blockY][blockX] = new Vector3f(blockX, getNeighboursMinHeight(blocks, blockX, blockY) / 16f, blockY);
				}
			}
			return vertex;
		}
		
		int blockX, blockY, vertexX, vertexY, detailX, detailY;
		
		final int detail = detailLevel.getDetail();
		final int cells = GeoEngine.GEO_BLOCK_SHIFT / detail;
		
		vertex = new Vector3f[GeoEngine.GEO_REGION_SIZE * detail][GeoEngine.GEO_REGION_SIZE * detail];
		for (blockX = GeoEngine.GEO_REGION_SIZE; blockX-- > 0;)
		{
			vertexX = blockX * detail;
			for (blockY = GeoEngine.GEO_REGION_SIZE; blockY-- > 0;)
			{
				vertexY = blockY * detail;
				
				for (detailX = detail; detailX-- > 0;)
				{
					for (detailY = detail; detailY-- > 0;)
					{
						vertex[vertexY + detailY][vertexX + detailX] = getVector(blocks, blockX, blockY, detailX * cells, detailY * cells, cells, vertexX + detailX, vertexY + detailY);
					}
				}
			}
		}
		return vertex;
	}
	
	private static final Vector3f getVector(final GeoBlock[][] blocks, final int blockX, final int blockY, final int cellX, final int cellY, final int cells, final int vertexX, final int vertexY)
	{
		int height = Integer.MAX_VALUE;
		height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX - cells, cellY - cells, cells));
		height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX - cells, cellY, cells));
		height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX, cellY - cells, cells));
		height = Math.min(height, getBlockHeightOfGroup(blocks, blockX, blockY, cellX, cellY, cells));
		return new Vector3f(vertexX, height / 16f, vertexY);
	}
	
	private static final int getBlockHeightOfGroup(final GeoBlock[][] blocks, int blockX, int blockY, int cellX, int cellY, final int cells)
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
		
		return blockX < 0 || blockY < 0 || blockX >= GeoEngine.GEO_REGION_SIZE || blockY >= GeoEngine.GEO_REGION_SIZE ? Short.MAX_VALUE : getBlockHeightOfGroup(blocks[blockX][blockY], cellX, cellY, cells);
	}
	
	private static final int getBlockHeightOfGroup(final GeoBlock block, final int cellX, final int cellY, final int cells)
	{
		int height = Integer.MAX_VALUE, x, y;
		for (x = cells; x-- > 0;)
		{
			for (y = cells; y-- > 0;)
			{
				height = Math.min(height, block.nGetCellByLayer(cellX + x, cellY + y, 0).getHeight());
			}
		}
		return height;
	}
	
	private static final int getNeighboursMinHeight(final GeoBlock[][] blocks, final int blockX, final int blockY)
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
	
	public static enum TerrainDetailLevel
	{
		LOW,
		MEDIUM,
		HIGH,
		ULTRA;
		
		public final int getDetail()
		{
			return 1 << ordinal();
		}
		
		public final float getScale()
		{
			return (float) TerrainDetailLevel.ULTRA.getDetail() / (float) getDetail();
		}
	}
}