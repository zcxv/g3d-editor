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

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLState
{
	private static GLColor _color;
	private static boolean _lockColor;
	private static boolean _vsyncEnabled;
	private static boolean _blendEnabled;
	private static int _blendFunc1;
	private static int _blendFunc2;
	private static boolean _depthTestEnabled;
	private static float _translateX;
	private static float _translateY;
	private static float _translateZ;
	
	public static final void init(final GL2 gl)
	{
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glDisable(GL2.GL_DITHER);
		gl.glDisable(GL2.GL_STENCIL_TEST);
		gl.glDisable(GL2.GL_POLYGON_STIPPLE);
		gl.glDisable(GL2.GL_ALPHA_TEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepthf(1.0f);
		gl.setSwapInterval(GL2.GL_ONE);
		
		_vsyncEnabled = true;
		_blendEnabled = true;
		_blendFunc1 = GL2.GL_SRC_ALPHA;
		_blendFunc2 = GL2.GL_ONE_MINUS_SRC_ALPHA;
		_depthTestEnabled = true;
	}
	
	public static final void glColor4f(final GL2 gl, final GLColor color)
	{
		if (!_lockColor && _color != color)
		{
			_color = color;
			if (color.getA() != 1f)
				gl.glColor4f(color.getR(), color.getG(), color.getB(), color.getA());
			else
				gl.glColor3f(color.getR(), color.getG(), color.getB());
		}
	}
	
	public static final void lockColor(final boolean locked)
	{
		_lockColor = locked;
	}
	
	public static final void setVSyncEnabled(final GL2 gl, final boolean enabled)
	{
		if (_vsyncEnabled != enabled)
		{
			_vsyncEnabled = enabled;
			gl.setSwapInterval(enabled ? GL2.GL_ONE : GL2.GL_ZERO);
		}
	}
	
	public static final void setBlendEnabled(final GL2 gl, final boolean enabled)
	{
		if (_blendEnabled != enabled)
		{
			_blendEnabled = enabled;
			if (enabled)
				gl.glEnable(GL2.GL_BLEND);
			else
				gl.glDisable(GL2.GL_BLEND);
		}
	}
	
	public static final void setBlendFunc(final GL2 gl, final int func1, final int func2)
	{
		if (_blendFunc1 != func1 || _blendFunc2 != func2)
		{
			_blendFunc1 = func1;
			_blendFunc2 = func2;
			gl.glBlendFunc(func1, func2);
		}
	}
	
	public static final void setDepthTestEnabled(final GL2 gl, final boolean enabled)
	{
		if (_depthTestEnabled != enabled)
		{
			_depthTestEnabled = enabled;
			if (enabled)
				gl.glEnable(GL2.GL_DEPTH_TEST);
			else
				gl.glDisable(GL2.GL_DEPTH_TEST);
		}
	}
	
	public static final void resetTranslate()
	{
		_translateX = 0f;
		_translateY = 0f;
		_translateZ = 0f;
	}
	
	public static final void translatef(final GL2 gl, final float x, final float y, final float z)
	{
		if (_translateX != x || _translateY != y || _translateZ != z)
		{
			gl.glTranslatef(x - _translateX, y - _translateY, z - _translateZ);
			_translateX = x;
			_translateY = y;
			_translateZ = z;
		}
	}
}