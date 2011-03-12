package g3deditor.swing;

import g3deditor.util.Util;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

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
import javax.swing.JWindow;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class Splash extends JWindow implements Runnable
{
	public static interface CheckedRunnable
	{
		public boolean run();
	}
	
	private final long _time;
	private final Runnable _r1;
	private final CheckedRunnable _r2;
	private final Runnable _r3;
	
	private BufferedImage _image;
	private BufferedImage _background;
	private BufferedImage _buffer;
	private AlphaComposite _composite;
	
	private float _alpha;
	
	public Splash(final long time, final Runnable r1, final CheckedRunnable r2, final Runnable r3)
	{
		setAlwaysOnTop(true);
		
		_time = System.currentTimeMillis() + time;
		_r1 = r1;
		_r2 = r2;
		_r3 = r3;
		
		_image = Util.loadImage("./data/icon/splash.png");
		
		if (_image != null)
		{
			setSize(_image.getWidth(), _image.getHeight());
			setLocationRelativeTo(null);
			
			try
			{
				_background = new Robot().createScreenCapture(new Rectangle(getX(), getY(), _image.getWidth(), _image.getHeight()));
				_buffer = new BufferedImage(_image.getWidth(), _image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F);
			}
			catch (final AWTException e)
			{
				
			}
			
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
		if (_background != null)
		{
			final Graphics2D g = (Graphics2D) _buffer.getGraphics();
			g.setComposite(_composite);
			g.drawImage(_background, 0, 0, null);
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
				_alpha += 0.025F;
				if (_alpha > 1.0F)
					_alpha = 1.0F;
				
				repaint();
				Thread.sleep(25);
			}
			
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
			
			final long sleep = _time - System.currentTimeMillis();
			if (sleep > 0)
				Thread.sleep(sleep);
			
			_r3.run();
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
		}
	}
	
	@Override
	public final void paint(final Graphics g)
	{
		drawImage(_alpha);
	}
}