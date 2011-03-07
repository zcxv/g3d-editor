package g3deditor.exceptions;

import java.io.File;

@SuppressWarnings("serial")
public final class GeoFileLoadException extends RuntimeException
{
	public GeoFileLoadException(final File file, final boolean l2j, final String couse)
	{
		super("Failed loading Geo File " + (l2j ? "L2j" : "L2Off") + " '" + file + "', " + couse);
	}
	
	public GeoFileLoadException(final File file, final boolean l2j, final Exception couse)
	{
		super("Failed loading Geo File " + (l2j ? "L2j" : "L2Off") + " '" + file + "', " + couse.getMessage());
	}
}