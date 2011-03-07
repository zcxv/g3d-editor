package g3deditor.geo;

import g3deditor.entity.SelectionState;
import g3deditor.geo.blocks.GeoBlockFlat;

/**
 * @author Forsaiken
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