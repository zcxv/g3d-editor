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
package g3deditor.entity;

import g3deditor.Config;
import g3deditor.geo.GeoBlock;
import g3deditor.geo.GeoCell;
import g3deditor.geo.GeoEngine;
import g3deditor.jogl.GLColor;
import g3deditor.jogl.GLDisplay;
import g3deditor.swing.FrameMain;
import g3deditor.util.Util;

import java.awt.Color;

/**
 * <a href="http://l2j-server.com/">L2jServer</a>
 * 
 * @author Forsaiken aka Patrick, e-mail: patrickbiesenbach@yahoo.de
 */
public enum SelectionState
{
	NORMAL		(Config.COLOR_FLAT_NORMAL, Config.COLOR_COMPLEX_NORMAL, Config.COLOR_MULTILAYER_NORMAL, Config.COLOR_MULTILAYER_NORMAL_SPECIAL),
	HIGHLIGHTED	(Config.COLOR_FLAT_HIGHLIGHTED, Config.COLOR_COMPLEX_HIGHLIGHTED, Config.COLOR_MULTILAYER_HIGHLIGHTED, Config.COLOR_MULTILAYER_HIGHLIGHTED_SPECIAL),
	SELECTED	(Config.COLOR_FLAT_SELECTED, Config.COLOR_COMPLEX_SELECTED, Config.COLOR_MULTILAYER_SELECTED, Config.COLOR_MULTILAYER_SELECTED_SPECIAL);
	
	public static final float ALPHA = 0.7f;
	
	private final GLColor _colorGuiSelected;
	private final GLColor _colorFlat;
	private final GLColor _colorComplex1;
	private final GLColor _colorComplex2;
	private final GLColor _colorMutliLayer1;
	private final GLColor _colorMutliLayer2;
	private final GLColor _colorMutliLayer1Special;
	private final GLColor _colorMutliLayer2Special;
	
	private SelectionState(final int colorFlat, final int colorComplex, final int colorMutliLayer, final int colorMultiLayerSpecial)
	{
		_colorGuiSelected = new GLColor(Color.YELLOW, ALPHA);
		_colorFlat = new GLColor(new Color(colorFlat), ALPHA);
		_colorComplex1 = new GLColor(new Color(colorComplex), ALPHA);
		_colorComplex2 = new GLColor(_colorComplex1, 0.85f, 0.85f, 0.85f);
		_colorMutliLayer1 = new GLColor(new Color(colorMutliLayer), ALPHA);
		_colorMutliLayer2 = new GLColor(_colorMutliLayer1, 0.85f, 0.85f, 0.85f);
		_colorMutliLayer1Special = new GLColor(new Color(colorMultiLayerSpecial), ALPHA);
		_colorMutliLayer2Special = new GLColor(_colorMutliLayer1Special, 0.85f, 0.85f, 0.85f);
	}
	
	public final GLColor getColorGuiSelected()
	{
		return _colorGuiSelected;
	}
	
	public final GLColor getColorFlat()
	{
		return _colorFlat;
	}
	
	public final GLColor getColorComplex()
	{
		return _colorComplex1;
	}
	
	public final GLColor getColorMultiLayer()
	{
		return _colorMutliLayer1;
	}
	
	public final void updateSecondaryColors()
	{
		_colorComplex2.setRGB(_colorComplex1.getR() * 0.85f, _colorComplex1.getG() * 0.85f, _colorComplex1.getB() * 0.85f);
		_colorMutliLayer2.setRGB(_colorMutliLayer1.getR() * 0.85f, _colorMutliLayer1.getG() * 0.85f, _colorMutliLayer1.getB() * 0.85f);
		_colorMutliLayer2Special.setRGB(_colorMutliLayer1Special.getR() * 0.85f, _colorMutliLayer1Special.getG() * 0.85f, _colorMutliLayer1Special.getB() * 0.85f);
	}
	
	/**
	 * For the cells inside the selection box
	 * 
	 * @return
	 */
	public final GLColor getColorMultiLayerSpecial()
	{
		return _colorMutliLayer1Special;
	}
	
	public final GLColor getColor(final GeoCell cell)
	{
		if (FrameMain.getInstance().isSelectedGeoCell(cell))
			return _colorGuiSelected;
		
		return getColor(cell.getBlock(), GLDisplay.getInstance().getSelectionBox().isInside(cell));
	}
	
	public final GLColor getColor(final GeoBlock block, final boolean insideSelectionBox)
	{
		switch (block.getType())
		{
			case GeoEngine.GEO_BLOCK_TYPE_FLAT:
				return _colorFlat;
				
			case GeoEngine.GEO_BLOCK_TYPE_COMPLEX:
				return block.getBlockX() % 2 != block.getBlockY() % 2 ? _colorComplex2 : _colorComplex1;
				
			default:
			{
				if (insideSelectionBox)
					return block.getBlockX() % 2 != block.getBlockY() % 2 ? _colorMutliLayer2Special : _colorMutliLayer1Special;
				
				return block.getBlockX() % 2 != block.getBlockY() % 2 ? _colorMutliLayer2 : _colorMutliLayer1;
			}
		}
	}
	
	@Override
	public final String toString()
	{
		return Util.capitalizeString(name());
	}
}