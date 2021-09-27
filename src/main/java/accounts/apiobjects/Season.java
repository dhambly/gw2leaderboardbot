package accounts.apiobjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class Season {
    String id;
    String name;
    long start;
    long end;
    boolean active;
    int databaseId;

    public void setStart(String date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        try {
            Date t = ft.parse(date);
            t.setTime(t.getTime() + TimeZone.getTimeZone("America/New_York").getOffset(t.getTime()));
            start = t.getTime();
        } catch (ParseException e) { e.printStackTrace();}
    }

    public void setEnd(String date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        try {
            Date t = ft.parse(date);
            t.setTime(t.getTime() + TimeZone.getTimeZone("America/New_York").getOffset(t.getTime()));
            end = t.getTime();
        } catch (ParseException e) { e.printStackTrace();}
    }

    public String getKey() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }
}
