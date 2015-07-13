package net.kjmaster.wiscalendarbeta.wis;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:CalendarManager
 * Created by kjstevo on 5/27/14.
 */
public class CalendarManager {
    private long calendarId;
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
            calendarId = calCursor.getLong(0);
            calIdList = new ArrayList<>();
            while (!calCursor.isAfterLast()) {

                try {
                    calIdList.add(calCursor.getLong(0));
                } catch (Exception e) {
                    Log.e("wis", "Error adding calenderId to list.  The message is :" + e.getMessage());
                }
                calCursor.moveToNext();

            }
        }
        calCursor.close();
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
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
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");

        if (wisLocation.getMeetTime() != null) {
            values.put(CalendarContract.Events.DTSTART, wisLocation.getMeetDate());
            values.put(CalendarContract.Events.DESCRIPTION, wisLocation.getMeetLocation() + "  " + wisLocation.getMeetTime());
        }

        cr.insert(CalendarContract.Events.CONTENT_URI, values);


    }

}
