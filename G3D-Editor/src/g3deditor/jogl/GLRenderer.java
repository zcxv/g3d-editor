package g3deditor.jogl;

import g3deditor.geo.GeoCell;

import javax.media.opengl.GL2;

public abstract class GLRenderer
{
	protected static final float COLOR_ALPHA = 0.7f;
	protected static final int NSWE_COMBINATIONS = 16;
	protected static final int NSWE_TEX_ROWS_COLS = 4;
	protected static final float NSWE_TEX_BLOCK = 1f / NSWE_TEX_ROWS_COLS;
	
	private final GLDisplay _display;
	
	public GLRenderer(final GLDisplay display)
	{
		_display = display;
	}
	
	public final GLDisplay getDisplay()
	{
		return _display;
	}
	
	protected final void renderCell(final GL2 gl, final boolean big, final int nswe)
	{
		final float size = big ? 7.9f : 0.9f;
		final float u1 = (nswe / NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float u2 = u1 + NSWE_TEX_BLOCK;
		final float v1 = (nswe % NSWE_TEX_ROWS_COLS) * NSWE_TEX_BLOCK;
		final float v2 = v1 + NSWE_TEX_BLOCK;
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glVertex3f(0.1f, -0.2f, 0.1f);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glVertex3f(size, -0.2f, 0.1f);
		gl.glVertex3f(size, 0.0f, 0.1f);
		gl.glVertex3f(size, -0.2f, size);
		gl.glVertex3f(size, 0.0f, size);
		gl.glVertex3f(0.1f, -0.2f, size);
		gl.glVertex3f(0.1f, 0.0f, size);
		gl.glVertex3f(0.1f, -0.2f, 0.1f);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glVertex3f(0.1f, -0.2f, size);
		gl.glVertex3f(0.1f, -0.2f, 0.1f);
		gl.glVertex3f(size, -0.2f, size);
		gl.glVertex3f(size, -0.2f, 0.1f);
		gl.glEnd();
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		gl.glTexCoord2f(u2, v2);
		gl.glVertex3f(0.1f, 0.0f, 0.1f);
		gl.glTexCoord2f(u1, v2);
		gl.glVertex3f(0.1f, 0.0f, size);
		gl.glTexCoord2f(u2, v1);
		gl.glVertex3f(size, 0.0f, 0.1f);
		gl.glTexCoord2f(u1, v1);
		gl.glVertex3f(size, 0.0f, size);
		gl.glEnd();
	}
	
	public abstract void init(final GL2 gl);
	
	public abstract void enableRender(final GL2 gl);
	
	public abstract void render(final GL2 gl, final GeoCell cell);
	
	public abstract void disableRender(final GL2 gl);
	
	public abstract void dispose(final GL2 gl);
}