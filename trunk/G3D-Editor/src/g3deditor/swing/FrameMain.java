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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class FrameMain extends JFrame implements ActionListener
{
	private static final FileFilter GEO_FILTER = new FileFilter()
	{
		public final boolean accept(final File file)
		{
			return file.isDirectory() || GeoEngine.GEO_FILE_FILTER.accept(file);
		}
		
		@Override
		public final String getDescription()
		{
			return "GeoData *.l2j/*.dat";
		}
	};
	
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
	private final PanelDirectNswe _panelDirectNswe;
	
	private final DialogJumpTo _dialogJumpTo;
	private final DialogConfig _dialogConfig;
	
	private final JMenuBar _menuBar;
	private final DefaultButton _buttonConfig;
	private final DefaultButton _buttonOpen;
	private final DefaultButton _buttonJumpTo;
	private final DefaultButton _buttonSave;
	private final DefaultButton _buttonHelp;
	private final DefaultLabel _labelLogoL2j;
	private final DefaultButton _buttonDonate;
	
	private final JFileChooser _fileChooser;
	
	private GeoCell _selectedCell;
	private boolean _waitForUpdate;
	
	private FrameMain()
	{
		super("G3D-Editor [Beta 1.5] by Forsaiken");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public final void windowClosing(final WindowEvent e)
			{
				final GeoRegion region = GeoEngine.getInstance().getActiveRegion();
				if (region != null && !region.allDataEqual())
				{
					final int choice = JOptionPane.showConfirmDialog(
						FrameMain.getInstance(),
						"Region " + region.getName() + " was modified.\nWould u like to save it before closing?",
						"Save and exit",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE
					);
					
					if (choice == JOptionPane.YES_OPTION)
					{
						new DialogSave(FrameMain.getInstance(), region, new Runnable()
						{
							@Override
							public final void run()
							{
								System.exit(0);
							}
						}).setVisible(true);
						return;
					}
				}
				System.exit(0);
			}
		});
		setIconImage(Util.loadImage("./data/icon/l2.jpg"));
		
		_panelRight = new JPanel();
		_panelNswe = new PanelNswe();
		_panelCellInfo = new PanelCellInfo();
		_panelBlockConvert = new PanelBlockConvert();
		_panelLayers = new PanelLayers(this);
		_panelDirectNswe = new PanelDirectNswe();
		_dialogJumpTo = new DialogJumpTo(this);
		_dialogConfig = new DialogConfig(this);
		
		_menuBar = new JMenuBar();
		_buttonConfig = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/config.png")));
		_buttonConfig.addActionListener(this);
		_buttonOpen = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/open.png")));
		_buttonOpen.addActionListener(this);
		_buttonJumpTo = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/search.png")));
		_buttonJumpTo.addActionListener(this);
		_buttonSave = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/save.png")));
		_buttonSave.addActionListener(this);
		_buttonHelp = new DefaultButton(new ImageIcon(Util.loadImage("./data/icon/help.png")));
		_buttonHelp.addActionListener(this);
		BufferedImage img = Util.loadImage("./data/icon/l2jserverlogo.png");
		_labelLogoL2j = new DefaultLabel(new ImageIcon(Util.scaleImage(img, (int) (img.getWidth() * (32D / img.getHeight())), 32, 2)));
		img = Util.loadImage("./data/icon/donate.png");
		_buttonDonate = new DefaultButton(new ImageIcon(Util.scaleImage(img, (int) (img.getWidth() * (32D / img.getHeight())), 32, 2)));
		_buttonDonate.addActionListener(this);
		
		_fileChooser = new JFileChooser();
		_fileChooser.setFileFilter(GEO_FILTER);
		_fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		_fileChooser.setDialogTitle("Open an GeoData file...");
		_fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
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
		_menuBar.add(_buttonOpen, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 50;
		_menuBar.add(new DefaultLabel(), gbc);
		
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		_menuBar.add(_labelLogoL2j, gbc);
		
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 50;
		_menuBar.add(new DefaultLabel(), gbc);
		
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		_menuBar.add(_buttonSave, gbc);
		
		gbc.gridx = 7;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		_menuBar.add(_buttonHelp, gbc);
		
		gbc.gridx = 8;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		_menuBar.add(_buttonDonate);
		
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
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		_panelRight.add(_panelDirectNswe, gbc);
		
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
	
	public final void checkAvailableRenderers()
	{
		_dialogConfig.checkAvailableRenderers();
	}
	
	public final void onNsweTexIdUpdated()
	{
		_panelDirectNswe.updateNsweTexId();
	}
	
	public final void setSelectedGeoCell(final GeoCell cell)
	{
		_selectedCell = cell;
		
		if (!_waitForUpdate)
		{
			_waitForUpdate = true;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public final void run()
				{
					onSelectedCellUpdated();
				}
			});
		}
	}
	
	final void onSelectedCellUpdated()
	{
		_waitForUpdate = false;
		_panelNswe.onSelectedCellUpdated();
		_panelCellInfo.onSelectedCellUpdated();
		_panelBlockConvert.onSelectedCellUpdated();
		_panelLayers.onSelectedCellUpdated();
		_panelDirectNswe.onSelectedCellUpdated();
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
				JOptionPane.showMessageDialog(this, "I am sure you want to load a region first ;)", "Save Region UNKOWN?!?", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (e.getSource() == _buttonOpen)
		{
			if (_fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				final File file = _fileChooser.getSelectedFile();
				if (file != null && GeoEngine.GEO_FILE_FILTER.accept(file))
				{
					final int[] header = GeoEngine.getHeaderOfL2jOrL2Off(file);
					final int regionX = header[0];
					final int regionY = header[1];
					final int geoX = GeoEngine.getGeoXY(regionX, 128);
					final int geoY = GeoEngine.getGeoXY(regionY, 128);
					
					final boolean l2j = !file.getName().toLowerCase().equals(".dat");
					final GeoRegion region = GeoEngine.getInstance().getActiveRegion();
					if (region != null)
					{
						if (region.getRegionX() != regionX || region.getRegionY() != regionY)
						{
							if (!region.allDataEqual())
							{
								switch (JOptionPane.showConfirmDialog(FrameMain.getInstance(), "Region " + region.getName() + " was modified.\nWould u like to save it?", "Save...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE))
								{
									case JOptionPane.YES_OPTION:
									{
										new DialogSave(FrameMain.getInstance(), region, new Runnable()
										{
											@Override
											public final void run()
											{
												try
												{
													GeoEngine.getInstance().unload();
													GeoEngine.getInstance().reloadGeo(regionX, regionY, l2j, file);
													
													if (GeoEngine.getInstance().getActiveRegion() != null)
														GLDisplay.getInstance().getCamera().setXYZ(geoX, GeoEngine.getInstance().nGetCell(geoX, geoY, 0).getHeight() / 16f, geoY);
												}
												catch (final Exception e1)
												{
													e1.printStackTrace();
												}
											}
										}).setVisible(true);
										setVisible(false);
										return;
									}
									
									case JOptionPane.NO_OPTION:
										break;
										
									default:
										return;
								}
							}
							GeoEngine.getInstance().unload();
							try
							{
								GeoEngine.getInstance().reloadGeo(regionX, regionY, l2j, file);
							}
							catch (final Exception e1)
							{
								e1.printStackTrace();
							}
						}
					}
					else
					{
						try
						{
							GeoEngine.getInstance().reloadGeo(regionX, regionY, l2j, file);
						}
						catch (final Exception e1)
						{
							e1.printStackTrace();
						}
					}
					
					if (GeoEngine.getInstance().getActiveRegion() != null)
						GLDisplay.getInstance().getCamera().setXYZ(geoX, GeoEngine.getInstance().nGetCell(geoX, geoY, 0).getHeight() / 16f, geoY);
				}
			}
		}
		else if (e.getSource() == _buttonConfig)
		{
			_dialogConfig.setVisible(true);
		}
		else if (e.getSource() == _buttonHelp)
		{
			JOptionPane.showMessageDialog(this,
					"<html><b>G3D-Editor</b> - By Forsaiken aka Patrick Biesenbach<br>" +
					"If you like this programm please donate!<br>PayPal/E-Mail: <i>patrickbiesenbach@yahoo.de</i><br><br>" +
					"Controll:<br><table>" +
					"<tr><td>Look around:</td><td>Hold RIGHT-MOUSE and move</td></tr>" +
					"<tr><td>Move around:</td><td>Use WASD and QE</td></tr>" +
					"<tr><td>Move faster:</td><td>Hold SPACE while moving</td></tr>" +
					"<tr><td>Select a single cell:</td><td>LEFT-MOUSE-CLICK on any cell</td></tr>" +
					"<tr><td>Select multiple cells:</td><td>Hold SHIFT while selecting</td></tr>" +
					"<tr><td>Select a whole block:</td><td>Hold ALT while selecting</td></tr>" +
					"<tr><td>Unselect a cell:</td><td>Hold SHIFT and LEFT-MOUSE-CLICK on it again</td></tr>" +
					"<tr><td>Special MultiLayer:</td><td>You see a white box when selecting</td></tr>" +
					"<tr><td>*</td><td>This box is you selection radius, only cells in this box will be selected</td></tr>" +
					"<tr><td>*</td><td>To change the radius use the MOUSE_WHEEL or MIDDLE_MOUSE_CLICK to (un)set radius to infinite</td></tr>" +
					"<tr><td>*</td><td>To change it faster hold SPACE while using the MOUSE_WHEEL</td></tr>" +
					"<tr><td>Changing height:</td><td>To change the height of the selected cells hold CTRL while using the MOUSE_WHEEL</td></tr>" +
					"<tr><td>*</td><td>To change the height faster hold SPACE while using the MOUSE_WHEEL</td></tr>" +
					"<tr><td>Dis/Enable terrain:</td><td>Press T (toggle)</td></tr>" +
					"<tr><td>Dis/Enable terrain wireframe:</td><td>Press R (toggle)</td></tr>" +
					"<tr><td>Dis/Enable grid:</td><td>Press G (toggle)</td></tr>" +
					"<tr><td>Freeze/Unfreeze grid:</td><td>Press F (toggle)</td></tr>" +
					"</table><br>" +
					"Note: The editor window need focus to accept inputs!<br><br>" +
					"This programm is FREE and licensed under GNU GPLv3 <i>http://www.gnu.org/licenses/</i><br>" +
					"Use it on your OWN RISC! I will not take an ANY WARRANTY!<br><br>" +
					"<i>http://code.google.com/p/g3d-editor/</i>" +
					"</html>", "About", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource() == _buttonDonate)
		{
			Util.openBrowser("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=R3U86G5YDPRNQ");
		}
	}
}