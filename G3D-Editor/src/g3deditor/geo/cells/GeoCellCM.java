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

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoCellCM extends GeoCell
{
	private byte _cellX;
	private byte _cellY;
	private short _heightAndNSWE;
	
	/**
	 * 
	 * @param block
	 * @param cellX
	 * @param cellY
	 * @param heightAndNSWE
	 */
	public GeoCellCM(final GeoBlock block, final short heightAndNSWE, final int cellX, final int cellY)
	{
		super(block);
		_cellX = (byte) cellX;
		_cellY = (byte) cellY;
		_heightAndNSWE = heightAndNSWE;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#isBig()
	 */
	@Override
	public final boolean isBig()
	{
		return false;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getHeight()
	 */
	@Override
	public final short getHeight()
	{
		return GeoEngine.getHeight(_heightAndNSWE);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getNSWE()
	 */
	@Override
	public final short getNSWE()
	{
		return GeoEngine.getNSWE(_heightAndNSWE);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getHeightAndNSWE()
	 */
	@Override
	public final short getHeightAndNSWE()
	{
		return _heightAndNSWE;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#addHeight(short)
	 */
	@Override
	public final void addHeight(final short height)
	{
		final short oldHeight = getHeight();
		_heightAndNSWE = GeoEngine.updateHeightOfHeightAndNSWE(_heightAndNSWE, (short) (getHeight() + height));
		getBlock().updateMinMaxHeight(getHeight(), oldHeight);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getGeoX()
	 */
	@Override
	public final int getGeoX()
	{
		return getBlock().getGeoX() + getCellX();
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getGeoY()
	 */
	@Override
	public final int getGeoY()
	{
		return getBlock().getGeoY() + getCellY();
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getCellX()
	 */
	@Override
	public final int getCellX()
	{
		return _cellX;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#getCellY()
	 */
	@Override
	public final int getCellY()
	{
		return _cellY;
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#setHeightAndNSWE(short)
	 */
	@Override
	public final void setHeightAndNSWE(final short heightAndNSWE)
	{
		final short oldHeight = getHeight();
		_heightAndNSWE = heightAndNSWE;
		getBlock().updateMinMaxHeight(getHeight(), oldHeight);
	}
	
	/**
	 * @see g3deditor.geo.GeoCell#setNswe(short)
	 */
	@Override
	public final void setNswe(final short nswe)
	{
		_heightAndNSWE = GeoEngine.updateNSWEOfHeightAndNSWE(_heightAndNSWE, nswe);
	}
}