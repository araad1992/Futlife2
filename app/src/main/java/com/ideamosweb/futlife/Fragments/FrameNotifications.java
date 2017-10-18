package com.ideamosweb.futlife.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.MessageBusUpdateTimeline;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.ideamosweb.futlife.views.ChatRoom;
import com.ideamosweb.futlife.views.Payments;
import com.ideamosweb.futlife.views.Profile;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 28/03/17.
 * Función:
 */
public class FrameNotifications extends Fragment {

    private Context context;
    private UserController userController;
    private RechargeController rechargeController;
    private PreferenceController preferenceController;
    private Challenge challenge;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;

    //Elementos
    @Bind(R.id.avatar_player)CircleImageView circle_avatar_player;
    @Bind(R.id.lbl_username_player)TextView lbl_username_player;
    @Bind(R.id.lbl_fullname_player)TextView lbl_fullname_player;
    @Bind(R.id.lbl_amount)TextView lbl_amount;
    @Bind(R.id.lbl_gain)TextView lbl_gain;
    @Bind(R.id.layout_wait)LinearLayout layout_wait;
    @Bind(R.id.layout_done)LinearLayout layout_done;
    @Bind(R.id.lbl_cancel)TextView lbl_cancel;
    @Bind(R.id.lbl_wait)TextView lbl_wait;
    @Bind(R.id.lbl_done)TextView lbl_done;

    public static FrameNotifications newInstance() {
        return new FrameNotifications();
    }

    public FrameNotifications() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_notification_challenge, container, false);
        ButterKnife.bind(this, view);
        readBundles(getArguments());
        setupFrame();
        return view;
    }

    public void readBundles(Bundle bundle){
        if (bundle != null) {
            challenge = (Challenge)bundle.getSerializable("challenge");
            dialog = new MaterialDialog(getContext());
            toast = new ToastMessages(getContext());
            utils = new Utils(getContext());
        }
    }

    public void stateChallenge(){
        if(challenge.getState().equalsIgnoreCase("Terminado")) {
            getActivity().finish();
        } else if(challenge.getState().equalsIgnoreCase("aceptado")) {
            getActivity().finish();
        } else if(challenge.getState().equalsIgnoreCase("rechazado")) {
            getActivity().finish();
        } else if(challenge.getState().equalsIgnoreCase("cancelado")) {
            getActivity().finish();
        } else if(challenge.getState().equalsIgnoreCase("validando")) {
            getActivity().finish();
        } else if(challenge.getState().equalsIgnoreCase("reportado")) {
            getActivity().finish();
        }
    }

    public void setupButtons() {
        if(challenge.getPlayer_one() == userController.show().getUser_id()) {
            if(challenge.getState().equals("enviado") || challenge.getState().equals("en espera")) {
                layout_wait.setVisibility(View.GONE);
                layout_done.setVisibility(View.GONE);
                lbl_cancel.setText("Cancelar");
            }
        } else {
            if(challenge.getState().equals("en espera")) {
                layout_wait.setVisibility(View.GONE);
                lbl_cancel.setText("Cancelar");
            }
        }
    }

    public void setupFrame(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        rechargeController = new RechargeController(context);
        preferenceController = new PreferenceController(context);
        stateChallenge();
        setReadChallenge();
        setTypeFontLabels();
        avatarPlayer();
        setInfoPlayer();
        setupButtons();
    }

    public void updateFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.frame_challenges_state);
        StationBus.getBus().post(new MessageBusChallenge(true));
        StationBus.getBus().post(new MessageBusUpdateTimeline(true, "state_challenge"));
    }

    public void setReadChallenge(){
        if(!challenge.isRead()) {
            challenge.setRead(true);
            System.out.println(challenge);
            updateFragment();
        }
        isRead(challenge.getChallenge_id());
    }

    public void setTypeFontLabels(){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_username_player.setTypeface(bebas_bold);
        lbl_fullname_player.setTypeface(bebas_regular);
        lbl_amount.setTypeface(bebas_bold);
        lbl_gain.setTypeface(bebas_bold);
        lbl_cancel.setTypeface(bebas_regular);
        lbl_wait.setTypeface(bebas_regular);
        lbl_done.setTypeface(bebas_regular);
    }

    public void avatarPlayer(){
        String url_player;
        User user = userController.show();
        if(user.getUser_id() == challenge.getPlayer_one()) {
            url_player = challenge.getAvatar_two();
        } else {
            url_player = challenge.getAvatar_one();
        }
        Picasso.with(context).load(url_player)
                .fit()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .centerCrop()
                .into(circle_avatar_player);
        clickCardDetails(circle_avatar_player);
    }

    public void clickCardDetails(CircleImageView avatar){
        final int player_id;
        User user = userController.show();
        if(user.getUser_id() == challenge.getPlayer_one()) {
            player_id = challenge.getPlayer_two();
        } else {
            player_id = challenge.getPlayer_one();
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(getActivity(), Profile.class);
                profile.putExtra("tab_select", 0);
                profile.putExtra("user_id", player_id);
                profile.putExtra("principal", false);
                startActivity(profile);
            }
        });
    }

    public void setInfoPlayer(){
        User user = userController.show();
        String value = String.valueOf(Math.round(challenge.getInitial_value()));
        String gain = String.valueOf(Math.round(challenge.getAmount_bet()));
        String username;
        String fullname;
        if(user.getUser_id() == challenge.getPlayer_one()) {
            username = challenge.getUsername_two();
            fullname = challenge.getName_two();
        } else {
            fullname = challenge.getName_one();
            username = challenge.getUsername_one();
        }
        lbl_username_player.setText(username);
        lbl_fullname_player.setText(fullname);
        lbl_amount.setText("Apuesta: $" + value + " COP");
        lbl_gain.setText("Ganancia: $" + gain + " COP");
    }

    @OnClick(R.id.but_cancel)
    public void clickButCancel(){
        boolean connection = utils.isOnline();
        if (connection) {
            if(lbl_cancel.getText().equals("Rechazar")) {
                launchResponse("rechazado");
            } else if(lbl_cancel.getText().equals("Cancelar")) {
                cancelChallenge("cancelado");
            }
        } else {
            dialog.dialogErrors("Error de conexión", "No se pudo detectar una conexión disponible a internet.");
        }
    }

    @OnClick(R.id.but_wait)
    public void clickButWait(){
        boolean connection = utils.isOnline();
        if (connection) {
            launchResponse("en espera");
        } else {
            dialog.dialogErrors("Error de conexión", "No se pudo detectar una conexión estable a internet.");
        }
    }

    @OnClick(R.id.but_done)
    public void clickButDone(){
        boolean connection = utils.isOnline();
        if (connection) {
            if(rechargeController.get() != null) {
                int balance = Math.round(rechargeController.get().getValue());
                String state = rechargeController.get().getState();
                if(state.equalsIgnoreCase("Aceptada")) {
                    if(balance > Math.round(challenge.getInitial_value())) {
                        if(validatePlayerId()) {
                            launchResponse("aceptado");
                        } else {
                            dialogNoPlayerId();
                        }
                    } else {
                        dialogNoCredit();
                    }
                } else {
                    dialog.dialogInfo("Esperando Confirmación","Estamos procesando tu solicitud de recarga, en cuanto este lista te notificaremos");
                }
            } else {
                dialogNoCredit();
            }
        } else {
            dialog.dialogErrors("Error de conexión", "No se pudo detectar una conexión estable a internet.");
        }
    }

