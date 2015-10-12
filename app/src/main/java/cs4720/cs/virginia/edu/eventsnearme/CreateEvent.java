package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.os.Environment;
import android.net.Uri;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class CreateEvent extends AppCompatActivity {

    public final static String EXTRA_TITLE = "cs4720.cs.virginia.edu.eventsnearme.TITLE";
    public final static String EXTRA_DESCRIPTION = "cs4720.cs.virginia.edu.eventsnearme.DESCRIPTION";
    public final static String EXTRA_TAG1 = "cs4720.cs.virginia.edu.eventsnearme.TAG1";
    public final static String EXTRA_TAG2 = "cs4720.cs.virginia.edu.eventsnearme.TAG2";
    public final static String EXTRA_TAG3 = "cs4720.cs.virginia.edu.eventsnearme.TAG3";
    public final static String PHOTO_URI = "cs4720.cs.virginia.edu.eventsnearme.PHOTOURI";
    public final static String EXTRA_RATING = "cs4720.cs.virginia.edu.eventsnearme.RATING";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PICK_PHOTO = 2;

    private final String file = "eventDataFile";
    File photoFile = null;
    Uri photoURI = null;
    String mCurrentPhotoPath;
    public int rating = 5;


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
            if(photoFile != null)
                finalString = finalString + " Image: " + photoURI.toString();
            else finalString = finalString + " Image: NO_IMAGE";
            finalString = finalString + "Rating: " + rating;
            finalString = finalString + " ||| ";
            output.write(finalString.getBytes());
            Log.d("Error Checking: ", finalString);
            output.close();
        } catch (Exception e) {
            Log.i("Exception writing file", e.getMessage());
        }

        if (photoFile != null)
            intent.putExtra(PHOTO_URI, photoURI.toString());
        else intent.putExtra(PHOTO_URI, "NO_IMAGE");
        String ratingString = "" + rating;
        intent.putExtra(EXTRA_RATING, ratingString);

        startActivity(intent);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("IOException occurred: ", ex.getMessage());
            }
            // Continue only if the File was successfully created
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
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Log.i("requestCode: ", ""+requestCode);
        Log.i("resultCode: ", ""+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                photoURI = Uri.fromFile(photoFile);
                Log.i("Uri.fromFile: ", photoURI.toString());
            }
        }
        if (requestCode == REQUEST_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                Log.i("data.getData: ", data.getDataString());
                photoURI = data.getData();
            }
        }
    }

    public void upVote(View view) {
        if(rating == 10) return;
        rating++;
        TextView ratingView = (TextView) findViewById(R.id.eventRating);
        ratingView.setText(""+rating);
    }

    public void downVote(View view) {
        if (rating == 1) return;
        rating--;
        TextView ratingView = (TextView) findViewById(R.id.eventRating);
        ratingView.setText(""+rating);
    }
}
