package com.lord_arduino.fr.elite_golf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR1;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR2;


public class statistique extends Activity{
    private Button menu;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistique);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(statistique.this);

        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));

        menu = (Button)findViewById(R.id.Menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(statistique.this, Menu_principal.class);
                startActivity(intent);

            }
        });
           /*     // données du tableau
                final String [] col1 = {"col1:ligne1","col1:ligne2","col1:ligne3","col1:ligne4","col1:ligne5"};
                final String [] col2 = {"col2:ligne1","col2:ligne2","col2:ligne3","col2:ligne4","col2:ligne5"};

                TableLayout table = (TableLayout) findViewById(R.id.Tableau); // on prend le tableau défini dans le layout
                TableRow row; // création d'un élément : ligne
                TextView tv1; // création des cellules

                 // pour chaque ligne
                for(int i=0;i<col1.length;i++) {
                    row = new TableRow(this); // création d'une nouvelle ligne

                    tv1 = new TextView(this); // création cellule
                    tv1.setText(col1[i]); // ajout du texte
                    tv1.setGravity(Gravity.CENTER); // centrage dans la cellule
                    // adaptation de la largeur de colonne à l'écran :
                    tv1.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );


                    // ajout des cellules à la ligne
                    row.addView(tv1);

                    // ajout de la ligne au tableau
                    table.addView(row);
                }*/

    }
}
