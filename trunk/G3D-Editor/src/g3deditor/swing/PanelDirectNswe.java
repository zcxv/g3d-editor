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
package g3deditor.swing;

import g3deditor.Config;
import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoBlockSelector.GeoBlockEntry;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.util.FastArrayList;
import g3deditor.util.Util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class PanelDirectNswe extends JPanel implements MouseListener
{
	private static final int IMAGE_WIDTH = 128;
	private static final int IMAGE_HEIGHT = 128;
	
	public static final Tri TRI_NORTH = new Tri(0d, 0d, 0.5d, 0.5d, 1d, 0d);
	public static final Tri TRI_SOUTH = new Tri(0d, 1d, 0.5d, 0.5d, 1d, 1d);
	public static final Tri TRI_WEST = new Tri(0d, 0d, 0.5d, 0.5d, 0d, 1d);
	public static final Tri TRI_EAST = new Tri(1d, 0d, 0.5d, 0.5d, 1d, 1d);
	
	private static final class Tri
	{
		private final Vec2 _v1;
		private final Vec2 _v2;
		private final Vec2 _v3;
		
		public Tri(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3)
		{
			this(new Vec2(x1, y1), new Vec2(x2, y2), new Vec2(x3, y3));
		}
		
		public Tri(final Vec2 v1, final Vec2 v2, final Vec2 v3)
		{
			_v1 = v1;
			_v2 = v2;
			_v3 = v3;
		}
		
		private static final boolean isSameOrient(final Vec2 p, final Vec2 v1, final Vec2 v2, final Vec2 v3)
		{
			return v3.substract(v2).cross2(p.substract(v2)) * v3.substract(v2).cross2(v1.substract(v2)) >= 0;
		}
		
		public final boolean isInside(final Vec2 v)
		{
			return isSameOrient(v, _v1, _v2, _v3) && isSameOrient(v, _v2, _v1, _v3) && isSameOrient(v, _v3, _v1, _v2);
		}
	}
	
	private static final class Vec2
	{
		private final double _x;
		private final double _y;
		
		public Vec2(final double x, final double y)
		{
			_x = x;
			_y = y;
		}
		
		public final Vec2 substract(final Vec2 vec)
		{
			return new Vec2(_x - vec._x, _y - vec._y);
		}
		
		public final double cross2(final Vec2 vec)
		{
			return _x * vec._y - _y * vec._x;
		}
	}
	
	private final BufferedImage _imageNswe;
	private final ImageIcon _iconNswe;
	private final JLabel _labelNswe;
	private byte _nswe;
	private Image _imageNswes;
	
	public PanelDirectNswe()
	{
		_imageNswe = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		_iconNswe = new ImageIcon();
		_iconNswe.setImage(_imageNswe);
		_labelNswe = new JLabel(_iconNswe);
		_labelNswe.addMouseListener(this);
		
		_nswe = -1;
		updateNsweTexId();
		initLayout();
	}
	
	public final void updateNsweTexId()
	{
		_imageNswes = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(Util.loadImage("./data/textures/nswe_" + Config.NSWE_TEXTURE_ID + ".png").getSource(), new RGBImageFilter()
	    {
	    	@Override
	    	public final int filterRGB(final int x, final int y, final int rgb)
	        {
	            return rgb == Color.WHITE.getRGB() ? 0 : rgb;
	        }
	    }));
		updateNswe(_nswe, true);
	}
	
	private final void initLayout()
	{
		setBorder(BorderFactory.createTitledBorder("Direct NSWE"));
		add(_labelNswe);
	}
	
	private final void updateNswe(final int nswe, final boolean force)
	{
		if (!force && nswe == _nswe)
			return;
		
		if (nswe != -1 && (nswe < 0 || nswe > GeoEngine.NSWE_MASK))
			throw new IllegalArgumentException("Illegal NSWE: " + nswe);
		
		_nswe = (byte) nswe;
		final Graphics2D g = (Graphics2D) _imageNswe.getGraphics();
		
		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, _imageNswe.getWidth(), _imageNswe.getHeight());
		if (nswe != -1)
		{
			final int row = _nswe / 4;
			final int col = _nswe % 4;
			final int width = _imageNswes.getWidth(null) / 4;
			final int height = _imageNswes.getHeight(null) / 4;
			final int sx1 = width * row;
			final int sy1 = height * col;
			final int sx2 = sx1 + width;
			final int sy2 = sy1 + height;
			g.drawImage(_imageNswes, 0, 0, _imageNswe.getWidth(), _imageNswe.getHeight(), sx1, sy1, sx2, sy2, null);
		}
		_labelNswe.repaint();
	}
	
	public final void onSelectedCellUpdated()
	{
		final GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		if (cell == null)
		{
			updateNswe(-1, false);
		}
		else
		{
			updateNswe(cell.getNSWE(), false);
		}
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseClicked(final MouseEvent e)
	{
		short nswe = _nswe;
		if (nswe == -1)
			return;
		
		final double x = (double) e.getX() / (double) _labelNswe.getWidth();
		final double y = (double) e.getY() / (double) _labelNswe.getHeight();
		final Vec2 point = new Vec2(x, y);
		
		int change = 0;
		if (TRI_NORTH.isInside(point))
		{
			change |= GeoEngine.NORTH; 
		}
		else if (TRI_SOUTH.isInside(point))
		{
			change |= GeoEngine.SOUTH; 
		}
		else if (TRI_WEST.isInside(point))
		{
			change |= GeoEngine.WEST; 
		}
		else if (TRI_EAST.isInside(point))
		{
			change |= GeoEngine.EAST; 
		}
		
		if ((nswe & change) == 0)
			nswe |= change;
		else
			nswe &= ~change & GeoEngine.NSWE_MASK;
		
		updateNswe(nswe, false);
		
		final GeoBlockSelector selector = GeoBlockSelector.getInstance();
		FastArrayList<GeoCell> cells;
		int i;
		for (GeoBlockEntry entry = selector.getHead(), tail = selector.getTail(); (entry = entry.getNext()) != tail;)
		{
			cells = entry.getValue();
			for (i = cells.size(); i-- > 0;)
			{
				cells.getUnsafe(i).setNswe(nswe);
			}
		}
		
		GLDisplay.getInstance().requestFocus();
	}
	
	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mousePressed(final MouseEvent e)
	{
		
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseReleased(final MouseEvent e)
	{
		
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseEntered(final MouseEvent e)
	{
		
	}
	
	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public final void mouseExited(final MouseEvent e)
	{
		
	}
}