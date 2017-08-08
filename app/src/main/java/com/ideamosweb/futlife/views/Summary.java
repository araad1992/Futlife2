package com.ideamosweb.futlife.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Adapters.TabPagerAdapter;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.PromptDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class Summary extends AppCompatActivity {

    //Elementos principales de la clase
    private Context context;
    private UserController userController;
    private PreferenceController preferenceController;
    private MaterialDialog dialog;
    private ToastMessages toasts;
    private Utils utils;
    private Typeface bebas_bold;
    private Typeface bebas_regular;
    boolean validate_prefereces;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.lbl_toolbar)TextView lbl_toolbar;
    @Bind(R.id.view_pager)ViewPager view_pager;
    @Bind(R.id.tab_layout)TabLayout tab_layout;
    @Bind(R.id.img_avatar_user)CircleImageView avatar_user;
    @Bind(R.id.lbl_fullname)TextView lbl_fullname;
    @Bind(R.id.lbl_username)TextView lbl_username;
    @Bind(R.id.fab_confirm)FloatingActionButton fab_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);
        Nammu.init(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(context);
        toasts = new ToastMessages(context);
        utils = new Utils(context);
        bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        validate_prefereces = true;
        setupToolbar();
        setupViewPager();
        setupTabLayout();
        setupUserData();
        setupLoadAvatar();
    }

    public void setupToolbar(){
        lbl_toolbar.setTypeface(bebas_bold);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Summary.this, SelectGame.class));
                    overridePendingTransition(R.anim.slide_open_translate, R.anim.slide_close_scale);
                    finish();
                }
            });
        }
    }

    public void setupViewPager(){
        List<String> tab_titles = new ArrayList<>();
        tab_titles.add("consolas");
        tab_titles.add("juegos");
        view_pager.setAdapter(new TabPagerAdapter(
                getSupportFragmentManager(),
                tab_titles.size(),
                tab_titles,
                fab_confirm
        ));
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if(!fab_confirm.isVisible()) {
                            fab_confirm.show();
                        }
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public void setupTabLayout(){
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);
    }

    public void setupUserData(){
        String fullname = userController.show().getName();
        String username = userController.show().getUsername();
        lbl_fullname.setTypeface(bebas_bold);
        lbl_username.setTypeface(bebas_regular);
        lbl_fullname.setText(fullname);
        lbl_username.setText(username);
        setupAvatarUser();
    }

    public void setupAvatarUser(){
        if(userController.show().getAvatar() != null) {
            Picasso.with(context)
                    .load(userController.show().getAvatar())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_default)
                    .into(avatar_user);
        }
    }

    @OnClick(R.id.img_avatar_user)
    public void clickAvatar(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            openGalleryDialog();
        } else {
            String[] permissions = new String[2];
            permissions[0] = Manifest.permission.CAMERA;
            permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            Nammu.askForPermission(this, permissions, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    openGalleryDialog();
                }
                @Override
                public void permissionRefused() {
                    dialog.dialogWarnings("¡Alerta!","Debes aceptar los permisos para el manejo de imagenes en tu equipo.");
                }
            });
        }
    }

    public void openGalleryDialog(){
        EasyImage.openChooserWithGallery(this, "Seleccione una foto para su avatar.", 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                dialog.dialogWarnings("Error","Carga de avatar cancelada.");
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(Summary.this);
                    photoFile.deleteOnExit();
                }
            }

            @Override
            public void onImagePicked(File imagesFiles, EasyImage.ImageSource source, int type) {
                loadImageAvatar(imagesFiles);
            }
        });
    }

    public void loadImageAvatar(File imagesFiles){
        Picasso.with(context)
                .load(imagesFiles)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.avatar_default)
                .into(avatar_user);
        uploadAvatar(imagesFiles);
    }

    public void setupLoadAvatar(){
        EasyImage.configuration(context)
                .setImagesFolderName("FutLife")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true)
                .saveInRootPicturesDirectory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                startActivity(new Intent(Summary.this, SelectGame.class));
                overridePendingTransition(R.anim.slide_open_translate, R.anim.slide_close_scale);
                finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EasyImage.clearConfiguration(context);
        super.onDestroy();
    }

    @OnClick(R.id.fab_confirm)
    public void confirm(){
        if(utils.connect()) {
            String preferences = createArrayPreferences();
            System.out.println("preferences: " + preferences);
            if(validate_prefereces) {
                uploadPreferences(preferences);
            }
        } else {
            dialog.dialogWarnings("Error de conexión", "No se pudo detectar una conexión estable a internet.");
        }
    }

    public String createArrayPreferences(){
        int user_id = userController.show().getUser_id();
        List<ConsolePreference> consoles = preferenceController.consoles(user_id);
        JsonArray preferences = new JsonArray();
        if(!consoles.isEmpty()) {
            for (int i = 0; i < consoles.size(); i++) {
                ConsolePreference console = consoles.get(i);
                JsonObject json_consoles = new JsonObject();
                json_consoles.addProperty("console_id", console.getConsole_id());
                json_consoles.addProperty("active", console.getActive());
                List<GamePreference> games = preferenceController.games(user_id, console.getConsole_id());
                if(!games.isEmpty()) {
                    JsonArray array_games = new JsonArray();
                    for (int j = 0; j < games.size(); j++) {
                        GamePreference game = games.get(j);
                        JsonObject json_games = new JsonObject();
                        json_games.addProperty("game_id", game.getGame_id());
                        json_games.addProperty("active", game.getActive());
                        array_games.add(json_games);
                    }
                    json_consoles.add("games", array_games);
                } else {
                    validate_prefereces = false;
                    dialog.dialogWarnings("Error", "No hay juegos asosiados a la consola " + console.getName() + ".");
                    break;
                }
                preferences.add(json_consoles);
            }
        } else {
            validate_prefereces = false;
            dialog.dialogErrors("Error", "No haz seleccionado ninguna consola.");
        }
        return new Gson().toJson(preferences);
    }

    public void uploadPreferences(String preferences) {
        dialog.dialogProgress("Enviando las preferencias de juego...");
        String token = "Bearer " + userController.show().getToken();
        final int user_id = userController.show().getUser_id();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.uploadPreferences(token, user_id, preferences, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                dialog.cancelProgress();
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    String message = jsonObject.get("message").getAsString();
                    User user = userController.show();
                    user.setActive(true);
                    if(userController.update(user)) {
                        if(preferenceController.clearConsolePreference() && preferenceController.clearGamePreference()) {
                            System.out.println("Se limpiaron las tablas");
                            JsonArray preferences = jsonObject.getAsJsonArray("preferences");
                            savePreferences(preferences);
                        }
                        dialogConfirm("¡Bien!", message);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Summary(uploadPreferences)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Summary(uploadPreferences)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void savePreferences(JsonArray array){
        int user_id = userController.show().getUser_id();
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
            Log.e("Summary(savePreferences)", "Error ex: " + e.getMessage());
        }
    }

    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setWriteTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        return client;
    }

    public void uploadAvatar(File file){
        dialog.dialogProgress("Subiendo avatar...");
        String token = "Bearer " + userController.show().getToken();
        int user_id = userController.show().getUser_id();
        final String url = getString(R.string.url_api);
        TypedFile avatar = new TypedFile("multipart/form-data", file);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .setClient(new OkClient(getClient()))
                .build();
        Api api = restAdapter.create(Api.class);
        api.uploadAvatar(token, user_id, avatar, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, retrofit.client.Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if (success) {
                    String avatar = jsonObject.get("avatar").getAsString();
                    String thumbnail = jsonObject.get("thumbnail").getAsString();
                    String message = jsonObject.get("message").getAsString();
                    User user = userController.show();
                    user.setAvatar(avatar);
                    user.setThumbnail(thumbnail);
                    if(userController.update(user)) {
                        toasts.toastSuccess(message);
                    }
                }
            }
            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Summary(uploadAvatar)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Summary(uploadAvatar)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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

    public void dialogConfirm(String title, String message){
        new PromptDialog(context).setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        startActivity(new Intent(Summary.this, Timeline.class));
                        overridePendingTransition(R.anim.slide_open_translate, R.anim.slide_close_scale);
                        finish();
                    }
                }).show();
    }

}
