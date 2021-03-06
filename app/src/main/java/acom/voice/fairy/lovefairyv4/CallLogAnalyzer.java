package acom.voice.fairy.lovefairyv4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.HashMap;

import acom.voice.fairy.lovefairyv4.SQLite.DataBaseHandler;

/**
 * Created by Nir on 3/18/2016.
 */
public class CallLogAnalyzer extends AsyncTask<Context,Void,Void> {


    private static HashMap<String,Integer> incomingCounterHM = new HashMap<>();
    private static HashMap<String,Integer> outgoingCounterHM = new HashMap<>();
    private static HashMap<String,Integer> missedCounterHM = new HashMap<>();
    private static HashMap<String,Integer> durationHM = new HashMap<>();
    private static Integer i1 = null;
    private static Integer i2 = null;
    private static Context context;

    @Override
    protected Void doInBackground(Context... params) {

        context = params[0];
        getCallLogDetails(context);
//        for (Map.Entry<String, Integer> keyset : durationHM.entrySet()){
//
//            String key = keyset.getKey();
//            Integer duration = keyset.getValue();
//
//            Log.e("CallLogSummary" , "Phone: " + key +
//                    " Duration: " + duration +
//                    " IncomingCounter: " + incomingCounterHM.get(key) +
//                    " Outgoing: " + outgoingCounterHM.get(key) +
//                    " Missed: " + missedCounterHM.get(key));
//
//        }
        DataBaseHandler db = new DataBaseHandler(context);
        db.addMultipleCallLogs(durationHM,incomingCounterHM,outgoingCounterHM,missedCounterHM);

        //db.addCallLog("5",999,DataBaseHandler.KEY_INCOMING_COUNTER);
        //Log.e("isTopFive ", db.isTopFive("0528581457", DataBaseHandler.KEY_INCOMING_COUNTER) + "");

        return null;
    }

    private static void getCallLogDetails(Context context) {

        StringBuffer stringBuffer = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            Log.wtf("CallLogAnalyzer: ", "getCallLogDetails: no user permission");

        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");

        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);


        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDuration = cursor.getString(duration);


            //set call Duration HashMap
            i2 = durationHM.get(phNumber);
            if(i2 == null) durationHM.put(phNumber, Integer.parseInt(callDuration));
            else durationHM.put(phNumber, i2 + Integer.parseInt(callDuration));


//            String callDate = cursor.getString(date);
//            Date callDayTime = new Date(Long.valueOf(callDate));


            //Set Call Types HashMaps Counters
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:

                    i1 = outgoingCounterHM.get(phNumber);
                    if(i1 == null) outgoingCounterHM.put(phNumber, 1);
                    else outgoingCounterHM.put(phNumber, i1 + 1);

                    break;
                case CallLog.Calls.INCOMING_TYPE:

                    i1 = incomingCounterHM.get(phNumber);
                    if(i1 == null) incomingCounterHM.put(phNumber, 1);
                    else incomingCounterHM.put(phNumber, i1 + 1);

                    break;

                case CallLog.Calls.MISSED_TYPE:

                    i1 = missedCounterHM.get(phNumber);
                    if(i1 == null) missedCounterHM.put(phNumber, 1);
                    else missedCounterHM.put(phNumber, i1 + 1);

                    break;
            }

        }
        cursor.close();
    }


}
