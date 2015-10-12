package cs4720.cs.virginia.edu.eventsnearme;

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

import java.io.FileInputStream;
import java.util.ArrayList;

public class EventsPage extends AppCompatActivity {

    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";

    private String[] titles;
    private ArrayList<String> tempTitles = new ArrayList<>();
    private String fileInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page);
        try {
            FileInputStream input = openFileInput("eventDataFile");
            int character;
            String temp = "";
            while((character = input.read()) != -1) {
                temp = temp + Character.toString((char)character);
            }

            fileInfo = temp;
            Log.d("File info", fileInfo);

            while(true) {
                int index = temp.indexOf("Title: ");
                if (index == -1) {
                    break;
                }
                temp = temp.substring(index + 7);
                int index2 = temp.indexOf(" ");
                String listInput = temp.substring(0, index2);
                tempTitles.add(listInput);
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
            Log.i("Tag 1: ", tag1);
            intent.putExtra(EXTRA_TAG1, tag1);

            index = temp.indexOf("Tag 2: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf("Tag 3:");
            String tag2 = temp.substring(0, spaceIndex);
            Log.i("Tag 2: ", tag2);
            intent.putExtra(EXTRA_TAG2, tag2);

            index = temp.indexOf("Tag 3: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf(" Image:");
            String tag3 = temp.substring(0, spaceIndex);
            Log.i("Tag 3: ", tag3);
            intent.putExtra(EXTRA_TAG3, tag3);

            index = temp.indexOf("Image: ");
            temp = temp.substring(index + 7);
            spaceIndex = temp.indexOf(" |||");
            String imageURI = temp.substring(0, spaceIndex);
            Log.i("Image URI: ", imageURI);
            intent.putExtra(PHOTO_URI, imageURI);

            startActivity(intent);

        }
    };

}
