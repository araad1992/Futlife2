package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 1/03/17.
 * Función:
 */
@DatabaseTable(tableName = "consoles_preference")
public class ConsolePreference {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int console_id;
    @DatabaseField(canBeNull = false)
    private int preference_id;
    @DatabaseField(canBeNull = false)
    private int user_id;
    @DatabaseField(canBeNull = true)
    private String player_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String avatar;
    @DatabaseField(canBeNull = true)
    private String thumbnail;
    @DatabaseField(canBeNull = true, defaultValue = "true")
    private Boolean active;

    public ConsolePreference(){}

    public ConsolePreference(int console_id, int preference_id, int user_id, String player_id,
                             String name, String avatar, String thumbnail, Boolean active) {
        this.console_id = console_id;
        this.preference_id = preference_id;
        this.user_id = user_id;
        this.player_id = player_id;
        this.name = name;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.active = active;
    }

//region Getters
    public int getCode() {
        return code;
    }
    public int getConsole_id() {
        return console_id;
    }
    public int getPreference_id() {
        return preference_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public String getPlayer_id() {
        return player_id;
    }
    public String getName() {
        return name;
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
    public void setConsole_id(int console_id) {
        this.console_id = console_id;
    }
    public void setPreference_id(int preference_id) {
        this.preference_id = preference_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }
    public void setName(String name) {
        this.name = name;
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

//region Funcion para imprimir los datos del objeto
    @Override
    public String toString() {
        return "ConsolePreference{" +
                "code=" + code +
                ", console_id=" + console_id +
                ", preference_id=" + preference_id +
                ", user_id=" + user_id +
                ", player_id='" + player_id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", active=" + active +
                '}';
    }
//endregion

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ConsolePreference)) return false;
        ConsolePreference obj_console = (ConsolePreference) obj;
        return this.getConsole_id() == obj_console.getConsole_id();
    }

}
