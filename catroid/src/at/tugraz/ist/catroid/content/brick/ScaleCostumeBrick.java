/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.brick;

import at.tugraz.ist.catroid.content.sprite.Sprite;

/**
 * @author Anton Rieder, Ainul Husna
 *
 */
public class ScaleCostumeBrick implements Brick {
	private transient Sprite sprite;
	private double scale;
	private static final long serialVersionUID = 1L;

	public ScaleCostumeBrick(Sprite sprite, double scale) {
		this.sprite = sprite;
		this.scale  = scale;
	}

	public void execute() {
		if (scale <= 0.0)
			throw new IllegalArgumentException("Sprite scale must be greater than zero!");
		sprite.setScale(scale);
	}

}
