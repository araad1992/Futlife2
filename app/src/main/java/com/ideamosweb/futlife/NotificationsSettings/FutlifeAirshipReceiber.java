package com.ideamosweb.futlife.NotificationsSettings;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.MessageController;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.RechargeController;
import com.ideamosweb.futlife.Controllers.ReportController;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.Message;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.Report;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.MessageBusChat;
import com.ideamosweb.futlife.EventBus.MessageBusPayment;
import com.ideamosweb.futlife.EventBus.MessageBusTimeline;
import com.ideamosweb.futlife.EventBus.MessageBusUpdateTimeline;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.views.BaseFrames;
import com.ideamosweb.futlife.views.ChatRoom;
import com.ideamosweb.futlife.views.Payments;
import com.ideamosweb.futlife.views.Reported;
import com.ideamosweb.futlife.views.Timeline;
import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 13/03/17.
 * Función:
 */
public class FutlifeAirshipReceiber extends AirshipReceiver {

//region Servicios de recoleccion de notificaciones

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        System.out.println(message.toString());
        receivedNotification(context, message.getAlert());
        super.onPushReceived(context, message, notificationPosted);
    }

    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        int notification_id = notificationInfo.getNotificationId();
        String message = notificationInfo.getMessage().getAlert();
        actionReceibedMessage(notification_id, message);
        System.out.println("Notification posted; Alert: " + notificationInfo.getMessage().getAlert() + "; NotificationId: " + notificationInfo.getNotificationId());
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        System.out.println("Notification opened. Alert: " + notificationInfo.getMessage().getAlert() + "; NotificationId: " + notificationInfo.getNotificationId());
        openedNotification(context, notificationInfo.getMessage().getAlert());
        System.out.println(notificationInfo.getNotificationId());
        return true;
    }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context, @NonNull NotificationInfo notificationInfo, @NonNull ActionButtonInfo actionButtonInfo) {
        System.out.println("Notification action button opened. Button ID: " + actionButtonInfo.getButtonId() + "; NotificationId: " + notificationInfo.getNotificationId());
        return false;
    }

    @Override
    protected void onNotificationDismissed(@NonNull Context context, @NonNull NotificationInfo notificationInfo) {
        System.out.println(
                "Notification dismissed. Alert: " + notificationInfo.getMessage().getAlert() +
                "; Notification ID: " + notificationInfo.getNotificationId()
        );
    }

//endregion

