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

import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.renderer.DLRenderer;
import g3deditor.jogl.renderer.IRenderer;
import g3deditor.jogl.renderer.VBORenderer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLDisplay implements GLEventListener
{
	private static final long NANOS_IN_MILLISECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS);
	private static final long NANOS_IN_SECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);
	
	private static final double nanosToFps(final double nanos)
	{
		return NANOS_IN_SECOND / nanos;
	}
	
	private static final double nanosToTpf(final double nanos)
	{
		return nanos / NANOS_IN_MILLISECOND;
	}
	
	private static final float VIEW_ANGLE = 45f;
	private static final float VIEW_Z_NEAR = 1f;
	private static final float VIEW_Z_FAR = 1000f;
	
	private final GLCanvas _canvas;
	private final GLRenderer _renderer;
	private final GLRenderSelector _renderSelector;
	private final GLCamera _camera;
	private final AWTInput _input;
	
	private GLU _glu;
	private int _width;
	private int _height;
	private long _time;
	private long _time10;
	private int _elements;
	private Texture _nsweTexture;
	
	public GLDisplay(final GLCanvas canvas)
	{
		_canvas = canvas;
		
		final int renderer = 2;
		switch (renderer)
		{
			// Old G3D 11 fps - 18092
			case 0: // 15 fps - 18171 - 136%(OLD)
				_renderer = new IRenderer(this);
				break;
				
			case 1: // 28 fps - 18171 - 254%(OLD) - 186% (I)
				_renderer = new DLRenderer(this);
				break;
				
			default: // 40 fps - 18171 - 363%(OLD) - 124%(DL) - 266%(I)
				_renderer = new VBORenderer(this);
				break;
		}
		
		_renderSelector = new GLRenderSelector(this);
		_camera = new GLCamera(this);
		_input = new AWTInput(this);
	}
	
	public final GLCanvas getCanvas()
	{
		return _canvas;
	}
	
	public final GLRenderer getRenderer()
	{
		return _renderer;
	}
	
	public final GLRenderSelector getRenderSelector()
	{
		return _renderSelector;
	}
	
	public final GLCamera getCamera()
	{
		return _camera;
	}
	
	public final AWTInput getInput()
	{
		return _input;
	}
	
	public final int getWidth()
	{
		return _width;
	}
	
	public final int getHeight()
	{
		return _height;
	}
	
	/**
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public final void init(final GLAutoDrawable glautodrawable)
	{
		final GL2 gl = glautodrawable.getGL().getGL2();
		_glu = GLU.createGLU(gl);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_DITHER);
		gl.glDisable(GL.GL_STENCIL_TEST);
		gl.glDisable(GL2.GL_POLYGON_STIPPLE);
		gl.glDisable(GL2.GL_ALPHA_TEST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		//gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		//gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		_glu.gluPerspective(VIEW_ANGLE, 1.0f, VIEW_Z_NEAR, VIEW_Z_FAR);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		int camX = GeoEngine.getGeoXY(10, 50);
		int camY = GeoEngine.getGeoXY(12, 50);
		
		_camera.setXYZ(camX, GeoEngine.getInstance().nGetCell(camX, camY, 0).getHeight() / 16f, camY);
		_camera.onProjectionMatrixChanged();
		//_camera.checkPositionOrRotationChanged();
		_renderer.init(gl);
		_renderSelector.init();
		_input.setEnabled(true);
		
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
		
		_time = System.nanoTime();
		_time10 = _time;
	}
	
	/**
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public final void dispose(final GLAutoDrawable glautodrawable)
	{
		final GL2 gl = glautodrawable.getGL().getGL2();
		_renderer.dispose(gl);
		_nsweTexture.destroy(gl);
		_renderSelector.dispose();
	}
	
	private int _fpsCounter;
	
	/**
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public final void display(final GLAutoDrawable glautodrawable)
	{
		final long currentTime = System.nanoTime();
		final double fps = nanosToFps(currentTime - _time);
		final double tpf = nanosToTpf(currentTime - _time);
		_time = currentTime;
		
		if (++_fpsCounter == 10)
		{
			final double fps10 = nanosToFps((currentTime - _time10) / 10);
			_time10 = currentTime;
			_fpsCounter = 0;
			System.out.println("FPS: " + fps + ", FPS10: " + fps10 + ", ELEMENTS: " + _elements + ", RAM: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024l / 1024l);
		}
		
		final GL2 gl = glautodrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		_input.update(gl, tpf);
		_camera.checkPositionOrRotationChanged();
		
		gl.glRotatef(360f - _camera.getRotX(), 1.0f, 0.0f, 0.0f);
		gl.glRotatef(360f - _camera.getRotY(), 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(-_camera.getX(), -_camera.getY(), -_camera.getZ());
		
		_renderSelector.select(gl, _camera);
		_renderer.enableRender(gl);
		_nsweTexture.bind();
		
		_elements = 0;
		GLSubRenderSelector selector;
		for (int i = _renderSelector.getElementsToRender(), y; i-- > 0;)
		{
			selector = _renderSelector.getElementToRender(i);
			for (y = selector.getElementsToRender(); y-- > 0;)
			{
				_elements++;
				_renderer.render(gl, selector.getElementToRender(y));
			}
		}
		
		_renderer.disableRender(gl);
		gl.glFlush();
	}
	
	/**
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public final void reshape(final GLAutoDrawable glautodrawable, final int x, final int y, final int width, final int height)
	{
		final GL2 gl = glautodrawable.getGL().getGL2();
		gl.glViewport (0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		_glu.gluPerspective(VIEW_ANGLE, (float) width / (float) height, VIEW_Z_NEAR, VIEW_Z_FAR);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		_width = width;
		_height = height;
		
		_camera.onProjectionMatrixChanged();
		_camera.onViewportChanged();
	}
}