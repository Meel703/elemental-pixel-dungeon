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

package com.elementalpixel.elementalpixeldungeon.levels.traps;


import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.Mob;
import com.elementalpixel.elementalpixeldungeon.effects.CellEmitter;
import com.elementalpixel.elementalpixeldungeon.effects.Speck;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class AlarmTrap extends Trap {

	{
		color = RED;
		shape = DOTS;
	}

	@Override
	public void activate() {

		for (Mob mob : Dungeon.level.mobs) {
				mob.beckon( pos );
		}

		if (Dungeon.level.heroFOV[pos]) {
			GLog.w( Messages.get(this, "alarm") );
			CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		}

		Sample.INSTANCE.play( Assets.Sounds.ALERT );
	}
}
