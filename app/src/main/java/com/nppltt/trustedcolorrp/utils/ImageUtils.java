package com.nppltt.trustedcolorrp.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageUtils {

    public static Bitmap CorrectImageColor(Bitmap image, int correctR, int correctG, int correctB, boolean minus) {

        int w = image.getWidth();
        int h = image.getHeight();
        Bitmap newBmp = image.copy(Bitmap.Config.ARGB_8888, true);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                int pixColor = newBmp.getPixel(i, j);

                int inR = Color.red(pixColor);
                int inG = Color.green(pixColor);
                int inB = Color.blue(pixColor);

                int outR = ColorUtils.SafeColorCorrection(inR, correctR, minus);
                int outG = ColorUtils.SafeColorCorrection(inG, correctG, minus);
                int outB = ColorUtils.SafeColorCorrection(inB, correctB, minus);

                newBmp.setPixel(i, j, Color.argb(255, outR, outG, outB));
            }
        }
        return newBmp;
    }
}
