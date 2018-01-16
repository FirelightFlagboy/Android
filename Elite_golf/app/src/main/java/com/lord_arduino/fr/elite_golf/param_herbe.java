package com.lord_arduino.fr.elite_golf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR1;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR2;

public class param_herbe extends Activity{
    private String defaut ="pour obtenir  un resultat cliker sur ''SELECTION DU CLUB''\n";
    private double mult = 0;
    private Button menu,selection;
    private View v;
    private String cl;
    private TextView m, d, h,resultat;
    private EditText Distance;
    private RadioGroup Meteo,Mode_d,H_herbe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.param_herbe);

        final club c = new club();

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int cl = Color.parseColor(pref.getString(FAVORITE_COLOR1, "#303F9F"));

        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(cl);
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));
        m = (TextView)findViewById(R.id.M);
        d = (TextView)findViewById(R.id.D);
        h = (TextView)findViewById(R.id.H);
        m.setTextColor(cl);
        d.setTextColor(cl);
        h.setTextColor(cl);

        Meteo = (RadioGroup)findViewById(R.id.Meteo);
        Mode_d = (RadioGroup)findViewById(R.id.D_herbe);
        H_herbe = (RadioGroup)findViewById(R.id.H_herbe);
        Distance = (EditText)findViewById(R.id.Distance);
        Distance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resultat.setText(defaut);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        selection = (Button)findViewById(R.id.selec_club);
        resultat = (TextView)findViewById(R.id.resultat);
        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (Mode_d.getCheckedRadioButtonId()){
                    case R.id.Centi: mult =0.01;
                        break;
                    case R.id.Metre: mult = 1;
                        break;
                    case R.id.Kilom: mult = 1000;
                }
                double dis = Integer.parseInt(Distance.getText().toString());
                dis =dis*mult;
                String ph = "je vous conseille d'utiliser le "+c.choix(dis);
                resultat.setText(ph);
            }
        });

        menu = (Button)findViewById(R.id.Menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(param_herbe.this, parcour.class);
                startActivity(intent);
            }
        });
    }

}
