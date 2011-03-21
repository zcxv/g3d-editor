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
package g3deditor.swing.defaults;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public class DefaultTable extends JTable
{
	public DefaultTable(final DefaultTableModel model)
	{
		super(model);
	}
	
	public final void setColumnIdentifiers(final Object... newIdentifiers)
	{
		getModel().setColumnIdentifiers(newIdentifiers);
	}
	
	public final void setColumnCount(final int columnCount)
	{
		getModel().setColumnCount(columnCount);
	}
	
	@Override
	public final DefaultTableModel getModel()
	{
		return (DefaultTableModel) super.getModel();
	}
	
	public final void setRowCount(final int rows)
	{
		if (getModel().getRowCount() != rows)
			getModel().setRowCount(rows);
	}
	
	public final void setColumnMinMaxWidth(final int columnIndex, final int minWidth, final int maxWidth)
	{
		final TableColumn tc = super.getColumnModel().getColumn(columnIndex);
		tc.setMinWidth(minWidth);
		tc.setMaxWidth(maxWidth);
	}
	
	public final void setColumnMinWidth(final int columnIndex, final int minWidth)
	{
		getColumnModel().getColumn(columnIndex).setMinWidth(minWidth);
	}
	
	public final void setSelectionInterval(final int index)
	{
		if (getSelectionModel().getMaxSelectionIndex() != index)
			getSelectionModel().setSelectionInterval(index, index);
	}
	
	@Override
	public final void setValueAt(final Object val, final int row, final int column)
	{
		if (getValueAt(row, column) != val)
			super.setValueAt(val, row, column);
	}
	
	@Override
	public final void setEnabled(final boolean enabled)
	{
		if (isEnabled() != enabled)
			super.setEnabled(enabled);
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (isVisible() != visible)
			super.setVisible(visible);
	}
	
	public static final class ComponentCellRendererEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
	{
		public static final ComponentCellRendererEditor STATIC_COMPONENT_CELL_RENDERER_EDITOR = new ComponentCellRendererEditor();
		
		private Component _lastValue;
		
		private ComponentCellRendererEditor()
		{
			
		}
		
		@Override
		public final Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
		{
			_lastValue = (Component) value;
			return _lastValue;
		}
		
		@Override
		public final Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
		{
			return (Component) value;
		}
		
		@Override
		public final Object getCellEditorValue()
		{
			return _lastValue;
		}
	}
}