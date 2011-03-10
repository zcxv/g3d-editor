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
import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoBlockSelector.ForEachGeoCellProcedure;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLTextRenderer.GLText;
import g3deditor.jogl.renderer.DLRenderer;
import g3deditor.jogl.renderer.IRenderer;
import g3deditor.jogl.renderer.VBORenderer;
import g3deditor.util.FastArrayList;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
	private final GLTextRenderer _textRenderer;
	private final GLRenderSelector _renderSelector;
	private final GLCamera _camera;
	private final AWTInput _input;
	private final GLText _fpsText;
	private final GLText _callsText;
	private final GLText _memoryText;
	private final GLText _renderInfoText;
	private final GLText _glInfoText;
	private final GLText _worldPositionText;
	private final GLText _geoPositionText;
	
	private GeoCell _prevPick;
	
	private GLU _glu;
	private int _width;
	private int _height;
	private long _time;
	private long _timeFPS;
	private int _loopsFPS;
	private int _elementsFPS;
	
	private Texture _nsweTexture;
	private Texture _fontTexture;
	
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
		
		_textRenderer = new GLTextRenderer(this);
		_renderSelector = new GLRenderSelector(this);
		_camera = new GLCamera(this);
		_input = new AWTInput(this);
		
		_callsText = _textRenderer.newText(10, 10);
		_fpsText = _textRenderer.newText(10, _callsText.getY() + GLTextRenderer.TEXT_HEIGHT);
		_memoryText = _textRenderer.newText(10, _fpsText.getY() + GLTextRenderer.TEXT_HEIGHT);
		_renderInfoText = _textRenderer.newText(10, _memoryText.getY() + GLTextRenderer.TEXT_HEIGHT);
		_glInfoText = _textRenderer.newText(10, _renderInfoText.getY() + GLTextRenderer.TEXT_HEIGHT);
		_geoPositionText = _textRenderer.newText(10, _glInfoText.getY() + GLTextRenderer.TEXT_HEIGHT);
		_worldPositionText = _textRenderer.newText(10, _geoPositionText.getY() + GLTextRenderer.TEXT_HEIGHT);
		
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
		gl.glClearDepthf(1.0f);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		_glu.gluPerspective(VIEW_ANGLE, 1.0f, VIEW_Z_NEAR, VIEW_Z_FAR);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		int camX = GeoEngine.getGeoXY(13, 50);
		int camY = GeoEngine.getGeoXY(8, 50);
		
		_camera.setXYZ(camX, GeoEngine.getInstance().nGetCell(camX, camY, 0).getHeight() / 16f, camY);
		_camera.onProjectionMatrixChanged();
		//_camera.checkPositionOrRotationChanged();
		_renderer.init(gl);
		_textRenderer.init(gl);
		_renderSelector.init();
		_input.setEnabled(true);
		
		try
		{
			_nsweTexture = TextureIO.newTexture(new File("./data/textures/nswe.png"), true);
			_nsweTexture.enable();
			_nsweTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
			_nsweTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
			
			_fontTexture = TextureIO.newTexture(new File("./data/textures/font.png"), false);
			_fontTexture.enable();
			_fontTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			_fontTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		
		_time = System.nanoTime();
		_timeFPS = 0L;
		_loopsFPS = 0;
		
		_renderInfoText.setText("Renderer: " + _renderer);
		_glInfoText.setText("GLProfile: " + glautodrawable.getGLProfile().getName());
	}
	
	/**
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public final void dispose(final GLAutoDrawable glautodrawable)
	{
		final GL2 gl = glautodrawable.getGL().getGL2();
		_renderer.dispose(gl);
		_textRenderer.dispose(gl);
		_nsweTexture.destroy(gl);
		_fontTexture.destroy(gl);
		_renderSelector.dispose();
	}
	
	
	private static final double getFPS(final double fps)
	{
		return ((int) (fps * 100f)) / 100d;
	}
	
	/**
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public final void display(final GLAutoDrawable glautodrawable)
	{
		final long currentTime = System.nanoTime();
		final double tpf = nanosToTpf(currentTime - _time);
		_timeFPS += currentTime - _time;
		_loopsFPS++;
		_time = currentTime;
		
		if (TimeUnit.SECONDS.convert(_timeFPS, TimeUnit.NANOSECONDS) >= 1)
		{
			final double fps = _loopsFPS == 0 ? Double.POSITIVE_INFINITY : nanosToFps(_timeFPS / _loopsFPS);
			final int elements = _elementsFPS / (_loopsFPS == 0 ? 1 : _loopsFPS);
			_timeFPS = 0L;
			_loopsFPS = 0;
			_elementsFPS = 0;
			_fpsText.setText("Fps:   " + getFPS(fps));
			_callsText.setText("Calls: " + elements);
		}
		
		_worldPositionText.setText("World-Pos XYZ: " + _camera.getWorldX() + ", " + _camera.getWorldY() + ", " + _camera.getWorldZ());
		_geoPositionText.setText("Geo-Pos XYZ: " + _camera.getGeoX() + ", " + _camera.getGeoY() + ", " + _camera.getGeoZ());
		_memoryText.setText("Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024l / 1024l + "mb");
		
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
		
		GLSubRenderSelector selector;
		for (int i = _renderSelector.getElementsToRender(), y; i-- > 0;)
		{
			selector = _renderSelector.getElementToRender(i);
			for (y = selector.getElementsToRender(); y-- > 0;)
			{
				_elementsFPS++;
				_renderer.render(gl, selector.getElementToRender(y));
			}
		}
		
		_renderer.disableRender(gl);
		
		final FastArrayList<MouseEvent> mouseEvents = _input.getMouseEvents();
		for (int i = 0; i < mouseEvents.size(); i++)
		{
			final MouseEvent event = mouseEvents.getUnsafe(i);
			if (event instanceof MouseWheelEvent)
			{
				if (event.isControlDown())
				{
					final MouseWheelEvent scrollevent = (MouseWheelEvent) event;
					final short addHeight = (short) (scrollevent.getWheelRotation() * -8);
					GeoBlockSelector.getInstance().forEachGeoCell(new ForEachGeoCellProcedure()
					{
						@Override
						public final boolean execute(final GeoCell cell)
						{
							cell.addHeight(addHeight);
							return true;
						}
					});
					_renderSelector.forceUpdateFrustum();
				}
			}
			else
			{
				final float[] point = _camera.pick(gl, _glu, event.getX(), event.getY());
				if (point != null)
				{
					final GeoCell cell = GeoEngine.getInstance().nGetCell((int) point[0], (int) point[2], (int) (point[1] * 16f));
					if (cell != null)
					{
						if (event.getID() == MouseEvent.MOUSE_DRAGGED)
						{
							if (_prevPick == cell)
								continue;
							
							final GeoBlock prevBlock = _prevPick != null ? _prevPick.getBlock() : null;
							_prevPick = cell;
							
							if (event.isAltDown() && prevBlock == cell.getBlock())
								continue;
						}
						
						GeoBlockSelector.getInstance().selectGeoCell(cell, event.isAltDown(), event.isShiftDown());
					}
				}
			}
		}
		mouseEvents.clear();
		
		_fontTexture.bind();
		_textRenderer.render(gl);
		
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