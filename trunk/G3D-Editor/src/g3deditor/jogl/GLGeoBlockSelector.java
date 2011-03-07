package g3deditor.jogl;

import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;

import java.util.concurrent.locks.ReentrantLock;

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