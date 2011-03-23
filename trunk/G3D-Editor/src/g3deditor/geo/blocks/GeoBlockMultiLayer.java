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

import java.util.Arrays;

/**
 * MultiLayer block, x levels, 64 heights + y (each cell can have multiple heights).<br>
 * 
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoBlockMultiLayer extends GeoBlock
{
	public static final GeoBlockMultiLayer convertFrom(final GeoBlock block)
	{
		if (block instanceof GeoBlockFlat)
			return new GeoBlockMultiLayer((GeoBlockFlat) block);
		
		if (block instanceof GeoBlockComplex)
			return new GeoBlockMultiLayer((GeoBlockComplex) block);
		
		return new GeoBlockMultiLayer((GeoBlockMultiLayer) block);
	}
	
	private GeoCell[][][] _cells3D;
	private GeoCell[] _cells;
	private short _minHeight;
	private short _maxHeight;
	
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
	
	public GeoBlockMultiLayer(final GeoReader reader, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][];
		
		int layers, count = 0;
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				layers = l2j ? reader.get() : reader.getShort();
				if (!GeoEngine.layersValid(layers))
					throw new RuntimeException("Invalid layer count " + layers);
				
				count += layers;
				_cells3D[x][y] = new GeoCell[layers];
				for (int i = layers; i-- > 0;)
				{
					_cells3D[x][y][i] = new GeoCellCM(this, reader.getShort(), x, y);
				}
			}
		}
		
		copyCells(count);
		calcMaxMinHeight();
	}
	
	private GeoBlockMultiLayer(final GeoBlockFlat block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][1];
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				final GeoCell cell = new GeoCellCM(this, GeoEngine.convertHeightToHeightAndNSWEALL(block.getMinHeight()), x, y);
				_cells3D[x][y][0] = cell;
				_cells[x * GeoEngine.GEO_BLOCK_SHIFT + y] = cell; 
			}
		}
		calcMaxMinHeight();
	}
	
	private GeoBlockMultiLayer(final GeoBlockComplex block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][1];
		_cells = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT * GeoEngine.GEO_BLOCK_SHIFT];
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				final GeoCell cell = new GeoCellCM(this, block.nGetCellByLayer(x, y, 0).getHeightAndNSWE(), x, y);
				_cells3D[x][y][0] = cell;
				_cells[x * GeoEngine.GEO_BLOCK_SHIFT + y] = cell; 
			}
		}
		calcMaxMinHeight();
	}
	
	private GeoBlockMultiLayer(final GeoBlockMultiLayer block)
	{
		super(block.getGeoX(), block.getGeoY());
		
		_cells3D = new GeoCell[GeoEngine.GEO_BLOCK_SHIFT][GeoEngine.GEO_BLOCK_SHIFT][];
		
		int layers, count = 0;
		for (int x = 0; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				layers = block.nGetLayerCount(x, y);
				count += layers;
				_cells3D[x][y] = new GeoCell[layers];
				while (layers-- > 0)
				{
					final GeoCell cell = new GeoCellCM(this, block.nGetCellByLayer(x, y, layers).getHeightAndNSWE(), x, y);
					_cells3D[x][y][layers] = cell;
				}
			}
		}
		
		copyCells(count);
		calcMaxMinHeight();
	}
	
	@Override
	public final byte getType()
	{
		return GeoEngine.GEO_BLOCK_TYPE_MULTILAYER;
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
	
	/**
	 * @see g3deditor.geo.GeoBlock#writeTo(g3deditor.util.GeoWriter, boolean)
	 */
	@Override
	public final void writeTo(final GeoWriter writer, final boolean l2j)
	{
		GeoCell[] layers;
		GeoRegion.putType(writer, l2j, getType());
		for (int x = 0, y, z; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				layers = _cells3D[x][y];
				if (l2j)
					writer.put((byte) layers.length);
				else
					writer.putShort((short) layers.length);
				
				for (z = layers.length; z-- > 0;)
				{
					writer.putShort(layers[z].getHeightAndNSWE());
				}
			}
		}
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#getRequiredCapacity(boolean)
	 */
	@Override
	public final int getRequiredCapacity(final boolean l2j)
	{
		int capacity = l2j ? 1 : 2;
		for (int x = 0, y; x < GeoEngine.GEO_BLOCK_SHIFT; x++)
		{
			for (y = 0; y < GeoEngine.GEO_BLOCK_SHIFT; y++)
			{
				capacity += l2j ? 1 : 2;
				capacity += _cells3D[x][y].length * 2;
			}
		}
		return capacity;
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
		
		GeoCell[][] cells2D;
		GeoCell[] cells1D;
		for (int i = _cells3D.length, j, k; i-- > 0;)
		{
			cells2D = _cells3D[i];
			for (j = cells2D.length; j-- > 0;)
			{
				cells1D = cells2D[j];
				for (k = cells1D.length; k-- > 0;)
				{
					cells1D[k].unload();
					cells1D[k] = null;
				}
				cells2D[j] = null;
			}
			_cells3D[i] = null;
		}
		_cells3D = null;
	}
	
	/**
	 * @see g3deditor.geo.GeoBlock#dataEquals(g3deditor.util.GeoReader)
	 */
	@Override
	public final boolean dataEquals(final GeoReader reader)
	{
		if (getType() != GeoRegion.getType(reader, true))
			return false;
		
		GeoCell[][] cells2D;
		GeoCell[] cells1D;
		for (int cellX = 0, cellY, layer; cellX < GeoEngine.GEO_BLOCK_SHIFT; cellX++)
		{
			cells2D = _cells3D[cellX];
			for (cellY = 0; cellY < GeoEngine.GEO_BLOCK_SHIFT; cellY++)
			{
				cells1D = cells2D[cellY];
				
				if (cells1D.length != reader.get())
					return false;
				
				for (layer = cells1D.length; layer-- > 0;)
				{
					if (cells1D[layer].getHeightAndNSWE() != reader.getShort())
						return false;
				}
			}
		}
		return true;
	}
}