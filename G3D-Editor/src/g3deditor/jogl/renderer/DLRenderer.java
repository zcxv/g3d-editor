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

import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLCellRenderer;
import g3deditor.jogl.GLState;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class DLRenderer extends GLCellRenderer
{
	public static final boolean isAvailable(final GL2 gl)
	{
		return true;
	}
	
	public static final String NAME = "DisplayList";
	public static final String NAME_SHORT = "DL";
	
	private int _listId;
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#init(javax.media.opengl.GL2)
	 */
	@Override
	public final boolean init(final GL2 gl)
	{
		if (!super.init(gl))
			return false;
		
		_listId = gl.glGenLists(NSWE_COMBINATIONS + 1);
		
		gl.glNewList(_listId, GL2.GL_COMPILE);
		renderCellFull(gl, true, GeoEngine.NSWE_ALL);
		gl.glEndList();
		
		for (int i = 0; i < NSWE_COMBINATIONS; i++)
		{
			gl.glNewList(_listId + 1 + i, GL2.GL_COMPILE);
			renderCellFull(gl, false, i);
			gl.glEndList();
		}
		
		return true;
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#render(javax.media.opengl.GL2, g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector)
	 */
	public final void render(final GL2 gl, final GLSubRenderSelector selector)
	{
		GeoCell cell;
		for (int i = selector.getElementsToRender(); i-- > 0;)
		{
			cell = selector.getElementToRender(i);
			GLState.glColor4f(gl, cell.getSelectionState().getColor(cell));
			GLState.translatef(gl, cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
			gl.glCallList(_listId + (cell.isBig() ? 0 : cell.getNSWE() + 1));
		}
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#dispose(javax.media.opengl.GL2)
	 */
	@Override
	public final void dispose(final GL2 gl)
	{
		super.dispose(gl);
		gl.glDeleteLists(_listId, NSWE_COMBINATIONS + 1);
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