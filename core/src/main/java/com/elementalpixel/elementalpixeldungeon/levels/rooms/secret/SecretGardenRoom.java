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

package com.elementalpixel.elementalpixeldungeon.levels.rooms.secret;


import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Foliage;
import com.elementalpixel.elementalpixeldungeon.items.wands.WandOfRegrowth;
import com.elementalpixel.elementalpixeldungeon.levels.Level;
import com.elementalpixel.elementalpixeldungeon.levels.Patch;
import com.elementalpixel.elementalpixeldungeon.levels.Terrain;
import com.elementalpixel.elementalpixeldungeon.levels.painters.Painter;
import com.elementalpixel.elementalpixeldungeon.plants.Starflower;
import com.watabou.utils.Random;

public class SecretGardenRoom extends SecretRoom {
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );

		if (Dungeon.depth == 36 || Dungeon.depth == 37 || Dungeon.depth == 38 || Dungeon.depth == 39) {
			Painter.fill( level, this, 1, Terrain.EMPTY );
		} else {
			Painter.fill(level, this, 1, Terrain.GRASS);
		}
		
		boolean[] grass = Patch.generate(width()-2, height()-2, 0.5f, 0, true);
		for (int i=top + 1; i < bottom; i++) {
			for (int j=left + 1; j < right; j++) {
				if (grass[xyToPatchCoords(j, i)] && Dungeon.depth != 36 && Dungeon.depth != 37 && Dungeon.depth != 38 && Dungeon.depth != 39) {
					level.map[i * level.width() + j] = Terrain.HIGH_GRASS;
				}
			}
		}
		
		entrance().set( Door.Type.HIDDEN );

		if (Dungeon.depth == 36 || Dungeon.depth == 37 || Dungeon.depth == 38 || Dungeon.depth == 39) {

		} else {

			level.plant(new Starflower.Seed(), plantPos(level));
			level.plant(new WandOfRegrowth.Seedpod.Seed(), plantPos(level));
			level.plant(new WandOfRegrowth.Dewcatcher.Seed(), plantPos(level));

			if (Random.Int(2) == 0) {
				level.plant(new WandOfRegrowth.Seedpod.Seed(), plantPos(level));
			} else {
				level.plant(new WandOfRegrowth.Dewcatcher.Seed(), plantPos(level));
			}
		}
		
		Foliage light = (Foliage)level.blobs.get( Foliage.class );
		if (light == null) {
			light = new Foliage();
		}
		for (int i=top + 1; i < bottom; i++) {
			for (int j=left + 1; j < right; j++) {
				light.seed( level, j + level.width() * i, 1 );
			}
		}
		level.blobs.put( Foliage.class, light );
	}
	
	private int plantPos( Level level ){
		int pos;
		do{
			pos = level.pointToCell(random());
		} while (level.plants.get(pos) != null);
		return pos;
	}
	
	protected int xyToPatchCoords(int x, int y){
		return (x-left-1) + ((y-top-1) * (width()-2));
	}
}
