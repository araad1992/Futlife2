package com.ideamosweb.futlife.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Adapters.TabPagerProfileAdapter;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.EventBus.MessageBusInformation;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.Objects.City;
import com.ideamosweb.futlife.Objects.SettingWeb;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.melnykov.fab.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.polak.clicknumberpicker.ClickNumberPickerListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import pl.polak.clicknumberpicker.PickerClickType;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class Profile extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener, DatePickerDialog.OnDateSetListener {

    private Context context;
    private UserController userController;
    private PreferenceController preferenceController;
    private RechargeController rechargeController;
    private Player player;
    private User user;
    private int user_id;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;
    private List<String> cities;
    private List<City> list_cities;
    private int percentage_animate_avatar;
    private boolean avatar_show;
    private int max_scroll_size;
    private int tab_select;
    private boolean principal;
    private boolean flag_avatar;
    private boolean edit_preferences;

    //Elements from dialog
    private EditText txt_username_profile;
    private EditText txt_fullname_profile;
    private EditText txt_email_profile;
    private EditText txt_telephone_profile;
    private EditText txt_birthdate_profile;
    private AutoCompleteTextView txt_ubication_profile;

    //toolbar:_activity
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.logo_futlife)ImageView logo_futlife;
    @Bind(R.id.lbl_username_player_toolbar)TextView lbl_player_toolbar;
    @Bind(R.id.app_bar)AppBarLayout app_bar;
    @Bind(R.id.view_pager)ViewPager view_pager;
    @Bind(R.id.tab_layout)TabLayout tab_layout;
    @Bind(R.id.fab_edit_avatar)FloatingActionButton fab_edit_avatar;
    @Bind(R.id.fab)FloatingActionButton fab;
    @Bind(R.id.fab_menu)FabSpeedDial fab_menu;
    @Bind(R.id.avatar_profile)CircleImageView avatar_profile;
    @Bind(R.id.lbl_username_player)TextView lbl_username_player;
    @Bind(R.id.lbl_fullname_player)TextView lbl_fullname_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Nammu.init(this);
        setupActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(edit_preferences) {
            if(principal) {
                setupToolbar();
                setupDataUser();
            } else {
                fab_edit_avatar.setVisibility(View.GONE);
                getPlayer(user_id);
            }
            edit_preferences = false;
        }
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
        rechargeController = new RechargeController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);
        cities = new ArrayList<>();
        list_cities = new ArrayList<>();
        percentage_animate_avatar = 20;
        avatar_show = true;
        flag_avatar = false;
        edit_preferences = false;
        getExtras();
        setupAppBarLayout();
        setupLoadAvatar();
        setupFab();
        setupFabMenu();
    }

    public void getExtras(){
        Bundle extras = getIntent().getExtras();
        tab_select = extras.getInt("tab_select");
        principal = extras.getBoolean("principal");
        user_id = extras.getInt("user_id");
        user = userController.show();
        if(principal) {
            setupToolbar();
            setupDataUser();
        } else {
            fab_edit_avatar.setVisibility(View.GONE);
            getPlayer(user_id);
        }
    }

    public String setDataPlayerFull(){
        String full_data;
        if(principal) {
            if(user.getCity_name().equalsIgnoreCase("no-data")){
                full_data = user.getName();
            } else {
                full_data = user.getName() + " • " + user.getCity_name();
            }
        } else {
            if(player.getCity_name() == null || player.getCity_name().isEmpty() || player.getCity_name().equalsIgnoreCase("no-data")){
                full_data = player.getName();
            } else {
                full_data = player.getName() + " • " + player.getCity_name();
            }
        }
        return full_data;
    }

    public void setupFab(){
        if(principal){
            fab_menu.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
    }

    public void setupAppBarLayout(){
        app_bar.addOnOffsetChangedListener(this);
        max_scroll_size = app_bar.getTotalScrollRange();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        if (max_scroll_size == 0) {
            max_scroll_size = appBarLayout.getTotalScrollRange();
        } int percentage = (Math.abs(offset)) * 100 / max_scroll_size;
        if (percentage >= percentage_animate_avatar && avatar_show) {
            avatar_show = false;
            avatar_profile.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(300)
                    .start();
            fab_edit_avatar.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(300)
                    .start();
            logo_futlife.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(300)
                    .start();
            logo_futlife.setVisibility(View.GONE);
            YoYo.with(Techniques.BounceInRight)
                    .duration(700)
                    .playOn(lbl_player_toolbar);
            lbl_player_toolbar.setVisibility(View.VISIBLE);
        } if (percentage <= percentage_animate_avatar && !avatar_show) {
            avatar_show = true;
            avatar_profile.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            fab_edit_avatar.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            logo_futlife.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            logo_futlife.setVisibility(View.VISIBLE);
            lbl_player_toolbar.setVisibility(View.GONE);
        }
    }

    public void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(flag_avatar) {
                        setResult(RESULT_OK);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
            if(principal) {
                toolbar.setTitle(user.getUsername());
            } else {
                toolbar.setTitle(player.getUsername());
            }
            toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.icons));
        }
    }

