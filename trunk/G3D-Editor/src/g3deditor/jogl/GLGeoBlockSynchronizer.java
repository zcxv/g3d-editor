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

import java.util.concurrent.locks.ReentrantLock;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
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