package com.webank.mbank.animationproject;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.webank.mbank.facescan.ScanView;
import com.webank.mbank.facescan.WeBankLogger;

public class MainActivity extends AppCompatActivity {
    private ScanView mScanView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取屏幕的宽高
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        final int width = point.x;
        final int height = point.y;

        WeBankLogger.d(TAG,"width="+width);
        WeBankLogger.d(TAG,"height="+height);


        mScanView = findViewById(R.id.scan_view);
        mScanView.setCaptureRect(new Rect(10,height/15,width-10,height*3/4));

        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanView.startScan();
            }
        });

        Button stop = findViewById(R.id.stop_scan);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanView.stopScan();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScanView.stopScan();
    }
}
