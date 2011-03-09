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
package g3deditor.entity;

import java.awt.Color;

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.blocks.GeoBlockComplex;
import g3deditor.geo.blocks.GeoBlockFlat;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public enum SelectionState
{
	NORMAL		(new CellColor(Color.BLUE), new CellColor(Color.GREEN), new CellColor(Color.RED)),
	HIGHLIGHTED	(new CellColor(Color.CYAN), new CellColor(Color.CYAN), new CellColor(Color.CYAN)),
	SELECTED	(new CellColor(Color.MAGENTA), new CellColor(Color.MAGENTA), new CellColor(Color.MAGENTA));
	
	private final CellColor _colorFlat;
	private final CellColor _colorComplex;
	private final CellColor _colorMutliLayer;
	
	private SelectionState(final CellColor colorFlat, final CellColor colorComplex, final CellColor colorMutliLayer)
	{
		_colorFlat = colorFlat;
		_colorComplex = colorComplex;
		_colorMutliLayer = colorMutliLayer;
	}
	
	public final CellColor getColor(final GeoCell cell)
	{
		return getColor(cell.getBlock());
	}
	
	public final CellColor getColor(final GeoBlock block)
	{
		if (block instanceof GeoBlockFlat)
			return _colorFlat;
		
		if (block instanceof GeoBlockComplex)
			return _colorComplex;
		
		return _colorMutliLayer;
	}
}