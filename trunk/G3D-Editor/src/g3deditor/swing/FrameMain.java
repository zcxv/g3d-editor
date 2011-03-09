package g3deditor.swing;

import g3deditor.jogl.GLDisplay;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class FrameMain extends JFrame
{
	private static FrameMain _instance;
	
	public static final void init(final GLDisplay display)
	{
		_instance = new FrameMain(display);
	}
	
	public static final FrameMain getInstance()
	{
		return _instance;
	}
	
	private final GLDisplay _display;
	private final PanelNswe _panelNswe;
	
	private FrameMain(final GLDisplay display)
	{
		super("G3D-Editor [A1] by Forsaiken");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_display = display;
		_panelNswe = new PanelNswe();
		
		initLayout();
		
		setSize(1024, 768);
		setLocationRelativeTo(null);
        setVisible(true);
	}
	
	private final void initLayout()
	{
		super.setLayout(new GridBagLayout());
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		super.add(_display.getCanvas(), gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		super.add(_panelNswe, gbc);
	}
}