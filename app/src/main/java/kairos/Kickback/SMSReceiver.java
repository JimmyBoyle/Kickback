package kairos.Kickback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


/**
 * Created by jimmy on 10/14/2015.
 */
public class SMSReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive (Context context,Intent intent)
    {
        
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (msgs[i].getOriginatingAddress().length()>7) {
                    Intent text = new Intent(context, Sender.class);
                    text.putExtra("number", msgs[i].getOriginatingAddress());
                    context.startService(text);
                }
            }

        }
    }





}
