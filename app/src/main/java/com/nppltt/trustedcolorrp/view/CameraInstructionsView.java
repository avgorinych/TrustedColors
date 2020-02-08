package com.nppltt.trustedcolorrp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nppltt.trustedcolorrp.R;
import com.nppltt.trustedcolorrp.UserData;
import com.nppltt.trustedcolorrp.settings.StaticSettings;
import com.nppltt.trustedcolorrp.utils.FileManager;

public class CameraInstructionsView extends AppCompatActivity {

    private boolean calibrationAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_instructions);
        findViewById(R.id.startPhotoShootingBtn).setOnClickListener(onStartPhotoShootingBtnClick);
        findViewById(R.id.buttonCallibration).setOnClickListener(onClickCalibration);

        calibrationAllowed = false;
        UserData userData = loadCalibration();
        if (userData != null)
        {
            calibrationAllowed = userData.calibrated;
        }
    }

    private UserData loadCalibration()
    {
        try
        {
            return new FileManager().loadData(this, UserData.class, StaticSettings.savedDataName);
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void OpenCalibrationView()
    {
        Intent intent = new Intent(CameraInstructionsView.this, CalibrationInstructionsView.class);
        startActivity(intent);
    }

    protected Button.OnClickListener onStartPhotoShootingBtnClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (calibrationAllowed) {
                Intent intent = new Intent(v.getContext(), CameraImagingView.class);
                startActivityForResult(intent, -1);
            }
            else
            {
                Toast.makeText(CameraInstructionsView.this, getString(R.string.needScreenColorCalibration), Toast.LENGTH_LONG).show();
                OpenCalibrationView();
            }
        }
    };

    protected ImageButton.OnClickListener onClickCalibration = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View v) {

            OpenCalibrationView();
        }
    };

    @Override
    public void onBackPressed() { }
}
