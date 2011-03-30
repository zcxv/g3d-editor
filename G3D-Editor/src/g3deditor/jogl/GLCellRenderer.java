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

import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.renderer.DLLoDRenderer;
import g3deditor.jogl.renderer.DLRenderer;
import g3deditor.jogl.renderer.IRenderer;
import g3deditor.jogl.renderer.VBOGSLSRenderer;
import g3deditor.jogl.renderer.VBORenderer;
import g3deditor.jogl.shader.GLShader;

import java.io.File;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public abstract class GLCellRenderer
{
	public static final String[] RENDERER_NAMES =
	{
		IRenderer.NAME,
		DLRenderer.NAME,
		VBORenderer.NAME,
		DLLoDRenderer.NAME,
		VBOGSLSRenderer.NAME,
	};
	
	public static final GLCellRenderer getRenderer(final String name)
	{
		if (IRenderer.NAME.equals(name))
			return new IRenderer();
		
		if (DLRenderer.NAME.equals(name))
			return new DLRenderer();
		
		if (VBORenderer.NAME.equals(name))
			return new VBORenderer();
		
		if (VBOGSLSRenderer.NAME.equals(name))
			return new VBOGSLSRenderer();
		
		return new DLLoDRenderer();
	}
	
	public static final String validateRenderer(final String name)
	{
		for (final String temp : GLCellRenderer.RENDERER_NAMES)
		{
			if (temp.equals(name))
				return temp;
		}
		return DLLoDRenderer.NAME;
	}
	
	protected static final int NSWE_COMBINATIONS = 16;
	protected static final int NSWE_TEX_ROWS_COLS = 4;
	protected static final float NSWE_TEX_BLOCK = 1f / NSWE_TEX_ROWS_COLS;
	
	protected static final byte[] GEOMETRY_INDICES_DATA_BYTE =
	{
		0, 1, 2, 2, 3, 0,
		1, 5, 6, 6, 2, 1,
		7, 6, 5, 5, 4, 7,
		4, 0, 3, 3, 7, 4,
		0, 5, 1, 5, 0, 4,
		8, 9, 10, 10, 11, 8 // top
	};
	
	protected static final short[] GEOMETRY_INDICES_DATA_SHORT =
	{
		0, 1, 2, 2, 3, 0,
		1, 5, 6, 6, 2, 1,
		7, 6, 5, 5, 4, 7,
		4, 0, 3, 3, 7, 4,
		0, 5, 1, 5, 0, 4,
		8, 9, 10, 10, 11, 8 // top
	};
	
	protected static final int GEOMETRY_INDICES_DATA_LENGTH = GEOMETRY_INDICES_DATA_BYTE.length;
	protected static final int GEOMETRY_INDICES_DATA_MAX_INDEX = 11;
	protected static final int GEOMETRY_INDICES_DATA_MAX = GEOMETRY_INDICES_DATA_MAX_INDEX + 1;
	
	protected static final float[] GEOMETRY_VERTEX_DATA_SMALL =
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
	
	protected static final int GEOMETRY_VERTEX_DATA_SMALL_LENGTH = GEOMETRY_VERTEX_DATA_SMALL.length;
	
	protected static final float[] GEOMETRY_VERTEX_DATA_BIG =
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
	
	private static final float TEX_COORD_U1 = 0.1f; // identifier for vertex shader
	private static final float TEX_COORD_U2 = 0.2f; // identifier for vertex shader
	private static final float TEX_COORD_V1 = 0.3f; // identifier for vertex shader
	private static final float TEX_COORD_V2 = 0.4f; // identifier for vertex shader
	
	protected static final float[] GEOMETRY_TEXTURE_DATA =
	{
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		TEX_COORD_U1, // top
		TEX_COORD_V2, // top
		TEX_COORD_U2, // top
		TEX_COORD_V2, // top
		TEX_COORD_U2, // top
		TEX_COORD_V1, // top
		TEX_COORD_U1, // top
		TEX_COORD_V1 // top
	};
	
	protected static final void fillTextureUV(final int nswe, final FloatBuffer textureBuffer)
	{
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
		textureBuffer.position(textureBuffer.position() + GEOMETRY_TEXTURE_DATA_LENGTH - 8);
		textureBuffer.put(u1);
		textureBuffer.put(v2);
		textureBuffer.put(u2);
		textureBuffer.put(v2);
		textureBuffer.put(u2);
		textureBuffer.put(v1);
		textureBuffer.put(u1);
		textureBuffer.put(v1);
	}
	
	protected static final int GEOMETRY_TEXTURE_DATA_LENGTH = GEOMETRY_TEXTURE_DATA.length;
	
	protected static final void renderCellFull(final GL2 gl, final boolean big, final int nswe)
	{
		final float size = big ? 7.9f : 0.9f;
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glVertex3f(0.1f, -0.2f, 0.1f);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glVertex3f(size, -0.2f, 0.1f);
		gl.glVertex3f(size, 0.0f, 0.1f);
		gl.glVertex3f(size, -0.2f, size);
		gl.glVertex3f(size, 0.0f, size);
		gl.glVertex3f(0.1f, -0.2f, size);
		gl.glVertex3f(0.1f, 0.0f, size);
		gl.glVertex3f(0.1f, -0.2f, 0.1f);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glEnd();
		
		renderCellBottom(gl, big, -0.2f);
		renderCellTop(gl, big, nswe);
	}
	
	protected static final void renderCellTop(final GL2 gl, final boolean big, final int nswe)
	{
		final float size = big ? 7.9f : 0.9f;
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glTexCoord2f(u1, v1);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glTexCoord2f(u1, v2);
		gl.glVertex3f(0.1f, 0.0f, size);
		gl.glTexCoord2f(u2, v1);
		gl.glVertex3f(size, 0.0f, 0.1f);
		gl.glTexCoord2f(u2, v2);
		gl.glVertex3f(size, 0.0f, size);
		gl.glEnd();
	}
	
	protected static final void renderCellBottom(final GL2 gl, final boolean big, final float height)
	{
		final float size = big ? 7.9f : 0.9f;
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glVertex3f(0.1f, height, size);
		gl.glVertex3f(0.1f, height, 0.1f);
		gl.glVertex3f(size, height, size);
		gl.glVertex3f(size, height, 0.1f);
		gl.glEnd();
	}
	
	private boolean _initialized;
	private Texture _nsweTexture;
	private GLShader _shader;
	
	protected final Texture getNsweTexture()
	{
		return _nsweTexture;
	}
	
	protected final GLShader getShader()
	{
		return _shader;
	}
	
	protected final void initShader(final GL2 gl, final String vertexShaderPath, final String fragmentShaderPath)
	{
		_shader = new GLShader(vertexShaderPath, fragmentShaderPath);
		_shader.init(gl);
	}
	
	public boolean init(final GL2 gl)
	{
		if (_initialized)
			return false;
		
		_initialized = true;
		
		try
		{
			_nsweTexture = TextureIO.newTexture(new File("./data/textures/nswe.png"), true);
			_nsweTexture.enable();
			_nsweTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
			_nsweTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		}
		catch (final Exception e)
		{
			_nsweTexture = null;
			e.printStackTrace();
		}
		
		if (_shader != null && _nsweTexture != null)
			_shader.setTexture(gl, _nsweTexture, "nswe_texture");
		
		return true;
	}
	
	public void enableRender(final GL2 gl)
	{
		if (_nsweTexture != null)
			_nsweTexture.bind();
		
		if (_shader != null)
		{
			gl.glUseProgram(getShader().getProgramId());
		}
		
		gl.glPushMatrix();
		GLState.resetTranslate();
	}
	
	public abstract void render(final GL2 gl, final GLSubRenderSelector selector);
	
	public void disableRender(final GL2 gl)
	{
		gl.glPopMatrix();
		gl.glUseProgram(0);
	}
	
	public void dispose(final GL2 gl)
	{
		if (!_initialized)
			return;
		
		_initialized = false;
		
		if (_nsweTexture != null)
			_nsweTexture.destroy(gl);
		
		if (_shader != null)
			_shader.dispose(gl);
	}
	
	public abstract String getName();
}