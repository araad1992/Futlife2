package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 4/11/16.
 * Función:
 */
@DatabaseTable(tableName = "consoles")
public class Console {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int console_id;
    @DatabaseField(canBeNull = false)
    private int platform_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String avatar;
    @DatabaseField(canBeNull = false)
    private String thumbnail;
    @DatabaseField(canBeNull = true, defaultValue = "false")
    private Boolean select;

    public Console() {}

    public Console(int console_id, int platform_id, String name, String avatar, String thumbnail, Boolean select) {
        this.console_id = console_id;
        this.platform_id = platform_id;
        this.name = name;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.select = select;
    }

    //region Getters del modelo
    public int getCode() {
        return code;
    }
    public int getConsole_id() {
        return console_id;
    }
    public int getPlatform_id() {
        return platform_id;
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
    public Boolean getSelect() {
        return select;
    }
//endregion

//region Setters del modelo
    public void setConsole_id(int console_id) {
        this.console_id = console_id;
    }
    public void setPlatform_id(int platform_id) {
        this.platform_id = platform_id;
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
    public void setSelect(Boolean select) {
        this.select = select;
    }
//endregion

    //Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "Console {" + '\n' +
                "   code=" + code + '\n' +
                "   console_id=" + console_id + '\n' +
                "   platform_id=" + platform_id + '\n' +
                "   name=" + name + '\n' +
                "   avatar=" + avatar + '\n' +
                "   thumbnail=" + thumbnail + '\n' +
                "   select=" + select + '\n' +
        '}'+ '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Console)) return false;
        Console obj_console = (Console) obj;
        return this.getConsole_id() == obj_console.getConsole_id();
    }
}
