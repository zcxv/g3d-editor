package g3deditor.geo.blocks;

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.inc.incolution.util.io.IncBufferedFileWriter;

/**
 * Flat block, 1 level, 1 height.
 *
 * @author Forsaiken
 */
public final class GeoBlockFlat extends GeoBlock
{
	private final GeoCell[] _cells;
	
	public GeoBlockFlat(final ByteBuffer bb, final int geoX, final int geoY, final boolean l2j)
	{
		super(geoX, geoY);
		_cells = new GeoCell[]{new GeoCell(this, GeoEngine.convertHeightToHeightAndNSWEALL(bb.getShort()))};
		
		if (!l2j)
			bb.getShort();
		
		calcMaxMinHeight();
	}
	
	public GeoBlockFlat(final GeoBlock block)
	{
		super(block.getGeoX(), block.getGeoY());
		_cells = new GeoCell[]{new GeoCell(this, GeoEngine.convertHeightToHeightAndNSWEALL(block.getMinHeight()))};
		
		calcMaxMinHeight();
	}
	
	private GeoBlockFlat(final GeoBlockFlat block)
	{
		super(block.getGeoX(), block.getGeoY());
		_cells = new GeoCell[1];
	}
	
	@Override
	public final byte getType()
	{
		return GeoEngine.GEO_BLOCK_TYPE_FLAT;
	}
	
	@Override
	public final int nGetLayer(final int geoX, final int geoY, final int z)
	{
		return 0;
	}
	
	@Override
	public final GeoCell nGetCellByLayer(final int geoX, final int geoY, final int layer)
	{
		return _cells[0];
	}
	
	@Override
	public final int nGetLayerCount(final int geoX, final int geoY)
	{
		return 1;
	}
	
	@Override
	public final GeoCell[] nGetLayers(final int geoX, final int geoY)
	{
		return new GeoCell[]{_cells[0]};
	}
	
	@Override
	public final void calcMaxMinHeight()
	{
		_maxHeight = _cells[0].getHeight();
		_minHeight = _maxHeight;
	}
	
	@Override
	public final GeoBlockFlat clone()
	{
		final GeoBlockFlat clone = new GeoBlockFlat(this);
		copyDataTo(clone);
		return clone;
	}
	
	public final void copyDataTo(final GeoBlockFlat block)
	{
		block._cells[0].setHeightAndNSWE(_cells[0].getHeightAndNSWE());
		block._maxHeight = _maxHeight;
		block._minHeight = _minHeight;
	}
	
	@Override
	public final void saveTo(final IncBufferedFileWriter writer, final boolean l2j) throws IOException
	{
		writer.writeByte(GeoEngine.GEO_BLOCK_TYPE_FLAT);
		writer.writeShort(_cells[0].getHeight());
		if (!l2j)
			writer.writeShort(_cells[0].getHeight());
	}
	
	@Override
	public final int getMaxLayerCount()
	{
		return 1;
	}
	
	@Override
	public final int addLayer(final int geoX, final int geoY, final short heightAndNSWE)
	{
		return -1;
	}
	
	@Override
	public final int removeLayer(final int geoX, final int geoY, final int layer)
	{
		return -1;
	}
	
	/**
	 * @see g3deditor.content.geo.GeoBlock#getAllCells()
	 */
	@Override
	public final GeoCell[] getCells()
	{
		return _cells;
	}
}