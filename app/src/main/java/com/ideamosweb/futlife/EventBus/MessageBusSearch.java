package com.ideamosweb.futlife.EventBus;

/**
 * Creado por Deimer Villa on 11/04/17.
 * Función:
 */
public class MessageBusSearch {

    private boolean active;
    private String keyword;

    public MessageBusSearch(String keyword, boolean active) {
        this.keyword = keyword;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
    public String search() {
        return keyword;
    }

}
