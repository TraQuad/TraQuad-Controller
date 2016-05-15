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

import android.app.Application;
import android.widget.Toast;

/**
 * Created by Prasad N R on 06-09-2015.
 */
//public class GlobalClass {
//}

public class GlobalClass extends Application {

    public int realWidth = 960;
    public int realHeight = 540;
    public String IPaddressNew = "192.168.43.135";

    public int getRealWidth() {

        return realWidth;
    }

    public void setRealWidth(Integer RealWidth) {

        realWidth = RealWidth;

    }

    public int getRealHeight() {

        return realHeight;
    }

    public void setRealHeight(Integer RealHeight) {

        realHeight = RealHeight;
    }

    public void setIP(String IPaddress){
        IPaddressNew = IPaddress;
    }

    public String getIP(){
        return IPaddressNew;
    }

}
