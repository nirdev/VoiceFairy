package acom.voice.fairy.lovefairyv4;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

import com.example.android.lovefairyv4.R;

import java.util.Random;

import acom.voice.fairy.lovefairyv4.SQLite.DataBaseHandler;


public class NotificationMagician extends IntentService {

    public static String PHONE_NUMBER = "phonenumber";
    public static String CALL_TYPE = "calltype";
    public static String DURATION = "callduration";
    public NotificationMagician() {
        super("NotificationMagician");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String[] answers = new String[3];
        String mcalltype = intent.getStringExtra(CALL_TYPE);
        String mPhoneNumber = intent.getStringExtra(PHONE_NUMBER);
        int mDuration = intent.getIntExtra(DURATION,0);


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
        }
        //
        else {
            answers[0] = "He / She is happy to speak to you";
            answers[1] = "He / She feeling generally calm while talking to you ";
            answers[2] = "His / Her voice high tones pace is slightly above average which " +
                    "indicates high heart beats rate ";
        }

        //Save current call on database
        db.addCallLog(mPhoneNumber,mDuration,mcalltype);
        createNotification(answers);
    }


    private void createNotification(String[] msgBody){

        int rand = new Random().nextInt(msgBody.length);
        String answer = (msgBody[rand]);

        Notification n  = new Notification.Builder(this)
                .setContentTitle("Voice Analysis")
                .setContentText(answer)
                .setSmallIcon(R.drawable.ic_hearing_white_24dp)
                .setStyle(new Notification.BigTextStyle().bigText(answer))
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,n);
    }

}
