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
package g3deditor.util;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
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