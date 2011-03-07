package g3deditor.exceptions;

import java.io.File;

@SuppressWarnings("serial")
public final class GeoFileNotFoundException extends RuntimeException
{
	public GeoFileNotFoundException(final File file, final boolean l2j)
	{
		super("Geo File " + (l2j ? "L2j not found '" + file + "'" : "L2Off not found"));
	}
}