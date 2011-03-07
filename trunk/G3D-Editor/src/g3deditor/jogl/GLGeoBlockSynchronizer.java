package g3deditor.jogl;

import java.util.concurrent.locks.ReentrantLock;

public final class GLGeoBlockSynchronizer
{
	private final GLDisplay _display;
	private final ReentrantLock _lock;
	
	public GLGeoBlockSynchronizer(final GLDisplay display)
	{
		_display = display;
		_lock = new ReentrantLock();
	}
	
	public final GLDisplay getDisplay()
	{
		return _display;
	}
	
	public final void modifyGeoCellNSWE(final int geoX, final int geoY, final int layer, final int nswe)
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
	
	public final void modifyGeoCellHeight(final int geoX, final int geoY, final int layer, final int height)
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
	
	public final void modifyGeoBlockType(final int geoX, final int geoY)
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
	
	public final void restoreGeoBlock(final int geoX, final int geoY)
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
	
	public final void addGeoCell(final int geoX, final int geoY, final int height, final int nswe)
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
	
	public final void removeGeoCell(final int geoX, final int geoY, final int layer)
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
	
	/**
	 * 
	 * @return True if one ore more cells/blocks was modified and the GLRenderSelector need to perform an full select.
	 */
	public final boolean processModifications()
	{
		_lock.lock();
		
		try
		{
			return false;
		}
		finally
		{
			_lock.unlock();
		}
	}
}