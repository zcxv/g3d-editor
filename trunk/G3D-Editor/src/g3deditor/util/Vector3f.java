package g3deditor.util;

public final class Vector3f
{
	private float _x;
	private float _y;
	private float _z;
	
	public Vector3f()
	{
		
	}
	
	public Vector3f(final float x, final float y, final float z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	public Vector3f(final Vector3f vec)
	{
		_x = vec._x;
		_y = vec._y;
		_z = vec._z;
	}
	
	public final float getX()
	{
		return _x;
	}
	
	public final float getY()
	{
		return _y;
	}
	
	public final float getZ()
	{
		return _z;
	}
}