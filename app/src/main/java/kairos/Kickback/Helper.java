package kairos.Kickback;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jimmy on 10/28/2015.
 */
public class Helper extends AppCompatActivity{

    String PREF_NAME;
    String CAL_IDS;
    String SWITCH_VAL;
    String TITLE_SWITCH;
    String TITLE_ID;
    String MESSAGE;

    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    EditText defaultMessage;

    public  Helper(Context context){



        this.context=context;
        PREF_NAME = context.getResources().getString(R.string.PREF_NAME);
        MESSAGE= context.getResources().getString(R.string.MESSAGE);

        prefs = context.getSharedPreferences(PREF_NAME, 0);
        editor = prefs.edit();
        CAL_IDS = context.getResources().getString(R.string.CAL_IDS);
        SWITCH_VAL = context.getResources().getString(R.string.SWITCH_VAL);
        TITLE_SWITCH = context.getResources().getString(R.string.TITLE_SWITCH);
        TITLE_ID = context.getResources().getString(R.string.TITLE_ID);
    }

    protected String getMessage()
    {
        return(prefs.getString(MESSAGE,""));
    }

    protected void setMessage(String message)
    {
        if (message.length()==0)
            message=context.getResources().getString(R.string.hint);
        if(getTITLE_SWITCH()){
            String title =getTITLE_ID();
            if (title.length()==0)
                title="'Event Name'";
            message+=" I am currently at: "+title;
        }
        editor.putString(MESSAGE,message);
        editor.commit();
    }

    protected Set<String> getCAL_IDS()
    {
        return(prefs.getStringSet(CAL_IDS,new HashSet<String>()));
    }

    protected void setCAL_IDS(Set<String> cal_ids)
    {
        editor.putStringSet(CAL_IDS,cal_ids);
        editor.commit();
    }

    protected boolean getSWITCH_VAL()
    {
        return (prefs.getBoolean(SWITCH_VAL,false));
    }

    protected void setSWITCH_VAL(boolean b)
    {
        editor.putBoolean(SWITCH_VAL,b);
        editor.commit();
    }

    protected boolean getTITLE_SWITCH()
    {
        return (prefs.getBoolean(TITLE_SWITCH,false));
    }

    protected void setTITLE_SWITCH(boolean b)
    {
        editor.putBoolean(TITLE_SWITCH,b);
        editor.commit();
    }

    protected String getTITLE_ID()
    {
        return (prefs.getString(TITLE_ID,"'Event Name'"));
    }

    protected void setTITLE_ID(String title)
    {
        editor.putString(TITLE_ID, title);
        editor.commit();
    }








}
