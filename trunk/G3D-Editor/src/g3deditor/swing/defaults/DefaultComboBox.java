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

import javax.swing.JComboBox;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
@SuppressWarnings("serial")
public class DefaultComboBox extends JComboBox
{
	public DefaultComboBox()
	{
		
	}
	
	public DefaultComboBox(final Object[] items)
	{
		super(items);
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
}