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

import g3deditor.util.BufferUtils;
import g3deditor.util.FastArrayList;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLGUIRenderer
{
	public static final int TEXT_HEIGHT = 16;
	public static final int COMPASS_HEIGHT = 96;
	public static final int COMPASS_HALF_HEIGHT = COMPASS_HEIGHT / 2;
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	private final FastArrayList<GLText> _texts;
	
	private int _listId;
	private Texture _fontTexture;
	private Texture _compassTexture;
	
	public GLGUIRenderer()
	{
		_texts = new FastArrayList<GLText>();
	}
	
	public final GLText newText(final int x, final int y)
	{
		final GLText text = new GLText(x, y);
		_texts.add(text);
		return text;
	}
	
	private static final int FONT_TEX_ROWS_COLS = 16;
	private static final float FONT_TEX_BLOCK = 1f / FONT_TEX_ROWS_COLS;
	
	public final void init(final GL2 gl)
	{
		_listId = gl.glGenLists(257); // Creating 256 Display Lists
		for (int loop = 0; loop < 256; loop++) // Loop Through All 256 Lists
		{
			final float u1 = (loop % FONT_TEX_ROWS_COLS) * FONT_TEX_BLOCK;
			final float u2 = u1 + FONT_TEX_BLOCK;
			final float v1 = (loop / FONT_TEX_ROWS_COLS) * FONT_TEX_BLOCK;
			final float v2 = v1 + FONT_TEX_BLOCK;
			
			gl.glNewList(_listId + loop, GL2.GL_COMPILE);
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			gl.glTexCoord2f(u1, v1);
			gl.glVertex3f(0.0f, TEXT_HEIGHT, 0.0f);
			gl.glTexCoord2f(u1, v2);
			gl.glVertex3f(0.0f, 0.0f, 0.0f);
			gl.glTexCoord2f(u2, v1);
			gl.glVertex3f(TEXT_HEIGHT, TEXT_HEIGHT, 0.0f);
			gl.glTexCoord2f(u2, v2);
			gl.glVertex3f(TEXT_HEIGHT, 0.0f, 0.0f);
			gl.glEnd();
			gl.glTranslatef(9f, 0, 0);
			gl.glEndList();
		}
		
		gl.glNewList(_listId + 256, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-COMPASS_HALF_HEIGHT, COMPASS_HALF_HEIGHT, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-COMPASS_HALF_HEIGHT, -COMPASS_HALF_HEIGHT, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(COMPASS_HALF_HEIGHT, COMPASS_HALF_HEIGHT, 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(COMPASS_HALF_HEIGHT, -COMPASS_HALF_HEIGHT, 0.0f);
		gl.glEnd();
		gl.glEndList();
		
		try
		{
			_fontTexture = TextureIO.newTexture(new File("./data/textures/font.png"), false);
			_fontTexture.enable();
			_fontTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			_fontTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
			
			_compassTexture = TextureIO.newTexture(new File("./data/textures/compass.png"), false);
			_compassTexture.enable();
			_compassTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			_compassTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public final void render(final GL2 gl)
	{
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, GLDisplay.getInstance().getWidth(), 0, GLDisplay.getInstance().getHeight(), -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glColor4f(1f, 1f, 1f, 1f);
		
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
		gl.glListBase(_listId - 32 + (128 * 0));
		_fontTexture.bind();
		
		ByteBuffer buffer;
		GLText gltext;
		for (int i = _texts.size(); i-- > 0;)
		{
			gltext = _texts.getUnsafe(i);
			buffer = gltext.getBuffer();
			buffer.rewind();
			if (buffer.hasRemaining())
			{
				gl.glPushMatrix();
				gl.glTranslatef(gltext.getX(), gltext.getY(), 0);
				gl.glCallLists(buffer.remaining(), GL2.GL_BYTE, buffer);
				gl.glPopMatrix();
			}
		}
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glListBase(0);
		_compassTexture.bind();
		gl.glPushMatrix();
		gl.glTranslatef(COMPASS_HALF_HEIGHT, GLDisplay.getInstance().getHeight() - COMPASS_HALF_HEIGHT, 0);
		gl.glRotatef(360f - GLDisplay.getInstance().getCamera().getRotY(), 0.0f, 0.0f, 1.0f);
		gl.glCallList(_listId + 256);
		gl.glPopMatrix();
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}
	
	public final void dispose(final GL2 gl)
	{
		gl.glDeleteLists(_listId, 256);
		_fontTexture.destroy(gl);
		_compassTexture.destroy(gl);
	}
	
	public static final class GLText
	{
		private final ByteBuffer _buffer;
		private final int _x;
		private final int _y;
		
		public GLText(final int x, final int y)
		{
			_buffer = BufferUtils.createByteBuffer(256);
			_x = x;
			_y = y;
		}
		
		public final int getX()
		{
			return _x;
		}
		
		public final int getY()
		{
			return _y;
		}
		
		public final void setText(final String text)
		{
			if (text != null && text.length() > _buffer.capacity())
				throw new IllegalArgumentException("Text is too long");
			
			_buffer.clear();
			if (text != null)
				_buffer.put(text.getBytes(UTF_8));
			_buffer.flip();
		}
		
		public final ByteBuffer getBuffer()
		{
			return _buffer;
		}
	}
}