package kairos.Kickback;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    Switch switchon;
    Spinner spcalendars;
    EditText  defaultMessage;
    Switch switchTitle;
    TextView preview;

    ArrayAdapter<String> dataAdapter;

    ListView LV;



    String PREF_NAME;
    String CAL_IDS;
    String SWITCH_VAL;
    String TITLE_SWITCH;
    String TITLE_ID;
    String MESSAGE;

    List<String> calendars = new ArrayList<String>();
    IntentFilter intentFilter;

    Helper helper;

    ArrayList mSelectedItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("my initial");

        PREF_NAME = getResources().getString(R.string.PREF_NAME);
        CAL_IDS = getResources().getString(R.string.CAL_IDS);
        SWITCH_VAL = getResources().getString(R.string.SWITCH_VAL);
        TITLE_SWITCH = getResources().getString(R.string.TITLE_SWITCH);
        TITLE_ID = getResources().getString(R.string.TITLE_ID);
        MESSAGE= getResources().getString(R.string.MESSAGE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new Helper(this);

        /*
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        */


        preview =(TextView) findViewById(R.id.textView2);
        preview.setMovementMethod(new ScrollingMovementMethod());


        defaultMessage=(EditText) findViewById(R.id.message);
        setMessage();
        defaultMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setMessage();
                preview.setText(helper.getMessage());
            }
        });

        switchon= (Switch) findViewById(R.id.switch1);
        switchon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                helper.setSWITCH_VAL(isChecked);
            }
        });

        switchTitle= (Switch) findViewById(R.id.switch2);
        switchTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                helper.setTITLE_SWITCH(isChecked);
                setMessage();
                preview.setText(helper.getMessage());

            }
        });


        Button button =(Button) findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewSetup();
            }
        });

        setup();
    }


    public void listViewSetup()
    {
        getCals();
        if (mSelectedItems==null)
            mSelectedItems=new ArrayList();

        mSelectedItems.clear();


        String[] calendars2 = calendars.toArray(new String[calendars.size()]);
        boolean [] calSelected = new boolean[calendars.size()];

        Set<String> selectedCalIDs=helper.getCAL_IDS();
        mSelectedItems.addAll(selectedCalIDs);
        for (int i = 0; i < calSelected.length ; i++) {
            if (selectedCalIDs.contains(String.valueOf(i+1))) {

                calSelected[i] = true;
                System.out.println("my "+i+" true");
            }
            else {
                calSelected[i] = false;
                System.out.println("my "+i+" false");
            }
        }
        for (String str:selectedCalIDs)
        {
            System.out.println("my hashset contains "+str);
        }


        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setMultiChoiceItems(calendars2, calSelected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                System.out.println("my value of which "+(which+1));

                if (isChecked) {
                    mSelectedItems.add(String.valueOf(which+1));

                } else if (mSelectedItems.contains(String.valueOf(which+1))) {
                    mSelectedItems.remove(String.valueOf(which+1));

                }
            }
        });
        d.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Set<String> temp = new HashSet<String>();
                temp.addAll(mSelectedItems);
                helper.setCAL_IDS(temp);
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });



        AlertDialog dialog = d.create();
        dialog.show();


    }






    public void  setMessage(){
        String message=defaultMessage.getText().toString();
        helper.setMessage(message);

    }




    public void getCals (){
        calendars.clear();

        if (checkCalPermission()==false)
            return;


        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };
        Cursor calendarCursor = getContentResolver().query(uri, projection, null, null, null);
        //calendars.add("None");
        while (calendarCursor.moveToNext())
        {
            calendars.add(calendarCursor.getString(2));
        }


    }

    public boolean checkCalPermission()
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CALENDAR)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        10);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                        calendars.clear();
                        calendars.add("None");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onResume(){

        super.onResume();
        setup();


    }

    protected void onPause() {
        super.onPause();

    }

    protected void setup()
    {
        switchset();
        titleSwitchSet();
        previewSet();
        getCals();
    }

    protected void previewSet()
    {
        preview.setText(helper.getMessage());
    }

    protected void switchset()
    {
        if (helper.getSWITCH_VAL())
            switchon.setChecked(true);
        else
            switchon.setChecked(false);
    }

    protected void titleSwitchSet()
    {

        if (helper.getTITLE_SWITCH())
            switchTitle.setChecked(true);
        else
            switchTitle.setChecked(false);
    }




}

