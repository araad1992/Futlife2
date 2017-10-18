package com.ideamosweb.futlife.views;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Adapters.RowRecyclerMessageAdapter;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.MessageController;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.ReportController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.Message;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.Report;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChat;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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

public class ChatRoom extends AppCompatActivity {

    private Context context;
    private UserController userController;
    private PlayerController playerController;
    private ChallengeController challengeController;
    private RechargeController rechargeController;
    private ReportController reportController;
    private MessageController messageController;
    private PreferenceController preferenceController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;
    private Player player;
    private Challenge challenge;
    private RowRecyclerMessageAdapter adapter;
    private List<Message> messages;
    private Typeface bebas_bold;
    private Typeface bebas_regular;

    private CountDownTimer countDownTimer;

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.avatar_player)CircleImageView avatar_player;
    @Bind(R.id.lbl_amount_bet)TextView lbl_amount_bet;
    @Bind(R.id.lbl_username_player)TextView lbl_username_player;
    @Bind(R.id.lbl_player_id)TextView lbl_player_id;
    @Bind(R.id.txt_message_send)EditText txt_message_send;
    @Bind(R.id.recycler_chat)RecyclerView recycler_chat;
    @Bind(R.id.layout_chat)RelativeLayout layout_chat;
    @Bind(R.id.progress_bar)ProgressBar progress_bar;
    @Bind(R.id.lbl_back_timer)TextView lbl_back_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
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

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        countDownTimer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackTimer();
    }

    @Subscribe
    public void recievedMessage(MessageBusChat messageBusChat){
        boolean refresh = messageBusChat.isRefresh();
        int type = messageBusChat.getType();
        int notification_id = messageBusChat.getNotification_id();
        if(refresh) {
            if(type == 1) {
                setupRecycler();
                clearNotification(notification_id);
            } else if(type == 2) {
                challenge = challengeController.show(challenge.getChallenge_id());
                if(challenge.getState().equalsIgnoreCase("validando")) {
                    dialogScoreNotification();
                    clearNotification(notification_id);
                }
            } else if(type == 3) {
                challenge = challengeController.show(challenge.getChallenge_id());
                leaveRivalScore();
                clearNotification(notification_id);
            }
        }
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        playerController = new PlayerController(context);
        challengeController = new ChallengeController(context);
        reportController = new ReportController(context);
        rechargeController = new RechargeController(context);
        messageController = new MessageController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);
        bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        getExtras();
        stateChallenge();
        setupToolbar();
        setupRecycler();
        setScrollbarChat();
    }

    public void getExtras(){
        Bundle extras = getIntent().getExtras();
        Challenge challenge = (Challenge)extras.getSerializable("challenge");
        if(challenge.getState().equalsIgnoreCase("validando")){
            dialogScoreNotification();
        }
        updatePlayerIdRival();
    }

    public void stateChallenge(){
        if(challenge.getState().equalsIgnoreCase("Terminado")) {
            finish();
        } else if(challenge.getState().equalsIgnoreCase("reportado")) {
            finish();
        }
    }

    public void setupToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_score:
                dialogScore();
                break;
            case R.id.action_cancel:
                dialogLeaveChallenge(challenge);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @OnClick(R.id.fab)
    public void setupFab(View view){
        String message_text = txt_message_send.getText().toString().trim();
        if(message_text.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(700).playOn(view);
        } else {
            createMessage(message_text);
            YoYo.with(Techniques.Swing).duration(700).playOn(view);
        }
    }

    public void setScrollbarChat() {
        recycler_chat.scrollToPosition(adapter.getItemCount() - 1);
    }

    public void updatePlayerIdRival(){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.showPlayerId(token, player.getUser_id(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    JsonArray array = jsonObject.get("data").getAsJsonArray();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject json = array.get(i).getAsJsonObject();
                        int preference_id = json.get("id").getAsInt();
                        String player_id = json.get("player_id").getAsString();
                        ConsolePreference console = preferenceController.find(preference_id);
                        if(console == null) {
                            updatePreferencesRival();
                        } else {
                            System.out.println("preferences: " + console.toString());
                            console.setPlayer_id(player_id);
                            preferenceController.update(console);
                            setupInfoChat();
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                layout_chat.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
                toast.toastWarning("Imposible conectar a red.");
                try {
                    Log.d("ChatRoom(updatePreferencesRival)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(updatePreferencesRival)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void updatePreferencesRival(){
        final User user = userController.show();
        String token = "Bearer " + user.getToken();
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.showPreference(token, player.getUser_id(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    JsonArray preferences = jsonObject.getAsJsonArray("preferences");
                    savePreferencesPlayer(preferences);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                layout_chat.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
                toast.toastWarning("Imposible conectar a red.");
                try {
                    Log.d("ChatRoom(updatePreferencesRival)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(updatePreferencesRival)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void savePreferencesPlayer(JsonArray array){
        int user_id = player.getUser_id();
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
                        System.out.println("consola: " + console.toString());
                        JsonArray array_games = preference.getAsJsonArray("games");
                        for (int j = 0; j < array_games.size(); j++) {
                            JsonObject json_game = array_games.get(j).getAsJsonObject();
                            GamePreference game = new Gson().fromJson(json_game, GamePreference.class);
                            game.setUser_id(user_id);
                            game.setConsole_id(console.getConsole_id());
                            if(preferenceController.create(game)) {
                                System.out.println("juegos:" + game.toString());
                            }
                        }
                    }
                }
                setupInfoChat();
            }
        } catch (JsonIOException e){
            Log.e("ChatRoom(savePreferencesPlayer)", "Error ex: " + e.getMessage());
        }
    }

    public void setupInfoChat(){
        List<ConsolePreference> consoles = preferenceController.consoles(player.getUser_id());
        for (int i = 0; i < consoles.size(); i++) {
            System.out.println("preference console: " + consoles.get(i).toString());
        }
        progress_bar.setVisibility(View.GONE);
        YoYo.with(Techniques.Pulse)
                .duration(700)
                .playOn(layout_chat);
        layout_chat.setVisibility(View.VISIBLE);
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        setupAvatarPlayer();
        String value = String.valueOf(Math.round(challenge.getInitial_value()));
        lbl_username_player.setText(player.getUsername());
        lbl_amount_bet.setText("$ " + value + " COP");
        System.out.println("Player ID: " + player.getUser_id() + "console_id: " + "; " + challenge.getConsole_id());
        lbl_player_id.setText("Player ID: " + preferenceController.playerIdConsolePreference(player.getUser_id(), challenge.getConsole_id()));
        lbl_amount_bet.setTypeface(bebas_bold);
        lbl_username_player.setTypeface(bebas_regular);
        lbl_player_id.setTypeface(bebas_regular);
        lbl_back_timer.setTypeface(bebas_regular);
        setBackTimer();
    }

    public void setBackTimer(){
        long timer = utils.getTimeInMilliSeconds(challenge.getDeadline());
        countDownTimer = new CountDownTimer(timer, 1000) {
            public void onTick(long milliseconds) {
                int hours = (int)((milliseconds / (1000 * 60 * 60)) % 24);
                int minutes = (int)((milliseconds / (1000 * 60)) % 60);
                int seconds = (int)((milliseconds / 1000) % 60);
                String deadline = utils.numberConvert(hours) + ":" +
                        utils.numberConvert(minutes) + ":" +
                        utils.numberConvert(seconds);
                lbl_back_timer.setText(deadline);
            }
            public void onFinish() {
                if(utils.compareDates(challenge.getDeadline())) {
                    lbl_back_timer.setText("Vencido");
                    challenge.setState("vencido");
                    challengeController.update(challenge);
                    toast.toastInfo("El tiempo disponible para este reto se ha vencido.");
                    finish();
                } else {
                    lbl_back_timer.setText("Tiempo Extra");
                }
            }
        }.start();
    }

    public void setupAvatarPlayer(){
        Picasso.with(context)
                .load(player.getThumbnail())
                .fit()
                .centerCrop()
                .error(R.drawable.avatar_default)
                .placeholder(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(avatar_player);
    }

    public Message createMessageAdmin() {
        Message message = new Message();
        message.setChallenge_id(challenge.getChallenge_id());
        message.setFrom_user(0);
        message.setTo_user(0);
        message.setCreated_at(utils.createDateNow());
        message.setMessage_text(getString(R.string.text_message_admin));
        message.setActive(true);
        return message;
    }

    public void setupRecycler(){
        messages = messageController.show(userController.show().getUser_id(), player.getUser_id());
        Message message_admin = createMessageAdmin();
        messages.add(0, message_admin);
        for (int i = 0; i < messages.size(); i++) {
            if(messages.get(i).getChallenge_id() != challenge.getChallenge_id()) {
                messages.remove(i);
            }
        }
        adapter = new RowRecyclerMessageAdapter(context, messages);
        LinearLayoutManager layout_manager = new LinearLayoutManager(context);
        layout_manager.setStackFromEnd(true);
        layout_manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_chat.setLayoutManager(layout_manager);
        recycler_chat.setAdapter(adapter);
    }

//Region gestión de los mensajes

    public void createMessage(String message_text){
        Utils utils = new Utils(context);
        Message message = new Message();
        message.setFrom_user(userController.show().getUser_id());
        message.setTo_user(player.getUser_id());
        message.setCreated_at(utils.createDateNow());
        message.setMessage_text(message_text);
        message.setActive(true);
        messages.add(message);
        adapter.notifyDataSetChanged();
        txt_message_send.setText("");
        messageController.create(message);
        setScrollbarChat();
        sendMessage(message);
    }

    public void sendMessage(final Message message){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.sendMessage(token, message.getFrom_user(), message.getTo_user(), challenge.getChallenge_id(), message.getMessage_text(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    JsonObject json_message = jsonObject.getAsJsonObject("message").getAsJsonObject();
                    Message message_chat = new Gson().fromJson(json_message, Message.class);
                    Message new_message = messageController.find(message.getCode());
                    new_message.setCreated_at(message_chat.getCreated_at());
                    new_message.setMessage_id(message_chat.getMessage_id());
                    new_message.setChallenge_id(message_chat.getChallenge_id());
                    messageController.update(new_message);
                    challenge.setUpdated_at(message.getCreated_at());
                    challengeController.update(challenge);
                    setupRecycler();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Error!");
                toast.toastWarning("No se pudo enviar el mensaje");
                try {
                    Log.d("ChatRoom(sendMessage)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(sendMessage)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void clearNotification(int notification_id) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_id);
    }

//endregion

//region Dialog para subir el marcador

    public void dialogScore(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_score, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setlayoutPlayers(view);
        setLabelsDialog(view);
        setElements(view, alertDialog);
        alertDialog.show();
    }

    public void setlayoutPlayers(View view){
        LinearLayout layout_player_1 = (LinearLayout)view.findViewById(R.id.layout_player_1);
        LinearLayout layout_player_2 = (LinearLayout)view.findViewById(R.id.layout_player_2);
        CircleImageView player_1 = (CircleImageView)view.findViewById(R.id.avatar_player_1);
        CircleImageView player_2 = (CircleImageView)view.findViewById(R.id.avatar_player_2);
        setAvatarPlayers(player_1, player_2);

        TextView lbl_player_1 = (TextView)view.findViewById(R.id.lbl_player_1);
        lbl_player_1.setText(userController.show().getUsername());
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
                .load(userController.show().getThumbnail())
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
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        lbl_title_dialog.setTypeface(bebas_bold);
    }

    public void setElements(View view, final AlertDialog alertDialog){
        final EditText txt_your_score = (EditText)view.findViewById(R.id.txt_your_score);
        final EditText txt_rival_score = (EditText)view.findViewById(R.id.txt_rival_score);
        txt_your_score.setTypeface(bebas_bold);
        txt_rival_score.setTypeface(bebas_bold);
        //Botones del dialog
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_challenge);
        Button but_done = (Button)view.findViewById(R.id.but_done_challenge);
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
                String your_score = txt_your_score.getText().toString();
                String rival_score = txt_rival_score.getText().toString();
                if(String.valueOf(your_score).isEmpty() || String.valueOf(rival_score).isEmpty()){
                    toast.toastWarning("Debes agregar un marcador antes de enviar.");
                } else if(your_score.equalsIgnoreCase(rival_score)) {
                    toast.toastWarning("El marcador no puede ser de empate.");
                } else {
                    setScore(Integer.parseInt(your_score), Integer.parseInt(rival_score));
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void setScore(int your_score, int rival_score) {
        int user_id = userController.show().getUser_id();
        int rival_id = player.getUser_id();
        if(user_id == challenge.getPlayer_one()) {
            addScore(user_id, rival_id, your_score, rival_score);
        } else if(rival_id == challenge.getPlayer_one()) {
            addScore(user_id, rival_id, rival_score, your_score);
        } else {
            System.out.println("No se que paso aqui.");
        }
    }

//endregion

//region Dialog para confirmar marcador

    public void dialogScoreNotification(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_score_notification, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setInfoPlayers(view);
        setLabelsDialogScore(view);
        setElementsNotification(view, alertDialog);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void setLabelsDialogScore(View view){
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        lbl_title_dialog.setTypeface(bebas_bold);
    }

    public void setInfoPlayers(View view){
        CircleImageView player_1 = (CircleImageView)view.findViewById(R.id.avatar_player_1);
        CircleImageView player_2 = (CircleImageView)view.findViewById(R.id.avatar_player_2);
        setAvatarPlayersNotification(player_1, player_2);

        TextView lbl_score_final = (TextView)view.findViewById(R.id.lbl_score_final);
        lbl_score_final.setText(challenge.getScore_player_one() + " - " + challenge.getScore_player_two());
        lbl_score_final.setTypeface(bebas_regular);

        if(challenge.getPlayer_one() == userController.show().getUser_id()) {

            TextView lbl_player_1 = (TextView)view.findViewById(R.id.lbl_player_1);
            lbl_player_1.setText(userController.show().getUsername());
            lbl_player_1.setTypeface(bebas_regular);

            TextView lbl_player_2 = (TextView)view.findViewById(R.id.lbl_player_2);
            lbl_player_2.setText(player.getUsername());
            lbl_player_2.setTypeface(bebas_regular);

        } else if(challenge.getPlayer_one() == player.getUser_id()) {

            TextView lbl_player_1 = (TextView)view.findViewById(R.id.lbl_player_1);
            lbl_player_1.setText(player.getUsername());
            lbl_player_1.setTypeface(bebas_regular);

            TextView lbl_player_2 = (TextView)view.findViewById(R.id.lbl_player_1);
            lbl_player_2.setText(userController.show().getUsername());
            lbl_player_2.setTypeface(bebas_regular);

        }
    }

    public void setAvatarPlayersNotification(CircleImageView player_1, CircleImageView player_2){
        String thumbnail_one = "";
        String thumbnail_two = "";

        if(challenge.getPlayer_one() == userController.show().getUser_id()) {
            thumbnail_one = userController.show().getThumbnail();
            thumbnail_two = player.getThumbnail();
        } else if(challenge.getPlayer_one() == player.getUser_id()) {
            thumbnail_two = userController.show().getThumbnail();
            thumbnail_one = player.getThumbnail();
        }
        Picasso.with(context)
                .load(thumbnail_one)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(player_1);
        Picasso.with(context)
                .load(thumbnail_two)
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(player_2);
    }

    public void setElementsNotification(View view, final AlertDialog alertDialog){
        //Botones del dialog
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_score_not);
        Button but_done = (Button)view.findViewById(R.id.but_done_score_not);
        but_cancel.setTypeface(bebas_regular);
        but_done.setTypeface(bebas_regular);
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmScore(challenge.getChallenge_id(), userController.show().getUser_id(), "apelar");
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int winner_id = 0;
                if(challenge.getScore_player_one() > challenge.getScore_player_two()) {
                    winner_id = challenge.getPlayer_one();
                } else if(challenge.getScore_player_two() > challenge.getScore_player_one()) {
                    winner_id = challenge.getPlayer_two();
                }
                confirmScore(challenge.getChallenge_id(), winner_id, "confirmar");
                alertDialog.dismiss();
            }
        });
    }

//endregion

//region Dialog para informar el abandono de un rival

    public void dialogLeaveChallenge(Challenge challenge){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_leave, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsLeaveChallenge(view, alertDialog, challenge);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void setElementsLeaveChallenge(View view, final AlertDialog alertDialog, final Challenge challenge){
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        TextView lbl_leave_message = (TextView)view.findViewById(R.id.lbl_leave_challenge_message);
        Button but_cancel_leave = (Button)view.findViewById(R.id.but_cancel_leave_challenge);
        Button but_done_leave = (Button)view.findViewById(R.id.but_done_leave_challenge);
        lbl_title_dialog.setTypeface(bebas_bold);
        lbl_leave_message.setTypeface(bebas_regular);
        but_done_leave.setTypeface(bebas_regular);
        but_cancel_leave.setTypeface(bebas_regular);
        but_done_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int user_id = userController.show().getUser_id();
                alertDialog.dismiss();
                challengeLeave(challenge.getChallenge_id(), user_id);
            }
        });
        but_cancel_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void challengeLeave(int challenge_id, int user_id){
        dialog.dialogProgress("Abandonando reto...");
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.challengeLeave(token, challenge_id, user_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                String message = jsonObject.get("message").getAsString();
                if(success) {
                    //JsonObject json_challenge = jsonObject.get("challenge").getAsJsonObject();
                    //Challenge challenge_update = new Gson().fromJson(json_challenge, Challenge.class);
                    //challenge_update.setCode(challenge.getCode());
                    //challengeController.update(challenge_update);
                    //toast.toastSuccess(message);
                    finish();
                } else {
                    toast.toastWarning("Error al enviar respuesta");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                System.out.println("Error!");
                try {
                    Log.d("ChatRoom(updateScore)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(updateScore)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void leaveRivalScore(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_rival_leave, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElementsLeave(view, alertDialog);
        alertDialog.show();
    }

    public void setElementsLeave(View view, final AlertDialog alertDialog){
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        TextView lbl_leave_message = (TextView)view.findViewById(R.id.lbl_leave_message);
        Button but_done_leave = (Button)view.findViewById(R.id.but_done_leave);
        lbl_title_dialog.setTypeface(bebas_bold);
        lbl_leave_message.setTypeface(bebas_regular);
        but_done_leave.setTypeface(bebas_regular);
        but_done_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
    }

//endregion

//region Funciones para subir y confirmar marcador

    public void addScore(int from_user, int to_user, final int score_player_one, final int score_player_two){
        dialog.dialogProgress("Enviando marcador...");
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.addScore(token, challenge.getChallenge_id(), from_user, to_user, score_player_one, score_player_two, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if(success) {
                    String message = jsonObject.get("message").getAsString();
                    challenge.setState("terminado");
                    challenge.setScore_player_one(score_player_one);
                    challenge.setScore_player_two(score_player_two);
                    challengeController.update(challenge);
                    toast.toastSuccess(message);
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("ChatRoom(uploadScore)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(uploadScore)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void confirmScore(int challenge_id, int winner_id, String confirm){
        dialog.dialogProgress("Enviando respuesta...");
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.confirmScore(token, challenge_id, winner_id, confirm, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                String message = jsonObject.get("message").getAsString();
                if(success) {
                    challenge.setState("terminado");
                    amWinner(jsonObject);
                    challengeController.update(challenge);
                    toast.toastSuccess(message);
                    finish();
                } else {
                    challenge.setState("reportado");
                    challengeController.update(challenge);
                    challengeReported(jsonObject, message);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                System.out.println("Error!");
                try {
                    Log.d("ChatRoom(updateScore)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(updateScore)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void amWinner(JsonObject jsonObject){
        int winner_id = jsonObject.get("winner_id").getAsInt();
        if(userController.show().getUser_id() == winner_id) {
            float new_value = jsonObject.get("new_value").getAsInt();
            Balance balance = rechargeController.get();
            balance.setValue(new_value);
            rechargeController.update(balance);
        }
    }

    public void challengeReported(JsonObject jsonObject, String message){
        JsonObject json_report = jsonObject.get("report").getAsJsonObject();
        Report report = new Gson().fromJson(json_report, Report.class);
        report.setState("Pendiente");
        reportController.create(report);
        toast.toastInfo(message);
        Intent intent = new Intent(context, Reported.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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

}
