package com.elementalpixel.elementalpixeldungeon.actors.buffs;

import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Hero;
import com.elementalpixel.elementalpixeldungeon.effects.Speck;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.ui.ActionIndicator;
import com.elementalpixel.elementalpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Rage extends Buff implements ActionIndicator.Action{

    {
        type = buffType.POSITIVE;

        actPriority = HERO_PRIO+1;
    }

    private static float RageLevel = 0f;
    private int RageCooldown = 0;
    private static int RageDuration = 0;

    private static final String LEVEL = "level";
    private static final String COOLDOWN = "cd";
    private static final String DURATION = "duration";

    @Override
    public boolean act(){
        if(RageCooldown > 0) {
            RageCooldown --;
            if(!raging()){
                detach(Dungeon.hero, Barrier.class);
            }
        }

        if(RageLevel > 0 && !raging()){
            RageLevel -= 0.25f;
        }

        if(RageLevel == 0) {
            detach();
            ActionIndicator.clearAction(this);
        }

        if(raging()){
            RageDuration --;
        }

        spend(TICK);
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, RageLevel);
        bundle.put(COOLDOWN, RageCooldown);
        bundle.put(DURATION, RageDuration);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        RageLevel = bundle.getFloat(LEVEL);
        RageCooldown = bundle.getInt(COOLDOWN);
        RageDuration = bundle.getInt(DURATION);
    }

    public static boolean raging(){
        return RageDuration > 0;
    }

    public void gainLevel(){
        if (RageCooldown <= 0){
            postpone(target.cooldown()+(1/target.speed()));
            RageLevel = Math.min(RageLevel + 1, 10);
            ActionIndicator.setAction(this);
        }
    }

    public int damageFactor(int damage){
        if(raging()){
            float damageBuff = RageLevel;
            damage += damage * (damageBuff / 10);
        }
        return damage;
    }

    @Override
    public int icon() {
        return BuffIndicator.BERSERK;
    }

    @Override
    public void tintIcon(Image icon) {
        if (RageDuration > 0){
            icon.hardlight(1f, 0f, 0f);
        } else if (RageCooldown > 0){
            icon.hardlight(0, 0, 1f);
        } else {
            icon.hardlight(1f, 0.5f, 0f);
        }
    }

    @Override
    public String toString() {
        if (RageDuration > 0){
            return Messages.get(this, "raging");
        } else if (RageCooldown > 0){
            return Messages.get(this, "recovering");
        } else {
            return Messages.get(this, "rage");
        }
    }

    @Override
    public String desc() {
        if (RageDuration > 0){
            return Messages.get(this, "raging_desc", RageDuration);
        } else if (RageCooldown > 0){
            return Messages.get(this, "recovering_desc", RageCooldown);
        } else {
            return Messages.get(this, "rage_desc", RageLevel);
        }
    }

    public Image getIcon() {
        Image im = new Image(Assets.Interfaces.BUFFS_LARGE, 144, 32, 16, 16);
        im.hardlight(0x99992E);
        return im;
    }

    public void doAction() {
        Buff.affect(Dungeon.hero, Barrier.class).setShield((int) (0.5f * Dungeon.hero.HT + 10));
        RageDuration = (int) (2 * RageLevel);
        RageCooldown = (int) (10 + 12 * RageLevel);
        Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
        target.sprite.emitter().burst(Speck.factory(Speck.JET), 5+ (int) (RageLevel));
        BuffIndicator.refreshHero();
        ActionIndicator.clearAction(this);
    }

}

