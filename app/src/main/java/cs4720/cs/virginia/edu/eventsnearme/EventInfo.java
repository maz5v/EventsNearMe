package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class EventInfo extends AppCompatActivity {

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

    private String myTag1 = "";
    private String myTag2 = "";
    private String myTag3 = "";
    private String myLat = "";
    private String myLong = "";
    private String myPhoto = "";
    private String myId = "";
    private String user;
    private int rating;
    private int initialRating;
    private boolean logged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Intent intent = getIntent();

        String title = intent.getStringExtra(CreateEvent.EXTRA_TITLE);
        TextView titleText = (TextView)findViewById(R.id.title);
        titleText.setText("Title: " + title);

        String description = intent.getStringExtra(CreateEvent.EXTRA_DESCRIPTION);
        TextView descriptionText = (TextView)findViewById(R.id.description);
        descriptionText.setText("Description: " + description);

        String tag1 = intent.getStringExtra(CreateEvent.EXTRA_TAG1);
        String tag2 = intent.getStringExtra(CreateEvent.EXTRA_TAG2);
        String tag3 = intent.getStringExtra(CreateEvent.EXTRA_TAG3);
        myTag1 = tag1;
        myTag2 = tag2;
        myTag3 = tag3;
        String tag = "Selected tags: ";
        if(tag1.length() > 0) {
            tag = tag + '\n' + tag1;
        }
        if(tag2.length() > 0) {
            tag = tag + '\n' + tag2;
        }
        if(tag3.length() > 0) {
            tag = tag + '\n' + tag3;
        }
        TextView tagsText = (TextView)findViewById(R.id.tags);
        tagsText.setText(tag);

        final ImageView eventImage = (ImageView)findViewById(R.id.eventPicture);
        String sender = intent.getStringExtra(CreateEvent.EXTRA_SENDER);
        if (sender.equals("CreateEvent")) {
            Log.d("reached here", "reached here");
            String imageURIString = intent.getStringExtra(CreateEvent.PHOTO_URI);
            if (imageURIString.equals("NO_IMAGE")) {
                eventImage.setVisibility(View.GONE);
            } else eventImage.setImageURI(Uri.parse(imageURIString));
        } else if (sender.equals("EventsMap") || sender.equals("EventsPage")) {
            Log.d("reached here 1", "reached here 1");
            String imageURIString = intent.getStringExtra(CreateEvent.PHOTO_URI);
            if (imageURIString.equals("NO_IMAGE")) {
                eventImage.setVisibility(View.GONE);
            } else {
                Log.d("reached here 2", "reached here 2");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
                query.whereEqualTo("eventId", CreateEvent.EXTRA_EVENTID);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objectList, ParseException e) {
                        if (e == null) {
                            for (ParseObject o : objectList) {
                                ParseFile imageFile = (ParseFile)o.get("image");
                                imageFile.getDataInBackground(new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            // data has the bytes for the resume
                                            Bitmap bmpNew = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            eventImage.setImageBitmap(bmpNew);
                                        } else {
                                            // something went wrong
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }

        String lat = intent.getStringExtra(CreateEvent.EXTRA_LAT);
        myLat = lat;
        String lon = intent.getStringExtra(CreateEvent.EXTRA_LONG);
        myLong = lon;
        String photo = intent.getStringExtra(CreateEvent.PHOTO_URI);
        myPhoto = photo;

        String rating2 = intent.getStringExtra(CreateEvent.EXTRA_RATING);
        TextView eventRating = (TextView)findViewById(R.id.eventRatingInfo);
        eventRating.setText("" + rating2);

        String id = intent.getStringExtra(CreateEvent.EXTRA_EVENTID);
        myId = id;

        logged = intent.getBooleanExtra(CreateEvent.EXTRA_LOGGED, false);
        user = intent.getStringExtra(CreateEvent.EXTRA_USERNAME);

        rating = Integer.parseInt(rating2);
        initialRating = Integer.parseInt(rating2);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Instead of checking title, we want to check ID
        if (rating != initialRating) {
            TextView titleText = (TextView)findViewById(R.id.title);
            String myTitle = titleText.getText().toString();
            myTitle = myTitle.substring(7);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
            query.whereEqualTo("title", myTitle);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        for (ParseObject o : objectList) {
                            int updatedTimesRated = o.getNumber("timesRated").intValue() + 1;
                            int updatedRatingsSum = o.getNumber("ratingsSum").intValue() + rating;
                            int updatedRating = updatedRatingsSum / updatedTimesRated;
                            o.put("timesRated", updatedTimesRated);
                            o.put("ratingsSum", updatedRatingsSum);
                            o.put("rating", updatedRating);
                            o.saveInBackground();
                        }
                    }
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_info, menu);
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

    public void upVote(View view) {
        if(rating == 10) return;
        rating++;
        TextView ratingView = (TextView) findViewById(R.id.eventRatingInfo);
        ratingView.setText("" + rating);
    }

    public void downVote(View view) {
        if (rating == 1) return;
        rating--;
        TextView ratingView = (TextView) findViewById(R.id.eventRatingInfo);
        ratingView.setText("" + rating);
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, WelcomePage.class);
        intent.putExtra(EXTRA_USERNAME, user);
        intent.putExtra(EXTRA_LOGGED, logged);
        Log.i("My user", user);
        Log.i("Logged IN", ""+logged);
        startActivity(intent);
    }

    public void editEvent(View view) {
        Intent intent = new Intent(this, EditEvent.class);

        TextView titleText = (TextView)findViewById(R.id.title);
        String title = titleText.getText().toString();
        title = title.substring(7);
        intent.putExtra(EXTRA_TITLE, title);

        TextView descriptionText = (TextView)findViewById(R.id.description);
        String description = descriptionText.getText().toString();
        description = description.substring(13);
        intent.putExtra(EXTRA_DESCRIPTION, description);

        TextView ratingText = (TextView)findViewById(R.id.eventRatingInfo);
        String rating2 = ratingText.getText().toString();
        intent.putExtra(EXTRA_RATING, rating2);

        intent.putExtra(EXTRA_TAG1, myTag1);
        intent.putExtra(EXTRA_TAG2, myTag2);
        intent.putExtra(EXTRA_TAG3, myTag3);
        intent.putExtra(EXTRA_LAT, myLat);
        intent.putExtra(EXTRA_LONG, myLong);
        intent.putExtra(PHOTO_URI, myPhoto);
        intent.putExtra(EXTRA_EVENTID, myId);
        intent.putExtra(EXTRA_USERNAME, user);
        intent.putExtra(EXTRA_LOGGED, logged);

        startActivity(intent);
    }

    public void deleteEvent(View view) {

    }

}
