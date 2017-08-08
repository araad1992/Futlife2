package com.ideamosweb.futlife.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.Utils;
import com.ideamosweb.futlife.views.Expired;
import java.util.List;

/**
 * Creado por Deimer Villa on 27/06/17.
 * Función:
 */
public class RevisionService extends Service {

    private Context context;
    private UserController userController;
    private ChallengeController challengeController;
    private Utils utils;

    public RevisionService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Servicio creado...");
        context = this;
        userController = new UserController(context);
        challengeController = new ChallengeController(context);
        utils = new Utils(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Servicio iniciado...");

        checkChallenges();
        return START_NOT_STICKY;
    }

    public void setupTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkChallenges();
            }
        }, 1000*60*60);
    }

    public void checkChallenges(){
        int number_expireds = 0;
        User user = userController.show();
        if(user != null) {
            int user_id = user.getUser_id();
            List<Challenge> challenges = challengeController.list(user_id);
            for (int i = 0; i < challenges.size(); i++) {
                Challenge challenge = challenges.get(i);
                if(challenge.getState().equalsIgnoreCase("aceptado")) {
                    if(utils.compareDates(challenge.getDeadline())) {
                        challenge.setState("vencido");
                        challengeController.update(challenge);
                        actionReceivedChallenge();
                        number_expireds = number_expireds + 1;
                    }
                }
            }
            if(number_expireds > 0) {
                System.out.println("Tienes " + number_expireds + " retos expirados");

                activeNotificationExpireds(number_expireds);
            } else {
                System.out.println("No hay expirados");
            }
            setupTimer();
        }
    }

    public void actionReceivedChallenge(){
        AppCompatActivity activity = new AppCompatActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.frame_challenges_state);
        StationBus.getBus().post(new MessageBusChallenge(true));
    }

    public void activeNotificationExpireds(int number_expireds){
        //Preferencias de notificación
        SharedPreferences preferences = getSharedPreferences("settings",Context.MODE_PRIVATE);
        boolean priority = preferences.getBoolean("priority", true);
        boolean ringtone = preferences.getBoolean("ringtone", true);
        boolean vibrate = preferences.getBoolean("vibrate", true);
        //Construcción de la notificación
        Bitmap large_icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pending);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setSmallIcon(R.drawable.ic_launcher);
        notification.setLargeIcon(large_icon);
        notification.setContentTitle("Retos expirados");
        notification.setContentText("Tienes " + number_expireds + " retos expirados");
        notification.setTicker("Alerta");
        notification.setAutoCancel(true);
        notification.setColor(ContextCompat.getColor(context, R.color.icons));
        if(priority) {
            notification.setPriority(Notification.PRIORITY_MAX);
        } else {
            notification.setPriority(Notification.PRIORITY_DEFAULT);
        }
        if(ringtone) {notification.setSound(soundNotification());}
        if(vibrate) {notification.setVibrate(new long[] { 1000, 1000 });}
        Intent intent = new Intent(context, Expired.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setContentIntent(pending);
        NotificationManager notification_manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notification_manager.notify(1, notification.build());
    }

    public Uri soundNotification(){
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onDestroy() {
        System.out.println("Servicio destruido...");
    }

}
