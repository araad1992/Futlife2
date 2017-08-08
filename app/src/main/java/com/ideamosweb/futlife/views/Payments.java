package com.ideamosweb.futlife.views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Objects.History;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusPayment;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import co.epayco.android.Epayco;
import co.epayco.android.models.Authentication;
import co.epayco.android.models.Card;
import co.epayco.android.models.Cash;
import co.epayco.android.models.Charge;
import co.epayco.android.models.Client;
import co.epayco.android.util.EpaycoCallback;
import pl.polak.clicknumberpicker.ClickNumberPickerListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import pl.polak.clicknumberpicker.PickerClickType;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Payments extends AppCompatActivity {

    private Context context;
    private UserController userController;
    private RechargeController rechargeController;
    private Epayco epayco;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;
    private Balance balance;
    private History history;
    private String option_payment;

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.lbl_title_activity)TextView lbl_title_activity;
    @Bind(R.id.layout_options_payments)LinearLayout layout_options_payments;
    @Bind(R.id.layout_card_credit)LinearLayout layout_card_credit;
    @Bind(R.id.layout_efecty)LinearLayout layout_efecty;
    //Payment Information
    @Bind(R.id.layout_content_information)RelativeLayout layout_content_information;
    @Bind(R.id.progress_bar)ProgressBar progress_bar;
    @Bind(R.id.layout_information)LinearLayout layout_information;
    @Bind(R.id.layout_identification)LinearLayout layout_identification;
    @Bind(R.id.lbl_document_info)TextView lbl_document_info;
    @Bind(R.id.lbl_document_payment)TextView lbl_document_payment;
    @Bind(R.id.line_identification)View line_identification;
    @Bind(R.id.lbl_type_transaction)TextView lbl_type_transaction;
    @Bind(R.id.lbl_type_trans_payment)TextView lbl_type_trans_payment;
    @Bind(R.id.layout_reference)LinearLayout layout_reference;
    @Bind(R.id.lbl_payco_reference)TextView lbl_payco_reference;
    @Bind(R.id.lbl_payco_reference_payment)TextView lbl_payco_reference_payment;
    @Bind(R.id.line_reference)View line_reference;
    @Bind(R.id.layout_project)LinearLayout layout_project;
    @Bind(R.id.lbl_project_id)TextView lbl_project_id;
    @Bind(R.id.lbl_project_id_payment)TextView lbl_project_id_payment;
    @Bind(R.id.line_project)View line_project;
    @Bind(R.id.lbl_invoice)TextView lbl_invoice;
    @Bind(R.id.lbl_invoice_payment)TextView lbl_invoice_payment;
    @Bind(R.id.lbl_value_info)TextView lbl_value_info;
    @Bind(R.id.lbl_value_payment)TextView lbl_value_payment;
    @Bind(R.id.lbl_coin_info)TextView lbl_coin_info;
    @Bind(R.id.lbl_coin_payment)TextView lbl_coin_payment;
    @Bind(R.id.lbl_state_info)TextView lbl_state_info;
    @Bind(R.id.lbl_state_payment)TextView lbl_state_payment;
    @Bind(R.id.ic_state)ImageView ic_state;
    //Credit Card
    @Bind(R.id.layout_content_card)RelativeLayout layout_content_card;
    @Bind(R.id.lbl_title_credit_card)TextView lbl_title_credit_card;
    @Bind(R.id.txt_number_card)EditText txt_number_card;
    @Bind(R.id.txt_expiry_month)EditText txt_expiry_month;
    @Bind(R.id.txt_expiry_year)EditText txt_expiry_year;
    @Bind(R.id.txt_cvc)EditText txt_cvc;
    @Bind(R.id.sp_type_doc)MaterialSpinner sp_type_doc;
    @Bind(R.id.txt_identification)EditText txt_identification;
    @Bind(R.id.txt_first_name_client)EditText txt_first_name_client;
    @Bind(R.id.txt_last_name_client)EditText txt_last_name_client;
    @Bind(R.id.txt_email_client)EditText txt_email_client;
    @Bind(R.id.txt_phone_client)EditText txt_phone_client;
    @Bind(R.id.cb_save_card)CheckBox cb_save_card;
    @Bind(R.id.lbl_amount_charge)TextView lbl_amount_charge;
    @Bind(R.id.txt_amount_charge)ClickNumberPickerView txt_amount_charge;
    @Bind(R.id.txt_real_value_card)EditText txt_real_value_card;
    //Efecty
    @Bind(R.id.layout_content_efecty)RelativeLayout layout_content_efecty;
    @Bind(R.id.lbl_title_efecty)TextView lbl_title_efecty;
    @Bind(R.id.sp_type_doc_efecty)MaterialSpinner sp_type_doc_efecty;
    @Bind(R.id.txt_identification_efecty)EditText txt_identification_efecty;
    @Bind(R.id.txt_first_name_client_efecty)EditText txt_first_name_client_efecty;
    @Bind(R.id.txt_last_name_client_efecty)EditText txt_last_name_client_efecty;
    @Bind(R.id.txt_email_client_efecty)EditText txt_email_client_efecty;
    @Bind(R.id.txt_phone_client_efecty)EditText txt_phone_client_efecty;
    @Bind(R.id.lbl_amount_charge_efecty)TextView lbl_amount_charge_efecty;
    @Bind(R.id.txt_value_charge)ClickNumberPickerView txt_value_charge;
    @Bind(R.id.txt_real_value_efecty)EditText txt_real_value_efecty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);
        setupActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        StationBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        StationBus.getBus().unregister(this);
    }

    @Subscribe
    public void recievedMessage(MessageBusPayment messageBusPayment){
        boolean active = messageBusPayment.isActive();
        if(active) {
            balance = rechargeController.get();
            lbl_value_payment.setText("$" + balance.getValue());
            loadStatePayment(balance.getState());
            lbl_state_payment.setText(balance.getState());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(rechargeController.get() != null) {
            getMenuInflater().inflate(R.menu.menu_payments, menu);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_recharges:
                showLayoutMoreRecharges();
                break;
        }
        return true;
    }

    public void setupActivity() {
        context = this;
        userController = new UserController(context);
        rechargeController = new RechargeController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);
        balance = new Balance();
        history = new History();
        option_payment = "";
        setupToolbar();
        setupStatePayment();
        System.out.println("Fecha actual: " + utils.getDateExpiry());
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

    public void setupStatePayment(){
        Balance balance = rechargeController.get();
        if(balance != null) {
            layout_content_information.setVisibility(View.VISIBLE);
            getLastHistory(balance.getBalance_id());
        } else {
            setupPaycoAuthentication();
            setupFab();
            setupSpinner();
        }
    }

    public void setupPaycoAuthentication(){
        Authentication auth = new Authentication();
        auth.setApiKey(getString(R.string.public_api_key));
        auth.setPrivateKey(getString(R.string.private_api_key));
        auth.setLang("ES");
        auth.setTest(true);
        epayco = new Epayco(auth);
    }

    public void setupFab(){
        YoYo.with(Techniques.BounceInLeft)
                .duration(2000)
                .playOn(layout_card_credit);
        layout_card_credit.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceInRight)
                .duration(2000)
                .playOn(layout_efecty);
        layout_efecty.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fab_credit_card)
    public void fabCreditCard(View view){
        view.setEnabled(false);
        option_payment = "tarjeta";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateExit();
            }
        }, 800);
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
    }

    @OnClick(R.id.fab_efecty)
    public void fabEfecty(View view){
        view.setEnabled(false);
        option_payment = "efecty";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateExit();
            }
        }, 800);
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
    }

    public void animateExit(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout_options_payments.setVisibility(View.GONE);
                validateOption();
            }
        }, 800);
        YoYo.with(Techniques.SlideOutLeft)
                .duration(700)
                .playOn(layout_card_credit);
        YoYo.with(Techniques.SlideOutRight)
                .duration(700)
                .playOn(layout_efecty);
    }

    public void validateOption(){
        if(option_payment.equalsIgnoreCase("tarjeta")) {
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(layout_content_card);
            layout_content_card.setVisibility(View.VISIBLE);
            loadDataClientCard();
        } else if(option_payment.equalsIgnoreCase("efecty")) {
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(layout_content_efecty);
            layout_content_efecty.setVisibility(View.VISIBLE);
            loadDataClientEfecty();
        }
    }

    public void setupSpinner() {
        List<String> list = new ArrayList<>();
        list.add("CC");
        list.add("CE");
        list.add("NIT");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        sp_type_doc.setTypeface(bebas_regular);
        sp_type_doc_efecty.setTypeface(bebas_regular);
        sp_type_doc.setItems(list);
        sp_type_doc_efecty.setItems(list);
    }

