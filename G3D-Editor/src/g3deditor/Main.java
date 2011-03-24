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

import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.swing.FrameMain;
import g3deditor.swing.Splash;
import g3deditor.swing.Splash.CheckedRunnable;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public final class Main
{
	public static final void main(final String[] args)
	{
		//ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		//JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		Config.load();
		
		new Splash(3000,
		new Runnable()
		{
			@Override
			public final void run()
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
				
				GeoEngine.init();
				GeoBlockSelector.init();
				
				GLProfile.initSingleton(false);
				GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
				GLCanvas canvas = new GLCanvas(caps);
				GLDisplay.init(canvas);
				canvas.addGLEventListener(GLDisplay.getInstance());
				FrameMain.init();
			}
		},
		new CheckedRunnable()
		{
			@Override
			public final boolean run()
			{
				if (GeoEngine.getInstance() == null)
					return false;
				
				if (GeoBlockSelector.getInstance() == null)
					return false;
				
				if (FrameMain.getInstance() == null)
					return false;
				return true;
			}
		},
		new Runnable()
		{
			@Override
			public final void run()
			{
				FrameMain.getInstance().validate();
				FrameMain.getInstance().setVisible(true);
				new Animator(GLDisplay.getInstance().getCanvas()).start();
			}
		});
	}
}