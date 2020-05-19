package com.nppltt.trustedcolorrp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nppltt.trustedcolorrp.R;
import com.nppltt.trustedcolorrp.SimpleDialog;
import com.nppltt.trustedcolorrp.colorWheel.SimpleColorWheelDialog;
import com.nppltt.trustedcolorrp.settings.StaticSettings;
import com.nppltt.trustedcolorrp.utils.ColorUtils;

import java.util.ArrayList;

public class ColorCalibrationView extends AppCompatActivity implements SimpleDialog.OnDialogResultListener {
    private ArrayList<int[]> resultingRGB = new ArrayList<>();
    private int[] colors;
    private int currentColor;
    private ImageView imageColor;
    private ImageView colorPanel;
    private TextView textNumStep;
    private int colorsCount;
    private int iterationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_color_calibration);
        initAndroidColors();

        imageColor = findViewById(R.id.currentColorImage);
        Button nextButton = findViewById(R.id.nextButton);
        colorPanel = findViewById(R.id.coloredPanel);
        textNumStep = findViewById(R.id.textViewStepNum);

        nextButton.setOnClickListener(onClickNextButton);
        imageColor.setOnClickListener(onClickShowPicker);

        iterationCount = 0;
        textNumStep.setText(String.format("1 %s %s", getString(R.string.colorProbeOf), Integer.toString(colorsCount)));
        setColorImage(iterationCount);
        setColorToColorPicker(colors[0]);
    }

    private Button.OnClickListener onClickShowPicker = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            setColorToColorPicker(colors[iterationCount]);
        }
    };

    private void setResultingImageColor(Bitmap bitmap) {
        ImageView image = colorPanel;
        image.setImageBitmap(bitmap);
    }

    private void setColorToColorPicker(int color) {

        SimpleColorWheelDialog.build().hideHexInput(true).color(color, true).alpha(false).show(this);
    }

    private Button.OnClickListener onClickNextButton = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (iterationCount < colorsCount) {
                int[] rgbColor = new int[3];
                rgbColor[StaticSettings.RED] = Color.red(currentColor);
                rgbColor[StaticSettings.GREEN] = Color.green(currentColor);
                rgbColor[StaticSettings.BLUE] = Color.blue(currentColor);
                resultingRGB.add(rgbColor);
            }

            iterationCount++;
            if (iterationCount < colorsCount) {
                textNumStep.setText(String.format("%s %s %s", Integer.toString(iterationCount + 1), getString(R.string.colorProbeOf), Integer.toString(colorsCount)));
                setColorImage(iterationCount);
                setColorToColorPicker(colors[iterationCount]);
            }

            if (iterationCount >= colorsCount) {
                onCorrectionFinish();
            }
        }
    };

    private void initAndroidColors() {

        colors = new int[StaticSettings.rawHexColors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(StaticSettings.rawHexColors[i]);
        }
        colorsCount = colors.length;
    }

    private void setColorImage(int number) {

        Drawable drawable = getDrawable(StaticSettings.imagesCorrection[number]);
        imageColor.setImageDrawable(drawable);
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {

        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {

                case SimpleColorWheelDialog.TAG: /** {@link MainActivity#showHsvWheel(View)} **/

                    currentColor = extras.getInt(SimpleColorWheelDialog.COLOR);

                    int widthPanel = colorPanel.getMeasuredWidth();
                    int heightPanel = colorPanel.getMeasuredHeight();

                    setResultingImageColor(ColorUtils.createBitmapFromColor(currentColor, widthPanel, heightPanel));

                    return true;
            }
        }
        return false;
    }

    private void onCorrectionFinish() {
        int[] averageRgbColor = new int[3];
        for (int i = 0; i < resultingRGB.size(); i++) {
            averageRgbColor[StaticSettings.RED] += resultingRGB.get(i)[StaticSettings.RED] - Color.red(colors[i]);
            averageRgbColor[StaticSettings.GREEN] += resultingRGB.get(i)[StaticSettings.GREEN] - Color.green(colors[i]);
            averageRgbColor[StaticSettings.BLUE] += resultingRGB.get(i)[StaticSettings.BLUE] - Color.blue(colors[i]);
        }
        averageRgbColor[StaticSettings.RED] = averageRgbColor[StaticSettings.RED] / resultingRGB.size();
        averageRgbColor[StaticSettings.GREEN] = averageRgbColor[StaticSettings.GREEN] / resultingRGB.size();
        averageRgbColor[StaticSettings.BLUE] = averageRgbColor[StaticSettings.BLUE] / resultingRGB.size();

        StaticSettings.rgb = averageRgbColor;

        Intent intent = new Intent(ColorCalibrationView.this, CalibrationResultView.class);
        startActivity(intent);
    }
}
