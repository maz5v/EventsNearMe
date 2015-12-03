package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class EditOnMap extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private GoogleMap map;
    private Marker marker;

    private String title;
    private String description;
    private String tag1;
    private String tag2;
    private String tag3;
    private String photoURI;
    private String rating;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_on_map);
        buildGoogleApiClient();

        intent = getIntent();

        title = intent.getStringExtra(EditEvent.EXTRA_TITLE);
        description = intent.getStringExtra(EditEvent.EXTRA_DESCRIPTION);
        tag1 = intent.getStringExtra(EditEvent.EXTRA_TAG1);
        tag2 = intent.getStringExtra(EditEvent.EXTRA_TAG2);
        tag3 = intent.getStringExtra(EditEvent.EXTRA_TAG3);
        photoURI = intent.getStringExtra(EditEvent.PHOTO_URI);
        rating = intent.getStringExtra(EditEvent.EXTRA_RATING);

        Log.i("Rating", rating);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_on_map, menu);
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
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        Log.i("Map Check", map.toString());
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                marker = map.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Set location here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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

    }

    public void onEdit(View view) {
        if (marker == null) {
            Context context = getApplicationContext();
            CharSequence text = "Please place a marker on the map to choose a location!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
            query.whereEqualTo("title", title);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + objectList.size() + " scores");
                        for (ParseObject eventObject : objectList) {
                            //eventObject.put("title", title);
                            eventObject.put("description", description);
                            eventObject.put("latitude", String.valueOf(marker.getPosition().latitude));
                            eventObject.put("longitude", String.valueOf(marker.getPosition().longitude));
                            eventObject.put("rating", Integer.parseInt(rating));
                            eventObject.put("tag1", tag1);
                            eventObject.put("tag2", tag2);
                            eventObject.put("tag3", tag3);
                            eventObject.saveInBackground();
                        }
                    } else {
                        //Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });

            intent.setClass(this, EventInfo.class);
            // maybe set lat/long? and also in selectOnMap?
            startActivity(intent);
        }
    }
}
