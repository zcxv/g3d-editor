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
import g3deditor.swing.defaults.DefaultButton;
import g3deditor.swing.defaults.DefaultTable;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public final class PanelLayers extends JPanel implements ActionListener
{
	private final CellLayerTable _tableLayers;
	private final JScrollPane _paneLayers;
	private final DefaultButton _buttonLayerAdd;
	private final DefaultButton _buttonLayerRemove;
	
	private final DialogAddLayers _dialogAddLayer;
	private final DialogRemoveLayers _dialogRemoveLayer;
	
	public PanelLayers(final FrameMain frame)
	{
		_tableLayers = new CellLayerTable();
		_paneLayers = new JScrollPane(_tableLayers);
		_paneLayers.setPreferredSize((Dimension) _tableLayers.getPreferredSize().clone());
		
		_buttonLayerAdd = new DefaultButton("Add Layer(s)");
		_buttonLayerAdd.setEnabled(false);
		_buttonLayerAdd.addActionListener(this);
		_buttonLayerRemove = new DefaultButton("Remove Layer(s)");
		_buttonLayerRemove.setEnabled(false);
		_buttonLayerRemove.addActionListener(this);
		
		_dialogAddLayer = new DialogAddLayers(frame);
		_dialogRemoveLayer = new DialogRemoveLayers(frame);
		
		initLayout();
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		setBorder(BorderFactory.createTitledBorder("Cell Layers"));
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(_paneLayers, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_buttonLayerAdd, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(_buttonLayerRemove, gbc);
	}
	
	private final void setFieldsEnabled(boolean enabled)
	{
		_tableLayers.setEnabled(enabled);
		
		enabled &= GeoBlockSelector.getInstance().getSelectedTypes()[GeoEngine.GEO_BLOCK_TYPE_MULTILAYER];
		_buttonLayerAdd.setEnabled(enabled);
		_buttonLayerRemove.setEnabled(enabled);
	}
	
	public final void onSelectedCellUpdated()
	{
		final GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		if (cell == null)
		{
			setFieldsEnabled(false);
			_tableLayers.updateValues(null, null);
		}
		else
		{
			setFieldsEnabled(true);
			_tableLayers.updateValues(cell.getBlock().nGetLayers(cell.getGeoX(), cell.getGeoY()), cell);
		}
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonLayerAdd)
		{
			_dialogAddLayer.setVisible(true);
		}
		else if (e.getSource() == _buttonLayerRemove)
		{
			_dialogRemoveLayer.setVisible(true);
		}
	}
	
	private final class CellLayerTable extends DefaultTable implements MouseListener
	{
		public CellLayerTable()
		{
			super(new DefaultTableModel()
			{
				@Override
				public final boolean isCellEditable(final int row, final int column)
				{
					return false;
				}
			});
			
			setColumnIdentifiers("Layer", "Height");
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			setColumnMinMaxWidth(0, 40, 40);
			setColumnMinWidth(1, 50);
			addMouseListener(this);
		}
		
		public final void updateValues(GeoCell[] layers, final GeoCell selected)
		{
			if (layers == null)
				layers = GeoCell.EMPTY_ARRAY;
			
			setRowCount(layers.length);
			
			int layer = -1, i;
			for (i = 0; i < layers.length; i++)
			{
				if (selected == layers[i])
					layer = i;
				
				setValueAt(i, i, 0);
				setValueAt(layers[i].getHeight(), i, 1);
			}
			
			if (layers.length > 0 && layer < layers.length)
				setSelectionInterval(layer);
		}
		
		@Override
		public final void mouseClicked(final MouseEvent e)
		{
			final int row = rowAtPoint(new Point(e.getX(), e.getY()));
			// TODO what is this supposed to be?
			if (row > -1)
			{
				
			}
			else
			{
				
			}
		}
		
		@Override
		public final void mouseEntered(final MouseEvent e)
		{
			
		}
		
		@Override
		public final void mouseExited(final MouseEvent e)
		{
			
		}
		
		@Override
		public final void mousePressed(final MouseEvent e)
		{
			
		}
		
		@Override
		public final void mouseReleased(final MouseEvent e)
		{
			
		}
	}
}