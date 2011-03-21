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
package g3deditor.jogl;

import g3deditor.Config;
import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;
import g3deditor.geo.blocks.GeoBlockFlat;
import g3deditor.util.TaskExecutor;
import g3deditor.util.Util;
import g3deditor.util.Util.FastComparator;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLCellRenderSelector
{
	public static final FastComparator<GLSubRenderSelector> GEO_BLOCK_COMPARATOR = new FastComparator<GLSubRenderSelector>()
	{
		@Override
		public final boolean compare(final GLSubRenderSelector o1, final GLSubRenderSelector o2)
		{
			final GLCamera camera = GLDisplay.getInstance().getCamera();
			final float dx1 = camera.getX() - o1.getGeoBlock().getGeoX();
			final float dy1 = camera.getZ() - o1.getGeoBlock().getGeoY();
			final float dx2 = camera.getX() - o2.getGeoBlock().getGeoX();
			final float dy2 = camera.getZ() - o2.getGeoBlock().getGeoY();
			return dx1 * dx1 + dy1 * dy1 > dx2 * dx2 + dy2 * dy2;
		}
	};
	
	public static final FastComparator<GeoCell> GEO_CELL_COMPARATOR = new FastComparator<GeoCell>()
	{
		@Override
		public final boolean compare(final GeoCell o1, final GeoCell o2)
		{
			final GLCamera camera = GLDisplay.getInstance().getCamera();
			final float dx1 = camera.getX() - o1.getGeoX();
			final float dy1 = camera.getZ() - o1.getGeoY();
			final float dz1 = camera.getY() - o1.getHeight() / 16f;
			final float dx2 = camera.getX() - o2.getGeoX();
			final float dy2 = camera.getZ() - o2.getGeoY();
			final float dz2 = camera.getY() - o2.getHeight() / 16f;
			return dx1 * dx1 + dy1 * dy1 + dz1 * dz1 > dx2 * dx2 + dy2 * dy2 + dz2 * dz2;
		}
	};
	
	public static final int MIN_VIS_GRID_RANGE = 8;
	public static final int MAX_VIS_GRID_RANGE = 96;
	
	private final TaskExecutor _taskExecutor;
	
	private float[][] _frustum;
	
	private GLSubRenderSelector[] _geoBlocks;
	private int _geoBlocksSize;
	
	private int _camBlockX;
	private int _camBlockY;
	private boolean _forceUpdateFrustum;
	private boolean _freezeGrid;
	private int _gridRange;
	
	public GLCellRenderSelector()
	{
		_geoBlocks = new GLSubRenderSelector[0];
		_taskExecutor = new TaskExecutor(Runtime.getRuntime().availableProcessors());
	}
	
	public final void init()
	{
		_taskExecutor.init();
	}
	
	public final void dispose()
	{
		_taskExecutor.dispose();
	}
	
	public final void forceUpdateFrustum()
	{
		_forceUpdateFrustum = true;
	}
	
	public final void select(final GL2 gl, final GLCamera camera, final boolean freezeGrid)
	{
		final GeoRegion region = GeoEngine.getInstance().getActiveRegion();
		if (region == null)
		{
			_geoBlocksSize = 0;
			return;
		}
		
		if (camera.positionXZChanged() || _freezeGrid != freezeGrid || _gridRange != Config.VIS_GRID_RANGE)
		{
			final int geoX = Math.max(Math.min(camera.getGeoX(), region.getGeoX(GeoEngine.GEO_REGION_SIZE - 1)), region.getGeoX(0));
			final int geoY = Math.max(Math.min(camera.getGeoY(), region.getGeoY(GeoEngine.GEO_REGION_SIZE - 1)), region.getGeoY(0));
			final int camBlockX = GeoEngine.getBlockXY(geoX);
			final int camBlockY = GeoEngine.getBlockXY(geoY);
			
			if (camBlockX != _camBlockX || camBlockY != _camBlockY || _freezeGrid != freezeGrid || _gridRange != Config.VIS_GRID_RANGE)
			{
				_camBlockX = camBlockX;
				_camBlockY = camBlockY;
				
				final int range = Config.VIS_GRID_RANGE;
				final int requiredSize = (range * 2 + 1) * (range * 2 + 1);
				final boolean needUpdate = _geoBlocks.length != requiredSize;
				if (_geoBlocks.length != requiredSize)
				{
					_geoBlocks = new GLSubRenderSelector[requiredSize];
					for (int i = requiredSize; i-- > 0;)
					{
						_geoBlocks[i] = new GLSubRenderSelector();
					}
				}
				
				if (!freezeGrid || needUpdate)
				{
					final int diffBlockXNeg = camBlockX - range;
					final int diffBlockXPos = camBlockX + range;
					final int diffBlockYNeg = camBlockY - range;
					final int diffBlockYPos = camBlockY + range;
					
					final int minBlockX = Math.max(diffBlockXNeg + (diffBlockXPos > GeoEngine.GEO_REGION_SIZE - 1 ? GeoEngine.GEO_REGION_SIZE - 1 - diffBlockXPos : 0), 0);
					final int maxBlockX = Math.min(diffBlockXPos + (diffBlockXNeg < 0 ? -diffBlockXNeg : 0), GeoEngine.GEO_REGION_SIZE - 1);
					final int minBlockY = Math.max(diffBlockYNeg + (diffBlockYPos > GeoEngine.GEO_REGION_SIZE - 1 ? GeoEngine.GEO_REGION_SIZE - 1 - diffBlockYPos : 0), 0);
					final int maxBlockY = Math.min(diffBlockYPos + (diffBlockYNeg < 0 ? -diffBlockYNeg : 0), GeoEngine.GEO_REGION_SIZE - 1);
					
					GeoBlockSelector.getInstance().checkDeselection(minBlockX, maxBlockX, minBlockY, maxBlockY);
					
					_geoBlocksSize = 0;
					for (int x = minBlockX, y; x < maxBlockX; x++)
					{
						for (y = minBlockY; y < maxBlockY; y++)
						{
							_geoBlocks[_geoBlocksSize++].setGeoBlock(region.getBlockByBlockXY(x, y));
						}
					}
					
					_forceUpdateFrustum = true;
				}
			}
		}
		
		_freezeGrid = freezeGrid;
		_gridRange = Config.VIS_GRID_RANGE;
		
		if (camera.positionXZChanged() || camera.positionYChanged() || camera.rotationChanged() || _forceUpdateFrustum)
		{
			_forceUpdateFrustum = false;
			_frustum = camera.getFrustum(gl);
			_taskExecutor.execute(_geoBlocks, _geoBlocksSize);
			Util.quickSort(_geoBlocks, _geoBlocksSize, GEO_BLOCK_COMPARATOR);
		}
	}
	
	public final int getElementsToRender()
	{
		return _geoBlocksSize;
	}
	
	public final GLSubRenderSelector getElementToRender(final int index)
	{
		return _geoBlocks[index];
	}
	
	public final boolean isVisible(final GeoBlock block)
	{
		if (block instanceof GeoBlockFlat) // flat block`s cell get checked
			return true;
		
		final float x1 = block.getGeoX();
		final float x2 = x1 + 8f;
		final float y1 = block.getMinHeight() / 16f;
		final float y2 = block.getMaxHeight() / 16f;
		final float z1 = block.getGeoY();
		final float z2 = z1 + 8f;
		
		float[] plane;
		float p, px1, px2, py1, py2, pz1, pz2;
		for (int i = 6; i-- > 0;)
		{
			plane = _frustum[i];
			p = plane[0];
			px1 = p * x1;
			px2 = p * x2;
			p = plane[1];
			py1 = p * y1;
			py2 = p * y2;
			p = plane[2];
			pz1 = p * z1;
			pz2 = p * z2;
			p = plane[3];
			
			if (px1 + py1 + pz1 + p <= 0f &&
					px2 + py1 + pz1 + p <= 0f &&
					px1 + py2 + pz1 + p <= 0f &&
					px2 + py2 + pz1 + p <= 0f &&
					px1 + py1 + pz2 + p <= 0f &&
					px2 + py1 + pz2 + p <= 0f &&
					px1 + py2 + pz2 + p <= 0f &&
					px2 + py2 + pz2 + p <= 0f)
				return false;
		}
		return true;
	}
	
	public final boolean isVisible(final GeoCell cell)
	{
		final float x;
		final float y;
		final float z;
		final float rad;
		
		if (cell.isBig())
		{
			x = cell.getRenderX() + 3.5f;
			y = cell.getRenderY() - 0.1f;
			z = cell.getRenderZ() + 3.5f;
			rad = 5f;
		}
		else
		{
			// TODO Implement proper (Used for heavy locations like ToI)
			//if (cell.getBlock().nGetLayerCount(cell.getGeoX(), cell.getGeoY()) > 6 && Math.abs(cell.getRenderY() - getDisplay().getCamera().getY()) >= 200)
			//	return false;
			
			x = cell.getRenderX() + 0.5f;
			y = cell.getRenderY() - 0.1f;
			z = cell.getRenderZ() + 0.5f;
			rad = 1f;
		}
		
		float[] plane;
		for (int i = 6; i-- > 0;)
		{
			plane = _frustum[i];
			if (plane[0] * x + plane[1] * y + plane[2] * z + plane[3] <= -rad)
				return false;
		}
		return true;
	}
	
	public final class GLSubRenderSelector implements Runnable
	{
		private GeoBlock _block;
		private GeoCell[] _geoCells;
		private int _count;
		
		public GLSubRenderSelector()
		{
			_geoCells = new GeoCell[0];
		}
		
		public final void setGeoBlock(final GeoBlock block)
		{
			_block = block;
		}
		
		public final GeoBlock getGeoBlock()
		{
			return _block;
		}
		
		public final int getElementsToRender()
		{
			return _count;
		}
		
		public final GeoCell getElementToRender(final int index)
		{
			return _geoCells[index];
		}
		
		@Override
		public final void run()
		{
			_count = 0;
			if (!GLCellRenderSelector.this.isVisible(_block))
				return;
			
			final GeoCell[] cells = _block.getCells();
			if (_geoCells.length < cells.length)
			{
				_geoCells = new GeoCell[cells.length];
				_geoCells = new GeoCell[cells.length];
			}
			
			for (final GeoCell cell : cells)
			{
				if (GLCellRenderSelector.this.isVisible(cell))
					_geoCells[_count++] = cell;
			}
			
			Util.quickSort(_geoCells, _count, GEO_CELL_COMPARATOR);
		}
	}
}