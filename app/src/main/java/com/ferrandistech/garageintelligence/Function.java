package com.ferrandistech.garageintelligence;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Function {
    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "93adbedb60e2c4fa11fde767c04e1088";

    //Switch para cambiar el icono de las nubes, el tiempo en castellano y el vehiculo

    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;"; //Soleado
            } else {
                icon = "&#xf02e;"; //Nocturno despejado
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;"; //Tormenta electrica
                    break;
                case 3 : icon = "&#xf01c;"; //Llovizna
                    break;
                case 7 : icon = "&#xf014;"; //Neblina
                    break;
                case 8 : icon = "&#xf013;"; //Nublado
                    break;
                case 6 : icon = "&#xf01b;"; //Nevado
                    break;
                case 5 : icon = "&#xf019;"; //Lluvioso
                    break;
            }
        }
        return icon;
    }

    public static String setClimate(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String climate ="";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                climate = "soleado";
            } else {
                climate = "nocturno despejado";
            }
        } else {
            switch(id) {
                case 2 :
                    climate = "con tormenta electrica";
                    break;
                case 3 :
                    climate = "con llovizna";
                    break;
                case 7 :
                    climate = "con neblina";
                    break;
                case 8 :
                    climate = "principalmente soleado";
                    break;
                case 6 :
                    climate = "nevado";
                    break;
                case 5 :
                    climate = "lluvioso";
                    break;
            }
        }
        return climate;
    }

    public static String setVehicle(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String vehicle ="";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                vehicle = "el MX-5";
            } else {
                vehicle = "el MX-5";
            }
        } else {
            switch(id) {
                case 2 :
                    vehicle = "una lancha motora y sino el Celica";
                    break;
                case 3 :
                    vehicle = "el Celica";
                    break;
                case 7 :
                    vehicle = "el MX-5";
                    break;
                case 8 :
                    vehicle = "el MX-5";
                    break;
                case 6 :
                    vehicle = "el MX-5";
                    break;
                case 5 :
                    vehicle = "el Celica";
                    break;
            }
        }
        return vehicle;
    }

    public static String setHud(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String hud ="";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                hud = "@drawable/miata_move";
            } else {
                hud = "@drawable/miata_move";
            }
        } else {
            switch(id) {
                case 2 :
                    hud = "@drawable/celica_move";
                    break;
                case 3 :
                    hud = "@drawable/celica_move";
                    break;
                case 7 :
                    hud = "@drawable/miata_move";
                    break;
                case 8 :
                    hud = "@drawable/hanway_move";
                    break;
                case 6 :
                    hud = "@drawable/miata_move";
                    break;
                case 5 :
                    hud = "@drawable/celica_move";
                    break;
            }
        }
        return hud;
    }


    public interface AsyncResponse {

        void processFinish(String output1, String output2, String output3, String output4, String output5, String output6, String output7, String output8);
    }





    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null){
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();


                    String city = json.getString("name");
                    String description = details.getString("description");
                    String temperature = String.format("%.2f", main.getDouble("temp"));
                    String humidity = main.getString("humidity") + "%";
                    String pressure = main.getString("pressure") + " hPa";
                    String updatedOn = df.format(new Date(json.getLong("dt")*1000));
                    //Cambio de icono, clima y vehiculo
                    String iconText = setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);
                    String climateText = setClimate(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);
                    String vehicleText = setVehicle(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);
                    String hudText = setHud(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);

                    delegate.processFinish(city, vehicleText, temperature, humidity, hudText, climateText, iconText,""+ (json.getJSONObject("sys").getLong("sunrise") * 1000));

                }
            } catch (JSONException e) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
            }



        }
    }






    public static JSONObject getWeatherJSON(String lat, String lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }




}
