package com.ideamosweb.futlife.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Feedback extends AppCompatActivity {

    private UserController userController;
    private Utils utils;
    private MaterialDialog dialog;

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.lbl_title_activity)TextView lbl_title_activity;
    @Bind(R.id.txt_email_contact)EditText txt_email_contact;
    @Bind(R.id.txt_name_contact)EditText txt_name_contact;
    @Bind(R.id.txt_telephone_contact)EditText txt_telephone_contact;
    @Bind(R.id.txt_message_contact)EditText txt_message_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity() {
        Context context = this;
        userController = new UserController(context);
        dialog = new MaterialDialog(context);
        utils = new Utils(context);
        setupToolbar();
        setupElements();
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
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        lbl_title_activity.setTypeface(bebas_bold);
    }

    public void setupElements() {
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        txt_email_contact.setTypeface(bebas_bold);
        txt_name_contact.setTypeface(bebas_bold);
        txt_telephone_contact.setTypeface(bebas_bold);
        txt_message_contact.setTypeface(bebas_bold);
        User user = userController.show();
        txt_email_contact.setText(user.getEmail());
        txt_name_contact.setText(user.getName());
        txt_telephone_contact.setText(user.getTelephone());
    }

    @OnClick(R.id.fab_send)
    public void onClickSend(View view) {
        String email = txt_email_contact.getText().toString().trim();
        String name = txt_name_contact.getText().toString().trim();
        String telephone = txt_telephone_contact.getText().toString().trim();
        String city = userController.show().getCity_name();
        String message = txt_message_contact.getText().toString().trim();
        if(email.isEmpty() || name.isEmpty() || telephone.isEmpty() || message.isEmpty()) {
            dialog.dialogWarnings("Alerta", "Debes llenar todos los campos antes de enviar tu mensaje.");
        } else {
            boolean connect = utils.connect();
            if(connect) {
                sendContact(email, name, telephone, city, message);
            } else {
                dialog.dialogWarnings("Alerta", "Error de conexión");
            }
        }
    }

    public void sendContact(String email, String name, String telephone, String city, String message){
        dialog.dialogProgress("Enviando...");
        final String url = getString(R.string.url_admin);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.contact(email, name, telephone, city, message, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                dialog.cancelProgress();
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    clearFields();
                    dialog.dialogSuccess("Gracias por contactarnos", "En breve estaremos dando respuesta a tu solicitud.");
                }
            }
            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                System.out.println("Error!");
                try {
                    Log.d("Payments(uploadRecharge)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Payments(uploadRecharge)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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
                JsonObject jsonErrors = (JsonObject) parser.parse(error);
                if (jsonErrors.has("error")) {
                    String title = retrofitError.getMessage();
                    String message = jsonErrors.get("error").getAsString();
                    dialog.dialogErrors(title, message);
                }
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void clearFields() {
        txt_email_contact.setText("");
        txt_name_contact.setText("");
        txt_telephone_contact.setText("");
        txt_message_contact.setText("");
    }

}
