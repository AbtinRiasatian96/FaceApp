package com.example.abtin.faceapp5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    String mCurrentPhotoPath;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddPersonClick(View view) {
        Intent myIntent = new Intent(this, AddPersonActivity.class);
        startActivity(myIntent);
    }

    public void onSettingsClick(View view) {
        Intent a = new Intent(this, SettingsActivity.class);
        startActivity(a);
    }

    public void onTrainingClick(View view){
        Intent a = new Intent(this, TrainingActivity.class);
        startActivity(a);
    }

    public void onRecognizeClick(View view){
        Intent a = new Intent(this, RecognitionActivity.class);
        startActivity(a);
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