//region Dialogs flotantes

    public void dialogNoCredit(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_no_credit, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsNoCredit(view, alertDialog);
        alertDialog.show();
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

    public void dialogNoPlayerId(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void isRead(int challenge_id){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.challengeIsRead(token, challenge_id, true, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                System.out.println("is read" + success);
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    Log.d("FrameNotifications(isRead)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("FrameNotifications(isRead)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void openChatRoom(String state){
        if(state.equalsIgnoreCase("aceptado")) {
            Intent chat_room = new Intent(getActivity(), ChatRoom.class);
            chat_room.putExtra("challenge", challenge);
            startActivity(chat_room);
        }
    }

    public void launchResponse(final String state){
        User user = userController.show();
        int player_id;
        if(user.getUser_id() == challenge.getPlayer_one()) {
            player_id = challenge.getPlayer_two();
        } else {
            player_id = challenge.getPlayer_one();
        }
        String token = "Bearer " + user.getToken();
        int balance_id = rechargeController.get().getBalance_id();
        dialog.dialogProgress("Respondiendo...");
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.responseChallenge(token, challenge.getChallenge_id(), player_id,
                user.getUser_id(), challenge.getAmount_bet(), state, balance_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    String message = jsonObject.get("message").getAsString();
                    String deadline = jsonObject.get("challenge").getAsJsonObject().get("deadline").getAsString();
                    String updated_at = jsonObject.get("challenge").getAsJsonObject().get("updated_at").getAsString();
                    toast.toastSuccess(message);
                    challenge.setState(state);
                    challenge.setDeadline(deadline);
                    challenge.setUpdated_at(updated_at);
                    updateValueBalance(challenge.getInitial_value(), state);
                    updateFragment();
                    openChatRoom(state);
                }
                dialog.cancelProgress();
                getActivity().finish();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("FrameNotifications(launchResponse)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("FrameNotifications(launchResponse)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void updateValueBalance(float initial_value, String state){
        if(state.equalsIgnoreCase("aceptado")) {
            Balance balance = rechargeController.get();
            float actual_value = balance.getValue();
            float new_value = actual_value - initial_value;
            balance.setValue(new_value);
            rechargeController.update(balance);
        }
    }

    public void cancelChallenge(final String state){
        User user = userController.show();
        int player_id;
        if(user.getUser_id() == challenge.getPlayer_one()) {
            player_id = challenge.getPlayer_two();
        } else {
            player_id = challenge.getPlayer_one();
        }
        String token = "Bearer " + user.getToken();
        int balance_id = rechargeController.get().getBalance_id();
        dialog.dialogProgress("Cancelando...");
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.cancelChallenge(token, challenge.getChallenge_id(), balance_id, state,
                user.getUser_id(), player_id, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if(success) {
                            String message = jsonObject.get("message").getAsString();
                            float new_value = jsonObject.get("new_balance").getAsFloat();
                            toast.toastSuccess(message);
                            challenge.setState(state);
                            Balance balance = rechargeController.get();
                            balance.setValue(new_value);
                            rechargeController.update(balance);
                        } else {
                            String message = jsonObject.get("message").getAsString();
                            String new_state = jsonObject.get("state").getAsString();
                            challenge.setState(new_state);
                            updateFragment();
                            openChatRoom(new_state);
                            toast.toastInfo(message);

                        }
                        dialog.cancelProgress();
                        getActivity().finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.cancelProgress();
                        errorsRequest(error);
                        try {
                            Log.d("FrameNotifications(cancelChallenge)", "Errors body: " + error.getMessage());
                        } catch (Exception ex) {
                            Log.e("FrameNotifications(cancelChallenge)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
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

}
