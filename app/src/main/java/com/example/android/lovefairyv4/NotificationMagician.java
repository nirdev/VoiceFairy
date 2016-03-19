package com.example.android.lovefairyv4;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import com.example.android.lovefairyv4.SQLite.DataBaseHandler;


public class NotificationMagician extends IntentService {

    public static String PHONE_NUMBER = "phonenumber";
    public static String CALL_TYPE = "calltype";
    public NotificationMagician() {
        super("NotificationMagician");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String[] answers = new String[3];
        Log.wtf("here", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$4");
        String mcalltype = intent.getStringExtra(CALL_TYPE);
        String mPhoneNumber = intent.getStringExtra(PHONE_NUMBER);
        Log.wtf("here ", " calltype " + mcalltype + " phonenumber " + mPhoneNumber);

        DataBaseHandler db = new DataBaseHandler(this);

        //First time talk
        if (!db.isExists(mPhoneNumber)){
            answers[0] = "That Don't sound like you are good friends";
            answers[1] =   "I wouldn't trust this guy yet";
            answers[2] =   "His/Her voice was shaky - indicates for lack of intimacy";
        }
        //The call was from Incoming Top 5
        else if (db.isTopFive(mPhoneNumber,DataBaseHandler.KEY_INCOMING_COUNTER)){

            answers[0] = "He / She truly care about you. You should keep him / her close.";
            answers[1] = "His / Her voice low tones are very calm which indicates about intimacy and concern";
            answers[2] = "That's a BFF's talk for sure!";
        }
        //Is top five on out going
        else if (db.isTopFive(mPhoneNumber,DataBaseHandler.KEY_OUTGOING_COUNTER)){
            answers[0] = "Sounds like you care about him more than he/she cares about you";
            answers[1] = "He / She truly care about you, but He / She sounds busy right now ";
            answers[2] = "His / Her voice low tones are too calm which indicates lack of attention ";
        }
        //is top five on missed
        else if (db.isTopFive(mPhoneNumber,DataBaseHandler.KEY_MISSED_COUNTER)){
            answers[0] = "He / She is really eager to speak to you";
            answers[1] = "He / She feeling nerves from your vocal presence ";
            answers[2] = "His / Her tone pace is slightly above average which" +
                    " indicates his / her stress and uncomfortableness ";
        }else
        {

        }
    }


    private void creatNotification(String msgBody){

        Notification n  = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_check_white_48dp)
                .setAutoCancel(true).build();
    }
}
