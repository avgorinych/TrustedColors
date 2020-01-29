package com.nppltt.trustedcolorrp.utils;

public class ColorUtils {

    public static int SafeColorCorrection(int colorComponent, int correctionValue, boolean minus)
    {
        if (minus)
        {
            if (colorComponent - correctionValue < 0)
            {
                return 0;
            }
            else
            {
                return colorComponent - correctionValue;
            }
        }
        else
        {
            if (colorComponent + correctionValue > 255)
            {
                return 255;
            }
            else
            {
                return colorComponent + correctionValue;
            }
        }
    }
}
