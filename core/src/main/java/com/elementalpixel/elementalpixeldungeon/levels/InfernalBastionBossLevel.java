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

package com.elementalpixel.elementalpixeldungeon.levels;


import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.items.fragments.FireFragment;
import com.elementalpixel.elementalpixeldungeon.levels.painters.HallsPainter;
import com.elementalpixel.elementalpixeldungeon.levels.painters.Painter;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class InfernalBastionBossLevel extends Level {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	private static int WIDTH = 45;
	private static int HEIGHT = 144;

	private static final Rect entry = new Rect(9, 38, 20, 48);
	private static final Rect arena = new Rect(1, 11, 28, 38);
	private static final Rect end = new Rect(11, 0, 18, 11);

	private static final Rect pillar = new Rect(20, 16, 23, 19);
	private static final Rect pillar2 = new Rect(20, 30, 23, 33);

	private static final Rect pillar3 = new Rect(6, 16, 9, 19);
	private static final Rect pillar4 = new Rect(6, 30, 9, 33);

	private static final Rect water = new Rect(9, 19, 20, 30);
	private static final Rect water2 = new Rect(11, 21, 18, 28);

	private static final Rect empty = new Rect(10, 20, 19, 29);
	private static final Rect empty2 = new Rect(12, 22, 17, 27);

	private static final Rect pedestal = new Rect(14, 24, 15, 25);

	private static final Rect wall = new Rect(11, 0, 18, 1);

	private static final int bottomDoor = 7 + (arena.bottom-1)*15;
	private static final int topDoor = 7 + arena.top*15;


	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_HALLS;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_HALLS;
	}

	private static final String IMP_SHOP = "imp_shop";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
	}

	@Override
	protected boolean build() {

		setSize(WIDTH, HEIGHT);

		//entrance room
		Painter.fill(this, entry, Terrain.WALL);
		Painter.fill(this, entry, 2, Terrain.WATER);


		Point c = entry.center();


		for (int i = 1; i < 9; i++) {
			Painter.set(this, c.x, c.y - i, Terrain.EMPTY);
		}

		Painter.set(this, c.x, c.y - 5, Terrain.DOOR);

		entrance = c.x + (c.y+0) * width();
		Rect e = new Rect(6, 44, 7, 45);

		Painter.set(this, entrance, Terrain.ENTRANCE);

		//Arena
		Painter.fill(this, arena, 1, Terrain.EMPTY);

		c = arena.center();

		Painter.set(this, c.x, arena.top, Terrain.OPEN_DOOR);

		Painter.fill(this, pillar, Terrain.WALL);
		Painter.fill(this, pillar2, Terrain.WALL);

		Painter.fill(this, pillar3, Terrain.WALL);
		Painter.fill(this, pillar4, Terrain.WALL);

		Painter.fill(this, water, Terrain.WATER);
		Painter.fill(this, empty, Terrain.EMPTY);

		Painter.fill(this, water2, Terrain.WATER);
		Painter.fill(this, empty2, Terrain.EMPTY);

		Painter.fill(this, pedestal, Terrain.PEDESTAL);

		//exit
		Painter.fill(this, end, Terrain.EMPTY);
		Painter.set(this, c.x, arena.top - 8, Terrain.EXIT);
		Painter.set(this, c.x, arena.top - 2, Terrain.PEDESTAL);


		Painter.fill(this, wall, Terrain.WALL);
		exit = c.x + (arena.top-2)*width();


		new HallsPainter().paint(this, null);

		return true;
	}

	@Override
	protected void createMobs() {

	}

	@Override
	protected void createItems() {
		drop(new FireFragment(), exit);
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(CityLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(CityLevel.class, "sp_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals( ) {
		super.addVisuals();
		InfernalBastionLevel.addHallsVisuals(this, visuals);
		return visuals;
	}
}
