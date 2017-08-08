package com.ideamosweb.futlife.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.github.chuross.library.ExpandableLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Settings extends AppCompatActivity {

    private Context context;
    private UserController userController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private boolean flag_pass;
    private boolean flag_nots;

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.lbl_title_activity)TextView lbl_title_activity;
    @Bind(R.id.lbl_change_password)TextView lbl_change_password;
    @Bind(R.id.layout_expandable)ExpandableLayout layout_expandable;
    @Bind(R.id.lbl_notifications)TextView lbl_notifications;
    @Bind(R.id.layout_expandable_nots)ExpandableLayout layout_expandable_nots;

    @Bind(R.id.txt_old_password)TextView txt_old_password;
    @Bind(R.id.txt_new_password)TextView txt_new_password;
    @Bind(R.id.txt_repeat_password)TextView txt_repeat_password;

    @Bind(R.id.lbl_priority)TextView lbl_priority;
    @Bind(R.id.lbl_ringtone)TextView lbl_ringtone;
    @Bind(R.id.lbl_vibrate)TextView lbl_vibrate;
    @Bind(R.id.switch_priority)SwitchCompat switch_priority;
    @Bind(R.id.switch_ringtone)SwitchCompat switch_ringtone;
    @Bind(R.id.switch_vibrate)SwitchCompat switch_vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        flag_pass = false;
        flag_nots = false;
        setupToolbar();
        createPreferences();
    }

    public void setupToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        setuplabels();
    }

    public void setuplabels() {
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_activity.setTypeface(bebas_bold);
        lbl_change_password.setTypeface(bebas_regular);
        lbl_notifications.setTypeface(bebas_regular);
        lbl_priority.setTypeface(bebas_regular);
        lbl_ringtone.setTypeface(bebas_regular);
        lbl_vibrate.setTypeface(bebas_regular);
    }

    public void createPreferences() {
        SharedPreferences preferences = getSharedPreferences("settings",Context.MODE_PRIVATE);
        boolean priority = preferences.getBoolean("priority", true);
        boolean ringtone = preferences.getBoolean("ringtone", true);
        boolean vibrate = preferences.getBoolean("vibrate", true);
        switch_priority.setChecked(priority);
        switch_ringtone.setChecked(ringtone);
        switch_vibrate.setChecked(vibrate);
    }

    @OnCheckedChanged(R.id.switch_priority)
    public void changePriority(boolean chedked){
        SharedPreferences preferences = getSharedPreferences("settings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("priority", chedked);
        editor.apply();
        System.out.println("priority: " + chedked);
    }

    @OnCheckedChanged(R.id.switch_ringtone)
    public void changeRingtone(boolean chedked){
        SharedPreferences preferences = getSharedPreferences("settings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ringtone", chedked);
        editor.apply();
        System.out.println("ringtone: " + chedked);
    }

    @OnCheckedChanged(R.id.switch_vibrate)
    public void changeVibrate(boolean chedked){
        SharedPreferences preferences = getSharedPreferences("settings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("vibrate", chedked);
        editor.apply();
        System.out.println("vibrate: " + chedked);
    }

    @OnClick(R.id.lbl_change_password)
    public void setupExpandable() {
        if(flag_pass) {
            flag_pass = false;
            layout_expandable.collapse();
        } else {
            flag_pass = true;
            layout_expandable.expand();
            flag_nots = false;
            layout_expandable_nots.collapse();
        }
    }

    @OnClick(R.id.lbl_notifications)
    public void setupExpandableNots(){
        if(flag_nots) {
            flag_nots = false;
            layout_expandable_nots.collapse();
        } else {
            flag_nots = true;
            layout_expandable_nots.expand();
            flag_pass = false;
            layout_expandable.collapse();
        }
    }

//region Change password

    @OnClick(R.id.fab)
    public void changePassword() {
        String old_password = txt_old_password.getText().toString();
        String new_password = txt_new_password.getText().toString();
        String repeat_password = txt_repeat_password.getText().toString();
        if(!old_password.isEmpty() && !new_password.isEmpty() && !repeat_password.isEmpty()) {
            if(old_password.equalsIgnoreCase(new_password)) {
                toast.toastWarning("Tu nueva contraseña no puede ser igual a la anterior");
            } else {
                if(new_password.equalsIgnoreCase(repeat_password)) {
                    Utils utils = new Utils(context);
                    if(utils.connect()) {
                        changePassword(old_password, new_password, repeat_password);
                    } else {
                        toast.toastWarning("Error de conexión");
                    }
                } else {
                    toast.toastWarning("Las contraseñas no coinciden");
                }
            }
        } else {
            toast.toastWarning("Debes agregar la información de todos los campos antes de actualizar");
        }
    }

    public void changePassword(String old_password, String new_password, String password_confirmation){
        dialog.dialogProgress("Actualizando contraseña...");
        final String url = getString(R.string.url_api);
        String token = "Bearer " + userController.show().getToken();
        int user_id = userController.show().getUser_id();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.changePassword(token, user_id, old_password, new_password, password_confirmation, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                System.out.println("data: " + jsonObject.toString());
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    String message = jsonObject.get("message").getAsString();
                    String token = jsonObject.get("token").getAsString();
                    User user = userController.show();
                    user.setToken(token);
                    userController.update(user);
                    clearViewsData();
                    toast.toastSuccess(message);
                }
                dialog.cancelProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Settings(changePassword)", "Errors: " + error.getBody().toString());
                    Log.d("Settings(changePassword)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Settings(changePassword)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequest(RetrofitError retrofitError){
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            dialog.dialogErrors("Error de conexión", retrofitError.getMessage());
        } else {
            int status = retrofitError.getResponse().getStatus();
            if(status == 400) {
                String error = retrofitError.getBody().toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonErrors = (JsonObject)parser.parse(error);
                String message = jsonErrors.get("message").getAsString();
                dialog.dialogWarnings("¡Alerta!", message);
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void clearViewsData() {
        txt_old_password.setText("");
        txt_new_password.setText("");
        txt_repeat_password.setText("");
        flag_pass = false;
        flag_nots = false;
        layout_expandable.collapse();
        layout_expandable_nots.collapse();
    }

//endregion

}
