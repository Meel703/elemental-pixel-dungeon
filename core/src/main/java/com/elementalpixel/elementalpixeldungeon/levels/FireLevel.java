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
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Burning;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.npcs.Ghost;
import com.elementalpixel.elementalpixeldungeon.effects.Ripple;
import com.elementalpixel.elementalpixeldungeon.items.DewVial;
import com.elementalpixel.elementalpixeldungeon.levels.painters.HallsPainter;
import com.elementalpixel.elementalpixeldungeon.levels.painters.Painter;
import com.elementalpixel.elementalpixeldungeon.levels.painters.SewerPainter;
import com.elementalpixel.elementalpixeldungeon.levels.rooms.special.SpecialRoom;
import com.elementalpixel.elementalpixeldungeon.levels.traps.AlarmTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.BlazingTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.BurningTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.ChillingTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.ConfusionTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.CursingTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.ExplosiveTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.FlockTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.OozeTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.ShockingTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.SummoningTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.TeleportationTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.ToxicTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.WornDartTrap;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.tiles.DungeonTilemap;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.stream.Stream;

public class FireLevel extends RegularLevel {

	{

		viewDistance = 7;
		color1 = 0x48763c;
		color2 = 0x59994a;
	}
	
	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 5;
		//5 to 7, average 5.57
		return 3+ Random.chances(new float[]{4, 2, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 2;
		//1 to 3, average 1.8
		return 1+Random.chances(new float[]{4, 2});

	}
	
	@Override
	protected Painter painter() {
		return new HallsPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_HALLS;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_HALLS;
	}

	@Override
	protected Class<?>[] trapClasses() {
			return new Class[]{
					BurningTrap.class, BlazingTrap.class, ExplosiveTrap.class,
					FlockTrap.class, CursingTrap.class,
					TeleportationTrap.class };
}

	@Override
	protected float[] trapChances() {
		return new float[]{
						4, 4, 4,
						2, 2,
						1 };
	}
	
	@Override
	protected void createItems() {
		super.createItems();
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addHallsVisuals(this, visuals);
		return visuals;
	}

	public static void addHallsVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WATER) {
				group.add( new Stream( i ) );
			}
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(FireLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(FireLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(FireLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	private static class Stream extends Group {

		private int pos;

		private float delay;

		public Stream( int pos ) {
			super();

			this.pos = pos;

			delay = Random.Float( 2 );
		}

		@Override
		public void update() {

			if (!Dungeon.level.water[pos]){
				killAndErase();
				return;
			}

			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {

				super.update();

				if ((delay -= Game.elapsed) <= 0) {

					delay = Random.Float( 2 );

					PointF p = DungeonTilemap.tileToWorld( pos );
					((FireParticle)recycle( FireParticle.class )).reset(
							p.x + Random.Float( DungeonTilemap.SIZE ),
							p.y + Random.Float( DungeonTilemap.SIZE ) );
				}
			}
		}

		@Override
		public void draw() {
			Blending.setLightMode();
			super.draw();
			Blending.setNormalMode();
		}
	}

	public static class FireParticle extends PixelParticle.Shrinking {

		public FireParticle() {
			super();

			color( 0xEE7722 );
			lifespan = 1f;

			acc.set( 0, +80 );
		}

		public void reset( float x, float y ) {
			revive();

			this.x = x;
			this.y = y;

			left = lifespan;

			speed.set( 0, -40 );
			size = 4;
		}

		@Override
		public void update() {
			super.update();
			float p = left / lifespan;
			am = p > 0.8f ? (1 - p) * 5 : 1;
		}
	}
}
