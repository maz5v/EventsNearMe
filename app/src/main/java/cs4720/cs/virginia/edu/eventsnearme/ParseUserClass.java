package cs4720.cs.virginia.edu.eventsnearme;

/**
 * Created by Student on 12/2/2015.
 */

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseUserClass extends ParseObject{
    public String username;
    public String password;

    public ParseUserClass () {
        super();
    }

    public static ParseQuery<ParseClass> getQuery() {
        return ParseQuery.getQuery(ParseClass.class);
    }

}
