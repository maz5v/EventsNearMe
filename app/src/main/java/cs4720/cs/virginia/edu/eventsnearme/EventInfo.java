package cs4720.cs.virginia.edu.eventsnearme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class EventInfo extends AppCompatActivity {

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

    public void returnHome(View view) {
        Intent intent = new Intent(this, WelcomePage.class);
        startActivity(intent);
    }

}
