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

import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoBlockSelector.GeoBlockEntry;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.swing.defaults.DefaultToggleButton;
import g3deditor.util.FastArrayList;
import g3deditor.util.Util;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class PanelNswe extends JPanel implements ActionListener
{
	private final NsweButton[] _buttonsNswe;
	
	public PanelNswe()
	{
		_buttonsNswe = new NsweButton[GeoEngine.NSWE_MASK + 1];
		for (int i = _buttonsNswe.length; i-- > 0;)
		{
			_buttonsNswe[i] = new NsweButton(i);
			_buttonsNswe[i].addActionListener(this);
		}
		initLayout();
	}
	
	private final void initLayout()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		for (final NsweButton buttonNswe : _buttonsNswe)
		{
			add(buttonNswe);
		}
	}
	
	private final void setButtonsEnabled(final boolean enabled)
	{
		for (final NsweButton buttonNswe : _buttonsNswe)
		{
			buttonNswe.setEnabled(enabled);
		}
	}
	
	private final void setButtonSelected(final NsweButton selected)
	{
		for (final NsweButton buttonNswe : _buttonsNswe)
		{
			if (buttonNswe == selected)
				buttonNswe.setSelected(true);
			else
				buttonNswe.setSelected(false);
		}
	}
	
	public final void onSelectedCellUpdated()
	{
		final GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		if (cell == null)
		{
			setButtonSelected(null);
			setButtonsEnabled(false);
		}
		else
		{
			setButtonsEnabled(true);
			setButtonSelected(_buttonsNswe[cell.getNSWE()]);
		}
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		final NsweButton nsweButton = (NsweButton) e.getSource();
		setButtonSelected(nsweButton);
		
		final GeoBlockSelector selector = GeoBlockSelector.getInstance();
		FastArrayList<GeoCell> cells;
		int i;
		for (GeoBlockEntry entry = selector.getHead(), tail = selector.getTail(); (entry = entry.getNext()) != tail;)
		{
			cells = entry.getValue();
			for (i = cells.size(); i-- > 0;)
			{
				cells.getUnsafe(i).setNswe(nsweButton.getNswe());
			}
		}
	}
	
	private static final class NsweButton extends DefaultToggleButton
	{
		private final short _nswe;
		
		public NsweButton(final int nswe)
		{
			_nswe = (short) nswe;
			
			final BufferedImage img = Util.loadImage("./data/icon/nswe-" + nswe + ".png");
			if (img != null)
			{
				setIcon(new ImageIcon(img));
				final int width = img.getWidth() + 15;
				final int height = img.getHeight() + 15;
				setPreferredSize(new Dimension(width, height));
				setMaximumSize(new Dimension(width, height));
				setMinimumSize(new Dimension(width, height));
			}
			else
			{
				setText(GeoEngine.nameOfNSWE(nswe));
			}
		}
		
		public final short getNswe()
		{
			return _nswe;
		}
	}
}