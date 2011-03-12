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
import g3deditor.swing.defaults.DefaultTable;
import g3deditor.util.Util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public final class PanelLayers extends JPanel implements ActionListener
{
	private final CellLayerTable _tableLayers;
	private final JScrollPane _paneLayers;
	private final JButton _buttonLayerAdd;
	private final JButton _buttonLayerRemove;
	
	public PanelLayers()
	{
		_tableLayers = new CellLayerTable();
		_paneLayers = new JScrollPane(_tableLayers);
		
		_buttonLayerAdd = new JButton("Add Layer(s)");
		_buttonLayerAdd.setEnabled(false);
		_buttonLayerAdd.addActionListener(this);
		_buttonLayerRemove = new JButton("Remove Layer(s)");
		_buttonLayerRemove.setEnabled(false);
		_buttonLayerRemove.addActionListener(this);
		
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
	
	private final void setFieldsEnabled(final boolean enabled)
	{
		_paneLayers.setEnabled(enabled);
		_buttonLayerAdd.setEnabled(enabled);
		_buttonLayerRemove.setEnabled(enabled);
	}
	
	public final void onSelectedCellUpdated()
	{
		final GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		if (cell == null)
		{
			setFieldsEnabled(false);
			_tableLayers.updateValues(null, -1);
		}
		else
		{
			setFieldsEnabled(true);
			final GeoCell[] layers = cell.getBlock().nGetLayers(cell.getGeoX(), cell.getGeoY());
			_tableLayers.updateValues(layers, Util.arrayIndexOf(layers, cell));
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
			
		}
		else if (e.getSource() == _buttonLayerRemove)
		{
			
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
			
			super.setColumnIdentifiers("Layer", "Height");
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.setColumnSelectionAllowed(false);
			super.setRowSelectionAllowed(true);
			super.setColumnMinMaxWidth(0, 40, 40);
			super.setColumnMinWidth(1, 50);
			super.addMouseListener(this);
		}
		
		public final void updateValues(GeoCell[] layers, final int layer)
		{
			if (layers == null)
				layers = GeoCell.EMPTY_ARRAY;
			
			super.setRowCount(layers.length);
			for (int i = 0; i < layers.length; i++)
			{
				super.setValueAt(i, i, 0);
				super.setValueAt(layers[i].getHeight(), i, 1);
			}
			
			if (layers.length > 0 && layer < layers.length)
				super.getSelectionModel().setSelectionInterval(layer, layer);
		}
		
		@Override
		public final void mouseClicked(final MouseEvent e)
		{
			final int row = rowAtPoint(new Point(e.getX(), e.getY()));
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