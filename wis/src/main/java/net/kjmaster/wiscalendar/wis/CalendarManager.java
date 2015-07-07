package net.kjmaster.wiscalendar.wis;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.List;

/**
 * Class:CalendarManager
 * Created by kjstevo on 5/27/14.
 */
public class CalendarManager {
    private long calId;
    private List<Long> calIdList;
    public CalendarManager(ContentResolver contentResolver) {
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        Cursor calCursor =
                contentResolver.
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            calId = calCursor.getLong(0);
            while(!calCursor.isAfterLast()){
                calIdList.add(calCursor.getLong(0));
            }
        }
    }
    public List<Long> getCalenderList(){
        return calIdList;
    }
    public void InsertEvent(WisLocation wisLocation, ContentResolver cr) {

        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, wisLocation.getStartDate());
        values.put(CalendarContract.Events.DTEND, wisLocation.getStartDate());
        values.put(CalendarContract.Events.TITLE, wisLocation.getName());
        values.put(CalendarContract.Events.EVENT_LOCATION, wisLocation.getAddress());
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");

        if (wisLocation.getMeetTime() != null) {
            values.put(CalendarContract.Events.DTSTART, wisLocation.getMeetDate());
            values.put(CalendarContract.Events.DESCRIPTION, wisLocation.getMeetLocation() + "  " + wisLocation.getMeetTime());
        }

        cr.insert(CalendarContract.Events.CONTENT_URI, values);


    }
}
