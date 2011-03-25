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

import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoBlockSelector;
import g3deditor.geo.GeoBlockSelector.GeoBlockEntry;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLDisplay;
import g3deditor.swing.defaults.DefaultButton;
import g3deditor.swing.defaults.DefaultCheckBox;
import g3deditor.swing.defaults.DefaultLabel;
import g3deditor.swing.defaults.DefaultTextField;
import g3deditor.util.FastArrayList;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class DialogAddLayers extends JDialog implements ActionListener, DocumentListener
{
	private final JPanel _panelChecks;
	private final DefaultCheckBox _checkAddToFullBlock;
	private final DefaultCheckBox _checkAddIfHeightAlreadyExist;
	private final DefaultCheckBox _checkAutoSelectAdded;
	private final DefaultCheckBox _checkAutoSelectAppend;
	
	private final DefaultLabel _labelHeight;
	private final DefaultTextField _fieldHeight;
	
	private final JPanel _panelButtons;
	private final DefaultButton _buttonOk;
	private final DefaultButton _buttonCancel;
	
	public DialogAddLayers(final Frame owner)
	{
		super(owner, "Add Layer(s)", true);
		
		_panelChecks = new JPanel();
		_checkAddToFullBlock = new DefaultCheckBox("Apply to full block");
		_checkAddIfHeightAlreadyExist = new DefaultCheckBox("Add if already exist");
		_checkAutoSelectAdded = new DefaultCheckBox("Selecte added cells");
		_checkAutoSelectAppend = new DefaultCheckBox("Append to selection");
		
		_labelHeight = new DefaultLabel("Height:");
		_fieldHeight = new DefaultTextField();
		_fieldHeight.getDocument().addDocumentListener(this);
		
		_panelButtons = new JPanel();
		_buttonOk = new DefaultButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonOk.setEnabled(false);
		_buttonCancel = new DefaultButton("Cancel");
		_buttonCancel.addActionListener(this);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		
		_panelChecks.setLayout(new GridLayout(4, 1));
		_panelChecks.add(_checkAddToFullBlock);
		_panelChecks.add(_checkAddIfHeightAlreadyExist);
		_panelChecks.add(_checkAutoSelectAdded);
		_panelChecks.add(_checkAutoSelectAppend);
		
		_panelButtons.setLayout(new GridLayout(1, 2));
		_panelButtons.add(_buttonOk);
		_panelButtons.add(_buttonCancel);
		
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelChecks, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelHeight, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldHeight, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelButtons, gbc);
		
		setResizable(false);
		pack();
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
			setLocationRelativeTo(getOwner());
		
		super.setVisible(visible);
		if (!visible)
			GLDisplay.getInstance().requestFocus();
	}
	
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonOk)
		{
			final short heightAndNSWE;
			
			try
			{
				final int height = Integer.parseInt(_fieldHeight.getText());
				if (height < GeoEngine.HEIGHT_MIN_VALUE || height > GeoEngine.HEIGHT_MAX_VALUE)
					throw new NumberFormatException();
				
				heightAndNSWE = GeoEngine.convertHeightToHeightAndNSWEALL((short) height);
			}
			catch (final NumberFormatException e1)
			{
				return;
			}
			
			final GeoBlockSelector selector = GeoBlockSelector.getInstance();
			if (!selector.hasSelected())
				return;
			
			final FastArrayList<GeoCell> added = new FastArrayList<GeoCell>();
			
			GeoBlock block;
			GeoCell temp;
			FastArrayList<GeoCell> selected;
			for (GeoBlockEntry entry = selector.getHead(); (entry = entry.getNext()) != selector.getTail();)
			{
				block = entry.getKey();
				if (block.getType() == GeoEngine.GEO_BLOCK_TYPE_MULTILAYER)
				{
					if (_checkAddToFullBlock.isSelected())
					{
						for (int cellX = GeoEngine.GEO_BLOCK_SHIFT, cellY; cellX-- > 0;)
						{
							for (cellY = GeoEngine.GEO_BLOCK_SHIFT; cellY-- > 0;)
							{
								if (!_checkAddIfHeightAlreadyExist.isSelected())
								{
									temp = block.nGetCell(cellX, cellY, GeoEngine.getHeight(heightAndNSWE));
									if (temp.getHeightAndNSWE() == heightAndNSWE)
										continue;
								}
									
								temp = block.addLayer(cellX, cellY, heightAndNSWE);
								if (temp != null)
									added.addLast(temp);
							}
						}
					}
					else
					{
						GeoCell temp2;
						boolean contains;
						selected = entry.getValue();
						for (int i = selected.size(), j; i-- > 0;)
						{
							temp = selected.getUnsafe(i);
							contains = false;
							for (j = added.size(); j-- > 0;)
							{
								temp2 = added.getUnsafe(j);
								if (temp2.getBlock() == temp.getBlock() && temp2.getCellX() == temp.getCellX() && temp2.getCellY() == temp.getCellY())
								{
									contains = true;
									break;
								}
							}
							if (!contains)
							{
								if (!_checkAddIfHeightAlreadyExist.isSelected())
								{
									temp = block.nGetCell(temp.getCellX(), temp.getCellY(), GeoEngine.getHeight(heightAndNSWE));
									if (temp.getHeightAndNSWE() == heightAndNSWE)
										continue;
								}
								
								temp = block.addLayer(temp.getCellX(), temp.getCellY(), heightAndNSWE);
								if (temp != null)
								{
									added.addLast(temp);
								}
							}
						}
					}
				}
			}
			
			if (!added.isEmpty())
			{
				if (!_checkAutoSelectAppend.isSelected())
					selector.unselectAll();
				
				for (int i = added.size(); i-- > 0;)
				{
					temp = added.getUnsafe(i);
					selector.selectGeoCell(temp, false, true);
				}
				GLDisplay.getInstance().getRenderSelector().forceUpdateFrustum();
			}
			
			setVisible(false);
		}
		else if (e.getSource() == _buttonCancel)
		{
			setVisible(false);
		}
	}
	
	private final void checkHeightOk()
	{
		try
		{
			final int value = Integer.parseInt(_fieldHeight.getText());
			if (value < GeoEngine.HEIGHT_MIN_VALUE || value > GeoEngine.HEIGHT_MAX_VALUE)
				throw new NumberFormatException();
		}
		catch (final NumberFormatException e)
		{
			_buttonOk.setEnabled(false);
			_fieldHeight.setForeground(Color.RED);
			_fieldHeight.setToolTipText("Non numbers or values < " + GeoEngine.HEIGHT_MIN_VALUE + " or > " + GeoEngine.HEIGHT_MAX_VALUE + " are invalid");
			return;
		}
		
		_buttonOk.setEnabled(true);
		_fieldHeight.setForeground(Color.BLACK);
		_fieldHeight.setToolTipText(null);
	}
	
	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public final void insertUpdate(final DocumentEvent e)
	{
		checkHeightOk();
	}
	
	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public final void removeUpdate(final DocumentEvent e)
	{
		checkHeightOk();
	}
	
	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public final void changedUpdate(final DocumentEvent e)
	{
		
	}
}