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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLTextRenderer
{
	private final GLDisplay _display;
	private final ByteBuffer _stringBuffer;
	private final FastArrayList<GLText> _texts;
	
	private int _listId;
	
	public GLTextRenderer(final GLDisplay display)
	{
		_display = display;
		_stringBuffer = BufferUtils.createByteBuffer(256);
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
		_listId = gl.glGenLists(256); // Creating 256 Display Lists
		for (int loop = 0; loop < 256; loop++) // Loop Through All 256 Lists
		{
			final float u1 = (loop % FONT_TEX_ROWS_COLS) * FONT_TEX_BLOCK;
			final float u2 = u1 + FONT_TEX_BLOCK;
			final float v1 = (loop / FONT_TEX_ROWS_COLS) * FONT_TEX_BLOCK;
			final float v2 = v1 + FONT_TEX_BLOCK;
			
			gl.glNewList(_listId + loop, GL2.GL_COMPILE);
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			
			gl.glTexCoord2f(u1, v1);
			gl.glVertex3f(0.0f, 16.0f, 0.0f);
			
			gl.glTexCoord2f(u1, v2);
			gl.glVertex3f(0.0f, 0.0f, 0.0f);
			
			gl.glTexCoord2f(u2, v1);
			gl.glVertex3f(16.0f, 16.0f, 0.0f);
			
			gl.glTexCoord2f(u2, v2);
			gl.glVertex3f(16.0f, 0.0f, 0.0f);
			
			gl.glEnd();
			gl.glTranslatef(9f, 0, 0);
			gl.glEndList();
		}
	}
	
	public final void render(final GL2 gl)
	{
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, _display.getWidth(), 0, _display.getHeight(), -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glListBase(_listId - 32 + (128 * 0));
		gl.glColor4f(1f, 1f, 1f, 1f);
		
		GLText gltext;
		String text;
		for (int i = _texts.size(); i-- > 0;)
		{
			gltext = _texts.getUnsafe(i);
			text = gltext.getText();
			if (text == null)
				continue;
			
			_stringBuffer.clear();
			_stringBuffer.put(text.getBytes(Charset.forName("UTF-8")));
			_stringBuffer.flip();
			
			gl.glPushMatrix();
			gl.glTranslatef(gltext.getX(), gltext.getY(), 0);
			gl.glCallLists(text.length(), GL2.GL_BYTE, _stringBuffer);
			gl.glPopMatrix();
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}
	
	public final void dispose(final GL2 gl)
	{
		gl.glDeleteLists(_listId, 256);
	}
	
	public static final class GLText
	{
		private final int _x;
		private final int _y;
		
		private String _text;
		
		public GLText(final int x, final int y)
		{
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
			_text = text;
		}
		
		public final String getText()
		{
			return _text;
		}
	}
}