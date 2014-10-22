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
import g3deditor.geo.cells.GeoCellCM;
import g3deditor.jogl.GLDisplay;
import g3deditor.util.GeoReader;
import g3deditor.util.GeoWriter;

/**
 * Complex block, 1 level, 64 heights (each cell in block).<br>
 * 
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockComplex extends GeoBlock
{
	public static final GeoBlockComplex convertFrom(final GeoBlock block)
	{
		if (block instanceof GeoBlockFlat)
			return new GeoBlockComplex((GeoBlockFlat) block);
		
		if (block instanceof GeoBlockComplex)
			return new GeoBlockComplex((GeoBlockComplex) block);
		
		return new GeoBlockComplex((GeoBlockMultiLayer) block);
	}
	
	private static final int indexOf(final int x, final int y)
	{
		return x * GeoEngine.GEO_BLOCK_SHIFT + y;
	}
	
	private GeoCell[] _cells;
	private short _minHeight;
	private short _maxHeight;
	
	public GeoBlockComplex(final GeoReader reader, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				_cells[indexOf(x, y)] = new GeoCellCM(this, reader.getShort(), x, y);
			}
		}
		calcMaxMinHeight();
	}
	
	private GeoBlockComplex(final GeoBlockFlat block)
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
	
	private GeoBlockComplex(final GeoBlockComplex block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				_cells[indexOf(x, y)] = new GeoCellCM(this, block.nGetCellByLayer(x, y, 0).getHeightAndNSWE(), x, y);
			}
		}
		calcMaxMinHeight();
	}
	
	private GeoBlockComplex(final GeoBlockMultiLayer block)
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
	public final void writeTo(final GeoWriter writer, final boolean l2j)
	{
		GeoRegion.putType(writer, l2j, getType());
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				writer.putShort(_cells[indexOf(x, y)].getHeightAndNSWE());
			}
		}
	}
	
	@Override
	public final int getRequiredCapacity(final boolean l2j)
	{
		return GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT * 2 + (l2j ? 1 : 2);
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
	
	@Override
	public final int removeCells(final GeoCell... cells)
	{
		return 0;
	}
	
	@Override
	public final GeoCell[] getCells()
	{
		return _cells;
	}
	
	@Override
	public final short getMinHeight()
	{
		return _minHeight;
	}
	
	@Override
	public final short getMaxHeight()
	{
		return _maxHeight;
	}
	
	@Override
	public final void updateMinMaxHeight(final short newHeight, final short oldHeight)
	{
		if (newHeight > _maxHeight)
		{
			_maxHeight = newHeight;
			GLDisplay.getInstance().getTerrain().checkNeedUpdateVBO(false, true);
		}
		else if (newHeight < _minHeight)
		{
			_minHeight = newHeight;
			GLDisplay.getInstance().getTerrain().checkNeedUpdateVBO(true, false);
		}
		else if (oldHeight == _maxHeight || oldHeight == _minHeight)
		{
			final int oldMinHeight = _minHeight;
			final int oldMaxHeight = _maxHeight;
			calcMaxMinHeight();
			GLDisplay.getInstance().getTerrain().checkNeedUpdateVBO(_minHeight != oldMinHeight, _maxHeight != oldMaxHeight);
		}
	}
	
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
	
	@Override
	public final boolean dataEquals(final GeoReader reader)
	{
		if (getType() != GeoRegion.getType(reader, true))
			return false;
		
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				if (_cells[indexOf(x, y)].getHeightAndNSWE() != reader.getShort())
					return false;
			}
		}
		return true;
	}
	
	@Override
	public final void updateLayerFor(final GeoCell cell)
	{
		
	}
}