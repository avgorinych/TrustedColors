package com.nppltt.trustedcolorrp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.nppltt.trustedcolorrp.R;

public class CameraInstructionsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_instructions);
        findViewById(R.id.startPhotoShootingBtn).setOnClickListener(onStartPhotoShootingBtnClick);
        findViewById(R.id.buttonCallibration).setOnClickListener(onClickCalibration);
    }

    protected Button.OnClickListener onStartPhotoShootingBtnClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(v.getContext(), CameraImagingView.class);
            startActivityForResult(intent, -1);
        }
    };

    protected ImageButton.OnClickListener onClickCalibration = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(v.getContext(), CalibrationInstructionsView.class);
            startActivityForResult(intent, -1);
        }
    };
}
