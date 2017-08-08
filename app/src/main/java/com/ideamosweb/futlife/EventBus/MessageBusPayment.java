package com.ideamosweb.futlife.EventBus;

/**
 * Creado por Deimer Villa on 8/05/17.
 * Función:
 */
public class MessageBusPayment {

    private boolean active;
    public MessageBusPayment(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }

}
