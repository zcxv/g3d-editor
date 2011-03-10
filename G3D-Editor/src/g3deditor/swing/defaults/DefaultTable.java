package g3deditor.swing.defaults;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * 
 * @author Forsaiken
 *
 */
@SuppressWarnings("serial")
public class DefaultTable extends JTable
{
	public DefaultTable(final DefaultTableModel model)
	{
		super(model);
		super.setIgnoreRepaint(true);
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
		super.getColumnModel().getColumn(columnIndex).setMinWidth(minWidth);
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