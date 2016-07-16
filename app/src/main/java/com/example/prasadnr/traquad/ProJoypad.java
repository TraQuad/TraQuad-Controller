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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
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

    boolean irritation = false; //A boolean variable which causes annoyance in users if it is not included
    final String TAG = "Traquad";

    boolean isWifiAPenabled = false;

    private BluetoothAdapter btAdapter = null;
    private OutputStream outStream = null;

    private static String address = "20:14:12:03:11:24";

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN: {
                ImageView joystickLeft = (ImageView) findViewById(R.id.joystickLeft);
                smallStickLength = joystickLeft.getWidth();
                final ImageView arenaLeft = (ImageView) findViewById(R.id.arenaLeft);
                arenaLength = arenaLeft.getWidth();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                final int widthScreen = (int) size.x;
                final int heightScreen = (int) size.y;
                TypedValue typedValue = new TypedValue();
                int actionBarHeight = 0;

                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
                {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
                }

                int statusBarHeight = 0;
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                final int finalActionBarHeight = actionBarHeight;
                final int finalStatusBarHeight = statusBarHeight;

                RelativeLayout.LayoutParams positionLeft = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XleftMid = (arenaLength)/2;
                xLeftMid = (smallStickLength)/2;
                YleftMid = heightScreen - (arenaLength)/2;
                yLeftMid = (smallStickLength)/2;

                int firstFingerX = (int) event.getX(0);
                int firstFingerY = (int) event.getY(0);

                int fingerLeftX, fingerLeftY;
                int topBound = heightScreen - arenaLength + xLeftMid;
                int bottomBound = heightScreen - xLeftMid;
                int leftBoundLEFT = xLeftMid;
                int rightBoundLEFT = arenaLength - xLeftMid;

                int flagLeftFirst = 0;
                int flagRightFirst = 0;
                if((firstFingerY<topBound)|(firstFingerY>bottomBound)|(firstFingerX<leftBoundLEFT)|(firstFingerX>rightBoundLEFT)){
                    flagLeftFirst = 1;
                }

                fingerLeftX = firstFingerX;
                fingerLeftY = firstFingerY;
                if(flagLeftFirst==0) {
                    positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                    positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                    joystickLeft.setLayoutParams(positionLeft);
                }

                ImageView joystickRight = (ImageView) findViewById(R.id.joystickRight);
                smallStickLength = joystickRight.getWidth();
                final ImageView arenaRight = (ImageView) findViewById(R.id.arenaRight);
                arenaLength = arenaRight.getWidth();

                RelativeLayout.LayoutParams positionRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XrightMid = widthScreen - (arenaLength)/2;
                xRightMid = (smallStickLength)/2;
                YrightMid = heightScreen - (arenaLength)/2;
                yRightMid = (smallStickLength)/2;

                int rightBoundRIGHT = widthScreen - xRightMid;
                int leftBoundRIGHT = widthScreen - arenaLength + xRightMid;

                if((firstFingerY<topBound)|(firstFingerY>bottomBound)|(firstFingerX<leftBoundRIGHT)|(firstFingerX>rightBoundRIGHT)){
                    flagRightFirst = 1;
                }

                int fingerRightX = firstFingerX;
                int fingerRightY = firstFingerY;

                if(flagRightFirst==0) {
                    positionRight.topMargin = (int) ((int) fingerRightY - yRightMid - finalStatusBarHeight);//throttle
                    positionRight.leftMargin = (int) ((int) fingerRightX - xRightMid);//roll
                    joystickRight.setLayoutParams(positionRight);
                }

                try{
                    int secondFingerX = (int) event.getX(1);
                    int secondFingerY = (int) event.getY(1);

                    fingerRightX = secondFingerX;
                    fingerRightY = secondFingerY;
                    fingerLeftX = secondFingerX;
                    fingerLeftY = secondFingerY;

                    int flagLeftSecond = 0;
                    int flagRightSecond = 0;
                    if((secondFingerY<topBound)|(secondFingerY>bottomBound)|(secondFingerX<leftBoundLEFT)|(secondFingerX>rightBoundLEFT)){
                        flagLeftSecond = 1;
                    }

                    if(flagLeftSecond==0) {
                        positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                        positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                        joystickLeft.setLayoutParams(positionLeft);
                    }

                    if((secondFingerY<topBound)|(secondFingerY>bottomBound)|(secondFingerX<leftBoundRIGHT)|(secondFingerX>rightBoundRIGHT)){
                        flagRightSecond = 1;
                    }

                    if(flagRightSecond==0) {
                        positionRight.topMargin = (int) ((int) fingerRightY - yRightMid - finalStatusBarHeight);//throttle
                        positionRight.leftMargin = (int) ((int) fingerRightX - xRightMid);//roll
                        joystickRight.setLayoutParams(positionRight);
                    }

                }catch(Exception e){}

                try {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    BluetoothSocket socket = globalVariable.getBluetoothSocket();
                    int xLeftCorner = joystickLeft.getLeft();
                    int yLeftCorner = joystickLeft.getTop();
                    int xRightCorner = joystickRight.getLeft();
                    int yRightCorner = joystickRight.getTop();
                    int totalRangeLength = arenaLength - smallStickLength;

                    int radioMode = globalVariable.getRadioMode();

                        int yawPWM = 1000 + (1000 * xLeftCorner) / totalRangeLength;
                        int pitchPWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yLeftCorner)) / totalRangeLength;
                        int rollPWM = 1000 + (1000 * (xRightCorner - (widthScreen - arenaLength))) / totalRangeLength;
                        int throttlePWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yRightCorner)) / totalRangeLength;

                    GlobalClass globalClass = (GlobalClass) getApplicationContext();
                    if(globalClass.getLeftInversion()==1)
                    {
                        pitchPWM = 3000 - pitchPWM;
                    }
                    if(globalClass.getRightInversion()==1)
                    {
                        throttlePWM = 3000 - throttlePWM;
                    }

                    if(radioMode==2) {
                        yawPWM = 1000 + (1000 * xLeftCorner) / totalRangeLength;
                        throttlePWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yLeftCorner)) / totalRangeLength;
                        rollPWM = 1000 + (1000 * (xRightCorner - (widthScreen - arenaLength))) / totalRangeLength;
                        pitchPWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yRightCorner)) / totalRangeLength;

                        if(globalClass.getLeftInversion()==1)
                        {
                            throttlePWM = 3000 - throttlePWM;
                        }
                        if(globalClass.getRightInversion()==1)
                        {
                            pitchPWM = 3000 - pitchPWM;
                        }
                    }

                    String throttleString = String.valueOf(throttlePWM);
                    String pitchString = String.valueOf(pitchPWM);
                    String yawString = String.valueOf(yawPWM);
                    String rollString = String.valueOf(rollPWM);

                    String sendString = throttleString + pitchString + yawString + rollString;
                    sendMessage(socket, sendString);
                    Log.e(TAG, sendString);
                }catch(Exception e){}

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                ImageView joystickLeft = (ImageView) findViewById(R.id.joystickLeft);
                smallStickLength = joystickLeft.getWidth();
                final ImageView arenaLeft = (ImageView) findViewById(R.id.arenaLeft);
                arenaLength = arenaLeft.getWidth();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                final int widthScreen = (int) size.x;
                final int heightScreen = (int) size.y;
                TypedValue typedValue = new TypedValue();
                int actionBarHeight = 0;

                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
                {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
                }

                int statusBarHeight = 0;
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                final int finalActionBarHeight = actionBarHeight;
                final int finalStatusBarHeight = statusBarHeight;

                RelativeLayout.LayoutParams positionLeft = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XleftMid = (arenaLength)/2;
                xLeftMid = (smallStickLength)/2;
                YleftMid = heightScreen - (arenaLength)/2;
                yLeftMid = (smallStickLength)/2;

                int firstFingerX = (int) event.getX(0);
                int firstFingerY = (int) event.getY(0);

                int fingerLeftX, fingerLeftY;
                int topBound = heightScreen - arenaLength + xLeftMid;
                int bottomBound = heightScreen - xLeftMid;
                int leftBoundLEFT = xLeftMid;
                int rightBoundLEFT = arenaLength - xLeftMid;

                int flagLeftFirst = 0;
                int flagRightFirst = 0;
                if((firstFingerY<topBound)|(firstFingerY>bottomBound)|(firstFingerX<leftBoundLEFT)|(firstFingerX>rightBoundLEFT)){
                    flagLeftFirst = 1;
                }

                fingerLeftX = firstFingerX;
                fingerLeftY = firstFingerY;
                if(flagLeftFirst==0) {
                    positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                    positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                    joystickLeft.setLayoutParams(positionLeft);
                }

                ImageView joystickRight = (ImageView) findViewById(R.id.joystickRight);
                smallStickLength = joystickRight.getWidth();
                final ImageView arenaRight = (ImageView) findViewById(R.id.arenaRight);
                arenaLength = arenaRight.getWidth();

                RelativeLayout.LayoutParams positionRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XrightMid = widthScreen - (arenaLength)/2;
                xRightMid = (smallStickLength)/2;
                YrightMid = heightScreen - (arenaLength)/2;
                yRightMid = (smallStickLength)/2;

                int rightBoundRIGHT = widthScreen - xRightMid;
                int leftBoundRIGHT = widthScreen - arenaLength + xRightMid;

                if((firstFingerY<topBound)|(firstFingerY>bottomBound)|(firstFingerX<leftBoundRIGHT)|(firstFingerX>rightBoundRIGHT)){
                    flagRightFirst = 1;
                }

                int fingerRightX = firstFingerX;
                int fingerRightY = firstFingerY;

                if(flagRightFirst==0) {
                    positionRight.topMargin = (int) ((int) fingerRightY - yRightMid - finalStatusBarHeight);//throttle
                    positionRight.leftMargin = (int) ((int) fingerRightX - xRightMid);//roll
                    joystickRight.setLayoutParams(positionRight);
                }

                try{
                    int secondFingerX = (int) event.getX(1);
                    int secondFingerY = (int) event.getY(1);

                    fingerRightX = secondFingerX;
                    fingerRightY = secondFingerY;
                    fingerLeftX = secondFingerX;
                    fingerLeftY = secondFingerY;

                    int flagLeftSecond = 0;
                    int flagRightSecond = 0;
                    if((secondFingerY<topBound)|(secondFingerY>bottomBound)|(secondFingerX<leftBoundLEFT)|(secondFingerX>rightBoundLEFT)){
                        flagLeftSecond = 1;
                    }

                    if(flagLeftSecond==0) {
                        positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                        positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                        joystickLeft.setLayoutParams(positionLeft);
                    }

                    if((secondFingerY<topBound)|(secondFingerY>bottomBound)|(secondFingerX<leftBoundRIGHT)|(secondFingerX>rightBoundRIGHT)){
                        flagRightSecond = 1;
                    }

                    if(flagRightSecond==0) {
                        positionRight.topMargin = (int) ((int) fingerRightY - yRightMid - finalStatusBarHeight);//throttle
                        positionRight.leftMargin = (int) ((int) fingerRightX - xRightMid);//roll
                        joystickRight.setLayoutParams(positionRight);
                    }

                }catch(Exception e){}

                try {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    BluetoothSocket socket = globalVariable.getBluetoothSocket();
                    int xLeftCorner = joystickLeft.getLeft();
                    int yLeftCorner = joystickLeft.getTop();
                    int xRightCorner = joystickRight.getLeft();
                    int yRightCorner = joystickRight.getTop();
                    int totalRangeLength = arenaLength - smallStickLength;

                    int radioMode = globalVariable.getRadioMode();

                    int yawPWM = 1000 + (1000 * xLeftCorner) / totalRangeLength;
                    int pitchPWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yLeftCorner)) / totalRangeLength;
                    int rollPWM = 1000 + (1000 * (xRightCorner - (widthScreen - arenaLength))) / totalRangeLength;
                    int throttlePWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yRightCorner)) / totalRangeLength;

                    GlobalClass globalClass = (GlobalClass) getApplicationContext();
                    if(globalClass.getLeftInversion()==1)
                    {
                        pitchPWM = 3000 - pitchPWM;
                    }
                    if(globalClass.getRightInversion()==1)
                    {
                        throttlePWM = 3000 - throttlePWM;
                    }

                    if(radioMode==2) {
                        yawPWM = 1000 + (1000 * xLeftCorner) / totalRangeLength;
                        throttlePWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yLeftCorner)) / totalRangeLength;
                        rollPWM = 1000 + (1000 * (xRightCorner - (widthScreen - arenaLength))) / totalRangeLength;
                        pitchPWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yRightCorner)) / totalRangeLength;

                        if(globalClass.getLeftInversion()==1)
                        {
                            throttlePWM = 3000 - throttlePWM;
                        }
                        if(globalClass.getRightInversion()==1)
                        {
                            pitchPWM = 3000 - pitchPWM;
                        }
                    }

                    String throttleString = String.valueOf(throttlePWM);
                    String pitchString = String.valueOf(pitchPWM);
                    String yawString = String.valueOf(yawPWM);
                    String rollString = String.valueOf(rollPWM);

                    String sendString = throttleString + pitchString + yawString + rollString;
                    sendMessage(socket, sendString);
                    Log.e(TAG, sendString);
                }catch(Exception e){}

                break;
            }

            case MotionEvent.ACTION_MOVE:{
                ImageView joystickLeft = (ImageView) findViewById(R.id.joystickLeft);
                smallStickLength = joystickLeft.getWidth();
                final ImageView arenaLeft = (ImageView) findViewById(R.id.arenaLeft);
                arenaLength = arenaLeft.getWidth();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                final int widthScreen = (int) size.x;
                final int heightScreen = (int) size.y;
                TypedValue typedValue = new TypedValue();
                int actionBarHeight = 0;

                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
                {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
                }

                int statusBarHeight = 0;
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                final int finalActionBarHeight = actionBarHeight;
                final int finalStatusBarHeight = statusBarHeight;

                RelativeLayout.LayoutParams positionLeft = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XleftMid = (arenaLength)/2;
                xLeftMid = (smallStickLength)/2;
                YleftMid = heightScreen - (arenaLength)/2;
                yLeftMid = (smallStickLength)/2;

                int firstFingerX = (int) event.getX(0);
                int firstFingerY = (int) event.getY(0);

                int fingerLeftX, fingerLeftY;
                int topBound = heightScreen - arenaLength + xLeftMid;
                int bottomBound = heightScreen - xLeftMid;
                int leftBoundLEFT = xLeftMid;
                int rightBoundLEFT = arenaLength - xLeftMid;

                int flagLeftFirst = 0;
                int flagRightFirst = 0;
                if((firstFingerY<topBound)|(firstFingerY>bottomBound)|(firstFingerX<leftBoundLEFT)|(firstFingerX>rightBoundLEFT)){
                    flagLeftFirst = 1;
                }

                fingerLeftX = firstFingerX;
                fingerLeftY = firstFingerY;
                if(flagLeftFirst==0) {
                    positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                    positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                    joystickLeft.setLayoutParams(positionLeft);
                }

                ImageView joystickRight = (ImageView) findViewById(R.id.joystickRight);
                smallStickLength = joystickRight.getWidth();
                final ImageView arenaRight = (ImageView) findViewById(R.id.arenaRight);
                arenaLength = arenaRight.getWidth();

                RelativeLayout.LayoutParams positionRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XrightMid = widthScreen - (arenaLength)/2;
                xRightMid = (smallStickLength)/2;
                YrightMid = heightScreen - (arenaLength)/2;
                yRightMid = (smallStickLength)/2;

                int rightBoundRIGHT = widthScreen - xRightMid;
                int leftBoundRIGHT = widthScreen - arenaLength + xRightMid;

                if((firstFingerY<topBound)|(firstFingerY>bottomBound)|(firstFingerX<leftBoundRIGHT)|(firstFingerX>rightBoundRIGHT)){
                    flagRightFirst = 1;
                }

                int fingerRightX = firstFingerX;
                int fingerRightY = firstFingerY;

                if(flagRightFirst==0) {
                    positionRight.topMargin = (int) ((int) fingerRightY - yRightMid - finalStatusBarHeight);//throttle
                    positionRight.leftMargin = (int) ((int) fingerRightX - xRightMid);//roll
                    joystickRight.setLayoutParams(positionRight);
                }

                try{
                    int secondFingerX = (int) event.getX(1);
                    int secondFingerY = (int) event.getY(1);

                    fingerRightX = secondFingerX;
                    fingerRightY = secondFingerY;
                    fingerLeftX = secondFingerX;
                    fingerLeftY = secondFingerY;

                    int flagLeftSecond = 0;
                    int flagRightSecond = 0;
                    if((secondFingerY<topBound)|(secondFingerY>bottomBound)|(secondFingerX<leftBoundLEFT)|(secondFingerX>rightBoundLEFT)){
                        flagLeftSecond = 1;
                    }

                    if(flagLeftSecond==0) {
                        positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                        positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                        joystickLeft.setLayoutParams(positionLeft);
                    }

                    if((secondFingerY<topBound)|(secondFingerY>bottomBound)|(secondFingerX<leftBoundRIGHT)|(secondFingerX>rightBoundRIGHT)){
                        flagRightSecond = 1;
                    }

                    if(flagRightSecond==0) {
                        positionRight.topMargin = (int) ((int) fingerRightY - yRightMid - finalStatusBarHeight);//throttle
                        positionRight.leftMargin = (int) ((int) fingerRightX - xRightMid);//roll
                        joystickRight.setLayoutParams(positionRight);
                    }

                }catch(Exception e){}

                try {
                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    BluetoothSocket socket = globalVariable.getBluetoothSocket();
                    int xLeftCorner = joystickLeft.getLeft();
                    int yLeftCorner = joystickLeft.getTop();
                    int xRightCorner = joystickRight.getLeft();
                    int yRightCorner = joystickRight.getTop();
                    int totalRangeLength = arenaLength - smallStickLength;

                    int radioMode = globalVariable.getRadioMode();

                    int yawPWM = 1000 + (1000 * xLeftCorner) / totalRangeLength;
                    int pitchPWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yLeftCorner)) / totalRangeLength;
                    int rollPWM = 1000 + (1000 * (xRightCorner - (widthScreen - arenaLength))) / totalRangeLength;
                    int throttlePWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yRightCorner)) / totalRangeLength;

                    GlobalClass globalClass = (GlobalClass) getApplicationContext();
                    if(globalClass.getLeftInversion()==1)
                    {
                        pitchPWM = 3000 - pitchPWM;
                    }
                    if(globalClass.getRightInversion()==1)
                    {
                        throttlePWM = 3000 - throttlePWM;
                    }

                    if(radioMode==2) {
                        yawPWM = 1000 + (1000 * xLeftCorner) / totalRangeLength;
                        throttlePWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yLeftCorner)) / totalRangeLength;
                        rollPWM = 1000 + (1000 * (xRightCorner - (widthScreen - arenaLength))) / totalRangeLength;
                        pitchPWM = 1000 + (1000 * ((heightScreen - smallStickLength - statusBarHeight) - yRightCorner)) / totalRangeLength;

                        if(globalClass.getLeftInversion()==1)
                        {
                            throttlePWM = 3000 - throttlePWM;
                        }
                        if(globalClass.getRightInversion()==1)
                        {
                            pitchPWM = 3000 - pitchPWM;
                        }
                    }

                    String throttleString = String.valueOf(throttlePWM);
                    String pitchString = String.valueOf(pitchPWM);
                    String yawString = String.valueOf(yawPWM);
                    String rollString = String.valueOf(rollPWM);

                    String sendString = throttleString + pitchString + yawString + rollString;
                    sendMessage(socket, sendString);
                    Log.e(TAG, sendString);
                }catch(Exception e){}

                break;
            }

            case MotionEvent.ACTION_UP: {
                int fingerLeftX = (int) event.getX(0);
                int fingerLeftY = (int) event.getY(0);
                //Toast.makeText(this, "First: (" + fingerLeftX + "," + fingerLeftY + ")", Toast.LENGTH_SHORT).show();
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int fingerRightX = (int) event.getX(1);
                int fingerRightY = (int) event.getY(1);
                //Toast.makeText(this, "Second: (" + fingerRightX + "," + fingerRightY + ")", Toast.LENGTH_SHORT).show();
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
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        final int finalStatusBarHeight = statusBarHeight;

        try {
            while(joystickLeft.getLeft()<0 &&joystickLeft.getLeft()>widthScreen) {
                smallStickLength = joystickLeft.getWidth();
                arenaLength = arenaLeft.getWidth();

                RelativeLayout.LayoutParams positionLeft = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                XleftMid = (arenaLength) / 2;
                xLeftMid = (smallStickLength) / 2;

                YleftMid = heightScreen - (arenaLength) / 2;
                yLeftMid = (smallStickLength) / 2;

                int fingerLeftX = (arenaLength) / 2;
                int fingerLeftY = YleftMid;

                Toast.makeText(ProJoypad.this, fingerLeftY, Toast.LENGTH_SHORT).show();

                positionLeft.topMargin = (int) ((int) fingerLeftY - yLeftMid - finalStatusBarHeight);//pitch
                positionLeft.leftMargin = (int) ((int) fingerLeftX - xLeftMid);//yaw
                joystickLeft.setLayoutParams(positionLeft);
            }
        }catch (Exception e){}

        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Joypad.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final ProgressDialog pDialog;
            MediaController mediaController = new MediaController(this);
            String buff = "Buffering... Please wait...";
            BluetoothAdapter btAdapter = null;
            TextView mLabel;
            EditText mDevice;
            BluetoothSocket btSocket = null;

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "Please enable critical-low-bandwidth bluetooth connection! (Pair it with HC05)", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Please enable critical-low-bandwidth bluetooth connection! (Pair it with HC05)", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            }

            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            BluetoothDevice btDevice = mBluetoothAdapter.getRemoteDevice(address);
            final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();
                Toast.makeText(getApplicationContext(), "Bluetooth has been connected!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Please connect bluetooth properly!", Toast.LENGTH_LONG).show();
            }

            WifiManager managerWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            Method[] wmMethods = managerWifi.getClass().getDeclaredMethods();
            for (Method method : wmMethods) {
                if (method.getName().equals("isWifiApEnabled")) {

                    try {
                        isWifiAPenabled = (boolean) method.invoke(managerWifi);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            WebView webView = (WebView) findViewById(R.id.webView2);
            pDialog = new ProgressDialog(ProJoypad.this);
            pDialog.setTitle("TraQuad app (Connecting...)");
            pDialog.setMessage("Buffering...Please wait...");
            pDialog.setCancelable(true);
            AlertDialog.Builder alert = new AlertDialog.Builder(ProJoypad.this);
            if (!isWifiAPenabled) {

                alert.setTitle("WiFi Hotspot Settings");
                alert.setMessage("Can you please connect WiFi-hotspot?");
                alert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                irritation = true;
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                pDialog.show();
                            }
                        });
                alert.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Dismiss AlertDialog
                                pDialog.show();
                                Toast.makeText(getApplicationContext(), "Please connect your WiFi!", Toast.LENGTH_LONG).show();
                            }
                        });
                alert.setCancelable(true);
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }

            if (irritation == true | isWifiAPenabled) {
                pDialog.show();
            }

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);

            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            final String IPaddressNew = globalVariable.getIP();
            final String httpString = "http://";
            final String commandPort = String.valueOf(1500);
            final String streamPort = String.valueOf(8080);
            final String IPaddressStream = httpString + IPaddressNew + ":" + streamPort;
            final String IPaddressCommand = httpString + IPaddressNew + ":" + commandPort;

            try {
                webView.loadUrl(IPaddressStream);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), IPaddressNew + ":Error!", Toast.LENGTH_LONG).show();
            }

            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    pDialog.dismiss();
                }
            });

            Button leftVerticalInversion = (Button) findViewById(R.id.left);
            Button rightVerticalInversion = (Button) findViewById(R.id.right);

            leftVerticalInversion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalClass globalClass = (GlobalClass) getApplicationContext();
                    globalClass.toggleLeftInversion();
                }
            });

            rightVerticalInversion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalClass globalClass = (GlobalClass) getApplicationContext();
                    globalClass.toggleRightInversion();
                }
            });

            final BluetoothSocket socket = btSocket;
            globalVariable.setBluetoothSocket(socket);
            Log.e(TAG, "Socket has been saved in GlobalClass!");

        }catch(Exception e){
            Log.e(TAG, "Either Bluetooth or WiFi isn't working!");
        }

        /*sensorManager.registerListener(new SensorEventListener() {
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
                    yOriginLeft = YleftMid - finalStatusBarHeight;//It is considering top tab margin also; It is using direct centre

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

        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);*/

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

    private void sendMessage(BluetoothSocket socket, String msg) {
        OutputStream outStream;
        try {
            outStream = socket.getOutputStream();
            byte[] byteString = (msg).getBytes();
            outStream.write(byteString);
        } catch (IOException e) {

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            AlertDialog.Builder alert = new AlertDialog.Builder(ProJoypad.this);
            alert.setTitle("Bluetooth problem");
            alert.setMessage("Can you let this activity be restarted and automatically attempt to restore bluetooth connection?");
            alert.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
            alert.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "BTproblem: No data transmission!", Toast.LENGTH_LONG).show();
                        }
                    });
            alert.setCancelable(true);
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
    }

}