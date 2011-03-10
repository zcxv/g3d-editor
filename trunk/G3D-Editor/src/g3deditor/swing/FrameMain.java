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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
	private final JPanel _panelBottom;
	private final JPanel _panelRight;
	private final PanelNswe _panelNswe;
	private final PanelCellInfo _panelCellInfo;
	private final PanelBlockConvert _panelBlockConvert;
	private final PanelLayers _panelLayers;
	
	private final JMenuBar _menuBar;
	private final JMenu _menuData;
	private final JMenuItem _itemLoad;
	private final JMenuItem _itemSave;
	private final JMenuItem _itemDataExit;
	private final JMenu _menuHelp;
	private final JMenuItem _itemAbout;
	
	private GeoCell _selectedCell;
	
	private FrameMain(final GLDisplay display)
	{
		super("G3D-Editor [A1] by Forsaiken");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_display = display;
		_panelBottom = new JPanel();
		_panelRight = new JPanel();
		_panelNswe = new PanelNswe();
		_panelCellInfo = new PanelCellInfo();
		_panelBlockConvert = new PanelBlockConvert();
		_panelLayers = new PanelLayers();
		
		_menuBar = new JMenuBar();
		_menuData = new JMenu("File");
		_itemLoad = new JMenuItem("Load");
		_itemSave = new JMenuItem("Save");
		_itemDataExit = new JMenuItem("Exit");
		_menuHelp = new JMenu("Help");
		_itemAbout = new JMenuItem("About");
		_itemAbout.addActionListener(new ActionListener()
		{
			@Override
			public final void actionPerformed(final ActionEvent e)
			{
				JOptionPane.showMessageDialog(FrameMain.this, "<html>G3D-Editor<br>By Forsaiken<br>If you like this programm please donate!<br>PayPal: patrickbiesenbach@yahoo.de</html>", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		_menuData.add(_itemLoad);
		_menuData.add(_itemSave);
		_menuData.add(_itemDataExit);
		_menuBar.add(_menuData);
		_menuHelp.add(_itemAbout);
		_menuBar.add(_menuHelp);
		
		setJMenuBar(_menuBar);
		
		initLayout();
		
		setSize(1024, 768);
		setLocationRelativeTo(null);
        setVisible(true);
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		//gbc.insets = new Insets(2, 2, 2, 2);
		
		_panelBottom.setLayout(new GridBagLayout());
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		_panelBottom.add(_panelNswe, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		_panelBottom.add(new JLabel(), gbc);
		
		_panelRight.setLayout(new GridBagLayout());
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		_panelRight.add(_panelCellInfo, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		_panelRight.add(_panelBlockConvert, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		_panelRight.add(_panelLayers, gbc);
		
		setLayout(new GridBagLayout());
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(_display.getCanvas(), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_panelBottom, gbc);
		
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(_panelRight, gbc);
	}
	
	public final void setSelectedGeoCell(final GeoCell cell)
	{
		if (_selectedCell != cell)
		{
			_selectedCell = cell;
			_panelNswe.onSelectedCellUpdated();
			_panelCellInfo.onSelectedCellUpdated();
			_panelLayers.onSelectedCellUpdated();
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