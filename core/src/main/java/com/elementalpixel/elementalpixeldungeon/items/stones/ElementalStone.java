package com.elementalpixel.elementalpixeldungeon.items.stones;

import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.items.Item;
import com.elementalpixel.elementalpixeldungeon.items.armor.Armor;
import com.elementalpixel.elementalpixeldungeon.items.weapon.Weapon;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSpriteSheet;
import com.elementalpixel.elementalpixeldungeon.utils.GLog;
import com.elementalpixel.elementalpixeldungeon.windows.WndArmorInfusion;
import com.elementalpixel.elementalpixeldungeon.windows.WndBag;
import com.elementalpixel.elementalpixeldungeon.windows.WndWeaponInfusion;

import java.util.ArrayList;

public class ElementalStone extends Item {

    protected WndBag.Mode mode = WndBag.Mode.ARMOR_WEAPON;
    protected static WndBag.Mode modeArmor = WndBag.Mode.ARMOR;
    protected static WndBag.Mode modeWeapon = WndBag.Mode.WEAPON;

    {
        image = ItemSpriteSheet.ELEMENTAL_STONE;

        stackable = true;
        bones = false;
    }

    public static final String AC_INFUSE = "infuse";
    public static final String AC_TRANSFER = "transfer";

    public static String title;

    public static int value = 0;

    public static Armor a2;
    public static Weapon w2;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_INFUSE);
        actions.add(AC_TRANSFER);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_INFUSE)) {
            GameScene.selectItem(itemSelector, mode, Messages.get(this, "select"));

        }

        if (action.equals(AC_TRANSFER)) {
            GameScene.selectItem(itemSelectorTransfer, mode, Messages.get(this, "select"));
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public static WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item infusion) {
            Hero hero = Dungeon.hero;
            curUser.spend(1);

            if (infusion instanceof Armor) {
                GameScene.show(new WndArmorInfusion(infusion));
            } else if (infusion instanceof Weapon) {
                GameScene.show(new WndWeaponInfusion(infusion));
            }
        }
    };

    public static WndBag.Listener armorSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item armor) {
            Armor a = (Armor)armor;

            try {
                for (int i = 0; i < value; i++) {
                    a.upgrade();
                    a.upgradeCounter++;
                }
            } catch (NullPointerException e) {
                a2.upgrade(value);
                a2.upgradeCounter += value;
                new ElementalStone().collect();
            }

            ((ElementalStone)curItem).detach(Dungeon.hero.belongings.backpack);
        }
    };

    public static WndBag.Listener weaponSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            Weapon w = (Weapon)item;

            try {
                for (int i = 0; i < value; i++) {
                    w.upgrade();
                    w.upgradeCounter++;
                }
            } catch (NullPointerException e) {
                w2.upgrade(value);
                w2.upgradeCounter += value;
                new ElementalStone().collect();
            }

            ((ElementalStone)curItem).detach(Dungeon.hero.belongings.backpack);
        }
    };

    public static WndBag.Listener itemSelectorTransfer = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            value = 0;
            Hero hero = Dungeon.hero;
            curUser.spend(1);

            //ARMOR
            if (item instanceof Armor) {
                Armor a = (Armor)item;
                value = a.upgradeCounter;

                if (a.upgradeCounter > 0 && a.level != 0 && curItem.isUpgraded(a)) {

                    if (a.upgradeCounter == 1) title = "transfer upgrade";
                    else title = "transfer upgrades";

                    GameScene.selectItem(armorSelector, modeArmor, title);

                    for (int i = 0;i < value; i++) {
                        a.degrade();
                        a.upgradeCounter--;
                    }

                    a2 = a;

                } else {
                    GLog.h("Please, select upgraded armor", ElementalStone.class);
                }

            //WEAPON
            } else if (item instanceof Weapon) {
                Weapon w = (Weapon)item;
                value = w.upgradeCounter;

                if (w.upgradeCounter > 0 && w.level != 0 && curItem.isUpgraded(w)) {

                    if (w.upgradeCounter == 1) title = "transfer upgrade";
                    else title = "transfer upgrades";

                    GameScene.selectItem(weaponSelector, modeWeapon, title);

                    for (int i = 0; i < value; i++) {
                        w.degrade();
                        w.upgradeCounter--;
                    }

                    w2 = w;
                } else {
                    GLog.h("Please, select upgraded weapon");
                }
            }
        }
    };
}
