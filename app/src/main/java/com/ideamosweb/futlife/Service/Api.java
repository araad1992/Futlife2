package com.ideamosweb.futlife.Service;

import com.google.gson.JsonObject;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Creado por Deimer Villa on 25/10/16.
 * Función:
 */
public interface Api {

//region Authetication
    @FormUrlEncoded
    @POST("/login")
    void login(
            @Field("email")String email,
            @Field("password")String password,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/login")
    void loginWhitUsername(
            @Field("username")String username,
            @Field("password")String password,
            Callback<JsonObject> cb
    );

    @GET("/logout")
    void logout(
            @Header("Authorization") String token,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/register")
    void register(
            @Field("name")String name,
            @Field("username")String username,
            @Field("email")String email,
            @Field("password")String password,
            @Field("password_confirmation")String password_confirmation,
            @Field("platform")String platform,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/register")
    void registerFacebook(
            @Field("name")String name,
            @Field("username")String username,
            @Field("email")String email,
            @Field("password")String password,
            @Field("password_confirmation")String password_confirmation,
            @Field("avatar")String avatar,
            @Field("thumbnail")String thumbnail,
            @Field("platform")String platform,
            @Field("provider")String provider,
            @Field("provider_id")String provider_id,
            @Field("social_token")String social_token,
            @Field("social")boolean social,
            Callback<JsonObject> cb
    );
//endregion

//region Users
    @GET("/players/{user_id}/{skip}")
    void getUsers(
            @Header("Authorization") String token,
            @Path("user_id") int user_id,
            @Path("skip") int skip,
            Callback<JsonObject> cb
    );

    @GET("/users/{user_id}/get")
    void showPlayer(
            @Header("Authorization") String token,
            @Path("user_id")int user_id,
            Callback<JsonObject> cb
    );

    @PUT("/user/{id}/update")
    void updateUser(
            @Header("Authorization") String token,
            @Path("id")int user_id,
            @Body JsonObject json_user,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @PUT("/user/password/change")
    void changePassword(
            @Header("Authorization") String token,
            @Field("user_id")int user_id,
            @Field("old_password")String old_password,
            @Field("new_password")String new_password,
            @Field("new_password_confirmation")String password_confirmation,
            Callback<JsonObject> cb
    );
//endregion

//region Data configure

    @FormUrlEncoded
    @POST("/contact")
    void contact(
            @Field("email")String email,
            @Field("name")String name,
            @Field("telephone")String telephone,
            @Field("city")String city,
            @Field("message")String message,
            Callback<JsonObject> cb
    );

    @GET("/settings/{platform}/get")
    void getSettings(
            @Path("platform")String platform,
            Callback<JsonObject> cb
    );

    @GET("/parameters")
    void getParameters(
            Callback<JsonObject> cb
    );

    @GET("/cities")
    void autocomplete(
            @Query("keyword") String keyword,
            Callback<JsonObject> cb
    );

//endregion

//region Preferences
    @FormUrlEncoded
    @POST("/preferences/create")
    void uploadPreferences(
            @Header("Authorization") String token,
            @Field("user_id")int user_id,
            @Field("preferences")String preferences,
            Callback<JsonObject> cb
    );

    @GET("/preferences/{user_id}/show")
    void showPreference(
            @Header("Authorization") String token,
            @Path("user_id")int user_id,
            Callback<JsonObject> cb
    );

    @GET("/preferences/{user_id}/player-id")
    void showPlayerId(
            @Header("Authorization") String token,
            @Path("user_id")int user_id,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @PUT("/update/{id}/player-id")
    void updatePreference(
            @Header("Authorization") String token,
            @Path("id")int user_id,
            @Field("player_id")String name,
            Callback<JsonObject> cb
    );
//endregion

//region Upload avatars
    @Multipart
    @POST("/user/avatar")
    void uploadAvatar(
            @Header("Authorization") String token,
            @Part("user_id")int id,
            @Part("avatar")TypedFile avatar,
            Callback<JsonObject> cb
    );
//endregion

//region Launch challenges
    @FormUrlEncoded
    @POST("/challenge")
    void challenge(
            @Header("Authorization") String token,
            @Field("player_one")int player_one,
            @Field("player_two")int player_two,
            @Field("console_id")int console_id,
            @Field("game_id")int game_id,
            @Field("amount_bet")float amount_bet,
            @Field("initial_value")float initial_value,
            @Field("balance_id")float balance_id,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/challenge/open")
    void openChallenge(
            @Header("Authorization") String token,
            @Field("player_one")int player_one,
            @Field("console_id")int console_id,
            @Field("game_id")int game_id,
            @Field("amount_bet")float amount_bet,
            @Field("initial_value")float initial_value,
            @Field("balance_id")int balance_id,
            Callback<JsonObject> cb
    );

    @GET("/challenge/{user_id}/get")
    void getOpenChallenges(
            @Header("Authorization") String token,
            @Path("user_id")int user_id,
            Callback<JsonObject> cb
    );

    @GET("/challenge/{user_id}/show")
    void showChallenges(
            @Header("Authorization") String token,
            @Path("user_id")int user_id,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/challenge/response")
    void responseChallenge(
            @Header("Authorization") String token,
            @Field("challenge_id")int challenger_id,
            @Field("player_one")int player_one,
            @Field("player_two")int player_two,
            @Field("amount_bet")float amount_bet,
            @Field("state")String state,
            @Field("balance_id")int balance_id,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/challenge/expire")
    void expireChallenge(
            @Header("Authorization") String token,
            @Field("challenge_id")int challenger_id,
            @Field("state")String state,
            @Field("spare_value")float spare_value,
            @Field("from_user")int from_user,
            @Field("to_user")int to_user,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/challenge/cancel")
    void cancelChallenge(
            @Header("Authorization") String token,
            @Field("challenge_id")int challenge_id,
            @Field("balance_id")int balance_id,
            @Field("state")String state,
            @Field("from_user")int from_user,
            @Field("to_user")int to_user,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @PUT("/challenge/accept")
    void acceptChallenge(
            @Header("Authorization") String token,
            @Field("player_two") int player_two,
            @Field("challenge_id")int challenge_id,
            @Field("balance_id")int balance_id,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @PUT("/challenge/{id}/read")
    void challengeIsRead(
            @Header("Authorization") String token,
            @Path("id")int challenge_id,
            @Field("read")boolean read,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @POST("/challenge/{id}/leave")
    void challengeLeave(
            @Header("Authorization") String token,
            @Path("id")int challenge_id,
            @Field("user_id") int user_id,
            Callback<JsonObject> cb
    );

    @DELETE("/challenge/{id}/{user_id}/delete")
    void deleteChallenge(
            @Header("Authorization") String token,
            @Path("id")int challenge_id,
            @Path("user_id")int user_id,
            Callback<JsonObject> cb
    );

    @DELETE("/challenge/{id}/delete")
    void deleteOpenChallenge(
            @Header("Authorization") String token,
            @Path("id")int challenge_id,
            Callback<JsonObject> cb
    );
//endregion

//region Scores

    @FormUrlEncoded
    @POST("/challenge/add/score")
    void addScore(
            @Header("Authorization") String token,
            @Field("challenge_id")int challenge_id,
            @Field("from_user")int from_user,
            @Field("to_user")int to_user,
            @Field("score_player_one")int score_player_one,
            @Field("score_player_two")int score_player_two,
            Callback<JsonObject> cb
    );

    @FormUrlEncoded
    @PUT("/challenge/confirm/score")
    void confirmScore(
            @Header("Authorization") String token,
            @Field("challenge_id")int challenge_id,
            @Field("winner_id")int winner_id,
            @Field("confirm")String confirm,
            Callback<JsonObject> cb
    );

//endregion

//region Send messages

    @FormUrlEncoded
    @PUT("/report/{id}/update")
    void sendProof(
            @Header("Authorization") String token,
            @Path("id") int report_id,
            @Field("player_id")int player_id,
            @Field("description")String description,
            @Field("url_youtube")String url_youtube,
            @Field("url_image")String url_image,
            @Field("state")String state,
            Callback<JsonObject> cb
    );

    @Multipart
    @POST("/report/upload/image")
    void uploadImage(
            @Header("Authorization") String token,
            @Part("report_id")int report_id,
            @Part("image")TypedFile image,
            Callback<JsonObject> cb
    );

//endregion

//region Send messages

    @FormUrlEncoded
    @POST("/recharge/create")
    void uploadRecharge(
            @Header("Authorization") String token,
            @Field("user_id")int user_id,
            @Field("document_type")String document_type,
            @Field("document_number")String document_number,
            @Field("email")String email,
            @Field("transaction_type")String transaction_type,
            @Field("transaction_id")String transaction_id,
            @Field("project_id")String project_id,
            @Field("authorization")String authorization,
            @Field("transaction_date")String transaction_date,
            @Field("ref_payco")String ref_payco,
            @Field("invoice")String invoice,
            @Field("value")float value,
            @Field("coin")String coin,
            @Field("state")String state,
            Callback<JsonObject> cb
    );

    @GET("/recharge/{id}/last-history")
    void getLastHistory(
            @Header("Authorization") String token,
            @Path("id")int balance_id,
            Callback<JsonObject> cb
    );

//endregion

//region Send messages

    @FormUrlEncoded
    @POST("/message")
    void sendMessage(
            @Header("Authorization") String token,
            @Field("from_user")int from_user,
            @Field("to_user")int to_user,
            @Field("challenge_id")int challenge_id,
            @Field("message_text")String message_text,
            Callback<JsonObject> cb
    );

//endregion

}
