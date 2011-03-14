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
package g3deditor.geo.cells;

import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.geo.blocks.GeoBlockFlat;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoCellFlat extends GeoCell
{
	private short _height;
	
	/**
	 * @param block
	 */
	public GeoCellFlat(final GeoBlockFlat block, final short height)
	{
		super(block);
		_height = GeoEngine.getGeoHeightOfHeight(height);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#isBig()
	 */
	@Override
	public final boolean isBig()
	{
		return true;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getHeight()
	 */
	@Override
	public final short getHeight()
	{
		return _height;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getNSWE()
	 */
	@Override
	public final short getNSWE()
	{
		return GeoEngine.NSWE_ALL;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getHeightAndNSWE()
	 */
	@Override
	public final short getHeightAndNSWE()
	{
		return GeoEngine.convertHeightToHeightAndNSWEALL(_height);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#addHeight(short)
	 */
	@Override
	public final void addHeight(final short height)
	{
		_height = GeoEngine.getGeoHeightOfHeight((short) (_height + height));
		getBlock().updateMinMaxHeight(_height);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getGeoX()
	 */
	@Override
	public final int getGeoX()
	{
		return getBlock().getGeoX();
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getGeoY()
	 */
	@Override
	public final int getGeoY()
	{
		return getBlock().getGeoY();
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getCellX()
	 */
	@Override
	public final int getCellX()
	{
		return 0;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getCellY()
	 */
	@Override
	public final int getCellY()
	{
		return 0;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#setHeightAndNSWE(short)
	 */
	@Override
	public final void setHeightAndNSWE(final short heightAndNSWE)
	{
		_height = GeoEngine.getHeight(heightAndNSWE);
		getBlock().updateMinMaxHeight(_height);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#setNswe(short)
	 */
	@Override
	public final void setNswe(final short nswe)
	{
		
	}
}