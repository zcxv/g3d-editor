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
package g3deditor.swing;

import g3deditor.Config;
import g3deditor.entity.SelectionState;
import g3deditor.geo.GeoEngine;
import g3deditor.swing.defaults.DefaultButton;
import g3deditor.swing.defaults.DefaultLabel;
import g3deditor.swing.defaults.DefaultTextField;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class DialogColorChooser extends JDialog implements ChangeListener, ActionListener, ItemListener
{
	private static final String[] TYPES =
	{
		"Flat",
		"Complex",
		"MultiLayer",
		"MultiLayerSpecial" // in selection box
	};
	
	private final JPanel _panelCombos;
	private final JLabel _labelSelectionState;
	private final JComboBox _comboSelectionState;
	private final JLabel _labelBlockType;
	private final JComboBox _comboBlockType;
	
	private final DefaultLabel _labelRed;
	private final JSlider _sliderRed;
	private final DefaultTextField _fieldRed;
	
	private final DefaultLabel _labelGreen;
	private final JSlider _sliderGreen;
	private final DefaultTextField _fieldGreen;
	
	private final DefaultLabel _labelBlue;
	private final JSlider _sliderBlue;
	private final DefaultTextField _fieldBlue;
	
	private final DefaultLabel _labelPreview;
	private final JPanel _panelPreviewColor;
	
	private final JPanel _panelButtons;
	private final DefaultButton _buttonOk;
	private final DefaultButton _buttonCancel;
	private final DefaultButton _buttonRestoreDefault;
	
	private Color _colorFlatNormal;
	private Color _colorFlatHighlighted;
	private Color _colorFlatSelected;
	private Color _colorComplexNormal;
	private Color _colorComplexHighlighted;
	private Color _colorComplexSelected;
	private Color _colorMultiLayerNormal;
	private Color _colorMultiLayerHighlighted;
	private Color _colorMultiLayerSelected;
	private Color _colorMultiLayerSpecialNormal;
	private Color _colorMultiLayerSpecialHighlighted;
	private Color _colorMultiLayerSpecialSelected;
	
	public DialogColorChooser(final JDialog dialog)
	{
		super(dialog, "Choose colors...", true);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public final void windowClosing(final WindowEvent e)
			{
				DialogColorChooser.this.actionPerformed(new ActionEvent(_buttonCancel, ActionEvent.ACTION_PERFORMED, ""));
			}
		});
		
		_panelCombos = new JPanel();
		_labelSelectionState = new JLabel("State:");
		_comboSelectionState = new JComboBox(SelectionState.values());
		_comboSelectionState.addItemListener(this);
		_labelBlockType = new JLabel("Type:");
		_comboBlockType = new JComboBox(new String[]{"Flat", "Complex", "MultiLayer", "MultiLayerSpecial"});
		_comboBlockType.addItemListener(this);
		
		_labelRed = new DefaultLabel("Red:");
		_sliderRed = new JSlider(0, 255, 255);
		_sliderRed.addChangeListener(this);
		_fieldRed = new DefaultTextField(String.valueOf(_sliderRed.getValue()));
		_fieldRed.setEditable(false);
		_labelGreen = new DefaultLabel("Green:");
		_sliderGreen = new JSlider(0, 255, 255);
		_sliderGreen.addChangeListener(this);
		_fieldGreen = new DefaultTextField(String.valueOf(_sliderGreen.getValue()));
		_fieldGreen.setEditable(false);
		_labelBlue = new DefaultLabel("Blue:");
		_sliderBlue = new JSlider(0, 255, 255);
		_sliderBlue.addChangeListener(this);
		_fieldBlue = new DefaultTextField(String.valueOf(_sliderBlue.getValue()));
		_fieldBlue.setEditable(false);
		
		_labelPreview = new DefaultLabel("Preview: ");
		_panelPreviewColor = new JPanel();
		
		_panelButtons = new JPanel();
		_buttonOk = new DefaultButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonCancel = new DefaultButton("Cancel");
		_buttonCancel.addActionListener(this);
		_buttonRestoreDefault = new DefaultButton("Restore default");
		_buttonRestoreDefault.addActionListener(this);
		
		initLayout();
		pack();
		setResizable(false);
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		
		_panelCombos.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelCombos.add(_labelSelectionState, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelCombos.add(_comboSelectionState, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelCombos.add(_labelBlockType, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelCombos.add(_comboBlockType, gbc);
		
		_panelButtons.setLayout(new GridLayout(1, 3));
		_panelButtons.add(_buttonOk);
		_panelButtons.add(_buttonCancel);
		_panelButtons.add(_buttonRestoreDefault);
		
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelCombos, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelRed, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_sliderRed, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldRed, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGreen, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_sliderGreen, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldGreen, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelBlue, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_sliderBlue, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldBlue, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelPreview, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelPreviewColor, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelButtons, gbc);
	}
	
	public final Color getColorOfState()
	{
		final SelectionState state = (SelectionState) _comboSelectionState.getSelectedItem();
		final String type = (String) _comboBlockType.getSelectedItem();
		
		if (TYPES[GeoEngine.GEO_BLOCK_TYPE_FLAT].equals(type))
		{
			return state.getColorFlat().getColorRGB();
		}
		else if (TYPES[GeoEngine.GEO_BLOCK_TYPE_COMPLEX].equals(type))
		{
			return state.getColorComplex().getColorRGB();
		}
		else if (TYPES[GeoEngine.GEO_BLOCK_TYPE_MULTILAYER].equals(type))
		{
			return state.getColorMultiLayer().getColorRGB();
		}
		else
		{
			return state.getColorMultiLayerSpecial().getColorRGB();
		}
	}
	
	public final void setColorOfState(final Color color)
	{
		final SelectionState state = (SelectionState) _comboSelectionState.getSelectedItem();
		final String type = (String) _comboBlockType.getSelectedItem();
		
		if (TYPES[GeoEngine.GEO_BLOCK_TYPE_FLAT].equals(type))
		{
			state.getColorFlat().setColorRGB(color);
		}
		else if (TYPES[GeoEngine.GEO_BLOCK_TYPE_COMPLEX].equals(type))
		{
			state.getColorComplex().setColorRGB(color);
		}
		else if (TYPES[GeoEngine.GEO_BLOCK_TYPE_MULTILAYER].equals(type))
		{
			state.getColorMultiLayer().setColorRGB(color);
		}
		else
		{
			state.getColorMultiLayerSpecial().setColorRGB(color);
		}
	}
	
	public final Color getColorOfField()
	{
		final int red = _sliderRed.getValue();
		final int green = _sliderGreen.getValue();
		final int blue = _sliderBlue.getValue();
		return new Color(red, green, blue);
	}
	
	public final void setColorOfField(final Color color)
	{
		_sliderRed.removeChangeListener(this);
		_sliderRed.setValue(color.getRed());
		_sliderRed.addChangeListener(this);
		_sliderGreen.removeChangeListener(this);
		_sliderGreen.setValue(color.getGreen());
		_sliderGreen.addChangeListener(this);
		_sliderBlue.removeChangeListener(this);
		_sliderBlue.setValue(color.getBlue());
		_sliderBlue.addChangeListener(this);
		
		_fieldRed.setText(String.valueOf(color.getRed()));
		_fieldGreen.setText(String.valueOf(color.getGreen()));
		_fieldBlue.setText(String.valueOf(color.getBlue()));
		_panelPreviewColor.setBackground(color);
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
		{
			setLocationRelativeTo(getOwner());
			_colorFlatNormal = SelectionState.NORMAL.getColorFlat().getColorRGB();
			_colorFlatHighlighted = SelectionState.HIGHLIGHTED.getColorFlat().getColorRGB();
			_colorFlatSelected = SelectionState.SELECTED.getColorFlat().getColorRGB();
			_colorComplexNormal = SelectionState.NORMAL.getColorComplex().getColorRGB();
			_colorComplexHighlighted = SelectionState.HIGHLIGHTED.getColorComplex().getColorRGB();
			_colorComplexSelected = SelectionState.SELECTED.getColorComplex().getColorRGB();
			_colorMultiLayerNormal = SelectionState.NORMAL.getColorMultiLayer().getColorRGB();
			_colorMultiLayerHighlighted = SelectionState.HIGHLIGHTED.getColorMultiLayer().getColorRGB();
			_colorMultiLayerSelected = SelectionState.SELECTED.getColorMultiLayer().getColorRGB();
			_colorMultiLayerSpecialNormal = SelectionState.NORMAL.getColorMultiLayerSpecial().getColorRGB();
			_colorMultiLayerSpecialHighlighted = SelectionState.HIGHLIGHTED.getColorMultiLayerSpecial().getColorRGB();
			_colorMultiLayerSpecialSelected = SelectionState.SELECTED.getColorMultiLayerSpecial().getColorRGB();
			setColorOfField(getColorOfState());
		}
		
		super.setVisible(visible);
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonOk)
		{
			Config.COLOR_FLAT_NORMAL = SelectionState.NORMAL.getColorFlat().getColorRGB().getRGB();
			Config.COLOR_COMPLEX_NORMAL = SelectionState.NORMAL.getColorComplex().getColorRGB().getRGB();
			Config.COLOR_MULTILAYER_NORMAL = SelectionState.NORMAL.getColorMultiLayer().getColorRGB().getRGB();
			Config.COLOR_MULTILAYER_NORMAL_SPECIAL = SelectionState.NORMAL.getColorMultiLayerSpecial().getColorRGB().getRGB();
			Config.COLOR_FLAT_HIGHLIGHTED = SelectionState.HIGHLIGHTED.getColorFlat().getColorRGB().getRGB();
			Config.COLOR_COMPLEX_HIGHLIGHTED = SelectionState.HIGHLIGHTED.getColorComplex().getColorRGB().getRGB();
			Config.COLOR_MULTILAYER_HIGHLIGHTED = SelectionState.HIGHLIGHTED.getColorMultiLayer().getColorRGB().getRGB();
			Config.COLOR_MULTILAYER_HIGHLIGHTED_SPECIAL = SelectionState.HIGHLIGHTED.getColorMultiLayerSpecial().getColorRGB().getRGB();
			Config.COLOR_FLAT_SELECTED = SelectionState.SELECTED.getColorFlat().getColorRGB().getRGB();
			Config.COLOR_COMPLEX_SELECTED = SelectionState.SELECTED.getColorComplex().getColorRGB().getRGB();
			Config.COLOR_MULTILAYER_SELECTED = SelectionState.SELECTED.getColorMultiLayer().getColorRGB().getRGB();
			Config.COLOR_MULTILAYER_SELECTED_SPECIAL = SelectionState.SELECTED.getColorMultiLayerSpecial().getColorRGB().getRGB();
			Config.save();
			setVisible(false);
		}
		else if (e.getSource() == _buttonCancel)
		{
			// restore default
			SelectionState.NORMAL.getColorFlat().setColorRGB(_colorFlatNormal);
			SelectionState.HIGHLIGHTED.getColorFlat().setColorRGB(_colorFlatHighlighted);
			SelectionState.SELECTED.getColorFlat().setColorRGB(_colorFlatSelected);
			SelectionState.NORMAL.getColorComplex().setColorRGB(_colorComplexNormal);
			SelectionState.HIGHLIGHTED.getColorComplex().setColorRGB(_colorComplexHighlighted);
			SelectionState.SELECTED.getColorComplex().setColorRGB(_colorComplexSelected);
			SelectionState.NORMAL.getColorMultiLayer().setColorRGB(_colorMultiLayerNormal);
			SelectionState.HIGHLIGHTED.getColorMultiLayer().setColorRGB(_colorMultiLayerHighlighted);
			SelectionState.SELECTED.getColorMultiLayer().setColorRGB(_colorMultiLayerSelected);
			SelectionState.NORMAL.getColorMultiLayerSpecial().setColorRGB(_colorMultiLayerSpecialNormal);
			SelectionState.HIGHLIGHTED.getColorMultiLayerSpecial().setColorRGB(_colorMultiLayerSpecialHighlighted);
			SelectionState.SELECTED.getColorMultiLayerSpecial().setColorRGB(_colorMultiLayerSpecialSelected);
			setVisible(false);
		}
		else if (e.getSource() == _buttonRestoreDefault)
		{
			SelectionState.NORMAL.getColorFlat().setColorRGB(new Color(Config.DEFAULT_COLOR_FLAT_NORMAL));
			SelectionState.HIGHLIGHTED.getColorFlat().setColorRGB(new Color(Config.DEFAULT_COLOR_FLAT_HIGHLIGHTED));
			SelectionState.SELECTED.getColorFlat().setColorRGB(new Color(Config.DEFAULT_COLOR_FLAT_SELECTED));
			SelectionState.NORMAL.getColorComplex().setColorRGB(new Color(Config.DEFAULT_COLOR_COMPLEX_NORMAL));
			SelectionState.HIGHLIGHTED.getColorComplex().setColorRGB(new Color(Config.DEFAULT_COLOR_COMPLEX_HIGHLIGHTED));
			SelectionState.SELECTED.getColorComplex().setColorRGB(new Color(Config.DEFAULT_COLOR_COMPLEX_SELECTED));
			SelectionState.NORMAL.getColorMultiLayer().setColorRGB(new Color(Config.DEFAULT_COLOR_MULTILAYER_NORMAL));
			SelectionState.HIGHLIGHTED.getColorMultiLayer().setColorRGB(new Color(Config.DEFAULT_COLOR_MULTILAYER_HIGHLIGHTED));
			SelectionState.SELECTED.getColorMultiLayer().setColorRGB(new Color(Config.DEFAULT_COLOR_MULTILAYER_SELECTED));
			SelectionState.NORMAL.getColorMultiLayerSpecial().setColorRGB(new Color(Config.DEFAULT_COLOR_MULTILAYER_NORMAL_SPECIAL));
			SelectionState.HIGHLIGHTED.getColorMultiLayerSpecial().setColorRGB(new Color(Config.DEFAULT_COLOR_MULTILAYER_HIGHLIGHTED_SPECIAL));
			SelectionState.SELECTED.getColorMultiLayerSpecial().setColorRGB(new Color(Config.DEFAULT_COLOR_MULTILAYER_SELECTED_SPECIAL));
			setColorOfField(getColorOfState());
		}
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public final void stateChanged(final ChangeEvent e)
	{
		final Color color = getColorOfField();
		setColorOfField(color);
		setColorOfState(color);
	}
	
	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public final void itemStateChanged(final ItemEvent e)
	{
		if (e.getSource() == _comboSelectionState)
		{
			setColorOfField(getColorOfState());
		}
		else if (e.getSource() == _comboBlockType)
		{
			setColorOfField(getColorOfState());
		}
	}
}