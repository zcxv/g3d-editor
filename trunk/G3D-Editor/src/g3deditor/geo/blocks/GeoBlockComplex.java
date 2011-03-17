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
import g3deditor.geo.cells.GeoCellCM;
import g3deditor.swing.FrameMain;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.inc.incolution.util.io.IncBufferedFileWriter;

/**
 * Complex block, 1 level, 64 heights (each cell in block).<br>
 * 
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockComplex extends GeoBlock
{
	private static final int indexOf(final int x, final int y)
	{
		return x * GeoEngine.GEO_BLOCK_SHIFT + y;
	}
	
	private final GeoCell[] _cells;
	private short _minHeight;
	private short _maxHeight;
	
	public GeoBlockComplex(final ByteBuffer bb, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				_cells[indexOf(x, y)] = new GeoCellCM(this, bb.getShort(), x, y);
			}
		}
		calcMaxMinHeight();
	}
	
	public GeoBlockComplex(final GeoBlockFlat block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				_cells[indexOf(x, y)] = new GeoCellCM(this, GeoEngine.convertHeightToHeightAndNSWEALL(block.getMinHeight()), x, y);
			}
		}
		calcMaxMinHeight();
	}
	
	public GeoBlockComplex(final GeoBlockMultiLevel block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				_cells[indexOf(x, y)] = new GeoCellCM(this, block.nGetCellByLayer(x, y, block.nGetLayerCount(x, y) - 1).getHeightAndNSWE(), x, y);
			}
		}
		calcMaxMinHeight();
	}
	
	private GeoBlockComplex(final GeoBlockComplex block)
	{
		super(block.getGeoX(), block.getGeoY());
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
	}
	
	@Override
	public final byte getType()
	{
		return GeoEngine.GEO_BLOCK_TYPE_COMPLEX;
	}
	
	@Override
	public final int nGetLayer(final int geoX, final int geoY, final int z)
	{
		return 0;
	}
	
	@Override
	public final GeoCell nGetCellByLayer(final int geoX, final int geoY, final int layer)
	{
		final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		return _cells[indexOf(cellX, cellY)];
	}
	
	@Override
	public final int nGetLayerCount(final int geoX, final int geoY)
	{
		return 1;
	}

	@Override
	public final GeoCell[] nGetLayers(final int geoX, final int geoY)
	{
		final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		return new GeoCell[]{_cells[indexOf(cellX, cellY)]};
	}
	
	public final void calcMaxMinHeight()
	{
		short minHeight = Short.MAX_VALUE, maxHeight = Short.MIN_VALUE;
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				final GeoCell cell = _cells[indexOf(x, y)];
				minHeight = (short) Math.min(cell.getHeight(), minHeight);
				maxHeight = (short) Math.max(cell.getHeight(), maxHeight);
			}
		}
		_minHeight = minHeight;
		_maxHeight = maxHeight;
	}
	
	@Override
	public final GeoBlockComplex clone()
	{
		final GeoBlockComplex clone = new GeoBlockComplex(this);
		copyDataTo(clone);
		return clone;
	}
	
	public final void copyDataTo(final GeoBlockComplex block)
	{
		for (int x = GeoEngine.GEO_BLOCK_SHIFT, y, i; x-- > 0;)
		{
			for (y = GeoEngine.GEO_BLOCK_SHIFT; y-- > 0;)
			{
				i = indexOf(x, y);
				block._cells[i].setHeightAndNSWE(_cells[i].getHeightAndNSWE());
			}
		}
		block._maxHeight = _maxHeight;
		block._minHeight = _minHeight;
	}

	@Override
	public final void saveTo(final IncBufferedFileWriter writer, final boolean l2j) throws IOException
	{
		writer.writeByte(GeoEngine.GEO_BLOCK_TYPE_COMPLEX);
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				writer.writeShort(_cells[indexOf(x, y)].getHeightAndNSWE());
			}
		}
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
	
	/**
	 * @see g3deditor.geo.GeoBlock#getMinHeight()
	 */
	@Override
	public final short getMinHeight()
	{
		return _minHeight;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#getMaxHeight()
	 */
	@Override
	public final short getMaxHeight()
	{
		return _maxHeight;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#updateMinMaxHeight(short)
	 */
	@Override
	public final void updateMinMaxHeight(final short newHeight, final short oldHeight)
	{
		if (newHeight > _maxHeight)
		{
			_maxHeight = newHeight;
		}
		else if (newHeight < _minHeight)
		{
			_minHeight = newHeight;
		}
		else if (oldHeight == _maxHeight || oldHeight == _minHeight)
		{
			calcMaxMinHeight();
		}
		else
		{
			return;
		}
		
		FrameMain.getInstance().getDisplay().getTerrain().setNeedUpdateVBO();
	}
}