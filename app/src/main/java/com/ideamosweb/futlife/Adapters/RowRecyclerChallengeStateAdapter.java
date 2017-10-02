package com.ideamosweb.futlife.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.GameController;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.views.BaseFrames;
import com.ideamosweb.futlife.views.ChatRoom;
import com.ideamosweb.futlife.views.Profile;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 27/03/17.
 * Función:
 */
public class RowRecyclerChallengeStateAdapter extends RecyclerView.Adapter<RowRecyclerChallengeStateAdapter.AdapterView> {

    private Context context;
    private Context dialogContext;
    private UserController userController;
    private ConsoleController consoleController;
    private GameController gameController;
    private List<Challenge> challenges_adapter;
    private ChallengeController challengeController;
    private MaterialDialog dialog;
    private ToastMessages toast;

    public RowRecyclerChallengeStateAdapter(Context context, List<Challenge> challenges) {
        this.context = context;
        this.challenges_adapter = challenges;
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
        return challenges_adapter.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.card_challenge_live)CardView card_challenge;
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
        consoleController = new ConsoleController(context);
        gameController = new GameController(context);
        challengeController = new ChallengeController(context);
        dialog = new MaterialDialog(dialogContext);
        toast = new ToastMessages(dialogContext);
        Challenge challenge = challenges_adapter.get(position);
        Player player = findPlayerNotNull(challenge.getPlayer_one(), challenge.getPlayer_two());
        loadAvatars(holder.avatar_player_1, holder.avatar_player_2, player);
        clickProfilePlayer(holder.avatar_player_2, player);
        setUsernames(holder.lbl_player_1, holder.lbl_player_2, player.getUsername());
        setLabelsChallenge(holder, challenge);
        setGames(holder.img_thumbnail_game, challenge.getGame_id());
        setAmountValue(holder.lbl_amount, challenge.getInitial_value());
        isReadChallenge(holder, challenge);
        openChallenge(holder, challenge, player);
        animateCardPlayer(holder.card_challenge, challenge);
        setStateChallenge(holder, challenge.getState());
        haveScore(holder, challenge);
    }

    public void animateCardPlayer(CardView card_challenge, Challenge challenge){
        if(challenge.getState().equalsIgnoreCase("en juego")) {
            YoYo.with(Techniques.BounceIn).duration(1300).playOn(card_challenge);
        } else {
            YoYo.with(Techniques.FadeIn).duration(400).playOn(card_challenge);
        }
    }

    public void openChallenge(final AdapterView holder, final Challenge challenge, final Player player){
        final String state = challenge.getState();
        holder.card_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!challenge.isRead()) {
                    challenge.setRead(true);
                    isReadChallenge(holder, challenge);
                    openActivityNotification(challenge.getChallenge_id(), player.getUser_id());
                } else {
                    switch (state){
                        case "enviado":
                            openActivityNotification(challenge.getChallenge_id(), player.getUser_id());
                            break;
                        case "recibido":
                            openActivityNotification(challenge.getChallenge_id(), player.getUser_id());
                            break;
                        case "en espera":
                            openActivityNotification(challenge.getChallenge_id(), player.getUser_id());
                            break;
                        case "aceptado":
                            openChatRoom(challenge.getChallenge_id(), player.getUser_id());
                            break;
                        case "validando":
                            openChatRoom(challenge.getChallenge_id(), player.getUser_id());
                            break;
                        case "reportado":
                            toast.toastInfo("El reto fue reportado, acercate a la opción [Retos Reportados] " +
                                    "en el menu para resolver el marcador");
                            break;
                    }
                }
            }
        });
        holder.card_challenge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(state.equalsIgnoreCase("rechazado") || state.equalsIgnoreCase("cancelado") ||
                        state.equalsIgnoreCase("expirado") || state.equalsIgnoreCase("fallido") ||
                        state.equalsIgnoreCase("terminado")) {
                    dialogDelete(challenge.getChallenge_id());
                }
                return true;
            }
        });
    }

//region Funciones para la eliminacion de un reto

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
        int user_id = userController.show().getUser_id();
        dialog.dialogProgress("Eliminando reto...");
        String url = context.getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.deleteChallenge(token, challenge_id, user_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if(success) {
                    String message = jsonObject.get("message").getAsString();
                    Challenge challenge = challengeController.show(challenge_id);
                    challengeController.delete(challenge);
                    actionReceivedChallenge();
                    toast.toastSuccess(message);
                }
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

    public void actionReceivedChallenge(){
        AppCompatActivity activity = new AppCompatActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.frame_challenges_state);
        StationBus.getBus().post(new MessageBusChallenge(true));
    }

//endregion

