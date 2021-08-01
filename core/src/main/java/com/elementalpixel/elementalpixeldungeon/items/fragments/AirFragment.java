package com.elementalpixel.elementalpixeldungeon.items.fragments;

import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;

public class AirFragment extends Fragment {
    {
        //image = ItemSpriteSheet.AIR_FRAGMENT;
        unique = true;
        stackable = false;
        bones = false;
    }

    public AirFragment() {
        super();
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