package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

//public class Newsfeed extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
public class Newsfeed extends AppCompatActivity {

    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";
    public final static String EXTRA_RATING = "cs4720.cs.virginia.edu.eventsnearme.RATING";
    public final static String EXTRA_LAT = "cs4720.cs.virginia.edu.eventsnearme.LAT";
    public final static String EXTRA_LONG = "cs4720.cs.virginia.edu.eventsnearme.LONG";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private final String file = "newsfeedDataFile";

    private String[] titles;
    private ArrayList<String> tempTitles = new ArrayList<>();
    private String fileInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //buildGoogleApiClient();
        setContentView(R.layout.activity_events_page);
        try {
            FileInputStream input = openFileInput(file);
            int character;
            String temp = "";
            while((character = input.read()) != -1) {
                temp = temp + Character.toString((char)character);
            }

            fileInfo = temp;
            Log.d("File info", fileInfo);

            int count = 0;
            while(true) {
                int index = temp.indexOf("Title: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                int index2 = temp.indexOf("Description: ")-1;
                String listInput = temp.substring(0, index2);
                tempTitles.add(listInput);
                if (count == 7) break;
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

            //Just checking it worked
            tester = "";
            for(int i = 0; i < titles.length; i++) {
                tester = tester + titles[i] + ", ";
            }
            Log.d("Test values: ", tester);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(mMessageClickedHandler);

        } catch (Exception e) {

        }
    }

    public void fillNewsfeedFile(View view) {
        try {
            FileOutputStream output = openFileOutput(file, Context.MODE_APPEND);
            String finalString = "";
            String tag1="Food";
            String tag2="";
            String tag3="";
            for (int i=0; i<10; i++) {
                if (i == 3) {
                    tag1 = "";
                    tag2 = "Entertainment";
                }
                if (i == 7) {
                    tag2 = "";
                    tag3 = "Shopping";
                }
                finalString = finalString + "Title: Newsfeed Test Event " + i;
                finalString = finalString + " Description: Test Description " + i;
                finalString = finalString + " Tag 1: " + tag1;
                finalString = finalString + " Tag 2: " + tag2;
                finalString = finalString + " Tag 3: " + tag3;
                /*if (photoURI != null)
                    finalString = finalString + " Image: " + photoURI.toString();
                else finalString = finalString + " Image: NO_IMAGE";*/
                finalString = finalString + " Image: NO_IMAGE";
                finalString = finalString + " Rating: 5";
                Random random = new Random();
                /*if (mLastLocation != null) {
                    finalString = finalString + " Latitude: " + (mLastLocation.getLatitude() + random.nextDouble() * .06 - .06);
                    finalString = finalString + " Longitude: " + (mLastLocation.getLongitude() + random.nextDouble() * .06 - .06);
                } else {
                    finalString = finalString + " Latitude: LAT_ERROR";
                    finalString = finalString + " Longitude: LONG_ERROR";
                }*/
                finalString = finalString + " Latitude: " + (38.034506 + random.nextDouble() * .06 - .06);
                finalString = finalString + " Longitude: " + (78.486474 + random.nextDouble() * .06 - .06);
                finalString = finalString + " ||| ";
                output.write(finalString.getBytes());
            }
            Log.d("Error Checking: ", finalString);
            output.close();
        } catch (Exception e) {
            Log.i("Exception writing file", e.getMessage());
        }
    }

    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), EventInfo.class);
            String temp = fileInfo;

            // Maybe position-1
            for(int i = 0; i < position; i++) {
                int index = temp.indexOf(" ||| ");
                temp = temp.substring(index + 5);
            }

            int index = temp.indexOf("Title: ");
            temp = temp.substring(index + 7);
            int spaceIndex = temp.indexOf("Description:");
            String title = temp.substring(0, spaceIndex);
            Log.i("Title: ", title);
            intent.putExtra(EXTRA_TITLE, title);

            index = temp.indexOf("Description: ");
            temp = temp.substring(index + 13);
            spaceIndex = temp.indexOf("Tag 1:");
            String description = temp.substring(0, spaceIndex);
            Log.i("Description: ", description);
            intent.putExtra(EXTRA_DESCRIPTION, description);

            index = temp.indexOf("Tag 1: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf("Tag 2:");
            String tag1 = temp.substring(0, spaceIndex);
            //if (tag1.length() > 1)
            //    tag1 = "Food";
            Log.i("Tag 1: ", tag1);
            intent.putExtra(EXTRA_TAG1, tag1);

            index = temp.indexOf("Tag 2: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf("Tag 3:");
            String tag2 = temp.substring(0, spaceIndex);
            //if (tag2.length() > 1)
            //    tag2 = "Entertainment";
            Log.i("Tag 2: ", tag2);
            intent.putExtra(EXTRA_TAG2, tag2);

            index = temp.indexOf("Tag 3: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf(" Image:");
            String tag3 = temp.substring(0, spaceIndex);
            //if (tag3.length() > 1)
            //    tag3 = "Shopping";
            Log.i("Tag 3: ", tag3);
            intent.putExtra(EXTRA_TAG3, tag3);

            index = temp.indexOf("Image: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf(" Rating:");
            String imageURI = temp.substring(0, spaceIndex);
            Log.i("Image URI: ", imageURI);
            intent.putExtra(PHOTO_URI, imageURI);

            index = temp.indexOf("Rating: ");
            temp = temp.substring(index + 8);
            spaceIndex = temp.indexOf(" Latitude:");
            String rating = temp.substring(0, spaceIndex);
            Log.i("Rating: ", rating);
            intent.putExtra(EXTRA_RATING, rating);

            index = temp.indexOf("Latitude: ");
            temp = temp.substring(index + 10);
            spaceIndex = temp.indexOf(" Longitude: ");
            String lat = temp.substring(0, spaceIndex);
            Log.i("Latitude: ", lat);
            intent.putExtra(EXTRA_LAT, lat);

            index = temp.indexOf("Longitude: ");
            temp = temp.substring(index + 11);
            spaceIndex = temp.indexOf(" ||| ");
            String lon = temp.substring(0, spaceIndex);
            Log.i("Longitude: ", lon);
            intent.putExtra(EXTRA_LONG, lon);

            startActivity(intent);

        }
    };

    /*
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    }*/
}



