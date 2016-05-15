/*
    This file (still experimental) is part of TraQuad-project's software, version Alpha (unstable release).

    TraQuad-project's software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraQuad-project's software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraQuad-project's software.  If not, see <http://www.gnu.org/licenses/>.

    Additional term: Clause 7(b) of GPLv3. Attribution is (even more) necessary if these (TraQuad-project's) softwares are distributed commercially.
    Date of creation: February 2016 - June 2016 and Attribution: Prasad N R as a representative of (unregistered) company TraQuad.
 */

package com.example.prasadnr.traquad;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProJoypad extends AppCompatActivity {

    //This is still under development

    int arenaLength, smallStickLength;  //75 and 338 although it is 50 and 225 in terms of orignal pixel length of original drawable
    int XrightMid, YrightMid, xRightMid, yRightMid, XleftMid, YleftMid, xLeftMid, yLeftMid;
    int xOriginRight, yOriginRight, xOriginLeft, yOriginLeft;
    float pitch, roll, yaw, throttle;

    final String TAG = "Traquad";

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN: {
                int fingerLeftX = (int) event.getX(0);
                int fingerLeftY = (int) event.getY(0);
                Toast.makeText(this, "First: (" + fingerLeftX + "," + fingerLeftY + ")", Toast.LENGTH_SHORT).show();
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                int fingerRightX = (int) event.getX(1);
                int fingerRightY = (int) event.getY(1);
                Toast.makeText(this, "Second: (" + fingerRightX + "," + fingerRightY + ")", Toast.LENGTH_SHORT).show();
                break;
            }

            case MotionEvent.ACTION_MOVE: { // a pointer was moved

                break;
            }

            case MotionEvent.ACTION_UP: {
                int fingerLeftX = (int) event.getX(0);
                int fingerLeftY = (int) event.getY(0);
                Toast.makeText(this, "First: (" + fingerLeftX + "," + fingerLeftY + ")", Toast.LENGTH_SHORT).show();
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int fingerRightX = (int) event.getX(1);
                int fingerRightY = (int) event.getY(1);
                Toast.makeText(this, "Second: (" + fingerRightX + "," + fingerRightY + ")", Toast.LENGTH_SHORT).show();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                pointerId = -1;
                break;
            }

        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_joypad);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        SensorManager sensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        final ImageView arenaLeft = (ImageView) findViewById(R.id.arenaLeft);
        final ImageView arenaRight = (ImageView) findViewById(R.id.arenaRight);

        ImageView joystickLeft = (ImageView) findViewById(R.id.joystickLeft);
        ImageView joystickRight = (ImageView) findViewById(R.id.joystickRight);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int widthScreen = (int) size.x;
        final int heightScreen = (int) size.y;
        TypedValue typedValue = new TypedValue();

        final int pitchRangeAccelerometer = (int) sensor.getMaximumRange(); //range = 19
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
        }

        final int finalActionBarHeight = actionBarHeight;
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double total = Math.sqrt(x * x + y * y + z * z);

                pitch = x;
                roll = y;

                try {
                    ImageView joystickLeft = (ImageView) findViewById(R.id.joystickLeft);
                    smallStickLength = joystickLeft.getWidth();
                    arenaLength = arenaLeft.getWidth();

                    RelativeLayout.LayoutParams position = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    XleftMid = (arenaLength)/2;
                    xLeftMid = (smallStickLength)/2;
                    xOriginLeft = XleftMid - xLeftMid;
                    YleftMid = heightScreen - (arenaLength)/2;
                    yLeftMid = (smallStickLength)/2;
                    yOriginLeft = YleftMid - finalActionBarHeight;//It is considering top tab margin also; It is using direct centre
                    //Repeat the same fuck for right side stick

                    Log.e(TAG, String.valueOf(yOriginLeft));

                    position.topMargin = (int) ((int) 25*pitch) + yOriginLeft;//pitch
                    position.leftMargin = (int) ((int) 25*roll) + xOriginLeft;//roll
                    joystickLeft.setLayoutParams(position);
                }catch(Exception e){
                    Log.e(TAG, "Accelerometer is not working!");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!
        final ImageView arenaLeft = (ImageView) findViewById(R.id.arenaLeft);
        arenaLength = arenaLeft.getMaxHeight();
        Toast.makeText(ProJoypad.this, "" + arenaLength, Toast.LENGTH_SHORT).show();
        return;
    }*/

}