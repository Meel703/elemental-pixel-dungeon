package com.elementalpixel.elementalpixeldungeon.items.weapon;

import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.Actor;
import com.elementalpixel.elementalpixeldungeon.actors.Char;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Blob;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Fire;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Freezing;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.ToxicGas;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Buff;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Corrosion;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Cripple;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Paralysis;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Poison;
import com.elementalpixel.elementalpixeldungeon.actors.buffs.Roots;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.effects.CellEmitter;
import com.elementalpixel.elementalpixeldungeon.effects.Splash;
import com.elementalpixel.elementalpixeldungeon.effects.particles.FlameParticle;
import com.elementalpixel.elementalpixeldungeon.items.rings.RingOfFuror;
import com.elementalpixel.elementalpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.elementalpixel.elementalpixeldungeon.levels.InfernalBastionBossLevel;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.CellSelector;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSpriteSheet;
import com.elementalpixel.elementalpixeldungeon.sprites.MissileSprite;
import com.elementalpixel.elementalpixeldungeon.ui.QuickSlotButton;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AlchemistFlask extends Weapon {

    public static final String AC_USE =      "USE";

    {
        image = ItemSpriteSheet.ELIXIR_TOXIC;

        defaultAction = AC_USE;
        usesTargeting = true;

        unique = true;
        bones = false;
    }

    public boolean special = false;
    public float specialBonusDamage = 0f;

    public int debuff;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_EQUIP);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_USE)) {

            curUser = hero;
            curItem = this;
            GameScene.selectCell( thrower );

        }
    }

    @Override
    public String info() {
        String info = desc();

        info += "\n\n" + Messages.get( AlchemistFlask.class, "stats",
                Math.round(augment.damageFactor(min())),
                Math.round(augment.damageFactor(max())),
                STRReq());

        if (STRReq() > Dungeon.hero.STR()) {
            info += " " + Messages.get(Weapon.class, "too_heavy");
        } else if (Dungeon.hero.STR() > STRReq()){
            info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
        }

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())){
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursed && isEquipped( Dungeon.hero )) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown){
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }



        return info;
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq(1, lvl); //tier 1
    }

    @Override
    public int min(int lvl) {
        return 1 + Dungeon.hero.lvl/5
                + (curseInfusionBonus ? 1 : 0);
    }

    @Override
    public int max(int lvl) {
        return 3 + (int)(Dungeon.hero.lvl/3f)
                + (curseInfusionBonus ? 2 : 0);
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockProjectile().targetingPos(user, dst);
    }

    private int targetPos;

    @Override
    public int damageRoll(Char owner) {
        int damage = augment.damageFactor(super.damageRoll(owner));
        if (owner instanceof Hero) {
            int exStr = ((Hero)owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange( 0, exStr );
            }
        }

        if (special){
            damage = Math.round(damage * (1f + specialBonusDamage));

            switch (augment){
                case NONE:
                    damage = Math.round(damage * 0.667f);
                    break;
                case SPEED:
                    damage = Math.round(damage * 0.5f);
                    break;
                case DAMAGE:
                    int distance = Dungeon.level.distance(owner.pos, targetPos) - 1;
                    float multiplier = Math.min(3f, 1.2f * (float)Math.pow(1.125f, distance));
                    damage = Math.round(damage * multiplier);
                    break;
            }
        }

        return damage;
    }

    @Override
    public float speedFactor(Char owner) {
        if (special){
            switch (augment){
                case NONE: default:
                    return 0f;
                case SPEED:
                    return 1f * RingOfFuror.attackDelayMultiplier(owner);
                case DAMAGE:
                    return 2f * RingOfFuror.attackDelayMultiplier(owner);
            }
        } else {
            return super.speedFactor(owner);
        }
    }


    @Override
    public int level() {
        return (Dungeon.hero == null ? 0 : Dungeon.hero.lvl/5) + (curseInfusionBonus ? 1 : 0);
    }

    @Override
    public int buffedLvl() {
        return level();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public AlchemistFlask.AchemistProjectile knockProjectile(){
        return new AchemistProjectile();
    }

    public class AchemistProjectile extends MissileWeapon {

        {
            debuff = Random.Int(1, 101);

            if (debuff <= 5) {
                image = ItemSpriteSheet.POTION_GOLDEN;
            } else if (debuff <= 11) {
                image = ItemSpriteSheet.POTION_CHARCOAL;
            } else if (debuff <= 19) {
                image = ItemSpriteSheet.POTION_MAGENTA;
            } else if (debuff <= 29) {
                image = ItemSpriteSheet.POTION_BISTRE;
            } else if (debuff <= 44) {
                image = ItemSpriteSheet.POTION_AMBER;
            } else if (debuff <= 59) {
                image = ItemSpriteSheet.POTION_JADE;
            } else if (debuff <= 74) {
                image = ItemSpriteSheet.POTION_TURQUOISE;
            } else if (debuff <= 94) {
                image = ItemSpriteSheet.POTION_IVORY;
            } else {
                image = ItemSpriteSheet.POTION_CRIMSON;
            }

            hitSound = Assets.Sounds.SHATTER;

        }

        @Override
        public int damageRoll(Char owner) {
            return AlchemistFlask.this.damageRoll(owner);
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return AlchemistFlask.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return AlchemistFlask.this.proc(attacker, defender, damage);
        }

        @Override
        public float speedFactor(Char user) {
            return AlchemistFlask.this.speedFactor(user);
        }

        @Override
        public float accuracyFactor(Char owner) {
            if (special && AlchemistFlask.this.augment == Augment.DAMAGE){
                return Float.POSITIVE_INFINITY;
            } else {
                return super.accuracyFactor(owner);
            }
        }

        @Override
        public int STRReq(int lvl) {
            return AlchemistFlask.this.STRReq(lvl);
        }


        @Override
        protected void onThrow( int cell ) {
            Char enemy = Actor.findChar( cell );
            if (enemy == null || enemy == curUser) {
                parent = null;
                Splash.at( cell, 0xCC99FFFF, 1 );
            } else {

                switch (debuff) {

                    //5% to paralysis
                    case 1: case 2: case 3: case 4: case 5:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            Buff.affect(enemy, Paralysis.class);
                        }
                        break;

                    //6% to root
                    case 6: case 7: case 8: case 9: case 10: case 11:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            Buff.affect(enemy, Roots.class);
                        }
                        break;

                    //8% to poison
                    case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            Buff.affect(enemy, Poison.class);
                        }
                        break;

                    //10% to Cripple
                    case 20: case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            Buff.affect(enemy, Cripple.class);
                        }
                        break;

                    //15% to Burning
                    case 30: case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39: case 40: case 41: case 42: case 43: case 44:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            GameScene.add(Blob.seed(targetPos, 2, Fire.class));
                            CellEmitter.get(targetPos).burst(FlameParticle.FACTORY, 2);
                        }
                        break;

                    //15% to toxic gas
                    case 45: case 46: case 47: case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: case 58: case 59:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            GameScene.add(Blob.seed(targetPos, 2, ToxicGas.class));
                            CellEmitter.get(targetPos).burst(FlameParticle.FACTORY, 2);
                        }
                        break;

                    //15% to frost
                    case 60: case 61: case 62: case 63: case 64: case 65: case 66: case 67: case 68: case 69: case 70: case 71: case 72: case 73: case 74:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            GameScene.add(Blob.seed(targetPos, 10, Freezing.class));
                        }
                        break;

                    //20% no special effect
                    default: case 75: case 76: case 77: case 78: case 79: case 80: case 81: case 82: case 83: case 84: case 85: case 86: case 87: case 88: case 89: case 90:
                    case 91: case 92: case 93: case 94:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {

                        }
                        break;

                    //6% to corrosion
                    case 95: case 96: case 97: case 98: case 99: case 100:
                        if (AlchemistFlask.curUser.shoot(enemy, this)) {
                            Buff.affect(enemy, Corrosion.class);
                        }
                        break;
                }

                if (special && AlchemistFlask.this.augment != Augment.SPEED) special = false;
            }
        }

        @Override
        public void throwSound() {

        }

        int flurryCount = -1;

        @Override
        public void cast(final Hero user, final int dst) {
            final int cell = throwPos( user, dst );
            AlchemistFlask.this.targetPos = cell;
            if (special && AlchemistFlask.this.augment == Augment.SPEED){
                if (flurryCount == -1) flurryCount = 3;

                final Char enemy = Actor.findChar( cell );

                if (enemy == null){
                    user.spendAndNext(castDelay(user, dst));
                    special = false;
                    flurryCount = -1;
                    return;
                }
                QuickSlotButton.target(enemy);

                final boolean last = flurryCount == 1;

                user.busy();

                throwSound();

                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                cell,
                                this,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        if (enemy.isAlive()) {
                                            curUser = user;
                                            onThrow(cell);
                                        }

                                        if (last) {
                                            user.spendAndNext(castDelay(user, dst));
                                            special = false;
                                            flurryCount = -1;
                                        }
                                    }
                                });

                user.sprite.zap(cell, new Callback() {
                    @Override
                    public void call() {
                        flurryCount--;
                        if (flurryCount > 0){
                            cast(user, dst);
                        }
                    }
                });

            } else {

                super.cast(user, dst);
            }
        }
    }

    private CellSelector.Listener thrower = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (Dungeon.depth == 35 && Dungeon.level.distance(Dungeon.hero.pos, InfernalBastionBossLevel.boss.pos) != 1 && (InfernalBastionBossLevel.boss.phase == 3 || InfernalBastionBossLevel.boss.phase == 3.1f) ) {
                    GLog.n("Great Fire Demon: You have to come closer and fight fair, kid");

                } else {

                    knockProjectile().cast(curUser, target);
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(AlchemistFlask.class, "prompt");
        }
    };
}