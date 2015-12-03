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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EventsMap extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public final static String EXTRA_EVENTID = "cs4720.cs.virginia.edu.eventsnearme.EVENTID";
    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";
    public final static String EXTRA_RATING = "cs4720.cs.virginia.edu.eventsnearme.RATING";
    public final static String EXTRA_LONG = "cs4720.cs.virginia.edu.eventsnearme.LONGITUDE";
    public final static String EXTRA_LAT = "cs4720.cs.virginia.edu.eventsnearme.LATITUDE";
    public final static String EXTRA_USERNAME = "cs4720.cs.virginia.edu.eventsnearme.USERNAME";
    public final static String EXTRA_LOGGED = "cs4720.cs.virginia.edu.eventsnearme.LOGGED";
    public final static String EXTRA_SENDER = "cs4720.cs.virginia.edu.eventsnearme.SENDER";

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
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> ratings = new ArrayList<>();

    private ArrayList<String> ids1 = new ArrayList<>();
    private ArrayList<String> tempTitles1 = new ArrayList<>();
    private ArrayList<String> latitudes1 = new ArrayList<>();
    private ArrayList<String> longitudes1 = new ArrayList<>();
    private ArrayList<String> descriptions1 = new ArrayList<>();
    private ArrayList<String> tag1s1 = new ArrayList<>();
    private ArrayList<String> tag2s1 = new ArrayList<>();
    private ArrayList<String> tag3s1 = new ArrayList<>();
    private ArrayList<String> images1 = new ArrayList<>();
    private ArrayList<String> ratings1 = new ArrayList<>();

    private String user;
    private boolean logged = false;

    private boolean loading = true;

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
        query.whereEqualTo("title", "update");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + objectList.size() + " scores");
                    for (ParseObject o : objectList) {
                        Log.i("The title: ", (String) o.get("title"));
                        tempTitles.add((String) o.get("title"));
                        latitudes.add((String) o.get("latitude"));
                        longitudes.add((String) o.get("longitude"));
                        descriptions.add((String) o.get("description"));
                        tag1s.add((String) o.get("tag1"));
                        tag2s.add((String) o.get("tag2"));
                        tag3s.add((String) o.get("tag3"));
                        images.add("NO_IMAGE");
                        ratings.add(Integer.toString((Integer) o.get("rating")));
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf((String) o.get("latitude")), Double.valueOf((String) o.get("longitude"))))
                                        .title((String) o.get("title")));
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
                Log.i("tempTitles size inside", "" + tempTitles.size());
                loading = false;
            }
        });*/


        Log.i("tempTitles size", ""+tempTitles.size());

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
                index2 = temp.indexOf("Description: ")-1;
                listInput = temp.substring(0, index2);
                tempTitles.add(listInput);

                index = temp.indexOf("Description: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 13);
                index2 = temp.indexOf("Tag 1: ")-1;
                listInput = temp.substring(0, index2);
                descriptions.add(listInput);

                index = temp.indexOf("Tag 1: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf("Tag 2: ")-1;
                listInput = temp.substring(0, index2);
                tag1s.add(listInput);

                index = temp.indexOf("Tag 2: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf("Tag 3: ")-1;
                listInput = temp.substring(0, index2);
                tag2s.add(listInput);

                index = temp.indexOf("Tag 3: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf("Image: ")-1;
                listInput = temp.substring(0, index2);
                tag3s.add(listInput);

                index = temp.indexOf("Image: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                index2 = temp.indexOf("Rating: ")-1;
                listInput = temp.substring(0, index2);
                images.add(listInput);

                index = temp.indexOf("Rating: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 8);
                index2 = temp.indexOf("Latitude: ")-1;
                listInput = temp.substring(0, index2);
                ratings.add(listInput);

                index = temp.indexOf("Latitude: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 10);
                index2 = temp.indexOf("Longitude: ")-1;
                listInput = temp.substring(0, index2);
                latitudes.add(listInput);

                index = temp.indexOf("Longitude: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 11);
                index2 = temp.indexOf(" ");
                listInput = temp.substring(0, index2);
                longitudes.add(listInput);
            }

        } catch (Exception e) {

        }

        titles = new String[tempTitles.size()];
        for(int i = 0; i < tempTitles.size(); i++) {
            titles[i] = tempTitles.get(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);
        buildGoogleApiClient();

        Intent intent = getIntent();

        user = intent.getStringExtra(WelcomePage.EXTRA_USERNAME);
        logged = intent.getBooleanExtra(WelcomePage.EXTRA_LOGGED, false);

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

        Log.i("Titles length", ""+titles.length);
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
        /*
        for(int i = 0; i < titles.length; i++){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(latitudes.get(i)), Double.valueOf(longitudes.get(i))))
                    .title(titles[i]));
        }*/
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
        //query.whereEqualTo("title", "update");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objectList, ParseException e) {

                final HashMap<Marker, Integer> markerMap = new HashMap<>();
                int myIndex = 0;

                if (e == null) {
                    Log.d("score", "Retrieved " + objectList.size() + " scores");
                    for (ParseObject o : objectList) {
                        Log.i("The title: ", (String) o.get("title"));
                        ids1.add((String) o.get("eventId"));
                        tempTitles1.add((String) o.get("title"));
                        latitudes1.add((String) o.get("latitude"));
                        longitudes1.add((String) o.get("longitude"));
                        descriptions1.add((String) o.get("description"));
                        tag1s1.add((String) o.get("tag1"));
                        tag2s1.add((String) o.get("tag2"));
                        tag3s1.add((String) o.get("tag3"));
                        if (o.get("image")!=null) {
                            images1.add("getImage");
                        } else {
                            images1.add("NO_IMAGE");
                        }
                        ratings1.add(Integer.toString((Integer) o.get("rating")));
                        Marker myMark = map.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf((String) o.get("latitude")), Double.valueOf((String) o.get("longitude"))))
                                .title((String) o.get("title")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        markerMap.put(myMark, myIndex);
                        myIndex++;
                    }
                } else {
                    //Log.d("score", "Error: " + e.getMessage());
                }


                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        int index = markerMap.get(marker);

                        /*
                        for (int i = 0; i < tempTitles1.size(); i++) {
                            if (marker.getTitle().equals(tempTitles1.get(i))) {
                                index = i;
                                break;
                            }
                        }*/

                        Intent intent = new Intent(EventsMap.this, EventInfo.class);

                        String id = ids1.get(index);
                        intent.putExtra(EXTRA_EVENTID, id);

                        String title = tempTitles1.get(index);
                        intent.putExtra(EXTRA_TITLE, title);

                        Log.i("ttttttttttttt", title);

                        String description = descriptions1.get(index);
                        intent.putExtra(EXTRA_DESCRIPTION, description);

                        String tag1 = tag1s1.get(index);
                        intent.putExtra(EXTRA_TAG1, tag1);

                        String tag2 = tag2s1.get(index);
                        intent.putExtra(EXTRA_TAG2, tag2);

                        String tag3 = tag3s1.get(index);
                        intent.putExtra(EXTRA_TAG3, tag3);

                        String image = images1.get(index);
                        intent.putExtra(PHOTO_URI, image);

                        String rating = ratings1.get(index);
                        intent.putExtra(EXTRA_RATING, rating);

                        String latitude = latitudes1.get(index);
                        Log.i("My latitude", latitude);
                        intent.putExtra(EXTRA_LAT, latitude);

                        String longitude = longitudes1.get(index);
                        intent.putExtra(EXTRA_LONG, longitude);

                        intent.putExtra(EXTRA_USERNAME, user);
                        intent.putExtra(EXTRA_LOGGED, logged);
                        intent.putExtra(EXTRA_SENDER, "EventsMap");

                        startActivity(intent);
                    }
                });


                Log.i("tempTitles size inside", "" + tempTitles.size());
                loading = false;
            }
        });

        /*map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                int index = -1;

                for (int i = 0; i < titles.length; i++) {
                    if (marker.getTitle().equals(titles[i])) {
                        index = i;
                        break;
                    }
                }

                Intent intent = new Intent(EventsMap.this, EventInfo.class);

                String title = marker.getTitle();
                intent.putExtra(EXTRA_TITLE, title);

                String description = descriptions.get(index);
                intent.putExtra(EXTRA_DESCRIPTION, description);

                String tag1 = tag1s.get(index);
                intent.putExtra(EXTRA_TAG1, tag1);

                String tag2 = tag2s.get(index);
                intent.putExtra(EXTRA_TAG2, tag2);

                String tag3 = tag3s.get(index);
                intent.putExtra(EXTRA_TAG3, tag3);

                String image = images.get(index);
                intent.putExtra(PHOTO_URI, image);

                String rating = ratings.get(index);
                intent.putExtra(EXTRA_RATING, rating);

                String latitude = latitudes.get(index);
                intent.putExtra(EXTRA_LATITUDE, latitude);

                String longitude = longitudes.get(index);
                intent.putExtra(EXTRA_LONGITUDE, longitude);

                startActivity(intent);
            }
        });*/
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
        /*Random random = new Random();
        for(int i = 0; i < titles.length; i++){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude() + random.nextDouble() * .06 - .06, mLastLocation.getLongitude() + random.nextDouble() * .06 - .06))
                    .title(titles[i]));
        }*/
    }
}
