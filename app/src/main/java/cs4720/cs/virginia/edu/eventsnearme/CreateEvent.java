package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CreateEvent extends AppCompatActivity {

    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";

    private final String file = "eventDataFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
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

    public void shareEvent(View view) {
        Intent intent = new Intent(this, EventInfo.class);

        EditText titleText = (EditText) findViewById(R.id.title);
        String title = titleText.getText().toString();
        intent.putExtra(EXTRA_TITLE, title);

        EditText descriptionText = (EditText) findViewById(R.id.description);
        String description = descriptionText.getText().toString();
        intent.putExtra(EXTRA_DESCRIPTION, description);

        ToggleButton tagButton1 = (ToggleButton) findViewById(R.id.tag1);
        String tag1 = "";
        if(tagButton1.isChecked()) {
            tag1 = tagButton1.getText().toString();
        }
        intent.putExtra(EXTRA_TAG1, tag1);

        ToggleButton tagButton2 = (ToggleButton) findViewById(R.id.tag2);
        String tag2 = "";
        if(tagButton2.isChecked()) {
            tag2 = tagButton2.getText().toString();
        }
        intent.putExtra(EXTRA_TAG2, tag2);

        ToggleButton tagButton3 = (ToggleButton) findViewById(R.id.tag3);
        String tag3 = "";
        if(tagButton3.isChecked()) {
            tag3 = tagButton3.getText().toString();
        }
        intent.putExtra(EXTRA_TAG3, tag3);

        try {
            FileOutputStream output = openFileOutput(file, Context.MODE_APPEND);
            String finalString = "";
            finalString = finalString + "Title: " + title;
            finalString = finalString + " Description: " + description;
            finalString = finalString + " Tag 1: " + tag1;
            finalString = finalString + " Tag 2: " + tag2;
            finalString = finalString + " Tag 3: " + tag3;
            finalString = finalString + " ||| ";
            output.write(finalString.getBytes());
            Log.d("Error Checking: ", finalString);
            output.close();
        } catch (Exception e) {
            
        }

        startActivity(intent);
    }
}
