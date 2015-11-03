package kairos.Kickback;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.telephony.SmsManager;
import android.text.format.DateUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jimmy on 10/24/2015.
 */
public class Sender extends IntentService {

    boolean busy;

    String PREF_NAME;
    String CAL_IDS;
    String SWITCH_VAL;
    String TITLE_SWITCH;
    String TITLE_ID;
    Helper helper;



    public Sender(){
        super("Sender");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PREF_NAME = getResources().getString(R.string.PREF_NAME);
        CAL_IDS = getResources().getString(R.string.CAL_IDS);
        SWITCH_VAL = getResources().getString(R.string.SWITCH_VAL);
        TITLE_ID=getResources().getString(R.string.TITLE_ID);
        System.out.println("my inside of sender class on handle");
        helper = new Helper(this);
        System.out.println("before isbusy");
        busy=isBusy();

        boolean autoRespond = helper.getSWITCH_VAL();
        System.out.println("after message isbusy 23");
        String number= intent.getExtras().getString("number");
        System.out.println("after message isbusy 25");

        String message = helper.getMessage();






        System.out.println("my auto =" + autoRespond + "busy = " + busy);
        if (autoRespond &&busy)
        {
            if (message.length()==0)
            {
                message = this.getResources().getString(R.string.hint);
            }
            System.out.println("my message " + message);
            sendSMS(number, message);
        }

    }
    /*
    True if calendar is currently busy
    False if calendar is free
     */
    protected Boolean isBusy()
    {
        String[] Projection = new String[]{
                CalendarContract.Instances.CALENDAR_ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.AVAILABILITY,
                CalendarContract.Instances.ALL_DAY

        };


        Cursor calendarCursor=null;
        ContentResolver cr = getContentResolver();


        long startMillis = System.currentTimeMillis()- DateUtils.DAY_IN_MILLIS;
        long endMillis = System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS;
        long nowMillis =System.currentTimeMillis();


        Set<String> cal_id=helper.getCAL_IDS();
        String [] args = new String[cal_id.size()];
        System.out.println("my cal_id size "+cal_id.size());
        int i=0;
        String helper1="(";
        for (String str:cal_id)
        {
            args[i]=str;
            System.out.println("my args at "+i+" "+args[i]);
            i++;
            helper1+="?,";
        }
        if (i==0)
            return false;
        helper1= helper1.substring(0,helper1.length()-1);
        helper1+=")";
        System.out.println("my helper "+helper1);


        String selection = "CALENDAR_ID in"+helper1+"AND END>='"+nowMillis+"' AND BEGIN<='"+nowMillis+"'";// AND AVAILABILITY='0'";

        //Uri CONTENT_URI = Uri.parse("content://com.android.calendar");
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        System.out.println("my test before cursor");

        calendarCursor = cr.query(builder.build(),
                Projection,
                selection,
                args,
                null);

        System.out.println("my test after cursor");


        while (calendarCursor.moveToNext()){
            String calendar = calendarCursor.getString(0);
            String begin = calendarCursor.getString(1);
            String end = calendarCursor.getString(2);
            String title = calendarCursor.getString(3);
            String availability = calendarCursor.getString(4);
            boolean allDay = calendarCursor.getString(5).equals("1");
           helper.setTITLE_ID(title);
            System.out.println("my events    title: " + title + " calendar: " + calendar + " allDay " + allDay);
            if(!allDay)
                return true;

        }
       helper.setTITLE_ID(null);
        System.out.println("my test end");
        return false;
    }

    private void sendSMS(String number, String text)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number,null,text,null,null);
    }
}
