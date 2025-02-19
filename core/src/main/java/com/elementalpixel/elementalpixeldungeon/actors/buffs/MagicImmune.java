/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.elementalpixel.elementalpixeldungeon.actors.buffs;


import com.elementalpixel.elementalpixeldungeon.items.armor.glyphs.AntiMagic;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class MagicImmune extends FlavourBuff {

	public static final float DURATION = 20f;
	
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	{
		immunities.addAll(AntiMagic.RESISTS);
	}
	
	//FIXME what about active buffs/debuffs?, what about rings? what about artifacts?
	
	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1, 0);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
	
}
