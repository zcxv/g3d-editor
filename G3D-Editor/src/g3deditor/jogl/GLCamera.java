package g3deditor.jogl;

import g3deditor.util.BufferUtils;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public final class GLCamera
{
	private final GLDisplay _display;
	private final int[] _viewport;
	private final float[] _projectionMatrix;
	private final float[] _modelviewMatrix;
	private final float[] _combinedMatrix;
	private final float[][] _frustum;
	private final float[] _pickResult;
	private final FloatBuffer _pickZBuffer;
	
	private float _curCamPosX;
	private float _curCamPosY;
	private float _curCamPosZ;
	private float _curCamRotX;
	private float _curCamRotY;
	
	private float _prevCamPosX;
	private float _prevCamPosY;
	private float _prevCamPosZ;
	private float _prevCamRotX;
	private float _prevCamRotY;
	
	private boolean _positionXZChanged;
	private boolean _positionYChanged;
	private boolean _rotationChanged;
	
	private boolean _needUpdateViewport;
	private boolean _needUpdateProjectionMatrix;
	private boolean _needUpdateModelviewMatrix;
	private boolean _needUpdateCombinedMatrix;
	private boolean _needUpdateFrustum;
	
	public GLCamera(final GLDisplay display)
	{
		_display = display;
		_viewport = new int[4];
		_projectionMatrix = new float[16];
		_modelviewMatrix = new float[16];
		_combinedMatrix = new float[16];
		_frustum = new float[6][4];
		_pickResult = new float[3];
		_pickZBuffer = BufferUtils.createFloatBuffer(1);
		
		_prevCamPosX = Float.NEGATIVE_INFINITY;
		_prevCamPosY = Float.NEGATIVE_INFINITY;
		_prevCamPosZ = Float.NEGATIVE_INFINITY;
		_prevCamRotX = Float.NEGATIVE_INFINITY;
		_prevCamRotY = Float.NEGATIVE_INFINITY;
	}
	
	public final GLDisplay getDisplay()
	{
		return _display;
	}
	
	public final void setXYZ(final float x, final float y, final float z)
	{
		_curCamPosX = x;
		_curCamPosY = y;
		_curCamPosZ = z;
	}
	
	/**
	 * 
	 * @return True if the X or Z position has changed in this frame
	 */
	public final boolean positionXZChanged()
	{
		return _positionXZChanged;
	}
	
	/**
	 * 
	 * @return True if the Y position has changed in this frame
	 */
	public final boolean positionYChanged()
	{
		return _positionYChanged;
	}
	
	/**
	 * 
	 * @return True if the X or Y rotation has changed in this frame
	 */
	public final boolean rotationChanged()
	{
		return _rotationChanged;
	}
	
	public final void onProjectionMatrixChanged()
	{
		_needUpdateProjectionMatrix = true;
	}
	
	public final void onViewportChanged()
	{
		_needUpdateViewport = true;
	}
	
	public final void checkPositionOrRotationChanged()
	{
		if (_curCamPosX != _prevCamPosX || _curCamPosZ != _prevCamPosZ)
		{
			_prevCamPosX = _curCamPosX;
			_prevCamPosZ = _curCamPosZ;
			_positionXZChanged = true;
		}
		else
		{
			_positionXZChanged = false;
		}
		
		if (_curCamPosY != _prevCamPosY)
		{
			_prevCamPosY = _curCamPosY;
			_positionYChanged = true;
		}
		else
		{
			_positionYChanged = false;
		}
		
		if (_curCamRotX != _prevCamRotX || _curCamRotY != _prevCamRotY)
		{
			_prevCamRotX = _curCamRotX;
			_prevCamRotY = _curCamRotY;
			_rotationChanged = true;
		}
		else
		{
			_rotationChanged = false;
		}
		
		if (_positionXZChanged || _positionYChanged || _rotationChanged)
			_needUpdateModelviewMatrix = true;
	}
	
	public final int[] getViewport(final GL2 gl)
	{
		extractViewport(gl);
		return _viewport;
	}
	
	private final void extractViewport(final GL2 gl)
	{
		if (_needUpdateViewport)
		{
			_needUpdateViewport = false;
			gl.glGetIntegerv(GL.GL_VIEWPORT, _viewport, 0);
		}
	}
	
	public final float[] getProjectionMatrix(final GL2 gl)
	{
		extractProjectionMatrix(gl);
		return _projectionMatrix;
	}
	
	private final void extractProjectionMatrix(final GL2 gl)
	{
		if (_needUpdateProjectionMatrix)
		{
			_needUpdateProjectionMatrix = false;
			_needUpdateCombinedMatrix = true;
			gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, _projectionMatrix, 0);
		}
	}
	
	public final float[] getModelviewMatrix(final GL2 gl)
	{
		extractModelviewMatrix(gl);
		return _modelviewMatrix;
	}
	
	private final void extractModelviewMatrix(final GL2 gl)
	{
		if (_needUpdateModelviewMatrix)
		{
			_needUpdateModelviewMatrix = false;
			_needUpdateCombinedMatrix = true;
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, _modelviewMatrix, 0);
		}
	}
	
	public final float[] getCombinedMatrix(final GL2 gl)
	{
		calcCombinedMatrix(gl);
		return _combinedMatrix;
	}
	
	private final void calcCombinedMatrix(final GL2 gl)
	{
		extractProjectionMatrix(gl);
		extractModelviewMatrix(gl);
		if (_needUpdateCombinedMatrix)
		{
			_needUpdateCombinedMatrix = false;
			_needUpdateFrustum = true;
			_combinedMatrix[0] = _modelviewMatrix[0] * _projectionMatrix[0] + _modelviewMatrix[1] * _projectionMatrix[4] + _modelviewMatrix[2] * _projectionMatrix[8] + _modelviewMatrix[3] * _projectionMatrix[12];
			_combinedMatrix[1] = _modelviewMatrix[0] * _projectionMatrix[1] + _modelviewMatrix[1] * _projectionMatrix[5] + _modelviewMatrix[2] * _projectionMatrix[9] + _modelviewMatrix[3] * _projectionMatrix[13];
			_combinedMatrix[2] = _modelviewMatrix[0] * _projectionMatrix[2] + _modelviewMatrix[1] * _projectionMatrix[6] + _modelviewMatrix[2] * _projectionMatrix[10] + _modelviewMatrix[3] * _projectionMatrix[14];
			_combinedMatrix[3] = _modelviewMatrix[0] * _projectionMatrix[3] + _modelviewMatrix[1] * _projectionMatrix[7] + _modelviewMatrix[2] * _projectionMatrix[11] + _modelviewMatrix[3] * _projectionMatrix[15];
			_combinedMatrix[4] = _modelviewMatrix[4] * _projectionMatrix[0] + _modelviewMatrix[5] * _projectionMatrix[4] + _modelviewMatrix[6] * _projectionMatrix[8] + _modelviewMatrix[7] * _projectionMatrix[12];
			_combinedMatrix[5] = _modelviewMatrix[4] * _projectionMatrix[1] + _modelviewMatrix[5] * _projectionMatrix[5] + _modelviewMatrix[6] * _projectionMatrix[9] + _modelviewMatrix[7] * _projectionMatrix[13];
			_combinedMatrix[6] = _modelviewMatrix[4] * _projectionMatrix[2] + _modelviewMatrix[5] * _projectionMatrix[6] + _modelviewMatrix[6] * _projectionMatrix[10] + _modelviewMatrix[7] * _projectionMatrix[14];
			_combinedMatrix[7] = _modelviewMatrix[4] * _projectionMatrix[3] + _modelviewMatrix[5] * _projectionMatrix[7] + _modelviewMatrix[6] * _projectionMatrix[11] + _modelviewMatrix[7] * _projectionMatrix[15];
			_combinedMatrix[8] = _modelviewMatrix[8] * _projectionMatrix[0] + _modelviewMatrix[9] * _projectionMatrix[4] + _modelviewMatrix[10] * _projectionMatrix[8] + _modelviewMatrix[11] * _projectionMatrix[12];
			_combinedMatrix[9] = _modelviewMatrix[8] * _projectionMatrix[1] + _modelviewMatrix[9] * _projectionMatrix[5] + _modelviewMatrix[10] * _projectionMatrix[9] + _modelviewMatrix[11] * _projectionMatrix[13];
			_combinedMatrix[10] = _modelviewMatrix[8] * _projectionMatrix[2] + _modelviewMatrix[9] * _projectionMatrix[6] + _modelviewMatrix[10] * _projectionMatrix[10] + _modelviewMatrix[11] * _projectionMatrix[14];
			_combinedMatrix[11] = _modelviewMatrix[8] * _projectionMatrix[3] + _modelviewMatrix[9] * _projectionMatrix[7] + _modelviewMatrix[10] * _projectionMatrix[11] + _modelviewMatrix[11] * _projectionMatrix[15];
			_combinedMatrix[12] = _modelviewMatrix[12] * _projectionMatrix[0] + _modelviewMatrix[13] * _projectionMatrix[4] + _modelviewMatrix[14] * _projectionMatrix[8] + _modelviewMatrix[15] * _projectionMatrix[12];
			_combinedMatrix[13] = _modelviewMatrix[12] * _projectionMatrix[1] + _modelviewMatrix[13] * _projectionMatrix[5] + _modelviewMatrix[14] * _projectionMatrix[9] + _modelviewMatrix[15] * _projectionMatrix[13];
			_combinedMatrix[14] = _modelviewMatrix[12] * _projectionMatrix[2] + _modelviewMatrix[13] * _projectionMatrix[6] + _modelviewMatrix[14] * _projectionMatrix[10] + _modelviewMatrix[15] * _projectionMatrix[14];
			_combinedMatrix[15] = _modelviewMatrix[12] * _projectionMatrix[3] + _modelviewMatrix[13] * _projectionMatrix[7] + _modelviewMatrix[14] * _projectionMatrix[11] + _modelviewMatrix[15] * _projectionMatrix[15];
		}
	}
	
	public final float[][] getFrustum(final GL2 gl)
	{
		calcFrustum(gl);
		return _frustum;
	}
	
	private final void calcFrustum(final GL2 gl)
	{
		calcCombinedMatrix(gl);
		if (_needUpdateFrustum)
		{
			_needUpdateFrustum = false;
			float[] plane = _frustum[0];
			plane[0] = _combinedMatrix[3] - _combinedMatrix[0];
			plane[1] = _combinedMatrix[7] - _combinedMatrix[4];
			plane[2] = _combinedMatrix[11] - _combinedMatrix[8];
			plane[3] = _combinedMatrix[15] - _combinedMatrix[12];
			normalizePlane(plane);
			
			plane = _frustum[1];
			plane[0] = _combinedMatrix[3] + _combinedMatrix[0];
			plane[1] = _combinedMatrix[7] + _combinedMatrix[4];
			plane[2] = _combinedMatrix[11] + _combinedMatrix[8];
			plane[3] = _combinedMatrix[15] + _combinedMatrix[12];
			normalizePlane(plane);
			
			plane = _frustum[2];
			plane[0] = _combinedMatrix[3] + _combinedMatrix[1];
			plane[1] = _combinedMatrix[7] + _combinedMatrix[5];
			plane[2] = _combinedMatrix[11] + _combinedMatrix[9];
			plane[3] = _combinedMatrix[15] + _combinedMatrix[13];
			normalizePlane(plane);
			
			plane = _frustum[3];
			plane[0] = _combinedMatrix[3] - _combinedMatrix[1];
			plane[1] = _combinedMatrix[7] - _combinedMatrix[5];
			plane[2] = _combinedMatrix[11] - _combinedMatrix[9];
			plane[3] = _combinedMatrix[15] - _combinedMatrix[13];
			normalizePlane(plane);
			
			plane = _frustum[4];
			plane[0] = _combinedMatrix[3] - _combinedMatrix[2];
			plane[1] = _combinedMatrix[7] - _combinedMatrix[6];
			plane[2] = _combinedMatrix[11] - _combinedMatrix[10];
			plane[3] = _combinedMatrix[15] - _combinedMatrix[14];
			normalizePlane(plane);
			
			plane = _frustum[5];
			plane[0] = _combinedMatrix[3] + _combinedMatrix[2];
			plane[1] = _combinedMatrix[7] + _combinedMatrix[6];
			plane[2] = _combinedMatrix[11] + _combinedMatrix[10];
			plane[3] = _combinedMatrix[15] + _combinedMatrix[14];
			normalizePlane(plane);
		}
	}
	
	private static final void normalizePlane(final float[] plane)
	{
		float t = plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2];
		if (t != 1f && t != 0f)
		{
			t = (float) Math.sqrt(t);
			plane[0] /= t;
			plane[1] /= t;
			plane[2] /= t;
			plane[3] /= t;
		}
	}
	
	public final float[] pick(final GL2 gl, final GLU glu, final int mouseX, int mouseY)
	{
		extractViewport(gl);
		extractModelviewMatrix(gl);
		extractProjectionMatrix(gl);
		
		mouseY = _viewport[3] - mouseY;
		_pickZBuffer.clear();
        gl.glReadPixels(mouseX, mouseY, 1, 1, GL2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, _pickZBuffer);
        _pickZBuffer.flip();
        
        glu.gluUnProject(mouseX, mouseY, _pickZBuffer.get(), _modelviewMatrix, 0, _projectionMatrix, 0, _viewport, 0, _pickResult, 0);
        return new float[]{_pickResult[0], _pickResult[1], _pickResult[2]};
	}
	
	public final float getX()
	{
		return _prevCamPosX;
	}
	
	public final float getY()
	{
		return _prevCamPosY;
	}
	
	public final float getZ()
	{
		return _prevCamPosZ;
	}
	
	public final float getRotX()
	{
		return _prevCamRotX;
	}
	
	public final float getRotY()
	{
		return _prevCamRotY;
	}
	
	public final void updateCamRotX(final float diffRotX)
	{
		_curCamRotX = Math.max(Math.min(_curCamRotX - diffRotX, 90), -90);
	}
	
	public final void updateCamRotY(final float diffRotY)
	{
		_curCamRotY -= diffRotY;
	}
	
	public final void moveForeward(final double move)
	{
		final double radians = Math.toRadians(_curCamRotY);
		_curCamPosX += Math.sin(radians) * move;
		_curCamPosY += Math.tan(Math.toRadians(_curCamRotX)) * -move;
		_curCamPosZ += Math.cos(radians) * move;
	}
	
	public final void moveSideways(final double move)
	{
		final double radians = Math.toRadians(_curCamRotY - 90f);
		_curCamPosX += Math.sin(radians) * move;
		_curCamPosZ += Math.cos(radians) * move;
	}
	
	public final void moveUp(final double move)
	{
		_curCamPosY += move;
	}
}