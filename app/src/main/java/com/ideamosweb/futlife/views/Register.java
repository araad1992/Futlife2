package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.SocialController;
import com.ideamosweb.futlife.Models.Social;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.Animate;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.Utils;
import com.urbanairship.UAirship;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Register extends Activity {

    //Iniciadores
    private Context context;
    private UserController userController;
    private SocialController socialController;
    private MaterialDialog dialog;
    private Utils utils;
    private static int code_terms;

    //Elemento para el login con facebook
    private CallbackManager callbackManager;

    //Elementos de la vista
    @Bind(R.id.txt_email)EditText txt_email;
    @Bind(R.id.txt_password)EditText txt_password;
    @Bind(R.id.txt_fullname)EditText txt_fullname;
    @Bind(R.id.txt_username)EditText txt_username;
    @Bind(R.id.check_terms_conditions)CheckBox check_terms_conditions;
    @Bind(R.id.but_register_facebook)LoginButton but_register_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        socialController = new SocialController(context);
        dialog = new MaterialDialog(context);
        utils = new Utils(context);
        code_terms = 12;
        setupFacebookSdk();
    }

    @OnClick(R.id.fab_register)
    public void clickRegister(){
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String fullname = txt_fullname.getText().toString().trim();
        String username = txt_username.getText().toString().trim();
        if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("") ||
                username.equalsIgnoreCase("") || fullname.equalsIgnoreCase("")) {
            dialog.dialogWarnings("Alerta", "Antes de continuar, debes proporcionar todos tus datos en el formulario.");
        } else {
            if (utils.isEmailValid(email)) {
                if (utils.isPasswordValid(password)) {
                    if(utils.isUsernameValid(username)) {
                        if(check_terms_conditions.isChecked()) {
                            boolean hayConexion = utils.isOnline();
                            if (hayConexion) {
                                register(email, password, fullname, username);
                            } else {
                                dialog.dialogErrors("Alerta de conexión", "No se pudo detectar una conexión estable a internet.");
                            }
                        } else {
                            dialog.dialogWarnings("Terminos y Condiciones", "Antes de registrarte con nosotros debes leer y aceptar las condiciones y restricciones de FutLife.");
                        }
                    } else {
                        dialog.dialogWarnings("Error", "Debes ingresar un usuario con formato valido.");
                    }
                } else {
                    dialog.dialogWarnings("Error", "La contraseña debe tener mas de 6 caracteres.");
                    txt_password.setText("");
                }
            } else {
                dialog.dialogWarnings("Error", "Debes ingresar un correo con formato valido.");
            }
        }
    }

    public void register(String email, String password, String fullname, String username){
        dialog.dialogProgress("Registrando...");
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.register(fullname, username, email, password, password, "android", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonObject jsonUser = jsonObject.get("data").getAsJsonObject();
                    User user = new Gson().fromJson(jsonUser, User.class);
                    user.setSocial(false);
                    user.setToken(jsonObject.get("token").getAsString());
                    if(userController.create(user)){
                        dialog.cancelProgress();
                        dialogRegisterSuccess(user.getName());
                    }
                }
            }
            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Register(register)", "Error: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Register(register)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void dialogRegisterSuccess(String title) {
        ColorDialog dialog = new ColorDialog(context);
        dialog.setColor(Color.parseColor("#55A323"));
        dialog.setTitle(title);
        dialog.setAnimationEnable(true);
        dialog.setAnimationIn(Animate.getInAnimationTest(context));
        dialog.setAnimationOut(Animate.getOutAnimationTest(context));
        dialog.setContentImage(ContextCompat.getDrawable(context, R.drawable.welcome));
        dialog.setPositiveListener("Continuar", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
                next();
            }
        }).show();
    }

    public void next(){
        generateNamedUser();
        Intent sel_console = new Intent(Register.this, SelectConsole.class);
        startActivity(sel_console);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent login = new Intent(Register.this, Login.class);
        startActivity(login);
        //finish();
        super.onBackPressed();
    }

//Metodos que permiten obtener el identificador unico para las notificaciones

    public void generateNamedUser(){
        String user_id = String.valueOf(userController.show().getUser_id());
        UAirship.shared().getNamedUser().setId(user_id);
        UAirship.shared().getPushManager().setAlias(user_id);
        System.out.println("Named user: " + user_id);
    }

//******************************* Registro usando la api de facebook *******************************//

    @OnClick(R.id.fab_facebook_register)
    public void onClickFacebook(){
        if(check_terms_conditions.isChecked()) {
            but_register_facebook.performClick();
            regFacebook();
        } else {
            dialog.dialogWarnings("Terminos y Condiciones", "Antes de registrarte con nosotros debes leer y aceptar las condiciones y restricciones de FutLife.");
        }
    }

    public void setupFacebookSdk(){
        List<String> scopes = new ArrayList<>();
        scopes.add("public_profile");
        scopes.add("email");
        scopes.add("user_birthday");
        but_register_facebook.setReadPermissions(scopes);
        callbackManager = CallbackManager.Factory.create();
    }

    public void regFacebook(){
        if(utils.connect()) {
            dialog.dialogProgress("Iniciando facebook...");
            but_register_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    List<String> data_string = utils.convertToObject(object);
                                    String token = loginResult.getAccessToken().getToken();
                                    registerFacebook(data_string, token);
                                }
                            }
                    );
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday,picture.width(500).height(500)");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
                @Override
                public void onCancel() {
                    dialog.cancelProgress();
                    dialog.dialogWarnings("Atención", "Registro cancelado por el usuario.");
                }
                @Override
                public void onError(FacebookException error) {
                    dialog.cancelProgress();
                    dialog.dialogErrors("Error", "Error on Login, check your facebook app_id");
                }
            });
            dialog.cancelProgress();
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            dialog.dialogWarnings("Error de conexión", "No se pudo detectar una conexión estable a internet.");
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == code_terms) {
                check_terms_conditions.setChecked(true);
            }
        }
    }

    public void registerFacebook(List<String> data_string, String social_token){

        String name = data_string.get(0);
        String username = data_string.get(1);
        String email = data_string.get(2);
        String provider_id = data_string.get(3);
        String avatar = data_string.get(4);
        String thumbnail = data_string.get(5);
        String platform = data_string.get(6);
        String provider = data_string.get(7);

        dialog.dialogProgress("Registrando...");
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.registerFacebook(name, username, email,
                provider_id, provider_id, avatar, thumbnail,
                platform, provider, provider_id, social_token, true,
                new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonObject jsonUser = jsonObject.get("data").getAsJsonObject();
                    JsonObject jsonSocial = jsonObject.get("social").getAsJsonObject();
                    String token = jsonObject.get("token").getAsString();
                    User user = new Gson().fromJson(jsonUser, User.class);
                    user.setToken(token);
                    Social social = new Gson().fromJson(jsonSocial, Social.class);
                    if(userController.create(user)){
                        socialController.create(social);
                        dialog.cancelProgress();
                        dialogRegisterSuccess(user.getUsername());
                    }
                }
            }
            @Override
            public void failure(RetrofitError error) {
                LoginManager.getInstance().logOut();
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Register(registerFacebook)", "Error: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Register(registerFacebook)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequest(RetrofitError retrofitError){
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            dialog.dialogWarnings("Error de conexión", retrofitError.getMessage());
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

//region Terms and Conditions

    @OnClick(R.id.lbl_terms_conditions)
    public void onClickLabel(){
        Intent intent = new Intent(context, TermsConditions.class);
        startActivityForResult(intent, code_terms);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

//endregion

}
