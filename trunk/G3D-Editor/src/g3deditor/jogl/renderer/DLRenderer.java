package g3deditor.jogl.renderer;

import g3deditor.entity.CellColor;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.GLRenderer;

import javax.media.opengl.GL2;

public final class DLRenderer extends GLRenderer
{
	private int _listId;
	
	/**
	 * @param display
	 */
	public DLRenderer(final GLDisplay display)
	{
		super(display);
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#init(javax.media.opengl.GL2)
	 */
	@Override
	public final void init(final GL2 gl)
	{
		_listId = gl.glGenLists(NSWE_COMBINATIONS + 1);
		
		gl.glNewList(_listId, GL2.GL_COMPILE);
		renderCell(gl, true, GeoEngine.NSWE_ALL);
		gl.glEndList();
		
		for (int i = 0; i < NSWE_COMBINATIONS; i++)
		{
			gl.glNewList(_listId + 1 + i, GL2.GL_COMPILE);
			renderCell(gl, false, i);
			gl.glEndList();
		}
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#enableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void enableRender(final GL2 gl)
	{
		
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#render(javax.media.opengl.GL2, g3deditor.geo.GeoCell)
	 */
	public final void render(final GL2 gl, final GeoCell cell)
	{
		final CellColor color = cell.getSelectionState().getColor(cell);
		gl.glPushMatrix();
		gl.glColor4f(color.getR(), color.getG(), color.getB(), COLOR_ALPHA);
		gl.glTranslatef(cell.getRenderX(), cell.getRenderY(), cell.getRenderZ());
		gl.glCallList(_listId + (cell.isBig() ? 0 : cell.getNSWE() + 1));
		gl.glPopMatrix();
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#disableRender(javax.media.opengl.GL2)
	 */
	@Override
	public final void disableRender(final GL2 gl)
	{
		
	}
	
	/**
	 * @see g3deditor.jogl.GLRenderer#dispose(javax.media.opengl.GL2)
	 */
	@Override
	public final void dispose(final GL2 gl)
	{
		gl.glDeleteLists(_listId, NSWE_COMBINATIONS + 1);
	}
}