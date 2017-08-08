package com.ideamosweb.futlife.Service;

import android.app.Application;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.ideamosweb.futlife.NotificationsSettings.CustomNotificationFactory;
import com.urbanairship.UAirship;

/**
 * Creado por Deimer Villa on 22/02/17.
 * Función:
 */
public class FutlifeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Manejador general de la api de Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Manejador general de la api de notificaciones Urban Airship
        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {
                airship.getPushManager().setUserNotificationsEnabled(true);
                airship.getPushManager().setSoundEnabled(true);

                CustomNotificationFactory notificationFactory;
                notificationFactory = new CustomNotificationFactory(UAirship.getApplicationContext());

                // Set the factory on the PushManager
                airship.getPushManager().setNotificationFactory(notificationFactory);
            }
        });
    }

}
