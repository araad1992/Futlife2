package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 31/10/16.
 * Función:
 */
@DatabaseTable(tableName = "users")
public class User {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private Integer user_id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String username;
    @DatabaseField(canBeNull = false)
    private String email;
    @DatabaseField(canBeNull = true)
    private String avatar;
    @DatabaseField(canBeNull = true)
    private String thumbnail;
    @DatabaseField(canBeNull = true)
    private int ubication;
    @DatabaseField(canBeNull = true)
    private String city_name;
    @DatabaseField(canBeNull = true)
    private String telephone;
    @DatabaseField(canBeNull = true)
    private String birthdate;
    @DatabaseField(canBeNull = true)
    private String platform;
    @DatabaseField(canBeNull = false)
    private Boolean social;
    @DatabaseField(canBeNull = false)
    private Boolean active;
    @DatabaseField(canBeNull = false)
    private String token;

    public User(){}

    public User(Integer user_id, String name, String username, String email, String avatar,
                String thumbnail, int ubication, String city_name, String telephone,
                String birthdate, String platform, Boolean social, Boolean active, String token) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.ubication = ubication;
        this.city_name = city_name;
        this.telephone = telephone;
        this.birthdate = birthdate;
        this.platform = platform;
        this.social = social;
        this.active = active;
        this.token = token;
    }

    //region Getters
    public int getCode() {
        return code;
    }
    public Integer getUser_id() {
        return user_id;
    }
    public String getName() {
        return name;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getAvatar() {
        return avatar;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public int getUbication() {
        return ubication;
    }
    public String getCity_name() {
        return city_name;
    }
    public String getTelephone() {
        return telephone;
    }
    public String getBirthdate() {
        return birthdate;
    }
    public String getPlatform() {
        return platform;
    }
    public Boolean getSocial() {
        return social;
    }
    public Boolean getActive() {
        return active;
    }
    public String getToken() {
        return token;
    }
//endregion

//region Setters
    public void setCode(int code) {
        this.code = code;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public void setUbication(int ubication) {
        this.ubication = ubication;
    }
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public void setSocial(Boolean social) {
        this.social = social;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public void setToken(String token) {
        this.token = token;
    }
//endregion

    //Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "User{" +
                "code=" + code +
                ", user_id=" + user_id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", ubication='" + ubication + '\'' +
                ", city_name='" + city_name + '\'' +
                ", telephone='" + telephone + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", platform='" + platform + '\'' +
                ", social=" + social +
                ", active=" + active +
                ", token='" + token + '\'' +
                '}';
    }
}
