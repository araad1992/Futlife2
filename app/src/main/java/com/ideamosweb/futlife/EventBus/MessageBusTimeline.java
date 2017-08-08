package com.ideamosweb.futlife.EventBus;

import java.util.List;

/**
 * Creado por Deimer Villa on 22/06/17.
 * Función:
 */
public class MessageBusTimeline {

    private boolean active;
    private int option;
    private List<String> list;

    public MessageBusTimeline(boolean active, int option, List<String> list) {
        this.active = active;
        this.option = option;
        this.list = list;
    }

    public boolean isActive() {
        return active;
    }
    public int getOption() {
        return option;
    }
    public List<String> getList() {
        return list;
    }
}
