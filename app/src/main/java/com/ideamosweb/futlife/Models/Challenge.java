package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 23/03/17.
 * Función:
 */
@DatabaseTable(tableName = "challenges")
public class Challenge {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int challenge_id;
    @DatabaseField(canBeNull = false)
    private int player_one;
    @DatabaseField(canBeNull = true)
    private int player_two;
    @DatabaseField(canBeNull = true)
    private int score_player_one;
    @DatabaseField(canBeNull = true)
    private int score_player_two;
    @DatabaseField(canBeNull = false)
    private int console_id;
    @DatabaseField(canBeNull = false)
    private int game_id;
    @DatabaseField(canBeNull = false)
    private float amount_bet;
    @DatabaseField(canBeNull = false)
    private float initial_value;
    @DatabaseField(canBeNull = false, defaultValue = "true")
    private String deadline;
    @DatabaseField(canBeNull = false, defaultValue = "normal")
    private String state;
    @DatabaseField(canBeNull = false)
    private String type;
    @DatabaseField(canBeNull = false)
    private boolean visible_one;
    @DatabaseField(canBeNull = false)
    private boolean visible_two;
    @DatabaseField(canBeNull = false)
    private boolean read;
    @DatabaseField(canBeNull = false)
    private String updated_at;

    public Challenge() {}

    public Challenge(int challenge_id, int player_one, int player_two, int score_player_one,
                     int score_player_two, int console_id, int game_id, float amount_bet,
                     float initial_value, String deadline, String state, String type,
                     boolean visible_one, boolean visible_two, boolean read, String updated_at) {
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
    }

//region Getters del modelo
    public int getCode() {
        return code;
    }
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
//endregion

//region Setters del modelo
    public void setCode(int code) {
        this.code = code;
    }
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
//endregion

//region Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "Challenge{" +
            "code=" + code +
            ", challenge_id=" + challenge_id +
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
        '}';
    }
//endregion

}
