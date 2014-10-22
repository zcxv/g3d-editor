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

import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.jogl.GLCamera;
import g3deditor.jogl.GLDisplay;
import g3deditor.swing.defaults.DefaultButton;
import g3deditor.swing.defaults.DefaultLabel;
import g3deditor.swing.defaults.DefaultTextField;
import g3deditor.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class DialogJumpTo extends JDialog implements ActionListener, KeyListener, MouseListener
{
	private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 64);
	
	private final DefaultLabel _labelWorldX;
	private final DefaultTextField _fieldWorldX;
	private final DefaultLabel _labelWorldY;
	private final DefaultTextField _fieldWorldY;
	private final DefaultLabel _labelWorldZ;
	private final DefaultTextField _fieldWorldZ;
	
	private final DefaultLabel _labelRegionX;
	private final DefaultTextField _fieldRegionX;
	private final DefaultLabel _labelRegionY;
	private final DefaultTextField _fieldRegionY;
	private final DefaultLabel _labelGeoX;
	private final DefaultTextField _fieldGeoX;
	private final DefaultLabel _labelGeoY;
	private final DefaultTextField _fieldGeoY;
	private final DefaultLabel _labelGeoL2jFound;
	private final DefaultTextField _fieldGeoL2jFound;
	private final DefaultLabel _labelGeoL2OffFound;
	private final DefaultTextField _fieldGeoL2OffFound;
	private final DefaultLabel _labelGeoFileType;
	private final JComboBox _comboGeoFileType;
	
	private final DefaultButton _buttonOk;
	private final DefaultButton _buttonCancel;
	
	private final DefaultLabel _labelRegionMap;
	private final ImageIcon _worldMap;
	
	private int _regionX;
	private int _regionY;
	private BufferedImage _regionImage;
	private BufferedImage _regionImageModified;
	
	public DialogJumpTo(final Frame owner)
	{
		super(owner, "Jump to...", true);
		
		final int regionWH = 37;
		final int width = regionWH * 17;
		final int height = regionWH * 16;
		final BufferedImage img = Util.scaleImage(Util.loadImage("./data/icon/map.jpg"), width, height, 2);
		final Graphics2D g = img.createGraphics();
		final Font font = new Font("Arial", Font.PLAIN, regionWH / 4);
		
		g.setColor(Color.WHITE);
		g.setFont(font);
		
		String name;
		for (int x = 17, y; x-- > 0;)
		{
			for (y = 16; y-- > 0;)
			{
				name = (x + 10) + "_" + (y + 10);
				g.drawString(name, x * regionWH + regionWH / 2 - g.getFontMetrics().stringWidth(name) / 2, y * regionWH + regionWH / 2 + g.getFontMetrics().getHeight() / 2);
				if (y != 15)
					g.drawLine(0, y * regionWH + regionWH, img.getWidth(), y * regionWH + regionWH);
				if (x != 16)
					g.drawLine(x * regionWH + regionWH, 0, x * regionWH + regionWH, img.getHeight());
			}
		}
		g.dispose();
		
		_worldMap = new ImageIcon(img);
		
		_labelWorldX = new DefaultLabel("World X:");
		_fieldWorldX = new DefaultTextField();
		_fieldWorldX.addKeyListener(this);
		_labelWorldY = new DefaultLabel("World Y:");
		_fieldWorldY = new DefaultTextField();
		_fieldWorldY.addKeyListener(this);
		_labelWorldZ = new DefaultLabel("World Z:");
		_fieldWorldZ = new DefaultTextField();
		
		_labelRegionX = new DefaultLabel("RegionX");
		_fieldRegionX = new DefaultTextField();
		_fieldRegionX.addKeyListener(this);
		_labelRegionY = new DefaultLabel("RegionY");
		_fieldRegionY = new DefaultTextField();
		_fieldRegionY.addKeyListener(this);
		
		_labelGeoX = new DefaultLabel("GeoX");
		_fieldGeoX = new DefaultTextField();
		_fieldGeoX.setEnabled(false);
		_labelGeoY = new DefaultLabel("GeoY");
		_fieldGeoY = new DefaultTextField();
		_fieldGeoY.setEnabled(false);
		
		_labelGeoL2jFound = new DefaultLabel("Geo L2j:");
		_fieldGeoL2jFound = new DefaultTextField();
		_fieldGeoL2jFound.setEditable(false);
		_fieldGeoL2jFound.setEnabled(false);
		_labelGeoL2OffFound = new DefaultLabel("Geo L2Off:");
		_fieldGeoL2OffFound = new DefaultTextField();
		_fieldGeoL2OffFound.setEditable(false);
		_fieldGeoL2OffFound.setEnabled(false);
		
		_labelGeoFileType = new DefaultLabel("Select:");
		_comboGeoFileType = new JComboBox();
		_comboGeoFileType.setEditable(false);
		_comboGeoFileType.setEnabled(false);
		
		_buttonOk = new DefaultButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonOk.setEnabled(false);
		_buttonCancel = new DefaultButton("Cancel");
		_buttonCancel.addActionListener(this);
		
		_labelRegionMap = new DefaultLabel();
		_labelRegionMap.addMouseListener(this);
		_labelRegionMap.setIcon(_worldMap);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelWorldX, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldWorldX, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelWorldY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldWorldY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelWorldZ, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldWorldZ, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelRegionX, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldRegionX, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelRegionY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldRegionY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGeoX, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldGeoX, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGeoY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldGeoY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGeoL2jFound, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldGeoL2jFound, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGeoL2OffFound, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldGeoL2OffFound, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGeoFileType, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 9;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_comboGeoFileType, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_buttonOk, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_buttonCancel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 150;
		gbc.ipady = 0;
		add(new DefaultLabel(), gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 12;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelRegionMap, gbc);
		
		setResizable(false);
		pack();
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
		{
			final GLCamera camera = GLDisplay.getInstance().getCamera();
			final int geoX = camera.getGeoX();
			final int geoY = camera.getGeoY();
			if (GeoEngine.getInstance().getActiveRegion() != null && geoX >= GeoEngine.GEOX_MIN && geoX <= GeoEngine.GEOX_MAX && geoY >= GeoEngine.GEOY_MIN && geoY <= GeoEngine.GEOY_MAX)
			{
				_fieldWorldX.setText(String.valueOf(GeoEngine.getWorldX(geoX)));
				_fieldWorldY.setText(String.valueOf(GeoEngine.getWorldY(geoY)));
				_fieldWorldZ.setText("");
				checkInputs(false);
			}
			
			setLocationRelativeTo(getOwner());
		}
		
		super.setVisible(visible);
		if (!visible)
			GLDisplay.getInstance().requestFocus();
	}
	
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonOk)
		{
			final int worldX = Integer.parseInt(_fieldWorldX.getText());
			final int worldY = Integer.parseInt(_fieldWorldY.getText());
			final int geoX = GeoEngine.getGeoX(worldX);
			final int geoY = GeoEngine.getGeoY(worldY);
			
			int worldZ = Integer.MIN_VALUE;
			
			try
			{
				worldZ = Integer.parseInt(_fieldWorldZ.getText());
			}
			catch (final NumberFormatException e1)
			{
				
			}
			
			try
			{
				final int regionX = GeoEngine.getRegionXY(geoX);
				final int regionY = GeoEngine.getRegionXY(geoY);
				final boolean l2j = (_comboGeoFileType.getSelectedItem() == "L2j Geo File");
				final GeoRegion region = GeoEngine.getInstance().getActiveRegion();
				if (region == null)
				{
					GeoEngine.getInstance().reloadGeo(regionX, regionY, l2j);
				}
				else if (region.getRegionX() != regionX || region.getRegionY() != regionY)
				{
					if (!region.allDataEqual())
					{
						final int choice = JOptionPane.showConfirmDialog(
							FrameMain.getInstance(),
							"Region " + region.getName() + " was modified.\nWould u like to save it?", "Save...",
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
									try
									{
										GeoEngine.getInstance().unload();
										GeoEngine.getInstance().reloadGeo(regionX, regionY, l2j);
										
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
					}
					GeoEngine.getInstance().unload();
					GeoEngine.getInstance().reloadGeo(regionX, regionY, l2j);
				}
				
				if (GeoEngine.getInstance().getActiveRegion() != null)
				{
					GLDisplay.getInstance().getCamera().setXYZ(
						geoX,
						// TODO it seems the variable order is incorrect here (x/z/y, not x/y/z)
						// can someone confirm this works incorrectly?
						((worldZ == Integer.MIN_VALUE)
							? GeoEngine.getInstance().nGetCell(geoX, geoY, 0).getHeight() / 16f
							: worldZ / 16f),
						geoY
					);
				}
			}
			catch (final Exception e1)
			{
				e1.printStackTrace();
			}
			
			setVisible(false);
		}
		else if (e.getSource() == _buttonCancel)
		{
			setVisible(false);
		}
	}
	
	private final void checkInputs(final boolean byRegion)
	{
		try
		{
			final int worldX;
			final int worldY;
			
			final int regionX;
			final int regionY;
			
			final int geoX;
			final int geoY;
			final int blockX;
			final int blockY;
			
			if (byRegion)
			{
				regionX = Integer.parseInt(_fieldRegionX.getText()) - 10;
				regionY = Integer.parseInt(_fieldRegionY.getText()) - 10;
				if (regionX < 0 || regionX > 17 || regionY < 0 || regionY > 16)
					throw new Exception();
				
				blockX = 128;
				blockY = 128;
				
				geoX = GeoEngine.getGeoXY(regionX, blockX);
				geoY = GeoEngine.getGeoXY(regionY, blockY);
				
				worldX = GeoEngine.getWorldX(geoX);
				worldY = GeoEngine.getWorldY(geoY);
			}
			else
			{
				worldX = Integer.parseInt(_fieldWorldX.getText());
				worldY = Integer.parseInt(_fieldWorldY.getText());
				
				if (worldX < GeoEngine.MAP_MIN_X || worldX > GeoEngine.MAP_MAX_X || worldY < GeoEngine.MAP_MIN_Y || worldY > GeoEngine.MAP_MAX_Y)
					throw new Exception();
				
				geoX = GeoEngine.getGeoX(worldX);
				geoY = GeoEngine.getGeoY(worldY);
				
				regionX = GeoEngine.getRegionXY(geoX);
				regionY = GeoEngine.getRegionXY(geoY);
				blockX = GeoEngine.getBlockXY(geoX);
				blockY = GeoEngine.getBlockXY(geoY);
			}
			
			_fieldWorldX.setText(String.valueOf(worldX));
			_fieldWorldY.setText(String.valueOf(worldY));
			_fieldRegionX.setText(String.valueOf(regionX + 10));
			_fieldRegionY.setText(String.valueOf(regionY + 10));
			_fieldGeoX.setText(String.valueOf(geoX));
			_fieldGeoY.setText(String.valueOf(geoY));
			
			final boolean hasGeoL2j = GeoEngine.hasGeoFile(regionX, regionY, true);
			final boolean hasGeoL2Off = GeoEngine.hasGeoFile(regionX, regionY, false);
			_fieldGeoL2jFound.setText(hasGeoL2j ? "Found" : "Not found");
			_fieldGeoL2OffFound.setText(hasGeoL2Off ? "Found" : "Not found");
			
			_comboGeoFileType.removeAllItems();
			
			if (hasGeoL2j)
				_comboGeoFileType.addItem("L2j Geo File");
			
			if (hasGeoL2Off)
				_comboGeoFileType.addItem("L2Off Geo File");
			
			_buttonOk.setEnabled(hasGeoL2j || hasGeoL2Off);
			
			_comboGeoFileType.setEnabled(hasGeoL2j && hasGeoL2Off);
			
			if (_regionX != regionX || _regionY != regionY)
			{
				_regionX = regionX;
				_regionY = regionY;
				_regionImage = Util.loadImage("./data/textures/region/" + (regionX + 10) + "_" + (regionY + 10) + ".jpg");
				if (_regionImage == null)
					_regionImage = Util.loadImage("./data/textures/region/water.jpg");
				
				if (_regionImage != null)
				{
					final int wh = Math.min(_worldMap.getIconWidth(), _worldMap.getIconHeight());
					_regionImage = Util.scaleImage(_regionImage, wh, wh, 0);
				}
			}
			
			if (_regionImage != null)
			{
				setRegionFound(_regionImage, blockX, blockY);
			}
			else
			{
				_labelRegionMap.setIcon(new ImageIcon(createRegionNotFound(regionX, regionY)));
			}
		}
		catch (final Exception e)
		{
			_buttonOk.setEnabled(false);
			_labelRegionMap.setIcon(_worldMap);
			_fieldGeoX.setText("");
			_fieldGeoY.setText("");
			_fieldGeoL2jFound.setText("");
			_fieldGeoL2OffFound.setText("");
			_comboGeoFileType.removeAllItems();
			_comboGeoFileType.setEnabled(false);
		}
		
		pack();
	}
	
	private final void setRegionFound(final BufferedImage img, final int blockX, final int blockY)
	{
		final int width = img.getWidth();
		final int height = img.getHeight();
		final int imgX = DialogJumpTo.getRelativePoint(GeoEngine.GEO_REGION_SIZE - 1, blockX, width);
		final int imgY = DialogJumpTo.getRelativePoint(GeoEngine.GEO_REGION_SIZE - 1, blockY, height);
		
		_regionImageModified = new BufferedImage(width, height, img.getType());
		final Graphics2D g = _regionImageModified.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.BLACK);
		g.fillRoundRect(imgX - 5, imgY - 5, 10, 10, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(imgX, imgY - 10, imgX, imgY + 10);
		g.drawLine(imgX - 10, imgY, imgX + 10, imgY);
		g.dispose();
		_labelRegionMap.setIcon(new ImageIcon(_regionImageModified));
	}
	
	private final BufferedImage createRegionNotFound(final int regionX, final int regionY)
	{
		final BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_3BYTE_BGR);
		final Graphics2D g = img.createGraphics();
		
		g.setBackground(Color.RED);
		g.clearRect(0, 0, 512, 512);
		g.setColor(Color.BLACK);
		g.setFont(DEFAULT_FONT);
		
		String name = "Region";
		g.drawString(name, 256 - g.getFontMetrics().stringWidth(name) / 2, 160);
		
		name = "NOT";
		g.drawString(name, 256 - g.getFontMetrics().stringWidth(name) / 2, 230);
		
		name = "Found";
		g.drawString(name, 256 - g.getFontMetrics().stringWidth(name) / 2, 300);
		
		name = (regionX + 10) + "_" + (regionY + 10);
		g.drawString(name, 256 - g.getFontMetrics().stringWidth(name) / 2, 370);
		
		g.dispose();
		return img;
	}
	
	@Override
	public final void keyTyped(final KeyEvent e)
	{
		// checkInputs();
	}
	
	@Override
	public final void keyPressed(final KeyEvent e)
	{
		// checkInputs();
	}
	
	@Override
	public final void keyReleased(final KeyEvent e)
	{
		if (e.getSource() == _fieldRegionX || e.getSource() == _fieldRegionY)
		{
			checkInputs(true);
		}
		else
		{
			checkInputs(false);
		}
	}
	
	private final int getRegionX(final int mouseX)
	{
		return getRelativePoint(_labelRegionMap.getWidth(), mouseX, 17D) + 10;
	}
	
	private final int getRegionY(final int mouseY)
	{
		return getRelativePoint(_labelRegionMap.getHeight(), mouseY, 16D) + 10;
	}
	
	private final int getBlockX(final int mouseX)
	{
		final int width = _labelRegionMap.getWidth();
		return mouseX > width ? - 1 : getRelativePoint(width, mouseX, GeoEngine.GEO_REGION_SIZE - 1);
	}
	
	private final int getBlockY(final int mouseY)
	{
		return getRelativePoint(_labelRegionMap.getHeight(), mouseY, GeoEngine.GEO_REGION_SIZE - 1);
	}
	
	private static final int getRelativePoint(final int val, final int rel, final double scale)
	{
		return (int) ((val - (val - rel)) * scale / val);
	}
	
	@Override
	public final void mouseClicked(final MouseEvent e)
	{
		if (e.getSource() == _labelRegionMap)
		{
			if (_labelRegionMap.getIcon() == _worldMap)
			{
				if (e.getClickCount() == (e.getButton() == MouseEvent.BUTTON1 ? 2 : 1))
				{
					final int regionX = getRegionX(e.getX());
					final int regionY = getRegionY(e.getY());
					_fieldRegionX.setText(String.valueOf(regionX));
					_fieldRegionY.setText(String.valueOf(regionY));
					checkInputs(true);
				}
			}
			else
			{
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1)
				{
					final int regionX = _regionX;
					final int regionY = _regionY;
					final int blockX = getBlockX(e.getX());
					final int blockY = getBlockY(e.getY());
					final int geoX = GeoEngine.getGeoXY(regionX, blockX);
					final int geoY = GeoEngine.getGeoXY(regionY, blockY);
					final int worldX = GeoEngine.getWorldX(geoX);
					final int worldY = GeoEngine.getWorldY(geoY);
					
					_fieldWorldX.setText(String.valueOf(worldX));
					_fieldWorldY.setText(String.valueOf(worldY));
					_fieldWorldZ.setText("");
					_fieldRegionX.setText(String.valueOf(regionX + 10));
					_fieldRegionY.setText(String.valueOf(regionY + 10));
					_fieldGeoX.setText(String.valueOf(geoX));
					_fieldGeoY.setText(String.valueOf(geoY));
					
					if (_regionImage != null)
					{
						setRegionFound(_regionImage, blockX, blockY);
					}
					else
					{
						_labelRegionMap.setIcon(new ImageIcon(createRegionNotFound(regionX, regionY)));
					}
				}
				else if (e.getClickCount() == (e.getButton() == MouseEvent.BUTTON1 ? 2 : 1))
				{
					_labelRegionMap.setIcon(_worldMap);
					_buttonOk.setEnabled(false);
					pack();
				}
			}
		}
	}
	
	@Override
	public final void mousePressed(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseReleased(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseEntered(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseExited(final MouseEvent e)
	{
		
	}
}