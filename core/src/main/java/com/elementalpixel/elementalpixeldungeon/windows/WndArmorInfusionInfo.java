package com.elementalpixel.elementalpixeldungeon.windows;

import com.elementalpixel.elementalpixeldungeon.scenes.PixelScene;
import com.elementalpixel.elementalpixeldungeon.ui.RenderedTextBlock;
import com.elementalpixel.elementalpixeldungeon.ui.Window;

class FireArmorInfo extends Window {
    private static final int WIDTH = 120;

    public FireArmorInfo() {
        RenderedTextBlock fireArmorInfo = PixelScene.renderTextBlock(6);
        fireArmorInfo.text("After being hit, the slight portion of the damage will be reflected to the attacker and you have _25% chance to set him on fire_.", WIDTH);
        fireArmorInfo.setRect(0, 0, WIDTH, 25);
        add(fireArmorInfo);
        resize(WIDTH, (int) fireArmorInfo.height());
    }
}

class AirArmorInfo extends Window {
    private static final int WIDTH = 120;

    public AirArmorInfo() {
        RenderedTextBlock airArmorInfo = PixelScene.renderTextBlock(6);
        airArmorInfo.text("After being hit the attacker becomes _crippled_", WIDTH);
        airArmorInfo.setRect(0, 0, WIDTH, 25);
        add(airArmorInfo);
        resize(WIDTH, (int) airArmorInfo.height());
    }
}

class WaterArmorInfo extends Window {
    private static int WIDTH = 120;

    public WaterArmorInfo() {
        RenderedTextBlock waterArmorInfo = PixelScene.renderTextBlock(6);
        waterArmorInfo.text("You have higher chance to dodge", WIDTH);
        waterArmorInfo.setRect(0, 0, WIDTH, 25);
        add(waterArmorInfo);
        resize(WIDTH, (int) waterArmorInfo.height());
    }
}

class EarthArmorInfo extends Window {
    private static int WIDTH = 120;

    public EarthArmorInfo() {
        RenderedTextBlock earthArmorInfo = PixelScene.renderTextBlock(6);
        earthArmorInfo.text("When you are below 50% of your max health, you will gain barkskin", WIDTH);
        earthArmorInfo.setRect(0, 0, WIDTH, 25);
        add(earthArmorInfo);
        resize(WIDTH, (int) earthArmorInfo.height());
    }
}

