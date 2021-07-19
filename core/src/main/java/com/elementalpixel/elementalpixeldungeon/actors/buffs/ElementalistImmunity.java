
package com.elementalpixel.elementalpixeldungeon.actors.buffs;


import com.elementalpixel.elementalpixeldungeon.actors.blobs.ConfusionGas;
import com.elementalpixel.elementalpixeldungeon.actors.blobs.Electricity;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class ElementalistImmunity extends FlavourBuff {

    public static final float DURATION = (float) Infinity;

    {
        immunities.add(ConfusionGas.class);
        immunities.add(Paralysis.class);
        immunities.add(Vertigo.class);

    }

    public static class ChillImmunity extends FlavourBuff { { immunities.add(Chill.class); } }
    public static class ElectricityImmunity extends FlavourBuff { { immunities.add(Electricity.class); } }
    public static class CorrosionImmunity extends FlavourBuff { { immunities.add(Corrosion.class); } }
}
