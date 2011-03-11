package g3deditor.swing;

import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.util.Util;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jogamp.opengl.util.FPSAnimator;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * 
 * @author Forsaiken
 *
 */
@SuppressWarnings("serial")
public final class Splash extends JWindow implements Runnable
{
	public static interface CheckedRunnable
	{
		public boolean run();
	}
	
	private final int _time;
	private final Runnable _r1;
	private final CheckedRunnable _r2;
	private final Runnable _r3;
	
	private Robot _robot;
	private BufferedImage _image;
	private BufferedImage _buffer;
	private AlphaComposite _composite;
	private float _alpha;
	
	public Splash(final int time, final Runnable r1, final CheckedRunnable r2, final Runnable r3)
	{
		_time = time;
		_r1 = r1;
		_r2 = r2;
		_r3 = r3;
		
		_image = Util.loadImage("./data/icon/splash.png");
		
		if (_image != null)
		{
			try
			{
				_robot = new Robot();
			}
			catch (final AWTException e)
			{
				
			}
			
			if (_robot != null)
			{
				_buffer = new BufferedImage(_image.getWidth(), _image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F);
			}
			
			setSize(_image.getWidth(), _image.getHeight());
			setLocationRelativeTo(null);
			setVisible(true);
			
			new Thread(this).start();
			
			_r1.run();
		}
		else
		{
			_r1.run();
			while (!_r2.run())
			{
				try
				{
					Thread.sleep(1l);
				}
				catch (final InterruptedException e)
				{
					
				}
			}
			_r3.run();
		}
	}
	
	private final void drawImage(final float alpha)
	{
		if (_robot != null)
		{
			final BufferedImage background = _robot.createScreenCapture(new Rectangle(getX(), getY(), _image.getWidth(), _image.getHeight()));
			final Graphics2D g = (Graphics2D) _buffer.getGraphics();
			
			g.clearRect(0, 0, _image.getWidth(), _image.getHeight());
			g.setComposite(_composite);
			g.drawImage(background, 0, 0, null);
			g.setComposite(_composite.derive(alpha));
			g.drawImage(_image, 0, 0, null);
	        
			getGraphics().drawImage(_buffer, 0, 0, null);
		}
		else
		{
			getGraphics().drawImage(_image, 0, 0, null);
		}
	}
	
	@Override
	public final void run()
	{
		try
		{
			while (_alpha < 1.0F)
			{
				_alpha += 0.05F;
				if (_alpha > 1.0F)
					_alpha = 1.0F;
				
				repaint();
				Thread.sleep(_time / 100);
			}
			
			Thread.sleep(_time / 2);
			
			while (!_r2.run())
			{
				try
				{
					Thread.sleep(1l);
				}
				catch (final InterruptedException e)
				{
					
				}
			}
		}
		catch (final Throwable t)
		{
			t.printStackTrace();
			System.exit(0);
		}
		finally
		{
			setVisible(false);
			
			try
			{
				if (_buffer != null)
					_buffer.getGraphics().dispose();
			}
			catch (final Throwable e)
			{
				
			}
			
			try
			{
				dispose();
			}
			catch (final Throwable e)
			{
				
			}
			
			_r3.run();
		}
	}
	
	@Override
	public final void paint(final Graphics g)
	{
		drawImage(_alpha);
	}
}