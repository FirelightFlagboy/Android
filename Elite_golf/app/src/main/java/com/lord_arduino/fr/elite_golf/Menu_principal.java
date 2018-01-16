package com.lord_arduino.fr.elite_golf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.integreight.onesheeld.sdk.OneSheeldSdk;


public class Menu_principal extends Activity{
    public final static String FAVORITE_COLOR1 = "fav_color1";
    public final static String FAVORITE_COLOR2 = "fav_color2";
    private ListView listV;
    private String[] title = new String[]{"Mon Parcour","Mes Statistique","Mes Information","Bluetooth","Réglages"};
    private View v;

  /*  public static final String CLUB_INDEX = "Index";
    public static final String CLUB_NOM = "Nom";
    public static final String CLUB_DISMIN = "Dist min";
    public static final String CLUB_DISMAX = "Dist max";

    public static final String CLUB_TABLE_NAME = "Club";
    public static final String CLUB_TABLE_CREATE =
            "CREATE TABLE "+CLUB_TABLE_NAME+" ("+
                    CLUB_INDEX+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    CLUB_NOM+" TEXT, "+
                    CLUB_DISMIN+" INTEGER, "+
                    CLUB_DISMAX+" INTEGER);";
    public static final String CLUB_TABLE_DROP = "DROP TABLE IF EXISTS "+CLUB_TABLE_NAME+";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

       // db.execSQL(CLUB_TABLE_CREATE);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));

        listV = (ListView)findViewById(R.id.ItemList);

        listV.setAdapter(new ArrayAdapter<String>(this ,android.R.layout.simple_list_item_1 ,title));

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class classe = Menu_principal.class;
                switch (position){
                    case 0:
                            classe = parcour.class;
                        break;
                    case 1:
                            classe = statistique.class;
                        break;
                    case 2:
                            classe = information.class;
                        break;
                    case 3:
                            classe = bluetooth.class;
                        break;
                    case 4:
                            classe = parametre.class;
                        break;
                }
                Intent intent = new Intent(Menu_principal.this, classe);
                startActivity(intent);
            }
        });

     /*   @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL(CLUB_TABLE_DROP)
            onCreate(null,db);
        }*/
    }
}
