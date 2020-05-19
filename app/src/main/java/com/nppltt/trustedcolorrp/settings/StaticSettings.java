package com.nppltt.trustedcolorrp.settings;

import com.nppltt.trustedcolorrp.R;

public abstract class StaticSettings {

    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;

    public static final String savedDataName = "trustedColorConfig";

    public static final int[] imagesCorrection = new int[]{

            R.drawable.c1,
            R.drawable.c2,
            R.drawable.c3,
            R.drawable.c4,
            R.drawable.c5,
            R.drawable.c6,
            R.drawable.c7,
            R.drawable.c8
    };

    public static final String[] rawHexColors = new String[]{

            "#00ff05", //0
            "#ff00fe", //1
            "#fefefe", //2
            "#fe0000", //3
            "#00ffff", //4
            "#ffff00", //5
            "#000000", //6
            "#0000fe"  //7
    };

    public static int[] rgb = new int[3];
}
