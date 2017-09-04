package com.dragon.wikimysore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

public class GPSActivity extends ActionBarActivity implements LocationListener {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
    }

*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_g, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    String wikiQ = "";
    TextView et1;
    EditText et2;
    private Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivityLocation(this);
        setContentView(R.layout.activity_gps);

        /*The places to fill out manually*/
        Places places[] = new Places[5];
        places[0] = new Places( "National_Institute_of_Engineering " , 12.2840367 , 76.6464159029 , .8);
        places[3] = new Places( "Karanji_Lake" , 12.3028 , 76.6736 , .8);
        places[4] = new Places( "Chamundeshwari_Temple" , 12.2725 , 76.6708 , 2);
        places[1] = new Places( "Mysore_Palace" , 12.3039 , 76.6544 , .4);
        places[2] = new Places( "Sri_Jayachamarajendra_College_of_Engineering" , 12.3112267 , 76.6137538 , .6);
        double t ,dis = 500;
        String place ="";
        for( int i = 0 ; i< 5  ;i++)
        {

            //Toast.makeText(this,String.valueOf(places[i].rad) +"--> "+places[i].name+"-->"+String.valueOf(distance(latitude,longitude , places[i].lat,places[i].lon)), Toast.LENGTH_SHORT).show();
            if( (t = distance(latitude,longitude , places[i].lat,places[i].lon)) < dis )
            {
                //Toast.makeText(this, ""+places[i].name, Toast.LENGTH_SHORT).show();
                dis = t ;
                place = places[i].name;
                //wikiQ = "http://en.m.wikipedia.org/wiki/"+places[i].name;
                //break;
            }

        }
        if( !places.equals(""))
        {
            wikiQ = "http://en.m.wikipedia.org/wiki/"+place;
            Toast.makeText(this, place +"\ndistance = " + dis, Toast.LENGTH_SHORT).show();
        }
        WebView wv = (WebView)findViewById( R.id.wb1);
        if( !wikiQ.equals("") )
        {

            wv.loadUrl(wikiQ);


        }
		/*else{
		MysoreDb db = new MysoreDb(this );
		String place = db.getPlace(latitude, longitude);
		wv.loadUrl("http://en.m.wikipedia.org/wiki/"+place);
		Toast.makeText(this, "nearest to "+place, Toast.LENGTH_SHORT);
		}*/
    }

    public void ActivityLocation(Context context) {
        this.mContext = context;
        getLocation();
        Toast.makeText(this, "over", Toast.LENGTH_SHORT).show();
        stopUsingGPS();
    }
    private static double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;

        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);

        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);

    }


    private static double deg2rad(double deg)
    {

        return (deg * Math.PI / 180.0);

    }

    private static double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);

    }



    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Toast.makeText(mContext,""+isGPSEnabled+"--"+isNetworkEnabled, Toast.LENGTH_SHORT).show();


            if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(mContext, "no network/gps", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
            Toast.makeText(this,"Inside Network",Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
            Toast.makeText(this, "network:"+latitude+"= latitude"+"\nlongitude= "+longitude, Toast.LENGTH_LONG).show();
                            // et1 = (TextView)findViewById(R.id.et1);
                            // et1.setText( "network: latitude = "+ latitude+" longitude= "+longitude);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
            Toast.makeText(this, "GPS"+latitude+"= latitude"+"\nlongitude= "+longitude, Toast.LENGTH_LONG).show();

                                //et2 = (EditText)findViewById(R.id.et2);
                                // et2.setText( "gps: latitude = "+ latitude+" longitude= "+longitude);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSActivity.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
