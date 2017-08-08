package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.ReportController;
import com.ideamosweb.futlife.Controllers.SocialController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.Report;
import com.ideamosweb.futlife.Models.Social;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.RevisionService;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.Utils;
import com.urbanairship.UAirship;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends Activity {

    //Iniciadores
    private Context context;
    private UserController userController;
    private SocialController socialController;
    private ChallengeController challengeController;
    private ReportController reportController;
    private PreferenceController preferenceController;
    private RechargeController rechargeController;
    private MaterialDialog dialog;
    private Utils utils;

    //Elemento para el login con facebook
    private CallbackManager callbackManager;

    //Elementos de la vista
    @Bind(R.id.txt_email)EditText txt_email;
    @Bind(R.id.txt_password)EditText txt_password;
    @Bind(R.id.lbl_register)TextView lbl_register;
    @Bind(R.id.but_login_facebook)LoginButton but_login_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        socialController = new SocialController(context);
        challengeController = new ChallengeController(context);
        reportController = new ReportController(context);
        preferenceController = new PreferenceController(context);
        rechargeController = new RechargeController(context);
        dialog = new MaterialDialog(context);
        utils = new Utils(context);
        setupFacebookSdk();
        createPreferences();
        onClickRegister();
    }

    public void createPreferences() {
        SharedPreferences preferences = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        boolean start = preferences.getBoolean("start", false);
        if(!start) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("start", true);
            editor.apply();
            Intent intent = new Intent(context, Tutorial.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @OnClick(R.id.fab_login)
    public void clickLogin(){
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
            dialog.dialogWarnings("Alerta", "Antes de continuar, debes proporcionar tus credenciales.");
        } else {
            if (utils.isPasswordValid(password)) {
                boolean connection = utils.isOnline();
                if (connection) {
                    if (utils.isEmailValid(email)) {
                        login(email, password);
                    } else {
                        if(utils.isUsernameValid(email)){
                            loginWithUsername(email, password);
                        } else {
                            dialog.dialogWarnings("Error", "Debes ingresar un usuario o un correo con formato valido.");
                            txt_password.setText("");
                        }
                    }
                } else {
                    dialog.dialogErrors("Error de conexión", "No se pudo detectar una conexión estable a internet.");
                    txt_password.setText("");
                }
            } else {
                dialog.dialogWarnings("Error", "La contraseña debe tener mas de 6 caracteres.");
                txt_password.setText("");
            }
        }
    }

    public void login(String email, String password) {
        dialog.dialogProgress("Iniciando Sesión...");
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.login(email, password, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if (success) {
                    JsonObject jsonUser = jsonObject.getAsJsonObject("data");
                    User user = new Gson().fromJson(jsonUser, User.class);
                    user.setToken(jsonObject.get("token").getAsString());
                    if(userController.create(user)){
                        if(user.getSocial()) {
                            JsonObject jsonSocial = jsonObject.get("data")
                                    .getAsJsonObject().get("socials").getAsJsonObject();
                            Social social = new Gson().fromJson(jsonSocial, Social.class);
                            socialController.create(social);
                        }
                        if(jsonUser.has("preferences")){
                            JsonArray preferences = jsonUser.getAsJsonArray("preferences");
                            savePreferences(preferences, user.getUser_id());
                        } if(jsonUser.has("challenges")) {
                            JsonArray challenges = jsonUser.getAsJsonArray("challenges");
                            saveChallenges(challenges);
                        } if(!jsonUser.get("balance").isJsonNull()) {
                            JsonObject json_balance = jsonUser.getAsJsonObject("balance");
                            Balance balance = new Gson().fromJson(json_balance, Balance.class);
                            rechargeController.create(balance);
                        }
                        next();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                LoginManager.getInstance().logOut();
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Login(login)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Login(login)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void loginWithUsername(String username, String password) {
        dialog.dialogProgress("Iniciando Sesión...");
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.loginWhitUsername(username, password, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if (success) {
                    JsonObject jsonUser = jsonObject.getAsJsonObject("data");
                    User user = new Gson().fromJson(jsonUser, User.class);
                    user.setToken(jsonObject.get("token").getAsString());
                    if(userController.create(user)){
                        if(jsonUser.has("preferences")){
                            JsonArray preferences = jsonUser.getAsJsonArray("preferences");
                            savePreferences(preferences, user.getUser_id());
                        } if(jsonUser.has("challenges")) {
                            JsonArray challenges = jsonUser.getAsJsonArray("challenges");
                            saveChallenges(challenges);
                        } if(!jsonUser.get("balance").isJsonNull()) {
                            JsonObject json_balance = jsonUser.getAsJsonObject("balance");
                            Balance balance = new Gson().fromJson(json_balance, Balance.class);
                            rechargeController.create(balance);
                        }
                        next();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Login(loginWhitUsername)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Login(loginWhitUsername)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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
            } else if(status == 401) {
                String error = retrofitError.getBody().toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonErrors = (JsonObject)parser.parse(error);
                String title = "Error de autenticación";
                String message = jsonErrors.get("message").getAsString();
                dialog.dialogWarnings(title, message);
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void savePreferences(JsonArray array, int user_id){
        generateNamedUser();
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject preference = array.get(i).getAsJsonObject();
                    JsonObject json_console = preference.getAsJsonObject("console");
                    int preference_id = preference.get("id").getAsInt();
                    String player_id = "";
                    if(!preference.get("player_id").isJsonNull()){
                        player_id = preference.get("player_id").getAsString();
                    }
                    ConsolePreference console = new Gson().fromJson(json_console, ConsolePreference.class);
                    console.setPreference_id(preference_id);
                    console.setUser_id(user_id);
                    console.setPlayer_id(player_id);
                    if(preferenceController.create(console)){
                        JsonArray array_games = preference.getAsJsonArray("games");
                        for (int j = 0; j < array_games.size(); j++) {
                            JsonObject json_game = array_games.get(j).getAsJsonObject();
                            GamePreference game = new Gson().fromJson(json_game, GamePreference.class);
                            game.setUser_id(user_id);
                            game.setConsole_id(console.getConsole_id());
                            preferenceController.create(game);
                        }
                    }
                }
            }
        } catch (JsonIOException e){
            Log.e("Login(savePreferences)", "Error ex: " + e.getMessage());
        }
    }

    public void saveChallenges(JsonArray challenges){
        try {
            if(challenges.size() > 0) {
                for (int i = 0; i < challenges.size(); i++) {
                    JsonObject json_challenge = challenges.get(i).getAsJsonObject();
                    System.out.println("json_challenge: " + json_challenge);
                    int player_one = json_challenge.get("player_one").getAsInt();
                    boolean is_visible;
                    boolean is_player_one;
                    if(userController.show().getUser_id() == player_one) {
                        is_player_one = true;
                        is_visible = json_challenge.get("visible_one").getAsBoolean();
                    } else {
                        is_player_one = false;
                        is_visible = json_challenge.get("visible_two").getAsBoolean();
                    }
                    if(is_visible) {
                        Challenge challenge = new Gson().fromJson(json_challenge, Challenge.class);
                        challengeController.create(challenge);
                        System.out.println("challenge: " + challenge.toString());
                    } if(!json_challenge.get("report").isJsonNull()) {
                        JsonObject json_report = json_challenge.get("report").getAsJsonObject();
                        saveReports(json_report, is_player_one);
                    }
                }
            }
        } catch (JsonIOException e){
            Log.e("Login(saveChallenges)", "Error ex: " + e.getMessage());
        }
    }

    public void saveReports(JsonObject json_report, boolean is_player_one){
        Report report = new Report();

        int report_id = json_report.get("id").getAsInt();
        int challenge_id = json_report.get("challenge_id").getAsInt();
        report.setReport_id(report_id);
        report.setChallenge_id(challenge_id);
        if(is_player_one) {
            if(!json_report.get("description_player_one").isJsonNull()) {
                report.setDescription(json_report.get("description_player_one").getAsString());
            } if(!json_report.get("shipping_date_one").isJsonNull()) {
                report.setShipping_date(json_report.get("shipping_date_one").getAsString());
            } if(!json_report.get("url_youtube_player_one").isJsonNull()) {
                report.setUrl_youtube(json_report.get("url_youtube_player_one").getAsString());
            } if(!json_report.get("url_image_player_one").isJsonNull()) {
                report.setUrl_youtube(json_report.get("url_image_player_one").getAsString());
            } if(!json_report.get("state_player_one").isJsonNull()) {
                report.setState(json_report.get("state_player_one").getAsString());
            }
        } else {
            if(!json_report.get("description_player_two").isJsonNull()) {
                report.setDescription(json_report.get("description_player_two").getAsString());
            } if(!json_report.get("shipping_date_two").isJsonNull()) {
                report.setShipping_date(json_report.get("shipping_date_two").getAsString());
            } if(!json_report.get("url_youtube_player_two").isJsonNull()) {
                report.setUrl_youtube(json_report.get("url_youtube_player_two").getAsString());
            } if(!json_report.get("url_image_player_two").isJsonNull()) {
                report.setUrl_youtube(json_report.get("url_image_player_two").getAsString());
            } if(!json_report.get("state_player_two").isJsonNull()) {
                report.setState(json_report.get("state_player_two").getAsString());
            }
        }
        reportController.create(report);
        System.out.println("reporte guardado: " + report.toString());
    }

    /*

    state_player_one
    state_player_two

    * */

//Metodos que permiten obtener el identificador unico para las notificaciones

    public void generateNamedUser(){
        UAirship.shared().getNamedUser().forceUpdate();
        String user_id = String.valueOf(userController.show().getUser_id());
        UAirship.shared().getNamedUser().setId(user_id);
    }

//Redirects

    public void next(){
        Intent activity = null;
        User user = userController.show();
        if(user.getActive()) {
            activity = new Intent(Login.this, Timeline.class);
        } if(!(user.getActive())) {
            if(preferenceController.consoles(user.getUser_id()).isEmpty()) {
                activity = new Intent(Login.this, SelectConsole.class);
            } else if(preferenceController.indexGame(user.getUser_id()).isEmpty()) {
                activity = new Intent(Login.this, SelectGame.class);
            } else {
                activity = new Intent(Login.this, Summary.class);
            }
        }
        startActivity(activity);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startServiceRevision();
        finish();
    }

    public void onClickRegister(){
        lbl_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

//**************************** Login usando la api de facebook ****************************//

    @OnClick(R.id.fab_facebook)
    public void onclickFacebook(){
        but_login_facebook.performClick();
        loginFacebook();
    }

    public void setupFacebookSdk(){
        List<String> scopes = new ArrayList<>();
        scopes.add("public_profile");
        scopes.add("email");
        scopes.add("user_birthday");
        but_login_facebook.setReadPermissions(scopes);
        callbackManager = CallbackManager.Factory.create();
    }

    public void loginFacebook(){
        if (utils.connect()){
            dialog.dialogProgress("Validando usuario...");
            but_login_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                JsonObject json = utils.convertToJsonGson(object);
                                String email_social = json.get("email").getAsString();
                                String pass_social = json.get("id").getAsString();
                                login(email_social, pass_social);
                            }
                        }
                    );
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,picture.width(500).height(500)");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
                @Override
                public void onCancel() {
                    dialog.cancelProgress();
                    dialog.dialogWarnings("Atención", "Login cancelado por el usuario.");
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
    }

    public void startServiceRevision(){
        startService(new Intent(context, RevisionService.class));
    }

}
