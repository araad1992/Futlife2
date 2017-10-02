package com.ideamosweb.futlife.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.Objects.SettingWeb;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.MessageBusTimeline;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.ideamosweb.futlife.views.Payments;
import com.ideamosweb.futlife.views.Profile;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.polak.clicknumberpicker.ClickNumberPickerListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;
import pl.polak.clicknumberpicker.PickerClickType;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 2/02/17.
 * Función:
 */
public class SheetDialogPlayer extends BottomSheetDialogFragment {

    private static Context context_dialog;
    private static Player player;
    private Context context;
    private PreferenceController preferenceController;
    private ChallengeController challengeController;
    private RechargeController rechargeController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;
    private User user;
    private SettingWeb setting;

    @Bind(R.id.img_avatar_user)CircleImageView img_avatar;
    @Bind(R.id.lbl_username)TextView lbl_username_player;
    @Bind(R.id.lbl_fullname)TextView lbl_fullname_player;
    @Bind(R.id.lbl_view_profile)TextView lbl_view_profile;
    @Bind(R.id.lbl_challenge)TextView lbl_challenge;
    @Bind(R.id.lbl_consoles)TextView lbl_consoles;
    @Bind(R.id.lbl_games)TextView lbl_games;

    public static SheetDialogPlayer newInstance(Player player_instance, Context context_instance) {
        context_dialog = context_instance;
        player = player_instance;
        return new SheetDialogPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_timeline, container, false);
        ButterKnife.bind(this, view);
        setDataPlayer();
        return view;
    }

    public void setDataPlayer(){
        context = this.getActivity();
        preferenceController = new PreferenceController(getContext());
        challengeController = new ChallengeController(getContext());
        rechargeController = new RechargeController(getContext());
        dialog = new MaterialDialog(getContext());
        toast = new ToastMessages(getContext());
        utils = new Utils(context);
        UserController userController = new UserController(getContext());
        user = userController.show();
        String avatar = player.getThumbnail();
        String lbl_username = player.getUsername();
        String lbl_fullname = player.getName();
        avatarProfilePlayer(img_avatar, avatar);
        setTypeFace(lbl_username_player, lbl_username, true);
        setTypeFace(lbl_fullname_player, lbl_fullname, false);
        setFontLabels();
    }

    public void avatarProfilePlayer(CircleImageView circle_avatar, String avatar_player){
        if(avatar_player != null) {
            Picasso.with(getContext()).load(avatar_player)
                    .fit()
                    .placeholder(R.drawable.avatar_default)
                    .error(R.drawable.avatar_default)
                    .centerCrop().into(circle_avatar);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.avatar_default)
                    .fit().centerCrop().into(circle_avatar);
        }
    }

    public void setTypeFace(TextView label, String text, boolean item){
        if(item) {
            label.setText(text);
            Typeface bebas_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_bold.ttf");
            label.setTypeface(bebas_bold);
        } else {
            label.setText(text);
            Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");
            label.setTypeface(bebas_regular);
        }
    }

    public void setFontLabels(){
        Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_view_profile.setTypeface(bebas_regular);
        lbl_challenge.setTypeface(bebas_regular);
        lbl_consoles.setTypeface(bebas_regular);
        lbl_games.setTypeface(bebas_regular);
    }

    @OnClick(R.id.but_view_profile)
    public void clickProfile(final View view){
        view.setEnabled(false);
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openProfileTab(0);
                view.setEnabled(true);
            }
        }, 700);
    }

    public void openProfileTab(int tab_select){
        Activity activity = (Activity)getContext();
        activity.startActivity(
                new Intent(getContext(), Profile.class)
                        .putExtra("tab_select", tab_select)
                        .putExtra("user_id", player.getUser_id())
                        .putExtra("principal", false));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        dismiss();
    }

    @OnClick(R.id.but_challenge)
    public void clickChallenge(View view){
        view.setEnabled(false);
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startChallenge();
                dismiss();
            }
        }, 700);
    }

    public void startChallenge(){
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

    public void dialogChallenger(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_challenge, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setlayoutPlayers(view);
        setLabelsDialog(view);
        loadDataChallenge(view, alertDialog);
        alertDialog.show();
    }

    public void dialogNoCredit(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_credit, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsNoCredit(view, alertDialog);
        alertDialog.show();
    }

    public void dialogNoPlayerId(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_player_id, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsNoPlayer(view, alertDialog);
        alertDialog.show();
    }

    public void loadDataChallenge(final View view, final AlertDialog alertDialog) {
        final List<ConsolePreference> equals_consoles = getPreferencesConsoles();
        final List<GamePreference> equals_games = getPreferencesGames(equals_consoles);
        if(utils.connect()) {
            getSettings(view, alertDialog, equals_consoles, equals_games);
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

    public void setupViewsDialogChallenge(View view, final AlertDialog alertDialog, List<ConsolePreference> equals_consoles, List<GamePreference> equals_games){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        TextView lbl_data_not_found = (TextView)view.findViewById(R.id.lbl_data_not_found);
        ProgressBar progress_bar_dialog = (ProgressBar)view.findViewById(R.id.progress_bar_dialog);
        LinearLayout layout_options_challenge = (LinearLayout)view.findViewById(R.id.layout_options_challenge);

        if(equals_consoles.isEmpty()) {
            lbl_data_not_found.setTypeface(bebas_regular);
            lbl_data_not_found.setVisibility(View.VISIBLE);
            progress_bar_dialog.setVisibility(View.GONE);
        } else {
            if(equals_games.isEmpty()) {
                lbl_data_not_found.setText("No tienes juegos en comun con este usuario");
                lbl_data_not_found.setTypeface(bebas_regular);
                lbl_data_not_found.setVisibility(View.VISIBLE);
                progress_bar_dialog.setVisibility(View.GONE);
            } else {
                lbl_data_not_found.setVisibility(View.GONE);
                progress_bar_dialog.setVisibility(View.GONE);
                layout_options_challenge.setVisibility(View.VISIBLE);
                setupSpinnersAndButtons(view, equals_consoles, equals_games, alertDialog);
            }
        }
    }

    public void setupSpinnersAndButtons(View view, final List<ConsolePreference> equals_consoles, final List<GamePreference> equals_games, final AlertDialog alertDialog){
        //Spinners de las consolas y juegos
        final MaterialSpinner spinner_consoles = (MaterialSpinner)view.findViewById(R.id.spinner_consoles);
        final MaterialSpinner spinner_games = (MaterialSpinner)view.findViewById(R.id.spinner_games);
        final List<String> titles_consoles = new ArrayList<>();
        titles_consoles.add(0, "Consolas");
        for (int i = 0; i < equals_consoles.size(); i++) {
            titles_consoles.add(equals_consoles.get(i).getName());
        }
        spinner_consoles.setItems(titles_consoles);
        spinner_games.setItems("Juegos");
        spinner_consoles.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position != 0){
                    int console_id = equals_consoles.get(position - 1).getConsole_id();
                    List<String> titles_games = new ArrayList<>();
                    titles_games.add(0, "Juegos");
                    for (int i = 0; i < equals_games.size(); i++) {
                        if(equals_games.get(i).getConsole_id() == console_id) {
                            titles_games.add(equals_games.get(i).getName());
                        }
                    }
                    spinner_games.setItems(titles_games);
                } else {
                    spinner_games.setItems("Juegos");
                }
            }
        });

        //Selector de valor
        final ClickNumberPickerView txt_amount_bet = (ClickNumberPickerView)view.findViewById(R.id.txt_amount_bet);
        final TextView txt_real_value_bet = (TextView)view.findViewById(R.id.txt_real_value_bet);
        configurePicker(txt_amount_bet, txt_real_value_bet);

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

    public List<ConsolePreference> getPreferencesConsoles(){
        List<ConsolePreference> my_consoles = preferenceController.consoles(user.getUser_id());
        List<ConsolePreference> rival_consoles = preferenceController.consoles(player.getUser_id());
        List<ConsolePreference> equals_consoles = new ArrayList<>();
        for (int i = 0; i < my_consoles.size(); i++) {
            ConsolePreference my_console = my_consoles.get(i);
            for (int j = 0; j < rival_consoles.size(); j++) {
                ConsolePreference rival_console = rival_consoles.get(j);
                if(my_console.equals(rival_console)) {
                    equals_consoles.add(my_console);
                }
            }
        }
        return equals_consoles;
    }

    public List<GamePreference> getPreferencesGames(List<ConsolePreference> equals_consoles) {
        List<GamePreference> my_games;
        List<GamePreference> rival_games;
        List<GamePreference> equals_games = new ArrayList<>();
        for (int i = 0; i < equals_consoles.size(); i++) {
            ConsolePreference console = equals_consoles.get(i);
            my_games = preferenceController.games(user.getUser_id(), console.getConsole_id());
            rival_games = preferenceController.games(player.getUser_id(), console.getConsole_id());
            for (int j = 0; j < my_games.size(); j++) {
                GamePreference my_game = my_games.get(j);
                for (int k = 0; k < rival_games.size(); k++) {
                    GamePreference rival_game = rival_games.get(k);
                    if(my_game.equals(rival_game)) {
                        equals_games.add(my_game);
                    }
                }
            }
        }
        return equals_games;
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
        Typeface bebas_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");
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
                Activity activity = (Activity)context_dialog;
                activity.startActivity(new Intent(context_dialog, Payments.class));
                alertDialog.dismiss();
            }
        });
    }

    public void setElementsNoPlayer(View view, final AlertDialog alertDialog){
        Typeface bebas_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");
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
                Activity activity = (Activity)context_dialog;
                Intent intent = new Intent(context_dialog, Profile.class);
                intent.putExtra("tab_select", 2);
                intent.putExtra("code", user.getCode());
                intent.putExtra("principal", true);
                activity.startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }

    public void launchChallenge(int console_id, int game_id, float amount_bet, final float new_balance, final float initial_value){
        String token = "Bearer " + user.getToken();
        int player_one = user.getUser_id();
        int player_two = player.getUser_id();
        int balance_id = rechargeController.get().getBalance_id();
        dialog.dialogProgress("Lanzando reto...");
        String url = context.getString(R.string.url_api);
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
                    JsonObject jsonChallenge = jsonObject.getAsJsonObject("data");
                    Challenge challenge = new Gson().fromJson(jsonChallenge, Challenge.class);
                    challenge.setRead(true);
                    if(challengeController.create(challenge)) {
                        toast.toastSuccess("¡Tu reto se a enviado exitosamente!");

                        Balance balance = rechargeController.get();
                        balance.setValue(new_balance);
                        rechargeController.update(balance);
                        List<String> empty = new ArrayList<>();
                        StationBus.getBus().post(new MessageBusTimeline(true, 1, empty));

                        AppCompatActivity activity = new AppCompatActivity();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        fragmentManager.findFragmentById(R.id.frame_challenges_state);
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
                    Log.d("SheetDialogPlayer(uploadAvatar)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("SheetDialogPlayer(login)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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
        Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");

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
        Picasso.with(getContext())
                .load(user.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(player_1);
        Picasso.with(getContext())
                .load(player.getThumbnail())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(player_2);
    }

    public void setLabelsDialog(View view){
        Typeface bebas_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");
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

    public void configurePicker(final ClickNumberPickerView picker_amount, final TextView lbl_real_value_card){
        float min_amount = setting.getMin_amount_bet();
        picker_amount.setPickerValue(min_amount);
        lbl_real_value_card.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf"));
        lbl_real_value_card.setText(realValueBet(picker_amount.getValue()));
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
                String real_value = realValueBet(currentValue);
                lbl_real_value_card.setText(real_value);
            }
        });
        realValueBet(picker_amount.getValue());
    }

    public String realValueBet(float value){
        String value_tax;
        float bet_amount = value + value;
        float comission = getComission(setting.getPercentage_gain(), bet_amount);
        int real_value = Math.round(bet_amount - comission);
        value_tax = "$" + real_value;
        return value_tax;
    }

    public float getComission(float value, float max) {
        float percent = ((int)value / 100.0f) * max;
        System.out.println("percent: " + percent);
        return percent;
    }

    @OnClick(R.id.but_consoles)
    public void clickConsoles(View view){
        view.setEnabled(false);
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openProfileTab(1);
            }
        }, 700);
    }

    @OnClick(R.id.but_record)
    public void clickHistory(View view){
        view.setEnabled(false);
        YoYo.with(Techniques.Swing)
                .duration(700)
                .playOn(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openProfileTab(2);
            }
        }, 700);
    }

    @OnClick(R.id.but_exit)
    public void exit(){
        dismiss();
    }

    //Settings web
    public void getSettings(final View view, final AlertDialog alertDialog, final List<ConsolePreference> equals_consoles, final List<GamePreference> equals_games) {
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
                    setting = new Gson().fromJson(json_setting, SettingWeb.class);
                    setupViewsDialogChallenge(view, alertDialog, equals_consoles, equals_games);
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

}
