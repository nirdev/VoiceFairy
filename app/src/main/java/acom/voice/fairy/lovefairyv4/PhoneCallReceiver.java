package acom.voice.fairy.lovefairyv4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.Date;

import acom.voice.fairy.lovefairyv4.SQLite.DataBaseHandler;

// taken from https://stackoverflow.com/questions/16878840/android-incoming-outgoing-calls/16888683#16888683?newreg=20be99b404ea4f00bc8db04bf90be3e0
public class PhoneCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    static PhonecallStartEndDetector listener;
    Intent i;
    int callDuration;
    protected Context savedContext;


    @Override

    public void onReceive(Context context, Intent intent) {
        savedContext = context;
        i = new Intent(savedContext, NotificationMagician.class);
        if(listener == null){
            listener = new PhonecallStartEndDetector();
        }

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
            return;
        }

        //The other intent tells us the phone state changed.  Here we set a listener to deal with it
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }


    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(String number, Date start){}
    protected void onOutgoingCallStarted(String number, Date start){}

    protected void onIncomingCallEnded(String number, Date start, Date end){

        //Only notify if call is not to short
        callDuration = (int) (end.getTime() - start.getTime())/1000;
        if ( callDuration > 60) {
            i.putExtra(NotificationMagician.PHONE_NUMBER, number);
            i.putExtra(NotificationMagician.CALL_TYPE, DataBaseHandler.KEY_INCOMING_COUNTER);
            i.putExtra(NotificationMagician.DURATION,callDuration);
            savedContext.startService(i);
        }else { //Just save
            DataBaseHandler db = new DataBaseHandler(savedContext);
            db.addCallLog(number,callDuration,DataBaseHandler.KEY_INCOMING_COUNTER);
        }

    }
    protected void onOutgoingCallEnded(String number, Date start, Date end){

        //Only notify if call is not to short
        callDuration = (int) (end.getTime() - start.getTime())/1000;
        if (callDuration > 70) {
            i.putExtra(NotificationMagician.PHONE_NUMBER, number);
            i.putExtra(NotificationMagician.CALL_TYPE, DataBaseHandler.KEY_OUTGOING_COUNTER);
            i.putExtra(NotificationMagician.DURATION,callDuration);
            savedContext.startService(i);
        }else { //Just save
            DataBaseHandler db = new DataBaseHandler(savedContext);
            db.addCallLog(number,callDuration,DataBaseHandler.KEY_OUTGOING_COUNTER);
        }
    }
    protected void onMissedCall(String number, Date start){

        DataBaseHandler db = new DataBaseHandler(savedContext);
        db.addCallLog(number,0,DataBaseHandler.KEY_MISSED_COUNTER);

    }


    //Deals with actual events
    public class PhonecallStartEndDetector extends PhoneStateListener {

        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String savedNumber;  //because the passed incoming is only valid in ringing

        public PhonecallStartEndDetector() {}

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number){
            savedNumber = number;
        }

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if(lastState == state){
                //No change, debounce extras -
                //you don't know how many times broadcast receiver will be called by android - due to this you nedd to check that state has been changed this call
                return;
            }

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = incomingNumber;
                    onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing down on them
                    if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                        onOutgoingCallStarted(savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if(lastState == TelephonyManager.CALL_STATE_RINGING){
                        //Ring but no pickup-  a miss
                        onMissedCall(savedNumber, callStartTime);
                    }
                    else if(isIncoming){
                        onIncomingCallEnded(savedNumber, callStartTime, new Date());
                    }
                    else{
                        onOutgoingCallEnded(savedNumber, callStartTime, new Date());
                    }
                    break;
            }
            lastState = state;

        }

    }

}
