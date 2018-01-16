package com.lord_arduino.fr.elite_golf;

/**
 * Created by Florian on 30/04/2017.
 */

public class club {
    private String[] nomClub =new String[]{"driver","bois 3","bois 5","fer 3","fer 4","fer 5","fer  6","fer 7","fer 8","fer 9","picth"};

    public String choix(double distance){
        if(distance >= 200) return nomClub[0];
        else if(distance >=180 && distance <=235) return nomClub[1];
        else if(distance >=170 && distance <=210) return nomClub[2];
        else if(distance >=160 && distance <=200 ) return nomClub[3];
        else if(distance >=150 && distance <=185 ) return nomClub[4];
        else if(distance >=140 && distance <=170 ) return nomClub[5];
        else if(distance >=130 && distance <=160 ) return nomClub[6];
        else if(distance >=120 && distance <=150 ) return nomClub[7];
        else if(distance >=110 && distance <=140 ) return nomClub[8];
        else if(distance >=95 && distance <=130 ) return nomClub[9];
        else return nomClub[10];
    }
}
