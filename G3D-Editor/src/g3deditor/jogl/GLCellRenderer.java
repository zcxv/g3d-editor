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

import java.io.File;

import g3deditor.geo.GeoCell;

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
	protected static final float COLOR_ALPHA = 0.7f;
	protected static final int NSWE_COMBINATIONS = 16;
	protected static final int NSWE_TEX_ROWS_COLS = 4;
	protected static final float NSWE_TEX_BLOCK = 1f / NSWE_TEX_ROWS_COLS;
	
	protected final void renderCell(final GL2 gl, final boolean big, final int nswe)
	{
		final float size = big ? 7.9f : 0.9f;
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
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
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glVertex3f(0.1f, -0.2f, size);
		gl.glVertex3f(0.1f, -0.2f, 0.1f);
		gl.glVertex3f(size, -0.2f, size);
		gl.glVertex3f(size, -0.2f, 0.1f);
		gl.glEnd();
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
	
	private Texture _nsweTexture;
	
	public void init(final GL2 gl)
	{
		try
		{
			_nsweTexture = TextureIO.newTexture(new File("./data/textures/nswe.png"), true);
			_nsweTexture.enable();
			_nsweTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
			_nsweTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void enableRender(final GL2 gl)
	{
		_nsweTexture.bind();
	}
	
	public abstract void render(final GL2 gl, final GeoCell cell);
	
	public void disableRender(final GL2 gl)
	{
		
	}
	
	public void dispose(final GL2 gl)
	{
		_nsweTexture.destroy(gl);
	}
}