//region Payment Information

    public void getLastHistory(int balance_id){
        String token = "Bearer " + userController.show().getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.getLastHistory(token, balance_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    if(!jsonObject.get("history").isJsonNull()) {
                        JsonObject json_history = jsonObject.get("history").getAsJsonObject();
                        history = new Gson().fromJson(json_history, History.class);
                        setupLabelsPaymentInfo();
                    }
                }
                progress_bar.setVisibility(View.GONE);
            }
            @Override
            public void failure(RetrofitError error) {
                progress_bar.setVisibility(View.GONE);
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

    public void setupLabelsPaymentInfo(){
        layout_information.setVisibility(View.VISIBLE);
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        balance = rechargeController.get();

        lbl_document_info.setTypeface(bebas_regular);
        lbl_type_transaction.setTypeface(bebas_regular);
        lbl_payco_reference.setTypeface(bebas_regular);
        lbl_project_id.setTypeface(bebas_regular);
        lbl_invoice.setTypeface(bebas_regular);
        lbl_value_info.setTypeface(bebas_regular);
        lbl_coin_info.setTypeface(bebas_regular);
        lbl_state_info.setTypeface(bebas_regular);

        lbl_document_payment.setTypeface(bebas_regular);
        lbl_document_payment.setText(balance.getDocument_type() + " " + balance.getDocument_number());
        lbl_type_trans_payment.setTypeface(bebas_regular);
        lbl_type_trans_payment.setText(history.getTransaction_type());
        lbl_payco_reference_payment.setTypeface(bebas_regular);
        lbl_payco_reference_payment.setText(history.getRef_payco());
        lbl_project_id_payment.setTypeface(bebas_regular);
        lbl_project_id_payment.setText(history.getProject_id());
        lbl_invoice_payment.setTypeface(bebas_regular);
        lbl_invoice_payment.setText(history.getInvoice());
        lbl_value_payment.setTypeface(bebas_regular);
        lbl_value_payment.setText("$" + Math.round(balance.getValue()));
        lbl_coin_payment.setTypeface(bebas_regular);
        lbl_coin_payment.setText(balance.getCoin());
        lbl_state_payment.setTypeface(bebas_regular);
        lbl_state_payment.setText(balance.getState());
        loadStatePayment(balance.getState());
    }

    public void loadStatePayment(String state){
        switch (state) {
            case "Pendiente":
                Picasso.with(context).load(R.drawable.ic_pending).into(ic_state);
                break;
            case "Aceptada":
                layout_identification.setVisibility(View.GONE);
                layout_reference.setVisibility(View.GONE);
                layout_project.setVisibility(View.GONE);
                line_identification.setVisibility(View.GONE);
                line_reference.setVisibility(View.GONE);
                line_project.setVisibility(View.GONE);
                Picasso.with(context).load(R.drawable.ic_accepted).into(ic_state);
                break;
            case "Rechazada":
                Picasso.with(context).load(R.drawable.ic_failed).into(ic_state);
                break;
            case "Fallida":
                Picasso.with(context).load(R.drawable.ic_cancel_challenge).into(ic_state);
                break;
        }
    }

//endregion

//region Credit Card

    public void loadDataClientCard(){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_credit_card.setTypeface(bebas_regular);
        txt_number_card.setTypeface(bebas_regular);
        txt_expiry_month.setTypeface(bebas_regular);
        txt_expiry_year.setTypeface(bebas_regular);
        txt_cvc.setTypeface(bebas_regular);
        txt_first_name_client.setTypeface(bebas_regular);
        txt_last_name_client.setTypeface(bebas_regular);
        txt_email_client.setTypeface(bebas_regular);
        txt_phone_client.setTypeface(bebas_regular);
        cb_save_card.setTypeface(bebas_regular);
        txt_identification.setTypeface(bebas_regular);
        lbl_amount_charge.setTypeface(bebas_regular);
        txt_first_name_client.setText(userController.show().getName());
        txt_phone_client.setText(userController.show().getTelephone());
        txt_email_client.setText(userController.show().getEmail());
        txt_amount_charge.setPickerValue(20000);
        txt_amount_charge.setClickNumberPickerListener(new ClickNumberPickerListener() {
            @Override
            public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {
                String real_value = setValueTaxCard(currentValue);
                txt_real_value_card.setText(real_value);
            }
        });
        txt_real_value_card.setTypeface(bebas_regular);
        txt_real_value_card.setText(setValueTaxCard(txt_amount_charge.getValue()));
    }

    public String setValueTaxCard(float value){
        String value_tax;
        float retention = (value * .015f);
        float comission = (value * .0299f) + 600;
        float iva = (comission * .19f);
        int tax = Math.round(comission + iva + retention);
        int real_value = Math.round(value - tax);
        value_tax = "$" + real_value;
        return value_tax;
    }

    public boolean validateInputs(String number, String month, String year, String cvc,
                                  String identification, String first_name, String last_name,
                                  String email, String phone){
        boolean response = true;
        if(number.isEmpty()){
            toast.toastWarning("Antes debes agregar el número de la tarjeta");
            response = false;
        } if(month.isEmpty()) {
            toast.toastWarning("Antes debes agregar el mes de expiración de la tarjeta");
            response = false;
        } if(year.isEmpty()) {
            toast.toastWarning("Antes debes agregar el año de expiración de la tarjeta");
            response = false;
        } if(cvc.isEmpty()) {
            toast.toastWarning("Antes debes agregar el número de CVC");
            response = false;
        } if(identification.isEmpty()) {
            toast.toastWarning("Antes debes agregar tu número de ifentificación");
            response = false;
        } if(first_name.isEmpty()) {
            toast.toastWarning("Antes debes agregar tus nombres");
            response = false;
        } if(last_name.isEmpty()) {
            toast.toastWarning("Antes debes agregar tus apellidos");
            response = false;
        } if(email.isEmpty()) {
            toast.toastWarning("Antes debes agregar tu correo electrónico");
            response = false;
        } if(phone.isEmpty()) {
            toast.toastWarning("Antes debes agregar tu telefono");
            response = false;
        }
        return response;
    }

    public boolean validateCard(String number, String month, String year, String cvc){
        boolean response = true;
        if(!epayco.validNumberCard(number)){
            response = false;
            toast.toastError("Número de tarjeta invalido");
        } if(!epayco.validExpiryMonth(month)){
            response = false;
            toast.toastError("Mes de expiración invalido");
        } if(!epayco.validExpiryYear(year)){
            response = false;
            toast.toastError("Año de expiración invalido");
        } if(!epayco.validateCVC(cvc)){
            response = false;
            toast.toastError("Número de CVC invalido");
        } if(!epayco.validateExpiryDate(month, year)){
            response = false;
            toast.toastError("Fecha de expiración invalido");
        }
        return response;
    }

    @OnClick(R.id.but_validate_card)
    public void validateCreditCard(){
        String number_card = txt_number_card.getText().toString().trim();
        String expiry_month = txt_expiry_month.getText().toString().trim();
        String expiry_year = txt_expiry_year.getText().toString().trim();
        String cvc = txt_cvc.getText().toString().trim();
        String identification = txt_identification.getText().toString().trim();
        String first_name = txt_first_name_client.getText().toString().trim();
        String last_name = txt_last_name_client.getText().toString().trim();
        String email = txt_email_client.getText().toString().trim();
        String phone = txt_phone_client.getText().toString().trim();
        boolean validateInputs = validateInputs(number_card, expiry_month, expiry_year, cvc,
                identification, first_name, last_name, email, phone);
        if(validateInputs) {
            boolean validate_card = validateCard(number_card, expiry_month, expiry_year, cvc);
            if(validate_card) {
                Card card = new Card();
                card.setNumber(number_card);
                card.setMonth(expiry_month);
                card.setYear(expiry_year);
                card.setCvc(cvc);
                if(epayco.validCard(card)) {
                    dialog.dialogProgress("Enlazando tarjeta a cuenta...");
                    createTokenEpayco(card);
                } else {
                    toast.toastError("La tarjeta es invalida");
                }
            }
        }
    }

    public void createTokenEpayco(Card card){
        epayco.createToken(card, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject object) throws JSONException {
                System.out.println("createTokenEpayco: " + object.toString());
                String token_id = object.getString("id");
                createClientEpayco(token_id);
            }

            @Override
            public void onError(Exception error) {
                dialog.cancelProgress();
                toast.toastWarning(error.getMessage());
                System.out.println("createTokenEpayco: " + error.getMessage());
            }
        });
    }

    public void createClientEpayco(final String token_id){
        String first_name = txt_first_name_client.getText().toString().trim();
        String last_name = txt_last_name_client.getText().toString().trim();
        String email = txt_email_client.getText().toString().trim();
        String phone = txt_phone_client.getText().toString().trim();

        Client client = new Client();
        client.setTokenId(token_id);
        client.setName(first_name + " " + last_name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setDefaultCard(cb_save_card.isChecked());

        epayco.createCustomer(client, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) throws JSONException {
                System.out.println("createClientEpayco: " + jsonObject.toString());
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("Creado") || status.equalsIgnoreCase("Actualizado")) {
                    String customer_id = jsonObject.getString("customerId");
                    getCustomerResponse(customer_id, token_id);
                }
            }

            @Override
            public void onError(Exception error) {
                dialog.cancelProgress();
                toast.toastWarning(error.getMessage());
                System.out.println("createClientEpayco: " + error.getMessage());
            }
        });
    }

    public void getCustomerResponse(final String customer_id, final String token_id){
        epayco.getCustomer(customer_id, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) throws JSONException {
                System.out.println("getCustomerResponse: " + jsonObject.toString());
                createPaymentEpayco(customer_id, token_id);
            }

            @Override
            public void onError(Exception error) {
                dialog.cancelProgress();
                toast.toastWarning(error.getMessage());
                System.out.println("getCustomerResponse: " + error.getMessage());
            }
        });
    }

    public void createPaymentEpayco(String customer_id, String token_id){
        String identification = txt_identification.getText().toString().trim();
        String first_name = txt_first_name_client.getText().toString().trim();
        String last_name = txt_last_name_client.getText().toString().trim();
        String email = txt_email_client.getText().toString().trim();
        float amount_base = txt_amount_charge.getValue();
        Charge charge = new Charge();
        charge.setCustomerId(customer_id);
        charge.setTokenCard(token_id);
        charge.setDocType(sp_type_doc.getText().toString());
        charge.setDocNumber(identification);
        charge.setName(first_name);
        charge.setLastName(last_name);
        charge.setEmail(email);
        charge.setInvoice("OR-1234");
        charge.setDescription("Test Payment Futlife");
        charge.setValue(String.valueOf(amount_base));
        charge.setTax("0");
        charge.setTaxBase(String.valueOf(amount_base));
        charge.setCurrency("COP");
        charge.setDues("12");
        if(rechargeController.get() == null) {
            charge.setUrlConfirmation(getString(R.string.url_api_notification_payments));
        } else {
            charge.setUrlConfirmation(getString(R.string.url_api_notification_recharge));
        }


        epayco.createCharge(charge, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                System.out.println("createPaymentEpayco: " + data.toString());
                String ref_payco = data.getString("ref_payco");
                String authorization = data.getString("autorizacion");
                String transaction_date = data.getString("fecha");
                String bank = data.getString("banco");
                loadRechargeCard(authorization, transaction_date, ref_payco, "OR-1234", bank);
                uploadRecharge("Tu nuevo saldo ya está disponible.");
            }

            @Override
            public void onError(Exception error) {
                dialog.cancelProgress();
                toast.toastWarning(error.getMessage());
                System.out.println("createPaymentEpayco(Error Charge): " + error.getMessage());
            }
        });
    }

    public void loadRechargeCard(String authorization, String transaction_date, String ref_payco, String invoice, String bank){
        balance.setUser_id(userController.show().getUser_id());
        balance.setDocument_type(sp_type_doc.getText().toString());
        balance.setDocument_number(txt_identification.getText().toString());
        balance.setEmail(txt_email_client.getText().toString());
        history.setTransaction_type(option_payment);
        history.setAuthorization(authorization);
        history.setTransaction_date(transaction_date);
        history.setRef_payco(ref_payco);
        history.setInvoice(invoice);
        balance.setValue(Float.parseFloat(txt_real_value_card.getText().toString().substring(1)));
        balance.setCoin("COP");
        history.setBank(bank);
        balance.setState("Pendiente");
    }

