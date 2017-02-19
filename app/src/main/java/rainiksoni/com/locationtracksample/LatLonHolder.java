package rainiksoni.com.locationtracksample;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by rainiksoni on 19/02/17.
 */

public class LatLonHolder  {

    private static ArrayList<LatLng> latLngsList = new ArrayList<>();

    public  static ArrayList getLatLngList(){
        return latLngsList;
    }
}
