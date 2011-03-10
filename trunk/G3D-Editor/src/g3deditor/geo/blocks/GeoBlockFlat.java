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
import g3deditor.geo.cells.GeoCellFlat;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.inc.incolution.util.io.IncBufferedFileWriter;

/**
 * Flat block, 1 level, 1 height.<br>
 * 
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockFlat extends GeoBlock
{
	private final GeoCell[] _cells;
	
	public GeoBlockFlat(final ByteBuffer bb, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells = new GeoCell[]{new GeoCellFlat(this, bb.getShort())};
		
		if (!l2j)
			bb.getShort();
		
		calcMaxMinHeight();
	}
	
	public GeoBlockFlat(final GeoBlock block)
	{
		super(block.getGeoX(), block.getGeoY());
		_cells = new GeoCell[]{new GeoCellFlat(this, block.getMinHeight())};
		
		calcMaxMinHeight();
	}
	
	private GeoBlockFlat(final GeoBlockFlat block)
	{
		super(block.getGeoX(), block.getGeoY());
		_cells = new GeoCell[1];
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
	
	@Override
	public final void calcMaxMinHeight()
	{
		_maxHeight = _cells[0].getHeight();
		_minHeight = _maxHeight;
	}
	
	@Override
	public final GeoBlockFlat clone()
	{
		final GeoBlockFlat clone = new GeoBlockFlat(this);
		copyDataTo(clone);
		return clone;
	}
	
	public final void copyDataTo(final GeoBlockFlat block)
	{
		block._cells[0].setHeightAndNSWE(_cells[0].getHeightAndNSWE());
		block._maxHeight = _maxHeight;
		block._minHeight = _minHeight;
	}
	
	@Override
	public final void saveTo(final IncBufferedFileWriter writer, final boolean l2j) throws IOException
	{
		writer.writeByte(GeoEngine.GEO_BLOCK_TYPE_FLAT);
		writer.writeShort(_cells[0].getHeight());
		if (!l2j)
			writer.writeShort(_cells[0].getHeight());
	}
	
	@Override
	public final int getMaxLayerCount()
	{
		return 1;
	}
	
	@Override
	public final int addLayer(final int geoX, final int geoY, final short heightAndNSWE)
	{
		return -1;
	}
	
	@Override
	public final int removeLayer(final int geoX, final int geoY, final int layer)
	{
		return -1;
	}
	
	/**
	 * @see g3deditor.content.geo.GeoBlock#getAllCells()
	 */
	@Override
	public final GeoCell[] getCells()
	{
		return _cells;
	}
}