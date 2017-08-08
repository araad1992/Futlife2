package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 31/10/16.
 * Función:
 */
@DatabaseTable(tableName = "socials")
public class Social {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = true)
    private int social_id;
    @DatabaseField(canBeNull = true)
    private int user_id;
    @DatabaseField(canBeNull = true)
    private String avatar;
    @DatabaseField(canBeNull = false)
    private String provider;
    @DatabaseField(canBeNull = false)
    private String social_token;

    public Social() {}



//region Getters
    public int getCode() {
        return code;
    }
    public int getSocial_id() {
        return social_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public String getAvatar() {
        return avatar;
    }
    public String getProvider() {
        return provider;
    }
    public String getSocial_token() {
        return social_token;
    }
//endregion

//region Setters
    public void setSocial_id(int social_id) {
        this.social_id = social_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public void setSocial_token(String social_token) {
        this.social_token = social_token;
    }
//endregion

    //Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "Social{" + '\n' +
                "code=" + code + '\n' +
                ", social_id=" + social_id + '\n' +
                ", user_id=" + user_id + '\n' +
                ", avatar=" + avatar + '\n' +
                ", provider=" + provider + '\n' +
                ", social_token=" + social_token + '\n' +
        '}';
    }
}
