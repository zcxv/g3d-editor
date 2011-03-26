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
package g3deditor.jogl;

import java.awt.Color;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLColor
{
	public static final GLColor WHITE = new GLColor(1f, 1f, 1f, 1f);
	public static final GLColor BLACK = new GLColor(0f, 0f, 0f, 1f);
	
	private final float _r;
	private final float _g;
	private final float _b;
	private final float _a;
	
	public GLColor(final Color color)
	{
		this(color, color.getAlpha() / 255f);
	}
	
	public GLColor(final Color color, final float alpha)
	{
		this(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha);
	}
	
	public GLColor(final GLColor color, final float alpha)
	{
		this(color.getR(), color.getG(), color.getB(), alpha);
	}
	
	public GLColor(final GLColor color, final float mulR, final float mulG, final float mulB)
	{
		this(color.getR() * mulR, color.getG() * mulG, color.getB() * mulB, color.getA());
	}
	
	public GLColor(final float r, final float g, final float b, final float a)
	{
		_r = Math.max(Math.min(r, 1f), 0f);
		_g = Math.max(Math.min(g, 1f), 0f);
		_b = Math.max(Math.min(b, 1f), 0f);
		_a = Math.max(Math.min(a, 1f), 0f);
	}
	
	public final Color getColor()
	{
		return new Color((int) (_r * 255f), (int) (_g * 255f), (int) (_b * 255f), (int) (_a * 255f));
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
	
	public final float getA()
	{
		return _a;
	}
	
	public final boolean equals(final GLColor color)
	{
		if (color == this)
			return true;
		
		if (color == null)
			return false;
		
		return color.getR() == getR() && color.getB() == getB() && color.getB() == getB() && color.getA() == getA();
	}
}