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

import g3deditor.Config;
import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoBlockSelector.GeoBlockEntry;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLGUIRenderer.GLText;
import g3deditor.util.FastArrayList;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLDisplay implements GLEventListener
{
	private static final long NANOS_IN_MILLISECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS);
	private static final long NANOS_IN_SECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);
	
	private static GLDisplay _instance;
	
	public static final void init(final GLCanvas canvas)
	{
		_instance = new GLDisplay(canvas);
	}
	
	public static final GLDisplay getInstance()
	{
		return _instance;
	}
	
	private static final double nanosToFps(final double nanos)
	{
		return NANOS_IN_SECOND / nanos;
	}
	
	private static final double nanosToTpf(final double nanos)
	{
		return nanos / NANOS_IN_MILLISECOND;
	}
	
	private static final double roundFps(final double fps)
	{
		return ((int) (fps * 100f)) / 100d;
	}
	
	private static final float VIEW_ANGLE = 45f;
	private static final float VIEW_Z_NEAR = 1f;
	private static final float VIEW_Z_FAR = 2000f;
	
	private final GLCanvas _canvas;
	private final GLGUIRenderer _guiRenderer;
	private final GLCellRenderSelector _renderSelector;
	private final GLSelectionBox _selectionBox;
	private final GLCamera _camera;
	private final GLTerrain _terrain;
	private final AWTInput _input;
	private final GLText _fpsText;
	private final GLText _callsText;
	private final GLText _memoryText;
	private final GLText _renderInfoText;
	private final GLText _glInfoText;
	private final GLText _worldPositionText;
	private final GLText _geoPositionText;
	
	private GLCellRenderer _renderer;
	private boolean _vsync;
	private GeoCell _prevPick;
	
	private GLU _glu;
	private int _width;
	private int _height;
	private long _time;
	private long _timeFPS;
	private int _loopsFPS;
	private int _elementsFPS;
	
	private GLDisplay(final GLCanvas canvas)
	{
		_canvas = canvas;
		_guiRenderer = new GLGUIRenderer();
		_renderSelector = new GLCellRenderSelector();
		_selectionBox = new GLSelectionBox();
		_camera = new GLCamera();
		_terrain = new GLTerrain();
		_input = new AWTInput();
		
		_callsText = _guiRenderer.newText(10, 10);
		_fpsText = _guiRenderer.newText(10, _callsText.getY() + GLGUIRenderer.TEXT_HEIGHT);
		_memoryText = _guiRenderer.newText(10, _fpsText.getY() + GLGUIRenderer.TEXT_HEIGHT);
		_renderInfoText = _guiRenderer.newText(10, _memoryText.getY() + GLGUIRenderer.TEXT_HEIGHT);
		_glInfoText = _guiRenderer.newText(10, _renderInfoText.getY() + GLGUIRenderer.TEXT_HEIGHT);
		_geoPositionText = _guiRenderer.newText(10, _glInfoText.getY() + GLGUIRenderer.TEXT_HEIGHT);
		_worldPositionText = _guiRenderer.newText(10, _geoPositionText.getY() + GLGUIRenderer.TEXT_HEIGHT);
		_vsync = true;
	}
	
	public final GLCanvas getCanvas()
	{
		return _canvas;
	}
	
	public final GLCellRenderer getRenderer()
	{
		return _renderer;
	}
	
	public final GLCellRenderSelector getRenderSelector()
	{
		return _renderSelector;
	}
	
	public final GLSelectionBox getSelectionBox()
	{
		return _selectionBox;
	}
	
	public final GLCamera getCamera()
	{
		return _camera;
	}
	
	public final AWTInput getInput()
	{
		return _input;
	}
	
	public final GLTerrain getTerrain()
	{
		return _terrain;
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
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepthf(1.0f);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		_glu.gluPerspective(VIEW_ANGLE, 1.0f, VIEW_Z_NEAR, VIEW_Z_FAR);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		_guiRenderer.init(gl);
		_renderSelector.init();
		_terrain.init(gl);
		_input.setEnabled(true);
		
		_time = System.nanoTime();
		_timeFPS = 0L;
		_loopsFPS = 0;
		
		_glInfoText.setText("GLProfile: " + glautodrawable.getGLProfile().getName());
	}
	
	/**
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public final void dispose(final GLAutoDrawable glautodrawable)
	{
		final GL2 gl = glautodrawable.getGL().getGL2();
		if (_renderer != null)
			_renderer.dispose(gl);
		_guiRenderer.dispose(gl);
		_renderSelector.dispose();
		_terrain.dispose(gl);
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
			_fpsText.setText("Fps:   " + roundFps(fps));
			_callsText.setText("Calls: " + elements);
		}
		
		_worldPositionText.setText("World-Pos XYZ: " + _camera.getWorldX() + ", " + _camera.getWorldY() + ", " + _camera.getWorldZ());
		_geoPositionText.setText("Geo-Pos XYZ: " + _camera.getGeoX() + ", " + _camera.getGeoY() + ", " + _camera.getGeoZ());
		_memoryText.setText("Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024l / 1024l + "mb");
		
		final GL2 gl = glautodrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		if (_vsync != Config.V_SYNC)
		{
			_vsync = Config.V_SYNC;
			gl.setSwapInterval(_vsync ? GL.GL_ONE : GL.GL_ZERO);
		}
		
		_input.update(gl, tpf);
		_camera.checkPositionOrRotationChanged();
		
		gl.glRotatef(360f - _camera.getRotX(), 1.0f, 0.0f, 0.0f);
		gl.glRotatef(360f - _camera.getRotY(), 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(-_camera.getX(), -_camera.getY(), -_camera.getZ());
		
		_terrain.setRegion(GeoEngine.getInstance().getActiveRegion());
		_terrain.setEnabled(_input.getKeyTToggle());
		_terrain.setWireframe(_input.getKeyRToggle());
		_terrain.render(gl);
		
		_renderSelector.select(gl, _camera, _input.getKeyFToggle());
		
		if (_renderer == null)
		{
			_renderer = GLCellRenderer.getRenderer(Config.CELL_RENDERER);
			_renderInfoText.setText("Renderer: " + _renderer);
		}
		else if (!_renderer.getName().equals(Config.CELL_RENDERER))
		{
			final GLCellRenderer newRenderer = GLCellRenderer.getRenderer(Config.CELL_RENDERER);
			if (!newRenderer.getName().equals(_renderer.getName()))
			{
				_renderer.dispose(gl);
				_renderer = newRenderer;
				_renderInfoText.setText("Renderer: " + _renderer);
			}
			else
			{
				Config.CELL_RENDERER = _renderer.getName();
			}
		}
		
		if (_input.getKeyGToggle())
		{
			_renderer.init(gl);
			_renderer.enableRender(gl);
			
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
		}
		
		final FastArrayList<MouseEvent> mouseEvents = _input.getMouseEvents();
		for (int i = 0, j; i < mouseEvents.size(); i++)
		{
			final MouseEvent event = mouseEvents.getUnsafe(i);
			if (event instanceof MouseWheelEvent)
			{
				final MouseWheelEvent scrollevent = (MouseWheelEvent) event;
				if (event.isControlDown())
				{
					final short addHeight = (short) (scrollevent.getWheelRotation() * -8);
					final GeoBlockSelector selector = GeoBlockSelector.getInstance();
					FastArrayList<GeoCell> cells;
					for (GeoBlockEntry entry = selector.getHead(), tail = selector.getTail(); (entry = entry.getNext()) != tail;)
					{
						cells = entry.getValue();
						for (j = cells.size(); j-- > 0;)
						{
							cells.getUnsafe(j).addHeight(addHeight);
						}
					}
					_renderSelector.forceUpdateFrustum();
				}
				else
				{
					_selectionBox.addHeight(scrollevent.getWheelRotation() * -8);
				}
			}
			else
			{
				final float[] point = _camera.pick(gl, _glu, event.getX(), event.getY());
				if (point != null)
				{
					final GeoCell cell = GeoEngine.getInstance().nGetCell((int) point[0], (int) point[2], (int) (point[1] * 16f));
					// check height difference from cell to picked point to eliminate ground/terrain picking
					if (cell != null && Math.abs(cell.getHeight() - (int) (point[1] * 16f)) <= 4)
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
		
		final float[] point = _camera.pick(gl, _glu, _input.getMouseX(), _input.getMouseY());
		if (point != null && GeoEngine.getInstance().getActiveRegion() != null)
		{
			final GeoCell cell = GeoEngine.getInstance().nGetCell((int) point[0], (int) point[2], (int) (point[1] * 16f));
			// check height difference from cell to picked point to eliminate ground/terrain picking
			if (cell != null && Math.abs(cell.getHeight() - (int) (point[1] * 16f)) <= 4)
			{
				_selectionBox.render(gl, cell);
			}
			else
			{
				_selectionBox.render(gl, null);
			}
		}
		
		_guiRenderer.render(gl);
		
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