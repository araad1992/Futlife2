package com.ideamosweb.futlife.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Creado por Deimer Villa on 8/06/17.
 * Función:
 */
@DatabaseTable(tableName = "reports")
public class Report {

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(generatedId = true)
    private int code;

    @SerializedName("id")
    @DatabaseField(canBeNull = false)
    private int report_id;
    @DatabaseField(canBeNull = false)
    private int challenge_id;
    @DatabaseField(canBeNull = true)
    private String description;
    @DatabaseField(canBeNull = true)
    private String url_youtube;
    @DatabaseField(canBeNull = true)
    private String url_image;
    @DatabaseField(canBeNull = false)
    private String state;
    @DatabaseField(canBeNull = true)
    private String shipping_date;

    public Report() {}

    public Report(int report_id, int challenge_id, String description, String url_youtube,
                  String url_image, String state, String shipping_date) {
        this.report_id = report_id;
        this.challenge_id = challenge_id;
        this.description = description;
        this.url_youtube = url_youtube;
        this.url_image = url_image;
        this.state = state;
        this.shipping_date = shipping_date;
    }

//region Getters
    public int getCode() {
        return code;
    }
    public int getReport_id() {
        return report_id;
    }
    public int getChallenge_id() {
        return challenge_id;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl_youtube() {
        return url_youtube;
    }
    public String getUrl_image() {
        return url_image;
    }
    public String getState() {
        return state;
    }
    public String getShipping_date() {
        return shipping_date;
    }
//endregion

//region Setters
    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }
    public void setChallenge_id(int challenge_id) {
        this.challenge_id = challenge_id;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setUrl_youtube(String url_youtube) {
        this.url_youtube = url_youtube;
    }
    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setShipping_date(String shipping_date) {
        this.shipping_date = shipping_date;
    }
//endregion


    @Override
    public String toString() {
        return "Report{" +
                "code=" + code +
                ", report_id=" + report_id +
                ", challenge_id=" + challenge_id +
                ", description='" + description + '\'' +
                ", url_youtube='" + url_youtube + '\'' +
                ", url_image='" + url_image + '\'' +
                ", state='" + state + '\'' +
                ", shipping_date='" + shipping_date + '\'' +
                '}';
    }
}
