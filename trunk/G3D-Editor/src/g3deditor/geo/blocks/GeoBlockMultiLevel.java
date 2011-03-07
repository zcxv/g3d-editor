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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.inc.incolution.util.io.IncBufferedFileWriter;

/**
 * MultiLevel block, x levels, 64 heights + y (each cell can have multiple heights).<br>
 * 
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockMultiLevel extends GeoBlock
{
	private final GeoCell[][][] _cells3D;
	private GeoCell[] _cells;
	
	private final void copyCells()
	{
		int count = 0;
		for (final GeoCell[][] cells2D : _cells3D)
		{
			for (final GeoCell[] cells1D : cells2D)
			{
				count += cells1D.length;
			}
		}
		copyCells(count);
	}
	
	private final void copyCells(int count)
	{
		_cells = new GeoCell[count];
		for (final GeoCell[][] cells2D : _cells3D)
		{
			for (final GeoCell[] cells1D : cells2D)
			{
				for (final GeoCell cell : cells1D)
				{
					_cells[--count] = cell;
				}
			}
		}
	}
	
	public GeoBlockMultiLevel(final ByteBuffer bb, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][];
		
		int layers, count = 0;
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				layers = l2j ? bb.get() : bb.getShort();
				if (!GeoEngine.layersValid(layers))
					throw new RuntimeException("Invalid layer count " + layers);
				
				count += layers;
				_cells3D[x][y] = new GeoCell[layers];
				for (int i = layers; i-- > 0;)
				{
					_cells3D[x][y][i] = new GeoCell(this, bb.getShort(), x, y);
				}
			}
		}
		
		copyCells(count);
		calcMaxMinHeight();
	}
	
	public GeoBlockMultiLevel(final GeoBlockFlat block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][1];
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				final GeoCell cell = new GeoCell(this, GeoEngine.convertHeightToHeightAndNSWEALL(block.getMinHeight()), x, y);
				_cells3D[x][y][0] = cell;
				_cells[x * GeoEngine.GEO_BLOCK_SHIFT + y] =cell; 
			}
		}
		calcMaxMinHeight();
	}
	
	public GeoBlockMultiLevel(final GeoBlockComplex block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][1];
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				final GeoCell cell = new GeoCell(this, block.nGetCellByLayer(x, y, 0).getHeightAndNSWE(), x, y);
				_cells3D[x][y][0] = cell;
				_cells[x * GeoEngine.GEO_BLOCK_SHIFT + y] =cell; 
			}
		}
		calcMaxMinHeight();
	}
	
	private GeoBlockMultiLevel(final GeoBlockMultiLevel block)
	{
		super(block.getGeoX(), block.getGeoX());
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][1];
	}
	
	@Override
	public final byte getType()
	{
		return GeoEngine.GEO_BLOCK_TYPE_MULTILEVEL;
	}
	
	@Override
	public final int nGetLayer(final int geoX, final int geoY, final int z)
	{
		final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		final GeoCell[] heights = _cells3D[cellX][cellY];
		
		short temp;
		int layer = 0, sub1, sub1Sq, sub2Sq = Integer.MAX_VALUE;
		// from highest z (layer) to lowest z (layer)
		for (int i = heights.length; i-- > 0;)
		{
			temp = heights[i].getHeightAndNSWE();
			sub1 = z - GeoEngine.getHeight(temp);
			sub1Sq = sub1 * sub1;
			if (sub1Sq < sub2Sq)
			{
				sub2Sq = sub1Sq;
				layer = i;
			}
			else
			{
				break;
			}
		}
		return layer;
	}
	
	@Override
	public final GeoCell nGetCellByLayer(final int geoX, final int geoY, final int layer)
	{
		final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		return _cells3D[cellX][cellY][layer];
	}
	
	@Override
	public final int nGetLayerCount(final int geoX, final int geoY)
	{
		final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		return _cells3D[cellX][cellY].length;
	}
	
	@Override
	public final GeoCell[] nGetLayers(final int geoX, final int geoY)
	{
		final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		final GeoCell[] layers = _cells3D[cellX][cellY];
		return Arrays.copyOf(layers, layers.length);
	}
	
	@Override
	public final void calcMaxMinHeight()
	{
		GeoCell[] heights;
		short height;
		short minHeight = Short.MAX_VALUE, maxHeight = Short.MIN_VALUE;
		for (int x = GeoEngine.GEO_BLOCK_SHIFT, y, z; x-- > 0;)
		{
			for (y = GeoEngine.GEO_BLOCK_SHIFT; y-- > 0;)
			{
				heights = _cells3D[x][y];
				for (z = heights.length; z-- > 0;)
				{
					height = heights[z].getHeight();
					minHeight = (short) Math.min(height, minHeight);
					maxHeight = (short) Math.max(height, maxHeight);
				}
			}
		}
		_minHeight = minHeight;
		_maxHeight = maxHeight;
	}
	
	@Override
	public final GeoBlockMultiLevel clone()
	{
		final GeoBlockMultiLevel clone = new GeoBlockMultiLevel(this);
		copyDataTo(clone);
		return clone;
	}
	
	public final void copyDataTo(final GeoBlockMultiLevel block)
	{
		GeoCell[] layers, layersCopy;
		for (int x = GeoEngine.GEO_BLOCK_SHIFT, y, z; x-- > 0;)
		{
			for (y = GeoEngine.GEO_BLOCK_SHIFT; y-- > 0;)
			{
				layers = _cells3D[x][y];
				z = layers.length;
				layersCopy = block._cells3D[x][y] = new GeoCell[z];
				while (z-- > 0)
				{
					layersCopy[z] = new GeoCell(this, layers[z].getHeightAndNSWE(), x, y);
				}
			}
		}
		block._maxHeight = _maxHeight;
		block._minHeight = _minHeight;
		block.copyCells();
	}
	
	@Override
	public final void saveTo(final IncBufferedFileWriter writer, final boolean l2j) throws IOException
	{
		GeoCell[] layers;
		writer.writeByte(GeoEngine.GEO_BLOCK_TYPE_MULTILEVEL);
		for (int x = 0, y, z; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				layers = _cells3D[x][y];
				if (l2j)
				{
					writer.writeByte((byte) layers.length);
				}
				else
				{
					writer.writeShort((short) layers.length);
				}
				
				for (z = layers.length; z-- > 0;)
				{
					writer.writeShort(layers[z].getHeightAndNSWE());
				}
			}
		}
	}
	
	@Override
	public final int getMaxLayerCount()
	{
		int maxLayerCount = 0;
		for (int x = GeoEngine.GEO_BLOCK_SHIFT, y; x-- > 0;)
		{
			for (y = GeoEngine.GEO_BLOCK_SHIFT; y-- > 0;)
			{
				maxLayerCount = Math.max(maxLayerCount, _cells3D[x][y].length);
			}
		}
		return maxLayerCount;
	}
	
	@Override
	public final int addLayer(final int geoX, final int geoY, final short heightAndNSWE)
	{
		/*final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		final short height = GeoEngine.getHeight(heightAndNSWE);
		int layer = nGetLayer(geoX, geoY, height);
		final short heightAtLayer = GeoEngine.getHeight(nGetHeightAndNSWEByLayer(geoX, geoY, layer));
		if (height > heightAtLayer)
		{
			if (++layer == _heights[cellX][cellY].length)
			{
				_heights[cellX][cellY] = ArrayUtil.arrayAdd(_heights[cellX][cellY], heightAndNSWE);
			}
			else
			{
				_heights[cellX][cellY] = ArrayUtil.arrayInsert(_heights[cellX][cellY], heightAndNSWE, layer);
			}
			return layer;
		}
		else
		{
			_heights[cellX][cellY] = ArrayUtil.arrayInsert(_heights[cellX][cellY], heightAndNSWE, layer);
			return layer;
		}*/
		return -1;
	}
	
	@Override
	public final int removeLayer(final int geoX, final int geoY, final int layer)
	{
		/*final int cellX = GeoEngine.getCellXY(geoX);
		final int cellY = GeoEngine.getCellXY(geoY);
		final int length = _heights[cellX][cellY].length;
		if (layer < 0 || layer >= length || length == 1)
			return -1;
		
		_heights[cellX][cellY] = ArrayUtil.arrayRemoveAtUnsafe(_heights[cellX][cellY], layer);
		return layer;*/
		return -1;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#getCells()
	 */
	@Override
	public final GeoCell[] getCells()
	{
		return _cells;
	}
}