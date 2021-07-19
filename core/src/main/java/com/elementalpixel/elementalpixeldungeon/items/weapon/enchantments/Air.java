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

package com.elementalpixel.elementalpixeldungeon.items.weapon.enchantments;

import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Char;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Talent;
import com.elementalpixel.elementalpixeldungeon.items.weapon.Weapon;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSprite;

public class Air extends Weapon.Enchantment {

    private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF );
    public static int counter = 2;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

        if (Talent.ElementalSurge) {
            if (Dungeon.hero.pointsInTalent(Talent.ATTUNED_MEAL) == 1) {
                weapon.DLY = 0.65f;
                Talent.ElementalSurge = false;
            } else {
                weapon.DLY = 0.65f;
                counter--;

                if (counter == 0) {
                    Talent.ElementalSurge = false;
                }

            }
        } else {
            weapon.DLY = 0.75f;
        }

        if (Dungeon.hero.hasTalent(Talent.OPPRESSIVE_OFFENCE)) {
            return damage + Math.round(damage * (0.1f * Dungeon.hero.pointsInTalent(Talent.OPPRESSIVE_OFFENCE)) );
        } else {
            return damage;
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }

}
