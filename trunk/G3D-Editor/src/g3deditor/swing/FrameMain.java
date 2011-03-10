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

import g3deditor.geo.GeoCell;
import g3deditor.jogl.GLDisplay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class FrameMain extends JFrame
{
	private static FrameMain _instance;
	
	public static final void init(final GLDisplay display)
	{
		_instance = new FrameMain(display);
	}
	
	public static final FrameMain getInstance()
	{
		return _instance;
	}
	
	private final GLDisplay _display;
	private final PanelNswe _panelNswe;
	
	private GeoCell _selectedCell;
	
	private FrameMain(final GLDisplay display)
	{
		super("G3D-Editor [A1] by Forsaiken");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_display = display;
		_panelNswe = new PanelNswe();
		
		initLayout();
		
		setSize(1024, 768);
		setLocationRelativeTo(null);
        setVisible(true);
	}
	
	private final void initLayout()
	{
		super.setLayout(new GridBagLayout());
		
		final GridBagConstraints gbc = new GridBagConstraints();
		//gbc.insets = new Insets(2, 2, 2, 2);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		super.add(_display.getCanvas(), gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		super.add(_panelNswe, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		super.add(new JLabel(), gbc);
	}
	
	public final void setSelectedGeoCell(final GeoCell cell)
	{
		if (_selectedCell != cell)
		{
			_selectedCell = cell;
			_panelNswe.onSelectedCellUpdated();
		}
	}
	
	public final GeoCell getSelectedGeoCell()
	{
		return _selectedCell;
	}
	
	public final boolean isSelectedGeoCell(final GeoCell cell)
	{
		return _selectedCell == cell;
	}
}