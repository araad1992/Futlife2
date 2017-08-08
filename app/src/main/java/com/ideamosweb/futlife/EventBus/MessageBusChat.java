package com.ideamosweb.futlife.EventBus;

/**
 * Creado por Deimer Villa on 21/04/17.
 * Función:
 */
public class MessageBusChat {

    private boolean refresh;
    private int type;
    private int notification_id;

    public MessageBusChat(boolean refresh, int type, int notification_id) {
        this.refresh = refresh;
        this.type = type;
        this.notification_id = notification_id;
    }

    public boolean isRefresh() {
        return refresh;
    }
    public int getType() {
        return type;
    }
    public int getNotification_id() {
        return notification_id;
    }

}
