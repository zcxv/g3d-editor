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

import g3deditor.geo.GeoEngine;
import g3deditor.geo.GeoRegion;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public final class DialogSave extends JDialog implements ActionListener, MouseListener
{
	private static final FileFilter FILE_FILTER = new FileFilter()
	{
		public final boolean accept(final File file)
		{
			return true;
		}
		
		@Override
		public final String getDescription()
		{
			return "GeoFile";
		}
	};
	
	private final GeoRegion _region;
	private final JFileChooser _fileChooser;
	
	private final JLabel _labelFile;
	private final JTextField _fieldFile;
	
	private final JProgressBar _progressRegion;
	
	private final JPanel _panelType;
	private final JLabel _labelType;
	private final JCheckBox _checkL2j;
	private final JCheckBox _checkL2Off;
	
	private final JPanel _panelButtons;
	private final JButton _buttonOk;
	private final JButton _buttonCancel;
	
	public DialogSave(final Frame owner, final GeoRegion region)
	{
		super(owner, "Save Region " + region.getName(), true);
		
		_region = region;
		_fileChooser = new JFileChooser();
		_fileChooser.removeChoosableFileFilter(_fileChooser.getAcceptAllFileFilter());
		_fileChooser.setFileFilter(FILE_FILTER);
		_fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		_fileChooser.setDialogTitle("Save as...");
		_fileChooser.setSelectedFile(region.getFile());
		_fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		_labelFile = new JLabel("File:");
		_fieldFile = new JTextField(22);
		_fieldFile.setEditable(false);
		_fieldFile.setText(_region.getFile().toString());
		_fieldFile.addMouseListener(this);
		
		_progressRegion = new JProgressBar();
		_progressRegion.setMaximum(GeoEngine.GEO_REGION_SIZE * GeoEngine.GEO_REGION_SIZE);
		_progressRegion.setStringPainted(true);
		_progressRegion.setString("");
		_progressRegion.setEnabled(false);
		
		_panelType = new JPanel();
		_labelType = new JLabel("GeoType:");
		_checkL2j = new JCheckBox("L2j");
		_checkL2j.addActionListener(this);
		_checkL2Off = new JCheckBox("L2Off");
		_checkL2Off.addActionListener(this);
		
		_checkL2Off.setSelected(region.getFile().getName().endsWith(".dat"));
		_checkL2j.setSelected(!_checkL2Off.isSelected());
		_checkL2j.setEnabled(_checkL2Off.isSelected() || _checkL2j.isSelected());
		_checkL2Off.setEnabled(_checkL2Off.isSelected() || _checkL2j.isSelected());
		
		_panelButtons = new JPanel();
		_buttonOk = new JButton("Ok");
		_buttonOk.addActionListener(this);
		_buttonOk.setEnabled(_checkL2Off.isSelected() || _checkL2j.isSelected());
		_buttonCancel = new JButton("Cancel");
		_buttonCancel.addActionListener(this);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		
		_panelButtons.setLayout(new GridLayout(1, 2));
		_panelButtons.add(_buttonOk);
		_panelButtons.add(_buttonCancel);
		
		_panelType.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelType.add(_labelType, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelType.add(_checkL2j, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelType.add(_checkL2Off, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		_panelType.add(new JLabel(), gbc);
		
		setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_labelFile, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_fieldFile, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_progressRegion, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		add(_panelType, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 0;
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
			dispose();
	}
	
	@Override
	public final void actionPerformed(final ActionEvent e)
	{
		if (e.getSource() == _buttonOk)
		{
			final File file = _fileChooser.getSelectedFile();
			if (file != null && FILE_FILTER.accept(file))
			{
				_checkL2j.setEnabled(false);
				_checkL2Off.setEnabled(false);
				_fieldFile.setEnabled(false);
				_buttonOk.setEnabled(false);
				_buttonCancel.setEnabled(false);
				_progressRegion.setEnabled(true);
				
				new Thread()
				{
					@SuppressWarnings("synthetic-access")
					@Override
					public final void run()
					{
						FileOutputStream fos = null;
						try
						{
							fos = new FileOutputStream(file);
							final BufferedOutputStream bos = new BufferedOutputStream(fos);
							_region.saveTo(bos, _checkL2j.isSelected(), DialogSave.this);
							bos.flush();
						}
						catch (final IOException e1)
						{
							JOptionPane.showMessageDialog(FrameMain.getInstance(), "Failed storing " + file + "\n" + e1.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
						}
						finally
						{
							try
							{
								if (fos != null)
									fos.close();
							}
							catch (final Exception e1)
							{
								
							}
						}
						onSaveComplete();
					}
				}.start();
			}
		}
		else if (e.getSource() == _buttonCancel)
		{
			setVisible(false);
		}
		else if (e.getSource() == _checkL2j)
		{
			_checkL2Off.setSelected(!_checkL2j.isSelected());
			updateFile();
		}
		else if (e.getSource() == _checkL2Off)
		{
			_checkL2j.setSelected(!_checkL2Off.isSelected());
			updateFile();
		}
	}
	
	private final void updateFile()
	{
		final File file = _fileChooser.getSelectedFile();
		if (file != null && FILE_FILTER.accept(file))
		{
			final String path = file.getAbsolutePath();
			final String newPath = path.substring(0, path.length() - file.getName().length()) + _region.getName() + (_checkL2Off.isSelected() ? "_conv.dat" : ".l2j");
			_fileChooser.setSelectedFile(new File(newPath));
			_fieldFile.setText(_fileChooser.getSelectedFile().toString());
		}
	}
	
	public final GeoRegion getRegion()
	{
		return _region;
	}
	
	public final void onSaveComplete()
	{
		_progressRegion.setValue(0);
		_progressRegion.setString("");
		
		_fieldFile.setEnabled(true);
		final File file = _fileChooser.getSelectedFile();
		_buttonOk.setEnabled(file != null && FILE_FILTER.accept(file));
		_checkL2j.setEnabled(_buttonOk.isEnabled());
		_checkL2Off.setEnabled(_buttonOk.isEnabled());
		_buttonCancel.setEnabled(true);
		_progressRegion.setEnabled(false);
	}
	
	private final int precent(final JProgressBar bar)
	{
		return (int) (bar.getPercentComplete() * 100D);
	}
	
	private boolean checkNeedUpdate(final JProgressBar bar, final int progress)
	{
		return precent(bar) != (int) (((double) progress - bar.getMinimum()) / (bar.getMaximum() - bar.getMinimum()) * 100D);
	}
	
	public final void updateProgressRegion(final int progress, final String blockName)
	{
		if (!checkNeedUpdate(_progressRegion, progress))
			return;
		
		_progressRegion.setValue(progress);
		_progressRegion.setString(blockName + "   " + precent(_progressRegion) + "%");
	}
	
	@Override
	public final void mouseClicked(final MouseEvent e)
	{
		if (e.getSource() == _fieldFile)
		{
			if (_fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				final File file = _fileChooser.getSelectedFile();
				if (file != null && FILE_FILTER.accept(file))
				{
					_fieldFile.setText(file.toString());
					_checkL2j.setEnabled(true);
					_checkL2Off.setEnabled(true);
					_buttonOk.setEnabled(true);
				}
				else
				{
					_checkL2j.setEnabled(false);
					_checkL2Off.setEnabled(false);
					_buttonOk.setEnabled(false);
				}
			}
		}
	}
	
	@Override
	public final void mousePressed(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseReleased(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseEntered(final MouseEvent e)
	{
		
	}
	
	@Override
	public final void mouseExited(final MouseEvent e)
	{
		
	}
}