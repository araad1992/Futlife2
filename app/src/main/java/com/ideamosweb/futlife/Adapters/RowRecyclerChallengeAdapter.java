package com.ideamosweb.futlife.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.GameController;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.MessageBusTimeline;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.views.ChatRoom;
import com.ideamosweb.futlife.views.Payments;
import com.ideamosweb.futlife.views.Profile;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 21/02/17.
 * Función:
 */
public class RowRecyclerChallengeAdapter extends RecyclerView.Adapter<RowRecyclerChallengeAdapter.AdapterView> {

    private Context context;
    private Context dialogContext;
    private UserController userController;
    private PlayerController playerController;
    private RechargeController rechargeController;
    private ChallengeController challengeController;
    private PreferenceController preferenceController;
    private List<Challenge> challenges = new ArrayList<>();
    private MaterialDialog dialog;
    private ToastMessages toast;

    public RowRecyclerChallengeAdapter(Context context, List<Challenge> challenges) {
        this.context = context;
        this.challenges = challenges;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_challenges_live, parent, false);
        dialogContext = parent.getContext();
        return new AdapterView(layoutView);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.card_challenge_live)CardView card_challenge_live;
        @Bind(R.id.avatar_player_1)CircleImageView avatar_player_1;
        @Bind(R.id.avatar_player_2)CircleImageView avatar_player_2;
        @Bind(R.id.lbl_player_1)TextView lbl_player_1;
        @Bind(R.id.lbl_player_2)TextView lbl_player_2;
        @Bind(R.id.lbl_level_1)TextView lbl_level_1;
        @Bind(R.id.lbl_level_2)TextView lbl_level_2;
        @Bind(R.id.lbl_score_1)TextView lbl_score_1;
        @Bind(R.id.lbl_score_2)TextView lbl_score_2;
        @Bind(R.id.img_thumbnail_game)ImageView img_thumbnail_game;
        @Bind(R.id.lbl_amount)TextView lbl_amount;
        @Bind(R.id.lbl_read_challenge)TextView lbl_read_challenge;
        @Bind(R.id.lbl_state_challenge)TextView lbl_state_challenge;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        userController = new UserController(context);
        playerController = new PlayerController(context);
        rechargeController = new RechargeController(context);
        challengeController = new ChallengeController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        ConsoleController consoleController = new ConsoleController(context);
        GameController gameController = new GameController(context);
        Challenge challenge = challenges.get(position);
        Console console = consoleController.find(challenge.getConsole_id());
        Game game = gameController.find(challenge.getGame_id());
        setPlayerOrUser(holder, challenge);
        setTypeFaceLevel(holder, console, game);
        setLabels(holder, challenge);
        holder.lbl_read_challenge.setVisibility(View.GONE);
        holder.lbl_state_challenge.setVisibility(View.GONE);
        setElementsRival(holder);
        animateCardPlayer(holder.card_challenge_live);
        longClickCard(holder, challenge);
        clickEnjoyOpenChallenge(holder, challenge, console, game);
    }

    public void setPlayerOrUser(AdapterView holder, Challenge challenge){
        Player player = playerController.find(challenge.getPlayer_one());
        if(player != null) {
            loadAvatarPlayer(holder.avatar_player_1, player.getThumbnail());
            setTypeFaceUsername(holder.lbl_player_1, player.getUsername());
            clickCardDetails(holder.avatar_player_1, player.getCode(), false);
        } else {
            User user = userController.show();
            loadAvatarPlayer(holder.avatar_player_1, user.getThumbnail());
            setTypeFaceUsername(holder.lbl_player_1, user.getUsername());
            clickCardDetails(holder.avatar_player_1, user.getCode(), true);
        }
    }

    public void loadAvatarPlayer(CircleImageView avatar_1, String url_thumbnail){
        Picasso.with(context)
                .load(url_thumbnail)
                .fit()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .centerCrop()
                .into(avatar_1);
    }

    public void setTypeFaceUsername(TextView lbl_username_1, String text){
        Typeface bebas_bold = Typeface.createFromAsset(
                context.getAssets(), "fonts/bebas_neue_bold.ttf"
        );
        lbl_username_1.setText(text);
        lbl_username_1.setTypeface(bebas_bold);
    }

    public void setTypeFaceLevel(AdapterView holder, Console console, Game game){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        holder.lbl_level_1.setText(console.getName());
        holder.lbl_level_2.setText(console.getName());
        holder.lbl_level_1.setTypeface(bebas_regular);
        holder.lbl_level_2.setTypeface(bebas_regular);

        Picasso.with(context)
                .load(game.getThumbnail())
                .placeholder(R.drawable.fifa16_mini)
                .error(R.drawable.fifa16_mini)
                .fit()
                .into(holder.img_thumbnail_game);
    }

    public void setLabels(AdapterView holder, Challenge challenge){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        holder.lbl_score_1.setVisibility(View.GONE);
        holder.lbl_score_2.setVisibility(View.GONE);
        holder.lbl_amount.setText("$" + Math.round(challenge.getInitial_value()));
        holder.lbl_amount.setTypeface(bebas_bold);
    }

    public void clickCardDetails(CircleImageView avatar, final int code, final boolean principal){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity)context;
                activity.startActivity(
                        new Intent(context, Profile.class)
                                .putExtra("tab_select", 0)
                                .putExtra("code", code)
                                .putExtra("principal", principal));
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    public void setElementsRival(AdapterView holder){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        holder.lbl_player_2.setText("¡únete!");
        holder.lbl_player_2.setTypeface(bebas_bold);
        Picasso.with(context)
                .load(R.drawable.avatar_default)
                .fit()
                .placeholder(R.drawable.avatar_default)
                .centerCrop()
                .into(holder.avatar_player_2);
    }

    public void animateCardPlayer(CardView card_challenge_open){
        YoYo.with(Techniques.FadeInUp).duration(400).playOn(card_challenge_open);
    }

    public void longClickCard(AdapterView holder, final Challenge challenge){
        holder.card_challenge_live.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(challenge.getPlayer_one() == userController.show().getUser_id()) {
                    String state = challenge.getState();
                    if(state.equalsIgnoreCase("aceptado") || state.equalsIgnoreCase("reportado")) {
                        toast.toastWarning("No puedes eliminar un reto abierto que haya sido aceptado.");
                    } else {
                        dialogDelete(challenge.getChallenge_id());
                    }
                }
                return true;
            }
        });
    }

    public void clickEnjoyOpenChallenge(AdapterView holder, final Challenge challenge, final Console console, final Game game){
        holder.card_challenge_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(challenge.getPlayer_one() != userController.show().getUser_id()) {
                    ConsolePreference consolePreference = preferenceController.consolesValidate(userController.show().getUser_id(), console.getConsole_id());
                    if(consolePreference != null) {
                        if(preferenceController.gameValidate(userController.show().getUser_id(), console.getConsole_id(), game.getGame_id()) != null) {
                            startChallenge(challenge, console, game);
                        } else {
                            toast.toastWarning("Ninguno de tus juegos ligados a la consola coincide con el del reto");
                        }
                    } else {
                        toast.toastWarning("Tu consola no coincide con la del reto");
                    }
                }
            }
        });
    }

