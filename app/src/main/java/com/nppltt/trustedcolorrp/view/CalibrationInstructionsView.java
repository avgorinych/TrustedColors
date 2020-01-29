package com.nppltt.trustedcolorrp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.nppltt.trustedcolorrp.R;

public class CalibrationInstructionsView extends AppCompatActivity {

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calibration_instructions);

        startButton = findViewById(R.id.startCorrectionBtn);
        startButton.setOnClickListener(onClickNextButton);
    }

    private Button.OnClickListener onClickNextButton = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(v.getContext(), ColorCalibrationView.class);
            startActivityForResult(intent, -1);
        }
    };
}
