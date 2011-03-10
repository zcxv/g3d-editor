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
import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.blocks.GeoBlockFlat;
import g3deditor.swing.FrameMain;
import g3deditor.util.FastArrayList;

import java.util.concurrent.locks.ReentrantLock;

import javolution.util.FastMap;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockSelector
{
	private static GeoBlockSelector _instance;
	
	public static final void init()
	{
		_instance = new GeoBlockSelector();
	}
	
	public static final GeoBlockSelector getInstance()
	{
		return _instance;
	}
	
	private final ReentrantLock _lock;
	private final FastMap<GeoBlock, FastArrayList<GeoCell>> _selected;
	
	public GeoBlockSelector()
	{
		_lock = new ReentrantLock();
		_selected = new FastMap<GeoBlock, FastArrayList<GeoCell>>();
	}
	
	private final void setStateOf(final FastArrayList<GeoCell> cells, final SelectionState state)
	{
		for (int i = cells.size(); i-- > 0;)
		{
			cells.getUnsafe(i).setSelectionState(state);
		}
	}
	
	private final void setStateOf(final GeoCell[] cells, final SelectionState state)
	{
		for (int i = cells.length; i-- > 0;)
		{
			cells[i].setSelectionState(state);
		}
	}
	
	public final boolean forEachGeoCell(final ForEachGeoCellProcedure proc)
	{
		_lock.lock();
		
		try
		{
			FastArrayList<GeoCell> selected;
			for (FastMap.Entry<GeoBlock, FastArrayList<GeoCell>> e = _selected.head(), tail = _selected.tail(); (e = e.getNext()) != tail;)
			{
				selected = e.getValue();
				for (int i = selected.size(); i-- > 0;)
				{
					if (!proc.execute(selected.getUnsafe(i)))
						return false;
				}
			}
			return true;
		}
		finally
		{
			_lock.unlock();
		}
	}
	
	public final boolean forEachGeoBlock(final ForEachGeoBlockProcedure proc)
	{
		_lock.lock();
		
		try
		{
			for (FastMap.Entry<GeoBlock, FastArrayList<GeoCell>> e = _selected.head(), tail = _selected.tail(); (e = e.getNext()) != tail;)
			{
				if (!proc.execute(e.getKey()))
					return false;
			}
			return true;
		}
		finally
		{
			_lock.unlock();
		}
	}
	
	public final boolean isGeoCellSelected(final GeoCell cell)
	{
		return false;
	}
	
	public final void selectGeoCell(final GeoCell cell, boolean fullBlock, final boolean append)
	{
		final GeoCell guiSelected;
		
		_lock.lock();
		
		try
		{
			final GeoBlock block = cell.getBlock();
			final GeoCell[] cells = block.getCells();
			fullBlock |= block instanceof GeoBlockFlat;
			
			if (append)
			{
				FastArrayList<GeoCell> selected = _selected.get(block);
				if (selected != null)
				{
					if (selected.size() == cells.length)
					{
						if (fullBlock || selected.size() == 1)
						{
							setStateOf(selected, SelectionState.NORMAL);
							_selected.remove(block);
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
							setStateOf(selected, SelectionState.SELECTED);
						}
						else
						{
							if (selected.remove(cell))
							{
								if (selected.isEmpty())
								{
									setStateOf(cells, SelectionState.NORMAL);
									_selected.remove(block);
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
					_selected.put(block, selected);
				}
			}
			else
			{
				for (FastMap.Entry<GeoBlock, FastArrayList<GeoCell>> e = _selected.head(), tail = _selected.tail(); (e = e.getNext()) != tail;)
				{
					setStateOf(e.getKey().getCells(), SelectionState.NORMAL);
				}
				_selected.clear();
				
				final FastArrayList<GeoCell> selected;
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
				_selected.put(block, selected);
			}
			
			if (_selected.isEmpty())
			{
				guiSelected = null;
			}
			else
			{
				final FastArrayList<GeoCell> temp = _selected.get(block);
				if (temp != null)
				{
					guiSelected = temp.getUnsafe(temp.size() - 1);
				}
				else
				{
					guiSelected = null;
				}
			}
		}
		finally
		{
			_lock.unlock();
		}
		
		FrameMain.getInstance().setSelectedGeoCell(guiSelected);
	}
	
	public static interface ForEachGeoCellProcedure
	{
		public boolean execute(final GeoCell cell);
	}
	
	public static interface ForEachGeoBlockProcedure
	{
		public boolean execute(final GeoBlock cell);
	}
}