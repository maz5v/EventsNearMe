package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.Parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class WelcomePage extends AppCompatActivity {

    private final String file = "eventDataFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_page, menu);
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

    public void findSomethingNearMe(View view) {
        Intent intent = new Intent(this, EventsMap.class);
        startActivity(intent);
    }

    public void shareAnEvent(View view) {
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }

    public void goToEvents(View view) {
        Intent intent = new Intent(this, EventsPage.class);
        startActivity(intent);
    }

    public void goToNewsfeed(View view) {
        Intent intent = new Intent(this, Newsfeed.class);
        startActivity(intent);
    }

    public void clearFile(View view) {
        try {
            FileOutputStream output = openFileOutput(file, Context.MODE_PRIVATE);
            output.close();
        } catch(Exception e) {
            Log.i("Exception writing file", e.getMessage());
        }

    }

    public void fillNewsfeedFile(View view) {
        try {
            FileOutputStream output = openFileOutput("newsfeedDataFile", Context.MODE_APPEND);
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

}
