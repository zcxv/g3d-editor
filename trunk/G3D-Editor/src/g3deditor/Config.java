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

import g3deditor.jogl.GLCellRenderSelector;
import g3deditor.jogl.GLCellRenderer;
import g3deditor.jogl.renderer.DLLoDRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class Config
{
	private static final LookAndFeelInfo[] LOOK_AND_FEEL_INFOS;

	private static final File CONFIG_FILE					= new File("./G3DEditor.ini");
	private static final ConfigProperties PROPERTIES		= new ConfigProperties();
	
	public static String PATH_TO_GEO_FILES					= "./data/geodata/";
	public static boolean TERRAIN_DEFAULT_ON				= false;
	public static int VIS_GRID_RANGE						= GLCellRenderSelector.MIN_VIS_GRID_RANGE;
	public static String LOOK_AND_FEEL						= "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static String CELL_RENDERER						= GLCellRenderer.validateRenderer(null);
	public static int DLLoDRANGE							= DLLoDRenderer.MAX_DISTANCE_SQ;
	public static boolean V_SYNC							= true;
	
	public static final LookAndFeelInfo[] getInstalledLookAndFeels()
	{
		return LOOK_AND_FEEL_INFOS;
	}
	
	public static final LookAndFeelInfo getLookAndFeel(final String className, final LookAndFeelInfo dftl)
	{
		
		for (int i = LOOK_AND_FEEL_INFOS.length; i-- > 0;)
		{
			if (LOOK_AND_FEEL_INFOS[i].getClassName().equals(className))
				return LOOK_AND_FEEL_INFOS[i];
		}
		return dftl;
	}
	
	public static final LookAndFeelInfo getActiveLookAndFeel()
	{
		return getLookAndFeel(UIManager.getLookAndFeel().getClass().getName(), null);
	}
	
	static
	{
		final LookAndFeelInfo[] temp = UIManager.getInstalledLookAndFeels();
		LOOK_AND_FEEL_INFOS = new LookAndFeelInfo[temp.length];
		for (int i = temp.length; i-- > 0;)
		{
			LOOK_AND_FEEL_INFOS[i] = new LookAndFeelInfo(temp[i].getName(), temp[i].getClassName())
			{
				@Override
				public final String toString()
				{
					return getName();
				}
			};
		}
	}
	
	public static final void load()
	{
		try
		{
			if (CONFIG_FILE.isFile())
			{
				PROPERTIES.load(CONFIG_FILE);
				
				PATH_TO_GEO_FILES		= PROPERTIES.getProperty("GeodataPath", "./data/geodata/");
				TERRAIN_DEFAULT_ON		= Boolean.parseBoolean(PROPERTIES.getProperty("TerrainDefaultOn", "false"));
				VIS_GRID_RANGE			= Integer.parseInt(PROPERTIES.getProperty("VisibleGridRange", String.valueOf(GLCellRenderSelector.MIN_VIS_GRID_RANGE)));
				LOOK_AND_FEEL			= PROPERTIES.getProperty("LookAndFeel", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				CELL_RENDERER			= PROPERTIES.getProperty("CellRenderer", GLCellRenderer.validateRenderer(null));
				DLLoDRANGE				= Integer.parseInt(PROPERTIES.getProperty("DLLoDRange", String.valueOf(DLLoDRenderer.MAX_DISTANCE_SQ)));
				V_SYNC					= Boolean.parseBoolean(PROPERTIES.getProperty("V_SYNC", "true"));
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			checkConfigs();
		}
		
		try
		{
			final LookAndFeelInfo lookAndFeelInfo = getLookAndFeel(LOOK_AND_FEEL, null);
			if (lookAndFeelInfo != null)
				UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static final void checkConfigs()
	{
		if (!new File(PATH_TO_GEO_FILES).isDirectory())
		{
			PATH_TO_GEO_FILES = "./data/geodata/";
		}
		
		if (VIS_GRID_RANGE < GLCellRenderSelector.MIN_VIS_GRID_RANGE)
		{
			VIS_GRID_RANGE = GLCellRenderSelector.MIN_VIS_GRID_RANGE;
		}
		else if (VIS_GRID_RANGE > GLCellRenderSelector.MAX_VIS_GRID_RANGE)
		{
			VIS_GRID_RANGE = GLCellRenderSelector.MAX_VIS_GRID_RANGE;
		}
		
		CELL_RENDERER = GLCellRenderer.validateRenderer(CELL_RENDERER);
		
		if (DLLoDRANGE < DLLoDRenderer.MIN_DISTANCE_SQ)
		{
			DLLoDRANGE = DLLoDRenderer.MIN_DISTANCE_SQ;
		}
		else if (DLLoDRANGE > DLLoDRenderer.MAX_DISTANCE_SQ)
		{
			DLLoDRANGE = DLLoDRenderer.MAX_DISTANCE_SQ;
		}
	}
	
	public static final void save()
	{
		PROPERTIES.clear();
		PROPERTIES.put("GeodataPath", String.valueOf(PATH_TO_GEO_FILES));
		PROPERTIES.put("TerrainDefaultOn", String.valueOf(TERRAIN_DEFAULT_ON));
		PROPERTIES.put("VisibleGridRange", String.valueOf(VIS_GRID_RANGE));
		PROPERTIES.put("LookAndFeel", LOOK_AND_FEEL);
		PROPERTIES.put("CellRenderer", CELL_RENDERER);
		PROPERTIES.put("DLLoDRange", String.valueOf(DLLoDRANGE));
		PROPERTIES.put("V_SYNC", String.valueOf(V_SYNC));
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