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
import g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector;
import g3deditor.jogl.GLCellRenderer;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class IRenderer extends GLCellRenderer
{
	public static final String NAME = "Immediate";
	public static final String NAME_SHORT = NAME;
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#render(javax.media.opengl.GL2, g3deditor.jogl.GLCellRenderSelector.GLSubRenderSelector)
	 */
	public final void render(final GL2 gl, final GLSubRenderSelector selector)
	{
		GeoCell cell;
		for (int i = selector.getElementsToRender(); i-- > 0;)
		{
			cell = selector.getElementToRender(i);
			setColor(gl, cell.getSelectionState().getColor(cell));
			translatef(gl, cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
			renderCellFull(gl, cell.isBig(), cell.getNSWE());
		}
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