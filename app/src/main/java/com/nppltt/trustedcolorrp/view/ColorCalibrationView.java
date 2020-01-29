package com.nppltt.trustedcolorrp.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nppltt.trustedcolorrp.R;
import com.nppltt.trustedcolorrp.settings.StaticSettings;

import java.util.ArrayList;

public class ColorCalibrationView extends AppCompatActivity {

    private ArrayList<int[]> resultingRGB = new ArrayList<>();
    private int[] colors;
    private int currentColor;
    private ImageView imageColor;
    private SeekBar seekValueR;
    private SeekBar seekValueG;
    private SeekBar seekValueB;
    private Button nextButton;
    private ImageView colorPanel;
    private TextView textNumStep;
    private TextView textColorsCount;
    private int colorsCount;
    private int iterationCount;
    private TextView textRed;
    private TextView textGreen;
    private TextView textBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_color_calibration);
        initAndroidColors();

        imageColor = findViewById(R.id.currentColorImage);
        seekValueR = findViewById(R.id.seekBarR);
        seekValueG = findViewById(R.id.seekBarG);
        seekValueB = findViewById(R.id.seekBarB);
        nextButton = findViewById(R.id.nextButton);
        colorPanel = findViewById(R.id.coloredPanel);
        textNumStep = findViewById(R.id.textViewStepNum);
        textColorsCount = findViewById(R.id.textViewStepsCount);
        textRed = findViewById(R.id.textRed);
        textGreen = findViewById(R.id.textGreen);
        textBlue = findViewById(R.id.textBlue);

        seekValueR.setOnSeekBarChangeListener(seekBarChangeListener);
        seekValueG.setOnSeekBarChangeListener(seekBarChangeListener);
        seekValueB.setOnSeekBarChangeListener(seekBarChangeListener);
        nextButton.setOnClickListener(onClickNextButton);

        iterationCount = 0;
        textNumStep.setText("1");
        textColorsCount.setText(Integer.toString(colorsCount));
        setColorImage(iterationCount);
        setColorToColorPicker(colors[0]);
    }

    private Button.OnClickListener onClickNextButton = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (iterationCount < colorsCount)
            {   int[] rgbColor = new int[3];
                rgbColor[StaticSettings.RED] = Color.red(currentColor);
                rgbColor[StaticSettings.GREEN] = Color.green(currentColor);
                rgbColor[StaticSettings.BLUE] = Color.blue(currentColor);
                resultingRGB.add(rgbColor);
            }

            iterationCount++;
            if (iterationCount < colorsCount) {
                textNumStep.setText(Integer.toString(iterationCount + 1));
                setColorImage(iterationCount);
                setColorToColorPicker(colors[iterationCount]);
            }

            if (iterationCount >= colorsCount)
            {
                onCorrectionFinish();
                return;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            String tag = (String) seekBar.getTag();

            if (tag.equals("0"))
            {
                textRed.setText(Integer.toString(seekValueR.getProgress()));
            }
            if (tag.equals("1"))
            {
                textGreen.setText(Integer.toString(seekValueG.getProgress()));
            }
            if (tag.equals("2"))
            {
                textBlue.setText(Integer.toString(seekValueB.getProgress()));
            }

            setColorPanel();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void setColorPanel() {

        int colorR = seekValueR.getProgress();
        int colorG = seekValueG.getProgress();
        int colorB = seekValueB.getProgress();

        currentColor = Color.rgb(colorR, colorG, colorB);
        colorPanel.setBackgroundColor(Color.argb(255, colorR, colorG, colorB));
    }

    private void initAndroidColors() {

        colors = new int[StaticSettings.rawHexColors.length];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(StaticSettings.rawHexColors[i]);
        }
        colorsCount = colors.length;
    }

    private void setColorToColorPicker(int color) {

        int colorR = Color.red(color);
        int colorG = Color.green(color);
        int colorB = Color.blue(color);

        seekValueR.setProgress(colorR);
        seekValueG.setProgress(colorG);
        seekValueB.setProgress(colorB);
        colorPanel.setBackgroundColor(color);

        currentColor = Color.rgb(colorR, colorG, colorB);
    }

    private void setColorImage(int number) {

        Drawable drawable = getDrawable(StaticSettings.imagesCorrection[number]);
        imageColor.setImageDrawable(drawable);
    }

    private void onCorrectionFinish()
    {
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
