package com.elementalpixel.elementalpixeldungeon.items.stones;

import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.items.Item;
import com.elementalpixel.elementalpixeldungeon.items.armor.Armor;
import com.elementalpixel.elementalpixeldungeon.items.weapon.Weapon;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSpriteSheet;
import com.elementalpixel.elementalpixeldungeon.windows.WndArmorInfusion;
import com.elementalpixel.elementalpixeldungeon.windows.WndBag;
import com.elementalpixel.elementalpixeldungeon.windows.WndWeaponInfusion;

import java.util.ArrayList;

public class ElementalStone extends Item {
    protected WndBag.Mode mode = WndBag.Mode.ARMOR_WEAPON;


    {
        image = ItemSpriteSheet.ELEMENTAL_STONE;

        stackable = true;
        unique = true;
        bones = false;
    }

    public static final String AC_AUGMENT = "augment";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_AUGMENT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_AUGMENT)) {
            GameScene.selectItem(itemSelector, mode, Messages.get(this, "select"));

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
        public void onSelect(Item item) {
            Hero hero = Dungeon.hero;
            curUser.spend(1);

            if (item instanceof Armor) {
                GameScene.show(new WndArmorInfusion(item));
            } else if (item instanceof Weapon) {
                GameScene.show(new WndWeaponInfusion(item));
            }
        }
    };
}
