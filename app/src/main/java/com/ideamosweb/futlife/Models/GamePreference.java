package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 1/03/17.
 * Función:
 */
@DatabaseTable(tableName = "games_preference")
public class GamePreference {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = true)
    private int game_id;
    @DatabaseField(canBeNull = false)
    private int console_id;
    @DatabaseField(canBeNull = false)
    private int user_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String year;
    @DatabaseField(canBeNull = false)
    private String avatar;
    @DatabaseField(canBeNull = true)
    private String thumbnail;
    @DatabaseField(canBeNull = true, defaultValue = "true")
    private Boolean active;

    public GamePreference() {}

    public GamePreference(int game_id, int console_id, int user_id, String name, String year,
                          String avatar, String thumbnail, Boolean active) {
        this.game_id = game_id;
        this.console_id = console_id;
        this.user_id = user_id;
        this.name = name;
        this.year = year;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.active = active;
    }

//region Getters
    public int getCode() {
        return code;
    }
    public int getGame_id() {
        return game_id;
    }
    public int getConsole_id() {
        return console_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public String getName() {
        return name;
    }
    public String getYear() {
        return year;
    }
    public String getAvatar() {
        return avatar;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public Boolean getActive() {
        return active;
    }
//endregion

//region Setters
    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }
    public void setConsole_id(int console_id) {
        this.console_id = console_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
//endregion

//Metodo para imprimir el objeto
    @Override
    public String toString() {
        return "GamePreference{" +
                "code=" + code +
                ", game_id=" + game_id +
                ", console_id=" + console_id +
                ", user_id=" + user_id +
                ", name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", avatar='" + avatar + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GamePreference)) return false;
        GamePreference obj_game = (GamePreference)obj;
        return this.getGame_id() == obj_game.getGame_id();
    }

}