//endregion

//region Efecty

    public void loadDataClientEfecty(){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_efecty.setTypeface(bebas_regular);
        sp_type_doc_efecty.setTypeface(bebas_regular);
        txt_identification_efecty.setTypeface(bebas_regular);
        txt_first_name_client_efecty.setTypeface(bebas_regular);
        txt_last_name_client_efecty.setTypeface(bebas_regular);
        txt_email_client_efecty.setTypeface(bebas_regular);
        txt_phone_client_efecty.setTypeface(bebas_regular);
        lbl_amount_charge_efecty.setTypeface(bebas_regular);
        txt_first_name_client_efecty.setText(userController.show().getName());
        txt_email_client_efecty.setText(userController.show().getEmail());
        txt_phone_client_efecty.setText(userController.show().getTelephone());
        txt_value_charge.setPickerValue(20000);
        txt_value_charge.setClickNumberPickerListener(new ClickNumberPickerListener() {
            @Override
            public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {
                String real_value = setValueTaxEfecty(currentValue);
                txt_real_value_efecty.setText(real_value);
            }
        });
        txt_real_value_efecty.setTypeface(bebas_regular);
        txt_real_value_efecty.setText(setValueTaxEfecty(txt_value_charge.getValue()));
    }

    public String setValueTaxEfecty(float value){
        String value_tax;
        float comission = (value * .0299f) + 600;
        float iva = (comission * .19f);
        int tax = Math.round(comission + iva);
        int real_value = Math.round(value - tax);
        value_tax = "$" + real_value;
        return value_tax;
    }

    public boolean validateInputsEfecty(String identification, String first_name, String last_name,
                                  String email, String phone){
        boolean response = true;
        if(identification.isEmpty()) {
            toast.toastWarning("Antes debes agregar tu número de ifentificación");
            response = false;
        } if(first_name.isEmpty()) {
            toast.toastWarning("Antes debes agregar tus nombres");
            response = false;
        } if(last_name.isEmpty()) {
            toast.toastWarning("Antes debes agregar tus apellidos");
            response = false;
        } if(email.isEmpty()) {
            toast.toastWarning("Antes debes agregar tu correo electrónico");
            response = false;
        } if(phone.isEmpty()) {
            toast.toastWarning("Antes debes agregar tu telefono");
            response = false;
        }
        return response;
    }

    @OnClick(R.id.but_validate_charge_efecty)
    public void validateChargeEfecty(){
        String identification = utils.cleanText(txt_identification_efecty.getText().toString().trim());
        String first_name = utils.cleanText(txt_first_name_client_efecty.getText().toString().trim());
        String last_name = utils.cleanText(txt_last_name_client_efecty.getText().toString().trim());
        String email = utils.cleanText(txt_email_client_efecty.getText().toString().trim());
        String phone = txt_phone_client_efecty.getText().toString().trim();
        float amount_base = txt_value_charge.getValue();
        boolean validateInputsEfecty = validateInputsEfecty(identification, first_name, last_name, email, phone);
        if(validateInputsEfecty) {
            dialog.dialogProgress("Enviando solicitud...");
            Cash cash = new Cash();
            cash.setType("efecty");
            cash.setEndDate(utils.getDateExpiry());
            cash.setDocType(sp_type_doc.getText().toString());
            cash.setDocNumber(identification);
            cash.setName(first_name);
            cash.setLastName(last_name);
            cash.setEmail(email);
            cash.setInvoice("OR-1234");
            cash.setDescription("Test Payment futlife");
            cash.setValue(String.valueOf(amount_base));
            cash.setTax("0");
            cash.setTaxBase("0");
            cash.setPhone(phone);
            cash.setCurrency("COP");
            cash.setCountry("CO");
            if(rechargeController.get() != null) {
                System.out.println("1 recharge");
                cash.setUrlConfirmation(getString(R.string.url_api_notification_recharge));
            } else {
                System.out.println("2 payment");
                cash.setUrlConfirmation(getString(R.string.url_api_notification_payments));
            }
            createChargeTransactionEfecty(cash);
        }
    }

    public void createChargeTransactionEfecty(Cash cash){
        epayco.createCashTransaction(cash, new EpaycoCallback() {
            @Override
            public void onSuccess(JSONObject data) throws JSONException {
                System.out.println("createChargeTransactionEfecty: " + data.toString());

                String ref_payco = data.getString("ref_payco");
                String authorization = data.getString("autorizacion");
                String project_id = data.getString("codigoproyecto");
                String transaction_id = data.getString("recibo");
                String transaction_date = data.getString("fecha");

                loadRechargeEfecty(transaction_id, project_id, authorization, transaction_date, ref_payco, "OR-1234");
                uploadRecharge("Hemos enviado a tu correo la factura de la recarga, en cuanto canceles seras notificado de tu nuevo saldo.");
            }

            @Override
            public void onError(Exception error) {
                dialog.cancelProgress();
                toast.toastWarning(error.getMessage());
                System.out.println("createChargeTransactionEfecty: " + error.getMessage());
            }
        });
    }

    public void loadRechargeEfecty(String transaction_id, String project_id, String authorization, String transaction_date, String ref_payco, String invoice){
        balance.setUser_id(userController.show().getUser_id());
        balance.setDocument_type(sp_type_doc.getText().toString());
        balance.setDocument_number(txt_identification_efecty.getText().toString());
        balance.setEmail(txt_email_client_efecty.getText().toString());
        history.setTransaction_type(option_payment);
        history.setTransaction_id(transaction_id);
        history.setProject_id(project_id);
        history.setAuthorization(authorization);
        history.setTransaction_date(transaction_date);
        history.setRef_payco(ref_payco);
        history.setInvoice(invoice);
        balance.setValue(Float.parseFloat(txt_real_value_efecty.getText().toString().substring(1)));
        balance.setCoin("COP");
        balance.setState("Pendiente");
    }

