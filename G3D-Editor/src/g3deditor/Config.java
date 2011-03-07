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
package g3deditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class Config
{
	private static final File CONFIG_FILE					= new File("./config.dat");
	private static final ConfigProperties PROPERTIES		= new ConfigProperties();
	
	public static String PATH_TO_GEO_FILES					= "./data/geodata/";
	public static boolean SHOW_EXIT_PROMT					= false;
	
	public static boolean OPTIMIZE_FOR_1024x768				= false;
	
	public static boolean TERRAIN_DEFAULT_ON				= false;
	//public static TerrainDetailLevel TERRAIN_DETAIL_LEVEL	= TerrainDetailLevel.LOW;
	public static boolean TERRAIN_WIREFRAME					= false;
	
	public static int VIS_GRID_RANGE						= 8;//DisplayGrid.MIN_VIS_GRID_RANGE;
	public static int VIS_GRID_HEIGHT						= 8;//DisplayGrid.MIN_VIS_GRID_HEIGHT;
	
	public static final void load()
	{
		try
		{
			PROPERTIES.load(CONFIG_FILE);
			
			PATH_TO_GEO_FILES		= PROPERTIES.getProperty("GeodataPath", "./data/geodata/");
			SHOW_EXIT_PROMT			= Boolean.parseBoolean(PROPERTIES.getProperty("ShowExitPromt", "false"));
			OPTIMIZE_FOR_1024x768	= Boolean.parseBoolean(PROPERTIES.getProperty("OptimizeFor1024x768", "false"));
			
			TERRAIN_DEFAULT_ON		= Boolean.parseBoolean(PROPERTIES.getProperty("TerrainDefaultOn", "false"));
			//TERRAIN_DETAIL_LEVEL	= TerrainDetailLevel.valueOf(PROPERTIES.getProperty("TerrainDetailLevel", TerrainDetailLevel.LOW.name()));
			TERRAIN_WIREFRAME		= Boolean.parseBoolean(PROPERTIES.getProperty("TerrainWireframe", "false"));
			
			VIS_GRID_RANGE			= Integer.parseInt(PROPERTIES.getProperty("VisibleGridRange", String.valueOf(8)));//DisplayGrid.MIN_VIS_GRID_RANGE)));
			VIS_GRID_HEIGHT			= Integer.parseInt(PROPERTIES.getProperty("VisibleGridHeight", String.valueOf(8)));//DisplayGrid.MIN_VIS_GRID_HEIGHT)));
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			checkConfigs();
		}
	}
	
	private static final void checkConfigs()
	{
		if (!new File(PATH_TO_GEO_FILES).isDirectory())
		{
			PATH_TO_GEO_FILES = "./data/geodata/";
		}
		
		/*if (VIS_GRID_RANGE < DisplayGrid.MIN_VIS_GRID_RANGE)
		{
			VIS_GRID_RANGE = DisplayGrid.MIN_VIS_GRID_RANGE;
		}
		else if (VIS_GRID_RANGE > DisplayGrid.MAX_VIS_GRID_RANGE)
		{
			VIS_GRID_RANGE = DisplayGrid.MAX_VIS_GRID_RANGE;
		}
		
		if (VIS_GRID_HEIGHT < DisplayGrid.MIN_VIS_GRID_HEIGHT)
		{
			VIS_GRID_HEIGHT = DisplayGrid.MIN_VIS_GRID_HEIGHT;
		}
		else if (VIS_GRID_HEIGHT > DisplayGrid.MAX_VIS_GRID_HEIGHT)
		{
			VIS_GRID_HEIGHT = DisplayGrid.MAX_VIS_GRID_HEIGHT;
		}*/
	}
	
	public static final void save()
	{
		PROPERTIES.clear();
		PROPERTIES.put("GeodataPath", String.valueOf(PATH_TO_GEO_FILES));
		PROPERTIES.put("ShowExitPromt", String.valueOf(SHOW_EXIT_PROMT));
		PROPERTIES.put("OptimizeFor1024x768", String.valueOf(OPTIMIZE_FOR_1024x768));
		
		PROPERTIES.put("TerrainDefaultOn", String.valueOf(TERRAIN_DEFAULT_ON));
		//PROPERTIES.put("TerrainDetailLevel", TERRAIN_DETAIL_LEVEL.name());
		PROPERTIES.put("TerrainWireframe", String.valueOf(TERRAIN_WIREFRAME));
		
		PROPERTIES.put("VisibleGridRange", String.valueOf(VIS_GRID_RANGE));
		PROPERTIES.put("VisibleGridHeight", String.valueOf(VIS_GRID_HEIGHT));
		PROPERTIES.save(CONFIG_FILE);
	}
	
	@SuppressWarnings("serial")
	private static final class ConfigProperties extends Properties
	{
		ConfigProperties()
		{
			
		}
		
		final void load(final File file) throws Exception
		{
			FileInputStream fis = null;
			
			try
			{
				fis = new FileInputStream(file);
				super.clear();
				super.load(fis);
			}
			finally
			{
				try
				{
					fis.close();
				}
				catch (Exception e)
				{
					
				}
			}
		}
		
		final void save(final File file)
		{
			FileWriter fw = null;
			
			try
			{
				fw = new FileWriter(file);
				super.store(fw, "L2j - G3DEditor Config");
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					fw.close();
				}
				catch (final Exception e)
				{
					
				}
			}
		}
		
		@Override
		public final String getProperty(final String key, final String defaultValue)
		{
			final String property = super.getProperty(key);
			if (property == null)
			{
				return defaultValue;
			}
			else
			{
				return property;
			}
		}
	}
}