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

import g3deditor.geo.blocks.GeoBlockComplex;
import g3deditor.geo.blocks.GeoBlockFlat;
import g3deditor.geo.blocks.GeoBlockMultiLevel;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.inc.incolution.util.io.IncBufferedFileWriter;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoRegion
{
	private static final byte getType(final ByteBuffer bb, final boolean l2j)
	{
		if (l2j)
			return bb.get();
		
		switch (bb.getShort())
		{
			case 0x0000:
				return GeoEngine.GEO_BLOCK_TYPE_FLAT;
				
			case 0x0040:
				return GeoEngine.GEO_BLOCK_TYPE_COMPLEX;
				
			default:
				return GeoEngine.GEO_BLOCK_TYPE_MULTILEVEL;
		}
	}
	
	private final File _file;
	private final int _regionX;
	private final int _regionY;
	private final GeoBlock[][] _geoBlocks;
	
	public GeoRegion(final int regionX, final int regionY, final ByteBuffer bb, final boolean l2j, final File file)
	{
		_file = file;
		_regionX = regionX;
		_regionY = regionY;
		_geoBlocks = new GeoBlock[GeoEngine.GEO_REGION_SIZE][GeoEngine.GEO_REGION_SIZE];
		
		bb.position(0);
		
		int type;
		for (int x = 0; x < GeoEngine.GEO_REGION_SIZE; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_REGION_SIZE; y++)
			{
				switch ((type = getType(bb, l2j)))
				{
					case GeoEngine.GEO_BLOCK_TYPE_FLAT:
					{
						_geoBlocks[x][y] = new GeoBlockFlat(bb, GeoEngine.getGeoXY(regionX, x), GeoEngine.getGeoXY(regionY, y), l2j);
						break;
					}
						
					case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
					{
						_geoBlocks[x][y] = new GeoBlockComplex(bb, GeoEngine.getGeoXY(regionX, x), GeoEngine.getGeoXY(regionY, y), l2j);
						break;
					}
						
					case GeoEngine.GEO_BLOCK_TYPE_MULTILEVEL:
					{
						_geoBlocks[x][y] = new GeoBlockMultiLevel(bb, GeoEngine.getGeoXY(regionX, x), GeoEngine.getGeoXY(regionY, y), l2j);
						break;
					}
						
					default:
					{
						throw new RuntimeException("Unknown type: " + type);
					}
				}
			}
		}
		
		for (int x = 0; x < GeoEngine.GEO_REGION_SIZE; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_REGION_SIZE; y++)
			{
				_geoBlocks[x][y].setRegion(this);
			}
		}
	}
	
	public final File getFile()
	{
		return _file;
	}
	
	public final GeoBlock[][] getGeoBlocks()
	{
		return _geoBlocks;
	}
	
	public final int getRegionX()
	{
		return _regionX;
	}
	
	public final int getRegionY()
	{
		return _regionY;
	}
	
	public final int getGeoX(final int blockX)
	{
		return GeoEngine.getGeoXY(getRegionX(), blockX);
	}
	
	public final int getGeoY(final int blockY)
	{
		return GeoEngine.getGeoXY(getRegionY(), blockY);
	}
	
	public final String getName()
	{
		return (getRegionX() + 10) + "_" + (getRegionY() + 10);
	}
	
	public final byte nGetType(final int geoX, final int geoY)
	{
		final int blockX = GeoEngine.getBlockXY(geoX);
		final int blockY = GeoEngine.getBlockXY(geoY);
		return _geoBlocks[blockX][blockY].getType();
	}
	
	public final GeoCell nGetCell(final int geoX, final int geoY, final int x)
	{
		final int blockX = GeoEngine.getBlockXY(geoX);
		final int blockY = GeoEngine.getBlockXY(geoY);
		return _geoBlocks[blockX][blockY].nGetCell(geoX, geoY, x);
	}
	
	public final GeoCell nGetCellByLayer(final int geoX, final int geoY, final int layer)
	{
		final int blockX = GeoEngine.getBlockXY(geoX);
		final int blockY = GeoEngine.getBlockXY(geoY);
		return _geoBlocks[blockX][blockY].nGetCellByLayer(geoX, geoY, layer);
	}
	
	public final GeoBlock getBlock(final int geoX, final int geoY)
	{
		final int blockX = GeoEngine.getBlockXY(geoX);
		final int blockY = GeoEngine.getBlockXY(geoY);
		return _geoBlocks[blockX][blockY];
	}
	
	public final GeoBlock getBlockByBlockXY(final int blockX, final int blockY)
	{
		return _geoBlocks[blockX][blockY];
	}
	
	public final void setBlock(final int geoX, final int geoY, final GeoBlock block)
	{
		final int blockX = GeoEngine.getBlockXY(geoX);
		final int blockY = GeoEngine.getBlockXY(geoY);
		_geoBlocks[blockX][blockY] = block;;
	}
	
	public final int nGetLayerCount(final int geoX, final int geoY)
	{
		final int blockX = GeoEngine.getBlockXY(geoX);
		final int blockY = GeoEngine.getBlockXY(geoY);
		return _geoBlocks[blockX][blockY].nGetLayerCount(geoX, geoY);
	}
	
	public final void saveTo(final IncBufferedFileWriter writer, final boolean l2j) throws IOException
	{
		if (!l2j)
		{
			writer.writeByte((byte) (_regionX + 10));
			writer.writeByte((byte) (_regionY + 10));
			writer.writeBytes(new byte[16]); // TODO the rest
		}
		
		for (int x = 0; x < GeoEngine.GEO_REGION_SIZE; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_REGION_SIZE; y++)
			{
				_geoBlocks[x][y].saveTo(writer, l2j);
			}
		}
	}
	
	public final boolean equals(final GeoRegion geoRegion)
	{
		for (int x = 0; x < GeoEngine.GEO_REGION_SIZE; x++)
		{
			for (int y = 0; y < GeoEngine.GEO_REGION_SIZE; y++)
			{
				if (!_geoBlocks[x][y].equals(geoRegion._geoBlocks[x][y]))
					return false;
			}
		}
		return true;
	}
}