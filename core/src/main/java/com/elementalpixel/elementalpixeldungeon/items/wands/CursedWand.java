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

package com.elementalpixel.elementalpixeldungeon.items.wands;


import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.Challenges;
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.ShatteredPixelDungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Actor;
import com.elementalpixel.elementalpixeldungeon.actors.Char;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Blob;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.ConfusionGas;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Fire;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.ParalyticGas;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Regrowth;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.ToxicGas;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Buff;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Burning;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Frost;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Recharging;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.GoldenMimic;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.Mimic;
import com.elementalpixel.elementalpixeldungeon.actors.mobs.npcs.Sheep;
import com.elementalpixel.elementalpixeldungeon.effects.CellEmitter;
import com.elementalpixel.elementalpixeldungeon.effects.Flare;
import com.elementalpixel.elementalpixeldungeon.effects.MagicMissile;
import com.elementalpixel.elementalpixeldungeon.effects.Speck;
import com.elementalpixel.elementalpixeldungeon.effects.SpellSprite;
import com.elementalpixel.elementalpixeldungeon.effects.particles.ShadowParticle;
import com.elementalpixel.elementalpixeldungeon.items.Generator;
import com.elementalpixel.elementalpixeldungeon.items.Item;
import com.elementalpixel.elementalpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.elementalpixel.elementalpixeldungeon.items.bombs.Bomb;
import com.elementalpixel.elementalpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.elementalpixel.elementalpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.elementalpixel.elementalpixeldungeon.levels.Terrain;
import com.elementalpixel.elementalpixeldungeon.levels.traps.CursingTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.ShockingTrap;
import com.elementalpixel.elementalpixeldungeon.levels.traps.SummoningTrap;
import com.elementalpixel.elementalpixeldungeon.mechanics.Ballistica;
import com.elementalpixel.elementalpixeldungeon.messages.Languages;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.plants.Plant;
import com.elementalpixel.elementalpixeldungeon.plants.Swiftthistle;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.scenes.InterlevelScene;
import com.elementalpixel.elementalpixeldungeon.ui.TargetHealthIndicator;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.elementalpixel.elementalpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;



//helper class to contain all the cursed wand zapping logic, so the main wand class doesn't get huge.
public class CursedWand {

	private static float COMMON_CHANCE = 0.6f;
	private static float UNCOMMON_CHANCE = 10.3f;
	private static float RARE_CHANCE = 0.09f;
	private static float VERY_RARE_CHANCE = 0.01f;

	public static void cursedZap(final Item origin, final Char user, final Ballistica bolt, final Callback afterZap){

		cursedFX(user, bolt, new Callback() {
			@Override
			public void call() {
				if (cursedEffect(origin, user, bolt.collisionPos)){
					if (afterZap != null) afterZap.call();
				}
			}
		});
	}

	public static boolean cursedEffect(final Item origin, final Char user, final Char target){
		return cursedEffect(origin, user, target.pos);
	}

	public static boolean cursedEffect(final Item origin, final Char user, final int targetPos){
		switch (Random.chances(new float[]{COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE})){
			case 0: default:
				return commonEffect(origin, user, targetPos);
			case 1:
				return uncommonEffect(origin, user, targetPos);
			case 2:
				return rareEffect(origin, user, targetPos);
			case 3:
				return veryRareEffect(origin, user, targetPos);
		}
	}

	private static boolean commonEffect(final Item origin, final Char user, final int targetPos){
		switch(Random.Int(4)){

			//anti-entropy
			case 0: default:
				Char target = Actor.findChar(targetPos);
				if (Random.Int(2) == 0) {
					if (target != null) Buff.affect(target, Burning.class).reignite(target);
					Buff.affect(user, Frost.class, Frost.DURATION);
				} else {
					Buff.affect(user, Burning.class).reignite(user);
					if (target != null) Buff.affect(target, Frost.class, Frost.DURATION);
				}
				return true;

			//spawns some regrowth
			case 1:
				GameScene.add( Blob.seed(targetPos, 30, Regrowth.class));
				return true;

			//random teleportation
			case 2:
				if(Random.Int(2) == 0) {
					if (user != null && !user.properties().contains(Char.Property.IMMOVABLE)) {
						ScrollOfTeleportation.teleportChar(user);
					} else {
						return cursedEffect(origin, user, targetPos);
					}
				} else {
					Char ch = Actor.findChar( targetPos );
					if (ch != null && !ch.properties().contains(Char.Property.IMMOVABLE)) {
						ScrollOfTeleportation.teleportChar(ch);
					} else {
						return cursedEffect(origin, user, targetPos);
					}
				}
				return true;

			//random gas at location
			case 3:
				Sample.INSTANCE.play( Assets.Sounds.GAS );
				switch (Random.Int(3)) {
					case 0: default:
						GameScene.add( Blob.seed( targetPos, 800, ConfusionGas.class ) );
						return true;
					case 1:
						GameScene.add( Blob.seed( targetPos, 500, ToxicGas.class ) );
						return true;
					case 2:
						GameScene.add( Blob.seed( targetPos, 200, ParalyticGas.class ) );
						return true;
				}
		}

	}

