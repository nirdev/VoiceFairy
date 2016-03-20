package acom.voice.fairy.lovefairyv4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.lovefairyv4.R;

public class MagicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic);



        TypeWriter writer = (TypeWriter) findViewById(R.id.typeWriter);


        //Add a character every 150ms
        writer.setCharacterDelay(50);
        writer.animateText("Now let me do my magic - " +
                "every time you'll have a phone" +
                " call I'll analyze your friend's emotions towards you!");


    }
}
