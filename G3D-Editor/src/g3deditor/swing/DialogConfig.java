package g3deditor.swing;

import g3deditor.Config;
import g3deditor.jogl.GLCellRenderSelector;
import g3deditor.jogl.GLCellRenderer;
import g3deditor.jogl.GLDisplay;
import g3deditor.jogl.renderer.DLLoDRenderer;
import g3deditor.jogl.renderer.VBORenderer;
import g3deditor.swing.defaults.DefaultButton;
import g3deditor.swing.defaults.DefaultLabel;
import g3deditor.swing.defaults.DefaultTextField;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
	private final DefaultLabel _labelLookAndFeel;
	private final JComboBox _comboLookAndFeel;
	
	private final JPanel _panelEditor;
	private final JPanel _panelCheckButtons;
	private final JCheckBox _checkTerrainDefaultOn;
	private final JCheckBox _checkVSync;
	private final JCheckBox _checkTransparency;
	private final JCheckBox _checkMultiThreading;
	private final JCheckBox _checkDrawOutline;
	private final JCheckBox _checkVBODrawRange;
	
	private final DefaultLabel _labelCellRenderer;
	private final JComboBox _comboCellRenderer;
	
	private final DefaultLabel _labelDLLoDRange;
	private final JSlider _sliderDLLoDRange;
	
	private final DefaultLabel _labelGridRange;
	private final JSlider _sliderGridRange;
	
	private final JPanel _panelGeodata;
	private final DefaultLabel _labelGeodataPath;
	private final DefaultTextField _fieldGeodataPath;
	
	private final JPanel _panelButtons;
	private final DefaultButton _buttonOk;
	private final DefaultButton _buttonCancel;
	
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
		_labelLookAndFeel = new DefaultLabel("LookAndFeel:");
		_comboLookAndFeel = new JComboBox(Config.getInstalledLookAndFeels());
		
		_panelEditor = new JPanel();
		_panelEditor.setBorder(BorderFactory.createTitledBorder("Editor"));
		
		_panelCheckButtons = new JPanel();
		_checkTerrainDefaultOn = new JCheckBox("Terrain default ON");
		_checkVSync = new JCheckBox("VSync");
		_checkTransparency = new JCheckBox("Transparency");
		_checkMultiThreading = new JCheckBox("MultiThreading");
		_checkDrawOutline = new JCheckBox("Draw Outline");
		_checkVBODrawRange = new JCheckBox("VBO DrawRangeElements");
		
		_labelCellRenderer = new DefaultLabel("CellRenderer:");
		_comboCellRenderer = new JComboBox(GLCellRenderer.RENDERER_NAMES);
		_comboCellRenderer.setSelectedItem(Config.CELL_RENDERER);
		_comboCellRenderer.addItemListener(new ItemListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public final void itemStateChanged(final ItemEvent e)
			{
				checkDLLoDSliderEnabled();
				checkVBODrawRangeEnabled();
			}
		});
		
		_labelDLLoDRange = new DefaultLabel("DisplayList LoD - max detail range:");
		_sliderDLLoDRange = new JSlider();
		_sliderDLLoDRange.setMinimum(DLLoDRenderer.MIN_DISTANCE_SQ);
		_sliderDLLoDRange.setMaximum(DLLoDRenderer.MAX_DISTANCE_SQ);
		_sliderDLLoDRange.setMinorTickSpacing(256);
		_sliderDLLoDRange.setMajorTickSpacing(1024);
		_sliderDLLoDRange.setPaintTicks(true);
		Hashtable<Integer, DefaultLabel> rangeTable = new Hashtable<Integer, DefaultLabel>();
		for (int i = DLLoDRenderer.MIN_DISTANCE_SQ; i <= DLLoDRenderer.MAX_DISTANCE_SQ; i += 2048)
		{
			rangeTable.put(i, new DefaultLabel(String.valueOf(i)));
		}
		_sliderDLLoDRange.setLabelTable(rangeTable);
		_sliderDLLoDRange.setPaintLabels(true);
		
		_labelGridRange = new DefaultLabel("Visible grid range:");
		_sliderGridRange = new JSlider();
		_sliderGridRange.setMinimum(GLCellRenderSelector.MIN_VIS_GRID_RANGE);
		_sliderGridRange.setMaximum(GLCellRenderSelector.MAX_VIS_GRID_RANGE);
		_sliderGridRange.setMinorTickSpacing(2);
		_sliderGridRange.setMajorTickSpacing(8);
		_sliderGridRange.setPaintTicks(true);
		rangeTable = new Hashtable<Integer, DefaultLabel>();
		for (int i = 8; i <= 96; i += 8)
		{
			rangeTable.put(i, new DefaultLabel(String.valueOf(i)));
		}
		_sliderGridRange.setLabelTable(rangeTable);
		_sliderGridRange.setPaintLabels(true);
		
		_panelGeodata = new JPanel();
		_panelGeodata.setBorder(BorderFactory.createTitledBorder("Geodata"));
		_labelGeodataPath = new DefaultLabel("Path to Geodata:");
		_fieldGeodataPath = new DefaultTextField(16);
		_fieldGeodataPath.setEditable(false);
		_fieldGeodataPath.addMouseListener(this);
		
		_panelButtons = new JPanel();
		_buttonOk = new DefaultButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonCancel = new DefaultButton("Cancel");
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
		
		_panelCheckButtons.setLayout(new GridLayout(3, 2));
		_panelCheckButtons.add(_checkTerrainDefaultOn);
		_panelCheckButtons.add(_checkVSync);
		_panelCheckButtons.add(_checkTransparency);
		_panelCheckButtons.add(_checkMultiThreading);
		_panelCheckButtons.add(_checkDrawOutline);
		_panelCheckButtons.add(_checkVBODrawRange);
		
		_panelEditor.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_panelCheckButtons, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_labelCellRenderer, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_comboCellRenderer, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_labelDLLoDRange, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_sliderDLLoDRange, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelEditor.add(_labelGridRange, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
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
		
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelMisc, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelEditor, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelGeodata, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelButtons, gbc);
		
		pack();
		setResizable(false);
	}
	
	private final void checkDLLoDSliderEnabled()
	{
		final boolean enabled = _comboCellRenderer.getSelectedItem() == DLLoDRenderer.NAME;
		_labelDLLoDRange.setEnabled(enabled);
		_sliderDLLoDRange.setEnabled(enabled);
	}
	
	private final void checkVBODrawRangeEnabled()
	{
		final boolean enabled = _comboCellRenderer.getSelectedItem() == VBORenderer.NAME;
		_checkVBODrawRange.setEnabled(enabled);
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
		{
			setLocationRelativeTo(super.getOwner());
			
			_checkTerrainDefaultOn.setSelected(Config.TERRAIN_DEFAULT_ON);
			_checkVSync.setSelected(Config.V_SYNC);
			_checkTransparency.setSelected(Config.USE_TRANSPARENCY);
			_checkMultiThreading.setSelected(Config.USE_MULTITHREADING);
			_checkDrawOutline.setSelected(Config.DRAW_OUTLINE);
			_checkVBODrawRange.setSelected(Config.VBO_DRAW_RANGE);
			_sliderGridRange.setValue(Config.VIS_GRID_RANGE);
			_comboLookAndFeel.setSelectedItem(Config.getLookAndFeel(Config.LOOK_AND_FEEL, Config.getActiveLookAndFeel()));
			_comboCellRenderer.setSelectedItem(Config.CELL_RENDERER);
			_sliderDLLoDRange.setValue(Config.DLLoD_RANGE);
			_fieldGeodataPath.setText(Config.PATH_TO_GEO_FILES);
			checkDLLoDSliderEnabled();
			checkVBODrawRangeEnabled();
		}
		
		super.setVisible(visible);
		
		if (!visible)
			GLDisplay.getInstance().requestFocus();
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
			Config.V_SYNC = _checkVSync.isSelected();
			Config.USE_TRANSPARENCY = _checkTransparency.isSelected();
			Config.USE_MULTITHREADING = _checkMultiThreading.isSelected();
			Config.DRAW_OUTLINE = _checkDrawOutline.isSelected();
			Config.VBO_DRAW_RANGE = _checkVBODrawRange.isSelected();
			Config.VIS_GRID_RANGE = _sliderGridRange.getValue();
			Config.LOOK_AND_FEEL = ((LookAndFeelInfo) _comboLookAndFeel.getSelectedItem()).getClassName();
			Config.CELL_RENDERER = GLCellRenderer.validateRenderer((String) _comboCellRenderer.getSelectedItem());
			Config.DLLoD_RANGE = _sliderDLLoDRange.getValue();
			
			Config.save();
			setVisible(false);
		}
		else if (e.getSource() == _buttonCancel)
		{
			setVisible(false);
		}
	}
}