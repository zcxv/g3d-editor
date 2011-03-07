package g3deditor.entity;

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.blocks.GeoBlockComplex;
import g3deditor.geo.blocks.GeoBlockFlat;

public enum SelectionState
{
	NORMAL(new CellColor(0, 0, 1), new CellColor(0, 1, 0), new CellColor(1, 0, 0)),
	SELECTED(new CellColor(0.5f, 0.5f, 1), new CellColor(0.5f, 1, 0.5f), new CellColor(1, 0.5f, 0.5f));
	
	private final CellColor _colorFlat;
	private final CellColor _colorComplex;
	private final CellColor _colorMutliLayer;
	
	private SelectionState(final CellColor colorFlat, final CellColor colorComplex, final CellColor colorMutliLayer)
	{
		_colorFlat = colorFlat;
		_colorComplex = colorComplex;
		_colorMutliLayer = colorMutliLayer;
	}
	
	public final CellColor getColor(final GeoCell cell)
	{
		return getColor(cell.getBlock());
	}
	
	public final CellColor getColor(final GeoBlock block)
	{
		if (block instanceof GeoBlockFlat)
			return _colorFlat;
		
		if (block instanceof GeoBlockComplex)
			return _colorComplex;
		
		return _colorMutliLayer;
	}
}