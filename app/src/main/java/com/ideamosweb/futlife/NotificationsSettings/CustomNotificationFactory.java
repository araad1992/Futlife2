package com.ideamosweb.futlife.NotificationsSettings;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.R;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.ActionsNotificationExtender;
import com.urbanairship.push.notifications.NotificationFactory;
import com.urbanairship.util.UAStringUtil;

/**
 * Creado por Deimer Villa on 26/03/17.
 * Función:
 */
public class CustomNotificationFactory extends NotificationFactory {

    public CustomNotificationFactory(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Notification createNotification(@NonNull PushMessage message, int notificationId) {
        if (UAStringUtil.isEmpty(message.getAlert())) {
            return null;
        }

        //Preferencias de notificación
        SharedPreferences preferences = getContext().getSharedPreferences("settings",Context.MODE_PRIVATE);
        boolean priority = preferences.getBoolean("priority", true);
        boolean ringtone = preferences.getBoolean("ringtone", true);
        boolean vibrate = preferences.getBoolean("vibrate", true);

        //Configuración de notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setContentTitle("FutLife");
        builder.setContentText(getMessageAlert(message.getAlert()));
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setLargeIcon(imageNotification(message.getAlert()));
        builder.setColor(ContextCompat.getColor(getContext(), R.color.icons));
        if(priority) {
            builder.setPriority(Notification.PRIORITY_MAX);
        } else {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }
        if(ringtone) {
            builder.setSound(soundNotification(message.getAlert()));
        }
        if(vibrate) {
            builder.setVibrate(new long[] { 1000, 1000 });
        }
        builder.extend(new ActionsNotificationExtender(getContext(), message, notificationId));
        return builder.build();
    }

    public String getMessageAlert(String message){
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(message);
        return json.get("alert").getAsString();
    }

    @Override
    public int getNextId(@NonNull PushMessage pushMessage) {
        return super.getNextId(pushMessage);
    }


    public Uri soundNotification(String message){
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(message);
        String type_notification = json.get("type").getAsString();
        if(type_notification.equalsIgnoreCase("message")) {
            return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.sound_referee_message);
        } else {
            return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.sound_referee);
        }
    }

    public Bitmap imageNotification(String message) {
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(message);
        String type_notification = json.get("type").getAsString();
        if(type_notification.equalsIgnoreCase("message")) {
            return ((BitmapDrawable)ContextCompat.getDrawable(getContext(), R.drawable.ic_chat)).getBitmap();
        } else {
            return ((BitmapDrawable)ContextCompat.getDrawable(getContext(), R.drawable.ic_notifications)).getBitmap();
        }
    }

}
