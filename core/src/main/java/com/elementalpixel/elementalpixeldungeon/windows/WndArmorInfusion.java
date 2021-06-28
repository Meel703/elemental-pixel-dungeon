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

package com.elementalpixel.elementalpixeldungeon.windows;


import com.elementalpixel.elementalpixeldungeon.items.Item;
import com.elementalpixel.elementalpixeldungeon.items.armor.Armor;
import com.elementalpixel.elementalpixeldungeon.items.armor.glyphs.Air;
import com.elementalpixel.elementalpixeldungeon.items.armor.glyphs.Earth;
import com.elementalpixel.elementalpixeldungeon.items.armor.glyphs.Fire;
import com.elementalpixel.elementalpixeldungeon.items.armor.glyphs.Water;
import com.elementalpixel.elementalpixeldungeon.items.stones.ElementalStone;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.scenes.GameScene;
import com.elementalpixel.elementalpixeldungeon.scenes.PixelScene;
import com.elementalpixel.elementalpixeldungeon.ui.IconButton;
import com.elementalpixel.elementalpixeldungeon.ui.Icons;
import com.elementalpixel.elementalpixeldungeon.ui.RedButton;
import com.elementalpixel.elementalpixeldungeon.ui.RenderedTextBlock;
import com.elementalpixel.elementalpixeldungeon.ui.Window;

public class WndArmorInfusion extends Window {

    private static final int WIDTH		= 120;
    private static final int BTN_HEIGHT	= 18;
    private static final float GAP		= 2;

    public WndArmorInfusion(Item item) {

        super();

        RenderedTextBlock hl = PixelScene.renderTextBlock( 6 );
        hl.text( "What element do you want to infuse your armor with?\n\n", WIDTH );
        hl.setRect( 0, 0, WIDTH, 0 );
        add( hl );

        RedButton btnFire = new RedButton( Messages.get(this,"fire")) {
            @Override
            protected void onClick() {
                hide();
                ((Armor)item).inscribe(new Fire());
            }
        };
        btnFire.setRect( 0, hl.bottom() + GAP, WIDTH, BTN_HEIGHT );
        add( btnFire );

        RedButton btnAir = new RedButton( Messages.get(this, "air" )) {
            @Override
            protected void onClick() {
                hide();
                ((Armor)item).inscribe(new Air());
            }
        };
        btnAir.setRect( 0, btnFire.bottom() + GAP, WIDTH, BTN_HEIGHT );
        add( btnAir );

        RedButton btnWater = new RedButton( Messages.get(this, "water" )) {
            @Override
            protected void onClick() {
                hide();
                ((Armor)item).inscribe(new Water());
            }
        };
        btnWater.setRect( 0, btnAir.bottom() + GAP, WIDTH, BTN_HEIGHT );
        add( btnWater );

        RedButton btnEarth = new RedButton( Messages.get(this, "earth" )) {
            @Override
            protected void onClick() {
                hide();
                ((Armor)item).inscribe(new Earth());
            }
        };
        btnEarth.setRect( 0, btnWater.bottom() + GAP, WIDTH, BTN_HEIGHT );
        add( btnEarth );


        RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
            @Override
            protected void onClick() {
                hide();
                new ElementalStone().collect();
            }
        };
        btnCancel.setRect( 0, btnEarth.bottom() + (GAP * 2), WIDTH, BTN_HEIGHT );
        add( btnCancel );

        resize( WIDTH, (int)btnCancel.bottom() );


        IconButton btnInfoFire = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                GameScene.show(new FireArmorInfo());
            }
        };
        btnInfoFire.setRect(WIDTH-20, btnFire.top() + (btnFire.height()-20)/2, 20, 20);
        add(btnInfoFire);

        IconButton btnInfoAir = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                GameScene.show(new AirArmorInfo());
            }
        };
        btnInfoAir.setRect(WIDTH-20, btnAir.top() + (btnAir.height()-20)/2, 20, 20);
        add(btnInfoAir);

        IconButton btnWaterInfo = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                GameScene.show(new WaterArmorInfo());
            }
        };
        btnWaterInfo.setRect(WIDTH-20, btnWater.top() + (btnWater.height()-20)/2, 20, 20);
        add(btnWaterInfo);

        IconButton btnEarthInfo = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                GameScene.show(new EarthArmorInfo());
            }
        };
        btnEarthInfo.setRect(WIDTH-20, btnEarth.top() + (btnEarth.height()-20)/2, 20, 20);
        add(btnEarthInfo);


    }
}
