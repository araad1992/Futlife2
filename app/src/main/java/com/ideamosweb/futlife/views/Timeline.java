package com.ideamosweb.futlife.views;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Adapters.SheetDialogFilter;
import com.ideamosweb.futlife.Adapters.TabPagerTimelineAdapter;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Objects.SettingWeb;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.MessageBusSearch;
import com.ideamosweb.futlife.EventBus.MessageBusTimeline;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Service.RevisionService;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.urbanairship.UAirship;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.polak.clicknumberpicker.ClickNumberPickerListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import pl.polak.clicknumberpicker.PickerClickType;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

public class Timeline extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Iniciadores
    private Context context;
    private UserController userController;
    private PlayerController playerController;
    private PreferenceController preferenceController;
    private RechargeController rechargeController;
    private ChallengeController challengeController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;
    private static int code_intro;
    private boolean flag_search;
    private Menu menu;

    //Elementos del navigation drawer
    private ImageView img_nav_header;
    private CircleImageView img_avatar_user;
    private TextView lbl_fullname_user;
    private TextView lbl_email_user;

    //Elementos de la vista
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.drawer_layout)DrawerLayout drawer_layout;
    @Bind(R.id.nav_view)NavigationView nav_view;
    @Bind(R.id.fab)FloatingActionButton fab;
    @Bind(R.id.tab_layout)TabLayout tab_layout;
    @Bind(R.id.view_pager)ViewPager view_pager;
    @Bind(R.id.txt_search)EditText txt_search;
    @Bind(R.id.logo_futlife)ImageView logo_futlife;
    @Bind(R.id.lbl_user_balance)TextView lbl_user_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
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
    public void recievedMessage(MessageBusTimeline messageBusTimeline){
        boolean refresh = messageBusTimeline.isActive();
        int option = messageBusTimeline.getOption();
        if(refresh) {
            if(option == 1) {
                if(rechargeController.get() == null){
                    lbl_user_balance.setText("Saldo actual: $0");
                } else {
                    int value = Math.round(rechargeController.get().getValue());
                    lbl_user_balance.setText("Saldo actual: $" + String.valueOf(value));
                    YoYo.with(Techniques.Flash).duration(700).playOn(lbl_user_balance);
                }
            } else if(option == 2) {
                List<String> items = messageBusTimeline.getList();
                for (int i = 0; i < items.size(); i++) {
                    System.out.println("item: " + items.get(i));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rechargeController.get() == null){
            lbl_user_balance.setText("Saldo actual: $0");
        } else {
            int value = Math.round(rechargeController.get().getValue());
            lbl_user_balance.setText("Saldo actual: $" + String.valueOf(value));
        }
        if(!playerController.get().isEmpty()) {
            setupViewPager();
        }
        activateService();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        playerController = new PlayerController(context);
        preferenceController = new PreferenceController(context);
        rechargeController = new RechargeController(context);
        challengeController = new ChallengeController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);
        code_intro = 13;
        flag_search = false;
        setupToolbar();
        setupDrawerLayout();
        loadDataUser();
        setupViewPager();
        createPreferences();
        setupSearch();
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

    public void setupToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        fab.hide();
        lbl_user_balance.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf"));
        if(rechargeController.get() == null){
            lbl_user_balance.setText("Saldo actual: $0");
        } else {
            int value = Math.round(rechargeController.get().getValue());
            lbl_user_balance.setText("Saldo actual: $" + String.valueOf(value));
        }
    }

    public void setupDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        nav_view.setSelected(true);
        View header = nav_view.getHeaderView(0);
        img_nav_header = (ImageView)header.findViewById(R.id.img_nav_header);
        img_avatar_user = (CircleImageView)header.findViewById(R.id.img_avatar_user);
        lbl_fullname_user = (TextView)header.findViewById(R.id.lbl_fullname_navigation);
        lbl_email_user = (TextView)header.findViewById(R.id.lbl_email_navigation);
        clickAvatarMenu();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_profile:
                openProfile();
                break;
            case R.id.action_payments:
                openPayments();
                break;
            case R.id.action_retiros:
                openRetreats();
                break;
            case R.id.action_expired:
                openExpired();
                break;
            case R.id.action_reported:
                openReported();
                break;
            case R.id.action_settings:
                openSettings();
                break;
            case R.id.action_intro:
                setupIntroView();
                break;
            case R.id.action_about:
                dialogAbout();
                break;
            case R.id.action_terms_conditions:
                openTermsConditions();
                break;
            case R.id.action_contact:
                openContact();
                break;
            case R.id.action_logout:
                actionLogout();
                break;
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clickAvatarMenu() {
        img_avatar_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.closeDrawer(GravityCompat.START);
                openProfile();
            }
        });
    }

    public void openProfile(){
        Intent intent = new Intent(context, Profile.class);
        intent.putExtra("code", userController.show().getCode());
        intent.putExtra("principal", true);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openPayments(){
        Intent intent = new Intent(context, Payments.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openRetreats(){
        if(rechargeController.get() != null) {
            Intent intent = new Intent(context, Retreats.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            dialog.dialogWarnings("Error", "Aun no tienes una recarga activa para retirar dinero de tu cuenta.");
        }
    }

    public void openReported(){
        Intent intent = new Intent(context, Reported.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openExpired(){
        Intent intent = new Intent(context, Expired.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openSettings(){
        Intent intent = new Intent(context, Settings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openTermsConditions(){
        Intent intent = new Intent(context, TermsConditions.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void openContact(){
        Intent intent = new Intent(context, Feedback.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void actionLogout(){
        boolean is_connect = utils.connect();
        if(is_connect) {
            String token = "Bearer " + userController.show().getToken();
            if(userController.show().getSocial()) {
                LoginManager.getInstance().logOut();
            }
            dialogConfirm(token);
        } else {
            dialog.dialogWarnings("Error de conexión",
                    "No se pudo detectar una conexión estable a internet.");
        }
    }

    public void dialogConfirm(final String token){
        ColorDialog color_dialog = new ColorDialog(context);
        color_dialog.setTitle("Logout");
        color_dialog.setContentText("¿Deseas realmente cerrar la sesión?");
        color_dialog.setPositiveListener("Si", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog prompt_dialog) {
                prompt_dialog.dismiss();
                boolean is_connect = utils.connect();
                if(is_connect) {
                    logout(token);
                } else {
                    dialog.dialogWarnings("Error de conexión",
                            "No se pudo detectar una conexión estable a internet.");
                }
            }
        }).setNegativeListener("No", new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    public void exit(){
        stopService(new Intent(context, RevisionService.class));
        UAirship.shared().getNamedUser().setId("0");
        startActivity(new Intent(Timeline.this, Login.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void setupIntroView(){
        Intent intro = new Intent(context, MaterialTutorialActivity.class);
        intro.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getIntroItems());
        startActivityForResult(intro, code_intro);
    }

    private ArrayList<TutorialItem> getIntroItems() {
        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        TutorialItem tutorialItem1 = new TutorialItem(
                "",
                null,
                R.color.colorTransparent, R.drawable.image_one
        );
        TutorialItem tutorialItem2 = new TutorialItem(
                "",
                null,
                R.color.colorTransparent, R.drawable.image_two
        );
        TutorialItem tutorialItem3 = new TutorialItem(
                "",
                null,
                R.color.colorTransparent, R.drawable.image_three
        );
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        return tutorialItems;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == code_intro) {
                System.out.println("Resultado ok");
            }
        }
    }

    public void setupViewPager(){
        List<String> tab_titles = new ArrayList<>();
        tab_titles.add("mis retos");
        tab_titles.add("jugadores");
        tab_titles.add("¡únete!");
        view_pager.setAdapter(new TabPagerTimelineAdapter(
                getSupportFragmentManager(),
                tab_titles
        ));
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if(fab.isVisible()) {
                            fab.hide();
                        }
                        break;
                    case 1:
                        if(fab.isVisible()) {
                            fab.hide();
                        }
                        break;
                    case 2:
                        if(!fab.isVisible()) {
                            fab.show();
                        }
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        setupTabLayout();
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
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf"));
                }
            }
        }
        view_pager.setCurrentItem(1);
    }

    public void loadDataUser(){
        User user = userController.show();
        lbl_fullname_user.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf"));
        lbl_fullname_user.setText(user.getName());
        lbl_email_user.setText(user.getEmail());
        lbl_email_user.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf"));
        Picasso.with(context)
                .load(R.drawable.background_navigation)
                .fit().centerCrop()
                .into(img_nav_header);
        Picasso.with(context)
                .load(user.getThumbnail())
                .centerCrop()
                .fit()
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(img_avatar_user);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_timeline_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if(flag_search) {
                    hideInputSearch();
                    flag_search = false;
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(context, R.drawable.ic_search));
                }
                break;
            case R.id.action_search:
                flag_search = true;
                menu.getItem(0).setIcon(ContextCompat.getDrawable(context, R.drawable.ic_action_clear));
                showInputSearch();
                break;
            case R.id.action_filter:
                SheetDialogFilter sheet_dialog = SheetDialogFilter.newInstance(context);
                sheet_dialog.setCancelable(true);
                sheet_dialog.show(((FragmentActivity)context).getSupportFragmentManager(), "Sheet Dialog Filter");
                break;
        }
        return true;
    }

    public void hideInputSearch(){
        txt_search.setText("");
        YoYo.with(Techniques.SlideOutRight)
                .duration(700)
                .playOn(txt_search);
        animateInputSearch();
        flag_search = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.frame_players);
        StationBus.getBus().post(new MessageBusSearch("", flag_search));
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showInputSearch(){
        logo_futlife.setVisibility(View.GONE);
        YoYo.with(Techniques.SlideInRight)
                .duration(700)
                .playOn(txt_search);
        txt_search.setVisibility(View.VISIBLE);
        txt_search.requestFocus();
        flag_search = true;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txt_search, InputMethodManager.SHOW_IMPLICIT);
    }

    public void animateInputSearch(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logo_futlife.setVisibility(View.VISIBLE);
                txt_search.setVisibility(View.GONE);
            }
        }, 800);
    }

    public void setupSearch(){
        txt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txt_search.getWindowToken(), 0);
                    String keyword = txt_search.getText().toString();
                    if(keyword.equalsIgnoreCase("")) {
                        hideInputSearch();
                    } else {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.findFragmentById(R.id.frame_players);
                        StationBus.getBus().post(new MessageBusSearch(keyword, true));
                    }
                    return true;
                }
                return false;
            }
        });
    }

