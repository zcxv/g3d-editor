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
package g3deditor.geo;

import g3deditor.entity.SelectionState;
import g3deditor.geo.blocks.GeoBlockFlat;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoCell
{
	private final GeoBlock _block;
	private final byte _cellX;
	private final byte _cellY;
	
	private short _heightAndNSWE;
	private SelectionState _selectionState;
	
	public GeoCell(final GeoBlock block, final short heightAndNSWE)
	{
		this(block, heightAndNSWE, 0, 0);
	}
	
	public GeoCell(final GeoBlock block, final short heightAndNSWE, final int cellX, final int cellY)
	{
		_block = block;
		_cellX = (byte) cellX;
		_cellY = (byte) cellY;
		_heightAndNSWE = heightAndNSWE;
		_selectionState = SelectionState.NORMAL;
	}
	
	public final GeoBlock getBlock()
	{
		return _block;
	}
	
	public final boolean isBig()
	{
		return _block instanceof GeoBlockFlat;
	}
	
	public final SelectionState getSelectionState()
	{
		return _selectionState;
	}
	
	public final void setSelectionState(final SelectionState selectionState)
	{
		_selectionState = selectionState;
	}
	
	public final short getHeight()
	{
		return GeoEngine.getHeight(_heightAndNSWE);
	}
	
	public final short getNSWE()
	{
		return GeoEngine.getNSWE(_heightAndNSWE);
	}
	
	public final short getHeightAndNSWE()
	{
		return _heightAndNSWE;
	}
	
	public final void setHeightAndNSWE(final short heightAndNSWE)
	{
		_heightAndNSWE = heightAndNSWE;
		getBlock().updateMaxMinHeight(getHeight());
	}
	
	public final void addHeight(final short height)
	{
		setHeightAndNSWE(GeoEngine.updateHeightOfHeightAndNSWE(getHeightAndNSWE(), (short) (getHeight() + height)));
	}
	
	public final int getGeoX()
	{
		return _block.getGeoX() + _cellX;
	}
	
	public final int getGeoY()
	{
		return _block.getGeoY() + _cellY;
	}
	
	public final float getRenderX()
	{
		return getGeoX();
	}
	
	/**
	 * 
	 * @return The adjusted Y to render
	 */
	public final float getRenderY()
	{
		return getHeight() / 16f;
	}
	
	public final float getRenderZ()
	{
		return getGeoY();
	}
}