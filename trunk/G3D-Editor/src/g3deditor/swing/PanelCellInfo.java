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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class PanelCellInfo extends JPanel
{
	private final JLabel _labelRegionXY;
	private final JTextField _fieldRegionX;
	private final JTextField _fieldRegionY;
	private final JLabel _labelWorldXY;
	private final JTextField _fieldWorldX;
	private final JTextField _fieldWorldY;
	private final JLabel _labelGeoXY;
	private final JTextField _fieldGeoX;
	private final JTextField _fieldGeoY;
	private final JLabel _labelBlockXY;
	private final JTextField _fieldBlockX;
	private final JTextField _fieldBlockY;
	private final JLabel _labelCellXY;
	private final JTextField _fieldCellX;
	private final JTextField _fieldCellY;
	private final JLabel _labelTypeLayers;
	private final JTextField _fieldType;
	private final JTextField _fieldLayers;
	
	public PanelCellInfo()
	{
		_labelRegionXY = new JLabel("Region X/Y:");
		_fieldRegionX = new AutoDisableJTextField();
		_fieldRegionX.setEditable(false);
		_fieldRegionY = new AutoDisableJTextField();
		_fieldRegionY.setEditable(false);
		_labelWorldXY = new JLabel("World X/Y:");
		_fieldWorldX = new AutoDisableJTextField();
		_fieldWorldX.setEditable(false);
		_fieldWorldY = new AutoDisableJTextField();
		_fieldWorldY.setEditable(false);
		_labelGeoXY = new JLabel("Geo X/Y:");
		_fieldGeoX = new AutoDisableJTextField();
		_fieldGeoX.setEditable(false);
		_fieldGeoY = new AutoDisableJTextField();
		_fieldGeoY.setEditable(false);
		_labelBlockXY = new JLabel("Block X/Y:");
		_fieldBlockX = new AutoDisableJTextField();
		_fieldBlockX.setEditable(false);
		_fieldBlockY = new AutoDisableJTextField();
		_fieldBlockY.setEditable(false);
		_labelCellXY = new JLabel("Cell X/Y:");
		_fieldCellX = new AutoDisableJTextField();
		_fieldCellX.setEditable(false);
		_fieldCellY = new AutoDisableJTextField();
		_fieldCellY.setEditable(false);
		_labelTypeLayers = new JLabel("Type/Layers:");
		_fieldType = new AutoDisableJTextField();
		_fieldType.setEditable(false);
		_fieldLayers = new AutoDisableJTextField();
		_fieldLayers.setEditable(false);
		
		initLayout();
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		setBorder(BorderFactory.createTitledBorder("Block/Cell Info"));
		setLayout(new GridBagLayout());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_labelRegionXY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldRegionX, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldRegionY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_labelWorldXY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldWorldX, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldWorldY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_labelGeoXY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldGeoX, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldGeoY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_labelBlockXY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldBlockX, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldBlockY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_labelCellXY, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldCellX, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldCellY, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_labelTypeLayers, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldType, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_fieldLayers, gbc);
	}
	
	private final void setFieldsEnabled(final boolean enabled)
	{
		_fieldRegionX.setEnabled(enabled);
		_fieldRegionY.setEnabled(enabled);
		_fieldGeoX.setEnabled(enabled);
		_fieldGeoY.setEnabled(enabled);
		_fieldCellX.setEnabled(enabled);
		_fieldCellY.setEnabled(enabled);
		_fieldWorldX.setEnabled(enabled);
		_fieldWorldY.setEnabled(enabled);
		_fieldBlockX.setEnabled(enabled);
		_fieldBlockY.setEnabled(enabled);
		_fieldType.setEnabled(enabled);
		_fieldLayers.setEnabled(enabled);
	}
	
	public final void onSelectedCellUpdated()
	{
		final GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		if (cell == null)
		{
			setFieldsEnabled(false);
			_fieldRegionX.setText("");
			_fieldRegionY.setText("");
			_fieldGeoX.setText("");
			_fieldGeoY.setText("");
			_fieldCellX.setText("");
			_fieldCellY.setText("");
			_fieldWorldX.setText("");
			_fieldWorldY.setText("");
			_fieldBlockX.setText("");
			_fieldBlockY.setText("");
			_fieldType.setText("");
			_fieldLayers.setText("");
		}
		else
		{
			setFieldsEnabled(true);
			_fieldRegionX.setText(String.valueOf(GeoEngine.getRegionXY(cell.getGeoX())));
			_fieldRegionY.setText(String.valueOf(GeoEngine.getRegionXY(cell.getGeoY())));
			_fieldWorldX.setText(String.valueOf(GeoEngine.getWorldX(cell.getGeoX())));
			_fieldWorldY.setText(String.valueOf(GeoEngine.getWorldY(cell.getGeoY())));
			_fieldGeoX.setText(String.valueOf(cell.getGeoX()));
			_fieldGeoY.setText(String.valueOf(cell.getGeoY()));
			_fieldBlockX.setText(String.valueOf(cell.getBlock().getBlockX()));
			_fieldBlockY.setText(String.valueOf(cell.getBlock().getBlockY()));
			_fieldCellX.setText(String.valueOf(GeoEngine.getCellXY(cell.getGeoX())));
			_fieldCellY.setText(String.valueOf(GeoEngine.getCellXY(cell.getGeoY())));
			_fieldType.setText(String.valueOf(cell.getBlock().getStringType()));
			_fieldLayers.setText(String.valueOf(cell.getBlock().nGetLayerCount(cell.getGeoX(), cell.getGeoY())));
		}
	}
	
	private static final class AutoDisableJTextField extends JTextField
	{
		public AutoDisableJTextField()
		{
			super.setEnabled(false);
		}
		
		@Override
		public final void setText(final String text)
		{
			super.setText(text);
			super.setEnabled(text != null && !text.isEmpty());
		}
	}
}