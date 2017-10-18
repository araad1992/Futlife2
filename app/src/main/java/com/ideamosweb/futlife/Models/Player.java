package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 2/02/17.
 * Función:
 */
public class Player {

    @SerializedName("id")
    private Integer user_id;
    private String name;
    private String username;
    private String email;
    private String avatar;
    private String thumbnail;
    private String player_id;
    private int ubication;
    private String city_name;
    private String telephone;
    private String birthdate;
    private String platform;
    private Boolean social;
    private Boolean active;

    //region Constructores
    public Player() {}
    public Player(Integer user_id, String name, String username, String email, String avatar,
                  String thumbnail, String player_id, int ubication, String city_name,
                  String telephone, String birthdate, String platform, Boolean social, Boolean active) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.thumbnail = thumbnail;
        this.player_id = player_id;
        this.ubication = ubication;
        this.city_name = city_name;
        this.telephone = telephone;
        this.birthdate = birthdate;
        this.platform = platform;
        this.social = social;
        this.active = active;
    }
    //endregion

    //region Getters
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
    public String getPlayer_id() {
        return player_id;
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
    //endregion

    //region Setters
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
    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
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
    //endregion

    //region Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "Player{" +
            ", user_id=" + user_id +
            ", name='" + name + '\'' +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", avatar='" + avatar + '\'' +
            ", thumbnail='" + thumbnail + '\'' +
            ", player_id='" + player_id + '\'' +
            ", ubication=" + ubication +
            ", city_name='" + city_name + '\'' +
            ", telephone='" + telephone + '\'' +
            ", birthdate='" + birthdate + '\'' +
            ", platform='" + platform + '\'' +
            ", social=" + social +
            ", active=" + active +
        '}';
    }
    //endregion

    //region Funcion para comparar
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (ubication != player.ubication) return false;
        if (user_id != null ? !user_id.equals(player.user_id) : player.user_id != null)
            return false;
        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        if (username != null ? !username.equals(player.username) : player.username != null)
            return false;
        if (email != null ? !email.equals(player.email) : player.email != null) return false;
        if (avatar != null ? !avatar.equals(player.avatar) : player.avatar != null) return false;
        if (thumbnail != null ? !thumbnail.equals(player.thumbnail) : player.thumbnail != null)
            return false;
        if (player_id != null ? !player_id.equals(player.player_id) : player.player_id != null)
            return false;
        if (city_name != null ? !city_name.equals(player.city_name) : player.city_name != null)
            return false;
        if (telephone != null ? !telephone.equals(player.telephone) : player.telephone != null)
            return false;
        if (birthdate != null ? !birthdate.equals(player.birthdate) : player.birthdate != null)
            return false;
        if (platform != null ? !platform.equals(player.platform) : player.platform != null)
            return false;
        if (social != null ? !social.equals(player.social) : player.social != null) return false;
        return active != null ? active.equals(player.active) : player.active == null;
    }
    @Override
    public int hashCode() {
        int result = user_id != null ? user_id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (player_id != null ? player_id.hashCode() : 0);
        result = 31 * result + ubication;
        result = 31 * result + (city_name != null ? city_name.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (social != null ? social.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        return result;
    }
    //endregion

}
