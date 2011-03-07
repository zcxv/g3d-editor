package g3deditor.entity;

public final class CellColor
{
	private final float _r;
	private final float _g;
	private final float _b;
	
	public CellColor(final float r, final float g, final float b)
	{
		_r = r;
		_g = g;
		_b = b;
	}
	
	public final float getR()
	{
		return _r;
	}
	
	public final float getG()
	{
		return _g;
	}
	
	public final float getB()
	{
		return _b;
	}
}