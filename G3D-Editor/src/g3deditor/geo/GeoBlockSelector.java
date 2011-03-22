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
package g3deditor.geo;

import g3deditor.entity.SelectionState;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.GLSelectionBox;
import g3deditor.swing.FrameMain;
import g3deditor.util.FastArrayList;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockSelector
{
	private static final void setStateOf(final FastArrayList<GeoCell> cells, final SelectionState state)
	{
		for (int i = cells.size(); i-- > 0;)
		{
			cells.getUnsafe(i).setSelectionState(state);
		}
	}
	
	private static final void setStateOf(final GeoCell[] cells, final SelectionState state)
	{
		for (int i = cells.length; i-- > 0;)
		{
			cells[i].setSelectionState(state);
		}
	}
	
	private static GeoBlockSelector _instance;
	
	public static final void init()
	{
		_instance = new GeoBlockSelector();
	}
	
	public static final GeoBlockSelector getInstance()
	{
		return _instance;
	}
	
	private final GeoBlockEntry[] _selected;
	private final GeoBlockEntry _head;
	private final GeoBlockEntry _tail;
	private final FastArrayList<GeoCell> _temp;
	
	public GeoBlockSelector()
	{
		_selected = new GeoBlockEntry[GeoEngine.GEO_REGION_SIZE * GeoEngine.GEO_REGION_SIZE];
		for (int i = _selected.length; i-- > 0;)
		{
			_selected[i] = new GeoBlockEntry();
		}
		_head = new GeoBlockEntry();
		_tail = new GeoBlockEntry();
		getHead().setPrev(getHead());
		getHead().setNext(getTail());
		getTail().setPrev(getHead());
		getTail().setNext(getTail());
		_temp = new FastArrayList<GeoCell>();
	}
	
	private final GeoBlockEntry getEntry(final GeoBlock block)
	{
		return _selected[block.getBlockX() * GeoEngine.GEO_REGION_SIZE + block.getBlockY()];
	}
	
	public final GeoBlockEntry getHead()
	{
		return _head;
	}
	
	public final GeoBlockEntry getTail()
	{
		return _tail;
	}
	
	public final boolean hasSelected()
	{
		return getHead().getNext() != getTail();
	}
	
	public final boolean isGeoCellSelected(final GeoCell cell)
	{
		final FastArrayList<GeoCell> selected = getEntry(cell.getBlock()).getValue();
		return selected != null && selected.contains(cell);
	}
	
	private final void unselectAll()
	{
		for (GeoBlockEntry e = getHead(), p; (e = e.getNext()) != getTail();)
		{
			setStateOf(e.getKey().getCells(), SelectionState.NORMAL);
			p = e.getPrev();
			e.remove();
			e = p;
		}
	}
	
	private final void selectGeoCellFlat(final GeoCell cell, final boolean append)
	{
		final GeoBlock block = cell.getBlock();
		final GeoBlockEntry entry = getEntry(block);
		FastArrayList<GeoCell> selected = entry.getValue();
		
		if (append)
		{
			if (selected != null)
			{
				cell.setSelectionState(SelectionState.NORMAL);
				entry.remove();
				return;
			}
		}
		else
		{
			unselectAll();
			selected = null;
		}
		
		if (selected == null)
		{
			cell.setSelectionState(SelectionState.SELECTED);
			selected = new FastArrayList<GeoCell>(block.getCells(), true);
			entry.setKey(block);
			entry.setValue(selected);
			entry.addBefore(getTail());
		}
	}
	
	private final void selectGeoCellComplex(final GeoCell cell, final boolean fullBlock, final boolean append)
	{
		final GeoBlock block = cell.getBlock();
		final GeoBlockEntry entry = getEntry(block);
		final GeoCell[] cells = block.getCells();
		FastArrayList<GeoCell> selected = entry.getValue();
		
		if (append)
		{
			if (selected != null)
			{
				if (selected.size() == cells.length)
				{
					if (fullBlock)
					{
						setStateOf(cells, SelectionState.NORMAL);
						entry.remove();
					}
					else
					{
						if (selected.remove(cell))
							cell.setSelectionState(SelectionState.HIGHLIGHTED);
					}
				}
				else
				{
					if (fullBlock)
					{
						selected.clear();
						selected.addAll(cells);
						setStateOf(cells, SelectionState.SELECTED);
					}
					else
					{
						if (selected.remove(cell))
						{
							if (selected.isEmpty())
							{
								setStateOf(cells, SelectionState.NORMAL);
								entry.remove();
							}
							else
							{
								cell.setSelectionState(SelectionState.HIGHLIGHTED);
							}
						}
						else
						{
							selected.addLast(cell);
							cell.setSelectionState(SelectionState.SELECTED);
						}
					}
				}
			}
			else
			{
				if (fullBlock)
				{
					selected = new FastArrayList<GeoCell>(cells, true);
				}
				else
				{
					selected = new FastArrayList<GeoCell>(8);
					selected.addLastUnsafe(cell);
					setStateOf(cells, SelectionState.HIGHLIGHTED);
				}
				
				setStateOf(selected, SelectionState.SELECTED);
				entry.setKey(block);
				entry.setValue(selected);
				entry.addBefore(getTail());
			}
		}
		else
		{
			unselectAll();
			
			if (fullBlock)
			{
				selected = new FastArrayList<GeoCell>(cells, true);
			}
			else
			{
				selected = new FastArrayList<GeoCell>(8);
				selected.addLastUnsafe(cell);
				setStateOf(cells, SelectionState.HIGHLIGHTED);
			}
			
			setStateOf(selected, SelectionState.SELECTED);
			entry.setKey(block);
			entry.setValue(selected);
			entry.addBefore(getTail());
		}
	}
	
	private final void selectGeoCellMultiLevel(final GeoCell cell, final boolean fullBlock, final boolean append)
	{
		final GeoBlock block = cell.getBlock();
		final GeoBlockEntry entry = getEntry(block);
		final GeoCell[] cells = block.getCells();
		final GLSelectionBox selectionBox = GLDisplay.getInstance().getSelectionBox();
		FastArrayList<GeoCell> selected = entry.getValue();
		
		if (append)
		{
			if (selected != null)
			{
				if (fullBlock)
				{
					if (selectionBox.isInfHeight())
					{
						if (selected.size() == cells.length)
						{
							setStateOf(cells, SelectionState.NORMAL);
							entry.remove();
						}
						else
						{
							selected.clear();
							selected.addAll(cells);
							setStateOf(cells, SelectionState.SELECTED);
						}
					}
					else
					{
						_temp.clear();
						selectionBox.getAllCellsInside(cells, _temp);
						
						if (selected.containsAll(_temp))
						{
							if (selected.size() == _temp.size())
							{
								setStateOf(cells, SelectionState.NORMAL);
								entry.remove();
							}
							else
							{
								setStateOf(_temp, SelectionState.NORMAL);
								selected.removeAll(_temp);
							}
						}
						else
						{
							setStateOf(_temp, SelectionState.SELECTED);
							selected.addAllIfAbsent(_temp);
						}
					}
				}
				else
				{
					if (selected.remove(cell))
					{
						if (selected.isEmpty())
						{
							setStateOf(cells, SelectionState.NORMAL);
							entry.remove();
						}
						else
						{
							cell.setSelectionState(SelectionState.HIGHLIGHTED);
						}
					}
					else
					{
						selected.addLast(cell);
						cell.setSelectionState(SelectionState.SELECTED);
					}
				}
			}
			else
			{
				if (fullBlock)
				{
					if (selectionBox.isInfHeight())
					{
						selected = new FastArrayList<GeoCell>(cells, true);
					}
					else
					{
						selected = new FastArrayList<GeoCell>();
						selectionBox.getAllCellsInside(cells, selected);
					}
				}
				else
				{
					selected = new FastArrayList<GeoCell>(8);
					selected.addLastUnsafe(cell);
					setStateOf(cells, SelectionState.HIGHLIGHTED);
				}
				
				if (!selected.isEmpty())
				{
					setStateOf(selected, SelectionState.SELECTED);
					entry.setKey(block);
					entry.setValue(selected);
					entry.addBefore(getTail());
				}
			}
		}
		else
		{
			unselectAll();
			
			if (fullBlock)
			{
				if (selectionBox.isInfHeight())
				{
					selected = new FastArrayList<GeoCell>(cells, true);
				}
				else
				{
					selected = new FastArrayList<GeoCell>();
					selectionBox.getAllCellsInside(cells, selected);
				}
			}
			else
			{
				selected = new FastArrayList<GeoCell>(8);
				selected.addLastUnsafe(cell);
				setStateOf(cells, SelectionState.HIGHLIGHTED);
			}
			
			if (!selected.isEmpty())
			{
				setStateOf(selected, SelectionState.SELECTED);
				entry.setKey(block);
				entry.setValue(selected);
				entry.addBefore(getTail());
			}
		}
	}
	
	public final void selectGeoCell(final GeoCell cell, boolean fullBlock, final boolean append)
	{
		switch (cell.getBlock().getType())
		{
			case GeoEngine.GEO_BLOCK_TYPE_FLAT:
				selectGeoCellFlat(cell, append);
				break;
				
			case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
				selectGeoCellComplex(cell, fullBlock, append);
				break;
				
			case GeoEngine.GEO_BLOCK_TYPE_MULTILEVEL:
				selectGeoCellMultiLevel(cell, fullBlock, append);
				break;
		}
		
		if (!hasSelected())
		{
			FrameMain.getInstance().setSelectedGeoCell(null);
		}
		else
		{
			FastArrayList<GeoCell> selected = getEntry(cell.getBlock()).getValue();
			if (selected == null)
				selected = getTail().getPrev().getValue();
			
			if (selected.contains(cell))
			{
				FrameMain.getInstance().setSelectedGeoCell(cell);
			}
			else
			{
				FrameMain.getInstance().setSelectedGeoCell(selected.getLastUnsafe());
			}
		}
	}
	
	public final void checkDeselection(final int minBlockX, final int maxBlockX, final int minBlockY, final int maxBlockY)
	{
		GeoCell cell = FrameMain.getInstance().getSelectedGeoCell();
		
		GeoBlock block;
		for (GeoBlockEntry e = getHead(), p; (e = e.getNext()) != getTail();)
		{
			block = e.getKey();
			if (block.getBlockX() < minBlockX || block.getBlockX() >= maxBlockX || block.getBlockY() < minBlockY || block.getBlockY() >= maxBlockY)
			{
				if (cell != null && cell.getBlock() == block)
				{
					cell = null;
					FrameMain.getInstance().setSelectedGeoCell(null);
				}
					
				setStateOf(block.getCells(), SelectionState.NORMAL);
				p = e.getPrev();
				e.remove();
				e = p;
			}
		}
		
		if (cell == null && hasSelected())
			FrameMain.getInstance().setSelectedGeoCell(getTail().getPrev().getValue().getLastUnsafe());
	}
	
	public final void unload()
	{
		for (GeoBlockEntry e = getHead(), p; (e = e.getNext()) != getTail();)
		{
			p = e.getPrev();
			e.remove();
			e = p;
		}
		
		FrameMain.getInstance().setSelectedGeoCell(null);
	}
	
	public static final class GeoBlockEntry
	{
		private GeoBlockEntry _next;
		private GeoBlockEntry _prev;
		private GeoBlock _key;
		private FastArrayList<GeoCell> _value;
		
		public GeoBlockEntry()
		{
			
		}
		
		public final GeoBlockEntry getNext()
		{
			return _next;
		}
		
		public final void setNext(final GeoBlockEntry entry)
		{
			_next = entry;
		}
		
		public final GeoBlockEntry getPrev()
		{
			return _prev;
		}
		
		public final void setPrev(final GeoBlockEntry entry)
		{
			_prev = entry;
		}
		
		public final void remove()
		{
			getPrev().setNext(getNext());
			getNext().setPrev(getPrev());
			setKey(null);
			setValue(null);
		}
		
		public final void addBefore(final GeoBlockEntry entry)
		{
			setPrev(entry.getPrev());
			setNext(entry);
			
			entry.getPrev().setNext(this);
			entry.setPrev(this);
		}
		
		public final GeoBlock getKey()
		{
			return _key;
		}
		
		public final void setKey(final GeoBlock key)
		{
			_key = key;
		}
		
		public final FastArrayList<GeoCell> getValue()
		{
			return _value;
		}
		
		public final void setValue(final FastArrayList<GeoCell> value)
		{
			_value = value;
		}
	}
}