package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
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
 * Creado por Deimer Villa on 29/06/17.
 * Función:
 */
public class RowRecyclerExpiredAdapter extends RecyclerView.Adapter<RowRecyclerExpiredAdapter.AdapterView> {

    private Context context;
    private UserController userController;
    private PlayerController playerController;
    private ChallengeController challengeController;
    private RechargeController rechargeController;
    private ToastMessages toast;
    private Utils utils;
    private List<Challenge> expireds = new ArrayList<>();
    private Typeface bebas_bold;
    private Typeface bebas_regular;

    public RowRecyclerExpiredAdapter(Context context, List<Challenge> expireds) {
        this.context = context;
        this.expireds = expireds;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_expired, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public int getItemCount() {
        return expireds.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.img_avatar_user)CircleImageView img_avatar_user;
        @Bind(R.id.lbl_username)TextView lbl_username;
        @Bind(R.id.lbl_date_reported)TextView lbl_date_reported;
        @Bind(R.id.lbl_amount_bet)TextView lbl_amount_bet;
        @Bind(R.id.but_sync_up)ImageButton but_sync_up;
        @Bind(R.id.load_sync_up)AVLoadingIndicatorView load_sync_up;
        @Bind(R.id.lbl_state_challenge)TextView lbl_state_challenge;
        @Bind(R.id.view_separe)View view_separe;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        userController = new UserController(context);
        playerController = new PlayerController(context);
        challengeController = new ChallengeController(context);
        rechargeController = new RechargeController(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);
        bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        Challenge challenge = expireds.get(position);
        setupAvatar(challenge, holder);
        setupCircleView(holder, challenge.getState());
        setupLabels(challenge, holder);
        isLast(holder, position);
        clickCardExpired(challenge, holder);
        setupLabelState(holder, challenge.getState());
    }

    public void setupAvatar(Challenge challenge, AdapterView holder){
        Player player = setupPlayerRival(challenge);
        if(player != null) {
            holder.lbl_username.setText(player.getUsername());
            holder.lbl_username.setTypeface(bebas_bold);
            loadAvatarRival(holder.img_avatar_user, player.getThumbnail());

        } else {
            loadAvatarRival(holder.img_avatar_user, "");
        }
    }

    public void setupLabels(Challenge challenge, AdapterView holder){
        String date = utils.setDatetimeFormat(challenge.getDeadline());
        holder.lbl_date_reported.setText("Expiro el " + date);
        holder.lbl_amount_bet.setText("$" + Math.round(challenge.getInitial_value()));
        holder.lbl_date_reported.setTypeface(bebas_regular);
        holder.lbl_amount_bet.setTypeface(bebas_regular);
        holder.lbl_state_challenge.setTypeface(bebas_regular);
    }

    public Player setupPlayerRival(Challenge challenge){
        Player player = null;
        User user = userController.show();
        if(challenge.getPlayer_one() != user.getUser_id()) {
            player = playerController.find(challenge.getPlayer_one());
        } else if(challenge.getPlayer_two() != user.getUser_id()) {
            player = playerController.find(challenge.getPlayer_two());
        }
        return player;
    }

    public void loadAvatarRival(CircleImageView avatar, String url_thumbnail){
        Picasso.with(context)
                .load(url_thumbnail)
                .fit()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .centerCrop()
                .into(avatar);
    }

    public void setupCircleView(AdapterView holder, String state) {
        if(state.equalsIgnoreCase("vencido")) {
            holder.img_avatar_user.setBorderColor(ContextCompat.getColor(context, R.color.color_alerts));
            holder.lbl_username.setTextColor(ContextCompat.getColor(context, R.color.color_alerts));
        } else if(state.equalsIgnoreCase("expirado")) {
            holder.img_avatar_user.setBorderColor(ContextCompat.getColor(context, R.color.color_warnings));
            holder.lbl_username.setTextColor(ContextCompat.getColor(context, R.color.color_warnings));
        }
    }

    public void isLast(AdapterView holder, int position){
        int last = getItemCount()-1;
        if(position == last) {
            holder.view_separe.setVisibility(View.GONE);
        } else {
            holder.view_separe.setVisibility(View.VISIBLE);
        }
    }

    public void setupLabelState(AdapterView holder, String state) {
        holder.lbl_state_challenge.setText(state);
        System.out.println("Estado del reto: " + state);
        if(state.equalsIgnoreCase("vencido")) {
            holder.but_sync_up.setVisibility(View.VISIBLE);
            holder.load_sync_up.setVisibility(View.GONE);
        } else if(state.equalsIgnoreCase("expirado")) {
            holder.but_sync_up.setVisibility(View.GONE);
            holder.load_sync_up.setVisibility(View.GONE);
        }
    }

    public void clickCardExpired(final Challenge challenge, final AdapterView holder){
        holder.but_sync_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lbl_state_challenge.setText("actualizando");
                holder.but_sync_up.setVisibility(View.GONE);
                holder.load_sync_up.setVisibility(View.VISIBLE);
                expireChallenge(challenge, holder);
            }
        });
    }

//region Reto expirado

    public void expireChallenge(final Challenge challenge, final AdapterView holder){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        String url = context.getString(R.string.url_api);
        String state = "expirado";
        float spare_value = Math.round(challenge.getInitial_value() * 0.90);
        int to_user = setupPlayerRival(challenge).getUser_id();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.expireChallenge(token, challenge.getChallenge_id(), state, spare_value,
                user.getUser_id(), to_user, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        actionSuccess(jsonObject, challenge, holder);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        errorsRequest(error);
                        holder.but_sync_up.setVisibility(View.VISIBLE);
                        holder.load_sync_up.setVisibility(View.GONE);
                        holder.lbl_state_challenge.setText("vencido");
                    }
                });
    }

    public void actionSuccess(JsonObject jsonObject, Challenge challenge, AdapterView holder){
        boolean success = jsonObject.get("success").getAsBoolean();
        float spare_value = Math.round(challenge.getInitial_value() * 0.90);
        if(success) {
            challenge.setState("expirado");
            challengeController.update(challenge);
            holder.lbl_state_challenge.setText("expirado");
            Balance balance = rechargeController.get();
            float new_value = balance.getValue() + spare_value;
            balance.setValue(new_value);
            rechargeController.update(balance);
            toast.toastSuccess("Saldo actualizado $" + balance.getValue());
            setupCircleView(holder, challenge.getState());
        }
        holder.load_sync_up.setVisibility(View.GONE);
    }

    public void errorsRequest(RetrofitError retrofitError){
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            toast.toastError("Error de conexión");
        } else {
            int status = retrofitError.getResponse().getStatus();
            if(status == 400) {
                String error = retrofitError.getBody().toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonErrors = (JsonObject)parser.parse(error);
                String message = jsonErrors.get("message").getAsString();
                toast.toastWarning(message);
            } else {
                String message = retrofitError.getMessage();
                toast.toastError(message);
            }
        }
    }

//endregion

}
