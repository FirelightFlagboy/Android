package com.lord_arduino.fr.elite_golf;

import android.app.Activity;
import android.app.Dialog;
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

import com.integreight.onesheeld.sdk.OneSheeldConnectionCallback;
import com.integreight.onesheeld.sdk.OneSheeldDevice;
import com.integreight.onesheeld.sdk.OneSheeldManager;
import com.integreight.onesheeld.sdk.OneSheeldScanningCallback;
import com.integreight.onesheeld.sdk.OneSheeldSdk;

import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR1;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR2;


public class parametre extends Activity{
    private Button menu;
    private String[] itemList = new String[]{"Couleur","Formater les Données","Paramètre club"};
    private ListView list;
    private View v;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametre);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));

        menu = (Button)findViewById(R.id.Menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parametre.this, Menu_principal.class);
                startActivity(intent);
            }
        });
        list = (ListView)findViewById(R.id.ItemList);
        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class cl = parametre.class;
                switch (position){
                    case 0: cl = couleur.class;
                        break;
                    case 2: cl = club.class;
                }
                Intent in = new Intent(parametre.this, cl);
                startActivity(in);
            }
        });

    }

}