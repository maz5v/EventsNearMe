package cs4720.cs.virginia.edu.eventsnearme;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Mike on 11/23/2015.
 */
@ParseClassName("ParseCLass")
public class ParseClass extends ParseObject {

    public String title;
    public String description;
    public String latitude;
    public String longitude;
    public int rating;
    public String tag1;
    public String tag2;
    public String tag3;

    public ParseClass () {
        super();
    }

    public static ParseQuery<ParseClass> getQuery() {
        return ParseQuery.getQuery(ParseClass.class);
    }

}
