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
package g3deditor.jogl.renderer;

import g3deditor.Config;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLCamera;
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLCellRenderer;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.GLState;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class DLLoDRenderer extends GLCellRenderer
{
	public static final String NAME = "DisplayList LoD";
	public static final String NAME_SHORT = "DLLoD";
	
	public static final int MAX_DISTANCE_SQ = 1024 * 8;
	public static final int MIN_DISTANCE_SQ = 0;
	
	private static final int ID_BIG_FULL = 0;
	private static final int ID_BIG_TOP = 1;
	private static final int ID_BIG_BOTTOM = 2;
	private static final int ID_SMALL_BOTTOM = 3;
	private static final int ID_SMALL_FULL_OFFSET = 4;
	private static final int ID_SMALL_TOP_OFFSET = ID_SMALL_FULL_OFFSET + NSWE_COMBINATIONS;
	private static final int ID_MAX_COMBINATIONS = ID_SMALL_FULL_OFFSET + NSWE_COMBINATIONS * 2;
	
	private int _listId;
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#init(javax.media.opengl.GL2)
	 */
	@Override
	public final void init(final GL2 gl)
	{
		super.init(gl);
		_listId = gl.glGenLists(ID_MAX_COMBINATIONS);
		
		gl.glNewList(_listId + ID_BIG_FULL, GL2.GL_COMPILE);
		renderCellFull(gl, true, GeoEngine.NSWE_ALL);
		gl.glEndList();
		
		gl.glNewList(_listId + ID_BIG_TOP, GL2.GL_COMPILE);
		renderCellTop(gl, true, GeoEngine.NSWE_ALL);
		gl.glEndList();
		
		gl.glNewList(_listId + ID_BIG_BOTTOM, GL2.GL_COMPILE);
		renderCellBottom(gl, true, 0f);
		gl.glEndList();
		
		gl.glNewList(_listId + ID_SMALL_BOTTOM, GL2.GL_COMPILE);
		renderCellBottom(gl, false, 0f);
		gl.glEndList();
		
		for (int i = 0; i < NSWE_COMBINATIONS; i++)
		{
			gl.glNewList(_listId + ID_SMALL_FULL_OFFSET + i, GL2.GL_COMPILE);
			renderCellFull(gl, false, i);
			gl.glEndList();
			
			gl.glNewList(_listId + ID_SMALL_TOP_OFFSET + i, GL2.GL_COMPILE);
			renderCellTop(gl, false, i);
			gl.glEndList();
		}
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#render(javax.media.opengl.GL2, g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector)
	 */
	public final void render(final GL2 gl, final GLSubRenderSelector selector)
	{
		final GLCamera camera = GLDisplay.getInstance().getCamera();
		GeoCell cell;
		
		if (Config.DLLoD_RANGE > 0)
		{
			float distSq, dx, dy, dz;
			for (int i = selector.getElementsToRender(); i-- > 0;)
			{
				cell = selector.getElementToRender(i);
				dx = cell.getRenderX() - camera.getX();
				dy = cell.getRenderY() - camera.getY();
				dz = cell.getRenderZ() - camera.getZ();
				distSq =  dx * dx + dy * dy + dz * dz;
				
				if (distSq > Config.DLLoD_RANGE)
				{
					GLState.glColor4f(gl, cell.getSelectionState().getColor(cell));
					GLState.translatef(gl, cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
					
					if (cell.isBig())
					{
						gl.glCallList(_listId + (camera.getY() > cell.getRenderY() ? ID_BIG_TOP : ID_BIG_BOTTOM));
					}
					else
					{
						gl.glCallList(_listId + (camera.getY() > cell.getRenderY() ? ID_SMALL_TOP_OFFSET + cell.getNSWE() : ID_SMALL_BOTTOM));
					}
				}
				else
				{
					// Any other cell is close enough, so save cpu time for checking it
					++i;
					while (i-- > 0)
					{
						cell = selector.getElementToRender(i);
						GLState.glColor4f(gl, cell.getSelectionState().getColor(cell));
						GLState.translatef(gl, cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
						
						if (cell.isBig())
						{
							gl.glCallList(_listId + ID_BIG_FULL);
						}
						else
						{
							gl.glCallList(_listId + ID_SMALL_FULL_OFFSET + cell.getNSWE());
						}
					}
					break;
				}
			}
		}
		else
		{
			for (int i = selector.getElementsToRender(); i-- > 0;)
			{
				cell = selector.getElementToRender(i);
				GLState.glColor4f(gl, cell.getSelectionState().getColor(cell));
				GLState.translatef(gl, cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
				
				if (cell.isBig())
				{
					gl.glCallList(_listId + (camera.getY() > cell.getRenderY() ? ID_BIG_TOP : ID_BIG_BOTTOM));
				}
				else
				{
					gl.glCallList(_listId + (camera.getY() > cell.getRenderY() ? ID_SMALL_TOP_OFFSET + cell.getNSWE() : ID_SMALL_BOTTOM));
				}
			}
		}
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#dispose(javax.media.opengl.GL2)
	 */
	@Override
	public final void dispose(final GL2 gl)
	{
		super.dispose(gl);
		gl.glDeleteLists(_listId, ID_MAX_COMBINATIONS);
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#getName()
	 */
	@Override
	public final String getName()
	{
		return NAME;
	}
	
	@Override
	public final String toString()
	{
		return NAME_SHORT;
	}
}