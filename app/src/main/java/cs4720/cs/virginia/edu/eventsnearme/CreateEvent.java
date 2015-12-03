package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Environment;
import android.net.Uri;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;


public class CreateEvent extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

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

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PICK_PHOTO = 2;

    private final String file = "eventDataFile";
    File photoFile = null;
    Uri photoURI = null;
    String mCurrentPhotoPath;
    public int rating = 5;

    private String userName;
    private boolean loggedIn;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private ParseFile imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_create_event);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();

        userName = intent.getStringExtra(WelcomePage.EXTRA_USERNAME);
        loggedIn = intent.getBooleanExtra(WelcomePage.EXTRA_LOGGED, false);

        Log.i("Username", userName);
        Log.i("Boolean value", ""+loggedIn);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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

    public void shareEvent(View view) {
        Intent intent = new Intent(this, EventInfo.class);
        Intent selectionIntent = new Intent(this, SelectOnMap.class);

        EditText titleText = (EditText) findViewById(R.id.title);
        String title = titleText.getText().toString();
        if (title.equals("")) {
            CharSequence text = "Please input a title!";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }
        intent.putExtra(EXTRA_TITLE, title);
        selectionIntent.putExtra(EXTRA_TITLE, title);

        Random random = new Random();
        int rand = random.nextInt(100000);
        String id = String.format("%06d", rand);
        id = title + id;
        intent.putExtra(EXTRA_EVENTID, id);
        selectionIntent.putExtra(EXTRA_EVENTID, id);

        EditText descriptionText = (EditText) findViewById(R.id.description);
        String description = descriptionText.getText().toString();
        if (description.equals("")) {
            CharSequence text = "Please input a description!";
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            return;
        }
        intent.putExtra(EXTRA_DESCRIPTION, description);
        selectionIntent.putExtra(EXTRA_DESCRIPTION, description);

        ToggleButton tagButton1 = (ToggleButton) findViewById(R.id.tag1);
        String tag1 = "";
        if(tagButton1.isChecked()) {
            //tag1 = tagButton1.getText().toString();
            tag1 = "Food";
        }
        intent.putExtra(EXTRA_TAG1, tag1);
        selectionIntent.putExtra(EXTRA_TAG1, tag1);

        ToggleButton tagButton2 = (ToggleButton) findViewById(R.id.tag2);
        String tag2 = "";
        if(tagButton2.isChecked()) {
            tag2 = "Entertainment";
        }
        intent.putExtra(EXTRA_TAG2, tag2);
        selectionIntent.putExtra(EXTRA_TAG2, tag2);

        ToggleButton tagButton3 = (ToggleButton) findViewById(R.id.tag3);
        String tag3 = "";
        if(tagButton3.isChecked()) {
            tag3 = "Shopping";
        }
        intent.putExtra(EXTRA_TAG3, tag3);
        selectionIntent.putExtra(EXTRA_TAG3, tag3);

        if (photoURI != null) {
            intent.putExtra(PHOTO_URI, photoURI.toString());
            selectionIntent.putExtra(PHOTO_URI, photoURI.toString());
        } else {
            intent.putExtra(PHOTO_URI, "NO_IMAGE");
            selectionIntent.putExtra(PHOTO_URI, "NO_IMAGE");
        }

        String ratingString = "" + rating;
        intent.putExtra(EXTRA_RATING, ratingString);
        selectionIntent.putExtra(EXTRA_RATING, ratingString);

        intent.putExtra(EXTRA_USERNAME, userName);
        selectionIntent.putExtra(EXTRA_USERNAME, userName);
        intent.putExtra(EXTRA_LOGGED, loggedIn);
        selectionIntent.putExtra(EXTRA_LOGGED, loggedIn);

        if (mLastLocation != null) {
            intent.putExtra(EXTRA_LAT, String.valueOf(mLastLocation.getLatitude()));
            intent.putExtra(EXTRA_LONG, String.valueOf(mLastLocation.getLongitude()));
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if (radioButtonId == -1) {
            //No button selected
            Context context = getApplicationContext();
            CharSequence text = "Please select a location option!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
        else {
            Log.i("Radio button ID: ", Integer.toString(radioButtonId));
            if (radioButtonId == 2131558540) {
                // Use current location
                try {
                    FileOutputStream output = openFileOutput(file, Context.MODE_APPEND);
                    String finalString = "";
                    finalString = finalString + "Title: " + title;
                    finalString = finalString + " Description: " + description;
                    finalString = finalString + " Tag 1: " + tag1;
                    finalString = finalString + " Tag 2: " + tag2;
                    finalString = finalString + " Tag 3: " + tag3;
                    if(photoURI != null)
                        finalString = finalString + " Image: " + photoURI.toString();
                    else finalString = finalString + " Image: NO_IMAGE";
                    finalString = finalString + " Rating: " + rating;
                    if (mLastLocation != null) {
                        finalString = finalString + " Latitude: " + String.valueOf(mLastLocation.getLatitude());
                        finalString = finalString + " Longitude: " + String.valueOf(mLastLocation.getLongitude());
                    }
                    else {
                        finalString = finalString + " Latitude: LAT_ERROR";
                        finalString = finalString + " Longitude: LONG_ERROR";
                    }
                    finalString = finalString + " ||| ";
                    output.write(finalString.getBytes());
                    Log.d("Error Checking: ", finalString);
                    output.close();
                } catch (Exception e) {
                    Log.i("Exception writing file", e.getMessage());
                }

                // Start PARSE stuff
                ParseClass eventObject = new ParseClass();
                eventObject.put("eventId", id);
                eventObject.put("title", title);
                eventObject.put("description", description);
                eventObject.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                eventObject.put("longitude", String.valueOf(mLastLocation.getLongitude()));
                eventObject.put("rating", rating);
                eventObject.put("tag1", tag1);
                eventObject.put("tag2", tag2);
                eventObject.put("tag3", tag3);
                eventObject.put("userName", userName);
                eventObject.put("ratingsSum", rating);
                eventObject.put("timesRated", 1);
                if (imageFile != null) {
                    eventObject.put("image", imageFile);
                }
                eventObject.saveInBackground();
                // Stop PARSE stuff

                startActivity(intent);
            } else {
                //Select on map
                startActivity(selectionIntent);
            }
        }

    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("IOException occurred: ", ex.getMessage());
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
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageData = stream.toByteArray();

                imageFile = new ParseFile("image.png", imageData);
                imageFile.saveInBackground();

            }
        }
        if (requestCode == REQUEST_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                photoURI = data.getData();
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageData = stream.toByteArray();

                imageFile = new ParseFile("image.png", imageData);
                imageFile.saveInBackground();
                Log.i("path", photoURI.getPath());
                Log.i("path2", photoURI.getEncodedPath());
            }
        }
    }

    public void upVote(View view) {
        if(rating == 10) return;
        rating++;
        TextView ratingView = (TextView) findViewById(R.id.eventRating);
        ratingView.setText("" + rating);
    }

    public void downVote(View view) {
        if (rating == 1) return;
        rating--;
        TextView ratingView = (TextView) findViewById(R.id.eventRating);
        ratingView.setText(""+rating);
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