//region usuario local

    public void setupLoadAvatar(){
        EasyImage.configuration(context)
                .setImagesFolderName("FutLife")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true)
                .saveInRootPicturesDirectory();
    }

    @OnClick(R.id.fab_edit_avatar)
    public void clickAvatarEdit(){
        int permission_camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permission_write_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission_camera == PackageManager.PERMISSION_GRANTED &&
                permission_write_storage == PackageManager.PERMISSION_GRANTED &&
                permission_read_storage == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openChooserWithGallery(this, "Seleccione una foto para su avatar.", 0);
        } else {
            String[] permissions = new String[3];
            permissions[0] = Manifest.permission.CAMERA;
            permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            permissions[2] = Manifest.permission.READ_EXTERNAL_STORAGE;
            Nammu.askForPermission(this, permissions, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openChooserWithGallery(Profile.this, "Seleccione una foto para su avatar.", 0);
                }
                @Override
                public void permissionRefused() {
                    dialog.dialogWarnings("¡Alerta!","Debes aceptar los permisos para el manejo de imagenes en tu equipo.");
                }
            });
        }
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(Profile.this);
                    photoFile.deleteOnExit();
                }
            }

            @Override
            public void onImagePicked(File imagesFiles, EasyImage.ImageSource source, int type) {
                loadImageAvatar(imagesFiles);
            }
        });
    }

    public void loadImageAvatar(File file){
        if(file != null){
            System.out.println("path: " + file.getAbsolutePath());
            Picasso.with(context)
                    .load(file)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_default)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(avatar_profile);
            uploadAvatar(file);
        } else {
            toast.toastError("Error al seleccionar la imagen");
        }
    }

    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setWriteTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        return client;
    }

    public void uploadAvatar(final File file){
        dialog.dialogProgress("Subiendo avatar...");
        String url = getString(R.string.url_api);
        String token = "Bearer " + userController.show().getToken();
        int user_id = userController.show().getUser_id();
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
                        toast.toastSuccess(message);
                        file.deleteOnExit();
                        flag_avatar = true;
                    }
                }
            }
            @Override
            public void failure(RetrofitError error) {
                try {
                    dialog.cancelProgress();
                    errorsRequestAvatar(error);
                    setupAvatarUser();
                } catch (Exception ex) {
                    Log.e("Profile(uploadAvatar)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequestAvatar(RetrofitError retrofitError){
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

//endregion

    @OnClick(R.id.fab)
    public void setupFab(View view){
        if(rechargeController.get() != null) {
            int value = Math.round(rechargeController.get().getValue());
            String state = rechargeController.get().getState();
            if(state.equalsIgnoreCase("Aceptada")) {
                if(value >= 1000) {
                    if(validatePlayerId()) {
                        dialogChallenger();
                    } else {
                        dialogNoPlayerId();
                    }
                } else {
                    dialogNoCredit();
                }
            } else {
                dialog.dialogWarnings("Esperando Confirmación","Estamos procesando tu solicitud de recarga, en cuanto este lista te notificaremos");
            }
        } else {
            dialogNoCredit();
        }
    }

    public boolean validatePlayerId(){
        boolean validate = true;
        List<ConsolePreference> consoles = preferenceController.consoles(user.getUser_id());
        for (int i = 0; i < consoles.size(); i++) {
            String player_id = consoles.get(i).getPlayer_id();
            if(player_id == null || player_id.isEmpty()) {
                validate = false;
                break;
            }
        }
        return validate;
    }

    public void setupDataUser(){
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_username_player.setText(user.getUsername());
        lbl_fullname_player.setText(setDataPlayerFull());
        lbl_player_toolbar.setText(user.getName());
        lbl_username_player.setTypeface(bebas_bold);
        lbl_fullname_player.setTypeface(bebas_regular);
        lbl_player_toolbar.setTypeface(bebas_bold);
        setupAvatarUser();
        setupViewPager();
        setupTabLayout();
    }

    public void setupViewPager(){
        List<String> tab_titles = new ArrayList<>();
        tab_titles.add("consolas");
        if(principal){
            tab_titles.add("información");
        }
        tab_titles.add("estadísticas");
        view_pager.setAdapter(new TabPagerProfileAdapter(
                getSupportFragmentManager(),
                tab_titles, fab, principal,
                player, user_id
        ));
        setupActivityEntry();
    }

    public void setupTabLayout(){
        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);
        ViewGroup view_group = (ViewGroup)tab_layout.getChildAt(0);
        int count_tabs = view_group.getChildCount();
        for (int i = 0; i < count_tabs; i++) {
            ViewGroup group_tabs = (ViewGroup)view_group.getChildAt(i);
            int tab_childs_count = group_tabs.getChildCount();
            for (int j = 0; j < tab_childs_count; j++) {
                View tabViewChild = group_tabs.getChildAt(j);
                if (tabViewChild instanceof TextView) {
                    ((TextView)tabViewChild).setTypeface(
                            Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf")
                    );
                }
            }
        }
    }

    public void setupActivityEntry(){
        if(tab_select == 0){
            view_pager.setCurrentItem(0);
        } else if(tab_select == 1){
            view_pager.setCurrentItem(0);
            app_bar.setExpanded(false, true);
        } else if(tab_select == 2) {
            view_pager.setCurrentItem(1);
            if(principal) {
                app_bar.setExpanded(false, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(context);
        super.onDestroy();
    }

    @OnClick(R.id.avatar_profile)
    public void clickAvatar(){
        String url_avatar;
        String name_user;
        String message;
        if(principal) {
            url_avatar = user.getAvatar();
            name_user = user.getName();
            message = "Aún no eliges una foto de usuario";
        } else {
            url_avatar = player.getAvatar();
            name_user = player.getName();
            message = "Este usuario aún no cuenta con una foto de perfil";
        }

        if(url_avatar != null) {
            Intent intent = new Intent(context, BaseFrames.class);
            intent.putExtra("from", "avatar");
            intent.putExtra("url_avatar", url_avatar);
            intent.putExtra("name_user", name_user);
            startActivity(intent);
        } else {
            toast.toastWarning(message);
        }
    }

    public void setupAvatarUser(){
        String thumbnail = user.getThumbnail();
        Picasso.with(context)
                .load(thumbnail)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(avatar_profile);
    }

//region Dialog de saldo insuficiente

    public void dialogNoCredit(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_credit, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElements(view, alertDialog);
        alertDialog.show();
    }

    public void setElements(View view, final AlertDialog alertDialog){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        TextView title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        title_dialog.setTypeface(bebas_bold);

        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_balance);
        but_cancel.setTypeface(bebas_regular);
        Button but_done = (Button)view.findViewById(R.id.but_done_balance);
        but_done.setTypeface(bebas_regular);

        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Payments.class));
                alertDialog.dismiss();
                finish();
            }
        });
    }

