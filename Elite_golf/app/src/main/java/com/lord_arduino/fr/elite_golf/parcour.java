package com.lord_arduino.fr.elite_golf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;

import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR1;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR2;

public class parcour extends Activity {
    private Button menu, play, pause, stop, choix_club;
    private Chronometer chronometer;
    private long timeStopped = 0;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parcour);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));

        chronometer = (Chronometer)findViewById(R.id.chronome);
        menu = (Button)findViewById(R.id.Menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parcour.this, Menu_principal.class);
                startActivity(intent);
            }
        });

        play = (Button)findViewById(R.id.Play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime() + timeStopped);
                chronometer.start();
            }
        });

        pause = (Button)findViewById(R.id.Pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
            }
        });

        stop = (Button)findViewById(R.id.Stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeStopped = 0;
            }
        });
        choix_club = (Button)findViewById(R.id.Club);
        choix_club.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parcour.this, param_herbe.class);
                startActivity(intent);
            }
        });
    }
}
