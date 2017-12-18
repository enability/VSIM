package com.enability.visionsimulator;
/**
 * Created by ennability on 18/7/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class astigmatism extends Activity {
    Button btn;
    SeekBar seekBar;
    TextView textView;
    int seekBarProgress;
    int flag=0;
    float Blur_radius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.astigmatism);
        final ImageView imageview =(ImageView) findViewById(R.id.originalImage);
        final ImageView resultImage = (ImageView) findViewById(R.id.resultImage);
        final ImageView resultImag = (ImageView) findViewById(R.id.resultImag);

        LayoutInflater inflater = getLayoutInflater();

        addContentView(inflater.inflate(R.layout.seekbar_progress, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        Button btn = (Button)findViewById(R.id.btn);

        // setContentView(R.layout.seekbar_progress);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(astigmatism.this, MainActivity.class);
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
                if (seekBarProgress > 70) {
                    textView.setTextSize(20);
                    textView.setTextColor(Color.BLACK);
                    textView.setText("VERTICAL");
                    resultImage.setVisibility(View.GONE);
                    resultImag.setVisibility(View.VISIBLE);
                    //  Bitmap resultBmp = BlurBuilder.blur(MainActivity.this, BitmapFactory.decodeResource(getResources(), R.drawable.sample), 18f);
                    // resultImage.setImageBitmap(resultBmp);
                    //   mCamera.setPreviewCallback(mOverlay);
                    //     mOverlay.setVisibility(View.VISIBLE);
                    flag = 1;
                    //mViewMode = VIEW_MODE_LEVEL2;
                } else if (seekBarProgress > 30 && seekBarProgress< 70) {
                    imageview.setVisibility(View.GONE);
                    resultImag.setVisibility(View.GONE);
                    resultImage.setVisibility(View.VISIBLE);
                    // Bitmap resultBmp = BlurBuilder.blur(MainActivity.this, BitmapFactory.decodeResource(getResources(), R.drawable.sample), 4f);
                    //resultImage.setImageBitmap(resultBmp);
                    flag = 0;
                    textView.setTextSize(20);
                    textView.setTextColor(Color.BLACK);
                    textView.setText("120");
                    //    mOverlay.setVisibility(View.GONE);
                }else if (seekBarProgress >0 && seekBarProgress < 30) {
                    resultImage.setVisibility(View.GONE);
                    resultImag.setVisibility(View.GONE);
                    imageview.setVisibility(View.VISIBLE);

                    textView.setTextSize(20);
                    textView.setTextColor(Color.BLACK);
                    textView.setText("HORIZONTAL");

                    //    mOverlay.setVisibility(View.GONE);
                }

                //       Toast.makeText(getApplicationContext(), "SeekBar Touch Stop ", Toast.LENGTH_SHORT).show();
            }

        });
    } }

