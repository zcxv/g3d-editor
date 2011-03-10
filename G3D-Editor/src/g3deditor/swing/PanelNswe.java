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
import g3deditor.geo.GeoBlockSelector.ForEachGeoCellProcedure;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

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
		super.setLayout(new GridLayout(4, 4));
		for (final NsweButton buttonNswe : _buttonsNswe)
		{
			super.add(buttonNswe);
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
		GeoBlockSelector.getInstance().forEachGeoCell(new ForEachGeoCellProcedure()
		{
			@Override
			public final boolean execute(final GeoCell cell)
			{
				cell.setNswe(nsweButton.getNswe());
				return true;
			}
		});
	}
	
	private static final class NsweButton extends JToggleButton
	{
		private final short _nswe;
		
		public NsweButton(final int nswe)
		{
			_nswe = (short) nswe;
			
			try
			{
				setIcon(new ImageIcon(ImageIO.read(new File("./data/icon/nswe-" + nswe + ".png"))));
				//setPreferredSize(new Dimension(getIcon().getIconWidth() + 8, getIcon().getIconHeight() + 8));
			}
			catch (final IOException e)
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