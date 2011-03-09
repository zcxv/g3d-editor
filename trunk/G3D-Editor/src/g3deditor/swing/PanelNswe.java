package g3deditor.swing;

import g3deditor.geo.GeoEngine;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class PanelNswe extends JPanel
{
	private final JButton[] _buttonsNswe;
	
	public PanelNswe()
	{
		_buttonsNswe = new JButton[GeoEngine.NSWE_MASK + 1];
		for (int i = _buttonsNswe.length; i-- > 0;)
		{
			_buttonsNswe[i] = new JButton(GeoEngine.nameOfNSWE(i));
		}
		initLayout();
	}
	
	private final void initLayout()
	{
		super.setLayout(new GridLayout(4, 4));
		for (final JButton buttonNswe : _buttonsNswe)
		{
			super.add(buttonNswe);
		}
	}
}