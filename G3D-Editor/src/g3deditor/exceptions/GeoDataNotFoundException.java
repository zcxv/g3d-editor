package g3deditor.exceptions;

@SuppressWarnings("serial")
public final class GeoDataNotFoundException extends RuntimeException
{
	public GeoDataNotFoundException(final int geoX, final int geoY)
	{
		super("GeoData not found at geoX: " + geoX + ", geoY: " + geoY);
	}
}