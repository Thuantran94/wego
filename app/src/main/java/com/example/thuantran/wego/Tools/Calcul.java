package com.example.thuantran.wego.Tools;


import com.example.thuantran.wego.DataAccess.Constant;

import java.util.Random;

public class Calcul {



    public static double phi_chuyen_di(double distance, int nperson, String typeRequest){


        double prix;
        double baseprix =  10;
        if (distance <= 2 ){ prix = baseprix; }
        else if( distance > 2 && distance < 5 ){ prix = baseprix + (distance - 2)*3.5; }
        else if( distance >= 5 && distance < 10 ){ prix = baseprix + 2*3.5 +(distance - 4)*3.25; }
        else   { prix = baseprix + 2*3.5 + 5*3.25 + (distance - 9)*3.0; }

        if (typeRequest.equals("taxi")){
            prix = prix*2.25;
            if( nperson == 2){ prix = prix + prix*0.2;}
            if( nperson == 3){ prix = prix + prix*0.2 + prix*0.15; }
            if( nperson == 4){ prix = prix + prix*0.2 + prix*0.15 + prix*0.1; }
            if( nperson == 5){ prix = prix + prix*0.2 + prix*0.15 + prix*0.1 + prix*0.1; }
        }



        return  prix*1000;

    }


    public static int svf(double cost, float percent){

        return (int) ((cost*percent)/100);

    }


    public static double phi_thap_nhat(double cost){

        return cost - (cost* Constant.MIN_PERCENT)/100;

    }



    public static double phi_cao_nhat(double cost){

        return cost + (cost*Constant.MAX_PERCENT)/100;

    }


    public static float svp(float r, int n){

        float rate = 10 - (r*1/20 + n/200);

        return rate < 5 ? 5 : rate;

    }





    public static String create_invite_code(String userID, String date){

        String str1 = randomString(userID,4);
        String str2 = randomNumber(date,2);


        return str1 + str2;

    }


    private static String randomString(String str, int n){

        char[] chars = str.toUpperCase().toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            char c = chars[random.nextInt(chars.length)];
            if (c == '-'  || c == '_'){
                n++;
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    private static String randomNumber(String date,int n){
        char[] chars = date.toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            char c = chars[random.nextInt(chars.length)];
            if (c == '/'  || c == '-'|| c == ':'){
                n++;
            }else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

}
