package com.nppltt.trustedcolorrp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nppltt.trustedcolorrp.R;
import com.nppltt.trustedcolorrp.UserData;
import com.nppltt.trustedcolorrp.settings.StaticSettings;
import com.nppltt.trustedcolorrp.utils.ColorUtils;
import com.nppltt.trustedcolorrp.utils.FileManager;

import java.io.IOException;

public class CalibrationResultView extends AppCompatActivity {

    private static int colorsCount;
    private int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calibration_result);

        findViewById(R.id.buttonOK).setOnClickListener(onClickOK);

        initAndroidColors();
        fillColorsTable();

        UserData userData = new UserData();
        userData.rgb = StaticSettings.rgb;
        userData.calibrated = true;
        saveCorrection(userData);
    }

    protected Button.OnClickListener onClickOK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            BackToMenu();
        }
    };

    private void BackToMenu() {
        Intent intent = new Intent(CalibrationResultView.this, CameraInstructionsView.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        BackToMenu();
    }

    private void fillColorsTable() {
        UserData userData = new UserData();
        userData.rgb = StaticSettings.rgb;

        TableLayout tableLayout = findViewById(R.id.tableColors);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / colorsCount;
        int height = size.y / 2;

        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < colorsCount; i++) {

            tableRow.setLayoutParams(new LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(createBitmapFromColor(correctColorWithAdjustments(colors[i], userData), width, height));
            tableRow.addView(imageView, i);
        }

        tableLayout.addView(tableRow, 0);
    }

    private int correctColorWithAdjustments(int color, UserData userData) {
        int colorR = ColorUtils.SafeColorCorrection(Color.red(color), userData.rgb[StaticSettings.RED], true);
        int colorG = ColorUtils.SafeColorCorrection(Color.green(color), userData.rgb[StaticSettings.GREEN], true);
        int colorB = ColorUtils.SafeColorCorrection(Color.blue(color), userData.rgb[StaticSettings.BLUE], true);

        return Color.argb(255, colorR, colorG, colorB);
    }

    private void initAndroidColors() {

        colors = new int[StaticSettings.rawHexColors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(StaticSettings.rawHexColors[i]);
        }
        colorsCount = colors.length;
    }

    private Bitmap createBitmapFromColor(int color, int width, int height) {
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        image.eraseColor(color);
        return image;
    }

    private void saveCorrection(UserData userData) {
        try {
            new FileManager().saveData(this, userData, StaticSettings.savedDataName);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
