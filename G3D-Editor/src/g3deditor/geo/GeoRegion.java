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
import g3deditor.geo.blocks.GeoBlockMultiLayer;
import g3deditor.swing.DialogSave;
import g3deditor.util.GeoByteBuffer;
import g3deditor.util.GeoReader;
import g3deditor.util.GeoStreamWriter;
import g3deditor.util.GeoWriter;
import g3deditor.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoRegion
{
	public static final byte getType(final GeoReader reader, final boolean l2j)
	{
		if (l2j)
			return reader.get();
		
		switch (reader.getShort())
		{
			case 0x0000:
				return GeoEngine.GEO_BLOCK_TYPE_FLAT;
				
			case 0x0040:
				return GeoEngine.GEO_BLOCK_TYPE_COMPLEX;
				
			default:
				return GeoEngine.GEO_BLOCK_TYPE_MULTILAYER;
		}
	}
	
	public static final void putType(final GeoWriter writer, final boolean l2j, final byte type)
	{
		if (l2j)
		{
			writer.put(type);
		}
		else
		{
			switch (type)
			{
				case GeoEngine.GEO_BLOCK_TYPE_FLAT:
					writer.putShort((short) 0x0000);
					break;
					
				case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
					writer.putShort((short) 0x0040);
					break;
					
				case GeoEngine.GEO_BLOCK_TYPE_MULTILAYER:
					writer.putShort((short) 0x0080); // TODO check this
					break;
					
				default:
					throw new IllegalArgumentException("Unkown type: " + type);
			}
		}
	}
	
	private final File _file;
	private final int _regionX;
	private final int _regionY;
	private final int _minGeoX;
	private final int _maxGeoX;
	private final int _minGeoY;
	private final int _maxGeoY;
	private GeoBlock[][] _geoBlocks;
	private GeoByteBuffer[][] _geoBlocksData;
	
	public GeoRegion(final int regionX, final int regionY, final GeoReader reader, final boolean l2j, final File file)
	{
		_file = file;
		_regionX = regionX;
		_regionY = regionY;
		_minGeoX = GeoEngine.getGeoXY(regionX, 0);
		_maxGeoX = GeoEngine.getGeoXY(regionX, GeoEngine.GEO_REGION_SIZE - 1);
		_minGeoY = GeoEngine.getGeoXY(regionY, 0);
		_maxGeoY = GeoEngine.getGeoXY(regionY, GeoEngine.GEO_REGION_SIZE - 1);
		_geoBlocks = new GeoBlock[GeoEngine.GEO_REGION_SIZE][GeoEngine.GEO_REGION_SIZE];
		_geoBlocksData = new GeoByteBuffer[GeoEngine.GEO_REGION_SIZE][GeoEngine.GEO_REGION_SIZE];
		
		GeoBlock block;
		GeoByteBuffer writer;
		for (int blockX = 0, blockY; blockX < GeoEngine.GEO_REGION_SIZE; blockX++)
		{
			for (blockY = 0; blockY < GeoEngine.GEO_REGION_SIZE; blockY++)
			{
				block = readBlock(blockX, blockY, reader, l2j);
				_geoBlocks[blockX][blockY] = block;
				writer = GeoByteBuffer.allocate(block.getRequiredCapacity(true));
				block.writeTo(writer, true);
				_geoBlocksData[blockX][blockY] = writer;
			}
		}
	}
	
	private final GeoBlock readBlock(final int blockX, final int blockY, final GeoReader reader, final boolean l2j)
	{
		final int geoX = GeoEngine.getGeoXY(_regionX, blockX);
		final int geoY = GeoEngine.getGeoXY(_regionY, blockY);
		final int type = getType(reader, l2j);
		switch (type)
		{
			case GeoEngine.GEO_BLOCK_TYPE_FLAT:
				return new GeoBlockFlat(reader, geoX, geoY, l2j).setRegion(this);
				
			case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
				return new GeoBlockComplex(reader, geoX, geoY, l2j).setRegion(this);
				
			case GeoEngine.GEO_BLOCK_TYPE_MULTILAYER:
				return new GeoBlockMultiLayer(reader, geoX, geoY, l2j).setRegion(this);
				
			default:
				throw new RuntimeException("Unknown type: " + type);
		}
	}
	
	public final void convertBlock(final GeoBlock block, final byte type)
	{
		final int blockX = block.getBlockX();
		final int blockY = block.getBlockY();
		final GeoBlock convertedBlock;
		
		switch (type)
		{
			case GeoEngine.GEO_BLOCK_TYPE_FLAT:
				convertedBlock = GeoBlockFlat.convertFrom(block).setRegion(this);
				break;
				
			case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
				convertedBlock = GeoBlockComplex.convertFrom(block).setRegion(this);
				break;
				
			case GeoEngine.GEO_BLOCK_TYPE_MULTILAYER:
				convertedBlock = GeoBlockMultiLayer.convertFrom(block).setRegion(this);
				break;
				
			default:
				throw new IllegalArgumentException("Unkown type: " + type);
		}
		
		block.unload();
		_geoBlocks[blockX][blockY] = convertedBlock;
	}
	
	public final void convertBlock(final int blockX, final int blockY, final byte type)
	{
		convertBlock(_geoBlocks[blockX][blockY], type);
	}
	
	public final void restoreBlock(final GeoBlock block)
	{
		final int blockX = block.getBlockX();
		final int blockY = block.getBlockY();
		final GeoByteBuffer reader = _geoBlocksData[blockX][blockY];
		reader.clear();
		block.unload();
		_geoBlocks[blockX][blockY] = readBlock(blockX, blockY, reader, true);
	}
	
	public final void restoreBlock(final int blockX, final int blockY)
	{
		restoreBlock(_geoBlocks[blockX][blockY]);
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
	
	public final GeoCell nGetCellChecked(int geoX, int geoY, final int x)
	{
		if (geoX < _minGeoX || geoX > _maxGeoX || geoY < _minGeoY || geoY > _maxGeoY)
			return null;
		
		return nGetCell(geoX, geoY, x);
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
	
	public final void saveTo(final OutputStream os, final boolean l2j, final DialogSave observ) throws IOException
	{
		if (!l2j)
		{
			Util.writeByte(_regionX + 10, os);
			Util.writeByte(_regionY + 10, os);
			
			// TODO put real data here
			Util.writeBytes(new byte[16], os);
		}
		
		final GeoWriter writer = GeoStreamWriter.wrap(os);
		for (int blockX = 0, blockY; blockX < GeoEngine.GEO_REGION_SIZE; blockX++)
		{
			for (blockY = 0; blockY < GeoEngine.GEO_REGION_SIZE; blockY++)
			{
				_geoBlocks[blockX][blockY].writeTo(writer, l2j);
				observ.updateProgressRegion(blockX * GeoEngine.GEO_REGION_SIZE + blockY, "[" + blockX + "-" + blockY + "]");
			}
		}
	}
	
	public final void unload()
	{
		GeoBlock[] block1D;
		GeoByteBuffer[] geoBlocksData1D;
		for (int blockX = GeoEngine.GEO_REGION_SIZE, blockY; blockX-- > 0;)
		{
			block1D = _geoBlocks[blockX];
			geoBlocksData1D = _geoBlocksData[blockX];
			for (blockY = GeoEngine.GEO_REGION_SIZE; blockY-- > 0;)
			{
				block1D[blockY].unload();
				block1D[blockY] = null;
				geoBlocksData1D[blockY] = null;
			}
			_geoBlocks[blockX] = null;
			_geoBlocksData[blockX] = null;
		}
		_geoBlocks = null;
		_geoBlocksData = null;
	}
	
	public final boolean dataEqualFor(final GeoBlock block)
	{
		final GeoByteBuffer geoBlockData = _geoBlocksData[block.getBlockX()][block.getBlockY()];
		geoBlockData.clear();
		return block.dataEquals(geoBlockData);
	}
	
	public final boolean allDataEqual()
	{
		GeoBlock[] block1D;
		GeoByteBuffer[] geoBlocksData1D;
		GeoByteBuffer geoBlockData;
		for (int blockX = GeoEngine.GEO_REGION_SIZE, blockY; blockX-- > 0;)
		{
			block1D = _geoBlocks[blockX];
			geoBlocksData1D = _geoBlocksData[blockX];
			for (blockY = GeoEngine.GEO_REGION_SIZE; blockY-- > 0;)
			{
				geoBlockData = geoBlocksData1D[blockY];
				geoBlockData.clear();
				if (!block1D[blockY].dataEquals(geoBlockData))
					return false;
			}
		}
		return true;
	}
}