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


import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Actor;
import com.elementalpixel.elementalpixeldungeon.actors.Char;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Talent;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.Mob;
import com.elementalpixel.elementalpixeldungeon.effects.Splash;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.ui.BuffIndicator;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import static com.watabou.utils.Random.NormalFloat;

public class Evasion extends FlavourBuff {

    {
        type = buffType.POSITIVE;
        announced = true;
    }


    public static int counter = 0;
    public static final float DURATION = 25f * (float)Dungeon.hero.pointsInTalent(Talent.FLUID_MOVES);
    protected float level;

    public static int def = Hero.defenseSkill;

    public float level(){
        return level;
    }

    private static final String LEVEL	= "level";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEVEL, level );

    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        level = bundle.getFloat( LEVEL );
    }

    public void set( float level ) {
        this.level = Math.max(this.level, level);
    }

    @Override
    public int icon() {
        return BuffIndicator.BLEEDING;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }

    @Override
    public boolean act() {
        if (counter <= DURATION) {

            level = NormalFloat(level / 2f, level);

            Dungeon.hero.defenseSkill = Hero.INFINITE_EVASION;


            spend( TICK );
            counter++;


        } else {

            detach();

        }

        return true;
    }

    @Override
    public String heroMessage() {
        return Messages.get(this, "heromsg");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.hero.defenseSkill = def;
    }
}
