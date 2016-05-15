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
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class TraQuad extends ActionBarActivity {

    String extra;

    boolean irritation = false; //A boolean variable which causes annoyance in users if it is not included
    boolean isWifiAPenabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tra_quad);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(TraQuad.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Builder alert = new AlertDialog.Builder(TraQuad.this);

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

        final ProgressDialog pDialog;
        MediaController mediaController = new MediaController(this);

        pDialog = new ProgressDialog(TraQuad.this);
        pDialog.setTitle("TraQuad app (Connecting...)");
        pDialog.setMessage("Buffering...Please wait...");
        pDialog.setCancelable(true);

        if (!isWifiAPenabled) {

            alert.setTitle("WiFi Hotspot Settings");
            alert.setMessage("Can you please connect WiFi-hotspot?");
            alert.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            irritation = true;
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
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
            alert.setCancelable(false);
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }

        WebView webView = (WebView)findViewById(R.id.webView);

        if (irritation == true | isWifiAPenabled) {
            pDialog.show();
        }

        mediaController.setAnchorView(webView);
        mediaController.setVisibility(View.GONE);

        extra = getIntent().getStringExtra("VideosId");
        webView = (WebView) findViewById(R.id.webView);
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

        final Button switchact =(Button)findViewById(R.id.btn2);
        switchact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent act1 = new Intent(view.getContext(), Joypad.class);
                startActivity(act1);

            }
        });

        final Button hometraquad =(Button)findViewById(R.id.button5);

        hometraquad.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent acthometraquad = new Intent(TraQuad.this, MainActivity.class);
                startActivity(acthometraquad);
            }
        });

    }

    public class MyOwnWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    private OnErrorListener mOnErrorListener = new OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tra_quad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        //return super.onOptionsItemSelected(item);
        super.onOptionsItemSelected(item);
        this.closeOptionsMenu();
        Intent intent = new Intent(TraQuad.this, Setting.class);
    /*Here ActivityA is current Activity and ColourActivity is the target Activity.*/
        startActivity(intent);
        return true;
    }
}
