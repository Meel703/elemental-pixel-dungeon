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

package com.elementalpixel.elementalpixeldungeon.items.potions;


import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Buff;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Invisibility;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.actors.hero.HeroClass;
import com.elementalpixel.elementalpixeldungeon.actors.hero.HeroSubClass;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Talent;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSpriteSheet;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfInvisibility extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_INVIS;
	}

	@Override
	public void apply( Hero hero ) {
		identify();
		if (curUser.subClass == HeroSubClass.SCIENTIST) {
			Buff.affect( hero, Invisibility.class, Invisibility.DURATION * 1.4f);
		} else {
			Buff.affect( hero, Invisibility.class, Invisibility.DURATION);
		}
		GLog.i( Messages.get(this, "invisible") );
		Sample.INSTANCE.play( Assets.Sounds.MELD );
	}
	
	@Override
	public int value() {
		if (Dungeon.hero.hasTalent(Talent.FAMILIAR_FACE)) {
			if (Dungeon.hero.pointsInTalent(Talent.FAMILIAR_FACE) == 1) {
				return isKnown() ? (40 * quantity) / 4 * 3 : super.value();
			} else {
				return isKnown() ? (40 * quantity) / 2 : super.value();
			}
		} else {
			return isKnown() ? 40 * quantity : super.value();
		}
	}

}