//region Funciones de implementacion

    public void receivedNotification(Context context, String message){
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(message);
        String type_notification = json.get("type").getAsString();
        PlayerController playerController = new PlayerController(context);
        ChallengeController challengeController = new ChallengeController(context);
        ReportController reportController = new ReportController(context);
        MessageController messageController = new MessageController(context);
        RechargeController rechargeController = new RechargeController(context);
        Player player;
        Challenge challenge;

        switch (type_notification){
            case "reto":
                JsonObject json_player = json.getAsJsonObject("user");
                JsonObject json_challenge = json.getAsJsonObject("challenge");
                player = new Gson().fromJson(json_player, Player.class);
                challenge = new Gson().fromJson(json_challenge, Challenge.class);
                challenge.setRead(false);
                playerController.create(player);
                challengeController.create(challenge);
                actionReceivedChallenge();
                break;
            case "update_state":
                int challenge_id = json.get("challenge_id").getAsInt();
                String state = json.get("state").getAsString();
                challenge = challengeController.show(challenge_id);
                challenge.setState(state);
                challengeController.update(challenge);
                if(state.equalsIgnoreCase("rechazado")) {
                    Balance balance_rejected = rechargeController.get();
                    float new_value_expired = Math.round(balance_rejected.getValue() + challenge.getInitial_value());
                    balance_rejected.setValue(new_value_expired);
                    rechargeController.update(balance_rejected);
                    actionUpdateValueBalance(1);
                }
                actionReceivedChallenge();
                break;
            case "message":
                JsonObject json_message = json.getAsJsonObject("message").getAsJsonObject();
                Message message_chat = new Gson().fromJson(json_message, Message.class);
                messageController.create(message_chat);
                int challenge_id_message = json.get("challenge_id").getAsInt();
                challenge = challengeController.show(challenge_id_message);
                challenge.setUpdated_at(message_chat.getCreated_at());
                challengeController.update(challenge);
                actionReceivedChallenge();
                break;
            case "payment":
                JsonObject json_balance = json.getAsJsonObject("balance").getAsJsonObject();
                Balance balance = new Gson().fromJson(json_balance, Balance.class);
                int balance_code = rechargeController.show(balance.getBalance_id()).getCode();
                balance.setCode(balance_code);
                rechargeController.update(balance);
                actionReceivedRecharge();
                actionUpdateValueBalance(1);
                break;
            case "score":
                JsonObject json_challenge_score = json.getAsJsonObject("challenge").getAsJsonObject();
                Challenge challenge_score = new Gson().fromJson(json_challenge_score, Challenge.class);
                int challenge_code = challengeController.show(challenge_score.getChallenge_id()).getCode();
                challenge_score.setCode(challenge_code);
                challengeController.update(challenge_score);
                actionReceivedChallenge();
                break;
            case "winner":
                Balance balance_update = rechargeController.get();
                float new_value = json.get("new_value").getAsFloat();
                balance_update.setValue(new_value);
                actionUpdateValueBalance(1);
                if(rechargeController.update(balance_update)) System.out.println("¡actualizo!");
                break;
            case "leave":
                Balance balance_leave = rechargeController.get();
                float new_value_leave = json.get("new_value").getAsFloat();
                balance_leave.setValue(new_value_leave);
                if(rechargeController.update(balance_leave)) System.out.println("¡actualizo!");
                JsonObject json_challenge_leave = json.getAsJsonObject("challenge").getAsJsonObject();
                Challenge challenge_leave = new Gson().fromJson(json_challenge_leave, Challenge.class);
                int code_leave = challengeController.show(challenge_leave.getChallenge_id()).getCode();
                challenge_leave.setCode(code_leave);
                challengeController.update(challenge_leave);
                actionUpdateValueBalance(1);
                actionReceivedChallenge();
                break;
            case "reported":
                JsonObject json_challenge_reported = json.getAsJsonObject("challenge").getAsJsonObject();
                Challenge challenge_reported = new Gson().fromJson(json_challenge_reported, Challenge.class);
                int code = challengeController.show(challenge_reported.getChallenge_id()).getCode();
                challenge_reported.setCode(code);
                challengeController.update(challenge_reported);

                JsonObject json_report = json.getAsJsonObject("report").getAsJsonObject();
                Report report = new Gson().fromJson(json_report, Report.class);
                report.setState("Pendiente");
                reportController.create(report);
                break;
            case "accept_challenge":
                JsonObject json_player_accept = json.getAsJsonObject("user");
                System.out.println("json accept: " + json_player_accept.toString());
                int challenge_id_accept = json.get("challenge_id").getAsInt();
                player = new Gson().fromJson(json_player_accept, Player.class);
                playerController.create(player);
                String state_challenge = json.get("state").getAsString();
                challenge = challengeController.show(challenge_id_accept);
                challenge.setPlayer_two(player.getUser_id());
                challenge.setState(state_challenge);
                challengeController.update(challenge);
                actionUpdateValueBalance(1);
                actionReceivedChallenge();
                break;
            case "challenge_expired":
                int challenge_id_expired = json.get("challenge_id").getAsInt();
                challenge = challengeController.show(challenge_id_expired);
                challenge.setState("expirado");
                challengeController.update(challenge);
                if(json.has("value_two")) {
                    float new_value_expired = json.get("value_two").getAsFloat();
                    Balance balance_expired = rechargeController.get();
                    balance_expired.setValue(new_value_expired);
                    rechargeController.update(balance_expired);
                    actionUpdateValueBalance(1);
                }
                break;
            case "challenge_canceled":
                int challenge_id_canceled = json.get("challenge_id").getAsInt();
                challenge = challengeController.show(challenge_id_canceled);
                challenge.setState("cancelado");
                challengeController.update(challenge);
                break;
            case "resolution":
                Balance balance_resolution = rechargeController.get();
                boolean win = json.get("win").getAsBoolean();
                JsonObject json_report_resolution = json.getAsJsonObject("report").getAsJsonObject();
                Report report_resolution = createReport(json_report_resolution);
                if(win) {
                    float new_value_resolution = json.get("new_value").getAsFloat();
                    balance_resolution.setValue(new_value_resolution);
                    report_resolution.setState("Ganador");
                    actionUpdateValueBalance(1);
                } else {
                    report_resolution.setState("Perdedor");
                }
                reportController.create(report_resolution);
                if(rechargeController.update(balance_resolution)) System.out.println("¡actualizo!");
                break;
            case "debug":
                System.out.println("debug: " + message);
                break;
        }

    }

    public void openedNotification(Context context, String message){
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(message);
        String type_notification = json.get("type").getAsString();
        int challenge_id;
        int player_id;
        switch (type_notification){
            case "reto":
                challenge_id = json.getAsJsonObject("challenge").getAsJsonObject().get("id").getAsInt();
                JsonObject json_player = json.getAsJsonObject("user");
                Player player = new Gson().fromJson(json_player, Player.class);
                Intent intent = new Intent(context, BaseFrames.class);
                intent.putExtra("from", "notifications");
                intent.putExtra("challenge_id", String.valueOf(challenge_id));
                intent.putExtra("player_id", String.valueOf(player.getUser_id()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case "update_state":
                Intent timeline_update = new Intent(context, Timeline.class);
                timeline_update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(timeline_update);
                break;
            case "message":
                challenge_id = json.get("challenge_id").getAsInt();
                player_id = json.get("player_id").getAsInt();
                Intent chat_room = new Intent(context, ChatRoom.class);
                chat_room.putExtra("challenge_id", String.valueOf(challenge_id));
                chat_room.putExtra("player_id", String.valueOf(player_id));
                chat_room.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chat_room);
                break;
            case "payment":
                System.out.println(json.getAsJsonObject("epayco"));
                Intent payment = new Intent(context, Payments.class);
                payment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(payment);
                break;
            case "winner":
                System.out.println(json.getAsJsonObject("winner"));
                Intent payment_winner = new Intent(context, Payments.class);
                payment_winner.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(payment_winner);
                break;
            case "leave":
                System.out.println(json.getAsJsonObject("leave"));
                Intent payment_leave = new Intent(context, Payments.class);
                payment_leave.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(payment_leave);
                break;
            case "score":
                JsonObject json_challenge_score = json.getAsJsonObject("challenge");
                int from_user = json.get("from_user").getAsInt();
                Challenge challenge = new Gson().fromJson(json_challenge_score, Challenge.class);
                Intent chat_room_score = new Intent(context, ChatRoom.class);
                chat_room_score.putExtra("challenge_id", String.valueOf(challenge.getChallenge_id()));
                chat_room_score.putExtra("player_id", String.valueOf(from_user));
                chat_room_score.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chat_room_score);
                break;
            case "reported":
                Intent reported = new Intent(context, Reported.class);
                reported.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(reported);
                break;
            case "accept_challenge":
                Intent timeline_acceptd = new Intent(context, Timeline.class);
                timeline_acceptd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(timeline_acceptd);
                break;
            case "challenge_expired":
                Intent timeline_expired = new Intent(context, Timeline.class);
                timeline_expired.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(timeline_expired);
                break;
            case "challenge_canceled":
                Intent timeline_canceled = new Intent(context, Timeline.class);
                timeline_canceled.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(timeline_canceled);
                break;
            case "resolution":
                Intent timeline_resolution = new Intent(context, Timeline.class);
                timeline_resolution.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(timeline_resolution);
                break;
        }
    }

    public void actionReceivedChallenge(){
        AppCompatActivity activity = new AppCompatActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.findFragmentById(R.id.frame_challenges_state);
        StationBus.getBus().post(new MessageBusChallenge(true));
    }

    public void actionReceivedRecharge(){
        StationBus.getBus().post(new MessageBusPayment(true));
        StationBus.getBus().post(new MessageBusUpdateTimeline(true, "payment"));
    }

    public void actionUpdateValueBalance(int option){
        List<String> empty = new ArrayList<>();
        StationBus.getBus().post(new MessageBusTimeline(true, option, empty));
    }

    public void actionReceibedMessage(int notification_id, String message){
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(message);
        String type_notification = json.get("type").getAsString();
        int type;
        switch (type_notification) {
            case "message":
                type = 1;
                break;
            case "score":
                type = 2;
                break;
            case "leave":
                type = 3;
                break;
            default:
                type = 9;
                break;
        }
        StationBus.getBus().post(new MessageBusChat(true, type, notification_id));
    }

    public Report createReport(JsonObject json_report) {
        Report report = new Report();
        report.setReport_id(json_report.get("id").getAsInt());
        report.setChallenge_id(json_report.get("challenge_id").getAsInt());
        return report;
    }

//endregion

}
