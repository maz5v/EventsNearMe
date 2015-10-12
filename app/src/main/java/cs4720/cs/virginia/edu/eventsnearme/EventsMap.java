package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class EventsMap extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";
    public final static String EXTRA_RATING = "cs4720.cs.virginia.edu.eventsnearme.RATING";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private GoogleMap map;

    private String[] titles;
    private ArrayList<String> tempTitles = new ArrayList<>();
    private String fileInfo = "";
    private ArrayList<String> latitudes = new ArrayList<>();
    private ArrayList<String> longitudes = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> tag1s = new ArrayList<>();
    private ArrayList<String> tag2s = new ArrayList<>();
    private ArrayList<String> tag3s = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);
        buildGoogleApiClient();

        try {
            FileInputStream input = openFileInput("eventDataFile");
            int character;
            String temp = "";
            while((character = input.read()) != -1) {
                temp = temp + Character.toString((char)character);
            }

            fileInfo = temp;
            Log.d("File info", fileInfo);

            int index = 0;
            int index2 = 0;
            String listInput = "";
            while(true) {
                index = temp.indexOf("Title: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf(" ");
                listInput = temp.substring(0, index2);
                tempTitles.add(listInput);

                index = temp.indexOf("Description: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 13);
                index2 = temp.indexOf(" ");
                listInput = temp.substring(0, index2);
                descriptions.add(listInput);

                index = temp.indexOf("Tag 1: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf(" ");
                listInput = temp.substring(0, index2);
                tag1s.add(listInput);

                index = temp.indexOf("Tag 2: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf(" ");
                listInput = temp.substring(0, index2);
                tag2s.add(listInput);

                index = temp.indexOf("Tag 3: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf(" ");
                listInput = temp.substring(0, index2);
                tag3s.add(listInput);
            }

            //Just checking it worked
            String tester = "";
            for(int i = 0; i < tempTitles.size(); i++) {
                tester = tester + tempTitles.get(i) + ", ";
            }
            Log.d("Test values: ", tester);

            titles = new String[tempTitles.size()];
            for(int i = 0; i < tempTitles.size(); i++) {
                titles[i] = tempTitles.get(i);
            }

        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_map, menu);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mLatitudeText = new TextView(getApplicationContext());
        mLongitudeText = new TextView(getApplicationContext());
        if (mLastLocation != null) {
            //Log.i("Latitude", "Latitude = " + mLastLocation.getLatitude());
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        TextView coordinates = (TextView)findViewById(R.id.textView8);
        coordinates.setText("Your latitude is: " + mLatitudeText.getText() + "\n" + "Your longitude is: " + mLongitudeText.getText());
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        Log.i("Map Check", map.toString());
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(EventsMap.this, EventInfo.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Finding Location", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        Random random = new Random();
        for(int i = 0; i < titles.length; i++){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude() + random.nextDouble() * .06 - .06, mLastLocation.getLongitude() + random.nextDouble() * .06 - .06))
                    .title(titles[i]));
        }
    }
}
