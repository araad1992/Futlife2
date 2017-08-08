package com.ideamosweb.futlife.Objects;

import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.Game;
import java.util.List;

/**
 * Creado por Deimer Villa on 15/02/17.
 * Función:
 */
public class ItemPreference {

    private int console_code;
    private int console_id;
    private String name;
    private String avatar;
    private List<Game> games;

    public ItemPreference() {}

    public ItemPreference(int console_code, int console_id, String name, String avatar, List<Game> games) {
        this.console_code = console_code;
        this.console_id = console_id;
        this.name = name;
        this.avatar = avatar;
        this.games = games;
    }

    //Getters
    public int getConsole_code() {
        return console_code;
    }
    public int getConsole_id() {
        return console_id;
    }
    public String getName() {
        return name;
    }
    public String getAvatar() {
        return avatar;
    }
    public List<Game> getGames() {
        return games;
    }

    //Setters
    public void setConsole_code(int console_code) {
        this.console_code = console_code;
    }
    public void setConsole_id(int console_id) {
        this.console_id = console_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "ItemPreference{" +
            "console_code=" + console_code +
            ", console_id=" + console_id +
            ", name='" + name + '\'' +
            ", avatar='" + avatar + '\'' +
            ", games=" + games +
        '}';
    }
}
