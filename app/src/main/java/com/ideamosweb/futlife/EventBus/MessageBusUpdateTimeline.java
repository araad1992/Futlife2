package com.ideamosweb.futlife.EventBus;

/**
 * Creado por Deimer Villa on 16/05/17.
 * Función:
 */
public class MessageBusUpdateTimeline {

    private boolean active;
    private String option;

    public MessageBusUpdateTimeline(boolean active, String option) {
        this.active = active;
        this.option = option;
    }

    public boolean isActive() {
        return active;
    }
    public String getOption() {
        return option;
    }

}
