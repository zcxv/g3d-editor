package g3deditor.swing;

import g3deditor.jogl.GLColor;
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public final class DialogColorChooser extends JDialog implements ChangeListener, ActionListener
{
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
	private final DefaultLabel _labelPreviewColor;
	
	private final JPanel _panelButtons;
	private final DefaultButton _buttonOk;
	private final DefaultButton _buttonCancel;
	
	public DialogColorChooser(final JFrame frame, final Color color)
	{
		super(frame, "Color...", true);
		
		_labelRed = new DefaultLabel("Red:");
		_sliderRed = new JSlider(0, 255, color.getRed());
		_sliderRed.addChangeListener(this);
		_fieldRed = new DefaultTextField(10);
		_labelGreen = new DefaultLabel("Green:");
		_sliderGreen = new JSlider(0, 255, color.getGreen());
		_sliderGreen.addChangeListener(this);
		_fieldGreen = new DefaultTextField(10);
		_labelBlue = new DefaultLabel("Blue:");
		_sliderBlue = new JSlider(0, 255, color.getBlue());
		_sliderBlue.addChangeListener(this);
		_fieldBlue = new DefaultTextField(10);
		
		_labelPreview = new DefaultLabel("Preview:");
		_labelPreviewColor = new DefaultLabel();
		_labelPreviewColor.setForeground(color);
		
		_panelButtons = new JPanel();
		_buttonOk = new DefaultButton("Ok");
		_buttonCancel = new DefaultButton("Cancel");
		
		initLayout();
	}
	
	private final void initLayout()
	{
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		
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
		add(_labelRed, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_sliderRed, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldRed, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelGreen, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_sliderGreen, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldGreen, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelBlue, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_sliderBlue, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldBlue, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelPreview, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelPreviewColor, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelButtons, gbc);
	}
	
	public final void setColor(final GLColor color)
	{
		setColor(color.getColor());
	}
	
	public final void setColor(final Color color)
	{
		_sliderRed.setValue(color.getRed());
		_sliderGreen.setValue(color.getGreen());
		_sliderBlue.setValue(color.getBlue());
		
		_fieldRed.setText(String.valueOf(color.getRed()));
		_fieldGreen.setText(String.valueOf(color.getGreen()));
		_fieldBlue.setText(String.valueOf(color.getBlue()));
		_labelPreviewColor.setForeground(color);
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
		{
			setLocationRelativeTo(super.getOwner());
		}
		
		super.setVisible(visible);
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public final void stateChanged(final ChangeEvent e)
	{
		final int red = _sliderRed.getValue();
		final int green = _sliderGreen.getValue();
		final int blue = _sliderBlue.getValue();
		setColor(new Color(red, green, blue));
	}
}