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
import g3deditor.util.BufferUtils;
import g3deditor.util.Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

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
	private int _vboIndex;
	private int _vboVertex;
	private int _vboTexture;
	
	private int _indexBufferLen;
	private FloatBuffer _vertexBuffer;
	
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
		_enabled = false;
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
	
	public final void setNeedUpdateVBO()
	{
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
		
		final int xUnit = GeoEngine.GEO_REGION_SIZE - 1;
		final int zUnit = GeoEngine.GEO_REGION_SIZE - 1;
		
		final IntBuffer indexBuffer = BufferUtils.createIntBuffer(xUnit * zUnit * 6);
		for (int i = 0, j; i < zUnit; i++)
		{
			for (j = 0; j < xUnit; j++)
			{
				indexBuffer.put(i * GeoEngine.GEO_REGION_SIZE + j);
				indexBuffer.put(i * GeoEngine.GEO_REGION_SIZE + GeoEngine.GEO_REGION_SIZE + j);
				indexBuffer.put(i * GeoEngine.GEO_REGION_SIZE + GeoEngine.GEO_REGION_SIZE + j + 1);
				indexBuffer.put(i * GeoEngine.GEO_REGION_SIZE + j);
				indexBuffer.put(i * GeoEngine.GEO_REGION_SIZE + GeoEngine.GEO_REGION_SIZE + j + 1);
				indexBuffer.put(i * GeoEngine.GEO_REGION_SIZE + j + 1);
			}
		}
		indexBuffer.flip();
		
		final FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(GeoEngine.GEO_REGION_SIZE * GeoEngine.GEO_REGION_SIZE * 2);
		final float xStep = 1F / (xUnit);
		final float zStep = 1F / (zUnit);
		for (int i = 0, j; i < GeoEngine.GEO_REGION_SIZE; i++)
		{
			for (j = 0; j < GeoEngine.GEO_REGION_SIZE; j++)
			{
				textureBuffer.put(j * xStep);
				textureBuffer.put(1F - i * zStep);
			}
		}
		textureBuffer.flip();
		
		_vertexBuffer = BufferUtils.createFloatBuffer(GeoEngine.GEO_REGION_SIZE * GeoEngine.GEO_REGION_SIZE * 3);
		_indexBufferLen = indexBuffer.remaining();
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.remaining() * BufferUtils.INTEGER_SIZE, indexBuffer, GL2.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboTexture);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, textureBuffer.remaining() * BufferUtils.FLOAT_SIZE, textureBuffer, GL2.GL_STATIC_DRAW);
	}
	
	public final void render(final GL2 gl)
	{
		if (_vboIndex == -1 || !_enabled)
			return;
		
		if (_needUpdateTexture)
		{
			_needUpdateTexture = false;
			
			if (_texture != null)
				_texture.destroy(gl);
			
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
				_texture = null;
				e.printStackTrace();
			}
		}
		
		if (_needUpdateVBO)
		{
			_needUpdateVBO = false;
			
			_vertexBuffer.clear();
			for (int blockY = 0, blockX; blockY < GeoEngine.GEO_REGION_SIZE; blockY++)
			{
				for (blockX = 0; blockX < GeoEngine.GEO_REGION_SIZE; blockX++)
				{
					_vertexBuffer.put(blockX);
					_vertexBuffer.put(getNeighboursMinHeight(_region.getGeoBlocks(), blockX, blockY) / 16f);
					_vertexBuffer.put(blockY);
				}
			}
			_vertexBuffer.flip();
			
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, _vboVertex);
			// call with null buffer first to tell VGA-driver that we give a shit about the old content 
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, 0, null, GL2.GL_DYNAMIC_DRAW);
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, _vertexBuffer.remaining() * BufferUtils.FLOAT_SIZE, _vertexBuffer, GL2.GL_DYNAMIC_DRAW);
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
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, _vboIndex);
		
		gl.glPushMatrix();
		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glTranslatef(GeoEngine.getGeoXY(_region.getRegionX(), 0), -0.3f, GeoEngine.getGeoXY(_region.getRegionY(), 0));
		gl.glScalef(8f, 1f, 8f);
		gl.glDrawElements(GL2.GL_TRIANGLES, _indexBufferLen, GL2.GL_UNSIGNED_INT, 0);
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
}