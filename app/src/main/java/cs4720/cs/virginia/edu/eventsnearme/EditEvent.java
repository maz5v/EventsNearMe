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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class EditEvent extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";
    public final static String EXTRA_RATING = "cs4720.cs.virginia.edu.eventsnearme.RATING";
    public final static String EXTRA_LAT = "cs4720.cs.virginia.edu.eventsnearme.LAT";
    public final static String EXTRA_LONG = "cs4720.cs.virginia.edu.eventsnearme.LONG";

    public int rating = 5;
    private String myTitle = "";
    private String myLat = "";
    private String myLong = "";
    private String myPhoto = "";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();

        String title = intent.getStringExtra(EventInfo.EXTRA_TITLE);
        myTitle = title;
        EditText titleText = (EditText) findViewById(R.id.title);
        titleText.setText(title, TextView.BufferType.EDITABLE);

        String description = intent.getStringExtra(EventInfo.EXTRA_DESCRIPTION);
        EditText descriptionText = (EditText) findViewById(R.id.description);
        descriptionText.setText(description, TextView.BufferType.EDITABLE);

        String rating2 = intent.getStringExtra(EventInfo.EXTRA_RATING);
        TextView ratingText = (TextView) findViewById(R.id.rating);
        ratingText.setText(rating2);
        rating = Integer.parseInt(rating2);

        String tag1 = intent.getStringExtra(EventInfo.EXTRA_TAG1);
        String tag2 = intent.getStringExtra(EventInfo.EXTRA_TAG2);
        String tag3 = intent.getStringExtra(EventInfo.EXTRA_TAG3);
        ToggleButton toggle1 = (ToggleButton) findViewById(R.id.tag1);
        ToggleButton toggle2 = (ToggleButton) findViewById(R.id.tag2);
        ToggleButton toggle3 = (ToggleButton) findViewById(R.id.tag3);
        if (tag1.length() > 1) {
            toggle1.setChecked(true);
        }
        if (tag2.length() > 1) {
            toggle2.setChecked(true);
        }
        if (tag3.length() > 1) {
            toggle3.setChecked(true);
        }

        myLat = intent.getStringExtra(EventInfo.EXTRA_LAT);
        myLong = intent.getStringExtra(EventInfo.EXTRA_LONG);
        myPhoto = intent.getStringExtra(EventInfo.PHOTO_URI);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void upVote(View view) {
        if(rating == 10) return;
        rating++;
        TextView ratingView = (TextView) findViewById(R.id.rating);
        ratingView.setText("" + rating);
    }

    public void downVote(View view) {
        if (rating == 1) return;
        rating--;
        TextView ratingView = (TextView) findViewById(R.id.rating);
        ratingView.setText("" + rating);
    }

    public void updateEvent(View view) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
        query.whereEqualTo("title", myTitle);
        Log.i("The title", myTitle);
        Log.i("The title length", ""+myTitle.length());
        String temppp = "testing space ";
        Log.i("The title", "" + temppp.length());

        Intent intent = new Intent(this, EventInfo.class);

        intent.putExtra(EXTRA_TITLE, myTitle);

        EditText descriptionText = (EditText) findViewById(R.id.description);
        String description = descriptionText.getText().toString();
        intent.putExtra(EXTRA_DESCRIPTION, description);

        intent.putExtra(EXTRA_RATING, Integer.toString(rating));

        ToggleButton toggle1 = (ToggleButton) findViewById(R.id.tag1);
        ToggleButton toggle2 = (ToggleButton) findViewById(R.id.tag2);
        ToggleButton toggle3 = (ToggleButton) findViewById(R.id.tag3);
        if (toggle1.isChecked()) {
            intent.putExtra(EXTRA_TAG1, "Food");
        } else {
            intent.putExtra(EXTRA_TAG1, "");
        }
        if (toggle2.isChecked()) {
            intent.putExtra(EXTRA_TAG2, "Entertainment");
        } else {
            intent.putExtra(EXTRA_TAG2, "");
        }
        if (toggle3.isChecked()) {
            intent.putExtra(EXTRA_TAG3, "Shopping");
        } else {
            intent.putExtra(EXTRA_TAG3, "");
        }

        intent.putExtra(PHOTO_URI, myPhoto);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup3);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        Log.i("Radio Button ID", "" + radioButtonId);
        if (radioButtonId == -1) {
            intent.putExtra(EXTRA_LAT, myLat);
            intent.putExtra(EXTRA_LONG, myLong);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + objectList.size() + " scores");
                        for (ParseObject eventObject : objectList) {
                            //eventObject.put("title", title);

                            EditText descriptionText = (EditText) findViewById(R.id.description);
                            String description = descriptionText.getText().toString();
                            eventObject.put("description", description);

                            eventObject.put("latitude", myLat);
                            eventObject.put("longitude", myLong);

                            eventObject.put("rating", rating);

                            ToggleButton toggle1 = (ToggleButton) findViewById(R.id.tag1);
                            ToggleButton toggle2 = (ToggleButton) findViewById(R.id.tag2);
                            ToggleButton toggle3 = (ToggleButton) findViewById(R.id.tag3);
                            if (toggle1.isChecked()) {
                                eventObject.put("tag1", "Food");
                            } else {
                                eventObject.put("tag1", "");
                            }
                            if (toggle2.isChecked()) {
                                eventObject.put("tag2", "Entertainment");
                            } else {
                                eventObject.put("tag2", "");
                            }
                            if (toggle3.isChecked()) {
                                eventObject.put("tag3", "Shopping");
                            } else {
                                eventObject.put("tag3", "");
                            }

                            eventObject.saveInBackground();

                        }
                    } else {
                        //Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });

            startActivity(intent);

        } else if (radioButtonId == 2131558555) {
            // Use current location
            intent.putExtra(EXTRA_LAT, String.valueOf(mLastLocation.getLatitude()));
            intent.putExtra(EXTRA_LAT, String.valueOf(mLastLocation.getLongitude()));

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + objectList.size() + " scores");
                        for (ParseObject eventObject : objectList) {
                            //eventObject.put("title", title);

                            EditText descriptionText = (EditText) findViewById(R.id.description);
                            String description = descriptionText.getText().toString();
                            eventObject.put("description", description);

                            eventObject.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                            eventObject.put("longitude", String.valueOf(mLastLocation.getLongitude()));

                            eventObject.put("rating", rating);

                            ToggleButton toggle1 = (ToggleButton) findViewById(R.id.tag1);
                            ToggleButton toggle2 = (ToggleButton) findViewById(R.id.tag2);
                            ToggleButton toggle3 = (ToggleButton) findViewById(R.id.tag3);
                            if (toggle1.isChecked()) {
                                eventObject.put("tag1", "Food");
                            } else {
                                eventObject.put("tag1", "");
                            }
                            if (toggle2.isChecked()) {
                                eventObject.put("tag2", "Entertainment");
                            } else {
                                eventObject.put("tag2", "");
                            }
                            if (toggle3.isChecked()) {
                                eventObject.put("tag3", "Shopping");
                            } else {
                                eventObject.put("tag3", "");
                            }

                            eventObject.saveInBackground();

                        }
                    } else {
                        //Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });

            startActivity(intent);

        } else {

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Finding Location", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

}
