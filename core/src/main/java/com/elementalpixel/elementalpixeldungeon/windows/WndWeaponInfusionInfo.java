package com.elementalpixel.elementalpixeldungeon.windows;

import com.elementalpixel.elementalpixeldungeon.scenes.PixelScene;
import com.elementalpixel.elementalpixeldungeon.ui.RenderedTextBlock;
import com.elementalpixel.elementalpixeldungeon.ui.Window;

class FireWeaponInfo extends Window {
    private static final int WIDTH = 120;

    public FireWeaponInfo() {
        RenderedTextBlock fireWeaponInfo = PixelScene.renderTextBlock(6);
        fireWeaponInfo.text("You are blessed when you attack with full health", WIDTH);
        fireWeaponInfo.setRect(0, 0, WIDTH, 25);
        add(fireWeaponInfo);
        resize(WIDTH, (int) fireWeaponInfo.height());
    }
}

class AirWeaponInfo extends Window {
    private static final int WIDTH = 120;

    public AirWeaponInfo() {
        RenderedTextBlock airWeaponInfo = PixelScene.renderTextBlock(6);
        airWeaponInfo.text("Your weapon is 1.5 times faster", WIDTH);
        airWeaponInfo.setRect(0, 0, WIDTH, 25);
        add(airWeaponInfo);
        resize(WIDTH, (int) airWeaponInfo.height());
    }
}

class WaterWeaponInfo extends Window {
    private static int WIDTH = 120;

    public WaterWeaponInfo() {
        RenderedTextBlock waterWeaponInfo = PixelScene.renderTextBlock(6);
        waterWeaponInfo.text("Your weapon has 20% accuracy boost", WIDTH);
        waterWeaponInfo.setRect(0, 0, WIDTH, 25);
        add(waterWeaponInfo);
        resize(WIDTH, (int) waterWeaponInfo.height());
    }
}

class EarthWeaponInfo extends Window {
    private static int WIDTH = 120;

    public EarthWeaponInfo() {
        RenderedTextBlock earthWeaponInfo = PixelScene.renderTextBlock(6);
        earthWeaponInfo.text("You will heal yourself by _25%_ of your max health when you attack while rooted", WIDTH);
        earthWeaponInfo.setRect(0, 0, WIDTH, 25);
        add(earthWeaponInfo);
        resize(WIDTH, (int) earthWeaponInfo.height());
    }
}

