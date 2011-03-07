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

import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;

import java.util.concurrent.locks.ReentrantLock;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLGeoBlockSelector
{
	private final GLDisplay _display;
	private final ReentrantLock _lock;
	private final GeoBlockHandler[][] _geoBlockGrid;
	private final GeoBlockHandler[] _geoBlocks;
	private int _geoBlocksSize;
	
	public GLGeoBlockSelector(final GLDisplay display)
	{
		_display = display;
		_lock = new ReentrantLock();
		_geoBlockGrid = new GeoBlockHandler[GeoEngine.GEO_REGION_SIZE][GeoEngine.GEO_REGION_SIZE];
		_geoBlocks = new GeoBlockHandler[GeoEngine.GEO_REGION_SIZE * GeoEngine.GEO_REGION_SIZE];
	}
	
	public final GLDisplay getDisplay()
	{
		return _display;
	}
	
	public final boolean isGeoCellSelected(final GeoCell cell)
	{
		return false;
	}
	
	public final void selectGeoCell(final int geoX, final int geoY, final int layer, final boolean fullBlock, final boolean append)
	{
		_lock.lock();
		
		try
		{
			
		}
		finally
		{
			_lock.unlock();
		}
	}
	
	private final class GeoBlockHandler
	{
		
	}
}