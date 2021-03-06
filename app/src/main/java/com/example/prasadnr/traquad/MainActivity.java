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

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.app.Application;

public class MainActivity extends ActionBarActivity {

    public void sendMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, Credits.class);
        startActivity(intent);
    }

    public void pro(View view)
    {
        Intent intent = new Intent(MainActivity.this, ProJoypad.class);
        startActivity(intent);
    }

    public void traquadMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, TraQuad.class);
        startActivity(intent);
    }

    public void aboutMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }

    public void licenceMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, Licence.class);
        startActivity(intent);
    }

    public void joypadMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, Joypad.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Intent intent = new Intent(MainActivity.this, Setting.class);
    /*Here ActivityA is current Activity and ColourActivity is the target Activity.*/
        startActivity(intent);
        return true;
    }
}
