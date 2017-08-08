package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 9/11/16.
 * Función:
 */
@DatabaseTable(tableName = "games")
public class Game {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int game_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String avatar;
    @DatabaseField(canBeNull = false)
    private String thumbnail;
    @DatabaseField(canBeNull = false)
    private String year;
    @DatabaseField(canBeNull = true, defaultValue = "false")
    private Boolean select;

    public Game() {}

    public Game(int game_id, String name, String avatar, String thumbnail, String year, Boolean select) {
        this.game_id = game_id;
        this.name = name;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.year = year;
        this.select = select;
    }

    public Game(int game_id, String name, String avatar, String year, Boolean select) {
        this.game_id = game_id;
        this.name = name;
        this.avatar = avatar;
        this.year = year;
        this.select = select;
    }

//region Getters del modelos
    public int getCode() {
        return code;
    }
    public int getGame_id() {
        return game_id;
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
    public String getYear() {
        return year;
    }
    public Boolean getSelect() {
        return select;
    }
//endregion

//region Setters del modelo
    public void setGame_id(int game_id) {
        this.game_id = game_id;
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
    public void setYear(String year) {
        this.year = year;
    }
    public void setSelect(Boolean select) {
        this.select = select;
    }
//endregion

    //Funcion para imprimir el modelo
    @Override
    public String toString() {
        return "Game {" + '\n' +
                "   code=" + code + '\n' +
                "   game_id=" + game_id + '\n' +
                "   name=" + name + '\n' +
                "   avatar=" + avatar + '\n' +
                "   thumbnail=" + thumbnail + '\n' +
                "   year=" + year + '\n' +
                "   select=" + select + '\n' +
        '}' + '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Game)) return false;
        Game obj_game = (Game)obj;
        return this.getGame_id() == obj_game.getGame_id();
    }

}