	private static boolean uncommonEffect(final Item origin, final Char user, final int targetPos){
		switch(Random.Int(4)){

			//Random plant
			case 0: default:
				int pos = targetPos;

				if (Dungeon.level.map[pos] != Terrain.ALCHEMY
						&& !Dungeon.level.pit[pos]
						&& Dungeon.level.traps.get(pos) == null
						&& !Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
					Dungeon.level.plant((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), pos);
				} else {
					return cursedEffect(origin, user, targetPos);
				}

				return true;

			//Health transfer
			case 1:
				final Char target = Actor.findChar( targetPos );
				if (target != null) {
					int damage = Dungeon.depth * 2;
					Char toHeal, toDamage;

					if (Random.Int(2) == 0){
						toHeal = user;
						toDamage = target;
					} else {
						toHeal = target;
						toDamage = user;
					}
					toHeal.HP = Math.min(toHeal.HT, toHeal.HP + damage);
					toHeal.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
					toDamage.damage(damage, origin == null ? toHeal : origin);
					toDamage.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);

					if (toDamage == Dungeon.hero){
						Sample.INSTANCE.play(Assets.Sounds.CURSED);
						if (!toDamage.isAlive()) {
							if (origin != null) {
								Dungeon.fail( origin.getClass() );
								GLog.n( Messages.get( CursedWand.class, "ondeath", origin.name() ) );
							} else {
								Dungeon.fail( toHeal.getClass() );
							}
						}
					} else {
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
					}
				} else {
					return cursedEffect(origin, user, targetPos);
				}
				return true;

			//Bomb explosion
			case 2:
				new Bomb().explode(targetPos);
				return true;

			//shock and recharge
			case 3:
				new ShockingTrap().set( user.pos ).activate();
				Buff.prolong(user, Recharging.class, Recharging.DURATION);
				ScrollOfRecharging.charge(user);
				SpellSprite.show(user, SpellSprite.CHARGE);
				return true;
		}

	}

	private static boolean rareEffect(final Item origin, final Char user, final int targetPos){
		switch(Random.Int(4)){

			//sheep transformation
			case 0: default:

				Char ch = Actor.findChar( targetPos );
				if (ch != null && !(ch instanceof Hero)
						&& !ch.properties().contains(Char.Property.BOSS)
						&& !ch.properties().contains(Char.Property.MINIBOSS)){
					Sheep sheep = new Sheep();
					sheep.lifespan = 10;
					sheep.pos = ch.pos;
					ch.destroy();
					ch.sprite.killAndErase();
					Dungeon.level.mobs.remove(ch);
					TargetHealthIndicator.instance.target(null);
					GameScene.add(sheep);
					CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
					Sample.INSTANCE.play(Assets.Sounds.PUFF);
					Sample.INSTANCE.play(Assets.Sounds.SHEEP);
				} else {
					return cursedEffect(origin, user, targetPos);
				}
				return true;

			//curses!
			case 1:
				if (user instanceof Hero) {
					CursingTrap.curse( (Hero) user );
				} else {
					return cursedEffect(origin, user, targetPos);
				}
				return true;

			//inter-level teleportation
			case 2:
				if (Dungeon.depth > 1 && !Dungeon.bossLevel() && user == Dungeon.hero) {

					//each depth has 1 more weight than the previous depth.
					float[] depths = new float[Dungeon.depth-1];
					for (int i = 1; i < Dungeon.depth; i++) depths[i-1] = i;
					int depth = 1+Random.chances(depths);

					Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
					if (buff != null) buff.detach();
					
					buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
					if (buff != null) buff.detach();

					InterlevelScene.mode = InterlevelScene.Mode.RETURN;
					InterlevelScene.returnDepth = depth;
					InterlevelScene.returnPos = -1;
					Game.switchScene(InterlevelScene.class);

				} else {
					ScrollOfTeleportation.teleportChar(user);

				}
				return true;

			//summon monsters
			case 3:
				new SummoningTrap().set( targetPos ).activate();
				return true;
		}
	}

	private static boolean veryRareEffect(final Item origin, final Char user, final int targetPos){
		switch(Random.Int(4)){

			//great forest fire!
			case 0: default:
				for (int i = 0; i < Dungeon.level.length(); i++){
					GameScene.add( Blob.seed(i, 15, Regrowth.class));
				}
				do {
					GameScene.add(Blob.seed(Dungeon.level.randomDestination(null), 10, Fire.class));
				} while (Random.Int(5) != 0);
				new Flare(8, 32).color(0xFFFF66, true).show(user.sprite, 2f);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				GLog.p(Messages.get(CursedWand.class, "grass"));
				GLog.w(Messages.get(CursedWand.class, "fire"));
				return true;

			//golden mimic
			case 1:

				Char ch = Actor.findChar(targetPos);
				int spawnCell = targetPos;
				if (ch != null){
					ArrayList<Integer> candidates = new ArrayList<Integer>();
					for (int n : PathFinder.NEIGHBOURS8) {
						int cell = targetPos + n;
						if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
							candidates.add( cell );
						}
					}
					if (!candidates.isEmpty()){
						spawnCell = Random.element(candidates);
					} else {
						return cursedEffect(origin, user, targetPos);
					}
				}

				Mimic mimic = Mimic.spawnAt(spawnCell, new ArrayList<Item>(), GoldenMimic.class);
				mimic.stopHiding();
				mimic.alignment = Char.Alignment.ENEMY;
				Item reward;
				do {
					reward = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
							Generator.Category.RING, Generator.Category.WAND));
				} while (reward.level() < 1);
				//play vfx/sfx manually as mimic isn't in the scene yet
				Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 0.85f);
				CellEmitter.get(mimic.pos).burst(Speck.factory(Speck.STAR), 10);
				mimic.items.clear();
				mimic.items.add(reward);
				GameScene.add(mimic);
				return true;

			//crashes the game, yes, really.
			case 2:
				
				try {
					Dungeon.saveAll();
					if(Messages.lang() != Languages.ENGLISH){
						//Don't bother doing this joke to none-english speakers, I doubt it would translate.
						return cursedEffect(origin, user, targetPos);
					} else {
						GameScene.show(
								new WndOptions("CURSED WAND ERROR", "this application will now self-destruct", "abort", "retry", "fail") {
									
									@Override
									protected void onSelect(int index) {
										Game.instance.finish();
									}
									
									@Override
									public void onBackPressed() {
										//do nothing
									}
								}
						);
						return false;
					}
				} catch(IOException e){
					ShatteredPixelDungeon.reportException(e);
					//maybe don't kill the game if the save failed.
					return cursedEffect(origin, user, targetPos);
				}

			//random transmogrification
			case 3:
				//skips this effect if there is no item to transmogrify
				if (origin == null || user != Dungeon.hero || !Dungeon.hero.belongings.contains(origin)){
					return cursedEffect(origin, user, targetPos);
				}
				origin.detach(Dungeon.hero.belongings.backpack);
				Item result;
				do {
					result = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
							Generator.Category.RING, Generator.Category.ARTIFACT));
				} while (result.cursed);
				if (result.isUpgradable()) result.upgrade();
				result.cursed = result.cursedKnown = true;
				if (origin instanceof Wand){
					GLog.w( Messages.get(CursedWand.class, "transmogrify_wand") );
				} else {
					GLog.w( Messages.get(CursedWand.class, "transmogrify_other") );
				}
				Dungeon.level.drop(result, user.pos).sprite.drop();
				return true;
		}
	}

	private static void cursedFX(final Char user, final Ballistica bolt, final Callback callback){
		MagicMissile.boltFromChar( user.sprite.parent,
				MagicMissile.RAINBOW,
				user.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

}
