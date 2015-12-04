package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditEvent extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static String EXTRA_EVENTID = "cs4720.cs.virginia.edu.eventsnearme.EVENTID";
    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";
    public final static String EXTRA_RATING = "cs4720.cs.virginia.edu.eventsnearme.RATING";
    public final static String EXTRA_LAT = "cs4720.cs.virginia.edu.eventsnearme.LAT";
    public final static String EXTRA_LONG = "cs4720.cs.virginia.edu.eventsnearme.LONG";
    public final static String EXTRA_USERNAME = "cs4720.cs.virginia.edu.eventsnearme.USERNAME";
    public final static String EXTRA_LOGGED = "cs4720.cs.virginia.edu.eventsnearme.LOGGED";
    public final static String EXTRA_SENDER = "cs4720.cs.virginia.edu.eventsnearme.SENDER";

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PICK_PHOTO = 2;

    public int rating = 5;
    private boolean rated = false;
    private int initialRating;
    private String myTitle = "";
    private String myLat = "";
    private String myLong = "";
    private String myPhoto = "";
    File photoFile = null;
    String mCurrentPhotoPath;
    private String photoString = "";
    Uri photoURI = null;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private String user;
    private boolean logged;
    private String id;

    private ParseFile imageFile = null;
    private byte[] rotatePic = null;

    private int updatedRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_edit_event);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();

        user = intent.getStringExtra(EventInfo.EXTRA_USERNAME);
        logged = intent.getBooleanExtra(EventInfo.EXTRA_LOGGED, false);
        id = intent.getStringExtra(EventInfo.EXTRA_EVENTID);

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
        initialRating = Integer.parseInt(rating2);
        updatedRating = initialRating;

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

        if (savedInstanceState != null) {
            TextView ratingView = (TextView)findViewById(R.id.eventRating);
            if (savedInstanceState.getBoolean("rated")) {
                rating = savedInstanceState.getInt("rating");
                rated = savedInstanceState.getBoolean("rated");
                ratingView.setText("" + rating);
            }
            if (savedInstanceState.getByteArray("rotatePic") != null) {
                photoString = savedInstanceState.getString("photoString");
                myPhoto = photoString;
                rotatePic = savedInstanceState.getByteArray("rotatePic");
                imageFile = new ParseFile("image.webp", rotatePic);
                imageFile.saveInBackground();
            }
        }

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("rating", rating);
        outState.putBoolean("rated", rated);
        outState.putByteArray("rotatePic", rotatePic);
        outState.putString("photoString", photoString);
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

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void dispatchPickPictureIntent(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                photoURI = Uri.fromFile(photoFile);
                photoString = photoURI.toString();
                myPhoto = photoString;
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.WEBP, 75, stream);
                    byte[] imageData = stream.toByteArray();
                    rotatePic = imageData;
                    imageFile = new ParseFile("image.webp", imageData);
                    imageFile.saveInBackground();
                } catch (IOException e) {

                }

            }
        }
        if (requestCode == REQUEST_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                photoURI = data.getData();
                photoString = photoURI.toString();
                myPhoto = photoString;
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.WEBP, 75, stream);

                    byte[] imageData = stream.toByteArray();
                    rotatePic = imageData;
                    imageFile = new ParseFile("image.webp", imageData);
                    imageFile.saveInBackground();
                } catch (IOException e) {

                }
            }
        }
    }

    public void upVote(View view) {
        rated = true;
        if(rating == 10) return;
        rating++;
        TextView ratingView = (TextView) findViewById(R.id.rating);
        ratingView.setText("" + rating);
    }

    public void downVote(View view) {
        rated = true;
        if (rating == 1) return;
        rating--;
        TextView ratingView = (TextView) findViewById(R.id.rating);
        ratingView.setText("" + rating);
    }

    public void updateEvent(View view) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
        query.whereEqualTo("eventId", id);

        Intent intent = new Intent(this, EventInfo.class);
        Intent selectionIntent = new Intent(this, EditOnMap.class);

        EditText titleText = (EditText) findViewById(R.id.title);
        String title = titleText.getText().toString();
        intent.putExtra(EXTRA_TITLE, title);
        selectionIntent.putExtra(EXTRA_TITLE, title);

        EditText descriptionText = (EditText) findViewById(R.id.description);
        String description = descriptionText.getText().toString();
        intent.putExtra(EXTRA_DESCRIPTION, description);
        selectionIntent.putExtra(EXTRA_DESCRIPTION, description);

        intent.putExtra(EXTRA_RATING, Integer.toString(rating));
        selectionIntent.putExtra(EXTRA_RATING, Integer.toString(rating));

        ToggleButton toggle1 = (ToggleButton) findViewById(R.id.tag1);
        ToggleButton toggle2 = (ToggleButton) findViewById(R.id.tag2);
        ToggleButton toggle3 = (ToggleButton) findViewById(R.id.tag3);
        if (toggle1.isChecked()) {
            intent.putExtra(EXTRA_TAG1, "Food");
            selectionIntent.putExtra(EXTRA_TAG1, "Food");
        } else {
            intent.putExtra(EXTRA_TAG1, "");
            selectionIntent.putExtra(EXTRA_TAG1, "");
        }
        if (toggle2.isChecked()) {
            intent.putExtra(EXTRA_TAG2, "Entertainment");
            selectionIntent.putExtra(EXTRA_TAG2, "Entertainment");
        } else {
            intent.putExtra(EXTRA_TAG2, "");
            selectionIntent.putExtra(EXTRA_TAG2, "");
        }
        if (toggle3.isChecked()) {
            intent.putExtra(EXTRA_TAG3, "Shopping");
            selectionIntent.putExtra(EXTRA_TAG3, "Shopping");
        } else {
            intent.putExtra(EXTRA_TAG3, "");
            selectionIntent.putExtra(EXTRA_TAG3, "");
        }

        intent.putExtra(PHOTO_URI, myPhoto);
        selectionIntent.putExtra(PHOTO_URI, myPhoto);

        intent.putExtra(EXTRA_USERNAME, user);
        selectionIntent.putExtra(EXTRA_USERNAME, user);
        intent.putExtra(EXTRA_LOGGED, logged);
        selectionIntent.putExtra(EXTRA_LOGGED, logged);
        intent.putExtra(EXTRA_EVENTID, id);
        selectionIntent.putExtra(EXTRA_EVENTID, id);
        intent.putExtra(EXTRA_SENDER, "EditEvent");
        selectionIntent.putExtra(EXTRA_SENDER, "EditEvent");

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if (radioButtonId == -1) {
            intent.putExtra(EXTRA_LAT, myLat);
            intent.putExtra(EXTRA_LONG, myLong);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + objectList.size() + " scores");
                        for (ParseObject eventObject : objectList) {
                            EditText titleText = (EditText) findViewById(R.id.title);
                            String title = titleText.getText().toString();
                            eventObject.put("title", title);

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
                            if (initialRating != rating) {
                                int updatedTimesRated = eventObject.getNumber("timesRated").intValue() + 1;
                                int updatedRatingsSum = eventObject.getNumber("ratingsSum").intValue() + rating;
                                updatedRating = updatedRatingsSum / updatedTimesRated;
                                eventObject.put("timesRated", updatedTimesRated);
                                eventObject.put("ratingsSum", updatedRatingsSum);
                                eventObject.put("rating", updatedRating);
                            }

                            eventObject.saveInBackground();

                        }
                    } else {

                    }
                }
            });

            if (rating > initialRating) {
                intent.putExtra(EXTRA_RATING, "" + (initialRating+1));
                selectionIntent.putExtra(EXTRA_RATING, "" + (initialRating+1));
            } else if (rating < initialRating) {
                intent.putExtra(EXTRA_RATING, "" + (initialRating-1));
                selectionIntent.putExtra(EXTRA_RATING, "" + (initialRating-1));
            } else {
                intent.putExtra(EXTRA_RATING, "" + updatedRating);
                selectionIntent.putExtra(EXTRA_RATING, "" + updatedRating);
            }
            startActivity(intent);

        } else if (radioButtonId == 2131558540) {
            // Use current location
            intent.putExtra(EXTRA_LAT, String.valueOf(mLastLocation.getLatitude()));
            intent.putExtra(EXTRA_LAT, String.valueOf(mLastLocation.getLongitude()));

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + objectList.size() + " scores");
                        for (ParseObject eventObject : objectList) {
                            EditText titleText = (EditText) findViewById(R.id.title);
                            String title = titleText.getText().toString();
                            eventObject.put("title", title);

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

                    }
                }
            });

            startActivity(intent);

        } else {
            startActivity(selectionIntent);
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
