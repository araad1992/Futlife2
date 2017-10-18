package com.ideamosweb.futlife.views;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ideamosweb.futlife.Fragments.DialogAvatar;
import com.ideamosweb.futlife.Fragments.FrameNotifications;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.R;

public class BaseFrames extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_frames);
        fromNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fromNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fromNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fromNotification();
    }

    public void fromNotification(){
        String from = getIntent().getStringExtra("from");
        if(from != null) {
            Bundle bundle = new Bundle();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (from) {
                case "notifications":
                    Challenge challenge = (Challenge) getIntent().getSerializableExtra("challenge");
                    FrameNotifications notifications = new FrameNotifications();
                    bundle.putSerializable("challenge", challenge);
                    notifications.setArguments(bundle);
                    transaction.replace(R.id.coordinator_base, notifications).commit();
                    break;
                case "avatar":
                    String url_avatar = getIntent().getStringExtra("url_avatar");
                    String name_user = getIntent().getStringExtra("name_user");
                    DialogAvatar dialog_avatar = new DialogAvatar();
                    bundle.putString("url_avatar", url_avatar);
                    bundle.putString("name_user", name_user);
                    dialog_avatar.setArguments(bundle);
                    transaction.replace(R.id.coordinator_base, dialog_avatar).commit();
                    break;
            }
        }
    }
}
