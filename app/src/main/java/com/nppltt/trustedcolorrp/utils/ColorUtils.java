package com.nppltt.trustedcolorrp.utils;

import android.graphics.Bitmap;

public class ColorUtils {

    public static Bitmap createBitmapFromColor(int color, int width, int height) {
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        image.eraseColor(color);
        return image;
    }

    public static int SafeColorCorrection(int colorComponent, int correctionValue, boolean minus) {
        if (minus) {
            if (colorComponent - correctionValue < 0) {
                return 0;
            } else {
                return colorComponent - correctionValue;
            }
        } else {
            if (colorComponent + correctionValue > 255) {
                return 255;
            } else {
                return colorComponent + correctionValue;
            }
        }
    }
}
