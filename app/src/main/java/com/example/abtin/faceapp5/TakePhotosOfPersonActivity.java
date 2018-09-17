package com.example.abtin.faceapp5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;


// photos are taken manually, from the front camera,

public class TakePhotosOfPersonActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private CustomCameraView mAddPersonView;
    // The timerDiff defines after how many milliseconds a picture is taken
    private long timerDiff;
    private long lastTime;
    private PreProcessorFactory ppF;
    private FileHelper fh;
    private String folder;
    private String subfolder;
    private String personName;
    private int total;
    private int numberOfPictures;
    private int method;
    private ImageButton btn_Capture;
    private boolean capturePressed;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photos_of_person_activity);



        Intent intent = getIntent();
        folder = "Training";

        personName = intent.getStringExtra("AddedPersonName");
        //Toast.makeText(getApplicationContext(), personName, Toast.LENGTH_SHORT).show();

        //check
        capturePressed = false;
        fh = new FileHelper();
        total = 0;

//        lastTime = new Date().getTime();
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //check


        mAddPersonView = (CustomCameraView) findViewById(R.id.AddPersonPreview);
        // Use camera which is selected in settings
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        front_camera = true;

        numberOfPictures = 5;         //Integer.valueOf(sharedPref.getString("key_numberOfPictures", "100"));

        night_portrait = false;
        exposure_compensation = 50;

        mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mAddPersonView.setVisibility(SurfaceView.VISIBLE);
        mAddPersonView.setCvCameraViewListener(this);

        int maxCameraViewWidth = 640;           //Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640"));
        int maxCameraViewHeight = 480;          //Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480"));
        mAddPersonView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);



        ppF = new PreProcessorFactory(this);
        if(ppF==null){
            Toast.makeText(getApplicationContext(), "null ppf.", Toast.LENGTH_SHORT).show();
        }
        mAddPersonView.enableView();

    }


    public void onTakePhotoClick(View view) {
        capturePressed = true;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        Mat imgRgba = inputFrame.rgba();
        Mat imgRgbaT =  imgRgba.t();
        Imgproc.resize(imgRgbaT, imgRgbaT, imgRgba.size());
        Core.flip(imgRgbaT, imgRgbaT, 0);
        imgRgba = imgRgbaT;

        Mat imgCopy = new Mat();
        imgRgba.copyTo(imgCopy);

//        Core.flip(imgRgba, imgRgba, 1); //flip cause it's taken from front camera


        // Check that only 1 face is found. Skip if any or more than 1 are found.

        List<Mat> images = ppF.getCroppedImage(imgCopy);
        if (images != null && images.size() == 1) {
            Mat img = images.get(0);
            if (img != null) {
                Rect[] faces = ppF.getFacesForRecognition();
                //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                if ((faces != null) && (faces.length == 1)) {
                    faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                    if (capturePressed) {
                        MatName m = new MatName(personName + "_" + total, img);

                        String wholeFolderPath = fh.TRAINING_PATH + personName;
                        new File(wholeFolderPath).mkdirs();
                        fh.saveMatToImage(m, wholeFolderPath + "/");

                        MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[0], String.valueOf(total), front_camera);


                        total++;

                        // Stop after numberOfPictures (settings option)
                        if (total >= numberOfPictures) {
                            Intent intent = new Intent(getApplicationContext(), AddPersonActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        capturePressed = false;
                    }

                    else {
                            MatOperation.drawRectangleOnPreview(imgRgba, faces[0], front_camera);
                    }
                }
            }
        }
        return imgRgba;

    }


    @Override
    public void onResume()
    {
        super.onResume();

        ppF = new PreProcessorFactory(this);
        mAddPersonView.enableView();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAddPersonView != null)
            mAddPersonView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mAddPersonView != null)
            mAddPersonView.disableView();
    }


}
