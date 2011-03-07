package g3deditor.exceptions;

@SuppressWarnings("serial")
public final class GeoDataNotLoadedException extends RuntimeException
{
	public GeoDataNotLoadedException(final int geoX, final int geoY)
	{
		super("GeoData not loaded at geoX: " + geoX + ", geoY: " + geoY);
	}
}