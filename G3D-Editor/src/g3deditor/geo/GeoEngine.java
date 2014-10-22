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

import g3deditor.Config;
import g3deditor.exceptions.GeoDataNotFoundException;
import g3deditor.exceptions.GeoDataNotLoadedException;
import g3deditor.exceptions.GeoFileLoadException;
import g3deditor.exceptions.GeoFileNotFoundException;
import g3deditor.util.GeoReader;
import g3deditor.util.GeoStreamReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.regex.Pattern;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GeoEngine
{
	public static final FileFilter OFF_GEO_FILE_FILTER = new FileFilter()
	{
		/**
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public final boolean accept(final File file)
		{
			return file.isFile() && file.getName().toLowerCase().endsWith(".dat") && hasValidL2OffHeader(file);
		}
	};
	
	public static final FileFilter GEO_FILE_FILTER = new FileFilter()
	{
		private final Pattern L2j_Pattern = Pattern.compile("\\d\\d_\\d\\d.l2j");
		
		/**
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public final boolean accept(final File file)
		{
			if (!file.isFile())
				return false;
			
			final String name = file.getName().toLowerCase();
			
			if (name.endsWith(".dat"))
			{
				return hasValidL2OffHeader(file);
			}
			
			if (name.endsWith(".l2j"))
			{
				return L2j_Pattern.matcher(name).matches();
			}
			
			return false;
		}
	};
	
	public static final int CELL_SHIFT = 4; // 16 units each cell
	public static final int GEO_BLOCK_SHIFT = 8; // 8 cells each block
	public static final int GEO_REGION_SIZE = 1 << GEO_BLOCK_SHIFT;
	public static final int GEO_REGION_MIN_FILE_SIZE = (GEO_REGION_SIZE * GEO_REGION_SIZE) * 3;
	public static final int GEO_BLOCK_REGION_SHIFT = 11;
	
	public static final byte GEO_BLOCK_TYPE_FLAT = 0;
	public static final byte GEO_BLOCK_TYPE_COMPLEX = 1;
	public static final byte GEO_BLOCK_TYPE_MULTILAYER = 2;
	
	public static final short HEIGHT_MAX_VALUE = 16376;
	public static final short HEIGHT_MIN_VALUE = -16384;
	
	public static final int NSWE_MASK = 0x0000000F;
	public static final int HEIGHT_MASK = 0x0000FFF0;
	
	public static final byte EAST = 1 << 0;
	public static final byte WEST = 1 << 1;
	public static final byte SOUTH = 1 << 2;
	public static final byte NORTH = 1 << 3;
	
	public static final byte NEAST = ~EAST & NSWE_MASK;
	public static final byte NWEST = ~WEST & NSWE_MASK;
	public static final byte NSOUTH = ~SOUTH & NSWE_MASK;
	public static final byte NNORTH = ~NORTH & NSWE_MASK;
	
	public static final byte NORTHWEST = NORTH | WEST;
	public static final byte NORTHEAST = NORTH | EAST;
	public static final byte SOUTHWEST = SOUTH | WEST;
	public static final byte SOUTHEAST = SOUTH | EAST;
	
	public static final int MAP_MIN_X = -327680;
	public static final int MAP_MAX_X = 229376;
	public static final int MAP_MIN_Y = -262144;
	public static final int MAP_MAX_Y = 294912;
	
	public static final byte NSWE_NONE = 0;
	public static final byte NSWE_ALL = EAST | WEST | SOUTH | NORTH;
	
	public static final int GEOX_MIN = ((MAP_MIN_X - MAP_MIN_X) >> CELL_SHIFT);
	public static final int GEOX_MAX = ((MAP_MAX_X - MAP_MIN_X) >> CELL_SHIFT);
	public static final int GEOY_MIN = ((MAP_MIN_Y - MAP_MIN_Y) >> CELL_SHIFT);
	public static final int GEOY_MAX = ((MAP_MAX_Y - MAP_MIN_Y) >> CELL_SHIFT);
	
	public static final int getGeoX(final int x)
	{
		return (x - MAP_MIN_X) >> CELL_SHIFT;
	}
	
	public static final int getGeoY(final int y)
	{
		return (y - MAP_MIN_Y) >> CELL_SHIFT;
	}
	
	public static final int getWorldX(final int geoX)
	{
		return (geoX << CELL_SHIFT) + MAP_MIN_X;
	}
	
	public static final int getWorldY(final int geoY)
	{
		return (geoY << CELL_SHIFT) + MAP_MIN_Y;
	}
	
	public static final int getBlockXY(final int geoXY)
	{
		return (geoXY >> 3) % GEO_REGION_SIZE;
	}
	
	public static final int getCellXY(final int geoXY)
	{
		return geoXY % GEO_BLOCK_SHIFT;
	}
	
	public static final int getRegionXY(final int geoXY)
	{
		return geoXY >> GEO_BLOCK_REGION_SHIFT;
	}
	
	public static final int getGeoXY(final int regionXY, final int blockXY)
	{
		return (regionXY << GEO_BLOCK_REGION_SHIFT) + (blockXY << 3);
	}
	
	public static final int getBlockIndex(final int blockX, final int blockY)
	{
		return (blockX << GeoEngine.GEO_BLOCK_SHIFT) + blockY;
	}
	
	public static final int getCellIndex(final int cellX, final int cellY)
	{
		return (cellX << 3) + cellY;
	}
	
	public static final short getHeight(short height)
	{
		height &= HEIGHT_MASK;
		height >>= 1;
		return height;
	}
	
	public static final short getHeight(final int height)
	{
		return getHeight((short) height);
	}
	
	public static final short getNSWE(final short heightAndNSWE)
	{
		return (short) (heightAndNSWE & NSWE_MASK);
	}
	
	public static final short getNSWE(final int heightAndNSWE)
	{
		return (short) (heightAndNSWE & NSWE_MASK);
	}
	
	public static final short convertHeightToHeightAndNSWEALL(short height)
	{
		height <<= 1;
		height &= GeoEngine.HEIGHT_MASK;
		height |= NSWE_ALL;
		return height;
	}
	
	public static final short updateHeightOfHeightAndNSWE(final short heightAndNSWE, short height)
	{
		height <<= 1;
		height &= GeoEngine.HEIGHT_MASK;
		height |= GeoEngine.getNSWE(heightAndNSWE);
		return height;
	}
	
	public static final short updateNSWEOfHeightAndNSWE(short heightAndNSWE, final short NSWE)
	{
		heightAndNSWE &= GeoEngine.HEIGHT_MASK;
		heightAndNSWE |= NSWE;
		return heightAndNSWE;
	}
	
	public static final short getGeoHeightOfHeight(short height)
	{
		if (height <= HEIGHT_MIN_VALUE)
			return HEIGHT_MIN_VALUE;
		
		if (height >= HEIGHT_MAX_VALUE)
			return HEIGHT_MAX_VALUE;
		
		height <<= 1;
		height &= GeoEngine.HEIGHT_MASK;
		height >>= 1;
		return height;
	}
	
	public static final boolean layersValid(final int layers)
	{
		return layers > 0 && layers <= Byte.MAX_VALUE;
	}
	
	public static final String nameOfNSWE(final int NSWE)
	{
		if ((NSWE & NSWE_ALL) == NSWE_ALL)
		{
			return "NSWE";
		}
		String nswe = "";
		
		if ((NSWE & NORTH) == NORTH)
			nswe += "N";
		
		if ((NSWE & SOUTH) == SOUTH)
			nswe += "S";
		
		if ((NSWE & WEST) == WEST)
			nswe += "W";
		
		if ((NSWE & EAST) == EAST)
			nswe += "E";
		
		if (nswe.isEmpty())
			nswe = "NONE";
		
		return nswe;
	}
	
	public static final boolean checkNSWE(final short NSWE, final int x1, final int y1, final int x2, final int y2)
	{
		if ((NSWE & NSWE_ALL) == NSWE_ALL)
			return true;
		
		if (x2 > x1)
		{
			if ((NSWE & EAST) == 0)
				return false;
		}
		else if (x2 < x1)
		{
			if ((NSWE & WEST) == 0)
				return false;
		}
		
		if (y2 > y1)
		{
			if ((NSWE & SOUTH) == 0)
				return false;
		}
		else if (y2 < y1)
		{
			if ((NSWE & NORTH) == 0)
				return false;
		}
		
		return true;
	}
	
	public static final boolean hasGeoFile(int regionX, int regionY, final boolean l2j)
	{
		regionX += 10;
		regionY += 10;
		if (l2j)
		{
			return new File(Config.PATH_TO_GEO_FILES, regionX + "_" + regionY + ".l2j").isFile();
		}
		return searchL2OffGeoFile(regionX, regionY) != null;
	}
	
	public static final File searchL2OffGeoFile(final int regionX, final int regionY)
	{
		final File geoFile = new File(Config.PATH_TO_GEO_FILES);
		if (!geoFile.isDirectory())
			return null;
		
		for (final File file : geoFile.listFiles(OFF_GEO_FILE_FILTER))
		{
			final int[] header = getL2OffHeader(file);
			if (header != null && header[0] == regionX && header[1] == regionY)
				return file;
		}
		return null;
	}
	
	public static final boolean hasValidL2OffHeader(final File file)
	{
		final int[] header = getL2OffHeader(file);
		return header != null && header[0] >= 10 && header[0] <= 26 && header[1] >= 10 && header[1] <= 25;
	}
	
	public static final int[] getL2OffHeader(final File file)
	{
		int[] retval;
		
		try (FileInputStream fis = new FileInputStream(file))
		{
			retval = new int[] { fis.read(), fis.read() };
		}
		catch (final Exception e)
		{
			retval = null;
		}
		return retval;
	}
	
	public static final int[] getHeaderOfL2jOrL2Off(final File file)
	{
		String name = file.getName().toLowerCase();
		if (name.endsWith(".dat"))
			return getL2OffHeader(file);
		
		name = name.substring(0, name.lastIndexOf(".l2j"));
		final String[] header = name.split("_");
		return new int[]
		{
			Integer.parseInt(header[0]),
			Integer.parseInt(header[1])
		};
	}
	
	private static GeoEngine _instance;
	
	public static final void init()
	{
		_instance = new GeoEngine();
	}
	
	public static final GeoEngine getInstance()
	{
		return _instance;
	}
	
	private GeoRegion _activeRegion;
	
	public GeoEngine()
	{
	}
	
	public final void unload()
	{
		final GeoRegion region = _activeRegion;
		if (region != null)
		{
			GeoBlockSelector.getInstance().unload();
			region.unload();
		}
		
		_activeRegion = null;
	}
	
	public final void reloadGeo(final int regionX, final int regionY, final boolean l2j, final File file) throws Exception
	{
		if (file == null || !file.isFile())
			throw new GeoFileNotFoundException(file, l2j);
		
		if (_activeRegion != null)
			throw new RuntimeException("Geo must be unloaded first");
		
		try (FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis))
		{
			final GeoReader reader = GeoStreamReader.wrap(bis);
			
			if (!l2j)
			{
				for (int i = 18; i-- > 0;)
				{
					reader.get();
				}
			}
			
			_activeRegion = new GeoRegion(regionX, regionY, reader, l2j, file);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			throw new GeoFileLoadException(file, l2j, e);
		}
	}
	
	public final void reloadGeo(final int regionX, final int regionY, final boolean l2j) throws Exception
	{
		final File file = l2j
			? new File(Config.PATH_TO_GEO_FILES, (regionX + 10) + "_" + (regionY + 10) + ".l2j")
			: searchL2OffGeoFile((regionX + 10), (regionY + 10));
		reloadGeo(regionX, regionY, l2j, file);
	}
	
	public final GeoRegion getActiveRegion()
	{
		return _activeRegion;
	}
	
	private final GeoRegion getGeoRegion(final int geoX, final int geoY)
	{
		final GeoRegion region = _activeRegion;
		if (region == null)
			throw new GeoDataNotLoadedException(geoX, geoY);
		
		final int regionX = GeoEngine.getRegionXY(geoX);
		final int regionY = GeoEngine.getRegionXY(geoY);
		if (regionX != region.getRegionX() || regionY != region.getRegionY())
			throw new GeoDataNotFoundException(geoX, geoY);
		
		return region;
	}
	
	public final boolean nHasGeo(final int geoX, final int geoY)
	{
		return getGeoRegion(geoX, geoY) != null;
	}
	
	public final byte nGetType(final int geoX, final int geoY)
	{
		final GeoRegion region = getGeoRegion(geoX, geoY);
		if (region != null)
		{
			return region.nGetType(geoX, geoY);
		}
		return GeoEngine.GEO_BLOCK_TYPE_FLAT;
	}
	
	public final GeoCell nGetCell(final int geoX, final int geoY, final int z)
	{
		final GeoRegion region = getGeoRegion(geoX, geoY);
		if (region != null)
		{
			return region.nGetCell(geoX, geoY, z);
		}
		throw new GeoDataNotFoundException(geoX, geoY);
	}
	
	public final GeoBlock getBlock(final int geoX, final int geoY)
	{
		final GeoRegion region = getGeoRegion(geoX, geoY);
		if (region != null)
		{
			return region.getBlock(geoX, geoY);
		}
		throw new GeoDataNotFoundException(geoX, geoY);
	}
	
	public final int nGetLayerCount(final int geoX, final int geoY)
	{
		final GeoRegion region = getGeoRegion(geoX, geoY);
		if (region != null)
		{
			return region.nGetLayerCount(geoX, geoY);
		}
		return 1;
	}
}