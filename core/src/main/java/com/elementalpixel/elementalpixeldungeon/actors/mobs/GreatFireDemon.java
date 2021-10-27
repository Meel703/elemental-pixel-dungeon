package com.elementalpixel.elementalpixeldungeon.actors.mobs;

import static com.elementalpixel.elementalpixeldungeon.levels.InfernalBastionBossLevel.arena;
import static com.elementalpixel.elementalpixeldungeon.levels.InfernalBastionBossLevel.boss;
import static com.elementalpixel.elementalpixeldungeon.levels.Level.set;
import static com.elementalpixel.elementalpixeldungeon.levels.Level.width;

import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Actor;
import com.elementalpixel.elementalpixeldungeon.actors.Char;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Blob;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.ConfusionGas;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.InfernalFire;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.ToxicGas;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Barrier;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Buff;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Burning;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Cripple;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.InfernalFlame;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Roots;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Sleep;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Slow;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Terror;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Vertigo;
import com.elementalpixel.elementalpixeldungeon.effects.TargetedCell;
import com.elementalpixel.elementalpixeldungeon.items.artifacts.DriedRose;
import com.elementalpixel.elementalpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.elementalpixel.elementalpixeldungeon.levels.Terrain;
import com.elementalpixel.elementalpixeldungeon.mechanics.Ballistica;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.sprites.CharSprite;
import com.elementalpixel.elementalpixeldungeon.sprites.GreatFireDemonSprite;
import com.elementalpixel.elementalpixeldungeon.ui.BossHealthBar;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class GreatFireDemon extends Mob {

    {
        spriteClass = GreatFireDemonSprite.class;

        HP = HT = 1400;
        EXP = 30;
        defenseSkill = 25;
        viewDistance = 1000;

        state = HUNTING;

        properties.add(Property.BOSS);
        properties.add(Property.DEMONIC);
        properties.add(Property.FIERY);
    }

    public static int counter = 0;
    public static int ability1Cooldown = 1;
    public static int ability2Cooldown = 0;

    private static boolean slowerCooldown = true;
    private static boolean fasterCooldown = true;

    private static boolean canTeleport = true;
    private static boolean transformed = false;
    public static boolean killed = false;

    private ArrayList<Integer> targetedCells = new ArrayList<>();

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 30);
    }

    @Override
    public int attackSkill(Char target) {
        return 26;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(5, 15);
    }

    public static float phase = 0;

    private static final String PHASE = "phase";
    private static final String HEALTH = "hp";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PHASE, phase);
        bundle.put(HEALTH, this.HP);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        phase = bundle.getInt(PHASE);
        HP = bundle.getInt(HEALTH);
    }

    @Override
    protected boolean act() {
        if (phase == 0) {
            baseSpeed = 10f;

            viewDistance = 1000;
            Dungeon.hero.viewDistance = 1000;
            Dungeon.level.viewDistance = 1000;

            if (Dungeon.level.adjacent( pos, Dungeon.hero.pos )) {
                sprite.attack(enemy.pos);
            }
        }

        else if (phase == 1) {
            baseSpeed = 1.75f;

            GameScene.updateMap();
            Dungeon.observe();

            if (Dungeon.level.adjacent( pos, Dungeon.hero.pos )) {
                sprite.attack(enemy.pos);
                spend(0.5f);
            }
        }

        else if (phase == 2) {
            Point c = arena.center();
            ScrollOfTeleportation.teleportToLocation(this, c.x + (c.y + 11) * width());
            ScrollOfTeleportation.teleportToLocation(Dungeon.hero, c.x + (c.y - 11) * width());

            GLog.w("\nThe Great Fire Demon has teleported!");

            spend(1f);

            GameScene.updateMap();
            Dungeon.observe();

            phase = 3;
        }

        else if (phase == 3) {

            Point c = arena.center();
            properties.add(Property.IMMOVABLE);
            fieldOfView[Dungeon.hero.pos] = true;

            if (pos != c.x + (c.y + 11) * width() && canTeleport) {
                moveSprite(pos, c.x + (c.y + 11) * width());
                move(c.x + (c.y + 11) * width());
            }

            if (Dungeon.level.adjacent( pos, Dungeon.hero.pos )) {
                sprite.attack(enemy.pos);
            }

            if (Dungeon.level.distance(pos, Dungeon.hero.pos) >= 15 && new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos) {
                return doAttack( Dungeon.hero );
            }

            if (Dungeon.level.distance(pos, enemy.pos) <= 3 && canTeleport) {
                ScrollOfTeleportation.teleportToLocation(this, c.x + (c.y - 11) * width());

                GLog.w("\nThe Great Fire Demon has teleported again!");

                canTeleport = false;
                phase = 3.1f;
            }
        }

        else if (phase == 3.1f) {
            Point c = arena.center();
            properties.add(Property.IMMOVABLE);

            if (pos != c.x + (c.y - 11) * width()) {
                moveSprite(pos, c.x + (c.y - 11) * width());
                move(c.x + (c.y - 11) * width());
            }

            if (Dungeon.level.distance(pos, Dungeon.hero.pos) >= 15 && new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos) {
                return doAttack( Dungeon.hero );
            }
        }

        else if (phase == 4) {
            Point c = arena.center();
            int a = Random.Int(2);

            if (a == 1) {
                ScrollOfTeleportation.teleportToLocation(this, (c.x - 7) + (c.y - 4) * width());
            } else {
                ScrollOfTeleportation.teleportToLocation(this, (c.x + 7) + (c.y - 4) * width());
            }

            Camera.main.shake(10, 3.08f);
            phase = 5;
        }

        if (phase == 5) {
            Camera.main.shake(10, 3.08f);
            properties.add(Property.IMMOVABLE);
            ScrollOfTeleportation.teleportToLocation(this, Dungeon.level.pointToCell(arena.center()));

            Point c = arena.center();

            if (boss.phase == 5 && !transformed) {
                transformed = true;
                GameScene.updateMap();

                // **************
                // REMOVE PILLARS
                // **************
                for (int i2 = -1; i2 < 5; i2++) {
                    for (int i = -8; i < -5; i++) {
                        Dungeon.level.discover((c.x + i) + (c.y - (8 - i2)) * width());
                        set((c.x + i) + (c.y - (8 - i2)) * width(), Terrain.EMPTY);
                        GameScene.updateMap((c.x + i) + (c.y - (8 - i2)) * width());
                        GameScene.updateFog((c.x + i) + (c.y - (8 - i2)) * width(), i2);
                    }
                }

                for (int i2 = -1; i2 < 5; i2++) {
                    for (int i = -8; i < -5; i++) {
                        set((c.x - i) + (c.y - (8 - i2)) * width(), Terrain.EMPTY);
                        GameScene.updateMap((c.x - i) + (c.y - (8 - i2)) * width());
                        GameScene.updateFog((c.x - i) + (c.y - (8 - i2)) * width(), i2);
                    }
                }

                for (int i2 = -1; i2 < 5; i2++) {
                    for (int i = -8; i < -5; i++) {
                        set((c.x + i) + (c.y - (-8 + i2)) * width(), Terrain.EMPTY);
                        GameScene.updateMap((c.x + i) + (c.y - (-8 + i2)) * width());
                        GameScene.updateFog((c.x + i) + (c.y - (-8 + i2)) * width(), i2);
                    }
                }

                for (int i2 = -1; i2 < 5; i2++) {
                    for (int i = -8; i < -5; i++) {
                        set((c.x - i) + (c.y - (-8 + i2)) * width(), Terrain.EMPTY);
                        GameScene.updateMap((c.x - i) + (c.y - (-8 + i2)) * width());
                        GameScene.updateFog((c.x - i) + (c.y - (-8 + i2)) * width(), i2);
                    }
                }

                GameScene.updateFog();
                Dungeon.observe();

                // ************
                // SPAWN DEBRIS - The map is separated into 4 sectors (corners), each corner gets 2 random shapes of walls (4 shapes are possible for each sector), so there is 16 shapes in total. Each sector also gets some grass and embers
                // ************
                // Note that this part of code is kinda long

                int a;
                int b;

                // ******************************************
                // SPAWN WALLS IN TOP-LEFT CORNER OF THE MAP
                // ******************************************

                for (int i = 0; i < 2;i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    set((c.x - a) + (c.y - b) * width(), Terrain.WALL);
                    GameScene.updateMap((c.x - a) + (c.y - b) * width());

                    switch (Random.Int(5)) {
                        default:
                        case 1:
                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x - (a - i2)) + (c.y - (b + 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - (a - i2)) + (c.y - (b + 1)) * width());
                            }
                            Dungeon.observe();
                            break;

                        case 2:
                            set((c.x - (a + 1)) + (c.y - b) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x - (a + 1)) + (c.y - b) * width());

                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x - (a + i2)) + (c.y - (b + 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - (a + i2)) + (c.y - (b + 1)) * width());
                            }

                            set((c.x - a) + (c.y - (b + 2)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x - a) + (c.y - (b + 2)) * width());

                            Dungeon.observe();
                            break;

                        case 3:
                            set((c.x - a) + (c.y - (b - 1)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x - a) + (c.y - (b + 2)) * width());

                            set((c.x - (a - 1)) + (c.y - (b - 1)) * width(), Terrain.WALL);
                            set((c.x - (a + 1)) + (c.y - (b + 1)) * width(), Terrain.WALL);

                            GameScene.updateMap((c.x - (a - 1)) + (c.y - (b - 1)) * width());
                            GameScene.updateMap((c.x - (a + 1)) + (c.y - (b + 1)) * width());

                            Dungeon.observe();
                            break;

                        case 4:
                            set((c.x - a) + (c.y - (b - 1)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x - a) + (c.y - (b - 1)) * width());

                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x - ((a - 1) + i2)) + (c.y - (b + 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - ((a - 1) + i2)) + (c.y - (b + 1)) * width());
                            }

                            Dungeon.observe();
                            break;
                    }
                }

                // **************************************************
                // SPAWN FURROWED GRASS IN TOP-LEFT CORNER OF THE MAP
                // **************************************************

                for (int i = 0; i < Random.Int(10, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x - a) + (c.y - b) * width()] && !Dungeon.level.water[(c.x - a) + (c.y - b) * width()]) {
                        set((c.x - a) + (c.y - b) * width(), Terrain.FURROWED_GRASS);
                        GameScene.updateMap((c.x - a) + (c.y - b) * width());
                    }

                    Dungeon.observe();
                }

                // ******************************************
                // SPAWN EMBERS IN TOP-LEFT CORNER OF THE MAP
                // ******************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x - a) + (c.y - b) * width()] && !Dungeon.level.water[(c.x - a) + (c.y - b) * width()]) {
                        set((c.x - a) + (c.y - b) * width(), Terrain.EMBERS);
                        GameScene.updateMap((c.x - a) + (c.y - b) * width());
                    }

                    Dungeon.observe();
                }

                // *****************************************
                // SPAWN GRASS IN TOP-LEFT CORNER OF THE MAP
                // *****************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x - a) + (c.y - b) * width()] && !Dungeon.level.water[(c.x - a) + (c.y - b) * width()]) {
                        set((c.x - a) + (c.y - b) * width(), Terrain.GRASS);
                        GameScene.updateMap((c.x - a) + (c.y - b) * width());
                    }

                    Dungeon.observe();
                }

                // *******************************************
                // SPAWN WALLS IN TOP-RIGHT CORNER OF THE MAP
                // *******************************************

                for (int i = 0; i < 2;i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    set((c.x + a) + (c.y - b) * width(), Terrain.WALL);
                    GameScene.updateMap((c.x + a) + (c.y - b) * width());

                    switch (Random.Int(5)) {
                        default:
                        case 1:
                            set((c.x + (a - 1)) + (c.y - b) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x + (a - 1)) + (c.y - b) * width());

                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x + a) + (c.y - ((b - 1) + i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + a) + (c.y - ((b - 1) + i2)) * width());
                            }

                            Dungeon.observe();
                            break;

                        case 2:
                            set((c.x + (a + 1)) + (c.y - b) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x + (a + 1)) + (c.y - b) * width());

                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + (a + i2)) + (c.y - (b + 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + (a + i2)) + (c.y - (b + 1)) * width());
                            }

                            set((c.x + a) + (c.y - (b - 1)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x + a) + (c.y - (b - 1)) * width());

                            set((c.x + (a - 1)) + (c.y - b) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x + (a - 1)) + (c.y - b) * width());

                            Dungeon.observe();
                            break;

                        case 3:
                            for (int i2 = 0; i2 <2; i2++) {
                                set((c.x + (a - 1)) + (c.y - (b - i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + (a - 1)) + (c.y - (b - i2)) * width());
                            }

                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + (a + 1)) + (c.y - (b + i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + (a + 1)) + (c.y - (b + i2)) * width());
                            }

                            Dungeon.observe();
                            break;

                        case 4:
                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + ((a - 2) + i2)) + (c.y - b) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + ((a - 2) + i2)) + (c.y - b) * width());
                            }

                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + a) + (c.y - ((b + 1) + i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + a) + (c.y - ((b + 1) + i2)) * width());
                            }

                            Dungeon.observe();
                            break;
                    }
                }

                // ***************************************************
                // SPAWN FURROWED GRASS IN TOP-RIGHT CORNER OF THE MAP
                // ***************************************************

                for (int i = 0; i < Random.Int(10, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x + a) + (c.y - b) * width()] && !Dungeon.level.water[(c.x + a) + (c.y - b) * width()]) {
                        set((c.x + a) + (c.y - b) * width(), Terrain.FURROWED_GRASS);
                        GameScene.updateMap((c.x + a) + (c.y - b) * width());
                    }

                    Dungeon.observe();
                }

                // *******************************************
                // SPAWN EMBERS IN TOP-RIGHT CORNER OF THE MAP
                // *******************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x + a) + (c.y - b) * width()] && !Dungeon.level.water[(c.x + a) + (c.y - b) * width()]) {
                        set((c.x + a) + (c.y - b) * width(), Terrain.EMBERS);
                        GameScene.updateMap((c.x + a) + (c.y - b) * width());
                    }

                    Dungeon.observe();
                }

                // ******************************************
                // SPAWN GRASS IN TOP-RIGHT CORNER OF THE MAP
                // ******************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x + a) + (c.y - b) * width()] && !Dungeon.level.water[(c.x + a) + (c.y - b) * width()]) {
                        set((c.x + a) + (c.y - b) * width(), Terrain.GRASS);
                        GameScene.updateMap((c.x + a) + (c.y - b) * width());
                    }

                    Dungeon.observe();
                }

                // *********************************************
                // SPAWN WALLS IN BOTTOM-LEFT CORNER OF THE MAP
                // *********************************************

                for (int i = 0; i < 2;i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    set((c.x - a) + (c.y + b) * width(), Terrain.WALL);
                    GameScene.updateMap((c.x - a) + (c.y + b) * width());

                    switch (Random.Int(5)) {
                        default:
                        case 1:
                            set((c.x - (a - 1)) + (c.y + (b - 1)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x - (a - 1)) + (c.y + (b - 1)) * width());

                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x - (a + i2)) + (c.y + (b + 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - (a + i2)) + (c.y + (b + 1)) * width());
                            }

                            Dungeon.observe();
                            break;

                        case 2:
                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x - ((a - 1) + i2)) + (c.y + (b + 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - (a + i2)) + (c.y + (b + 1)) * width());
                            }

                            Dungeon.observe();
                            break;

                        case 3:
                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x - (a + 1)) + (c.y + (b - i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - (a + 1)) + (c.y + (b - i2)) * width());
                            }

                            set((c.x - (a + 2)) + (c.y + (b - 2)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x - (a + 2)) + (c.y + (b - 2)) * width());

                            Dungeon.observe();
                            break;

                        case 4:
                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x - (a - i2)) + (c.y + ((b + 2) - i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x - (a - i2)) + (c.y + ((b + 2) - i2)) * width());
                            }

                            Dungeon.observe();
                            break;
                    }
                }

                // *****************************************************
                // SPAWN FURROWED GRASS IN BOTTOM-LEFT CORNER OF THE MAP
                // *****************************************************

                for (int i = 0; i < Random.Int(10, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x - a) + (c.y + b) * width()] && !Dungeon.level.water[(c.x - a) + (c.y + b) * width()]) {
                        set((c.x - a) + (c.y + b) * width(), Terrain.FURROWED_GRASS);
                        GameScene.updateMap((c.x - a) + (c.y + b) * width());
                    }

                    Dungeon.observe();
                }

                // *********************************************
                // SPAWN EMBERS IN BOTTOM-LEFT CORNER OF THE MAP
                // *********************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x - a) + (c.y + b) * width()] && !Dungeon.level.water[(c.x - a) + (c.y + b) * width()]) {
                        set((c.x - a) + (c.y + b) * width(), Terrain.EMBERS);
                        GameScene.updateMap((c.x - a) + (c.y + b) * width());
                    }

                    Dungeon.observe();
                }

                // ********************************************
                // SPAWN GRASS IN BOTTOM-LEFT CORNER OF THE MAP
                // ********************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x - a) + (c.y + b) * width()] && !Dungeon.level.water[(c.x - a) + (c.y + b) * width()]) {
                        set((c.x - a) + (c.y + b) * width(), Terrain.GRASS);
                        GameScene.updateMap((c.x - a) + (c.y + b) * width());
                    }

                    Dungeon.observe();
                }

                // *********************************************
                // SPAWN WALLS IN BOTTOM-RIGHT CORNER OF THE MAP
                // *********************************************

                for (int i = 0; i < 2;i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    set((c.x + a) + (c.y + b) * width(), Terrain.WALL);
                    GameScene.updateMap((c.x + a) + (c.y + b) * width());

                    switch (Random.Int(5)) {
                        default:
                        case 1:
                            for (int i2 = 0; i2 < 3; i2++) {
                                set((c.x + a) + (c.y + ((b + 1) - i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + a) + (c.y + ((b + 1) - i2)) * width());
                            }

                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + ((a + 2) + i2)) + (c.y + b) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + ((a + 2) + i2)) + (c.y + b) * width());
                            }

                            Dungeon.observe();
                            break;

                        case 2:
                            for (int i2 = 0; i2 < 4; i2++) {
                                set((c.x + (a)) + (c.y + ((b + 1) - i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + (a)) + (c.y + ((b + 1) - i2)) * width());
                            }

                            Dungeon.observe();
                            break;
                        case 3:
                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + ((a - 1) + i2)) + (c.y + ((b + 1) + i2)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + ((a - 1) + i2)) + (c.y + ((b + 1) + i2)) * width());
                            }

                            set((c.x + (a + 1)) + (c.y + (b + 1)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x + (a + 1)) + (c.y + (b + 1)) * width());

                            Dungeon.observe();
                            break;

                        case 4:
                            for (int i2 = 0; i2 < 2; i2++) {
                                set((c.x + (a - i2)) + (c.y + (b - 1)) * width(), Terrain.WALL);
                                GameScene.updateMap((c.x + (a - i2)) + (c.y + (b - 1)) * width());
                            }

                            set((c.x + (a - 1)) + (c.y + (b + 1)) * width(), Terrain.WALL);
                            GameScene.updateMap((c.x + (a - 1)) + (c.y + (b + 1)) * width());

                            Dungeon.observe();
                            break;
                    }
                }

                // *****************************************************
                // SPAWN FURROWED GRASS IN BOTTOM-LEFT CORNER OF THE MAP
                // *****************************************************

                for (int i = 0; i < Random.Int(10, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x + a) + (c.y + b) * width()] && !Dungeon.level.water[(c.x + a) + (c.y + b) * width()]) {
                        set((c.x + a) + (c.y + b) * width(), Terrain.FURROWED_GRASS);
                        GameScene.updateMap((c.x + a) + (c.y + b) * width());
                    }

                    Dungeon.observe();
                }

                // *********************************************
                // SPAWN EMBERS IN BOTTOM-LEFT CORNER OF THE MAP
                // *********************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x + a) + (c.y + b) * width()] && !Dungeon.level.water[(c.x + a) + (c.y + b) * width()]) {
                        set((c.x + a) + (c.y + b) * width(), Terrain.EMBERS);
                        GameScene.updateMap((c.x + a) + (c.y + b) * width());
                    }

                    Dungeon.observe();
                }

                // ********************************************
                // SPAWN GRASS IN BOTTOM-LEFT CORNER OF THE MAP
                // ********************************************

                for (int i = 0; i < Random.Int(7, 21); i++) {
                    a = Random.Int(13);
                    b = Random.Int(13);

                    if (!Dungeon.level.solid[(c.x + a) + (c.y + b) * width()] && !Dungeon.level.water[(c.x + a) + (c.y + b) * width()]) {
                        set((c.x + a) + (c.y + b) * width(), Terrain.GRASS);
                        GameScene.updateMap((c.x + a) + (c.y + b) * width());
                    }

                    Dungeon.observe();
                }

                Dungeon.observe();

                if (Dungeon.level.map[Dungeon.hero.pos] == Terrain.WALL) {
                    set(Dungeon.hero.pos, Terrain.EMPTY);
                    GameScene.updateMap(Dungeon.hero.pos);
                }

                if (Dungeon.level.map[boss.pos] == Terrain.WALL) {
                    set(boss.pos, Terrain.EMPTY);
                    GameScene.updateMap(boss.pos);
                }

                GameScene.updateMap();

                boss.phase = 6;
            }
        }

        if (phase == 6) {
            state = FLEEING;
            baseSpeed = 1f;

            boolean terrainAffected = false;

            if (!Dungeon.hero.rooted) {
                for (int i : targetedCells) {
                    Ballistica b = new Ballistica(pos, i, Ballistica.STOP_SOLID);

                    for (int p : b.path) {
                        if (!Dungeon.level.solid[p]) {
                            GameScene.add(Blob.seed(p, 4, InfernalFire.class));
                        }
                    }
                }
                if (terrainAffected) {
                    Dungeon.observe();
                }
                targetedCells.clear();
            }

            if (ability2Cooldown % 3 == 0){

                int beams = 1 + Math.round(Dungeon.level.distance(pos, enemy.pos) * 0.1f);
                HashSet<Integer> affectedCells = new HashSet<>();
                for (int i = 0; i < beams; i++){

                    int targetPos = Dungeon.hero.pos;
                    if (i != 0){
                        do {
                            targetPos = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                        } while (Dungeon.level.trueDistance(pos, Dungeon.hero.pos)
                                > Dungeon.level.trueDistance(pos, targetPos));
                    }
                    targetedCells.add(targetPos);
                    Ballistica b = new Ballistica(pos, targetPos, Ballistica.STOP_SOLID);
                    affectedCells.addAll(b.path);
                }

                //remove one beam if multiple shots would cause every cell next to the hero to be targeted
                boolean allAdjTargeted = true;
                for (int i : PathFinder.NEIGHBOURS9){
                    if (!affectedCells.contains(Dungeon.hero.pos + i) && Dungeon.level.passable[Dungeon.hero.pos + i]){
                        allAdjTargeted = false;
                        break;
                    }
                }
                if (allAdjTargeted){
                    targetedCells.remove(targetedCells.size()-1);
                }
                for (int i : targetedCells){
                    Ballistica b = new Ballistica(pos, i, Ballistica.STOP_SOLID);
                    for (int p : b.path){
                        sprite.parent.add(new TargetedCell(p, 0xFF0000));
                        affectedCells.add(p);
                    }
                }

                spend(GameMath.gate(TICK, Dungeon.hero.cooldown(), 3*TICK));
                Dungeon.hero.interrupt();

            } else {
                spend(TICK);
                ability2Cooldown++;
            }

        }

        return super.act();
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
            for (Char ch : Actor.chars()){
                if (ch instanceof DriedRose.GhostHero){
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
        }
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return (phase == 3 || phase == 3.1f) && new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;

    }

    @Override
    protected boolean doAttack( Char enemy ) {
        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else if(ability1Cooldown % 2 == 0) {
            ability2Cooldown++;

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }

        } else {
            ability1Cooldown++;

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                if (canTeleport) {
                    sprite.throwInfernalFire(enemy.pos + (3 * width()));
                } else {
                    sprite.throwInfernalFire(enemy.pos - (3 * width()));
                }
                return false;
            } else {
                if (canTeleport) {
                    throwInfernalFire();
                } else {
                    throwReversedInfernalFire();
                }
                return true;
            }
        }
    }

    public static class DarkBolt{}
    private void zap() {
        spend(1f);

         if (hit( this, enemy, false )) {
            int dmg = Random.NormalIntRange( 0, 18 );
            enemy.damage( dmg, new DarkBolt() );

            if (enemy == Dungeon.hero && !enemy.isAlive()) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "bolt_kill") );
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
        ability1Cooldown += 1;
    }

    public void onZapComplete() {
        zap();
        next();
    }

    public void onThrowComplete() {
        if (canTeleport) {
            throwInfernalFire();
        } else {
            throwReversedInfernalFire();
        }
        next();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive();
    }

    @Override
    public void die(Object cause) {
        GameScene.bossSlain();
        Dungeon.level.unseal();
        killed = true;
        super.die( cause );

        yell( Messages.get(this, "defeated") );

    }

    @Override
    public void damage(int dmg, Object src) {
        int preHP = HP;
        super.damage(dmg, src);
        if (phase == 0) {
            phase = 1;
        }

        if (phase == 1) {
            int dmgTaken = preHP - HP;
            if (HP >= 1100 && dmgTaken > 0) {
                Buff.affect(this, Barrier.class).setShield((int) (dmgTaken * 0.667f));
            }
        }

        if (HP <= 1100 && phase <= 2) {
            int dmgTaken = preHP - HP;
            if (dmgTaken == 0) {
                Buff.detach(this, Barrier.class);
            }
            phase = 2;
        }

        if (phase == 3 || phase == 3.1f) {
            int dmgTaken = preHP - HP;
            if (dmgTaken != 0) {
                properties.remove(Property.IMMOVABLE);
                phase = 4;
            }
        }
    }

    protected void throwInfernalFire() {
        spend(1.5f);

        for (int i : PathFinder.NEIGHBOURS9){
            if (!Dungeon.level.water[enemy.pos+i] && !Dungeon.level.solid[enemy.pos+i]){
                int vol = InfernalFire.volumeAt(enemy.pos+i, InfernalFire.class);
                if (vol < 4) {
                    if (!Dungeon.level.solid[enemy.pos + (2 * width())]) {
                        GameScene.add(Blob.seed(enemy.pos + (2 * width()), 4 - vol, InfernalFire.class));
                    }

                    for (int i2 = -2; i2 <= 2; i2++ ) {
                        if (!Dungeon.level.solid[enemy.pos + (3 * width()) + i2]) {
                            GameScene.add(Blob.seed(enemy.pos + (3 * width() + i2), 4 - vol, InfernalFire.class));
                        }
                    }

                    if (!Dungeon.level.solid[enemy.pos + (4 * width())]) {
                        GameScene.add(Blob.seed(enemy.pos + (4 * width()), 4 - vol, InfernalFire.class));
                    }

                }
            }
        }
        ability2Cooldown += 1;
    }

    protected void throwReversedInfernalFire() {
        spend(1.5f);

        for (int i : PathFinder.NEIGHBOURS9){
            if (!Dungeon.level.water[enemy.pos+i] && !Dungeon.level.solid[enemy.pos+i]){
                int vol = InfernalFire.volumeAt(enemy.pos+i, InfernalFire.class);
                if (vol < 4) {
                    if (!Dungeon.level.solid[enemy.pos - (2 * width())]) {
                        GameScene.add(Blob.seed(enemy.pos - (2 * width()), 4 - vol, InfernalFire.class));
                    }

                    for (int i2 = -2; i2 <= 2; i2++ ) {
                        if (!Dungeon.level.solid[enemy.pos - (3 * width()) + i2]) {
                            GameScene.add(Blob.seed(enemy.pos - (3 * width() + i2), 4 - vol, InfernalFire.class));
                        }
                    }

                    if (!Dungeon.level.solid[enemy.pos - (4 * width())]) {
                        GameScene.add(Blob.seed(enemy.pos - (4 * width()), 4 - vol, InfernalFire.class));
                    }

                }
            }
        }
        ability2Cooldown += 1;
    }

    {
        immunities.add(Sleep.class);
        immunities.add(InfernalFlame.class);
        immunities.add(Burning.class);
        immunities.add(ToxicGas.class);
        immunities.add(ConfusionGas.class);

        resistances.add(Terror.class);
        resistances.add(Vertigo.class);
        resistances.add(Cripple.class);
        resistances.add(Roots.class);
        resistances.add(Slow.class);
    }
}
