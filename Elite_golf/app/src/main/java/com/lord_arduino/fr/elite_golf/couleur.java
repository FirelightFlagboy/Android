package com.lord_arduino.fr.elite_golf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR1;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR2;

/**
 * Created by Florian on 01/03/2017.
 */

public class couleur extends Activity {
    private ListView listView;
    private String[] color_list = new String[]{"Bleu","Rouge","Vert","Marron","Violet","Rose","Noir"},
        color_list_tag = new String[]{"#3F51B5","#DF0101","#01DF01","#A67E2E","#6A0888","#FA58F4","#585858"},
        colorDark_list_tag = new String[]{"#303F9F","#B40404","#04B404","#663E10", "#4C0B5F","#FE2EC8","#424242"};
    private Button back;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.couleur);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));

        back = (Button)findViewById(R.id.Menu);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(couleur.this, parametre.class);
                startActivity(in);
            }
        });

        listView = (ListView)findViewById(R.id.ItemList);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, color_list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String color1 = color_list_tag[position];
                String color2 = colorDark_list_tag[position];
                SharedPreferences.Editor ed = pref.edit();
                ed.putString(FAVORITE_COLOR1, color1);
                ed.putString(FAVORITE_COLOR2, color2);
                ed.commit();
                v.setBackgroundColor(Color.parseColor(color1));
                Window w = getWindow();
                w.setStatusBarColor(Color.parseColor(color2));
            }
        });
    }
}