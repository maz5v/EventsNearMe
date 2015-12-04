package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsPage extends AppCompatActivity {

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

    private String[] titles;
    private ArrayList<String> tempTitles = new ArrayList<>();
    private String fileInfo = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page);

        Intent intent = getIntent();
        user = intent.getStringExtra(WelcomePage.EXTRA_USERNAME);
        logged = intent.getBooleanExtra(WelcomePage.EXTRA_LOGGED, false);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseCLass");
        query.whereEqualTo("userName", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objectList, ParseException e) {

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
                        if (o.get("image") != null) {
                            images1.add("getImage");
                        } else {
                            images1.add("NO_IMAGE");
                        }
                        ratings1.add(Integer.toString((Integer) o.get("rating")));
                        myIndex++;
                    }
                } else {
                    //Log.d("score", "Error: " + e.getMessage());
                }

                Log.i("tempTitles1 size", "" + tempTitles1.size());
                titles = new String[tempTitles1.size()];
                for (int i = 0; i < tempTitles1.size(); i++) {
                    titles[i] = tempTitles1.get(i);
                }

                Context context = getApplicationContext();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, titles);
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(mMessageClickedHandler);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events_page, menu);
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

    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), EventInfo.class);

            intent.putExtra(EXTRA_TITLE, tempTitles1.get(position));
            intent.putExtra(EXTRA_DESCRIPTION, descriptions1.get(position));
            intent.putExtra(EXTRA_EVENTID, ids1.get(position));
            intent.putExtra(EXTRA_RATING, ratings1.get(position));
            intent.putExtra(EXTRA_TAG1, tag1s1.get(position));
            intent.putExtra(EXTRA_TAG2, tag2s1.get(position));
            intent.putExtra(EXTRA_TAG3, tag3s1.get(position));
            intent.putExtra(EXTRA_LAT, latitudes1.get(position));
            intent.putExtra(EXTRA_LONG, longitudes1.get(position));
            intent.putExtra(PHOTO_URI, images1.get(position));
            intent.putExtra(EXTRA_USERNAME, user);
            intent.putExtra(EXTRA_LOGGED, logged);
            intent.putExtra(EXTRA_SENDER, "EventsPage");
            
            startActivity(intent);

        }
    };

}
