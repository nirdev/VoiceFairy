package com.example.android.lovefairyv4;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView txtBelowMic;
    private TextView txtBelowNextBtn;
    private ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CallLogAnalyzer().execute(this);

//        Intent i = new Intent(this, MagicActivity.class);
//        startActivity(i);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        txtBelowMic = (TextView) findViewById(R.id.text_below_mic);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnNext = (ImageButton) findViewById(R.id.next_button);
        txtBelowNextBtn = (TextView) findViewById(R.id.text_below_next_button);

//
//        //CAL LOG READER
//        StringBuffer sb = new StringBuffer();
//        Cursor managedCursor = new CursorLoader(CallLog.Calls.CONTENT_URI, null,
//                null, null, null);
//        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
//        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
//        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
//        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//        sb.append("Call Details :");
//        while (managedCursor.moveToNext()) {
//            String phNumber = managedCursor.getString(number);
//            String callType = managedCursor.getString(type);
//            String callDate = managedCursor.getString(date);
//            Date callDayTime = new Date(Long.valueOf(callDate));
//            String callDuration = managedCursor.getString(duration);
//            String dir = null;
//            int dircode = Integer.parseInt(callType);
//            switch (dircode) {
//                case CallLog.Calls.OUTGOING_TYPE:
//                    dir = "OUTGOING";
//                    break;
//
//                case CallLog.Calls.INCOMING_TYPE:
//                    dir = "INCOMING";
//                    break;
//
//                case CallLog.Calls.MISSED_TYPE:
//                    dir = "MISSED";
//                    break;
//            }
//            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
//                    + dir + " \nCall Date:--- " + callDayTime
//                    + " \nCall duration in sec :--- " + callDuration);
//            sb.append("\n----------------------------------");
//
//            Log.e("phone" ,phNumber);
//            Log.e("phone" ,callDate);
//            Log.e("phone" ,Count+"");
//            Count++;

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });


    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    txtBelowMic.setText("Try again");
                    btnNext.setImageResource(R.drawable.ic_check_white_48dp);
                    txtBelowNextBtn.setText("Click on me if this is what you meant");

                }
                break;
            }

        }
    }

    public void onClickNextBtn(View view) {

        Intent i = new Intent(this, MagicActivity.class);
        startActivity(i);

    }





}









