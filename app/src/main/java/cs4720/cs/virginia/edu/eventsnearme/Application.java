package cs4720.cs.virginia.edu.eventsnearme;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Mike on 11/23/2015.
 */
public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(ParseClass.class);
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "DVUcMPlrM3QzvY6Q66wAFmaEm1EEbzWO3FbAwJB4", "92slMVH8LFBDX80A7NNSNsv4QpyRSp9uQ1rkK0Xs");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

}
