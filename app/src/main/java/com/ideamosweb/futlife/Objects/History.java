package com.ideamosweb.futlife.Objects;

/**
 * Creado por Deimer Villa on 26/05/17.
 * Función:
 */
public class History {

    private int balance_id;
    private String transaction_type;
    private String transaction_id;
    private String project_id;
    private String authorization;
    private String transaction_date;
    private String ref_payco;
    private String invoice;
    private String bank;
    private float value;
    private String state;
    private String operation_type;

    public History() {}

//region Getters del modelo
    public int getBalance_id() {
        return balance_id;
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
    public String getBank() {
        return bank;
    }
    public float getValue() {
        return value;
    }
    public String getState() {
        return state;
    }
    public String getOperation_type() {
        return operation_type;
    }
//endregion

//region Setters del modelo
    public void setBalance_id(int balance_id) {
        this.balance_id = balance_id;
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
    public void setBank(String bank) {
        this.bank = bank;
    }
    public void setValue(float value) {
        this.value = value;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setOperation_type(String operation_type) {
        this.operation_type = operation_type;
    }
//endregion

//region Funcion para imprimir el objeto
    @Override
    public String toString() {
        return "History{" +
                "balance_id=" + balance_id +
                ", transaction_type='" + transaction_type + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", project_id='" + project_id + '\'' +
                ", authorization='" + authorization + '\'' +
                ", transaction_date='" + transaction_date + '\'' +
                ", ref_payco='" + ref_payco + '\'' +
                ", invoice='" + invoice + '\'' +
                ", bank='" + bank + '\'' +
                ", value=" + value +
                ", state='" + state + '\'' +
                ", operation_type='" + operation_type + '\'' +
                '}';
    }
//endregion

}
