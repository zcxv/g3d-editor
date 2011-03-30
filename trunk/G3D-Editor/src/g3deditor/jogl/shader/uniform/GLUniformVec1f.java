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
package g3deditor.jogl.shader.uniform;

import g3deditor.jogl.shader.GLUniform;

import javax.media.opengl.GL2;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLUniformVec1f extends GLUniform
{
	private float _v1;
	
	public GLUniformVec1f(final String uniformName)
	{
		super(uniformName);
	}
	
	public final void set(final float v1)
	{
		_v1 = v1;
	}
	
	@Override
	public final void update(final GL2 gl)
	{
		gl.glUniform1f(getUniformLocation(), _v1);
	}
}