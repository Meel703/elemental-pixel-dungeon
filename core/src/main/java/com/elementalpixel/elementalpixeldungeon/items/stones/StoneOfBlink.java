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

package com.elementalpixel.elementalpixeldungeon.items.stones;


import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Actor;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.elementalpixel.elementalpixeldungeon.levels.InfernalBastionBossLevel;
import com.elementalpixel.elementalpixeldungeon.mechanics.Ballistica;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSpriteSheet;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;

public class StoneOfBlink extends Runestone {
	
	{
		image = ItemSpriteSheet.STONE_BLINK;
	}
	
	private static Ballistica throwPath;
	
	@Override
	public int throwPos(Hero user, int dst) {
		if (Dungeon.bossLevel() && (InfernalBastionBossLevel.boss.phase == 0 || InfernalBastionBossLevel.boss.phase == 3 || InfernalBastionBossLevel.boss.phase == 3.1f)) {
			GLog.w(Messages.get(StoneOfBlink.class, "no_tele"));
			return user.pos;
		} else {

		throwPath = new Ballistica(user.pos, dst, Ballistica.PROJECTILE);
		return throwPath.collisionPos;
		}
	}
	
	@Override
	protected void onThrow(int cell) {
		if (Dungeon.bossLevel() && (InfernalBastionBossLevel.boss.phase == 0 || InfernalBastionBossLevel.boss.phase == 3 || InfernalBastionBossLevel.boss.phase == 3.1f)) {
			new StoneOfBlink().collect();
		} else {

			if (Actor.findChar(cell) != null && throwPath.dist >= 1) {
				cell = throwPath.path.get(throwPath.dist - 1);
			}
			throwPath = null;
			super.onThrow(cell);
		}
	}
	
	@Override
	protected void activate(int cell) {
		ScrollOfTeleportation.teleportToLocation(curUser, cell);
	}
}
