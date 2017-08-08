package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 18/04/17.
 * Función:
 */
@DatabaseTable(tableName = "messages")
public class Message {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = true)
    private int message_id;
    @DatabaseField(canBeNull = false)
    private int from_user;
    @DatabaseField(canBeNull = false)
    private int to_user;
    @DatabaseField(canBeNull = false)
    private int challenge_id;
    @DatabaseField(canBeNull = false)
    private String message_text;
    @DatabaseField(canBeNull = true)
    private String created_at;
    @DatabaseField(canBeNull = true, defaultValue = "true")
    private Boolean active;

    public Message() {}

    public Message(int message_id, int from_user, int to_user, String message_text, String created_at, Boolean active) {
        this.message_id = message_id;
        this.from_user = from_user;
        this.to_user = to_user;
        this.message_text = message_text;
        this.created_at = created_at;
        this.active = active;
    }

//region Getters del modelo
    public int getCode() {
        return code;
    }
    public int getMessage_id() {
        return message_id;
    }
    public int getFrom_user() {
        return from_user;
    }
    public int getTo_user() {
        return to_user;
    }
    public int getChallenge_id() {
        return challenge_id;
    }
    public String getMessage_text() {
        return message_text;
    }
    public String getCreated_at() {
        return created_at;
    }
    public Boolean getActive() {
        return active;
    }
//endregion

//region Setters del modelo
    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }
    public void setFrom_user(int from_user) {
        this.from_user = from_user;
    }
    public void setTo_user(int to_user) {
        this.to_user = to_user;
    }
    public void setChallenge_id(int challenge_id) {
        this.challenge_id = challenge_id;
    }
    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
//endregion Setters del modelo

    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", message_id=" + message_id +
                ", from_user=" + from_user +
                ", to_user=" + to_user +
                ", challenge_id=" + challenge_id +
                ", message_text='" + message_text + '\'' +
                ", created_at='" + created_at + '\'' +
                ", active=" + active +
                '}';
    }
}