//*********************************** Metodos asincronicos ***********************************//

    public void logout(String token){
        dialog.dialogProgress("Cerrando sesión...");
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.logout(token, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    String message = jsonObject.get("message").getAsString();
                    if(userController.logout()){
                        dialog.cancelProgress();
                        System.out.println(message);
                        exit();
                    }
                } else {
                    dialog.cancelProgress();
                    dialog.dialogErrors("Error", "Error durante el cierre de sesión.");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                try {
                    dialog.dialogErrors("Error", "Se ha producido un error durante el proceso, intentarlo más tarde.");
                    Log.e("Timeline(logout)", "Error: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Timeline(logout)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

//region Lanzar retos abiertos

    @OnClick(R.id.fab)
    public void clickFabChallenges(View view){
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
            } else if(state.equalsIgnoreCase("Pendiente")) {
                dialog.dialogWarnings("Esperando Confirmación","Estamos procesando tu solicitud de recarga, en cuanto este lista te notificaremos");
            }
        } else {
            dialogNoCredit();
        }
    }

    public boolean validatePlayerId(){
        boolean validate = true;
        List<ConsolePreference> consoles = preferenceController.consoles(userController.show().getUser_id());
        for (int i = 0; i < consoles.size(); i++) {
            String player_id = consoles.get(i).getPlayer_id();
            if(player_id == null || player_id.isEmpty()) {
                validate = false;
                break;
            }
        }
        return validate;
    }

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
        if(utils.connect()) {
            getSettings(view, alertDialog);
        } else {
            Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
            TextView lbl_data_not_found = (TextView)view.findViewById(R.id.lbl_data_not_found);
            ProgressBar progress_bar_dialog = (ProgressBar)view.findViewById(R.id.progress_bar_dialog);
            lbl_data_not_found.setText("No tienes juegos en comun con este usuario");
            lbl_data_not_found.setTypeface(bebas_regular);
            lbl_data_not_found.setVisibility(View.VISIBLE);
            progress_bar_dialog.setVisibility(View.GONE);
        }
    }

    public void dialogNoCredit(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_credit, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsNoCredit(view, alertDialog);
        alertDialog.show();
    }

    public void dialogNoPlayerId(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_player_id, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsNoPlayer(view, alertDialog);
        alertDialog.show();
    }

    public void dialogAbout(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsAbout(view, alertDialog);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    //*********************************** Elementos de los dialogs ***********************************//

    public void setElementsChallenge(View view, final AlertDialog alertDialog, SettingWeb setting){

        //Selector de valor
        final ClickNumberPickerView txt_amount_bet = (ClickNumberPickerView)view.findViewById(R.id.txt_amount_bet);
        final TextView txt_real_value_bet = (TextView)view.findViewById(R.id.txt_real_value_bet);
        configurePicker(txt_amount_bet, txt_real_value_bet, setting);
        //Spinners de las consolas y juegos
        final MaterialSpinner spinner_consoles = (MaterialSpinner)view.findViewById(R.id.spinner_consoles);
        final MaterialSpinner spinner_games = (MaterialSpinner)view.findViewById(R.id.spinner_games);
        configureSpinners(spinner_consoles, spinner_games);
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
                String console = spinner_consoles.getText().toString();
                String game = spinner_games.getText().toString();
                if(console.equals("Consolas") || game.equals("Juegos")) {
                    toast.toastWarning("Debes seleccionar una consola y un juego para iniciar el reto.");
                } else if(validateAmountBet(value)) {
                    toast.toastWarning("El valor que deseas apostar es mayor al de tu saldo disponible.");
                } else {
                    int console_id = preferenceController.findForNameConsole(console).getConsole_id();
                    int game_id = preferenceController.findForNameGame(game).getGame_id();
                    float actual_balance = rechargeController.get().getValue();
                    float new_balance = actual_balance - value;
                    launchOpenChallenge(console_id, game_id, bet_amount, new_balance, value);
                    alertDialog.dismiss();
                }
            }
        });
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

    public void setElementsNoCredit(View view, final AlertDialog alertDialog){
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
            }
        });
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

    public void setElementsAbout(View view, final AlertDialog alertDialog){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        ImageView img_icon_app = (ImageView)view.findViewById(R.id.img_icon_app);
        TextView title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        TextView lbl_subtitle_dialog = (TextView)view.findViewById(R.id.lbl_subtitle_dialog);
        title_dialog.setTypeface(bebas_bold);
        lbl_subtitle_dialog.setTypeface(bebas_regular);
        ImageButton but_exit = (ImageButton) view.findViewById(R.id.but_exit);
        but_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        img_icon_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickInstagram(v, alertDialog);
            }
        });
    }

    public void onclickInstagram(View view, AlertDialog alertDialog){
        YoYo.with(Techniques.Pulse).duration(300).playOn(view);
        Uri uri = Uri.parse("https://www.instagram.com/_u/futlife.co/");
        Intent i= new Intent(Intent.ACTION_VIEW,uri);
        i.setPackage("com.instagram.android");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/futlife.co/")));
        }
        alertDialog.dismiss();
    }

    //Personalizacion de los elementos dle dialog de retos
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
        lbl_player_1.setText(userController.show().getUsername());
        lbl_player_1.setTypeface(bebas_regular);

        TextView lbl_player_2 = (TextView)view.findViewById(R.id.lbl_player_2);
        lbl_player_2.setText("Retador");
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
                .load(userController.show().getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(player_1);
        Picasso.with(context)
                .load(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .into(player_2);
    }

    public void setLabelsDialog(View view){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        lbl_title_dialog.setTypeface(bebas_bold);
        TextView lbl_amount_dialog = (TextView)view.findViewById(R.id.lbl_amount_dialog);
        lbl_amount_dialog.setTypeface(bebas_regular);
        TextView lbl_real_amount_dialog = (TextView)view.findViewById(R.id.lbl_real_value_bet);
        lbl_real_amount_dialog.setTypeface(bebas_regular);
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
                String real_value_bet = realValueBet(currentValue, setting.getPercentage_gain());
                real_value.setText(real_value_bet);
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

    public void configureSpinners(MaterialSpinner spinner_consoles, final MaterialSpinner spinner_games){
        final List<String> titles_consoles = new ArrayList<>();
        titles_consoles.add(0, "Consolas");
        List<ConsolePreference> consoles = preferenceController.consoles(userController.show().getUser_id());
        for (int i = 0; i < consoles.size(); i++) {
            titles_consoles.add(consoles.get(i).getName());
        }
        spinner_consoles.setItems(titles_consoles);
        spinner_games.setItems("Juegos");
        spinner_consoles.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position != 0){
                    int console_id = preferenceController.findForNameConsole(titles_consoles.get(position)).getConsole_id();
                    List<GamePreference> games = preferenceController.games(userController.show().getUser_id(), console_id);
                    List<String> titles_games = new ArrayList<>();
                    for (int i = 0; i < games.size(); i++) {
                        titles_games.add(games.get(i).getName());
                    }
                    titles_games.add(0, "Juegos");
                    spinner_games.setItems(titles_games);
                } else {
                    spinner_games.setItems("Juegos");
                }
            }
        });
    }

    //Función para enviar a la api un reto abierto ya configurado
    public void launchOpenChallenge(int console_id, int game_id, float amount_bet, final float new_balance, float initial_value){
        String token = "Bearer " + userController.show().getToken();
        int player_one = userController.show().getUser_id();
        int balance_id = rechargeController.get().getBalance_id();
        dialog.dialogProgress("Lanzando reto...");
        String url = context.getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.openChallenge(token, player_one, console_id, game_id, amount_bet, initial_value, balance_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    JsonObject jsonChallenge = jsonObject.getAsJsonObject("data");
                    Challenge challenge = new Gson().fromJson(jsonChallenge, Challenge.class);
                    challenge.setRead(true);
                    if(challengeController.create(challenge)) {
                        toast.toastSuccess("¡Tu reto abierto se a enviado exitosamente!");
                        System.out.println("Reto abierto: " + challenge.toString());

                        Balance balance = rechargeController.get();
                        balance.setValue(new_balance);
                        rechargeController.update(balance);
                        lbl_user_balance.setText("Saldo actual: $" + Math.round(new_balance));
                        YoYo.with(Techniques.Flash).duration(700).playOn(lbl_user_balance);

                        AppCompatActivity activity = new AppCompatActivity();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentManager.findFragmentById(R.id.frame_challenges_live);
                        StationBus.getBus().post(new MessageBusChallenge(true));
                    }
                }
                dialog.cancelProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Timeline(launchOpenChallenge)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Timeline(launchOpenChallenge)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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

//endregion

//region Check if service is running

    public void activateService(){
        if(!serviceIsRunnig()) {
            startService(new Intent(context, RevisionService.class));
        }
    }

    private boolean serviceIsRunnig() {
        String service_name = RevisionService.class.getName();
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(service_name.equalsIgnoreCase(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//endregion

//region Check if service is running

    public void getSettings(final View view, final AlertDialog alertDialog) {
        String platform = "m";
        final String url = getString(R.string.url_admin);
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

                    TextView lbl_data_not_found = (TextView)view.findViewById(R.id.lbl_data_not_found);
                    ProgressBar progress_bar_dialog = (ProgressBar)view.findViewById(R.id.progress_bar_dialog);
                    LinearLayout layout_options_challenge = (LinearLayout)view.findViewById(R.id.layout_options_challenge);

                    lbl_data_not_found.setVisibility(View.GONE);
                    progress_bar_dialog.setVisibility(View.GONE);
                    layout_options_challenge.setVisibility(View.VISIBLE);

                    setElementsChallenge(view, alertDialog, setting);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                errorsRequest(error);
                try {
                    Log.d("Timeline(getSettings)", "Errors: " + error.getBody().toString());
                    Log.d("Timeline(getSettings)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("Timeline(getSettings)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

//endregion

}
