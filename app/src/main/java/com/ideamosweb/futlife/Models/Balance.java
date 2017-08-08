package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 26/05/17.
 * Función:
 */
@DatabaseTable(tableName = "balances")
public class Balance {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int balance_id;
    @DatabaseField(canBeNull = false)
    private int user_id;
    @DatabaseField(canBeNull = false)
    private String document_type;
    @DatabaseField(canBeNull = false)
    private String document_number;
    @DatabaseField(canBeNull = false)
    private String email;
    @DatabaseField(canBeNull = false)
    private float value;
    @DatabaseField(canBeNull = false)
    private String coin;
    @DatabaseField(canBeNull = false)
    private String state;

    public Balance() {}

    public Balance(int balance_id, int user_id, String document_type, String document_number, String email, float value, String coin, String state) {
        this.balance_id = balance_id;
        this.user_id = user_id;
        this.document_type = document_type;
        this.document_number = document_number;
        this.email = email;
        this.value = value;
        this.coin = coin;
        this.state = state;
    }

//region Getters del modelo
    public int getCode() {
        return code;
    }
    public int getBalance_id() {
        return balance_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public String getDocument_type() {
        return document_type;
    }
    public String getDocument_number() {
        return document_number;
    }
    public String getEmail() {
        return email;
    }
    public float getValue() {
        return value;
    }
    public String getCoin() {
        return coin;
    }
    public String getState() {
        return state;
    }
//endregion

//region Setters del modelo
    public void setCode(int code) {
        this.code = code;
    }
    public void setBalance_id(int balance_id) {
        this.balance_id = balance_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }
    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setValue(float value) {
        this.value = value;
    }
    public void setCoin(String coin) {
        this.coin = coin;
    }
    public void setState(String state) {
        this.state = state;
    }
//endregion

//region Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "Balance{" +
            "code=" + code +
            ", balance_id=" + balance_id +
            ", user_id=" + user_id +
            ", document_type='" + document_type + '\'' +
            ", document_number='" + document_number + '\'' +
            ", email='" + email + '\'' +
            ", value=" + value +
            ", coin='" + coin + '\'' +
            ", state='" + state + '\'' +
        '}';
    }
//endregion

}
