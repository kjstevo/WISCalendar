package net.kjmaster.wiscalendarbeta.wis;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class:WisLocation
 * Created by kjstevo on 5/23/14.
 */
public class WisLocation {
    private String Address;
    private String Name;
    private String Time;
    private long StartDate;
    private String MeetTime;
    private long MeetDate;
    private String MeetLocation;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "WisLocation{" +
                "Address='" + Address + '\'' +
                ", Name='" + Name + '\'' +
                ", Time='" + Time + '\'' +
                ", StartDate=" + StartDate +
                ", MeetTime='" + MeetTime + '\'' +
                ", MeetDate=" + MeetDate +
                ", MeetLocation='" + MeetLocation + '\'' +
                '}';
    }

    public void setTime(String time) {
        Time = time;

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mma");
        formatter.setTimeZone(TimeZone.getDefault());
//        String dateInStringTemplate = "Friday, Jun 7, 2013 12:10:56 PM";

        try {
            Object dtime = formatter.parse(time);
            StartDate = ((Date) dtime).getTime();
        } catch (ParseException e) {
            Log.e("WisLocation", e.getMessage());
        }
    }

    public long getStartDate() {
        return StartDate;
    }


    public String getMeetTime() {
        return MeetTime;
    }

    public void setMeetTime(String meetTime) {
        MeetTime = meetTime;
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mma");
        formatter.setTimeZone(TimeZone.getDefault());
//        String dateInStringTemplate = "Jun 7 2013 12:10PM";

        try {
            MeetDate = formatter.parse(meetTime).getTime();
        } catch (ParseException e) {
            Log.e("WisLocation", e.getMessage());
        }
    }

    public long getMeetDate() {
        return MeetDate;
    }


    public String getMeetLocation() {
        return MeetLocation;
    }

    public void setMeetLocation(String meetLocation) {
        MeetLocation = meetLocation;
    }
}
