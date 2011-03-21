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
package g3deditor.swing.defaults;

import javax.swing.JTextField;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public class DefaultTextField extends JTextField
{
	public DefaultTextField()
	{
		
	}
	
	public DefaultTextField(final int columns)
	{
		super(columns);
	}
	
	public DefaultTextField(final String text)
	{
		super(text);
	}
	
	public DefaultTextField(final String text, final int columns)
	{
		super(text, columns);
	}
	
	@Override
	public final void setEnabled(final boolean enabled)
	{
		if (isEnabled() != enabled)
			super.setEnabled(enabled);
	}
	
	@Override
	public final void setVisible(final boolean visible)
	{
		if (isVisible() != visible)
			super.setVisible(visible);
	}
	
	@Override
	public void setText(final String text)
	{
		if (text != null)
		{
			if (text.equals(getText()))
				return;
		}
		else
		{
			if (getText() == null)
				return;
		}
		super.setText(text);
	}
}