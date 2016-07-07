package com.lightcone.animatordemo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class MotionRunner extends View implements Runnable {

    private Thread animator = null;      // The thread that will hold the animation
    private long delay;                  // Delay in ms controlling speed of thread looping
    private boolean please_stop = false; // Boolean controlling whether thread loop is running

    private static final int ORBIT_COLOR = Color.argb(255, 66, 66, 66);
    private static final int PLANET_COLOR = Color.argb(255, 0, 0, 0);
    private static final int SUN_COLOR = Color.argb(255, 255, 0, 0);
    private static final double RAD_CIRCLE = 2 * Math.PI;  // Number radians in a circle
    private Paint paint;                 // Paint object controlling screen draw format
    private ShapeDrawable planet;        // Planet symbol
    private int planetRadius = 7;        // Radius of spherical planet (pixels)
    private int sunRadius = 12;          // Radius of Sun (pixels)
    private float X0 = 0;                // X offset from center (pixels)
    private float Y0 = 0;                // Y offset from center (pixels)
    private float X;                     // Current X position of planet (pixels)
    private float Y;                     // Current Y position of planet (pixels)
    private float centerX;               // X for center of display (pixels)
    private float centerY;               // Y for center of display (pixels)
    private float R0;                    // Radius of circular orbit (pixels)
    private int nsteps = 600;            // Number animation steps around circle
    private double theta;                // Angle around orbit (radians)
    private double dTheta;               // Angular increment each step (radians)
    private double direction = -1;       // Direction: counter-clockwise -1; clockwise +1

    public MotionRunner(Context context) {
        super(context);
        // Initialize angle and angle step (in radians)
        theta = 0;
        dTheta = RAD_CIRCLE / ((double) nsteps);     // Angle increment in radians

        // Define the planet as circular shape
        planet = new ShapeDrawable(new OvalShape());
        planet.getPaint().setColor(PLANET_COLOR);
        planet.setBounds(0, 0, 2 * planetRadius, 2 * planetRadius);

        // Set up the Paint object that will control format of screen draws
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(14);
        paint.setStrokeWidth(1);
    }

    @Override
    public void run() {
        while (!please_stop) {

            //Log.i("ANIMATOR","  ..... LOOPED");

            // Move planet by dTheta and compute new X and Y

            newXY();

            // Must use postInvalidate() rather than invalidate() to request redraw since
            // this is invoked from different thread than the one that created the View

            postInvalidate();

            // Wait then execute it again
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                ;
            }
        }
    }

    // Method to start animation loop
    public void startIt(long delay) {
        this.delay = delay;
        animator = new Thread(this);
        animator.start();
    }

    // Method to stop animation loop
    public void stopLooper() {
        please_stop = true;
    }

    // Method to resume animation loop
    public void startLooper(long delay) {
        please_stop = false;
        if (animator == null) {
            startIt(delay);
        }
    }


/*
The View display size is only available after a certain stage of the layout.  Before then
the width and height are by default set to zero.  The onSizeChanged method of View is called
when the size is changed and its arguments give the new and old dimensions.  Thus this can be
used to get the sizes of the View after it has been laid out (or if the layout changes, as in a
switch from portrait to landscape mode, for example).
*/

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Coordinates for center of screen
        centerX = w / 2;
        centerY = h / 2;
        // Make orbital radius a fraction of minimum of width and height of display
        R0 = (float) (0.90 * Math.min(centerX, centerY));
        // Set the initial position of the planet (translate by planetRadius so center of planet
        // is at this position)
        X = centerX - planetRadius;
        Y = centerY - R0 - planetRadius;
    }

    // Method to increment theta and compute the new X and Y

    private void newXY() {
        theta += dTheta;
        if (theta > RAD_CIRCLE) theta -= RAD_CIRCLE;  // For convenience, keep angle 0-2pi
        X = (float) (R0 * Math.sin(direction * theta)) + centerX - planetRadius;
        Y = centerY - (float) (R0 * Math.cos(direction * theta)) - planetRadius;
        Log.i("ANIMATOR", "X=" + X + " Y=" + Y);
    }

    /*
This method will be called each time the screen is redrawn. The draw is
on the Canvas object, with formatting controlled by the Paint object.
When to redraw is under Android control, but we can request a redraw
using the method invalidate() or postInvalidate() inherited from the View superclass.
In this case we must use postInvalidate(), since we are updating on a thread separate
from the main UI thread.
*/

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(paint, canvas);
        canvas.save();
        canvas.translate(X + X0, Y + Y0);
        planet.draw(canvas);
        canvas.restore();
    }

    // Called by onDraw to draw the background
    private void drawBackground(Paint paint, Canvas canvas) {
        paint.setColor(SUN_COLOR);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX + X0, centerY + Y0, sunRadius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ORBIT_COLOR);
        canvas.drawCircle(centerX + X0, centerY + Y0, R0, paint);
    }
}
