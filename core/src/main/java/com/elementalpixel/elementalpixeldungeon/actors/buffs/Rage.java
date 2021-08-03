package com.elementalpixel.elementalpixeldungeon.actors.buffs;

import com.elementalpixel.elementalpixeldungeon.Assets;
import com.elementalpixel.elementalpixeldungeon.Dungeon;
import com.elementalpixel.elementalpixeldungeon.actors.hero.Talent;
import com.elementalpixel.elementalpixeldungeon.effects.Speck;
import com.elementalpixel.elementalpixeldungeon.messages.Messages;
import com.elementalpixel.elementalpixeldungeon.ui.BuffIndicator;
import com.elementalpixel.elementalpixeldungeon.ui.RageIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Rage extends Buff implements RageIndicator.Action{

    {
        type = buffType.POSITIVE;

        actPriority = HERO_PRIO+1;
    }

    private static float RageLevel = 0f;
    private float RageCooldown = 0f;
    private static float RageDuration = 0f;

    private static final String LEVEL = "level";
    private static final String COOLDOWN = "cd";
    private static final String DURATION = "duration";

    @Override
    public boolean act(){
        if(RageCooldown > 0) {
            RageCooldown --;
        }

        if(RageLevel > 0 && !raging()){
            RageLevel -= 0.25f;
        }

        if(RageLevel == 0 && RageCooldown == 0) {
            detach();
            RageIndicator.clearAction(this);
        }

        if(raging()){
            RageDuration --;
            if(RageDuration == 0){
                detach(Dungeon.hero, Barrier.class);
                Talent.onRageEnd(Dungeon.hero);
            }
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
        RageCooldown = bundle.getFloat(COOLDOWN);
        RageDuration = bundle.getFloat(DURATION);
    }

    public static boolean raging(){
        return RageDuration > 0;
    }

    public void gainLevel(){
        if (RageCooldown <= 0){
            postpone(target.cooldown()+(1/target.speed()));
            RageLevel = Math.min(RageLevel + 1, 10 + 2 * Dungeon.hero.pointsInTalent(Talent.ENHANCED_RAGE));
            RageLevel = (float) Math.ceil(RageLevel);
            RageIndicator.setAction(this);
        }
    }

    public int damageFactor(int damage){
        if(raging()){
            float damageBuff = RageLevel;
            damage += damage * (damageBuff / 20);
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
            return Messages.get(this, "enraged");
        }
    }

    @Override
    public String desc() {
        if (RageDuration > 0){
            return Messages.get(this, "raging_desc", (RageLevel * 10f), RageDuration);
        } else if (RageCooldown > 0){
            return Messages.get(this, "recovering_desc", RageCooldown);
        } else {
            return Messages.get(this, "enraged_desc", RageLevel);
        }
    }

    public Image getIcon() {
        Image im = new Image(Assets.Interfaces.BUFFS_LARGE, 144, 0, 16, 16);
        im.hardlight(0xFFFF4C);
        return im;
    }

    public void doAction() {
        Talent.onRage(Dungeon.hero);
        RageLevel = (float) Math.ceil(RageLevel);
        RageDuration = RageLevel;
        RageCooldown = (10 + 5 * RageLevel);
        Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
        target.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
        BuffIndicator.refreshHero();
        RageIndicator.clearAction(this);
    }

}

