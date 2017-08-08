package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 14/12/16.
 * Función:
 */
@DatabaseTable(tableName = "tournaments")
public class Tournament {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int tournament_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private float value_inscription;
    @DatabaseField(canBeNull = false)
    private float reward;
    @DatabaseField(canBeNull = false)
    private int state;
    @DatabaseField(canBeNull = false)
    private String start_date;
    @DatabaseField(canBeNull = false)
    private String start_time;
    @DatabaseField(canBeNull = false)
    private String close_date;
    @DatabaseField(canBeNull = false)
    private String close_time;
    @DatabaseField(canBeNull = false)
    private int console_id;
    @DatabaseField(canBeNull = false)
    private int game_id;
    @DatabaseField(canBeNull = false)
    private int mode_id;
    @DatabaseField(canBeNull = true, defaultValue = "true")
    private Boolean active;
    @DatabaseField(canBeNull = true, defaultValue = "false")
    private Boolean subscribed;

    public Tournament() {}

    public Tournament(int tournament_id, String name, float value_inscription, float reward,
                      int state, String start_date, String start_time, String close_date,
                      String close_time, int console_id, int game_id, int mode_id,
                      Boolean active, Boolean subscribed) {
        this.tournament_id = tournament_id;
        this.name = name;
        this.value_inscription = value_inscription;
        this.reward = reward;
        this.state = state;
        this.start_date = start_date;
        this.start_time = start_time;
        this.close_date = close_date;
        this.close_time = close_time;
        this.console_id = console_id;
        this.game_id = game_id;
        this.mode_id = mode_id;
        this.active = active;
        this.subscribed = subscribed;
    }

//region getters del objeto
    public int getCode() {
        return code;
    }

    public int getTournament_id() {
        return tournament_id;
    }

    public String getName() {
        return name;
    }

    public float getValue_inscription() {
        return value_inscription;
    }

    public float getReward() {
        return reward;
    }

    public int getState() {
        return state;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getClose_date() {
        return close_date;
    }

    public String getClose_time() {
        return close_time;
    }

    public int getConsole_id() {
        return console_id;
    }

    public int getGame_id() {
        return game_id;
    }

    public int getMode_id() {
        return mode_id;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }
//endregion

//region getters del objeto
    public void setTournament_id(int tournament_id) {
        this.tournament_id = tournament_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setValue_inscription(float value_inscription) {
        this.value_inscription = value_inscription;
    }
    public void setReward(float reward) {
        this.reward = reward;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
    public void setClose_date(String close_date) {
        this.close_date = close_date;
    }
    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }
    public void setConsole_id(int console_id) {
        this.console_id = console_id;
    }
    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }
    public void setMode_id(int mode_id) {
        this.mode_id = mode_id;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }
//endregion

    @Override
    public String toString() {
        return "Tournament{" +
            "code=" + code +
            ", tournament_id=" + tournament_id +
            ", name='" + name + '\'' +
            ", value_inscription=" + value_inscription +
            ", reward=" + reward +
            ", state=" + state +
            ", start_date='" + start_date + '\'' +
            ", start_time='" + start_time + '\'' +
            ", close_date='" + close_date + '\'' +
            ", close_time='" + close_time + '\'' +
            ", console_id=" + console_id +
            ", game_id=" + game_id +
            ", mode_id=" + mode_id +
            ", active=" + active +
            ", subscribed=" + subscribed +
        '}';
    }
}
