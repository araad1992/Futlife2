package com.ideamosweb.futlife.EventBus;

/**
 * Creado por Deimer Villa on 9/06/17.
 * Función:
 */
public class MessageBusReport {

    private boolean active;
    private int report_id;

    public MessageBusReport(boolean active, int report_id) {
        this.active = active;
        this.report_id = report_id;
    }

    public boolean isActive() {
        return active;
    }
    public int getReport_id() {
        return report_id;
    }

}
