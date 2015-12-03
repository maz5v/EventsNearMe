package cs4720.cs.virginia.edu.eventsnearme;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
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

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

public class WelcomePage extends AppCompatActivity
        implements LogInDialogFragment.LoginDialogListener, RegisterUserDialogFragment.RegisterUserDialogListener {

    public final static String EXTRA_USERNAME = "cs4720.cs.virginia.edu.eventsnearme.USERNAME";
    public final static String EXTRA_LOGGED = "cs4720.cs.virginia.edu.eventsnearme.LOGGED";

    private final String file = "eventDataFile";
    private boolean loggedIn = false;
    private String userName = "";

    @Override
    public void onLoginDialogPositiveClick(DialogFragment dialog, String username, String password) {
        login(username, false, password);
    }

    /*
    @Override
    public void onLoginDialogNegativeClick(DialogFragment dialog) {

    }
    */

    @Override
    public void onRegisterUserDialogPositiveClick(DialogFragment dialog, String username, String password) {
        ParseUserClass reg = new ParseUserClass();
        reg.put("username", username);
        reg.put("password", password);
        reg.saveInBackground();
        login(username, true, "");
    }

    public void login(String user, boolean registering, String password) {
        Log.d("username", user);
        final String finalUser = user;
        final String finalPassword = password;
        if (!registering) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseUserClass");
            query.whereEqualTo("username", finalUser);
            query.whereEqualTo("password", finalPassword);
            Log.d("finalUser", finalUser);
            Log.d("finalPassword", finalPassword);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objectList, ParseException e) {
                    if (e == null) {
                        //create toast login failed message
                        if (objectList.isEmpty()) {
                            Log.d("login failed,", "login failed");
                        } else {
                            userName = finalUser;
                            loggedIn = true;
                            findViewById(R.id.button3).setVisibility(View.VISIBLE);
                            findViewById(R.id.button4).setVisibility(View.VISIBLE);
                            findViewById(R.id.button8).setVisibility(View.VISIBLE);
                            findViewById(R.id.button11).setVisibility(View.VISIBLE);
                            findViewById(R.id.loginButton).setVisibility(View.INVISIBLE);
                            findViewById(R.id.registerButton).setVisibility(View.INVISIBLE);
                        }
                    } else {
                        //error occurred
                    }
                }
            });
        } else {
            userName = user;
            loggedIn = true;
            findViewById(R.id.button3).setVisibility(View.VISIBLE);
            findViewById(R.id.button4).setVisibility(View.VISIBLE);
            findViewById(R.id.button8).setVisibility(View.VISIBLE);
            findViewById(R.id.button11).setVisibility(View.VISIBLE);
            findViewById(R.id.loginButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.registerButton).setVisibility(View.INVISIBLE);
        }
    }

    public void logout(String user) {
        userName = "";
        loggedIn = false;
        findViewById(R.id.button3).setVisibility(View.INVISIBLE);
        findViewById(R.id.button4).setVisibility(View.INVISIBLE);
        findViewById(R.id.button8).setVisibility(View.INVISIBLE);
        findViewById(R.id.button11).setVisibility(View.INVISIBLE);
        findViewById(R.id.loginButton).setVisibility(View.VISIBLE);
        findViewById(R.id.registerButton).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        Intent intent = getIntent();

        userName = "lainfiafnvfavnvadfv6i76ju";

        Log.i("Welcome user BEFORE", userName);
        Log.i("Welcome logged BEFORE", "" + loggedIn);

        if (intent.getStringExtra(EventInfo.EXTRA_USERNAME) != null) {
            userName = intent.getStringExtra(EventInfo.EXTRA_USERNAME);
            loggedIn = intent.getBooleanExtra(EventInfo.EXTRA_LOGGED, false);
        }

        Log.i("Welcome user AFTER", userName);
        Log.i("Welcome logged AFTER", "" + loggedIn);

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

    public void showLogInWindow(View view) {
        DialogFragment dialog = new LogInDialogFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "NoticeDialogFragment");

    }

    public void showRegisterWindow(View view) {
        DialogFragment dialog = new RegisterUserDialogFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "NoticeDialogFragment");
    }


    public void findSomethingNearMe(View view) {
        Intent intent = new Intent(this, EventsMap.class);
        intent.putExtra(EXTRA_LOGGED, loggedIn);
        intent.putExtra(EXTRA_USERNAME, userName);
        startActivity(intent);
    }

    public void shareAnEvent(View view) {
        Intent intent = new Intent(this, CreateEvent.class);
        intent.putExtra(EXTRA_LOGGED, loggedIn);
        intent.putExtra(EXTRA_USERNAME, userName);
        startActivity(intent);
    }

    public void goToEvents(View view) {
        Intent intent = new Intent(this, EventsPage.class);
        intent.putExtra(EXTRA_LOGGED, loggedIn);
        intent.putExtra(EXTRA_USERNAME, userName);
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
