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
import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.jogl.GLDisplay;
import g3deditor.swing.defaults.DefaultButton;
import g3deditor.swing.defaults.DefaultLabel;
import g3deditor.util.Util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class FrameMain extends JFrame implements ActionListener
{
	private static FrameMain _instance;
	
	public static final void init()
	{
		_instance = new FrameMain();
	}
	
	public static final FrameMain getInstance()
	{
		return _instance;
	}
	
	private final JPanel _panelRight;
	private final PanelNswe _panelNswe;
	private final PanelCellInfo _panelCellInfo;
	private final PanelBlockConvert _panelBlockConvert;
	private final PanelLayers _panelLayers;
	
	private final DialogJumpTo _dialogJumpTo;
	private final DialogConfig _dialogConfig;
	
	private final JMenuBar _menuBar;
	private final DefaultButton _buttonConfig;
	private final DefaultButton _buttonJumpTo;
	private final DefaultButton _buttonSave;
	private final DefaultButton _buttonHelp;
	private final DefaultLabel _labelLogoL2j;
	
	private GeoCell _selectedCell;
	
	private FrameMain()
	{
		super("G3D-Editor [A1] by Forsaiken");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_panelRight = new JPanel();
		_panelNswe = new PanelNswe();
		_panelCellInfo = new PanelCellInfo();
		_panelBlockConvert = new PanelBlockConvert();
		_panelLayers = new PanelLayers();
		_dialogJumpTo = new DialogJumpTo(this);
		_dialogConfig = new DialogConfig(this);
		
		_menuBar = new JMenuBar();
		_buttonConfig = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/config.png")));
		_buttonConfig.addActionListener(this);
		_buttonJumpTo = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/search.png")));
		_buttonJumpTo.addActionListener(this);
		_buttonSave = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/save.png")));
		_buttonSave.addActionListener(this);
		_buttonHelp = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/help.png")));
		_buttonHelp.addActionListener(this);
		final BufferedImage img = Util.loadImage("./data/icon/l2jserverlogo.png");
		_labelLogoL2j = new DefaultLabel(new ImageIcon(Util.scaleImage(img, (int) (img.getWidth() * (32D / img.getHeight())), 32, 2)));
		
		setJMenuBar(_menuBar);
		
		initLayout();
		
		setMinimumSize(new Dimension(1024, 768));
		setLocationRelativeTo(null);
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		
		_menuBar.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		_menuBar.add(_buttonConfig, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		_menuBar.add(_buttonJumpTo, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 50;
		_menuBar.add(new DefaultLabel(), gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		_menuBar.add(_labelLogoL2j, gbc);
		
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 50;
		_menuBar.add(new DefaultLabel(), gbc);
		
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		_menuBar.add(_buttonSave, gbc);
		
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		_menuBar.add(_buttonHelp, gbc);
		
		//gbc.insets = new Insets(2, 2, 2, 2);
		
		_panelRight.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		_panelRight.add(_panelCellInfo, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		_panelRight.add(_panelBlockConvert, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		_panelRight.add(_panelLayers, gbc);
		
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(GLDisplay.getInstance().getCanvas(), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_panelNswe, gbc);
		
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
		_selectedCell = cell;
		_panelNswe.onSelectedCellUpdated();
		_panelCellInfo.onSelectedCellUpdated();
		_panelBlockConvert.onSelectedCellUpdated();
		_panelLayers.onSelectedCellUpdated();
	}
	
	public final GeoCell getSelectedGeoCell()
	{
		return _selectedCell;
	}
	
	public final boolean isSelectedGeoCell(final GeoCell cell)
	{
		return _selectedCell == cell;
	}
	
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonJumpTo)
		{
			_dialogJumpTo.setVisible(true);
		}
		else if (e.getSource() == _buttonSave)
		{
			final GeoRegion region = GeoEngine.getInstance().getActiveRegion();
			if (region != null)
			{
				new DialogSave(this, region).setVisible(true);
			}
			else
			{
				JOptionPane.showMessageDialog(FrameMain.this, "I am sure you want to load a region first ;)", "Save Region UNKOWN?!?", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (e.getSource() == _buttonConfig)
		{
			_dialogConfig.setVisible(true);
		}
		else if (e.getSource() == _buttonHelp)
		{
			JOptionPane.showMessageDialog(FrameMain.this, "<html>G3D-Editor<br>By Forsaiken<br>If you like this programm please donate!<br>PayPal: patrickbiesenbach@yahoo.de</html>", "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}