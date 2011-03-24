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
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.jogl.GLDisplay;
import g3deditor.swing.defaults.DefaultButton;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class PanelBlockConvert extends JPanel implements ActionListener
{
	private final DefaultButton _buttonConvertFlat;
	private final DefaultButton _buttonConvertComplex;
	private final DefaultButton _buttonConvertMultiLayer;
	private final DefaultButton _buttonRestoreBlock;
	
	public PanelBlockConvert()
	{
		_buttonConvertFlat = new DefaultButton("Flat");
		_buttonConvertFlat.addActionListener(this);
		_buttonConvertFlat.setEnabled(false);
		_buttonConvertComplex = new DefaultButton("Complex");
		_buttonConvertComplex.addActionListener(this);
		_buttonConvertComplex.setEnabled(false);
		_buttonConvertMultiLayer = new DefaultButton("Multi");
		_buttonConvertMultiLayer.addActionListener(this);
		_buttonConvertMultiLayer.setEnabled(false);
		_buttonRestoreBlock = new DefaultButton("Restore from base");
		_buttonRestoreBlock.addActionListener(this);
		_buttonRestoreBlock.setEnabled(false);
		
		initLayout();
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		setBorder(BorderFactory.createTitledBorder("Block Convert"));
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_buttonConvertFlat, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_buttonConvertComplex, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_buttonConvertMultiLayer, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(_buttonRestoreBlock, gbc);
	}
	
	private final void setFieldsEnabled(final boolean enabled)
	{
		if (enabled)
		{
			final boolean[] selectedTypes = GeoBlockSelector.getInstance().getSelectedTypes();
			final boolean selectedDataEqual = GeoBlockSelector.getInstance().getSelectedDataEqual();
			
			_buttonConvertFlat.setEnabled(selectedTypes[GeoEngine.GEO_BLOCK_TYPE_COMPLEX] || selectedTypes[GeoEngine.GEO_BLOCK_TYPE_MULTILAYER]);
			_buttonConvertComplex.setEnabled(selectedTypes[GeoEngine.GEO_BLOCK_TYPE_FLAT] || selectedTypes[GeoEngine.GEO_BLOCK_TYPE_MULTILAYER]);
			_buttonConvertMultiLayer.setEnabled(selectedTypes[GeoEngine.GEO_BLOCK_TYPE_FLAT] || selectedTypes[GeoEngine.GEO_BLOCK_TYPE_COMPLEX]);
			_buttonRestoreBlock.setEnabled(!selectedDataEqual);
		}
		else
		{
			_buttonConvertFlat.setEnabled(false);
			_buttonConvertComplex.setEnabled(false);
			_buttonConvertMultiLayer.setEnabled(false);
			_buttonRestoreBlock.setEnabled(false);
		}
	}
	
	public final void onSelectedCellUpdated()
	{
		final GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		if (cell == null)
		{
			setFieldsEnabled(false);
		}
		else
		{
			setFieldsEnabled(true);
		}
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		final GeoRegion region = GeoEngine.getInstance().getActiveRegion();
		if (region == null)
			return;
		
		if (e.getSource() == _buttonConvertFlat)
		{
			final int notEqualCount = GeoBlockSelector.getInstance().getSelectedTypesNotEqual(GeoEngine.GEO_BLOCK_TYPE_FLAT);
			if (notEqualCount > 0)
			{
				if (JOptionPane.showConfirmDialog(FrameMain.getInstance(), "Do you really want to convert the selected " + notEqualCount + " blocks to Flat?", "Convert to Flat", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					GeoBlockSelector.getInstance().convertSelectedToType(GeoEngine.GEO_BLOCK_TYPE_FLAT);
			}
		}
		else if (e.getSource() == _buttonConvertComplex)
		{
			final int notEqualCount = GeoBlockSelector.getInstance().getSelectedTypesNotEqual(GeoEngine.GEO_BLOCK_TYPE_COMPLEX);
			if (notEqualCount > 0)
			{
				if (JOptionPane.showConfirmDialog(FrameMain.getInstance(), "Do you really want to convert the selected " + notEqualCount + " blocks to Complex?", "Convert to Complex", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					GeoBlockSelector.getInstance().convertSelectedToType(GeoEngine.GEO_BLOCK_TYPE_COMPLEX);
			}
		}
		else if (e.getSource() == _buttonConvertMultiLayer)
		{
			final int notEqualCount = GeoBlockSelector.getInstance().getSelectedTypesNotEqual(GeoEngine.GEO_BLOCK_TYPE_MULTILAYER);
			if (notEqualCount > 0)
			{
				if (JOptionPane.showConfirmDialog(FrameMain.getInstance(), "Do you really want to convert the selected " + notEqualCount + " blocks to MultiLayer?", "Convert to MultiLayer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					GeoBlockSelector.getInstance().convertSelectedToType(GeoEngine.GEO_BLOCK_TYPE_MULTILAYER);
			}
		}
		else if (e.getSource() == _buttonRestoreBlock)
		{
			final int notEqualCount = GeoBlockSelector.getInstance().getSelectedDataNotEqualCount();
			if (notEqualCount > 0)
			{
				if (JOptionPane.showConfirmDialog(FrameMain.getInstance(), "Do you really want to restore the selected " + notEqualCount + " blocks?", "Restore from Base", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					GeoBlockSelector.getInstance().restoreSelectedData();
			}
		}
		
		GLDisplay.getInstance().requestFocus();
	}
}