//endregion

//region Dialog para agregar player id

    public void dialogNoPlayerId(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_player_id, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsNoPlayer(view, alertDialog);
        alertDialog.show();
    }

    public void setElementsNoPlayer(View view, final AlertDialog alertDialog){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        TextView title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        title_dialog.setTypeface(bebas_bold);

        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_player);
        but_cancel.setTypeface(bebas_regular);
        Button but_done = (Button)view.findViewById(R.id.but_done_player);
        but_done.setTypeface(bebas_regular);

        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("tab_select", 2);
                intent.putExtra("code", userController.show().getCode());
                intent.putExtra("principal", true);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }

//endregion

//region Dialog de reto

    public void dialogChallenger(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_challenge, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setlayoutPlayers(view);
        setLabelsDialog(view);
        loadDataChallenge(view, alertDialog);
        alertDialog.show();
    }

    public void loadDataChallenge(final View view, final AlertDialog alertDialog) {
        if(utils.isOnline()) {
            getSettings(view, alertDialog);
        } else {
            Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
            TextView lbl_data_not_found = (TextView)view.findViewById(R.id.lbl_data_not_found);
            ProgressBar progress_bar_dialog = (ProgressBar)view.findViewById(R.id.progress_bar_dialog);
            lbl_data_not_found.setText("Imposible conectar a la red Futlife");
            lbl_data_not_found.setTypeface(bebas_regular);
            lbl_data_not_found.setVisibility(View.VISIBLE);
            progress_bar_dialog.setVisibility(View.GONE);
        }
    }

    public void setupViewsDialogChallenge(View view, final AlertDialog alertDialog, SettingWeb setting){
        TextView lbl_data_not_found = (TextView)view.findViewById(R.id.lbl_data_not_found);
        ProgressBar progress_bar_dialog = (ProgressBar)view.findViewById(R.id.progress_bar_dialog);
        LinearLayout layout_options_challenge = (LinearLayout)view.findViewById(R.id.layout_options_challenge);
        lbl_data_not_found.setVisibility(View.GONE);
        progress_bar_dialog.setVisibility(View.GONE);
        layout_options_challenge.setVisibility(View.VISIBLE);
        setupSpinnersAndButtons(view, alertDialog, setting);
    }

    public void setupSpinnersAndButtons(View view, final AlertDialog alertDialog, SettingWeb setting){
        //Spinners de las consolas y juegos
        final MaterialSpinner spinner_consoles = (MaterialSpinner)view.findViewById(R.id.spinner_consoles);
        final MaterialSpinner spinner_games = (MaterialSpinner)view.findViewById(R.id.spinner_games);
        final List<String> titles_consoles = new ArrayList<>();
        titles_consoles.add(0, "Consolas");
        titles_consoles.addAll(preferenceController.showNames(true));
        spinner_consoles.setItems(titles_consoles);
        spinner_games.setItems("Juegos");
        spinner_consoles.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position != 0){
                    List<String> titles_games = new ArrayList<>();
                    titles_games.add(0, "Juegos");
                    int console_id = preferenceController.findForNameConsole(item.toString().trim()).getConsole_id();
                    titles_games.addAll(preferenceController.listGames(console_id));
                    spinner_games.setItems(titles_games);
                } else {
                    spinner_games.setItems("Juegos");
                }
            }
        });
        //Selector de valor
        final ClickNumberPickerView txt_amount_bet = (ClickNumberPickerView)view.findViewById(R.id.txt_amount_bet);
        final TextView txt_real_value_bet = (TextView)view.findViewById(R.id.txt_real_value_bet);
        configurePicker(txt_amount_bet, txt_real_value_bet, setting);
        //Botones del dialog
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_challenge);
        Button but_done = (Button)view.findViewById(R.id.but_done_challenge);
        setStyleButton(but_cancel, but_done);
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = txt_amount_bet.getValue();
                float bet_amount = Float.parseFloat(txt_real_value_bet.getText().toString().substring(1));
                String console = spinner_consoles.getText().toString().trim();
                String game = spinner_games.getText().toString().trim();
                System.out.println(console + ", " + game);
                sendChallenge(value, bet_amount, console, game, alertDialog);
            }
        });
    }

    public void sendChallenge(float value, float bet_amount, String console, String game, AlertDialog alertDialog) {
        if(console.equals("Consolas") || game.equals("Juegos")) {
            toast.toastWarning("Debes seleccionar una consola y un juego para iniciar el reto.");
        } else if(validateAmountBet(value)) {
            toast.toastWarning("El valor que deseas apostar es mayor al de tu saldo disponible.");
        } else {
            int console_id = preferenceController.findForNameConsole(console).getConsole_id();
            int game_id = preferenceController.findForNameGame(game).getGame_id();
            float actual_balance = rechargeController.get().getValue();
            float new_balance = actual_balance - value;
            launchChallenge(console_id, game_id, bet_amount, new_balance, value);
            alertDialog.dismiss();
        }
    }

    public void launchChallenge(int console_id, int game_id, float amount_bet, final float new_balance, float initial_value){
        String token = "Bearer " + user.getToken();
        int player_one = user.getUser_id();
        int player_two = player.getUser_id();
        int balance_id = rechargeController.get().getBalance_id();
        dialog.dialogProgress("Lanzando reto...");
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.challenge(token, player_one, player_two, console_id, game_id, amount_bet, initial_value, balance_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    toast.toastSuccess("¡Tu reto se a enviado exitosamente!");
                    Balance balance = rechargeController.get();
                    balance.setValue(new_balance);
                    rechargeController.update(balance);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.findFragmentById(R.id.frame_challenges_state);
                    StationBus.getBus().post(new MessageBusChallenge(true));
                } else {
                    String message = jsonObject.get("message").getAsString();
                    dialog.dialogWarnings("Alerta", message);
                }
                dialog.cancelProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Profile(uploadAvatar)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Profile(login)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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

    public void setStyleButton(Button but_cancel, Button but_done){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        but_cancel.setTypeface(bebas_regular);
        but_done.setTypeface(bebas_regular);
    }

    public void setlayoutPlayers(View view){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");

        LinearLayout layout_player_1 = (LinearLayout)view.findViewById(R.id.layout_player_1);
        LinearLayout layout_player_2 = (LinearLayout)view.findViewById(R.id.layout_player_2);
        CircleImageView player_1 = (CircleImageView)view.findViewById(R.id.avatar_player_1);
        CircleImageView player_2 = (CircleImageView)view.findViewById(R.id.avatar_player_2);
        setAvatarPlayers(player_1, player_2);

        TextView lbl_player_1 = (TextView)view.findViewById(R.id.lbl_player_1);
        lbl_player_1.setText(user.getUsername());
        lbl_player_1.setTypeface(bebas_regular);

        TextView lbl_player_2 = (TextView)view.findViewById(R.id.lbl_player_2);
        lbl_player_2.setText(player.getUsername());
        lbl_player_2.setTypeface(bebas_regular);

        YoYo.with(Techniques.BounceInLeft)
                .duration(1300)
                .playOn(layout_player_1);
        layout_player_1.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceInRight)
                .duration(1300)
                .playOn(layout_player_2);
        layout_player_2.setVisibility(View.VISIBLE);
    }

    public void setAvatarPlayers(CircleImageView player_1, CircleImageView player_2){
        Picasso.with(context)
                .load(user.getThumbnail())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(player_1);
        Picasso.with(context)
                .load(player.getThumbnail())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(player_2);
    }

    public void setLabelsDialog(View view){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        lbl_title_dialog.setTypeface(bebas_bold);
        TextView lbl_amount_dialog = (TextView)view.findViewById(R.id.lbl_amount_dialog);
        lbl_amount_dialog.setTypeface(bebas_regular);
        TextView lbl_real_value_bet = (TextView)view.findViewById(R.id.lbl_real_value_bet);
        lbl_real_value_bet.setTypeface(bebas_regular);
        TextView lbl_consoles_dialog = (TextView)view.findViewById(R.id.lbl_consoles_dialog);
        lbl_consoles_dialog.setTypeface(bebas_regular);
        TextView lbl_games_dialog = (TextView)view.findViewById(R.id.lbl_games_dialog);
        lbl_games_dialog.setTypeface(bebas_regular);
    }

    public void configurePicker(final ClickNumberPickerView picker_amount, final TextView real_value, final SettingWeb setting){
        picker_amount.setPickerValue(setting.getMin_amount_bet());
        real_value.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf"));
        real_value.setText(realValueBet(picker_amount.getValue(), setting.getPercentage_gain()));
        picker_amount.setClickNumberPickerListener(new ClickNumberPickerListener() {
            @Override
            public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {
                float max_value = 0;
                if(rechargeController.get() != null) {
                    max_value = rechargeController.get().getValue();
                }
                if(currentValue > max_value) {
                    toast.toastWarning("El valor que deseas apostar es mayor al de tu saldo total.");
                }
                String real_amount = realValueBet(currentValue, setting.getPercentage_gain());
                real_value.setText(real_amount);
            }
        });
        realValueBet(picker_amount.getValue(), setting.getPercentage_gain());
    }

    public String realValueBet(float value, float percentage_gain){
        String value_tax;
        float bet_amount = value + value;
        float comission = getComission(percentage_gain, bet_amount);
        int real_value = Math.round(bet_amount - comission);
        value_tax = "$" + real_value;
        return value_tax;
    }

    public float getComission(float value, float max) {
        float percent = ((int)value / 100.0f) * max;
        System.out.println("percent: " + percent);
        return percent;
    }

    public boolean validateAmountBet(float value){
        if(rechargeController.get() != null) {
            float max_value = rechargeController.get().getValue();
            if(value > max_value) {
                return true;
            }
        }
        return false;
    }

    //Settings web
    public void getSettings(final View view, final AlertDialog alertDialog) {
        String platform = "m";
        final String url = context.getString(R.string.url_admin);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.getSettings(platform, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonObject json_setting = jsonObject.get("settings").getAsJsonObject();
                    SettingWeb setting = new Gson().fromJson(json_setting, SettingWeb.class);
                    setupViewsDialogChallenge(view, alertDialog, setting);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                errorsRequest(error);
                try {
                    Log.d("Profile(getSettings)", "Errors: " + error.getBody().toString());
                    Log.d("Profile(getSettings)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Timeline(getSettings)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

//endregion

    public void setupFabMenu(){
        fab_menu.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(android.support.design.widget.FloatingActionButton miniFab, @Nullable TextView label, int itemId) {
                switch (itemId) {
                    case R.id.menu_preferences:
                        openPreferences();
                        break;
                    case R.id.menu_information:
                        openInformationEdit();
                        break;
                }
            }
        });
    }

    public void openInformationEdit() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_information, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setViewsDialogInformation(view, alertDialog);
        alertDialog.show();
    }

    public void setViewsDialogInformation(final View view, final AlertDialog alertDialog){
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_dialog.setTypeface(bebas_regular);
        setupViews(view);
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_dialog);
        Button but_done = (Button)view.findViewById(R.id.but_ok_dialog);
        but_cancel.setTypeface(bebas_regular);
        but_done.setTypeface(bebas_regular);
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(utils.connect()) {
                    updateInformation(view, alertDialog);
                } else {
                    alertDialog.dismiss();
                    dialog.dialogWarnings("Error de conexión", "No se detecto una red de conexión estable a internet.");
                }
            }
        });
    }

    public void updateInformation(View view, AlertDialog alertDialog) {
        String username = txt_username_profile.getText().toString();
        String fullname = txt_fullname_profile.getText().toString();
        String email = txt_email_profile.getText().toString();
        String telephone = txt_telephone_profile.getText().toString();
        String birthdate = txt_birthdate_profile.getText().toString();

        if(username.isEmpty() || fullname.isEmpty() || email.isEmpty()) {
            dialog.dialogWarnings("Alerta", "Los campos de usuario, nombre completo y correo electrónico son obligatorios.");
        } else {
            User user = userController.show();
            user.setUsername(username);
            user.setName(fullname);
            user.setEmail(email);
            user.setTelephone(telephone);
            user.setBirthdate(birthdate);
            user.setUbication(ubicationId());
            updateUser(user, view, alertDialog);
        }
    }

    public void changeViewLoad(boolean flag, View view) {
        LinearLayout layout_content_edit_info = (LinearLayout)view.findViewById(R.id.layout_content_edit_info);
        ProgressBar progress_bar = (ProgressBar)view.findViewById(R.id.progress_bar);
        if(flag) {
            layout_content_edit_info.setVisibility(View.INVISIBLE);
            progress_bar.setVisibility(View.VISIBLE);
        } else {
            progress_bar.setVisibility(View.GONE);
            layout_content_edit_info.setVisibility(View.VISIBLE);
        }
    }

    public void updateUser(User user, final View view, final AlertDialog alertDialog){
        changeViewLoad(true, view);
        final String url = getString(R.string.url_api);
        String token = "Bearer " + userController.show().getToken();
        int user_id = userController.show().getUser_id();
        String string_user = new Gson().toJson(user);
        JsonParser jsonParser = new JsonParser();
        JsonObject json_user = (JsonObject)jsonParser.parse(string_user);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.updateUser(token, user_id, json_user, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                System.out.println("data: " + jsonObject.toString());
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    String message = jsonObject.get("message").getAsString();
                    JsonObject json_user = jsonObject.get("user_update").getAsJsonObject();
                    userController.update(userController.show());
                    toast.toastSuccess(message);
                    updateUserLocal(json_user);
                    StationBus.getBus().post(new MessageBusInformation(true));
                }
                alertDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                changeViewLoad(false, view);
                errorsRequest(error);
                try {
                    Log.d("TabInformation(updateUser)", "Errors: " + error.getBody().toString());
                    Log.d("TabInformation(updateUser)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("TabInformation(updateUser)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public boolean updateUserLocal(JsonObject json_user){
        User user = userController.show();
        if(json_user.has("name")) {
            user.setName(json_user.get("name").getAsString());
        } if(json_user.has("username")) {
            user.setUsername(json_user.get("username").getAsString());
        } if(json_user.has("email")) {
            user.setEmail(json_user.get("email").getAsString());
        } if(json_user.has("ubication")) {
            user.setUbication(json_user.get("ubication").getAsInt());
            user.setCity_name(txt_ubication_profile.getText().toString());
        } if(json_user.has("telephone")) {
            user.setTelephone(json_user.get("telephone").getAsString());
        } if(json_user.has("birthdate")) {
            user.setBirthdate(json_user.get("birthdate").getAsString());
        }
        return userController.update(user);
    }

    public void setupViews(View view) {
        User user = userController.show();
        txt_username_profile = (EditText)view.findViewById(R.id.txt_username_profile);
        txt_fullname_profile = (EditText)view.findViewById(R.id.txt_fullname_profile);
        txt_email_profile = (EditText)view.findViewById(R.id.txt_email_profile);
        txt_telephone_profile = (EditText)view.findViewById(R.id.txt_telephone_profile);
        txt_birthdate_profile = (EditText)view.findViewById(R.id.txt_birthdate_profile);
        txt_ubication_profile = (AutoCompleteTextView)view.findViewById(R.id.txt_ubication_profile);
        setViewDataNull(txt_username_profile, user.getUsername(), "Usuario");
        setViewDataNull(txt_fullname_profile, user.getName(), "Nombre Completo");
        setViewDataNull(txt_email_profile, user.getEmail(), "Correo Electronico");
        setViewDataNull(txt_telephone_profile, user.getTelephone(), "Teléfono");
        setViewDataNull(txt_birthdate_profile, user.getBirthdate(), "Fecha de Nacimiento");
        setViewDataNull(txt_ubication_profile, user.getCity_name(), "Ubicación");
        autoCompleteCities(txt_ubication_profile);
        txt_birthdate_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDate();
            }
        });
    }

    public void autoCompleteCities(AutoCompleteTextView txt_ubication_profile){
        txt_ubication_profile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable text) {
                autocomplete(text.toString().trim());
            }
        });
    }

    public void setViewDataNull(EditText label, String value, String hint){
        if(utils.valueIsNull(value)){
            label.setHint(hint);
        } else {
            label.setText(value);
        }
    }

    public void autocomplete(String keyword){
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.autocomplete(keyword, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonArray array = jsonObject.get("cities").getAsJsonArray();
                    getCitiesArray(array);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TabInformation(autocomplete)", "Errors message: " + error.getMessage());
            }
        });
    }

    public void getCitiesArray(JsonArray array){
        cities.clear();
        list_cities.clear();
        if(array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                JsonObject json_city = array.get(i).getAsJsonObject();
                City city = new Gson().fromJson(json_city, City.class);
                cities.add(city.getName() + ", " + city.getCountry_name());
                list_cities.add(city);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, cities);
        txt_ubication_profile.setAdapter(adapter);
        txt_ubication_profile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txt_ubication_profile.setText(list_cities.get(position).getName());
            }
        });
    }

    public int ubicationId(){
        User user_instance = userController.show();
        String city_name = txt_ubication_profile.getText().toString();
        for (int i = 0; i < list_cities.size(); i++) {
            System.out.println(list_cities.get(i).getName());
            if(list_cities.get(i).getName().equalsIgnoreCase(city_name)){
                return list_cities.get(i).getId();
            }
        }
        return user_instance.getUbication();
    }

    public void showDialogDate(){
        Calendar now = Calendar.getInstance();
        Date d = new Date();
        Calendar before = Calendar.getInstance();
        before.setTime(d);
        before.add(Calendar.YEAR, -14);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.dismissOnPause(false);
        dpd.vibrate(false);
        dpd.setMaxDate(before);
        dpd.showYearPickerFirst(true);
        dpd.setAccentColor(ContextCompat.getColor(context, R.color.color_success));
        dpd.setTitle("Seleccione su fecha de nacimiento");
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("DatePicker", "Cancelado");
            }
        });
        dpd.show(this.getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = (++monthOfYear);
        String date = year + "-" + utils.numberConvert(month) + "-" + utils.numberConvert(dayOfMonth);
        txt_birthdate_profile.setText(date);
    }

    public void openPreferences() {
        edit_preferences = true;
        Intent intent = new Intent(context, SelectConsole.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_edit", true);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

//region Jugadores

    private void getPlayer(final int player_id) {
        dialog.dialogProgress("Obteniendo datos del jugador");
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.showPlayer(token, player_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    player = new Gson().fromJson(jsonObject.get("data").getAsJsonObject(), Player.class);
                    setupToolbar();
                    setupDataPlayer();
                }
                dialog.cancelProgress();
            }
            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Profile(getPlayer)", "Errors: " + error.getBody().toString());
                    Log.d("Profile(getPlayer)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Profile(getPlayer)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void setupDataPlayer(){
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_username_player.setText(player.getUsername());
        lbl_fullname_player.setText(setDataPlayerFull());
        lbl_player_toolbar.setText(player.getName());
        lbl_username_player.setTypeface(bebas_bold);
        lbl_fullname_player.setTypeface(bebas_regular);
        lbl_player_toolbar.setTypeface(bebas_bold);
        setupAvatarProfile(player);
        setupViewPager();
        setupTabLayout();
    }

    public void setupAvatarProfile(Player player){
        String thumbnail = player.getThumbnail();
        Picasso.with(context)
                .load(thumbnail)
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(avatar_profile);
    }

//endregion

}