//endregion

//region Connection Network

    public void uploadRecharge(final String message){
        String token = "Bearer " + userController.show().getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.uploadRecharge(token, balance.getUser_id(), balance.getDocument_type(), balance.getDocument_number(),
                balance.getEmail(), history.getTransaction_type(), history.getTransaction_id(), history.getProject_id(), history.getAuthorization(), history.getTransaction_date(),
                history.getRef_payco(), history.getInvoice(), balance.getValue(), balance.getCoin(), balance.getState(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                dialog.cancelProgress();
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    int balance_id = jsonObject.get("balance_id").getAsInt();
                    balance.setBalance_id(balance_id);
                    int process = rechargeController.create(balance);
                    if(process != 0) {
                        dialog.dialogSuccess("¡Enhorabuena!", message);
                        showResumePayment();
                    }
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

    public void showResumePayment(){
        if(option_payment.equalsIgnoreCase("tarjeta")){
            layout_content_card.setVisibility(View.GONE);
        } else if(option_payment.equalsIgnoreCase("efecty")) {
            layout_content_efecty.setVisibility(View.GONE);
        }
        layout_content_information.setVisibility(View.VISIBLE);
        setupLabelsPaymentInfo();
    }

    public void showLayoutMoreRecharges(){
        YoYo.with(Techniques.FadeOut)
                .duration(300)
                .playOn(layout_content_information);
        layout_content_information.setVisibility(View.GONE);
        setupPaycoAuthentication();
        setupFab();
        setupSpinner();
    }

//endregion

}
