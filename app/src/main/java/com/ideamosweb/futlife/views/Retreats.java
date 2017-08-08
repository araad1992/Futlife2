package com.ideamosweb.futlife.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.polak.clicknumberpicker.ClickNumberPickerListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import pl.polak.clicknumberpicker.PickerClickType;

public class Retreats extends AppCompatActivity {

    private Context context;
    private UserController userController;
    private RechargeController rechargeController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private boolean flag_value;

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.lbl_title_activity)TextView lbl_title_activity;
    @Bind(R.id.txt_phone_number_retreats)EditText txt_phone_number_retreats;
    @Bind(R.id.txt_number_count)EditText txt_number_count;
    @Bind(R.id.lbl_amount_retreat)TextView lbl_amount_retreat;
    @Bind(R.id.txt_amount_retreat)ClickNumberPickerView txt_amount_retreat;
    @Bind(R.id.lbl_cancel_retreat)TextView lbl_cancel_retreat;
    @Bind(R.id.lbl_confirm_retreat)TextView lbl_confirm_retreat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retreats);
        ButterKnife.bind(this);
        setupAcitivity();
    }

    public void setupAcitivity(){
        context = this;
        userController = new UserController(context);
        rechargeController = new RechargeController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        flag_value = true;
        setupToolbar();
        setupFontsElements();
        setupAmountRetreats();
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

    public void setupFontsElements(){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        txt_phone_number_retreats.setTypeface(bebas_regular);
        txt_number_count.setTypeface(bebas_regular);
        lbl_amount_retreat.setTypeface(bebas_regular);
        lbl_cancel_retreat.setTypeface(bebas_regular);
        lbl_confirm_retreat.setTypeface(bebas_regular);
        loadDataUser();
    }

    public void loadDataUser(){
        String number_phone = userController.show().getTelephone();
        if(number_phone != null && !number_phone.isEmpty()) {
            txt_phone_number_retreats.setText(number_phone);
        }
    }

    public void setupAmountRetreats(){
        txt_amount_retreat.setPickerValue(20000);
        txt_amount_retreat.setClickNumberPickerListener(new ClickNumberPickerListener() {
            @Override
            public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {
                if(currentValue > rechargeController.get().getValue()) {
                    toast.toastError("No puedes retirar un saldo mayor al que posees.");
                    flag_value = false;
                } else {
                    flag_value = true;
                }
            }
        });
    }

    @OnClick(R.id.fab_cancel)
    public void cancelRetreat(View view){
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 800);
    }

    @OnClick(R.id.fab_confirm)
    public void confirmRetreat(View view){
        if(flag_value) {
            YoYo.with(Techniques.Swing)
                    .duration(700)
                    .playOn(view);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestWithdrawal();
                }
            }, 800);
        } else {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .playOn(view);
            toast.toastError("No puedes retirar un saldo mayor al que posees.");
        }
    }

    public void requestWithdrawal() {
        String txt_phone = txt_phone_number_retreats.getText().toString().trim();
        String txt_identification = txt_number_count.getText().toString().trim();
        float txt_value = txt_amount_retreat.getValue();
        if(txt_phone.isEmpty() || txt_identification.isEmpty()) {
            toast.toastWarning("Antes debes agregar un número de celular y tu identificaciòn asosiada a Daviplata");
        } else {
            createPreferences(txt_phone, txt_identification, txt_value);
            dialog.dialogInfo("¡ENHORABUENA!", "Hemos agregado tu petición a la cola de retiros, pronto nos comunicaremos contigo");
        }
    }

    public void createPreferences(String txt_phone, String txt_identification, float txt_value) {
        SharedPreferences preferences = getSharedPreferences("retreats",Context.MODE_PRIVATE);
        boolean retirement = preferences.getBoolean("retirement", false);
        if(!retirement) {
            preferences.getString("phone", txt_phone);
            preferences.getString("identification", txt_identification);
            preferences.getFloat("value", txt_value);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("retirement", true);
            editor.apply();
        }
    }

}
