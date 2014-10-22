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
package g3deditor.jogl.shader;

import g3deditor.jogl.shader.uniform.GLUniformVec1f;
import g3deditor.jogl.shader.uniform.GLUniformVec1fv;
import g3deditor.jogl.shader.uniform.GLUniformVec1i;
import g3deditor.jogl.shader.uniform.GLUniformVec1iv;
import g3deditor.jogl.shader.uniform.GLUniformVec2f;
import g3deditor.jogl.shader.uniform.GLUniformVec3fv;
import g3deditor.jogl.shader.uniform.GLUniformVec4fv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class GLShader
{
	private static final String readShader(final String file, final String type)
	{
		try (FileInputStream fis = new FileInputStream(file);
			final BufferedReader br = new BufferedReader(new InputStreamReader(fis)))
		{
			final StringBuilder sb = new StringBuilder();
			
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append('\n');
			}
			
			return sb.toString();
		}
		catch (final IOException e)
		{
			throw new RuntimeException("Failed reading " + type + ": " + file);
		}
	}
	
	private final String _vertexShader;
	private final String _vertexShaderName;
	private final String _fragmentShader;
	private final String _fragmentShaderName;
	private final HashMap<String, GLUniform> _uniforms;
	
	private boolean _initialized;
	private int _programId;
	private int _vertexShaderId;
	private int _fragmentShaderId;
	
	public GLShader(final String vertexShaderPath, final String fragmentShaderPath)
	{
		_vertexShader = readShader(vertexShaderPath, "VertexShader");
		_vertexShaderName = new File(vertexShaderPath).getName();
		_fragmentShader = readShader(fragmentShaderPath, "FragmentShader");
		_fragmentShaderName = new File(fragmentShaderPath).getName();
		_uniforms = new HashMap<String, GLUniform>();
	}
	
	public final int getProgramId()
	{
		return _programId;
	}
	
	private final boolean checkCompilationOk(final GL2 gl, final int id, final boolean shader, final String name)
	{
		final IntBuffer ib = Buffers.newDirectIntBuffer(1);
		
		if (shader)
			gl.glGetShaderiv(id, GL2.GL_COMPILE_STATUS, ib);
		else
			gl.glGetProgramiv(id, GL2.GL_LINK_STATUS, ib);
		final boolean error = ib.get(0) == GL2.GL_FALSE;
		
		if (shader)
			gl.glGetShaderiv(id, GL2.GL_INFO_LOG_LENGTH, ib);
		else
			gl.glGetProgramiv(id, GL2.GL_INFO_LOG_LENGTH, ib);
		int length = ib.get(0);
		String log = null;
		
		if (length > 0)
		{
			final ByteBuffer bb = Buffers.newDirectByteBuffer(length);
			
			if (shader)
				gl.glGetShaderInfoLog(id, length, ib, bb);
			else
				gl.glGetProgramInfoLog(id, length, ib, bb);
			length = ib.get(0);
			if (length > 0)
			{
				final byte[] logData = new byte[length];
				bb.get(logData);
				log = new String(logData);
			}
		}
		
		System.out.println((shader ? "Compilation" : "Link") + " of " + name + (shader ? "-Shader" : "-Program") + (error ? " has failed" : " was successful") + (log != null ? ":" : "."));
		if (log != null)
			System.out.print(log);
		
		return !error;
	}
	
	public final boolean init(final GL2 gl)
	{
		if (_initialized)
			return true;
		
		_initialized = true;
		if (_vertexShader == null || _fragmentShader == null)
			return false;
		
		_vertexShaderId = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		_fragmentShaderId = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		
		gl.glShaderSource(_vertexShaderId, 1, new String[]{_vertexShader}, new int[]{_vertexShader.length()}, 0);
		gl.glCompileShader(_vertexShaderId);
		if (!checkCompilationOk(gl, _vertexShaderId, true, "[" + _vertexShaderName + "] Vertex"))
			return false;
		
		gl.glShaderSource(_fragmentShaderId, 1, new String[]{_fragmentShader}, new int[]{_fragmentShader.length()}, 0);
		gl.glCompileShader(_fragmentShaderId);
		if (!checkCompilationOk(gl, _fragmentShaderId, true, "[" + _fragmentShaderName + "] Fragment"))
			return false;
		
		_programId = gl.glCreateProgram();
		gl.glAttachShader(_programId, _vertexShaderId);
		gl.glAttachShader(_programId, _fragmentShaderId);
		gl.glLinkProgram(_programId);
		gl.glValidateProgram(_programId);
		if (!checkCompilationOk(gl, _programId, false, "[" + _vertexShaderName + "/" + _fragmentShaderName + "] GLSL"))
			return false;
		
		return true;
	}
	
	public final void setTexture(final GL2 gl, final Texture texture, final String unifromName)
	{
		if (!_initialized || _programId == 0)
			return;
		
		gl.glUseProgram(_programId);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		texture.bind();
		gl.glUniform1i(gl.glGetUniformLocation(_programId, unifromName), 0);
	}
	
	public final GLUniformVec3fv getUniformVec3fv(final GL2 gl, final String uniformName, final int numElements)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec3fv(uniformName, numElements);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec3fv))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec3fv.class);
		}
		return (GLUniformVec3fv) uniform;
	}
	
	public final GLUniformVec4fv getUniformVec4fv(final GL2 gl, final String uniformName, final int numElements)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec4fv(uniformName, numElements);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec4fv))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec4fv.class);
		}
		return (GLUniformVec4fv) uniform;
	}
	
	public final GLUniformVec1fv getUniformVec1fv(final GL2 gl, final String uniformName, final int numElements)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec1fv(uniformName, numElements);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec1fv))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec1fv.class);
		}
		return (GLUniformVec1fv) uniform;
	}
	
	public final GLUniformVec1i getUniformVec1i(final GL2 gl, final String uniformName)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec1i(uniformName);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec1i))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec1fv.class);
		}
		return (GLUniformVec1i) uniform;
	}
	
	public final GLUniformVec1iv getUniformVec1iv(final GL2 gl, final String uniformName, final int numElements)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec1iv(uniformName, numElements);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec1iv))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec1iv.class);
		}
		return (GLUniformVec1iv) uniform;
	}
	
	public final GLUniformVec2f getUniformVec2f(final GL2 gl, final String uniformName)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec2f(uniformName);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec2f))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec2f.class);
		}
		return (GLUniformVec2f) uniform;
	}
	
	public final GLUniformVec1f getUniformVec1f(final GL2 gl, final String uniformName)
	{
		GLUniform uniform = _uniforms.get(uniformName);
		if (uniform == null)
		{
			uniform = new GLUniformVec1f(uniformName);
			uniform.init(gl, this);
			_uniforms.put(uniformName, uniform);
		}
		else if (!(uniform instanceof GLUniformVec1f))
		{
			throw new RuntimeException("Unexpected uniform type: " + uniform.getClass() + ", expected: " + GLUniformVec1f.class);
		}
		return (GLUniformVec1f) uniform;
	}
	
	public final void dispose(final GL2 gl)
	{
		if (!_initialized)
			return;
		
		_initialized = false;
		
		if (_vertexShaderId != 0)
			gl.glDeleteShader(_vertexShaderId);
		
		if (_fragmentShaderId != 0)
			gl.glDeleteShader(_fragmentShaderId);
		
		if (_programId != 0)
			gl.glDeleteProgram(_programId);
	}
}