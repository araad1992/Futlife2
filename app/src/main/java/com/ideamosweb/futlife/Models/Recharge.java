package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 5/05/17.
 * Función:
 */
@DatabaseTable(tableName = "recharges")
public class Recharge {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int recharge_id;
    @DatabaseField(canBeNull = false)
    private int user_id;
    @DatabaseField(canBeNull = false)
    private String document_type;
    @DatabaseField(canBeNull = false)
    private String document_number;
    @DatabaseField(canBeNull = false)
    private String email;
    @DatabaseField(canBeNull = false)
    private String transaction_type;
    @DatabaseField(canBeNull = true)
    private String transaction_id;
    @DatabaseField(canBeNull = true)
    private String project_id;
    @DatabaseField(canBeNull = true)
    private String authorization;
    @DatabaseField(canBeNull = false)
    private String transaction_date;
    @DatabaseField(canBeNull = false)
    private String ref_payco;
    @DatabaseField(canBeNull = true)
    private String invoice;
    @DatabaseField(canBeNull = false)
    private float value;
    @DatabaseField(canBeNull = false)
    private String coin;
    @DatabaseField(canBeNull = true)
    private String bank;
    @DatabaseField(canBeNull = false)
    private String state;

    public Recharge() {}

    public Recharge(int recharge_id, int user_id, String document_type, String document_number,
                    String email, String transaction_type, String transaction_id, String project_id,
                    String authorization, String transaction_date, String ref_payco, String invoice,
                    float value, String coin, String bank, String state) {
        this.recharge_id = recharge_id;
        this.user_id = user_id;
        this.document_type = document_type;
        this.document_number = document_number;
        this.email = email;
        this.transaction_type = transaction_type;
        this.transaction_id = transaction_id;
        this.project_id = project_id;
        this.authorization = authorization;
        this.transaction_date = transaction_date;
        this.ref_payco = ref_payco;
        this.invoice = invoice;
        this.value = value;
        this.coin = coin;
        this.bank = bank;
        this.state = state;
    }

//region Getters del modelo
    public int getCode() {
        return code;
    }
    public int getRecharge_id() {
        return recharge_id;
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
    public String getTransaction_type() {
        return transaction_type;
    }
    public String getTransaction_id() {
        return transaction_id;
    }
    public String getProject_id() {
        return project_id;
    }
    public String getAuthorization() {
        return authorization;
    }
    public String getTransaction_date() {
        return transaction_date;
    }
    public String getRef_payco() {
        return ref_payco;
    }
    public String getInvoice() {
        return invoice;
    }
    public float getValue() {
        return value;
    }
    public String getCoin() {
        return coin;
    }
    public String getBank() {
        return bank;
    }
    public String getState() {
        return state;
    }
//endregion

//region Setters del modelo
    public void setCode(int code) {
        this.code = code;
    }
    public void setRecharge_id(int recharge_id) {
        this.recharge_id = recharge_id;
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
    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }
    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }
    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }
    public void setRef_payco(String ref_payco) {
        this.ref_payco = ref_payco;
    }
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
    public void setValue(float value) {
        this.value = value;
    }
    public void setCoin(String coin) {
        this.coin = coin;
    }
    public void setBank(String bank) {
        this.bank = bank;
    }
    public void setState(String state) {
        this.state = state;
    }
//endregion

//region Funcion para impresion del modelo
    @Override
    public String toString() {
        return "Recharge{" +
                "code=" + code +
                ", recharge_id=" + recharge_id +
                ", user_id=" + user_id +
                ", document_type='" + document_type + '\'' +
                ", document_number='" + document_number + '\'' +
                ", email='" + email + '\'' +
                ", transaction_type='" + transaction_type + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", project_id='" + project_id + '\'' +
                ", authorization='" + authorization + '\'' +
                ", transaction_date='" + transaction_date + '\'' +
                ", ref_payco='" + ref_payco + '\'' +
                ", invoice='" + invoice + '\'' +
                ", value=" + value +
                ", coin='" + coin + '\'' +
                ", bank='" + bank + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
//endregion

}
