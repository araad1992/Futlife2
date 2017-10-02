package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Creado por Deimer Villa on 23/03/17.
 * Función:
 */
public class Challenge {

    @SerializedName("id")
    private int challenge_id;
    private int player_one;
    private int player_two;
    private int score_player_one;
    private int score_player_two;
    private int console_id;
    private int game_id;
    private float amount_bet;
    private float initial_value;
    private String deadline;
    private String state;
    private String type;
    private boolean visible_one;
    private boolean visible_two;
    private boolean read;
    private String updated_at;
    private String username_one;
    private String username_two;
    private String name_one;
    private String name_two;
    private String avatar_one;
    private String avatar_two;

//region Constructores
    public Challenge() {}
    public Challenge(int challenge_id, int player_one, int player_two, int score_player_one,
                     int score_player_two, int console_id, int game_id, float amount_bet,
                     float initial_value, String deadline, String state, String type,
                     boolean visible_one, boolean visible_two, boolean read, String updated_at,
                     String username_one, String username_two, String name_one, String name_two,
                     String avatar_one, String avatar_two) {
        this.challenge_id = challenge_id;
        this.player_one = player_one;
        this.player_two = player_two;
        this.score_player_one = score_player_one;
        this.score_player_two = score_player_two;
        this.console_id = console_id;
        this.game_id = game_id;
        this.amount_bet = amount_bet;
        this.initial_value = initial_value;
        this.deadline = deadline;
        this.state = state;
        this.type = type;
        this.visible_one = visible_one;
        this.visible_two = visible_two;
        this.read = read;
        this.updated_at = updated_at;
        this.username_one = username_one;
        this.username_two = username_two;
        this.name_one = name_one;
        this.name_two = name_two;
        this.avatar_one = avatar_one;
        this.avatar_two = avatar_two;
    }
//endregion

//region Getters del modelo
    public int getChallenge_id() {
        return challenge_id;
    }
    public int getPlayer_one() {
        return player_one;
    }
    public int getPlayer_two() {
        return player_two;
    }
    public int getScore_player_one() {
        return score_player_one;
    }
    public int getScore_player_two() {
        return score_player_two;
    }
    public int getConsole_id() {
        return console_id;
    }
    public int getGame_id() {
        return game_id;
    }
    public float getAmount_bet() {
        return amount_bet;
    }
    public float getInitial_value() {
        return initial_value;
    }
    public String getDeadline() {
        return deadline;
    }
    public String getState() {
        return state;
    }
    public String getType() {
        return type;
    }
    public boolean isVisible_one() {
        return visible_one;
    }
    public boolean isVisible_two() {
        return visible_two;
    }
    public boolean isRead() {
        return read;
    }
    public String getUpdated_at() {
        return updated_at;
    }
    public String getUsername_one() {
        return username_one;
    }
    public String getUsername_two() {
        return username_two;
    }
    public String getName_one() {
        return name_one;
    }
    public String getName_two() {
        return name_two;
    }
    public String getAvatar_one() {
        return avatar_one;
    }
    public String getAvatar_two() {
        return avatar_two;
    }
    //endregion

//region Setters del modelo
    public void setChallenge_id(int challenge_id) {
        this.challenge_id = challenge_id;
    }
    public void setPlayer_one(int player_one) {
        this.player_one = player_one;
    }
    public void setPlayer_two(int player_two) {
        this.player_two = player_two;
    }
    public void setScore_player_one(int score_player_one) {
        this.score_player_one = score_player_one;
    }
    public void setScore_player_two(int score_player_two) {
        this.score_player_two = score_player_two;
    }
    public void setConsole_id(int console_id) {
        this.console_id = console_id;
    }
    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }
    public void setAmount_bet(float amount_bet) {
        this.amount_bet = amount_bet;
    }
    public void setInitial_value(float initial_value) {
        this.initial_value = initial_value;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setVisible_one(boolean visible_one) {
        this.visible_one = visible_one;
    }
    public void setVisible_two(boolean visible_two) {
        this.visible_two = visible_two;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    public void setUsername_one(String username_one) {
        this.username_one = username_one;
    }
    public void setUsername_two(String username_two) {
        this.username_two = username_two;
    }
    public void setName_one(String name_one) {
        this.name_one = name_one;
    }
    public void setName_two(String name_two) {
        this.name_two = name_two;
    }
    public void setAvatar_one(String avatar_one) {
        this.avatar_one = avatar_one;
    }
    public void setAvatar_two(String avatar_two) {
        this.avatar_two = avatar_two;
    }
    //endregion

//region Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "Challenge{" +
                "challenge_id=" + challenge_id +
                ", player_one=" + player_one +
                ", player_two=" + player_two +
                ", score_player_one=" + score_player_one +
                ", score_player_two=" + score_player_two +
                ", console_id=" + console_id +
                ", game_id=" + game_id +
                ", amount_bet=" + amount_bet +
                ", initial_value=" + initial_value +
                ", deadline='" + deadline + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", visible_one=" + visible_one +
                ", visible_two=" + visible_two +
                ", read=" + read +
                ", updated_at='" + updated_at + '\'' +
                ", username_one='" + username_one + '\'' +
                ", username_two='" + username_two + '\'' +
                ", name_one='" + name_one + '\'' +
                ", name_two='" + name_two + '\'' +
                ", avatar_one='" + avatar_one + '\'' +
                ", avatar_two='" + avatar_two + '\'' +
                '}';
    }
//endregion

}
