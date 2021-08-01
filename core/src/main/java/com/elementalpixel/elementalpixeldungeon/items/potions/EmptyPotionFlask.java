package com.elementalpixel.elementalpixeldungeon.items.potions;

import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.items.Item;
import com.elementalpixel.elementalpixeldungeon.sprites.ItemSpriteSheet;

public class EmptyPotionFlask extends Item {
    {
        image = ItemSpriteSheet.POTION_EMPTY;
        bones = false;
        stackable = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
    }

    @Override
    public boolean doPickUp( Hero hero ) {
        if (super.doPickUp( hero )) {
            return true;
        } else {
            return false;
        }
    }
}
