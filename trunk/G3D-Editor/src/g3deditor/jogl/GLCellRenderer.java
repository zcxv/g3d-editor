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

import g3deditor.entity.CellColor;
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.renderer.DLLoDRenderer;
import g3deditor.jogl.renderer.DLRenderer;
import g3deditor.jogl.renderer.IRenderer;
import g3deditor.jogl.renderer.VBORenderer;

import java.io.File;

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
		DLLoDRenderer.NAME
	};
	
	public static final GLCellRenderer getRenderer(final String name)
	{
		if (IRenderer.NAME.equals(name))
			return new IRenderer();
		
		if (DLRenderer.NAME.equals(name))
			return new DLRenderer();
		
		if (VBORenderer.NAME.equals(name))
			return new VBORenderer();
		
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
	
	protected static final float COLOR_ALPHA = 0.7f;
	protected static final int NSWE_COMBINATIONS = 16;
	protected static final int NSWE_TEX_ROWS_COLS = 4;
	protected static final float NSWE_TEX_BLOCK = 1f / NSWE_TEX_ROWS_COLS;
	
	private boolean _initialized;
	private Texture _nsweTexture;
	private CellColor _color;
	private float _translateX;
	private float _translateY;
	private float _translateZ;
	
	public void init(final GL2 gl)
	{
		if (_initialized)
			return;
		
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
	}
	
	protected final void setColor(final GL2 gl, final CellColor color)
	{
		if (_color != color)
		{
			_color = color;
			gl.glColor4f(color.getR(), color.getG(), color.getB(), COLOR_ALPHA);
		}
	}
	
	public final void translatef(final GL2 gl, final float x, final float y, final float z)
	{
		if (_translateX != x || _translateY != y || _translateZ != z)
		{
			final float dx = x - _translateX;
			final float dy = y - _translateY;
			final float dz = z - _translateZ;
			_translateX = x;
			_translateY = y;
			_translateZ = z;
			gl.glTranslatef(dx, dy, dz);
		}
	}
	
	public void enableRender(final GL2 gl)
	{
		if (_nsweTexture != null)
			_nsweTexture.bind();
		
		_color = null;
		_translateX = 0f;
		_translateY = 0f;
		_translateZ = 0f;
		
		gl.glPushMatrix();
	}
	
	public abstract void render(final GL2 gl, final GLSubRenderSelector selector);
	
	public void disableRender(final GL2 gl)
	{
		gl.glPopMatrix();
	}
	
	public void dispose(final GL2 gl)
	{
		if (!_initialized)
			return;
		
		_initialized = false;
		
		if (_nsweTexture != null)
			_nsweTexture.destroy(gl);
	}
	
	public abstract String getName();
	
	protected final void renderCellFull(final GL2 gl, final boolean big, final int nswe)
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
	
	protected final void renderCellTop(final GL2 gl, final boolean big, final int nswe)
	{
		final float size = big ? 7.9f : 0.9f;
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glTexCoord2f(u2, v2);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glTexCoord2f(u1, v2);
		gl.glVertex3f(0.1f, 0.0f, size);
		gl.glTexCoord2f(u2, v1);
		gl.glVertex3f(size, 0.0f, 0.1f);
		gl.glTexCoord2f(u1, v1);
		gl.glVertex3f(size, 0.0f, size);
		gl.glEnd();
	}
	
	protected final void renderCellBottom(final GL2 gl, final boolean big, final float height)
	{
		final float size = big ? 7.9f : 0.9f;
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glVertex3f(0.1f, height, size);
		gl.glVertex3f(0.1f, height, 0.1f);
		gl.glVertex3f(size, height, size);
		gl.glVertex3f(size, height, 0.1f);
		gl.glEnd();
	}
}