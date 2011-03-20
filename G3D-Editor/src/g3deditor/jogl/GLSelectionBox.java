package g3deditor.jogl;

import g3deditor.geo.GeoCell;
import g3deditor.geo.blocks.GeoBlockMultiLevel;

import javax.media.opengl.GL2;

public final class GLSelectionBox
{
	private static final int MIN_HEIGHT = 10;
	private static final int MAX_HEIGHT = 1000;
	
	private int _geoX;
	private int _geoY;
	private int _geoZ;
	private int _height;
	
	public GLSelectionBox()
	{
		_height = MIN_HEIGHT;
	}
	
	public final boolean isInside(final GeoCell cell)
	{
		if (_geoX == Integer.MIN_VALUE)
			return false;
		
		if (cell.getBlock().getGeoX() != _geoX)
			return false;
		
		if (cell.getBlock().getGeoY() != _geoY)
			return false;
		
		final int height = cell.getHeight();
		if (height < _geoZ - _height || height > _geoZ + _height)
			return false;
		
		return true;
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
		if (cell == null || !(cell.getBlock() instanceof GeoBlockMultiLevel))
		{
			_geoX = Integer.MIN_VALUE;
			return;
		}
		
		_geoX = cell.getBlock().getGeoX();
		_geoY = cell.getBlock().getGeoY();
		_geoZ = cell.getHeight();
		
		gl.glPushMatrix();
		gl.glColor4f(1f, 1f, 1f, 1f);
		gl.glTranslatef(_geoX, _geoZ / 16f, _geoY);
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0f, -_height / 16f, 0f);
		gl.glVertex3f(0f, -_height / 16f, 8f);
		gl.glVertex3f(8f, -_height / 16f, 8f);
		gl.glVertex3f(8f, -_height / 16f, 0f);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0f, _height / 16f, 0f);
		gl.glVertex3f(0f, _height / 16f, 8f);
		gl.glVertex3f(8f, _height / 16f, 8f);
		gl.glVertex3f(8f, _height / 16f, 0f);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0f, -_height / 16f, 0f);
		gl.glVertex3f(0f, _height / 16f, 0f);
		gl.glVertex3f(8f, -_height / 16f, 0f);
		gl.glVertex3f(8f, _height / 16f, 0f);
		gl.glVertex3f(0f, -_height / 16f, 8f);
		gl.glVertex3f(0f, _height / 16f, 8f);
		gl.glVertex3f(8f, -_height / 16f, 8f);
		gl.glVertex3f(8f, _height / 16f, 8f);
		gl.glEnd();
		
		gl.glPopMatrix();
	}
	
	public final void dispose(final GL2 gl)
	{
		
	}
}