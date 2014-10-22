package g3deditor.swing;

import g3deditor.util.Util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

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
				try
				{
					setBackground(new Color(0, 0, 0, 0));
				}
				catch (final Exception e)
				{
					_background = new Robot().createScreenCapture(new Rectangle(getX(), getY(), _image.getWidth(), _image.getHeight()));
					_buffer = new BufferedImage(_image.getWidth(), _image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				}
				
				_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F);
			}
			catch (final Exception e)
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
		Graphics2D g;
		
		if (_background != null)
		{
			g = (Graphics2D) _buffer.getGraphics();
			g.setComposite(_composite);
			g.drawImage(_background, 0, 0, null);
			g.setComposite(_composite.derive(alpha));
			g.drawImage(_image, 0, 0, null);
		}
		
		g = (Graphics2D) getGraphics();
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setComposite(_composite.derive(alpha));
		g.drawImage(_image, 0, 0, null);
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