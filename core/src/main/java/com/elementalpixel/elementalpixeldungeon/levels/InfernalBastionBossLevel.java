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
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Actor;
import com.elementalpixel.elementalpixeldungeon.actors.Char;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.GreatFireDemon;
import com.elementalpixel.elementalpixeldungeon.items.fragments.FireFragment;
import com.elementalpixel.elementalpixeldungeon.items.potions.PotionOfHealing;
import com.elementalpixel.elementalpixeldungeon.levels.painters.HallsPainter;
import com.elementalpixel.elementalpixeldungeon.levels.painters.Painter;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class InfernalBastionBossLevel extends Level {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;

		viewDistance = 12;
	}

	private static int WIDTH = 45;
	private static int HEIGHT = 144;

	private static final Rect entry = new Rect(9, 38, 20, 48);
	public static final Rect arena = new Rect(1, 11, 28, 38);
	private static final Rect end = new Rect(11, 0, 18, 11);

	protected static final Rect pillar = new Rect(20, 15, 23, 18);
	private static final Rect pillar2 = new Rect(20, 31, 23, 34);

	public static final Rect pillar3 = new Rect(6, 15, 9, 18);
	private static final Rect pillar4 = new Rect(6, 31, 9, 34);

	private static final Rect empty3 = new Rect(6, 16, 9, 19);
	private static final Rect empty4 = new Rect(6, 30, 9, 33);

	private static final Rect empty5 = new Rect(20, 16, 23, 19);
	private static final Rect empty6 = new Rect(20, 30, 23, 33);

	public static final Rect chasmDuringBossFight = new Rect(2, 16, 27, 17);
	private static final Rect chasmDuringBossFight2 = new Rect();
	private static final Rect chasmDuringBossFight3 = new Rect();

	private static final Rect water = new Rect(9, 19, 20, 30);
	private static final Rect water2 = new Rect(11, 21, 18, 28);

	private static final Rect empty = new Rect(10, 20, 19, 29);
	private static final Rect empty2 = new Rect(12, 22, 17, 27);

	private static final Rect pedestal = new Rect(14, 24, 15, 25);

	private static final Rect wall = new Rect(11, 0, 18, 1);

	protected final Rect debris = new Rect();

	private static int bottomDoor;
	public static int topDoor;
	public static int door;

	public static int bossSpawnPoint;

	public static GreatFireDemon boss;
	public static boolean canSpawn = true;


	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_HALLS;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_HALLS;
	}

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


		entrance = c.x + c.y * width();
		door = c.x + (c.y - 5)  * width();

		Painter.set(this, door, Terrain.DOOR);
		Painter.set(this, entrance, Terrain.ENTRANCE);

		//Arena
		Painter.fill(this, arena, 1, Terrain.EMPTY);

		c = arena.center();

		Painter.set(this, c.x, arena.top, Terrain.OPEN_DOOR);

		Painter.fill(this, pillar, Terrain.WALL);
		Painter.fill(this, pillar2, Terrain.WALL);

		Painter.fill(this, pillar3, Terrain.WALL);
		Painter.fill(this, pillar4, Terrain.WALL);

		/*Painter.fill(this, pillar.left + 1, pillar.bottom - 2, 1, 1, Terrain.STATUE_SP);
		Painter.fill(this, pillar2.left + 1, pillar2.bottom - 2, 1, 1, Terrain.STATUE_SP);

		Painter.fill(this, pillar3.left + 1, pillar3.bottom - 2, 1, 1, Terrain.STATUE_SP);
		Painter.fill(this, pillar4.left + 1, pillar4.bottom - 2, 1, 1, Terrain.STATUE_SP);
*/

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
		bossSpawnPoint = c.x + (arena.top + 13) * width();

		topDoor = c.x + (arena.top) * width();
		bottomDoor = entrance;

		new HallsPainter().paint(this, null);

		return true;
	}

	@Override
	protected void createMobs() {

	}

	@Override
	protected void createItems() {
		drop(new PotionOfHealing(), exit);
		drop(new FireFragment(), exit);
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);

		Point c = arena.center();
		if (map[entrance] == Terrain.ENTRANCE && map[exit] != Terrain.EXIT
				&& Dungeon.level.distance(Dungeon.hero.pos, entrance) >= 30) {

			seal();
		}
	}

	@Override
	public void seal() {
		super.seal();
	}

	@Override
	public void unseal() {
		super.unseal();

		set(door, Terrain.DOOR);
		GameScene.updateMap(door);

		set(topDoor, Terrain.DOOR);
		GameScene.updateMap(topDoor);

		for (int i = 1; i < 4; i++) {
			for (int i2 = 0; i2 < 3; i2++) {
				set(((arena.top + 1) + i) + ((arena.top + 1) + i2) * width(), Terrain.EMPTY_SP);
				GameScene.updateMap(((arena.top + 1) + i) + ((arena.top + 1) + i2) * width());
			}
		}

		Dungeon.observe();
	}

	@Override
	public int randomRespawnCell(Char ch) {
		int pos = entrance;
		int cell;
		do {
			cell = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (!passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| Actor.findChar(cell) != null);
		return cell;

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
