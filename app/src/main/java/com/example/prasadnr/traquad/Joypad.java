/*
    This file is part of TraQuad-project's software, version Alpha (unstable release).

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
    Date of creation: June 2015 - June 2016 and Attribution: Prasad N R as a representative of (unregistered) company TraQuad.
 */

package com.example.prasadnr.traquad;

import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.app.Activity;
import android.view.MotionEvent;
import android.widget.Toast;
import android.view.Gravity;
import android.widget.VideoView;
import android.util.DisplayMetrics;
import android.net.Uri;
import android.widget.MediaController;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.app.ProgressDialog;
import android.os.Message;
//import java.util.logging.Handler;
import android.os.Handler;
import android.util.Log;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaRecorder.OnInfoListener;
import android.bluetooth.BluetoothAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Set;
import android.os.ParcelUuid;
import java.lang.reflect.Method;
import android.widget.ListView;
import android.widget.EditText;

public class Joypad extends AppCompatActivity {

    private static final String TAG = "TraQuad";
    boolean irritation = false; //A boolean variable which causes annoyance in users if it is not included
    boolean isWifiAPenabled = false;

    private BluetoothAdapter btAdapter = null;
    private OutputStream outStream = null;

    private static String address = "20:14:12:03:11:24"; //HC05
    //private static String address = "5C:F3:70:77:6B:18"; //Kinivo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joypad);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Joypad.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final ProgressDialog pDialog;
        MediaController mediaController = new MediaController(this);
        String buff = "Buffering... Please wait...";

        final Button switchact =(Button)findViewById(R.id.btn1);

        final ImageButton up = (ImageButton)findViewById(R.id.imageButton2);
        final ImageButton left = (ImageButton)findViewById(R.id.imageButton3);
        final ImageButton right = (ImageButton)findViewById(R.id.imageButton5);
        final ImageButton down = (ImageButton)findViewById(R.id.imageButton7);
        final ImageButton verticalup = (ImageButton)findViewById(R.id.imageButton8);
        final ImageButton verticaldown = (ImageButton)findViewById(R.id.imageButton10);
        final ImageButton anticlockwise = (ImageButton)findViewById(R.id.imageButton9);
        final ImageButton clockwise = (ImageButton)findViewById(R.id.imageButton11);

        BluetoothAdapter btAdapter = null;
        TextView mLabel;
        EditText mDevice;
        BluetoothSocket btSocket = null;
        StringBuilder recDataString = new StringBuilder();
        Intent intent = getIntent();

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
        } catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "Please connect bluetooth properly!", Toast.LENGTH_LONG).show();
        }

        WifiManager managerWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Method[] wmMethods = managerWifi.getClass().getDeclaredMethods();
        for (Method method: wmMethods) {
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

        //VideoView vidView = (VideoView)findViewById(R.id.videoView);
        WebView webView = (WebView)findViewById(R.id.webViewJoypad);
        pDialog = new ProgressDialog(Joypad.this);
        pDialog.setTitle("TraQuad app (Connecting...)");
        pDialog.setMessage("Buffering...Please wait...");
        pDialog.setCancelable(true);
        AlertDialog.Builder alert = new AlertDialog.Builder(Joypad.this);
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
            Toast toast = Toast.makeText(this, "Please use Emergency landing button only \nin last ditch efforts", Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
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
        TextView sendCharacter = (TextView) findViewById(R.id.sendCharacter);

        try {
            webView.loadUrl(IPaddressStream);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), IPaddressNew + ":Error!", Toast.LENGTH_LONG).show();
        }


        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                pDialog.dismiss();
            }
        });

        final BluetoothSocket socket = btSocket;

        final String u = "u", l = "l", r = "r", d = "d", f = "f", c = "c", a = "a", b = "b";
        final String S = "s", x = "x";

        up.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    viewup.setText("f");
                    try {
                        sendMessage(socket, f);
                    }catch(Exception e){}
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("l");
                    try {
                        sendMessage(socket, l);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("r");
                    try {
                        sendMessage(socket, r);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("b");
                    try {
                        sendMessage(socket, b);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });


        verticalup.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("u");
                    try {
                        sendMessage(socket, u);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        verticaldown.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("d");
                    try {
                        sendMessage(socket, d);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        anticlockwise.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("a");
                    try {
                        sendMessage(socket, a);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        clockwise.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView viewup = (TextView) findViewById(R.id.textView18);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewup.setText("c");
                    try {
                        sendMessage(socket, c);
                    }catch(Exception e){}
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewup.setText("S");
                    try {
                        sendMessage(socket, S);
                    }catch(Exception e){}
                }
                return true;
            }
        });

        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent act2 = new Intent(view.getContext(), TraQuad.class);
                startActivity(act2);
            }
        });

        final Button homejoy =(Button)findViewById(R.id.button4);

        homejoy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent acthomejoy = new Intent(Joypad.this, MainActivity.class);
                startActivity(acthomejoy);
            }
        });

        final Button emergency =(Button)findViewById(R.id.button6);

        emergency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendMessage(socket, x);
            }
        });

    }

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_joypad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        this.closeOptionsMenu();
        Intent intent = new Intent(Joypad.this, Setting.class);

        startActivity(intent);
        return true;
    }

    private void sendMessage(BluetoothSocket socket, String msg) {
        OutputStream outStream;
        try {
            outStream = socket.getOutputStream();
            byte[] byteString = (msg).getBytes();
            outStream.write(byteString);
        } catch (IOException e) {

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            AlertDialog.Builder alert = new AlertDialog.Builder(Joypad.this);
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
