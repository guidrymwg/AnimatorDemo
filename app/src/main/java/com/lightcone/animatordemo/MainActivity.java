package com.lightcone.animatordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private long delay = 40;      // Delay in ms controlling speed of thread looping
    MotionRunner mrunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Instantiate the class MotionRunner to define the entry screen display
        mrunner = new MotionRunner(this);
        mrunner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mrunner);
        mrunner.startIt(delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop animation loop if going into background
        mrunner.stopLooper();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume animation loop
        mrunner.startLooper(delay);
    }
}
