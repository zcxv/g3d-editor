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

import java.io.IOException;

import org.inc.incolution.util.io.IncBufferedFileWriter;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public abstract class GeoBlock implements Cloneable
{
	public static final GeoBlock[] EMPTY_ARRAY = new GeoBlock[0];
	
	private final int _geoX;
	private final int _geoY;
	
	private GeoRegion _region;
	
	protected short _minHeight;
	protected short _maxHeight;
	
	protected GeoBlock(final int geoX, final int geoY)
	{
		_geoX = geoX;
		_geoY = geoY;
	}
	
	public final int getGeoX()
	{
		return _geoX;
	}
	
	public final int getGeoY()
	{
		return _geoY;
	}
	
	public final void setRegion(final GeoRegion region)
	{
		_region = region;
	}
	
	public final GeoRegion getRegion()
	{
		return _region;
	}
	
	public final int getBlockX()
	{
		return GeoEngine.getBlockXY(_geoX);
	}
	
	public final int getBlockY()
	{
		return GeoEngine.getBlockXY(_geoY);
	}
	
	public final int getMaxGeoX()
	{
		return _geoX + GeoEngine.GEO_BLOCK_SHIFT - 1;
	}
	
	public final int getMaxGeoY()
	{
		return _geoY + GeoEngine.GEO_BLOCK_SHIFT - 1;
	}
	
	public final short getMinHeight()
	{
		return _minHeight;
	}
	
	public final short getMaxHeight()
	{
		return _maxHeight;
	}
	
	public final void updateMaxMinHeight(final short height)
	{
		_minHeight = (short) Math.min(_minHeight, height);
		_maxHeight = (short) Math.max(_maxHeight, height);;
	}
	
	public final String getStringType()
	{
		return getStringType(getType());
	}
	
	public static final String getStringType(final byte type)
	{
		switch (type)
		{
			case GeoEngine.GEO_BLOCK_TYPE_FLAT:
				return "Flat";
				
			case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
				return "Complex";
				
			case GeoEngine.GEO_BLOCK_TYPE_MULTILEVEL:
				return "MultiLevel";
		}
		
		throw new RuntimeException();
	}
	
	@Override
	public final String toString()
	{
		return getStringType(getType()) + " " + GeoEngine.getBlockXY(_geoX) + ", " + GeoEngine.getBlockXY(_geoY);
	}
	
	public final GeoCell nGetCell(final int geoX, final int geoY, final int z)
	{
		return nGetCellByLayer(geoX, geoY, nGetLayer(geoX, geoY, z));
	}
	
	public abstract byte getType();
	
	public abstract int nGetLayerCount(final int geoX, final int geoY);
	
	public abstract int nGetLayer(final int geoX, final int geoY, final int z);
	
	public abstract GeoCell nGetCellByLayer(final int geoX, final int geoY, final int layer);
	
	public abstract GeoCell[] nGetLayers(final int geoX, final int geoY);
	
	public abstract GeoBlock clone();
	
	public abstract void calcMaxMinHeight();
	
	public abstract int getMaxLayerCount();
	
	public abstract int addLayer(final int geoX, final int geoY, final short heightAndNSWE);
	
	public abstract int removeLayer(final int geoX, final int geoY, final int layer);
	
	public abstract void saveTo(final IncBufferedFileWriter writer, final boolean l2j) throws IOException;
	
	public abstract GeoCell[] getCells();
}