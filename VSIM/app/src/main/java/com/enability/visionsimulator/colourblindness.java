package com.enability.visionsimulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.randn;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by enability on 15/9/16.
 */
public class colourblindness extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String    TAG = "OCVSample::Activity";

    private static final int       VIEW_MODE_LEVEL0     = 0;
    private static final int       VIEW_MODE_LEVEL1     = 1;
    private static final int       VIEW_MODE_LEVEL2     = 2;
    private static final int       VIEW_MODE_LEVEL3     = 3;
    private static final int       VIEW_MODE_LEVEL4     = 4;

    private int                    mViewMode;
    private Mat mRgba;
    private Mat mGray;
    int flag=0;

    private MenuItem mItem0;
    private MenuItem               mItem1;
    private MenuItem               mItem2;
    private MenuItem               mItem3;
    private MenuItem               mItem4;
    private MenuItem               mItem5;
    private MenuItem               mItem6;
    private Bitmap bMap_darkveil;
    private Mat					   mDarkveil;
    private int					   frameCount;
    private long				   startTime;
    private int					   enableDebug;
    private int					   showRings;
    private int					    enableMacularEdema;
    private int					   enableMobilityMode;
    private int					   enableContrastSensitivity;
    private int[]				   blurValues;
    private float[]				   blurRadii;
    private CameraBridgeViewBase mOpenCvCameraView;
    private int					   callInit;

    SeekBar seekBar;
    TextView textView;
    int seekBarProgress = 0;
    Button btn;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("jnicataract_lib");

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public colourblindness() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // hideNavigation();

        setContentView(R.layout.main_surface);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_camera);
        //  mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.seekbar_progress, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));



        Button btn = (Button)findViewById(R.id.btn);

        // setContentView(R.layout.seekbar_progress);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(colourblindness.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {//Prabha to check here 4thdec 17
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "loading opencv ", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            Toast.makeText(getApplicationContext(), "Not loading opencv ", Toast.LENGTH_SHORT).show();
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        //    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            Toast.makeText(getApplicationContext(), "destroy ", Toast.LENGTH_SHORT).show();
            mOpenCvCameraView.disableView();
        }
    }

    public void onCameraViewStarted(int width, int height) {

        //   mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgba = new Mat();
        mGray = new Mat();
        //    Toast.makeText(this, "Diabetic Retinopathy Simulator v1.0", Toast.LENGTH_SHORT).show();
        bMap_darkveil= BitmapFactory.decodeResource(getResources(), R.drawable.darkveil);
        mDarkveil = new Mat(bMap_darkveil.getHeight(), bMap_darkveil.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(bMap_darkveil, mDarkveil);
        frameCount = 0;
        enableDebug = 0;
        showRings = 0;
        enableMacularEdema = 1;
        enableMobilityMode = 1;
        enableContrastSensitivity = 0;
        blurValues = new int[]{1,4,9,14,20};
        blurRadii = new float[]{0.0f, 0.1f, 0.3f, 0.55f, 0.9f};
        startTime = System.currentTimeMillis();
        callInit = 2;

    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        textView = (TextView) findViewById(R.id.count);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                 if (seekBarProgress > 50){
                    textView.setText("Severe");
                    flag =1;
                    //mViewMode = VIEW_MODE_LEVEL2;
                }else if (seekBarProgress < 50){
                    flag=0;
                    mViewMode = VIEW_MODE_LEVEL0;
                    textView.setText("Mild");
                }
                //       Toast.makeText(getApplicationContext(), "SeekBar Touch Stop ", Toast.LENGTH_SHORT).show();
            }

        });
        if(flag == 1){
            mRgba = inputFrame.rgba();//prabha here i changed it for cataract severe condition,it is no more going to c++ file
            Imgproc.blur(mRgba, mRgba, new org.opencv.core.Size(12, 12));
            Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY);
            //return mRgba;
            return mGray;


        }else {
            mRgba = inputFrame.rgba();//prabha here i changed it for cataract severe condition,it is no more going to c++ file
           /* Mat src;
            int w;
            int h;
            Rect rect2(0, 0, w/2, h);*/
            final int viewMode = mViewMode;
            mRgba = inputFrame.rgba();
            Log.i(TAG, "Processed frame " + frameCount);
           // FindFeaturesCataract(mRgba.getNativeObjAddr(), mDarkveil.getNativeObjAddr(), (int) viewMode, (int) showRings, (int) enableMacularEdema, (int) enableMobilityMode, (int) enableContrastSensitivity, (int[]) blurValues, (float[]) blurRadii, (int) callInit, (int) enableDebug);
            ++frameCount;
            if (callInit > 0) callInit = callInit - 1;
            return mRgba;
        }
    }


 //   public native void FindFeaturesCataract(long matAddrRgba, long matAddrDarkveil, int viewMode, int showRings, int enableMacularEdema, int enableMobilityMode, int enableContrastSensitivity, int[] blurValues, float[] blurRadii, int callInit, int enableDebug);


}
