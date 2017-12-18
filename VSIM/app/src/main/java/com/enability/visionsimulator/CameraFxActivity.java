package com.enability.visionsimulator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class CameraFxActivity extends Activity {

    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;
    CameraPreview mPreview;
    CameraPreview mPrev;
    Overlay mOverlay;
    SevereOverlay mSevereOverlay;
    Button btn;
    SeekBar seekBar;
    TextView textView;
    int seekBarProgress;
    int flag=0;
    // The first rear facing camera
    int defaultCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LayoutInflater inflater = getLayoutInflater();

        // Create our Preview view and set it as the content of our activity.
        // Create our DrawOnTop view.
        mOverlay = new Overlay(this);
        mSevereOverlay = new SevereOverlay(this);
        mPreview = new CameraPreview(this, mOverlay, mSevereOverlay);//Prabha this is the place it expects add additional arguement
        setContentView(mPreview);
       // mPrev = new CameraPreview(this, mOverlay, mSevereOverlay);
        //setContentView(mPrev);
        addContentView(mOverlay,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addContentView(mSevereOverlay,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addContentView(inflater.inflate(R.layout.seekbar_progress, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        Button btn = (Button)findViewById(R.id.btn);
       /* final TextView textView1 = (TextView) findViewById(R.id.textView);
        // this is the view on which you will listen for touch events
        final View touchView = findViewById(R.id.touchView);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {//prabha this is the code for ontouchevent
                textView1.setText("Touch coordinates : "
                        + String.valueOf(event.getX()) + "x"
                        + String.valueOf(event.getY()));
                return true;
            }
        });*/
        // setContentView(R.layout.seekbar_progress);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraFxActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                
            }
        });
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.count);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
              if (seekBarProgress > 50) {
                    textView.setText("Severe");
                    mCamera.setPreviewCallback(mOverlay);
                    mOverlay.setVisibility(View.VISIBLE);
                    mSevereOverlay.setVisibility(View.GONE);
                    flag = 1;
                    //mViewMode = VIEW_MODE_LEVEL2;
                } else if (seekBarProgress > 20 && seekBarProgress < 50) {
                    flag = 0;
                    textView.setText("Moderate");
                  mCamera.setPreviewCallback(mSevereOverlay);
                  mSevereOverlay.setVisibility(View.VISIBLE);
                    mOverlay.setVisibility(View.GONE);
                }else if (seekBarProgress < 20) {
                  flag = 0;
                  textView.setText("Mild");
                  mOverlay.setVisibility(View.GONE);
                  mSevereOverlay.setVisibility(View.GONE);
              }

                //       Toast.makeText(getApplicationContext(), "SeekBar Touch Stop ", Toast.LENGTH_SHORT).show();
            }

        });
        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

}