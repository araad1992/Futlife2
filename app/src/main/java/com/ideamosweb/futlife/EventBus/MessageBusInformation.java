package com.ideamosweb.futlife.EventBus;

/**
 * Creado por Deimer Villa on 8/08/17.
 * Función:
 */
public class MessageBusInformation {

    private boolean active;

    public MessageBusInformation(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

}
