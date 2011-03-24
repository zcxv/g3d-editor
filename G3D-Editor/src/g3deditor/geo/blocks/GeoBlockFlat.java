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
package g3deditor.geo.blocks;

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.geo.cells.GeoCellFlat;
import g3deditor.jogl.GLDisplay;
import g3deditor.util.GeoReader;
import g3deditor.util.GeoWriter;

/**
 * Flat block, 1 level, 1 height.<br>
 * 
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockFlat extends GeoBlock
{
	public static final GeoBlockFlat convertFrom(final GeoBlock block)
	{
		return new GeoBlockFlat(block);
	}
	
	private GeoCell[] _cells;
	
	public GeoBlockFlat(final GeoReader reader, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells = new GeoCell[]{new GeoCellFlat(this, reader.getShort())};
		
		if (!l2j)
			reader.getShort();
	}
	
	private GeoBlockFlat(final GeoBlock block)
	{
		super(block.getGeoX(), block.getGeoY());
		_cells = new GeoCell[]{new GeoCellFlat(this, block.getMinHeight())};
	}
	
	@Override
	public final byte getType()
	{
		return GeoEngine.GEO_BLOCK_TYPE_FLAT;
	}
	
	@Override
	public final int nGetLayer(final int geoX, final int geoY, final int z)
	{
		return 0;
	}
	
	@Override
	public final GeoCell nGetCellByLayer(final int geoX, final int geoY, final int layer)
	{
		return _cells[0];
	}
	
	@Override
	public final int nGetLayerCount(final int geoX, final int geoY)
	{
		return 1;
	}
	
	@Override
	public final GeoCell[] nGetLayers(final int geoX, final int geoY)
	{
		return new GeoCell[]{_cells[0]};
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#writeTo(g3deditor.util.GeoWriter, boolean)
	 */
	@Override
	public final void writeTo(final GeoWriter writer, final boolean l2j)
	{
		GeoRegion.putType(writer, l2j, getType());
		writer.putShort(_cells[0].getHeight());
		if (!l2j)
			writer.putShort(_cells[0].getHeight());
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#getRequiredCapacity(boolean)
	 */
	@Override
	public final int getRequiredCapacity(final boolean l2j)
	{
		return l2j ? 3 : 5;
	}
	
	@Override
	public final int getMaxLayerCount()
	{
		return 1;
	}
	
	@Override
	public final GeoCell addLayer(final int geoX, final int geoY, final short heightAndNSWE)
	{
		return null;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#removeCells(g3deditor.geo.GeoCell[])
	 */
	@Override
	public final int removeCells(final GeoCell... cells)
	{
		return 0;
	}
	
	/**
	 * @see g3deditor.content.geo.GeoBlock#getAllCells()
	 */
	@Override
	public final GeoCell[] getCells()
	{
		return _cells;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#getMinHeight()
	 */
	@Override
	public final short getMinHeight()
	{
		return _cells[0].getHeight();
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#getMaxHeight()
	 */
	@Override
	public final short getMaxHeight()
	{
		return getMinHeight();
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#updateMinMaxHeight(short)
	 */
	@Override
	public final void updateMinMaxHeight(final short newHeight, final short oldHeight)
	{
		if (newHeight != oldHeight)
			GLDisplay.getInstance().getTerrain().checkNeedUpdateVBO(true, true);
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#unload()
	 */
	@Override
	public final void unload()
	{
		for (int i = _cells.length; i-- > 0;)
		{
			_cells[i].unload();
			_cells[i] = null;
		}
		_cells = null;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#dataEquals(g3deditor.util.GeoReader)
	 */
	@Override
	public final boolean dataEquals(final GeoReader reader)
	{
		if (getType() != GeoRegion.getType(reader, true))
			return false;
		
		if (_cells[0].getHeight() != reader.getShort())
			return false;
		
		return true;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#updateLayerFor(g3deditor.geo.GeoCell)
	 */
	@Override
	public final void updateLayerFor(final GeoCell cell)
	{
		
	}
}