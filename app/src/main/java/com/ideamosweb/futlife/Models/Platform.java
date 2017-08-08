package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 12/02/17.
 * Función:
 */
@DatabaseTable(tableName = "platforms")
public class Platform {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int platform_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = true)
    private String avatar;

    public Platform() {}

    public Platform(int platform_id, String name, String avatar) {
        this.platform_id = platform_id;
        this.name = name;
        this.avatar = avatar;
    }

//region Getters
    public int getCode() {
        return code;
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
//endregion

//region Setters
    public void setPlatform_id(int platform_id) {
        this.platform_id = platform_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
//endregion

    //Metodo para imprimir el objeto
    @Override
    public String toString() {
        return "Platform{" +
            "code=" + code +
            ", platform_id=" + platform_id +
            ", name='" + name + '\'' +
            ", avatar='" + avatar + '\'' +
        '}';
    }
}
