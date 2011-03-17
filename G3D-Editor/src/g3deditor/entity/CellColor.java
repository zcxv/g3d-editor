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
package g3deditor.entity;

import java.awt.Color;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class CellColor
{
	private static final float intToFloat(final int rgb)
	{
		return Math.max(Math.min(rgb / 255f, 1f), 0f);
	}
	
	private final float _r;
	private final float _g;
	private final float _b;
	
	public CellColor(final Color color)
	{
		this(intToFloat(color.getRed()), intToFloat(color.getGreen()), intToFloat(color.getBlue()));
	}
	
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