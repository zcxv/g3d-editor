package g3deditor.jogl;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

public final class AWTInput implements MouseListener, KeyListener
{
	private final GLDisplay _display;
	private final Cursor _cursor;
	
	private boolean _mouse1;
	private boolean _mouse2;
	private boolean _mouse3;
	private int _mouse3DragX;
	private int _mouse3DragY;
	
	private boolean _keyW;
	private boolean _keyA;
	private boolean _keyS;
	private boolean _keyD;
	private boolean _keyQ;
	private boolean _keyE;
	private boolean _keySpace;
	
	private Robot _robot;
	
	public AWTInput(final GLDisplay display)
	{
		_display = display;
		_cursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR), new Point(0, 0), "CCursor");
		
		try
		{
			_robot = new Robot();
		}
		catch (final AWTException e)
		{
			e.printStackTrace();
		}
	}
	
	public final GLDisplay getDisplay()
	{
		return _display;
	}
	
	public final void setEnabled(final boolean enabled)
	{
		final GLCanvas canvas = _display.getCanvas();
		if (enabled)
		{
			canvas.addMouseListener(this);
			canvas.addKeyListener(this);
			resetKeys();
		}
		else
		{
			canvas.removeMouseListener(this);
			canvas.removeKeyListener(this);
			resetKeys();
		}
	}
	
	public final void update(final GL2 gl, final double tpf)
	{
		final GLCamera camera = _display.getCamera();
		
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
				_display.getCanvas().setCursor(state ? _cursor : Cursor.getDefaultCursor());
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
	}
}