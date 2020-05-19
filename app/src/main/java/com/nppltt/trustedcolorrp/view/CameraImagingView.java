package com.nppltt.trustedcolorrp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.google.gson.Gson;
import com.nppltt.trustedcolorrp.BuildConfig;
import com.nppltt.trustedcolorrp.R;
import com.nppltt.trustedcolorrp.UserData;
import com.nppltt.trustedcolorrp.settings.StaticSettings;
import com.nppltt.trustedcolorrp.utils.FileManager;
import com.nppltt.trustedcolorrp.utils.ImageUtils;
import com.nppltt.trustedcolorrp.webapi.Requests;
import com.nppltt.trustedcolorrp.webapi.ServiceGenerator;
import com.nppltt.trustedcolorrp.webapi.requests.GetCorrectionColorsRequest;
import com.nppltt.trustedcolorrp.webapi.responses.GetCorrectionColorsResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraImagingView extends AppCompatActivity {

    private static final String appTag = "TrustedColorRP";
    private static final int REQUEST_TAKE_PHOTO = 1;

    private String currentPhotoPath;
    private ImageView imageView;
    private ImageView imageView2;
    private TextView TextViewR;
    private TextView TextViewG;
    private TextView TextViewB;
    private Button sendButton;
    private ProgressDialog dialog;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        findViewById(R.id.button_capture).setOnClickListener(onClickTakeImage);
        findViewById(R.id.button_send).setOnClickListener(onClickSendImage);

        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        TextViewR = findViewById(R.id.textViewR);
        TextViewG = findViewById(R.id.textViewG);
        TextViewB = findViewById(R.id.textViewB);
        sendButton = findViewById(R.id.button_send);
        sendButton.setVisibility(View.GONE);

        userData = loadCalibration();
        if (userData == null) {
            userData = new UserData();
            userData.rgb = new int[3];
            for (int i = 0; i < 3; i++) {
                userData.rgb[i] = 0;
            }
        }

        ResetControls();
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {

        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    String errorMessage = getString(R.string.deviceNoCamera);
                    Toast toast = Toast.makeText(CameraImagingView.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(CameraImagingView.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        try {
            String timeStamp = new SimpleDateFormat("MMdd_HHmm").format(new Date());
            String imageFileName = "TrustedColor_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (Exception ex) {
            Toast.makeText(CameraImagingView.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private UserData loadCalibration() {
        try {
            return new FileManager().loadData(this, UserData.class, StaticSettings.savedDataName);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void ResetControls() {
        TextViewR.setText("");
        TextViewG.setText("");
        TextViewB.setText("");
    }

    private void setPic(int width) throws IOException {

        Bitmap bitmap = getBitMapFromFile(width);
        this.imageView.setImageBitmap(bitmap);
    }

    public Bitmap getBitMapFromFile(int maxSize) throws IOException {

        Log.w(appTag, "getBitMapFromFile");
        Bitmap image = BitmapFactory.decodeFile(currentPhotoPath);

        ExifInterface ei = new ExifInterface(currentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(image, 90);
                Log.w(appTag, "ORIENTATION_ROTATE_90");
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(image, 180);
                Log.w(appTag, "ORIENTATION_ROTATE_180");
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(image, 270);
                Log.w(appTag, "ORIENTATION_ROTATE_270");
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = image;
                Log.w(appTag, "ORIENTATION_NORMAL");
        }

        int width = rotatedBitmap.getWidth();
        int height = rotatedBitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (maxSize > 0) {
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
        }
        return Bitmap.createScaledBitmap(rotatedBitmap, width, height, true);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String encodeToBase64(Bitmap image) {

        Bitmap immageX = image;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        immageX.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    protected Button.OnClickListener onClickTakeImage = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            ResetControls();
            //imageView.setImageBitmap(null);
            //imageView2.setImageBitmap(null);
            dispatchTakePictureIntent();
        }
    };

    protected final Button.OnClickListener onClickSendImage = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            dialog = ProgressDialog.show(CameraImagingView.this, "",
                    getString(R.string.loadingWaiting), true);
            ResetControls();
            imageView2.setImageBitmap(null);

            try {
                sendImageToServer(getBitMapFromFile(1400));
            } catch (IOException e) {
                dialog.dismiss();
                e.printStackTrace();
            }
        }
    };

    private void sendImageToServer(Bitmap bitmap) {

        Requests netClient = ServiceGenerator.createService(Requests.class);
        GetCorrectionColorsRequest req_ = new GetCorrectionColorsRequest();
        req_.base64Image = encodeToBase64(bitmap);

        Log.i("netClient", "sendImageToServer");

        netClient.GetCorrectionColors(req_).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        String resp_ = response.body().string();
                        Gson gson = new Gson();
                        GetCorrectionColorsResponse fromJson = gson.fromJson(resp_, GetCorrectionColorsResponse.class);
                        if (fromJson.error == null || fromJson.error.isEmpty())
                            fromJson.error = "OK";

                        Log.i("Response result: ", fromJson.error);
                        Log.i("Response score: ", Integer.toString(fromJson.scoreCalibrated));
                        Log.i("Response time: ", Integer.toString(fromJson.timeCalibrated));

                        Toast.makeText(CameraImagingView.this, fromJson.error, Toast.LENGTH_LONG).show();

                        int width = imageView2.getWidth();
                        int height = imageView2.getHeight();
                        int maxsize = width;
                        if (height > width)
                            maxsize = height;

                        int amendmentR = (int) fromJson.rgb.red + userData.rgb[StaticSettings.RED];
                        int amendmentG = (int) fromJson.rgb.green + userData.rgb[StaticSettings.GREEN];
                        int amendmentB = (int) fromJson.rgb.blue + userData.rgb[StaticSettings.BLUE];

                        Bitmap correctedBitmap = ImageUtils.CorrectImageColor(getBitMapFromFile(maxsize), amendmentR, amendmentG, amendmentB, true);
                        dialog.dismiss();

                        imageView2.setImageBitmap(correctedBitmap);

                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        if (!storageDir.exists()) {
                            storageDir.mkdirs();
                        }
                        String timeStamp = new SimpleDateFormat("MMdd_HHmm").format(new Date());
                        String imageFileName = "Corrected_" + timeStamp;
                        File image = File.createTempFile(imageFileName, ".jpeg", storageDir);
                        FileOutputStream fos = new FileOutputStream(image);
                        correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        galleryAddPic(image);
                        writeExif(image, (int) fromJson.rgb.red, (int) fromJson.rgb.green, (int) fromJson.rgb.blue);

                        TextViewR.setText(String.format("R: %s; ", String.valueOf(fromJson.rgb.red)));
                        TextViewG.setText(String.format("G: %s; ", String.valueOf(fromJson.rgb.green)));
                        TextViewB.setText(String.format("B: %s; ", String.valueOf(fromJson.rgb.blue)));
                    } catch (Exception ex) {
                        dialog.dismiss();
                        Toast.makeText(CameraImagingView.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(CameraImagingView.this, response.message(), Toast.LENGTH_LONG).show();
                    Log.i("netClient", response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, final Throwable t) {
                CameraImagingView.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(CameraImagingView.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("netClient", t.getMessage());
                    }
                });
            }
        });
    }

    private void galleryAddPic(File photoFile) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void writeExif(File photoFile, int r, int g, int b) throws IOException {

        String data = String.format("R: %s; G: %s; B: %s", Integer.toString(r), Integer.toString(g), Integer.toString(b));
        ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
        exif.setAttribute(ExifInterface.TAG_USER_COMMENT, data);
        exif.saveAttributes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            sendButton.setVisibility(View.VISIBLE);
            try {
                setPic(getResources().getDisplayMetrics().widthPixels);
            } catch (IOException ex) {
                //ex.printStackTrace();
                Toast.makeText(CameraImagingView.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
