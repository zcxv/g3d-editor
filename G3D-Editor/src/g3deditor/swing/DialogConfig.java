package g3deditor.swing;

import g3deditor.Config;
import g3deditor.jogl.GLCellRenderSelector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public final class DialogConfig extends JDialog implements MouseListener, ActionListener
{
	private static final FileFilter DIR_FILTER = new FileFilter()
	{
		public final boolean accept(final File file)
		{
			return file.isDirectory();
		}
		
		@Override
		public final String getDescription()
		{
			return "DIRs";
		}
	};
	
	private final JPanel _panelMisc;
	private final JLabel _labelLookAndFeel;
	private final JComboBox _comboLookAndFeel;
	
	private final JPanel _panelEditor;
	private final JCheckBox _checkTerrainDefaultOn;
	
	private final JLabel _labelGridRange;
	private final JSlider _sliderGridRange;
	
	private final JPanel _panelGeodata;
	private final JLabel _labelGeodataPath;
	private final JTextField _fieldGeodataPath;
	
	private final JPanel _panelButtons;
	private final JButton _buttonOk;
	private final JButton _buttonCancel;
	
	private final JFileChooser _fileChooser;
	
	public DialogConfig(final JFrame frame)
	{
		super(frame, "Config", true);
		
		_fileChooser = new JFileChooser();
		_fileChooser.setFileFilter(DIR_FILTER);
		_fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		_fileChooser.setDialogTitle("Choose the folder that contains the Geodata files...");
		_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		_panelMisc = new JPanel();
		_panelMisc.setBorder(BorderFactory.createTitledBorder("Misc"));
		_labelLookAndFeel = new JLabel("LookAndFeel:");
		_comboLookAndFeel = new JComboBox(Config.getInstalledLookAndFeels());
		_comboLookAndFeel.setSelectedItem(Config.getActiveLookAndFeel());
		
		_panelEditor = new JPanel();
		_panelEditor.setBorder(BorderFactory.createTitledBorder("Editor"));
		_checkTerrainDefaultOn = new JCheckBox("Terrain default ON");
		_checkTerrainDefaultOn.setSelected(Config.TERRAIN_DEFAULT_ON);
		
		_labelGridRange = new JLabel("Visible grid range:");
		_sliderGridRange = new JSlider();
		_sliderGridRange.setMinimum(GLCellRenderSelector.MIN_VIS_GRID_RANGE);
		_sliderGridRange.setMaximum(GLCellRenderSelector.MAX_VIS_GRID_RANGE);
		_sliderGridRange.setMinorTickSpacing(2);
		_sliderGridRange.setMajorTickSpacing(8);
		_sliderGridRange.setPaintTicks(true);
		_sliderGridRange.setValue(Config.VIS_GRID_RANGE);
		final Hashtable<Integer, JLabel> rangeTable = new Hashtable<Integer, JLabel>();
		for (int i = 8; i <= 96; i += 8)
		{
			rangeTable.put(i, new JLabel(String.valueOf(i)));
		}
		_sliderGridRange.setLabelTable(rangeTable);
		_sliderGridRange.setPaintLabels(true);
		
		_panelGeodata = new JPanel();
		_panelGeodata.setBorder(BorderFactory.createTitledBorder("Geodata"));
		_labelGeodataPath = new JLabel("Path to Geodata:");
		_fieldGeodataPath = new JTextField(16);
		_fieldGeodataPath.setEditable(false);
		_fieldGeodataPath.setText(Config.PATH_TO_GEO_FILES);
		_fieldGeodataPath.addMouseListener(this);
		
		_panelButtons = new JPanel();
		_buttonOk = new JButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonCancel = new JButton("Cancel");
		_buttonCancel.addActionListener(this);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		
		_panelMisc.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelMisc.add(_labelLookAndFeel, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelMisc.add(_comboLookAndFeel, gbc);
		
		_panelEditor.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_checkTerrainDefaultOn, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_labelGridRange, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_sliderGridRange, gbc);
		
		_panelGeodata.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelGeodata.add(_labelGeodataPath, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelGeodata.add(_fieldGeodataPath, gbc);
		
		_panelButtons.setLayout(new GridLayout(1, 2));
		_panelButtons.add(_buttonOk);
		_panelButtons.add(_buttonCancel);
		
		super.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		super.add(_panelMisc, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		super.add(_panelEditor, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		super.add(_panelGeodata, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		super.add(_panelButtons, gbc);
		
		super.pack();
		super.setResizable(false);
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
			setLocationRelativeTo(super.getOwner());
		
		super.setVisible(visible);
	}
	
	@Override
	public final void mouseClicked(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mousePressed(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseReleased(final MouseEvent e)
	{
		if (e.getSource() == _fieldGeodataPath)
		{
			if (_fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				final File file = _fileChooser.getSelectedFile();
				if (file != null && DIR_FILTER.accept(file))
				{
					final String path = file.getAbsolutePath();
					_fieldGeodataPath.setText(path);
					Config.PATH_TO_GEO_FILES = path;
					Config.save();
				}
			}
		}
	}
	
	@Override
	public final void mouseEntered(final MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseExited(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonOk)
		{
			Config.TERRAIN_DEFAULT_ON = _checkTerrainDefaultOn.isSelected();
			Config.VIS_GRID_RANGE = _sliderGridRange.getValue();
			Config.LOOK_AND_FEEL = ((LookAndFeelInfo) _comboLookAndFeel.getSelectedItem()).getClassName();
			
			Config.save();
			setVisible(false);
		}
		else if (e.getSource() == _buttonCancel)
		{
			_checkTerrainDefaultOn.setSelected(Config.TERRAIN_DEFAULT_ON);
			_sliderGridRange.setValue(Config.VIS_GRID_RANGE);
			_comboLookAndFeel.setSelectedItem(Config.getLookAndFeel(Config.LOOK_AND_FEEL, Config.getActiveLookAndFeel()));
			setVisible(false);
		}
	}
}