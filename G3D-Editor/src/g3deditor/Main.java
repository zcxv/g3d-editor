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
package g3deditor;

import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.GLGeoBlockSelector;
import g3deditor.swing.FrameMain;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jogamp.opengl.util.FPSAnimator;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class Main
{
	public static final void main(final String[] args)
	{
		System.out.println("OpenGL Test");
		System.out.println("GL2:   " + (GLProfile.isGL2Available() ? "Yes" : "No"));
		System.out.println("GL3:   " + (GLProfile.isGL3Available() ? "Yes" : "No"));
		System.out.println("GL3bc: " + (GLProfile.isGL3bcAvailable() ? "Yes" : "No"));
		System.out.println("GL4:   " + (GLProfile.isGL4Available() ? "Yes" : "No"));
		System.out.println("GL4bc: " + (GLProfile.isGL4bcAvailable() ? "Yes" : "No"));
		
		if (!GLProfile.isGL2Available())
			throw new RuntimeException("OpenGL2 is required to run this software");
		
		if (!GLProfile.isAWTAvailable())
			throw new RuntimeException("AWT support is required to run this software");
		
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch (final UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		
		GeoEngine.init();
		GLGeoBlockSelector.init();
		
		try
		{
			GeoEngine.getInstance().reloadGeo(10, 12, true);
			System.out.println(GeoEngine.getInstance().getActiveRegion());
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		GLProfile.initSingleton(false);
		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);
		GLDisplay display = new GLDisplay(canvas);
		canvas.addGLEventListener(display);
		
		FrameMain.init(display);
		
		final FPSAnimator animator = new FPSAnimator(canvas, 60);
		//animator.setRunAsFastAsPossible(false);
		animator.start();
	}
}