//region LLamada a las activities de funcion de retos

    public void openActivityNotification(int challenge_id, int player_id){
        Intent intent = new Intent(context, BaseFrames.class);
        intent.putExtra("from", "notifications");
        intent.putExtra("challenge_id", String.valueOf(challenge_id));
        intent.putExtra("player_id", String.valueOf(player_id));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void openChatRoom(int challenge_id, int player_id){
        Intent intent = new Intent(context, ChatRoom.class);
        intent.putExtra("challenge_id", String.valueOf(challenge_id));
        intent.putExtra("player_id", String.valueOf(player_id));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

//endregion

//region Busqueda del rival del reto

    public Player findPlayerNotNull(int player_one, int player_two){
        PlayerController playerController = new PlayerController(context);
        Player player = playerController.find(player_one);
        if(player == null) {
            player = playerController.find(player_two);
        }
        return player;
    }

//endregion

    public void loadAvatars(CircleImageView avatar_1, CircleImageView avatar_2, Player player){
        Picasso.with(context)
                .load(userController.show().getThumbnail())
                .fit()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerCrop()
                .into(avatar_1);
        Picasso.with(context)
                .load(player.getThumbnail())
                .fit()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerCrop()
                .into(avatar_2);
    }

    public void clickProfilePlayer(CircleImageView avatar_2, final Player player){
        avatar_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class)
                        .putExtra("tab_select", 0)
                        .putExtra("user_id", player.getUser_id())
                        .putExtra("principal", false)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void setUsernames(TextView lbl_username_1, TextView lbl_username_2, String username_player_two){
        String username_player_one = userController.show().getUsername();
        Typeface bebas_bold = Typeface.createFromAsset(
                context.getAssets(), "fonts/bebas_neue_bold.ttf"
        );
        lbl_username_1.setText(username_player_one);
        lbl_username_1.setTypeface(bebas_bold);
        lbl_username_2.setText(username_player_two);
        lbl_username_2.setTypeface(bebas_bold);
    }

    public void setLabelsChallenge(AdapterView holder, Challenge challenge){
        String console = consoleController.show(challenge.getConsole_id()).getName();
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        holder.lbl_level_1.setText(console);
        holder.lbl_level_2.setText(console);
        holder.lbl_read_challenge.setText("¡nuevo!");
        holder.lbl_state_challenge.setText(challenge.getState());
        if(challenge.getState().equals("enviado")){
            if(challenge.getPlayer_one() != userController.show().getUser_id()) {
                holder.lbl_state_challenge.setText("Recibido");
            }
        }
        holder.lbl_level_1.setTypeface(bebas_regular);
        holder.lbl_level_2.setTypeface(bebas_regular);
        holder.lbl_read_challenge.setTypeface(bebas_regular);
        holder.lbl_state_challenge.setTypeface(bebas_regular);
    }

    public void setGames(ImageView image_game, int game_id){
        String game = gameController.show(game_id).getThumbnail();
        Picasso.with(context)
                .load(game)
                .placeholder(R.drawable.pes16_mini)
                .error(R.drawable.avatar_default)
                .fit()
                .into(image_game);
    }

    public void setAmountValue(TextView lbl_amount, float amount){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        String value = String.valueOf(Math.round(amount));
        lbl_amount.setText("$ " + value + " COP");
        lbl_amount.setTypeface(bebas_regular);
    }

    public void isReadChallenge(AdapterView holder, Challenge challenge){
        boolean read_challenge = challenge.isRead();
        if(read_challenge){
            holder.lbl_read_challenge.setVisibility(View.GONE);
        } else {
            holder.lbl_read_challenge.setVisibility(View.VISIBLE);
            holder.lbl_state_challenge.setVisibility(View.GONE);
        }
    }

    public void haveScore(AdapterView holder, Challenge challenge){
        if(challenge.getState().equalsIgnoreCase("terminado")) {
            holder.lbl_score_1.setVisibility(View.VISIBLE);
            holder.lbl_score_2.setVisibility(View.VISIBLE);
            if(userController.show().getUser_id() == challenge.getPlayer_one()) {
                holder.lbl_score_1.setText(String.valueOf(challenge.getScore_player_one()));
                holder.lbl_score_2.setText(String.valueOf(challenge.getScore_player_two()));
            } else {
                holder.lbl_score_1.setText(String.valueOf(challenge.getScore_player_two()));
                holder.lbl_score_2.setText(String.valueOf(challenge.getScore_player_one()));
            }
        } else {
            holder.lbl_score_1.setVisibility(View.GONE);
            holder.lbl_score_2.setVisibility(View.GONE);
        }
    }

    public void setStateChallenge(AdapterView holder, String state_challenge) {
        switch (state_challenge) {
            case "enviado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.divider));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.divider));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.divider));
                break;
            case "en espera":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.icons));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.icons));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.icons));
                break;
            case "reportado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.iconsAccent));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.iconsAccent));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.iconsAccent));
                break;
            case "cancelado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_errors));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_errors));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_errors));
                break;
            case "fallido":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_errors));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_errors));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_errors));
                break;
            case "aceptado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_success));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_success));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_success));
                break;
            case "terminado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_info_light));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_info_light));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_info_light));
                break;
            case "validando":
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_info_light));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_info_light));
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_info_light));
                break;
            case "expirado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_warnings));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_warnings));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_warnings));
                break;
            case "rechazado":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_warnings));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_warnings));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_warnings));
                break;
            case "vencido":
                holder.lbl_state_challenge.setTextColor(ContextCompat.getColor(context, R.color.color_alerts));
                holder.avatar_player_1.setBorderColor(ContextCompat.getColor(context, R.color.color_alerts));
                holder.avatar_player_2.setBorderColor(ContextCompat.getColor(context, R.color.color_alerts));
                break;
        }
        holder.lbl_state_challenge.setVisibility(View.VISIBLE);
    }

}
