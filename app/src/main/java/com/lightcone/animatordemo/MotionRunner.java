package com.lightcone.animatordemo;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class MotionRunner extends View implements Runnable {

    private Thread animator = null;      // The thread that will hold the animation
    private long delay;                  // Delay in ms controlling speed of thread looping
    private boolean please_stop = false; // Boolean controlling whether thread loop is running

    public MotionRunner(Context context) {
        super(context);
    }

    @Override
    public void run() {
        while(!please_stop) {
            Log.i("ANIMATOR","  ..... LOOPED");
            // Wait then execute it again
            try { Thread.sleep(delay); } catch (InterruptedException e) { ; }
        }
    }

    // Method to start animation loop
    public void startIt(long delay) {
        this.delay = delay;
        animator = new Thread(this);
        animator.start();
    }

    // Method to stop animation loop
    public void stopLooper(){
        please_stop = true;
    }

    // Method to resume animation loop
    public void startLooper(long delay){
        please_stop = false;
        if(animator == null) {
            startIt(delay);
        }
    }
}
