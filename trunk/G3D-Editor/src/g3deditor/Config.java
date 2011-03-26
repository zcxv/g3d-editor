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

import java.awt.Color;
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

	private static final File CONFIG_FILE				= new File("./G3DEditor.ini");
	private static final ConfigProperties PROPERTIES	= new ConfigProperties();
	
	public static String PATH_TO_GEO_FILES				= "./data/geodata/";
	public static boolean TERRAIN_DEFAULT_ON			= false;
	public static int VIS_GRID_RANGE					= GLCellRenderSelector.MIN_VIS_GRID_RANGE;
	public static String LOOK_AND_FEEL					= "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static String CELL_RENDERER					= GLCellRenderer.validateRenderer(null);
	public static int DLLoD_RANGE						= DLLoDRenderer.MAX_DISTANCE_SQ;
	public static boolean V_SYNC						= true;
	public static boolean USE_TRANSPARENCY				= true;
	public static boolean USE_MULTITHREADING			= Runtime.getRuntime().availableProcessors() > 1;
	public static boolean DRAW_OUTLINE					= false;
	
	public static int COLOR_FLAT_NORMAL					= Color.BLUE.getRGB();
	public static int COLOR_FLAT_HIGHLIGHTED			= Color.CYAN.getRGB();
	public static int COLOR_FLAT_SELECTED				= Color.MAGENTA.getRGB();
	public static int COLOR_COMPLEX_NORMAL				= Color.GREEN.getRGB();
	public static int COLOR_COMPLEX_HIGHLIGHTED			= Color.CYAN.getRGB();
	public static int COLOR_COMPLEX_SELECTED			= Color.MAGENTA.getRGB();
	public static int COLOR_MULTILAYER_NORMAL			= Color.RED.getRGB();
	public static int COLOR_MULTILAYER_HIGHLIGHTED		= Color.CYAN.getRGB();
	public static int COLOR_MULTILAYER_SELECTED			= Color.MAGENTA.getRGB();
	
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
				
				PATH_TO_GEO_FILES		= PROPERTIES.getProperty("PATH_TO_GEO_FILES", "./data/geodata/");
				TERRAIN_DEFAULT_ON		= Boolean.parseBoolean(PROPERTIES.getProperty("TERRAIN_DEFAULT_ON", "false"));
				VIS_GRID_RANGE			= Integer.parseInt(PROPERTIES.getProperty("VIS_GRID_RANGE", String.valueOf(GLCellRenderSelector.MIN_VIS_GRID_RANGE)));
				LOOK_AND_FEEL			= PROPERTIES.getProperty("LOOK_AND_FEEL", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				CELL_RENDERER			= PROPERTIES.getProperty("CELL_RENDERER", GLCellRenderer.validateRenderer(null));
				DLLoD_RANGE				= Integer.parseInt(PROPERTIES.getProperty("DLLoD_RANGE", String.valueOf(DLLoDRenderer.MAX_DISTANCE_SQ)));
				V_SYNC					= Boolean.parseBoolean(PROPERTIES.getProperty("V_SYNC", "true"));
				USE_TRANSPARENCY		= Boolean.parseBoolean(PROPERTIES.getProperty("USE_TRANSPARENCY", "true"));
				USE_MULTITHREADING		= Boolean.parseBoolean(PROPERTIES.getProperty("USE_MULTITHREADING", String.valueOf(Runtime.getRuntime().availableProcessors() > 1)));
				DRAW_OUTLINE			= Boolean.parseBoolean(PROPERTIES.getProperty("DRAW_OUTLINE", "false"));
				
				COLOR_FLAT_NORMAL				= Integer.parseInt(PROPERTIES.getProperty("COLOR_FLAT_NORMAL", String.valueOf(Color.BLUE.getRGB())));
				COLOR_FLAT_HIGHLIGHTED			= Integer.parseInt(PROPERTIES.getProperty("COLOR_FLAT_HIGHLIGHTED", String.valueOf(Color.CYAN.getRGB())));
				COLOR_FLAT_SELECTED				= Integer.parseInt(PROPERTIES.getProperty("COLOR_FLAT_SELECTED", String.valueOf(Color.MAGENTA.getRGB())));
				COLOR_COMPLEX_NORMAL			= Integer.parseInt(PROPERTIES.getProperty("COLOR_COMPLEX_NORMAL", String.valueOf(Color.GREEN.getRGB())));
				COLOR_COMPLEX_HIGHLIGHTED		= Integer.parseInt(PROPERTIES.getProperty("COLOR_COMPLEX_HIGHLIGHTED", String.valueOf(Color.CYAN.getRGB())));
				COLOR_COMPLEX_SELECTED			= Integer.parseInt(PROPERTIES.getProperty("COLOR_COMPLEX_SELECTED", String.valueOf(Color.MAGENTA.getRGB())));
				COLOR_MULTILAYER_NORMAL			= Integer.parseInt(PROPERTIES.getProperty("COLOR_MULTILAYER_NORMAL", String.valueOf(Color.RED.getRGB())));
				COLOR_MULTILAYER_HIGHLIGHTED	= Integer.parseInt(PROPERTIES.getProperty("COLOR_MULTILAYER_HIGHLIGHTED", String.valueOf(Color.BLUE.getRGB())));
				COLOR_MULTILAYER_SELECTED		= Integer.parseInt(PROPERTIES.getProperty("COLOR_MULTILAYER_SELECTED", String.valueOf(Color.MAGENTA.getRGB())));
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
		
		if (DLLoD_RANGE < DLLoDRenderer.MIN_DISTANCE_SQ)
		{
			DLLoD_RANGE = DLLoDRenderer.MIN_DISTANCE_SQ;
		}
		else if (DLLoD_RANGE > DLLoDRenderer.MAX_DISTANCE_SQ)
		{
			DLLoD_RANGE = DLLoDRenderer.MAX_DISTANCE_SQ;
		}
	}
	
	public static final void save()
	{
		PROPERTIES.clear();
		PROPERTIES.put("PATH_TO_GEO_FILES", String.valueOf(PATH_TO_GEO_FILES));
		PROPERTIES.put("TERRAIN_DEFAULT_ON", String.valueOf(TERRAIN_DEFAULT_ON));
		PROPERTIES.put("VIS_GRID_RANGE", String.valueOf(VIS_GRID_RANGE));
		PROPERTIES.put("LOOK_AND_FEEL", String.valueOf(LOOK_AND_FEEL));
		PROPERTIES.put("CELL_RENDERER", String.valueOf(CELL_RENDERER));
		PROPERTIES.put("DLLoD_RANGE", String.valueOf(DLLoD_RANGE));
		PROPERTIES.put("V_SYNC", String.valueOf(V_SYNC));
		PROPERTIES.put("USE_TRANSPARENCY", String.valueOf(USE_TRANSPARENCY));
		PROPERTIES.put("USE_MULTITHREADING", String.valueOf(USE_MULTITHREADING));
		PROPERTIES.put("DRAW_OUTLINE", String.valueOf(DRAW_OUTLINE));
		
		PROPERTIES.put("COLOR_FLAT_NORMAL", String.valueOf(COLOR_FLAT_NORMAL));
		PROPERTIES.put("COLOR_FLAT_HIGHLIGHTED", String.valueOf(COLOR_FLAT_HIGHLIGHTED));
		PROPERTIES.put("COLOR_FLAT_SELECTED", String.valueOf(COLOR_FLAT_SELECTED));
		PROPERTIES.put("COLOR_COMPLEX_NORMAL", String.valueOf(COLOR_COMPLEX_NORMAL));
		PROPERTIES.put("COLOR_COMPLEX_HIGHLIGHTED", String.valueOf(COLOR_COMPLEX_HIGHLIGHTED));
		PROPERTIES.put("COLOR_COMPLEX_SELECTED", String.valueOf(COLOR_COMPLEX_SELECTED));
		PROPERTIES.put("COLOR_MULTILAYER_NORMAL", String.valueOf(COLOR_MULTILAYER_NORMAL));
		PROPERTIES.put("COLOR_MULTILAYER_HIGHLIGHTED", String.valueOf(COLOR_MULTILAYER_HIGHLIGHTED));
		PROPERTIES.put("COLOR_MULTILAYER_SELECTED", String.valueOf(COLOR_MULTILAYER_SELECTED));
		
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
				super.store(fw, "G3DEditor Config");
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