//region

    public void dialogDelete(int challenge_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        LayoutInflater inflater = (LayoutInflater)dialogContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        setElementsDelete(view, alertDialog, challenge_id);
        alertDialog.show();
    }

    public void setElementsDelete(View view, final AlertDialog alertDialog, final int challenge_id){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");

        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_delete);
        but_cancel.setTypeface(bebas_regular);
        Button but_done = (Button)view.findViewById(R.id.but_done_delete);
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
                deleteChallenge(challenge_id);
                alertDialog.dismiss();
            }
        });
    }

    public void deleteChallenge(final int challenge_id){
        String token = "Bearer " + userController.show().getToken();
        dialog.dialogProgress("Eliminando reto...");
        String url = context.getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.deleteOpenChallenge(token, challenge_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if(success) {
                    String message = jsonObject.get("message").getAsString();
                    Challenge challenge = challengeController.show(challenge_id);
                    challengeController.delete(challenge);
                    updateValueRecharge(challenge.getInitial_value());
                    actionReceivedChallenge();
                    toast.toastSuccess(message);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("RowRecyclerChallengeAdapter(deleteChallenge)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("RowRecyclerChallengeAdapter(deleteChallenge)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void updateValueRecharge(float initial_value){
        Balance balance = rechargeController.get();
        float actual_value = balance.getValue();
        float total_value = actual_value + initial_value;
        balance.setValue(total_value);
        rechargeController.update(balance);
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
                if(jsonErrors.has("error")){
                    String title = "¡Error!";
                    String message = jsonErrors.get("error").getAsString();
                    dialog.dialogErrors(title, message);
                }
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void actionReceivedChallenge(){
        List<String> empty = new ArrayList<>();
        AppCompatActivity activity = new AppCompatActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.frame_challenges_live);
        StationBus.getBus().post(new MessageBusChallenge(true));
        StationBus.getBus().post(new MessageBusTimeline(true, 1, empty));
    }

//endregion

//region Aceptar retos abiertos

    public void startChallenge(Challenge challenge, Console console, Game game){
        if(rechargeController.get() != null) {
            int value = Math.round(rechargeController.get().getValue());
            String state = rechargeController.get().getState();
            if(state.equalsIgnoreCase("Aceptada")) {
                if(value >= 1000) {
                    if(validatePlayerId()) {
                        dialogOpenChallenger(challenge, console, game);
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

    //Validador de player_id del jugador
    public boolean validatePlayerId(){
        boolean validate = true;
        User user = userController.show();
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

    //Dialog para abrir el reto abierto
    public void dialogOpenChallenger(Challenge challenge, Console console, Game game){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_open_challenge, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setlayoutPlayers(view, challenge);
        setElementsChallenge(view, alertDialog, challenge, console, game);
        alertDialog.show();
    }

    //Dialog para credito
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

    //Seteador de elementos del dialog
    public void setElementsChallenge(View view, final AlertDialog alertDialog, final Challenge challenge, Console console, Game game){
        setPreferencesChallenge(view, challenge, console, game);
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_challenge);
        Button but_done = (Button)view.findViewById(R.id.but_done_challenge);
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
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
                acceptChallenge(challenge.getChallenge_id());
                alertDialog.dismiss();
            }
        });
    }

    //Seteador de elementos del dialog de credito activo
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
                Activity activity = (Activity)context;
                activity.startActivity(new Intent(context, Payments.class));
                alertDialog.dismiss();
            }
        });
    }

    //Seteador de los elementos del dialog del player_id
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
                Activity activity = (Activity)context;
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("tab_select", 2);
                intent.putExtra("code", userController.show().getCode());
                intent.putExtra("principal", true);
                activity.startActivity(intent);
                alertDialog.dismiss();
            }
        });
    }

    //Seteador de animaciones y avatares del dialog
    public void setlayoutPlayers(View view, Challenge challenge){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");

        LinearLayout layout_player_1 = (LinearLayout)view.findViewById(R.id.layout_player_1);
        LinearLayout layout_player_2 = (LinearLayout)view.findViewById(R.id.layout_player_2);
        CircleImageView player_1 = (CircleImageView)view.findViewById(R.id.avatar_player_1);
        CircleImageView player_2 = (CircleImageView)view.findViewById(R.id.avatar_player_2);
        setAvatarPlayers(player_1, player_2, challenge);

        Player player = playerController.find(challenge.getPlayer_one());
        TextView lbl_player_1 = (TextView)view.findViewById(R.id.lbl_player_1);
        lbl_player_1.setText(player.getUsername());
        lbl_player_1.setTypeface(bebas_regular);

        User user = userController.show();
        TextView lbl_player_2 = (TextView)view.findViewById(R.id.lbl_player_2);
        lbl_player_2.setText(user.getUsername());
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

    public void setAvatarPlayers(CircleImageView player_1, CircleImageView player_2, Challenge challenge){
        Player player = playerController.find(challenge.getPlayer_one());
        Picasso.with(context)
                .load(player.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(player_1);
        User user = userController.show();
        Picasso.with(context)
                .load(user.getThumbnail())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(player_2);
    }

    //Seteador de las preferencias del reto abierto
    public void setPreferencesChallenge(View view, Challenge challenge, Console console, Game game){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");

        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        TextView lbl_amount_dialog = (TextView)view.findViewById(R.id.lbl_amount_dialog);
        TextView txt_amount_dialog = (TextView)view.findViewById(R.id.txt_amount_dialog);
        TextView lbl_real_value_bet = (TextView)view.findViewById(R.id.lbl_real_value_bet);
        TextView txt_real_value_bet = (TextView)view.findViewById(R.id.txt_real_value_bet);
        TextView lbl_consoles_dialog = (TextView)view.findViewById(R.id.lbl_consoles_dialog);
        TextView txt_consoles_dialog = (TextView)view.findViewById(R.id.txt_consoles_dialog);
        TextView lbl_games_dialog = (TextView)view.findViewById(R.id.lbl_games_dialog);
        TextView txt_games_dialog = (TextView)view.findViewById(R.id.txt_games_dialog);

        lbl_title_dialog.setTypeface(bebas_regular);
        lbl_amount_dialog.setTypeface(bebas_regular);
        txt_amount_dialog.setTypeface(bebas_regular);
        lbl_real_value_bet.setTypeface(bebas_regular);
        txt_real_value_bet.setTypeface(bebas_regular);
        lbl_consoles_dialog.setTypeface(bebas_regular);
        txt_consoles_dialog.setTypeface(bebas_regular);
        lbl_games_dialog.setTypeface(bebas_regular);
        txt_games_dialog.setTypeface(bebas_regular);

        txt_amount_dialog.setText(String.valueOf(Math.round(challenge.getInitial_value())));
        txt_real_value_bet.setText(String.valueOf(Math.round(challenge.getAmount_bet())));
        txt_consoles_dialog.setText(console.getName());
        txt_games_dialog.setText(game.getName());
    }

//endregion

//region Aceptar retos abiertos

    public void acceptChallenge(int challenge_id){
        String token = "Bearer " + userController.show().getToken();
        int user_id = userController.show().getUser_id();
        int balance_id = rechargeController.get().getBalance_id();
        dialog.dialogProgress("Estableciendo conexión...");
        String url = context.getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.acceptChallenge(token, user_id, challenge_id, balance_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    String message = jsonObject.get("message").getAsString();
                    float new_balance = jsonObject.get("new_balance").getAsFloat();
                    JsonObject json_challenge = jsonObject.get("challenge").getAsJsonObject();
                    Challenge challenge = new Gson().fromJson(json_challenge, Challenge.class);
                    challengeController.create(challenge);
                    Balance balance = rechargeController.get();
                    balance.setValue(new_balance);
                    rechargeController.update(balance);
                    dialog.cancelProgress();
                    openChatRoom(challenge.getChallenge_id(), challenge.getPlayer_one());
                    toast.toastSuccess(message);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequestAction(error);
                try {
                    Log.d("RowRecyclerChallengeAdapter(acceptChallenge)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("RowRecyclerChallengeAdapter(acceptChallenge)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequestAction(RetrofitError retrofitError){
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

    public void openChatRoom(int challenge_id, int player_id){
        Intent intent = new Intent(context, ChatRoom.class);
        intent.putExtra("challenge_id", String.valueOf(challenge_id));
        intent.putExtra("player_id", String.valueOf(player_id));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

//endregion

}
