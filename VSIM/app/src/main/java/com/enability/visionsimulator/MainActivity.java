package com.enability.visionsimulator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    TextView tvCat, tvCS, tvCB,tvDR,tvROP,tvRCD,tvMY,tvAs;
    Button btnCat,btnCS,btnCB,btnDR,btnROP,btnRCD,btnMY,btnAs;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hideNavigation();
        tvCat = (TextView)findViewById(R.id.tvCataract);
        tvCS = (TextView)findViewById(R.id.tvCornea);
        tvCB = (TextView)findViewById(R.id.tvColourBlind);
        tvDR = (TextView)findViewById(R.id.tvDR);
        tvROP = (TextView)findViewById(R.id.tvROP);
        tvRCD = (TextView)findViewById(R.id.tvRCD);
        tvMY = (TextView)findViewById(R.id.tvMyopia);
        tvAs = (TextView)findViewById(R.id.tvAstigm);

        btnCat = (Button)findViewById(R.id.btnCataract);
        btnCS = (Button)findViewById(R.id.btnCornea);
        btnCB = (Button)findViewById(R.id.btnColourBlind);
        btnDR = (Button)findViewById(R.id.btnDR);
        btnROP = (Button)findViewById(R.id.btnROP);
        btnRCD = (Button)findViewById(R.id.btnRCD);
        btnMY = (Button)findViewById(R.id.btnMyopia);
        btnAs = (Button)findViewById(R.id.btnAstigm);
       /* btnDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, diabeticretinopathysim.class);
                startActivity(intent);
                finish();
            }
        });*/

       /* btnCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, colourblindness.class);
                startActivity(intent);
                finish();
            }
        });
*/
        btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cataract.class);
                startActivity(intent);
                finish();
            }
        });

        btnAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, astigmatism.class);
                startActivity(intent);
                finish();
            }
        });
/*
        btnCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ushers.class);
                startActivity(intent);
                finish();
            }
        });

        btnROP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, retinopathyprematurity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, rodcone.class);
                startActivity(intent);
                finish();
            }
        });*/

        btnMY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraFxActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
