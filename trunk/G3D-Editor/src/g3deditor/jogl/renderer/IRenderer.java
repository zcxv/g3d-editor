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

import g3deditor.entity.CellColor;
import g3deditor.geo.GeoCell;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.GLCellRenderer;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class IRenderer extends GLCellRenderer
{
	/**
	 * @param display
	 */
	public IRenderer(final GLDisplay display)
	{
		super(display);
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#init(javax.media.opengl.GL2)
	 */
	@Override
	public final void init(final GL2 gl)
	{
		
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#enableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void enableRender(final GL2 gl)
	{
		
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#render(javax.media.opengl.GL2, g3deditor.geo.GeoCell)
	 */
	public final void render(final GL2 gl, final GeoCell cell)
	{
		final CellColor color = cell.getSelectionState().getColor(cell);
		gl.glPushMatrix();
		gl.glColor4f(color.getR(), color.getG(), color.getB(), COLOR_ALPHA);
		gl.glTranslatef(cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
		renderCell(gl, cell.isBig(), cell.getNSWE());
		gl.glPopMatrix();
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#disableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void disableRender(final GL2 gl)
	{
		
	}
	
	/**
	 * @see g3deditor.jogl.GLCellRenderer#dispose(javax.media.opengl.GL2)
	 */
	@Override
	public final void dispose(final GL2 gl)
	{
		
	}
	
	@Override
	public final String toString()
	{
		return "Immediate";
	}
}