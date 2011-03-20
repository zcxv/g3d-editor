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
import g3deditor.util.FastArrayList;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class AWTInput implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	private final Cursor _cursor;
	private final FastArrayList<MouseEvent> _mouseEvents;
	
	private boolean _mouse1;
	private boolean _mouse2;
	private boolean _mouse3;
	
	private int _mouseX;
	private int _mouseY;
	private int _mouse3DragX;
	private int _mouse3DragY;
	
	private boolean _keyW;
	private boolean _keyA;
	private boolean _keyS;
	private boolean _keyD;
	private boolean _keyQ;
	private boolean _keyE;
	private boolean _keyR;
	private boolean _keyRToggle;
	private boolean _keyT;
	private boolean _keyTToggle;
	private boolean _keyG;
	private boolean _keyGToggle;
	private boolean _keyF;
	private boolean _keyFToggle;
	private boolean _keySpace;
	
	private Robot _robot;
	
	public AWTInput()
	{
		_cursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR), new Point(0, 0), "CCursor");
		_mouseEvents = new FastArrayList<MouseEvent>();
		
		try
		{
			_robot = new Robot();
		}
		catch (final AWTException e)
		{
			e.printStackTrace();
		}
	}
	
	public final FastArrayList<MouseEvent> getMouseEvents()
	{
		return _mouseEvents;
	}
	
	public final boolean getMouseButton1()
	{
		return _mouse1;
	}
	
	public final boolean getMouseButton2()
	{
		return _mouse2;
	}
	
	public final boolean getMouseButton3()
	{
		return _mouse3;
	}
	
	public final boolean getKeyR()
	{
		return _keyR;
	}
	
	public final boolean getKeyRToggle()
	{
		return _keyRToggle;
	}
	
	public final boolean getKeyT()
	{
		return _keyT;
	}
	
	public final boolean getKeyTToggle()
	{
		return _keyTToggle;
	}
	
	public final boolean getKeyG()
	{
		return _keyG;
	}
	
	public final boolean getKeyGToggle()
	{
		return _keyGToggle;
	}
	
	public final boolean getKeyF()
	{
		return _keyF;
	}
	
	public final boolean getKeyFToggle()
	{
		return _keyFToggle;
	}
	
	public final int getMouseX()
	{
		return _mouseX;
	}
	
	public final int getMouseY()
	{
		return _mouseY;
	}
	
	public final void setEnabled(final boolean enabled)
	{
		final GLCanvas canvas = GLDisplay.getInstance().getCanvas();
		if (enabled)
		{
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			canvas.addMouseWheelListener(this);
			canvas.addKeyListener(this);
			resetKeys();
		}
		else
		{
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);
			canvas.removeMouseWheelListener(this);
			canvas.removeKeyListener(this);
			resetKeys();
		}
	}
	
	public final void update(final GL2 gl, final double tpf)
	{
		final GLCamera camera = GLDisplay.getInstance().getCamera();
		
		if (_mouse3)
		{
			final Point location = MouseInfo.getPointerInfo().getLocation();
			final int diffX = _mouse3DragX - location.x;
			final int diffY = _mouse3DragY - location.y;
			if (diffX != 0 || diffY != 0)
			{
				camera.updateCamRotX(-diffY * 0.25f);
				camera.updateCamRotY(-diffX * 0.25f);
				
				if (_robot != null)
					_robot.mouseMove(_mouse3DragX, _mouse3DragY);
			}
		}
		
		final double move = tpf * (_keySpace ? 0.1f : 0.02f);
		if (_keyW && !_keyS)
			camera.moveForeward(-move);
		
		if (_keyS && !_keyW)
			camera.moveForeward(move);
		
		if (_keyA && !_keyD)
			camera.moveSideways(move);
		
		if (_keyD && !_keyA)
			camera.moveSideways(-move);
		
		if (_keyQ && !_keyE)
			camera.moveUp(move);
		
		if (_keyE && !_keyQ)
			camera.moveUp(-move);
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseClicked(final MouseEvent mouseevent)
	{
		if ((mouseevent.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
			_mouseEvents.addLast(mouseevent);
	}
	
	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mousePressed(final MouseEvent mouseevent)
	{
		updateMouseButton(mouseevent, true);
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseReleased(final MouseEvent mouseevent)
	{
		updateMouseButton(mouseevent, false);
	}
	
	private final void updateMouseButton(final MouseEvent mouseevent, final boolean state)
	{
		switch (mouseevent.getButton())
		{
			case MouseEvent.BUTTON1:
				_mouse1 = state;
				break;
				
			case MouseEvent.BUTTON2:
				_mouse2 = state;
				break;
				
			case MouseEvent.BUTTON3:
			{
				GLDisplay.getInstance().getCanvas().setCursor(state ? _cursor : Cursor.getDefaultCursor());
				_mouse3 = state;
				_mouse3DragX = mouseevent.getXOnScreen();
				_mouse3DragY = mouseevent.getYOnScreen();
				break;
			}
		}
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseEntered(final MouseEvent mouseevent)
	{
		
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseExited(final MouseEvent mouseevent)
	{
		
	}
	
	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseDragged(final MouseEvent e)
	{
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
			_mouseEvents.addLast(e);
		
		mouseMoved(e);
	}
	
	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseMoved(final MouseEvent e)
	{
		_mouseX = e.getX();
		_mouseY = e.getY();
	}
	
	/**
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public final void mouseWheelMoved(final MouseWheelEvent e)
	{
		_mouseEvents.addLast(e);
	}
	
	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public final void keyTyped(final KeyEvent keyevent)
	{
		
	}
	
	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public final void keyPressed(final KeyEvent keyevent)
	{
		updateKey(keyevent.getKeyCode(), true);
	}
	
	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public final void keyReleased(final KeyEvent keyevent)
	{
		updateKey(keyevent.getKeyCode(), false);
	}
	
	private final void updateKey(final int key, final boolean state)
	{
		switch (key)
		{
			case KeyEvent.VK_W:
				_keyW = state;
				break;
				
			case KeyEvent.VK_A:
				_keyA = state;
				break;
				
			case KeyEvent.VK_S:
				_keyS = state;
				break;
				
			case KeyEvent.VK_D:
				_keyD = state;
				break;
				
			case KeyEvent.VK_Q:
				_keyQ = state;
				break;
				
			case KeyEvent.VK_E:
				_keyE = state;
				break;
				
			case KeyEvent.VK_R:
				_keyR = state;
				if (state)
					_keyRToggle = !_keyRToggle;
				break;
				
			case KeyEvent.VK_T:
				_keyT = state;
				if (state)
					_keyTToggle = !_keyTToggle;
				break;
				
			case KeyEvent.VK_G:
				_keyG = state;
				if (state)
					_keyGToggle = !_keyGToggle;
				break;
				
			case KeyEvent.VK_F:
				_keyF = state;
				if (state)
					_keyFToggle = !_keyFToggle;
				break;
				
			case KeyEvent.VK_SPACE:
				_keySpace = state;
				break;
		}
	}
	
	private final void resetKeys()
	{
		_keyW = false;
		_keyA = false;
		_keyS = false;
		_keyD = false;
		_keyQ = false;
		_keyE = false;
		_keyR = false;
		_keyRToggle = false;
		_keyT = false;
		_keyTToggle = Config.TERRAIN_DEFAULT_ON;
		_keyG = false;
		_keyGToggle = true;
		_keyF =  false;
		_keyFToggle = false;
		_keySpace = false;
	}
}