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
import g3deditor.swing.defaults.DefaultComboBox;
import g3deditor.swing.defaults.DefaultLabel;
import g3deditor.util.FastArrayList;
import g3deditor.util.Util.FastComparator;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class DialogRemoveLayers extends JDialog implements ActionListener, ItemListener
{
	private static final FastComparator<Integer> HEIGHT_COMPARATOR = new FastComparator<Integer>()
	{
		@Override
		public final boolean compare(final Integer o1, final Integer o2)
		{
			return o1.intValue() < o2.intValue();
		}
	};
	
	private static final String REMOVE_MODE_SELECTED = "Remove selected";
	private static final String REMOVE_MODE_LAYER = "Remove layer(s)";
	private static final String REMOVE_MODE_HEIGHT = "Remove height(s)";
	
	private final DefaultLabel _labelRemoveMode;
	private final DefaultComboBox _comboRemoveMode;
	private final DefaultCheckBox _checkRemoveRemoveFromFullBlock;
	
	private final DefaultLabel _labelMinHeightLayer;
	private final DefaultComboBox _comboMinHeightLayer;
	private final DefaultLabel _labelMaxHeightLayer;
	private final DefaultComboBox _comboMaxHeightLayer;
	
	private final JPanel _panelButtons;
	private final DefaultButton _buttonOk;
	private final DefaultButton _buttonCancel;
	
	public DialogRemoveLayers(final Frame owner)
	{
		super(owner, "Remove Layer(s)", true);
		
		_labelRemoveMode = new DefaultLabel("Mode:");
		_comboRemoveMode = new DefaultComboBox(new String[]{REMOVE_MODE_SELECTED, REMOVE_MODE_LAYER, REMOVE_MODE_HEIGHT});
		_comboRemoveMode.addItemListener(this);
		_checkRemoveRemoveFromFullBlock = new DefaultCheckBox("Remove from full block");
		_checkRemoveRemoveFromFullBlock.addActionListener(this);
		
		_labelMinHeightLayer = new DefaultLabel("Min:");
		_comboMinHeightLayer = new DefaultComboBox();
		_comboMinHeightLayer.addItemListener(this);
		_labelMaxHeightLayer = new DefaultLabel("Max:");
		_comboMaxHeightLayer = new DefaultComboBox();
		_comboMaxHeightLayer.addItemListener(this);
		
		_panelButtons = new JPanel();
		_buttonOk = new DefaultButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonCancel = new DefaultButton("Cancel");
		_buttonCancel.addActionListener(this);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
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
		add(_labelRemoveMode, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_comboRemoveMode, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_checkRemoveRemoveFromFullBlock, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelMinHeightLayer, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_comboMinHeightLayer, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelMaxHeightLayer, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_comboMaxHeightLayer, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
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
	
	private final void enableLayerHeight(final boolean enabled)
	{
		_labelMinHeightLayer.setEnabled(enabled);
		_comboMinHeightLayer.setEnabled(enabled);
		_labelMaxHeightLayer.setEnabled(enabled);
		_comboMaxHeightLayer.setEnabled(enabled);
		_checkRemoveRemoveFromFullBlock.setEnabled(enabled);
	}
	
	private final void checkLayerHeightSelection()
	{
		_comboMinHeightLayer.removeItemListener(this);
		_comboMaxHeightLayer.removeItemListener(this);
		_comboMinHeightLayer.removeAllItems();
		_comboMaxHeightLayer.removeAllItems();
		
		if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_SELECTED)
		{
			enableLayerHeight(false);
		}
		else
		{
			final FastArrayList<Integer> layersHeights = new FastArrayList<>();
			final GeoBlockSelector selector = GeoBlockSelector.getInstance();
			
			GeoBlock block;
			int maxLayerCount = 1;
			for (GeoBlockEntry e = selector.getHead(); (e = e.getNext()) != selector.getTail();)
			{
				block = e.getKey();
				if (block.getType() == GeoEngine.GEO_BLOCK_TYPE_MULTILAYER)
				{
					if (_checkRemoveRemoveFromFullBlock.isSelected())
					{
						if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_LAYER)
						{
							for (int cellX = GeoEngine.GEO_BLOCK_SHIFT, cellY; cellX-- > 0;)
							{
								for (cellY = GeoEngine.GEO_BLOCK_SHIFT; cellY-- > 0;)
								{
									maxLayerCount = Math.max(maxLayerCount, block.nGetLayerCount(cellX, cellY));
								}
							}
						}
						else
						{
							Integer height;
							for (final GeoCell cell : block.getCells())
							{
								height = Integer.valueOf(cell.getHeight());
								if (!layersHeights.contains(height))
									layersHeights.add(height);
							}
						}
					}
					else
					{
						final FastArrayList<GeoCell> selected = e.getValue();
						GeoCell cell;
						Integer height;
						for (int i = selected.size(); i-- > 0;)
						{
							cell = selected.get(i);
							if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_LAYER)
							{
								maxLayerCount = Math.max(maxLayerCount, block.nGetLayerCount(cell.getCellX(), cell.getCellY()));
							}
							else
							{
								for (final GeoCell layer : block.nGetLayers(cell.getCellX(), cell.getCellY()))
								{
									height = Integer.valueOf(layer.getHeight());
									if (!layersHeights.contains(height))
										layersHeights.add(height);
								}
							}
						}
					}
				}
			}
			
			if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_LAYER)
			{
				for (int layer = 0; layer < maxLayerCount; layer++)
				{
					_comboMinHeightLayer.addItem(Integer.valueOf(layer));
					_comboMaxHeightLayer.addItem(Integer.valueOf(layer));
				}
				_comboMinHeightLayer.addItemListener(this);
				_comboMaxHeightLayer.addItemListener(this);
			}
			else
			{
				layersHeights.sort(HEIGHT_COMPARATOR);
				for (int i = layersHeights.size(); i-- > 0;)
				{
					_comboMinHeightLayer.addItem(layersHeights.getUnsafe(i));
					_comboMaxHeightLayer.addItem(layersHeights.getUnsafe(i));
				}
				_comboMinHeightLayer.addItemListener(this);
				_comboMaxHeightLayer.addItemListener(this);
			}
			
			enableLayerHeight(true);
		}
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (!isVisible() && visible)
		{
			setLocationRelativeTo(getOwner());
			_comboRemoveMode.setSelectedItem(REMOVE_MODE_SELECTED);
			_checkRemoveRemoveFromFullBlock.setSelected(false);
			checkLayerHeightSelection();
		}
		
		super.setVisible(visible);
		if (!visible)
			GLDisplay.getInstance().requestFocus();
	}
	
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _checkRemoveRemoveFromFullBlock)
		{
			checkLayerHeightSelection();
		}
		else if (e.getSource() == _buttonOk)
		{
			final GeoBlockSelector selector = GeoBlockSelector.getInstance();
			final FastArrayList<GeoCell> toRemove = new FastArrayList<>();
			
			FastArrayList<GeoCell> selected;
			GeoBlock block;
			GeoCell cell;
			for (GeoBlockEntry entry = selector.getHead(); (entry = entry.getNext()) != selector.getTail();)
			{
				block = entry.getKey();
				if (block.getType() == GeoEngine.GEO_BLOCK_TYPE_MULTILAYER)
				{
					selected = entry.getValue();
					toRemove.clear();
					if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_SELECTED)
					{
						toRemove.addAll(selected);
					}
					else
					{
						if (_checkRemoveRemoveFromFullBlock.isSelected())
						{
							if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_LAYER)
							{
								final int minLayer = (Integer) _comboMinHeightLayer.getSelectedItem();
								final int maxLayer = (Integer) _comboMaxHeightLayer.getSelectedItem();
								
								GeoCell[] cells;
								for (int cellX = GeoEngine.GEO_BLOCK_SHIFT, cellY, layer; cellX-- > 0;)
								{
									for (cellY = GeoEngine.GEO_BLOCK_SHIFT; cellY-- > 0;)
									{
										cells = block.nGetLayers(cellX, cellY);
										if (cells.length > minLayer)
										{
											for (layer = Math.min(maxLayer + 1, cells.length); layer-- > minLayer;)
											{
												toRemove.add(cells[layer]);
											}
										}
									}
								}
							}
							else
							{
								final int minHeight = (Integer) _comboMinHeightLayer.getSelectedItem();
								final int maxHeight = (Integer) _comboMaxHeightLayer.getSelectedItem();
								
								GeoCell[] cells;
								for (int cellX = GeoEngine.GEO_BLOCK_SHIFT, cellY, height, layer; cellX-- > 0;)
								{
									for (cellY = GeoEngine.GEO_BLOCK_SHIFT; cellY-- > 0;)
									{
										cells = block.nGetLayers(cellX, cellY);
										for (layer = cells.length; layer-- > 0;)
										{
											cell = cells[layer];
											height = cell.getHeight();
											if (height >= minHeight && height <= maxHeight)
												toRemove.add(cell);
										}
									}
								}
							}
						}
						else
						{
							if (_comboRemoveMode.getSelectedItem() == REMOVE_MODE_LAYER)
							{
								final int minLayer = (Integer) _comboMinHeightLayer.getSelectedItem();
								final int maxLayer = (Integer) _comboMaxHeightLayer.getSelectedItem();
								
								GeoCell[] cells;
								for (int i = selected.size(), layer; i-- > 0;)
								{
									cell = selected.get(i);
									cells = block.nGetLayers(cell.getCellX(), cell.getCellY());
									if (cells.length > minLayer)
									{
										for (layer = Math.min(maxLayer + 1, cells.length); layer-- > minLayer;)
										{
											toRemove.add(cells[layer]);
										}
									}
								}
							}
							else
							{
								final int minHeight = (Integer) _comboMinHeightLayer.getSelectedItem();
								final int maxHeight = (Integer) _comboMaxHeightLayer.getSelectedItem();
								
								for (int i = selected.size(), height; i-- > 0;)
								{
									cell = selected.get(i);
									height = cell.getHeight();
									if (height >= minHeight && height <= maxHeight)
										toRemove.add(cell);
								}
							}
						}
					}
					
					block.removeCells(toRemove.toArray(new GeoCell[toRemove.size()]));
				}
			}
			
			setVisible(false);
		}
		else if (e.getSource() == _buttonCancel)
		{
			setVisible(false);
		}
	}
	
	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public final void itemStateChanged(final ItemEvent e)
	{
		if (e.getSource() == _comboRemoveMode)
		{
			checkLayerHeightSelection();
		}
		else
		{
			final int indexMin = _comboMinHeightLayer.getSelectedIndex();
			final int indexMax = _comboMaxHeightLayer.getSelectedIndex();
			
			if (indexMin > indexMax)
			{
				if (e.getSource() == _comboMinHeightLayer)
				{
					_comboMaxHeightLayer.setSelectedIndex(indexMin);
				}
				else if (e.getSource() == _comboMaxHeightLayer)
				{
					_comboMinHeightLayer.setSelectedIndex(indexMax);
				}
			}
		}
	}
}