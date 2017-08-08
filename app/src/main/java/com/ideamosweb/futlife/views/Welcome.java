package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.GameController;
import com.ideamosweb.futlife.Controllers.PlatformController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.Platform;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.ToastMessages;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Welcome extends Activity {

    //Iniciadores
    private UserController userController;
    private PreferenceController preferenceController;
    private PlatformController platformController;
    private ConsoleController consoleController;
    private GameController gameController;
    private ToastMessages toast;

    //Elementos de la vista
    @Bind(R.id.icon_logo)ImageView icon_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        soundPlay();
        setupActivity();
    }

    public void setupActivity(){
        Context context = this;
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
        platformController = new PlatformController(context);
        consoleController = new ConsoleController(context);
        gameController = new GameController(context);
        toast = new ToastMessages(context);
        datConfiguration();
        setupLogo();
        setupSession();
    }

    public void soundPlay(){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.efect_stadium_short);
        mp.start();
    }

    public void setupLogo(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FlipInX)
                        .duration(700)
                        .playOn(icon_logo);
                icon_logo.setVisibility(View.VISIBLE);
            }
        }, 1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.Pulse)
                        .duration(700)
                        .playOn(icon_logo);
            }
        }, 3500);
    }

    public void setupSession(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean session = userController.session();
                slideTransition(session);
            }
        }, 5000);
    }

    public void slideTransition(boolean activity){
        User user = userController.show();
        if(activity) {
            if(user.getActive()) {
                startActivity(new Intent(Welcome.this, Timeline.class));
            } if(!(user.getActive())) {
                if(preferenceController.consoles(user.getUser_id()).isEmpty()) {
                    startActivity(new Intent(Welcome.this, SelectConsole.class));
                } else if(preferenceController.indexGame(user.getUser_id()).isEmpty()) {
                    startActivity(new Intent(Welcome.this, SelectGame.class));
                } else {
                    startActivity(new Intent(Welcome.this, Summary.class));
                }
            }
        } else {
            startActivity(new Intent(Welcome.this, Login.class));
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void datConfiguration(){
        if(!consoleController.stocks()){
            getParameters();
        }
    }

    public void getParameters() {
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.getParameters(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonArray platforms = jsonObject.getAsJsonArray("platforms");
                JsonArray consoles = jsonObject.getAsJsonArray("consoles");
                JsonArray games = jsonObject.getAsJsonArray("games");
                savePlatforms(platforms);
                saveConsoles(consoles);
                saveGames(games);
            }

            @Override
            public void failure(RetrofitError error) {
                errorsRequest(error);
                try {
                    Log.d("Welcome(getParameters)", "Error body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Welcome(getParameters)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequest(RetrofitError retrofitError){
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            toast.toastWarning(retrofitError.getMessage());
        } else {
            int status = retrofitError.getResponse().getStatus();
            if(status == 400) {
                String error = retrofitError.getBody().toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonErrors = (JsonObject)parser.parse(error);
                if(jsonErrors.has("error")){
                    String message = jsonErrors.get("error").getAsString();
                    toast.toastWarning(message);
                }
            } else {
                String message = retrofitError.getMessage();
                toast.toastError(message);
            }
        }
    }

    public boolean savePlatforms(JsonArray array){
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject json = array.get(i).getAsJsonObject();
                    Platform platform = new Gson().fromJson(json, Platform.class);
                    System.out.println(platform.toString());
                    platformController.create(platform);
                }
            }
        } catch (JsonIOException e) {
            Log.e("Welcome(savePlatforms)", "Error ex: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveConsoles(JsonArray array){
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject json = array.get(i).getAsJsonObject();
                    Console console = new Gson().fromJson(json, Console.class);
                    System.out.println(console.toString());
                    consoleController.create(console);
                }
            }
        } catch (JsonIOException e){
            Log.e("Welcome(saveConsoles)", "Error ex: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveGames(JsonArray array){
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject json = array.get(i).getAsJsonObject();
                    Game game = new Gson().fromJson(json, Game.class);
                    System.out.println(game.toString());
                    gameController.create(game);
                }
            }
        } catch (JsonIOException e){
            Log.e("Welcome(saveGames)", "Error ex: " + e.getMessage());
            return false;
        }
        return true;
    }

}
