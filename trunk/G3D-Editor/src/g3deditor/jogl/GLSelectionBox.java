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
package g3deditor.jogl;

import g3deditor.geo.GeoCell;
import g3deditor.geo.blocks.GeoBlockMultiLayer;
import g3deditor.util.FastArrayList;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLSelectionBox
{
	private static final int MIN_HEIGHT = 10;
	private static final int MAX_HEIGHT = 1000;
	
	private int _geoX;
	private int _geoY;
	private int _geoZ;
	private int _height;
	private boolean _infHeight;
	
	public GLSelectionBox()
	{
		_height = MIN_HEIGHT;
	}
	
	public final void toggleInfHeight()
	{
		setInfHeight(!isInfHeight());
	}
	
	public final void setInfHeight(final boolean infHeight)
	{
		_infHeight = infHeight;
	}
	
	public final boolean isInfHeight()
	{
		return _infHeight;
	}
	
	public final boolean isInside(final GeoCell cell)
	{
		if (_geoX == Integer.MIN_VALUE)
			return false;
		
		if (cell.getBlock().getGeoX() != _geoX)
			return false;
		
		if (cell.getBlock().getGeoY() != _geoY)
			return false;
		
		if (_infHeight)
			return true;
		
		final int height = cell.getHeight();
		if (height < _geoZ - _height || height > _geoZ + _height)
			return false;
		
		return true;
	}
	
	public final void getAllCellsInside(final GeoCell reference, final GeoCell[] cells, final FastArrayList<GeoCell> store)
	{
		if (_infHeight)
		{
			store.addAll(cells);
		}
		else
		{
			final int geoZ = reference.getHeight();
			int height;
			for (final GeoCell cell : cells)
			{
				height = cell.getHeight();
				if (height >= geoZ - _height && height <= geoZ + _height)
					store.add(cell);
			}
		}
	}
	
	public final void addHeight(final int height)
	{
		int newHeight = _height + height;
		if (newHeight < MIN_HEIGHT)
			newHeight = MIN_HEIGHT;
		else if (newHeight > MAX_HEIGHT)
			newHeight = MAX_HEIGHT;
		
		_height = newHeight;
	}
	
	public final void init(final GL2 gl)
	{
		
	}
	
	public final void render(final GL2 gl, final GeoCell cell)
	{
		if (cell == null || !(cell.getBlock() instanceof GeoBlockMultiLayer))
		{
			_geoX = Integer.MIN_VALUE;
			return;
		}
		
		final float height = (isInfHeight() ? Short.MAX_VALUE : _height) / 16f;
		_geoX = cell.getBlock().getGeoX();
		_geoY = cell.getBlock().getGeoY();
		_geoZ = cell.getHeight();
		
		gl.glPushMatrix();
		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glTranslatef(_geoX, _geoZ / 16f, _geoY);
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0f, -height, 0f);
		gl.glVertex3f(0f, -height, 8f);
		gl.glVertex3f(8f, -height, 8f);
		gl.glVertex3f(8f, -height, 0f);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0f, height, 0f);
		gl.glVertex3f(0f, height, 8f);
		gl.glVertex3f(8f, height, 8f);
		gl.glVertex3f(8f, height, 0f);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0f, -height, 0f);
		gl.glVertex3f(0f, height, 0f);
		gl.glVertex3f(8f, -height, 0f);
		gl.glVertex3f(8f, height, 0f);
		gl.glVertex3f(0f, -height, 8f);
		gl.glVertex3f(0f, height, 8f);
		gl.glVertex3f(8f, -height, 8f);
		gl.glVertex3f(8f, height, 8f);
		gl.glEnd();
		
		gl.glPopMatrix();
	}
	
	public final void dispose(final GL2 gl)
	{
		
	}
}