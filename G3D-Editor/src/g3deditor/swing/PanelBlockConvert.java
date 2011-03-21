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
import g3deditor.swing.defaults.DefaultButton;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
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
		_buttonConvertFlat.setEnabled(enabled);
		_buttonConvertComplex.setEnabled(enabled);
		_buttonConvertMultiLayer.setEnabled(enabled);
		_buttonRestoreBlock.setEnabled(enabled);
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
		if (e.getSource() == _buttonConvertFlat)
		{
			
		}
		else if (e.getSource() == _buttonConvertComplex)
		{
			
		}
		else if (e.getSource() == _buttonConvertMultiLayer)
		{
			
		}
		else if (e.getSource() == _buttonRestoreBlock)
		{
			
		}
	}
}