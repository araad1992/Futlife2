package com.ideamosweb.futlife.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Creado por Deimer Villa on 25/10/16.
 * Función:
 */
public class Utils {

    //Variable para alerta asincronica
    private Context contexto;

    public void Validaciones(){}

    public Utils(Context contexto){
        this.contexto = contexto;
        Validaciones();
    }

    public void setContext(Context contexto){
        this.contexto = contexto;
    }

    //Metodo para verificar la conectividad a internet
    public boolean connect() {
        ConnectivityManager con_manager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con_manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isOnline(){
        boolean online = false;
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                if(activeNetwork.isConnectedOrConnecting()) {
                    Log.i("Utils()isOnline", "Connected to wifi.");
                    online = true;
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                if(activeNetwork.isConnectedOrConnecting()) {
                    Log.i("Utils()isOnline", "Connected to the mobile provider's data plan.");
                    online = true;
                }
            }
        } else {
            online = false;
        }
        return online;
    }

    public boolean valueIsNull(String value){
        return !(value != null && !value.isEmpty());
    }

    public boolean isEmailValid(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean isUsernameValid(String username){
        String ePattern = "^[a-z0-9_-]{3,15}$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(username);
        return m.matches();
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public JsonObject convertToJsonGson(JSONObject object){
        JsonParser jsonParser = new JsonParser();
        return (JsonObject)jsonParser.parse(object.toString());
    }

    public List<String> convertToObject(JSONObject json){
        List<String> data_string = new ArrayList<>();
        try {
            String social_id = json.getString("id");
            data_string.add(json.getString("name"));
            data_string.add(formatUsername(json.getString("email")));
            data_string.add(json.getString("email"));
            data_string.add(social_id);
            data_string.add(json.getJSONObject("picture").getJSONObject("data").getString("url"));
            data_string.add("http://graph.facebook.com/" + social_id + "/picture?type=large");
            data_string.add("android");
            data_string.add("facebook");
        } catch (JSONException ex) {
            Log.e("Utils(convertToJsonGson)", "Error: " + ex.getMessage());
        }
        return data_string;
    }

    public String formatUsername(String email){
        String res = "";
        String[] parts = email.split("@");
        String part_one = parts[0];
        if(part_one.toCharArray().length > 9) {
            return part_one.substring(0, 8);
        } else {
            return part_one;
        }
    }

    public String createDateNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public long getTimeInMilliSeconds(String deadline){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long back_timer = 0;
        try {
            Calendar calendar_deadline = Calendar.getInstance();
            Calendar calendar_datenow = Calendar.getInstance();

            Date date = format.parse(deadline);
            calendar_deadline.setTime(date);

            System.out.println("tiempo convertido: " + calendar_deadline.getTimeInMillis());

            back_timer = calendar_deadline.getTimeInMillis() - calendar_datenow.getTimeInMillis();
            System.out.println("back_timer: " + back_timer);
        } catch (Exception ex) {
            Log.e("Challenge(setDeadline)", "Error: " + ex.getMessage());
        }
        return back_timer;
    }

    public boolean compareDates(String deadline){
        boolean res = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Calendar calendar_deadline = Calendar.getInstance();
            Calendar calendar_datenow = Calendar.getInstance();
            Date date = format.parse(deadline);
            calendar_deadline.setTime(date);
            if(calendar_datenow.getTime().after(calendar_deadline.getTime())) {
                res = true;
            }
        } catch (Exception ex) {
            Log.e("Challenge(setDeadline)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public String setDatetimeFormat(String deadline){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String deadLine_format = "";
        try {
            Date date = format.parse(deadline);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String day_name = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            String mt = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());
            deadLine_format = day_name + " " + day + " de " + month + " - " + hour + ":" + numberConvert(minute) + " " + mt;
        } catch (Exception ex) {
            Log.e("Challenge(setDeadline)", "Error: " + ex.getMessage());
        }
        if(deadLine_format.equalsIgnoreCase("")) {
            return "data_null";
        } else {
            return deadLine_format;
        }
    }

    public String getTimeNow(String created_at){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = "";
        try {
            Date date = format.parse(created_at);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            String mt = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());
            time = hour + ":" + minute + " " + mt;
        } catch (Exception ex) {
            Log.e("Challenge(setDeadline)", "Error: " + ex.getMessage());
        }
        return time;
    }

    public String getDateTimeNow(){
        String time = "";
        try {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            String mt = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());
            time = hour + ":" + minute + " " + mt;
        } catch (Exception ex) {
            Log.e("Challenge(setDeadline)", "Error: " + ex.getMessage());
        }
        return time;
    }

    public String getDateExpiry(){
        String time = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            time = year + "-" + numberConvert(month) + "-" + numberConvert(day);
        } catch (Exception ex) {
            Log.e("Challenge(setDeadline)", "Error: " + ex.getMessage());
        }
        return time;
    }

    public String numberConvert(int value){
        if(value < 10){
            return "0" + value;
        } else {
            return String.valueOf(value);
        }
    }

    public String cleanText(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }

}
