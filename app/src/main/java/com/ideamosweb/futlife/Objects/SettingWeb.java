package com.ideamosweb.futlife.Objects;

/**
 * Creado por Deimer Villa on 1/08/17.
 * Función:
 */
public class SettingWeb {

    private float min_amount_bet;
    private String retreat_date;
    private float min_amount_retreat;
    private float amount_min_penalty;
    private float percentage_gain;

    public SettingWeb() {}

    public SettingWeb(float min_amount_bet, String retreat_date,
                      float min_amount_retreat, float amount_min_penalty, float percentage_gain) {
        this.min_amount_bet = min_amount_bet;
        this.retreat_date = retreat_date;
        this.min_amount_retreat = min_amount_retreat;
        this.amount_min_penalty = amount_min_penalty;
        this.percentage_gain = percentage_gain;
    }

    //Getters
    public float getMin_amount_bet() {
        return min_amount_bet;
    }
    public String getRetreat_date() {
        return retreat_date;
    }
    public float getMin_amount_retreat() {
        return min_amount_retreat;
    }
    public float getAmount_min_penalty() {
        return amount_min_penalty;
    }
    public float getPercentage_gain() {
        return percentage_gain;
    }

    //Setters
    public void setMin_amount_bet(float min_amount_bet) {
        this.min_amount_bet = min_amount_bet;
    }
    public void setRetreat_date(String retreat_date) {
        this.retreat_date = retreat_date;
    }
    public void setMin_amount_retreat(float min_amount_retreat) {
        this.min_amount_retreat = min_amount_retreat;
    }
    public void setAmount_min_penalty(float amount_min_penalty) {
        this.amount_min_penalty = amount_min_penalty;
    }
    public void setPercentage_gain(float percentage_gain) {
        this.percentage_gain = percentage_gain;
    }

    //Print
    @Override
    public String toString() {
        return "Settings: {" +
                "min_amount_bet=" + min_amount_bet +
                ", retreat_date='" + retreat_date + '\'' +
                ", min_amount_retreat=" + min_amount_retreat +
                ", amount_min_penalty=" + amount_min_penalty +
                ", percentage_gain=" + percentage_gain +
                '}';
    }
}
